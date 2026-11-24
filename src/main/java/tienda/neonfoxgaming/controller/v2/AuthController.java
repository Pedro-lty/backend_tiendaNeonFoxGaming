package tienda.neonfoxgaming.controller.v2;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tienda.neonfoxgaming.assembler.UsuarioModelAssembler;
import tienda.neonfoxgaming.dto.LoginRequest;
import tienda.neonfoxgaming.dto.RegistroRequest;
import tienda.neonfoxgaming.dto.UsuarioDto;
import tienda.neonfoxgaming.service.UsuarioService;

@RestController("authControllerV2")
@RequestMapping("/api/v2/auth")
public class AuthController {
    private final UsuarioService service;
    private final UsuarioModelAssembler assembler;
    public AuthController(UsuarioService service, UsuarioModelAssembler assembler) {
        this.service = service; this.assembler = assembler;
    }
    @PostMapping(value = "/login", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<UsuarioDto> login(@Valid @RequestBody LoginRequest request) {
        return assembler.toModel(service.autenticar(request));
    }
    @PostMapping(value = "/registro", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<UsuarioDto>> registro(@Valid @RequestBody RegistroRequest request) {
        UsuarioDto creado = service.registrar(request);
        return ResponseEntity.created(linkTo(methodOn(UsuarioController.class).porId(creado.id())).toUri())
                .body(assembler.toModel(creado));
    }
}
