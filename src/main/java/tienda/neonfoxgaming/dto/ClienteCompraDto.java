package tienda.neonfoxgaming.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteCompraDto(
        @NotBlank String nombre,
        @NotBlank String apellidos,
        @NotBlank @Email String correo,
        @NotBlank String calle,
        @Size(max = 20) String departamento,
        @NotBlank String region,
        @NotBlank String comuna,
        @Size(max = 300) String indicaciones) { }
