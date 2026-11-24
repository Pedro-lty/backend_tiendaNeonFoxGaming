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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tienda.neonfoxgaming.dto.UsuarioDto;
import tienda.neonfoxgaming.dto.UsuarioRequest;
import tienda.neonfoxgaming.service.UsuarioService;

@RestController("usuarioControllerV1")
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {
    private final UsuarioService service;
    public UsuarioController(UsuarioService service) { this.service = service; }

    @GetMapping public List<UsuarioDto> todos() { return service.todos(); }
    @GetMapping("/{id}") public UsuarioDto porId(@PathVariable Long id) { return service.porId(id); }
    @GetMapping("/email/{email}") public UsuarioDto porEmail(@PathVariable String email) { return service.porEmail(email); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDto crear(@Valid @RequestBody UsuarioRequest request) { return service.crear(request); }
    @PutMapping("/{id}") public UsuarioDto actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest request) {
        return service.actualizar(id, request);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
