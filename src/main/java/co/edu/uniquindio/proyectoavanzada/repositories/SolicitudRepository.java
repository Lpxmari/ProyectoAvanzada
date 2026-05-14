package co.edu.uniquindio.proyectoavanzada.repositories;

import co.edu.uniquindio.proyectoavanzada.entities.Solicitud;
import co.edu.uniquindio.proyectoavanzada.entities.enums.EstadoSolicitud;
import co.edu.uniquindio.proyectoavanzada.entities.enums.NivelSolicitud;
import co.edu.uniquindio.proyectoavanzada.entities.enums.TipoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    List<Solicitud> findByEstudianteId(Long estudianteId);

    List<Solicitud> findByEstado(EstadoSolicitud estado);

    List<Solicitud> findByResponsableAsignadoId(Long responsableId);

    @Query("""
    SELECT s
    FROM Solicitud s
    LEFT JOIN s.responsableAsignado r
    LEFT JOIN s.prioridad p
    WHERE
        (:responsable IS NULL OR :responsable = ''
            OR LOWER(r.nombreCompleto) LIKE LOWER(CONCAT('%', :responsable, '%')))
    AND (:nivel IS NULL OR p.nivel = :nivel)
    AND (:estado IS NULL OR s.estado = :estado)
    AND (:tipoSolicitud IS NULL OR s.tipo = :tipoSolicitud)
""")
    List<Solicitud> filtrar(
            @Param("responsable") String responsable,
            @Param("nivel") NivelSolicitud nivel,
            @Param("estado") EstadoSolicitud estado,
            @Param("tipoSolicitud") TipoSolicitud tipoSolicitud
    );
}