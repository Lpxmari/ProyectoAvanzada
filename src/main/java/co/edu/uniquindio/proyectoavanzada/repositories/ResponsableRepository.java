package co.edu.uniquindio.proyectoavanzada.repositories;

import co.edu.uniquindio.proyectoavanzada.entities.Responsable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResponsableRepository extends JpaRepository<Responsable, Long> {
    // Para filtrar solo los que están trabajando actualmente
    List<Responsable> findByActivoTrue();
}