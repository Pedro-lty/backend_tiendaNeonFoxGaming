package tienda.neonfoxgaming.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotBlank String nombre,
        String apellido,
        String rut,
        @NotBlank @Email String email,
        @Size(min = 8) String password,
        String telefono,
        String region,
        String comuna,
        String role,
        String estado) { }
