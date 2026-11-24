package tienda.neonfoxgaming.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import tienda.neonfoxgaming.dto.CategoriaDto;
import tienda.neonfoxgaming.dto.CategoriaRequest;
import tienda.neonfoxgaming.model.Categoria;
import tienda.neonfoxgaming.repository.CategoriaRepository;
import tienda.neonfoxgaming.repository.ProductoRepository;

@Service
@Transactional(readOnly = true)
public class CategoriaService {
    private final CategoriaRepository categorias;
    private final ProductoRepository productos;

    public CategoriaService(CategoriaRepository categorias, ProductoRepository productos) {
        this.categorias = categorias;
        this.productos = productos;
    }

    public List<CategoriaDto> todas() {
        return categorias.findAll().stream().map(DtoMapper::categoria).toList();
    }

    public CategoriaDto porId(Long id) {
        return DtoMapper.categoria(categorias.findById(id).orElseThrow(() -> noExiste(id)));
    }

    @Transactional
    public CategoriaDto crear(CategoriaRequest request) {
        if (categorias.existsByNombreIgnoreCase(request.nombre().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una categoría con ese nombre.");
        }
        return DtoMapper.categoria(categorias.save(new Categoria(request.nombre().trim(), request.descripcion())));
    }

    @Transactional
    public CategoriaDto actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = categorias.findById(id).orElseThrow(() -> noExiste(id));
        if (categorias.existsByNombreIgnoreCaseAndIdNot(request.nombre().trim(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una categoría con ese nombre.");
        }
        categoria.setNombre(request.nombre().trim());
        categoria.setDescripcion(request.descripcion() == null ? "" : request.descripcion().trim());
        return DtoMapper.categoria(categorias.save(categoria));
    }

    @Transactional
    public void eliminar(Long id) {
        Categoria categoria = categorias.findById(id).orElseThrow(() -> noExiste(id));
        if (productos.existsByCategoria_Id(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar: hay productos que usan esta categoría.");
        }
        categorias.delete(categoria);
    }

    private ResponseStatusException noExiste(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la categoría " + id + ".");
    }
}
