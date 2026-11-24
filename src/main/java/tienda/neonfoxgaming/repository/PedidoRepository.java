package tienda.neonfoxgaming.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tienda.neonfoxgaming.model.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Optional<Pedido> findByCodigo(String codigo);
    List<Pedido> findAllByOrderByFechaDesc();
    List<Pedido> findByCliente_CorreoIgnoreCaseOrderByFechaDesc(String correo);
}
