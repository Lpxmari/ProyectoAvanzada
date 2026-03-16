package co.edu.uniquindio.proyectoavanzada.controllers;

import co.edu.uniquindio.proyectoavanzada.dto.*;
import co.edu.uniquindio.proyectoavanzada.entities.*;
import co.edu.uniquindio.proyectoavanzada.entities.enums.EstadoSolicitud;
import co.edu.uniquindio.proyectoavanzada.entities.enums.NivelSolicitud;
import co.edu.uniquindio.proyectoavanzada.services.SolicitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SolicitudController {

    private final SolicitudService solicitudService;

    // 1. Registrar (Mantenemos tu CrearSolicitudDTO)
    @PostMapping
    public ResponseEntity<String> registrar(@RequestBody CrearSolicitudDTO dto) {
        solicitudService.registrarSolicitud(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Solicitud registrada con éxito");
    }

    // 2. Listar todas convirtiendo a DTO
    @GetMapping
    public ResponseEntity<List<SolicitudDTO>> listar() {
        List<SolicitudDTO> respuesta = solicitudService.listarTodas().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(respuesta);
    }

    // 3. Detalle de una sola
    @GetMapping("/{id}")
    public ResponseEntity<SolicitudDTO> obtenerPorId(@PathVariable Long id) {
        Solicitud s = solicitudService.obtenerPorId(id);
        return ResponseEntity.ok(convertirADTO(s));
    }

    // 4. Filtrar por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<SolicitudDTO>> listarPorEstado(@PathVariable EstadoSolicitud estado) {
        List<SolicitudDTO> respuesta = solicitudService.listarPorEstado(estado).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(respuesta);
    }

    // 5. Triage: Ahora recibimos PrioridadDTO y la convertimos
    @PutMapping("/{id}/priorizar")
    public ResponseEntity<String> priorizar(@PathVariable Long id, @RequestBody PrioridadDTO prioridadDTO) {
        // Convertimos el String del DTO al Enum que pide la Entidad
        Prioridad prioridad = Prioridad.builder()
                .nivel(NivelSolicitud.valueOf(prioridadDTO.getNivel())) // <--- Usa .valueOf() aquí
                .impactoAcademico(prioridadDTO.getImpactoAcademico())
                .justificacion(prioridadDTO.getJustificacion())
                .vigencia(prioridadDTO.getVigencia())
                .build();

        solicitudService.realizarTriage(id, prioridad);
        return ResponseEntity.ok("Prioridad y Triage asignados correctamente");
    }

    // --- MÉTODO AUXILIAR DE CONVERSIÓN ---
    private SolicitudDTO convertirADTO(Solicitud s) {
        return SolicitudDTO.builder()
                .id(s.getId())
                .descripcion(s.getDescripcion())
                .fechaHoraRegistro(s.getFechaHoraRegistro())
                .fechaCierre(s.getFechaCierre())
                .estado(s.getEstado())
                .tipoSolicitud(s.getTipo())
                .estudiante(new EstudianteDTO(
                        s.getEstudiante().getId(),
                        s.getEstudiante().getNombreCompleto(),
                        s.getEstudiante().getCorreo(),
                        s.getEstudiante().getPrograma().name()))
                // Si hay responsable, lo mapeamos, si no, va null
                .responsableAsignado(s.getResponsableAsignado() != null ? new ResponsableDTO(
                        s.getResponsableAsignado().getId(),
                        s.getResponsableAsignado().getNombreCompleto(),
                        s.getResponsableAsignado().getCargo(),
                        s.getResponsableAsignado().isActivo()) : null)
                .build();
    }

    // 8. Cerrar Solicitud
    @PutMapping("/{id}/cerrar")
    public ResponseEntity<String> cerrarSolicitud(@PathVariable Long id) {
        solicitudService.cerrarSolicitud(id);
        return ResponseEntity.ok("La solicitud ha sido cerrada exitosamente.");
    }

    // 9. Obtener Historial (Usando DTO)
    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialDTO>> obtenerHistorial(@PathVariable Long id) {
        List<HistorialDTO> respuesta = solicitudService.obtenerHistorial(id).stream()
                .map(h -> HistorialDTO.builder()
                        .idHistorial(h.getId())
                        .fechaHora(h.getFechaHora())
                        .estadoAnterior(h.getEstadoAnterior())
                        .estadoNuevo(h.getEstadoNuevo())

                        .observaciones(h.getObservaciones())
                        .responsableAccion(h.getResponsableAccion() != null ? new ResponsableDTO(
                                h.getResponsableAccion().getId(),
                                h.getResponsableAccion().getNombreCompleto(),
                                h.getResponsableAccion().getCargo(),
                                h.getResponsableAccion().isActivo()
                        ) : null)
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(respuesta);
    }

    // 10. Consultar las solicitudes de un estudiante específico
    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<List<SolicitudDTO>> listarPorEstudiante(@PathVariable Long idEstudiante) {
        List<SolicitudDTO> respuesta = solicitudService.listarPorEstudiante(idEstudiante).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(respuesta);
    }
}