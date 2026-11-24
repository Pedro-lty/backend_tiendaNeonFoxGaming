package tienda.neonfoxgaming.service;

import java.time.format.DateTimeFormatter;
import java.util.List;

import tienda.neonfoxgaming.dto.CategoriaDto;
import tienda.neonfoxgaming.dto.ClienteCompraDto;
import tienda.neonfoxgaming.dto.PedidoDto;
import tienda.neonfoxgaming.dto.PedidoItemDto;
import tienda.neonfoxgaming.dto.ProductoDto;
import tienda.neonfoxgaming.dto.UsuarioDto;
import tienda.neonfoxgaming.model.Categoria;
import tienda.neonfoxgaming.model.ClienteCompra;
import tienda.neonfoxgaming.model.Pedido;
import tienda.neonfoxgaming.model.PedidoItem;
import tienda.neonfoxgaming.model.Producto;
import tienda.neonfoxgaming.model.Usuario;

final class DtoMapper {
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private DtoMapper() { }

    static ProductoDto producto(Producto p) {
        return new ProductoDto(p.getId(), p.getName(), p.getBrand(), p.getDescription(), p.getPrice(),
                p.getImage(), List.copyOf(p.getImages()), p.getStock(), p.getCategoria().getNombre());
    }

    static CategoriaDto categoria(Categoria c) {
        return new CategoriaDto(c.getId(), c.getNombre(), c.getDescripcion());
    }

    static UsuarioDto usuario(Usuario u) {
        return new UsuarioDto(u.getId(), u.getNombre(), u.getApellido(), u.getRut(), u.getEmail(),
                u.getTelefono(), u.getRegion(), u.getComuna(), u.getRole(), u.getEstado(), u.getFechaCreacion());
    }

    static PedidoDto pedido(Pedido p) {
        ClienteCompra c = p.getCliente();
        ClienteCompraDto cliente = new ClienteCompraDto(c.getNombre(), c.getApellidos(), c.getCorreo(),
                c.getCalle(), c.getDepartamento(), c.getRegion(), c.getComuna(), c.getIndicaciones());
        List<PedidoItemDto> items = p.getProductos().stream()
                .map(i -> new PedidoItemDto(i.getProductoId(), i.getName(), i.getBrand(), i.getImage(), i.getPrice(), i.getQuantity()))
                .toList();
        return new PedidoDto(p.getCodigo(), cliente, items, p.getTotal(), p.getFecha().format(FECHA), p.getEstado());
    }
}
