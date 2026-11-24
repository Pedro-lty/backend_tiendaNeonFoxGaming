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
import tienda.neonfoxgaming.assembler.UsuarioModelAssembler;
import tienda.neonfoxgaming.dto.UsuarioDto;
import tienda.neonfoxgaming.dto.UsuarioRequest;
import tienda.neonfoxgaming.service.UsuarioService;

@RestController("usuarioControllerV2")
@RequestMapping("/api/v2/usuarios")
public class UsuarioController {
    private final UsuarioService service;
    private final UsuarioModelAssembler assembler;
    public UsuarioController(UsuarioService service, UsuarioModelAssembler assembler) {
        this.service = service; this.assembler = assembler;
    }
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<UsuarioDto>> todos() {
        List<EntityModel<UsuarioDto>> items = service.todos().stream().map(assembler::toModel).toList();
        return CollectionModel.of(items, linkTo(methodOn(UsuarioController.class).todos()).withSelfRel());
    }
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<UsuarioDto> porId(@PathVariable Long id) { return assembler.toModel(service.porId(id)); }
    @GetMapping(value = "/email/{email}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<UsuarioDto> porEmail(@PathVariable String email) { return assembler.toModel(service.porEmail(email)); }
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<UsuarioDto>> crear(@Valid @RequestBody UsuarioRequest request) {
        UsuarioDto creado = service.crear(request);
        return ResponseEntity.created(linkTo(methodOn(UsuarioController.class).porId(creado.id())).toUri())
                .body(assembler.toModel(creado));
    }
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<UsuarioDto> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest request) {
        return assembler.toModel(service.actualizar(id, request));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id); return ResponseEntity.noContent().build();
    }
}
