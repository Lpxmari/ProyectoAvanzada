package co.edu.uniquindio.proyectoavanzada.repositories;

import co.edu.uniquindio.proyectoavanzada.entities.Solicitud;
import co.edu.uniquindio.proyectoavanzada.entities.enums.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    // Spring crea automáticamente la consulta para buscar por estado
    List<Solicitud> findByEstado(EstadoSolicitud estado);
}
