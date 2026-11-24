package tienda.neonfoxgaming.controller.v2;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tienda.neonfoxgaming.assembler.ProductoModelAssembler;
import tienda.neonfoxgaming.dto.ProductoDto;
import tienda.neonfoxgaming.dto.ProductoRequest;
import tienda.neonfoxgaming.service.ProductoService;

@RestController("productoControllerV2")
@RequestMapping("/api/v2/productos")
public class ProductoController {
    private final ProductoService service;
    private final ProductoModelAssembler assembler;
    public ProductoController(ProductoService service, ProductoModelAssembler assembler) {
        this.service = service; this.assembler = assembler;
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<ProductoDto>> todos() {
        List<EntityModel<ProductoDto>> items = service.todos().stream().map(assembler::toModel).toList();
        return CollectionModel.of(items, linkTo(methodOn(ProductoController.class).todos()).withSelfRel());
    }
    @GetMapping(value = "/stock-critico", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<ProductoDto>> stockCritico(@RequestParam(defaultValue = "5") int umbral) {
        return CollectionModel.of(service.stockCritico(umbral).stream().map(assembler::toModel).toList(),
                linkTo(methodOn(ProductoController.class).stockCritico(umbral)).withSelfRel());
    }
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<ProductoDto> porId(@PathVariable Long id) { return assembler.toModel(service.porId(id)); }
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<ProductoDto>> crear(@Valid @RequestBody ProductoRequest request) {
        ProductoDto creado = service.crear(request);
        return ResponseEntity.created(linkTo(methodOn(ProductoController.class).porId(creado.id())).toUri())
                .body(assembler.toModel(creado));
    }
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<ProductoDto> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        return assembler.toModel(service.actualizar(id, request));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id); return ResponseEntity.noContent().build();
    }
}
