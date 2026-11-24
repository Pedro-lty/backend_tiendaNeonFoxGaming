package tienda.neonfoxgaming.controller.v2;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tienda.neonfoxgaming.assembler.PedidoModelAssembler;
import tienda.neonfoxgaming.dto.EstadoPedidoRequest;
import tienda.neonfoxgaming.dto.PedidoDto;
import tienda.neonfoxgaming.dto.PedidoRequest;
import tienda.neonfoxgaming.service.PedidoService;

@RestController("pedidoControllerV2")
@RequestMapping("/api/v2/pedidos")
public class PedidoController {
    private final PedidoService service;
    private final PedidoModelAssembler assembler;
    public PedidoController(PedidoService service, PedidoModelAssembler assembler) {
        this.service = service; this.assembler = assembler;
    }
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<PedidoDto>> todos(@RequestParam(required = false) String correo) {
        List<PedidoDto> pedidos = correo == null || correo.isBlank() ? service.todos() : service.porCorreo(correo);
        return CollectionModel.of(pedidos.stream().map(assembler::toModel).toList(),
                linkTo(methodOn(PedidoController.class).todos(correo)).withSelfRel());
    }
    @GetMapping(value = "/{codigo}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<PedidoDto> porCodigo(@PathVariable String codigo) { return assembler.toModel(service.porCodigo(codigo)); }
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<PedidoDto>> crear(@Valid @RequestBody PedidoRequest request) {
        PedidoDto creado = service.crear(request);
        return ResponseEntity.created(linkTo(methodOn(PedidoController.class).porCodigo(creado.codigo())).toUri())
                .body(assembler.toModel(creado));
    }
    @PatchMapping(value = "/{codigo}/estado", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<PedidoDto> cambiarEstado(@PathVariable String codigo,
            @Valid @RequestBody EstadoPedidoRequest request) {
        return assembler.toModel(service.cambiarEstado(codigo, request.estado()));
    }
}
