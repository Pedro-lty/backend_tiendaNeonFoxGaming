package tienda.neonfoxgaming.dto;

import java.time.LocalDate;

public record UsuarioDto(Long id, String nombre, String apellido, String rut, String email,
                         String telefono, String region, String comuna, String role,
                         String estado, LocalDate fechaCreacion) { }
