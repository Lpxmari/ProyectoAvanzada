package co.edu.uniquindio.proyectoavanzada.repositories;

import co.edu.uniquindio.proyectoavanzada.entities.Historial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialRepository extends JpaRepository<Historial, Long> {
    // Busca todo el historial de una solicitud específica
    List<Historial> findBySolicitudIdOrderByFechaHoraDesc(Long solicitudId);
}