package tienda.neonfoxgaming.controller.v1;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tienda.neonfoxgaming.dto.EstadoPedidoRequest;
import tienda.neonfoxgaming.dto.PedidoDto;
import tienda.neonfoxgaming.dto.PedidoRequest;
import tienda.neonfoxgaming.service.PedidoService;

@RestController("pedidoControllerV1")
@RequestMapping("/api/v1/pedidos")
public class PedidoController {
    private final PedidoService service;
    public PedidoController(PedidoService service) { this.service = service; }

    @GetMapping public List<PedidoDto> todos(@RequestParam(required = false) String correo) {
        return correo == null || correo.isBlank() ? service.todos() : service.porCorreo(correo);
    }
    @GetMapping("/{codigo}") public PedidoDto porCodigo(@PathVariable String codigo) { return service.porCodigo(codigo); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public PedidoDto crear(@Valid @RequestBody PedidoRequest request) { return service.crear(request); }
    @PatchMapping("/{codigo}/estado") public PedidoDto cambiarEstado(@PathVariable String codigo,
            @Valid @RequestBody EstadoPedidoRequest request) { return service.cambiarEstado(codigo, request.estado()); }
}
