package tienda.neonfoxgaming.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PedidoItemRequest(@NotNull Long id, @NotNull @Min(1) Integer quantity) { }
