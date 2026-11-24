package tienda.neonfoxgaming.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import tienda.neonfoxgaming.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    boolean existsByCategoria_Id(Long categoriaId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Producto p where p.id = :id")
    Optional<Producto> findForUpdate(@Param("id") Long id);
}
