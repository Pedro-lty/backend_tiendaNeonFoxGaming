package tienda.neonfoxgaming.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import tienda.neonfoxgaming.dto.ClienteCompraDto;
import tienda.neonfoxgaming.dto.PedidoDto;
import tienda.neonfoxgaming.dto.PedidoItemRequest;
import tienda.neonfoxgaming.dto.PedidoRequest;
import tienda.neonfoxgaming.model.ClienteCompra;
import tienda.neonfoxgaming.model.Pedido;
import tienda.neonfoxgaming.model.PedidoItem;
import tienda.neonfoxgaming.model.Producto;
import tienda.neonfoxgaming.repository.PedidoRepository;
import tienda.neonfoxgaming.repository.ProductoRepository;

@Service
@Transactional(readOnly = true)
public class PedidoService {
    private static final List<String> ESTADOS = List.of("Pagada", "Enviada", "Entregada", "Cancelada");
    private final PedidoRepository pedidos;
    private final ProductoRepository productos;

    public PedidoService(PedidoRepository pedidos, ProductoRepository productos) {
        this.pedidos = pedidos;
        this.productos = productos;
    }

    public List<PedidoDto> todos() {
        return pedidos.findAllByOrderByFechaDesc().stream().map(DtoMapper::pedido).toList();
    }

    public PedidoDto porCodigo(String codigo) {
        return DtoMapper.pedido(pedidos.findByCodigo(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el pedido.")));
    }

    public List<PedidoDto> porCorreo(String correo) {
        return pedidos.findByCliente_CorreoIgnoreCaseOrderByFechaDesc(correo.trim()).stream().map(DtoMapper::pedido).toList();
    }

    @Transactional
    public PedidoDto crear(PedidoRequest request) {
        if (request.codigo() != null && !request.codigo().isBlank()) {
            var previo = pedidos.findByCodigo(request.codigo().trim());
            if (previo.isPresent()) return DtoMapper.pedido(previo.get());
        }

        Pedido pedido = new Pedido();
        pedido.setCodigo(request.codigo() == null || request.codigo().isBlank()
                ? "NFG-" + Instant.now().toEpochMilli() + "-" + UUID.randomUUID().toString().substring(0, 6)
                : request.codigo().trim());
        pedido.setCliente(mapCliente(request.cliente()));
        pedido.setEstado("Pagada");

        BigDecimal total = BigDecimal.ZERO;
        for (PedidoItemRequest requestedItem : request.productos()) {
            Producto producto = productos.findForUpdate(requestedItem.id()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el producto " + requestedItem.id() + "."));
            int cantidad = requestedItem.quantity();
            if (producto.getStock() < cantidad) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Stock insuficiente para el producto " + producto.getName() + ".");
            }
            producto.setStock(producto.getStock() - cantidad);
            productos.save(producto);

            PedidoItem item = new PedidoItem();
            item.setPedido(pedido);
            item.setProductoId(producto.getId());
            item.setName(producto.getName());
            item.setBrand(producto.getBrand());
            item.setImage(producto.getImage());
            item.setPrice(producto.getPrice());
            item.setQuantity(cantidad);
            pedido.getProductos().add(item);
            total = total.add(producto.getPrice().multiply(BigDecimal.valueOf(cantidad)));
        }

        pedido.setTotal(total);
        return DtoMapper.pedido(pedidos.save(pedido));
    }

    @Transactional
    public PedidoDto cambiarEstado(String codigo, String estado) {
        if (estado == null || !ESTADOS.contains(estado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado inválido. Valores permitidos: " + ESTADOS);
        }
        Pedido pedido = pedidos.findByCodigo(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el pedido."));
        pedido.setEstado(estado);
        return DtoMapper.pedido(pedidos.save(pedido));
    }

    private ClienteCompra mapCliente(ClienteCompraDto dto) {
        ClienteCompra cliente = new ClienteCompra();
        cliente.setNombre(dto.nombre().trim());
        cliente.setApellidos(dto.apellidos().trim());
        cliente.setCorreo(dto.correo().trim().toLowerCase());
        cliente.setCalle(dto.calle().trim());
        cliente.setDepartamento(dto.departamento() == null ? "" : dto.departamento().trim());
        cliente.setRegion(dto.region().trim());
        cliente.setComuna(dto.comuna().trim());
        cliente.setIndicaciones(dto.indicaciones() == null ? "" : dto.indicaciones().trim());
        return cliente;
    }
}
