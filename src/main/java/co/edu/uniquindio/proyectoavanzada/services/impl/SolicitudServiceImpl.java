package co.edu.uniquindio.proyectoavanzada.services.impl;

import co.edu.uniquindio.proyectoavanzada.dto.CrearSolicitudDTO;
import co.edu.uniquindio.proyectoavanzada.entities.*;
import co.edu.uniquindio.proyectoavanzada.entities.enums.EstadoSolicitud;
import co.edu.uniquindio.proyectoavanzada.excepciones.RecursoNoEncontradoException;
import co.edu.uniquindio.proyectoavanzada.repositories.EstudianteRepository;
import co.edu.uniquindio.proyectoavanzada.repositories.HistorialRepository;
import co.edu.uniquindio.proyectoavanzada.repositories.ResponsableRepository;
import co.edu.uniquindio.proyectoavanzada.repositories.SolicitudRepository;
import co.edu.uniquindio.proyectoavanzada.services.SolicitudService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor // Lombok crea el constructor para inyectar los repositorios

public class SolicitudServiceImpl implements SolicitudService {


    private final SolicitudRepository solicitudRepository;
    private final ResponsableRepository responsableRepository;
    private final HistorialRepository historialRepository;
    private final EstudianteRepository estudianteRepository;

    // 1. Crear nueva solicitud (Estudiante)
    @Override
    public Solicitud registrarSolicitud(CrearSolicitudDTO solicitud) {

        Estudiante estudiante = estudianteRepository.findById(solicitud.getEstudianteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado"));

        Solicitud nueva = Solicitud.builder()
                .descripcion(solicitud.getDescripcion())
                .tipo(solicitud.getTipoSolicitud())
                .canalOrigen(solicitud.getCanalOrigen())
                .estudiante(estudiante)
                .fechaHoraRegistro(LocalDateTime.now())
                .estado(EstadoSolicitud.REGISTRADA)
                .build();

        return solicitudRepository.save(nueva);
    }

    // 2. Listar solicitudes (Responsable)
    @Override
    public List<Solicitud> listarTodas() {
        return solicitudRepository.findAll();
    }

    // 3. Proceso de Triage (Asignar Prioridad)
    @Override
    public Solicitud realizarTriage(Long id, Prioridad prioridad) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        // Guardamos el historial del cambio
        Historial h = Historial.builder()
                .fechaHora(LocalDateTime.now())
                .estadoAnterior(solicitud.getEstado())
                .estadoNuevo(EstadoSolicitud.CLASIFICADA)
                .observaciones("Se realizó el triage y se asignó prioridad " + prioridad.getNivel())
                .solicitud(solicitud)
                .build();

        historialRepository.save(h);

        // Actualizamos la solicitud
        solicitud.setPrioridad(prioridad);
        solicitud.setEstado(EstadoSolicitud.CLASIFICADA);

        return solicitudRepository.save(solicitud);
    }

    // 4. Asignar Responsable
    @Override
    public Solicitud asignarResponsable(Long idSolicitud, Long idResponsable) {
        Solicitud solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada"));

        Responsable responsable = responsableRepository.findById(idResponsable)
                .orElseThrow(() -> new RecursoNoEncontradoException("Responsable no encontrado"));

        // Guardar quién hizo el cambio en el historial
        Historial h = Historial.builder()
                .fechaHora(LocalDateTime.now())
                .estadoAnterior(solicitud.getEstado())
                .estadoNuevo(EstadoSolicitud.EN_ATENCION)
                .observaciones("Responsable asignado: " + responsable.getNombreCompleto())
                .solicitud(solicitud)
                .responsableAccion(responsable) // Quién toma el caso
                .build();

        historialRepository.save(h);

        solicitud.setResponsableAsignado(responsable);
        solicitud.setEstado(EstadoSolicitud.EN_ATENCION);

        return solicitudRepository.save(solicitud);
    }

    @Override
    @Transactional
    public void cerrarSolicitud(Long id) {
        Solicitud solicitud = obtenerPorId(id);
        solicitud.setEstado(EstadoSolicitud.CERRADA);
        solicitud.setFechaCierre(LocalDateTime.now());
        solicitudRepository.save(solicitud);
    }

    @Override
    public Solicitud obtenerPorId(Long id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada con ID: " + id));
    }

    @Override
    public List<Solicitud> listarPorEstado(EstadoSolicitud estado) {
        return solicitudRepository.findByEstado(estado);
    }

    @Override
    public List<Historial> obtenerHistorial(Long idSolicitud) {
        return historialRepository.findBySolicitudIdOrderByFechaHoraDesc(idSolicitud);
    }

    @Override
    public List<Solicitud> listarPorEstudiante(Long estudianteId) {
        return solicitudRepository.findByEstudianteId(estudianteId);
    }
}
