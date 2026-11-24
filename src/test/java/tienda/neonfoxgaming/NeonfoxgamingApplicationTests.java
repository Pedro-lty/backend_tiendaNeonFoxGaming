package tienda.neonfoxgaming;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import tienda.neonfoxgaming.dto.CategoriaDto;
import tienda.neonfoxgaming.dto.CategoriaRequest;
import tienda.neonfoxgaming.dto.ClienteCompraDto;
import tienda.neonfoxgaming.dto.LoginRequest;
import tienda.neonfoxgaming.dto.PedidoDto;
import tienda.neonfoxgaming.dto.PedidoItemRequest;
import tienda.neonfoxgaming.dto.PedidoRequest;
import tienda.neonfoxgaming.dto.ProductoDto;
import tienda.neonfoxgaming.dto.UsuarioDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestRestTemplate
class NeonfoxgamingApplicationTests {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    private String url(String path) { return "http://localhost:" + port + path; }

    @Test
    void catalogIsSeededAndBothVersionsAndSwaggerRespond() {
        ResponseEntity<ProductoDto[]> catalogo = rest.getForEntity(url("/api/v1/productos"), ProductoDto[].class);
        assertEquals(200, catalogo.getStatusCode().value());
        assertEquals(12, catalogo.getBody().length);
        assertNotNull(catalogo.getBody()[0].categoria());

        HttpHeaders halHeaders = new HttpHeaders();
        halHeaders.setAccept(List.of(MediaType.parseMediaType("application/hal+json")));
        ResponseEntity<String> hal = rest.exchange(url("/api/v2/productos"), HttpMethod.GET,
                new HttpEntity<>(halHeaders), String.class);
        assertEquals(200, hal.getStatusCode().value());
        assertTrue(hal.getBody().contains("_links"));

        ResponseEntity<String> docs = rest.getForEntity(url("/v3/api-docs"), String.class);
        assertEquals(200, docs.getStatusCode().value());
        assertTrue(docs.getBody().contains("Neon Fox Gaming API"));
    }

    @Test
    void adminRoutesRequireCredentialsAndAllowAdminWrites() {
        CategoriaRequest request = new CategoriaRequest("Accesorios QA", "Categoría de prueba");
        ResponseEntity<String> denied = rest.postForEntity(url("/api/v1/categorias"), request, String.class);
        assertEquals(401, denied.getStatusCode().value());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("admin@neonfox.com", "admin123");
        ResponseEntity<CategoriaDto> created = rest.postForEntity(url("/api/v1/categorias"),
                new HttpEntity<>(request, headers), CategoriaDto.class);
        assertEquals(201, created.getStatusCode().value());
        assertNotNull(created.getBody().id());
    }

    @Test
    void registrationLoginAndOrderUseStoredCredentialsAndDatabasePrices() {
        String email = "prueba-" + UUID.randomUUID() + "@example.cl";
        var registerBody = new tienda.neonfoxgaming.dto.RegistroRequest("Prueba", "Cliente", "12.345.678-5",
                email, "clave-segura-123", "+56912345678", "Región Metropolitana", "Maipú");
        ResponseEntity<UsuarioDto> registered = rest.postForEntity(url("/api/v1/auth/registro"), registerBody, UsuarioDto.class);
        assertEquals(201, registered.getStatusCode().value());
        assertEquals("Cliente", registered.getBody().role());

        ResponseEntity<UsuarioDto> login = rest.postForEntity(url("/api/v1/auth/login"),
                new LoginRequest(email, "clave-segura-123"), UsuarioDto.class);
        assertEquals(200, login.getStatusCode().value());
        assertEquals(email, login.getBody().email());

        ProductoDto producto = rest.getForObject(url("/api/v1/productos/4"), ProductoDto.class);
        String codigo = "NFG-QA-" + UUID.randomUUID();
        ClienteCompraDto cliente = new ClienteCompraDto("Prueba", "Cliente", email, "Avenida 123", "",
                "Región Metropolitana de Santiago", "Maipú", "");
        PedidoRequest pedidoRequest = new PedidoRequest(codigo, cliente, List.of(new PedidoItemRequest(4L, 1)));
        ResponseEntity<PedidoDto> pedido = rest.postForEntity(url("/api/v1/pedidos"), pedidoRequest, PedidoDto.class);
        assertEquals(201, pedido.getStatusCode().value());
        assertEquals(producto.price(), pedido.getBody().total());
        assertEquals(producto.stock() - 1, rest.getForObject(url("/api/v1/productos/4"), ProductoDto.class).stock());
    }
}
