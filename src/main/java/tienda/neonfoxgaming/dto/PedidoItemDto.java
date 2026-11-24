package tienda.neonfoxgaming.dto;

import java.math.BigDecimal;

public record PedidoItemDto(Long id, String name, String brand, String image,
                            BigDecimal price, Integer quantity) { }
