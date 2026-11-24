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
import tienda.neonfoxgaming.dto.CategoriaDto;
import tienda.neonfoxgaming.dto.CategoriaRequest;
import tienda.neonfoxgaming.service.CategoriaService;

@RestController("categoriaControllerV1")
@RequestMapping("/api/v1/categorias")
public class CategoriaController {
    private final CategoriaService service;
    public CategoriaController(CategoriaService service) { this.service = service; }

    @GetMapping public List<CategoriaDto> todas() { return service.todas(); }
    @GetMapping("/{id}") public CategoriaDto porId(@PathVariable Long id) { return service.porId(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public CategoriaDto crear(@Valid @RequestBody CategoriaRequest request) { return service.crear(request); }
    @PutMapping("/{id}") public CategoriaDto actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        return service.actualizar(id, request);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
