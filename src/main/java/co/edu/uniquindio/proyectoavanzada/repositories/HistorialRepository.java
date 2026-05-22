package co.edu.uniquindio.proyectoavanzada.repositories;

import co.edu.uniquindio.proyectoavanzada.entities.Historial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface    HistorialRepository extends JpaRepository<Historial, Long> {
    // Busca todo el historial de una solicitud específica
    List<Historial> findBySolicitudIdOrderByFechaHoraDesc(Long solicitudId);

    @Query("""
        SELECT h
        FROM Historial h
        WHERE h.solicitud.id = :solicitudId
        AND h.solicitud.estudiante.id = :estudianteId
        ORDER BY h.fechaHora ASC
    """)
    List<Historial> obtenerHistorialEstudiante(
            Integer estudianteId,
            Integer solicitudId
    );
}