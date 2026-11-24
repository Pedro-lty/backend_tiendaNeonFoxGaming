package tienda.neonfoxgaming.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PedidoRequest(String codigo, @NotNull @Valid ClienteCompraDto cliente,
                            @NotEmpty List<@Valid PedidoItemRequest> productos) { }
