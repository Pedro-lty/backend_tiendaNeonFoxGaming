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
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tienda.neonfoxgaming.assembler.CategoriaModelAssembler;
import tienda.neonfoxgaming.dto.CategoriaDto;
import tienda.neonfoxgaming.dto.CategoriaRequest;
import tienda.neonfoxgaming.service.CategoriaService;

@RestController("categoriaControllerV2")
@RequestMapping("/api/v2/categorias")
public class CategoriaController {
    private final CategoriaService service;
    private final CategoriaModelAssembler assembler;
    public CategoriaController(CategoriaService service, CategoriaModelAssembler assembler) {
        this.service = service; this.assembler = assembler;
    }
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<CategoriaDto>> todas() {
        List<EntityModel<CategoriaDto>> items = service.todas().stream().map(assembler::toModel).toList();
        return CollectionModel.of(items, linkTo(methodOn(CategoriaController.class).todas()).withSelfRel());
    }
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<CategoriaDto> porId(@PathVariable Long id) { return assembler.toModel(service.porId(id)); }
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<CategoriaDto>> crear(@Valid @RequestBody CategoriaRequest request) {
        CategoriaDto creado = service.crear(request);
        return ResponseEntity.created(linkTo(methodOn(CategoriaController.class).porId(creado.id())).toUri())
                .body(assembler.toModel(creado));
    }
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<CategoriaDto> actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        return assembler.toModel(service.actualizar(id, request));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id); return ResponseEntity.noContent().build();
    }
}
