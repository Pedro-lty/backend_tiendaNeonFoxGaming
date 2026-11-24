package tienda.neonfoxgaming.controller.v1;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import tienda.neonfoxgaming.dto.ProductoDto;
import tienda.neonfoxgaming.dto.ProductoRequest;
import tienda.neonfoxgaming.service.ProductoService;
import jakarta.validation.Valid;

@RestController("productoControllerV1")
@RequestMapping("/api/v1/productos")
public class ProductoController {
    private final ProductoService service;
    public ProductoController(ProductoService service) { this.service = service; }

    @GetMapping public List<ProductoDto> todos() { return service.todos(); }
    @GetMapping("/stock-critico") public List<ProductoDto> stockCritico(@RequestParam(defaultValue = "5") int umbral) {
        return service.stockCritico(umbral);
    }
    @GetMapping("/{id}") public ProductoDto porId(@PathVariable Long id) { return service.porId(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ProductoDto crear(@Valid @RequestBody ProductoRequest request) { return service.crear(request); }
    @PutMapping("/{id}") public ProductoDto actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        return service.actualizar(id, request);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
