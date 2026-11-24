package tienda.neonfoxgaming.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class ClienteCompra {
    @Column(name = "cliente_nombre", nullable = false, length = 150)
    private String nombre;
    @Column(name = "cliente_apellidos", nullable = false, length = 150)
    private String apellidos;
    @Column(name = "cliente_correo", nullable = false, length = 180)
    private String correo;
    @Column(name = "cliente_calle", nullable = false, length = 200)
    private String calle;
    @Column(name = "cliente_departamento", length = 40)
    private String departamento;
    @Column(name = "cliente_region", nullable = false, length = 120)
    private String region;
    @Column(name = "cliente_comuna", nullable = false, length = 120)
    private String comuna;
    @Column(name = "cliente_indicaciones", length = 500)
    private String indicaciones;
}
