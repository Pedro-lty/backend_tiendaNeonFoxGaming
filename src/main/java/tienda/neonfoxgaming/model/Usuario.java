package tienda.neonfoxgaming.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 100)
    private String apellido = "";

    @Column(unique = true, length = 20)
    private String rut;

    @Column(name = "gmail", nullable = false, unique = true, length = 180)
    private String email;

    @JsonIgnore
    @Column(nullable = false, name = "contrasena", length = 255)
    private String passwordHash;

    @Column(length = 40)
    private String telefono = "";

    @Column(length = 100)
    private String region = "";

    @Column(length = 100)
    private String comuna = "";

    @Column(name = "rol", nullable = false, length = 20)
    private String role = "Cliente";

    @Column(nullable = false, length = 20)
    private String estado = "Activo";

    @Column(name = "fecha_creacion_cuenta", nullable = false)
    private LocalDate fechaCreacion = LocalDate.now();

    @PrePersist
    void alCrear() {
        if (fechaCreacion == null) fechaCreacion = LocalDate.now();
        if (role == null || role.isBlank()) role = "Cliente";
        if (estado == null || estado.isBlank()) estado = "Activo";
    }
}
