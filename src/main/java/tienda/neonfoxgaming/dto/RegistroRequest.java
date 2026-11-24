package tienda.neonfoxgaming.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @NotBlank String nombre,
        @NotBlank String apellido,
        @NotBlank String rut,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8) String password,
        String telefono,
        String region,
        String comuna) { }
