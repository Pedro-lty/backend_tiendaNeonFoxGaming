package tienda.neonfoxgaming.controller.v1;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tienda.neonfoxgaming.dto.LoginRequest;
import tienda.neonfoxgaming.dto.RegistroRequest;
import tienda.neonfoxgaming.dto.UsuarioDto;
import tienda.neonfoxgaming.service.UsuarioService;

@RestController("authControllerV1")
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UsuarioService service;
    public AuthController(UsuarioService service) { this.service = service; }

    @PostMapping("/login") public UsuarioDto login(@Valid @RequestBody LoginRequest request) {
        return service.autenticar(request);
    }
    @PostMapping("/registro") @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDto registro(@Valid @RequestBody RegistroRequest request) { return service.registrar(request); }
}
