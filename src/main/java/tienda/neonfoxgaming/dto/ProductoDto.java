package tienda.neonfoxgaming.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductoDto(Long id, String name, String brand, String description, BigDecimal price,
                          String image, List<String> images, Integer stock, String categoria) { }
