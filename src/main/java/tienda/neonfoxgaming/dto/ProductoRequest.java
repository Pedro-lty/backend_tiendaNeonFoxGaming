package tienda.neonfoxgaming.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductoRequest(
        @NotBlank String name,
        String brand,
        String description,
        @NotNull @Min(0) BigDecimal price,
        String image,
        List<String> images,
        @NotNull @Min(0) Integer stock,
        @NotBlank String categoria) { }
