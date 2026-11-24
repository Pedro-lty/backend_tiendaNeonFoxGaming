package tienda.neonfoxgaming.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import tienda.neonfoxgaming.dto.ProductoDto;
import tienda.neonfoxgaming.dto.ProductoRequest;
import tienda.neonfoxgaming.model.Categoria;
import tienda.neonfoxgaming.model.Producto;
import tienda.neonfoxgaming.repository.CategoriaRepository;
import tienda.neonfoxgaming.repository.ProductoRepository;

@Service
@Transactional(readOnly = true)
public class ProductoService {
    private final ProductoRepository productos;
    private final CategoriaRepository categorias;

    public ProductoService(ProductoRepository productos, CategoriaRepository categorias) {
        this.productos = productos;
        this.categorias = categorias;
    }

    public List<ProductoDto> todos() {
        return productos.findAll().stream().map(DtoMapper::producto).toList();
    }

    public List<ProductoDto> stockCritico(int umbral) {
        return productos.findAll().stream().filter(p -> p.getStock() <= umbral).map(DtoMapper::producto).toList();
    }

    public ProductoDto porId(Long id) {
        return DtoMapper.producto(productos.findById(id).orElseThrow(() -> noExiste(id)));
    }

    @Transactional
    public ProductoDto crear(ProductoRequest request) {
        return guardar(new Producto(), request);
    }

    @Transactional
    public ProductoDto actualizar(Long id, ProductoRequest request) {
        Producto producto = productos.findById(id).orElseThrow(() -> noExiste(id));
        return guardar(producto, request);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!productos.existsById(id)) throw noExiste(id);
        productos.deleteById(id);
    }

    private ProductoDto guardar(Producto p, ProductoRequest r) {
        Categoria categoria = categorias.findByNombreIgnoreCase(r.categoria().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "La categoría indicada no existe."));
        p.setName(r.name().trim());
        p.setBrand(r.brand() == null ? "" : r.brand().trim());
        p.setDescription(r.description() == null ? "" : r.description().trim());
        p.setPrice(r.price());
        p.setStock(r.stock());
        p.setImage(r.image() == null ? "" : r.image().trim());
        p.setCategoria(categoria);

        List<String> imagenes = r.images() == null ? new ArrayList<>(p.getImages()) : new ArrayList<>(r.images());
        imagenes.removeIf(img -> img == null || img.isBlank());
        if (!p.getImage().isBlank()) {
            if (imagenes.isEmpty()) imagenes.add(p.getImage());
            else imagenes.set(0, p.getImage());
        }
        p.setImages(imagenes);
        return DtoMapper.producto(productos.save(p));
    }

    private ResponseStatusException noExiste(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el producto " + id + ".");
    }
}
