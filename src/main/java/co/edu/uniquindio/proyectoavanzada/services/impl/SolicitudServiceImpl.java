package co.edu.uniquindio.proyectoavanzada.services.impl;

import co.edu.uniquindio.proyectoavanzada.dto.*;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor 

public class SolicitudServiceImpl implements SolicitudService {


    private final SolicitudRepository solicitudRepository;
    private final ResponsableRepository responsableRepository;
    private final HistorialRepository historialRepository;
    private final EstudianteRepository estudianteRepository;

    // 1. Crear nueva solicitud (Estudiante)
    @Override
    public Solicitud registrarSolicitud(CrearSolicitudDTO solicitud) {

        Estudiante estudiante = estudianteRepository.findById(solicitud.estudianteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado"));

        Solicitud nueva = Solicitud.builder()
                .descripcion(solicitud.descripcion())
                .tipo(solicitud.tipoSolicitud())
                .canalOrigen(solicitud.canalOrigen())
                .estudiante(estudiante)
                .fechaHoraRegistro(LocalDateTime.now())
                .estado(EstadoSolicitud.REGISTRADA)
                .build();

        return solicitudRepository.save(nueva);
    }

    // 2. Listar solicitudes (Responsable)
    @Override
    public List<SolicitudDTO> listarTodas() {
        return solicitudRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // 3. Proceso de Triage (Asignar Prioridad)
    @Override
    public Solicitud realizarTriage(Long id, PrioridadDTO prioridadDTO) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        // Convertimos el String del DTO al Enum que pide la Entidad
        Prioridad prioridad = Prioridad.builder()
                .nivel(prioridadDTO.nivel())
                .impactoAcademico(prioridadDTO.impactoAcademico())
                .justificacion(prioridadDTO.justificacion())
                .vigencia(prioridadDTO.vigencia())
                .build();

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
        Solicitud solicitud = findById(id);
        solicitud.setEstado(EstadoSolicitud.CERRADA);
        solicitud.setFechaCierre(LocalDateTime.now());
        solicitudRepository.save(solicitud);
    }

    @Override
    public SolicitudDTO obtenerPorId(Long id) {
        Solicitud solicitud =  solicitudRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada con ID: " + id));
        return convertirADTO(solicitud);
    }

    @Override
    public List<SolicitudDTO> listarPorEstado(EstadoSolicitud estado) {
        return solicitudRepository.findByEstado(estado).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<HistorialDTO> obtenerHistorial(Long idSolicitud) {
        return historialRepository.findBySolicitudIdOrderByFechaHoraDesc(idSolicitud).stream()
                .map(h -> new HistorialDTO(
                        h.getId(),
                        h.getFechaHora(),
                        h.getEstadoAnterior(),
                        h.getEstadoNuevo(),
                        h.getObservaciones(),
                        h.getResponsableAccion() != null ? new ResponsableDTO(
                                h.getResponsableAccion().getId(),
                                h.getResponsableAccion().getNombreCompleto(),
                                h.getResponsableAccion().getCargo(),
                                h.getResponsableAccion().isActivo()
                        ) : null
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<SolicitudDTO> listarPorEstudiante(Long estudianteId) {
        return solicitudRepository.findByEstudianteId(estudianteId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private Solicitud findById(Long id) {
        return  solicitudRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada con ID: " + id));
    }

    // --- MÉTODO AUXILIAR DE CONVERSIÓN ---
    private SolicitudDTO convertirADTO(Solicitud s) {
        return new SolicitudDTO(
                s.getId(),
                s.getDescripcion(),
                s.getFechaHoraRegistro(),
                s.getFechaCierre(),
                s.getEstado(),
                s.getTipo(),
                new EstudianteDTO(
                        s.getEstudiante().getId(),
                        s.getEstudiante().getNombreCompleto(),
                        s.getEstudiante().getCorreo(),
                        s.getEstudiante().getPrograma()),
                // Si hay responsable, lo mapeamos, si no, va null
                s.getResponsableAsignado() != null ? new ResponsableDTO(
                        s.getResponsableAsignado().getId(),
                        s.getResponsableAsignado().getNombreCompleto(),
                        s.getResponsableAsignado().getCargo(),
                        s.getResponsableAsignado().isActivo()) : null,
                s.getPrioridad() != null ? new PrioridadDTO(
                        s.getPrioridad().getNivel(),
                        s.getPrioridad().getImpactoAcademico(),
                        s.getPrioridad().getJustificacion(),
                        s.getPrioridad().getVigencia()) : null
        );
    }
}
