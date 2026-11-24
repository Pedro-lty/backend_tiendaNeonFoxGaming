package tienda.neonfoxgaming.dto;

import java.math.BigDecimal;
import java.util.List;

public record PedidoDto(String codigo, ClienteCompraDto cliente, List<PedidoItemDto> productos,
                        BigDecimal total, String fecha, String estado) { }
