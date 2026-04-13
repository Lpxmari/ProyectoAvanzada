package co.edu.uniquindio.proyectoavanzada.controllers;

import co.edu.uniquindio.proyectoavanzada.dto.*;
import co.edu.uniquindio.proyectoavanzada.entities.*;
import co.edu.uniquindio.proyectoavanzada.entities.enums.EstadoSolicitud;
import co.edu.uniquindio.proyectoavanzada.entities.enums.NivelSolicitud;
import co.edu.uniquindio.proyectoavanzada.services.SolicitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import co.edu.uniquindio.proyectoavanzada.repositories.EstudianteRepository;
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
    private final EstudianteRepository estudianteRepository;

    // 1. Registrar (Mantenemos tu CrearSolicitudDTO)
    @PostMapping
    public ResponseEntity<String> registrar(@RequestBody CrearSolicitudDTO dto) {
        Solicitud nueva = solicitudService.registrarSolicitud(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Solicitud registrada con éxito, su radicado es: "+nueva.getId());
    }

    // 2. Listar todas convirtiendo a DTO
    @GetMapping
    public ResponseEntity<List<SolicitudDTO>> listar() {
        return ResponseEntity.ok(solicitudService.listarTodas());
    }

    // 3. Detalle de una sola
    @GetMapping("/{id}")
    public ResponseEntity<SolicitudDTO> obtenerPorId(@PathVariable Long id) {
        SolicitudDTO s = solicitudService.obtenerPorId(id);
        return ResponseEntity.ok(s);
    }

    // 4. Filtrar por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<SolicitudDTO>> listarPorEstado(@PathVariable EstadoSolicitud estado) {
        List<SolicitudDTO> respuesta = solicitudService.listarPorEstado(estado);
        return ResponseEntity.ok(respuesta);
    }

    // 5. Triage: Ahora recibimos PrioridadDTO y la convertimos
    @PutMapping("/{id}/priorizar")
    public ResponseEntity<String> priorizar(@PathVariable Long id, @RequestBody PrioridadDTO prioridadDTO) {
        solicitudService.realizarTriage(id, prioridadDTO);
        return ResponseEntity.ok("Prioridad y Triage asignados correctamente");
    }

    // 6. Asignar responsable a una solicitud (ADMIN)
    @PutMapping("/{id}/responsable")
    public ResponseEntity<String> asignarResponsable(
            @PathVariable Long id,
            @RequestParam Long responsableId) {
        solicitudService.asignarResponsable(id, responsableId);
        return ResponseEntity.ok("Responsable asignado correctamente");
    }

    // 7. Consultar solicitudes asignadas a un responsable específico
    @GetMapping("/responsable/{idResponsable}")
    public ResponseEntity<List<SolicitudDTO>> listarPorResponsable(
            @PathVariable Long idResponsable) {
        List<SolicitudDTO> respuesta = solicitudService.listarPorResponsable(idResponsable);
        return ResponseEntity.ok(respuesta);
    }

    // 8. Cerrar Solicitud
    @PutMapping("/{id}/cerrar")
    public ResponseEntity<String> cerrarSolicitud(
            @PathVariable Long id,
            @RequestBody CierreDTO cierreDTO) {
        solicitudService.cerrarSolicitud(id, cierreDTO);
        return ResponseEntity.ok("La solicitud ha sido cerrada exitosamente.");
    }

    // 9. Obtener Historial (Usando DTO)
    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialDTO>> obtenerHistorial(@PathVariable Long id) {
        List<HistorialDTO> respuesta = solicitudService.obtenerHistorial(id);
        return ResponseEntity.ok(respuesta);
    }

    // 10. Consultar las solicitudes de un estudiante específico
    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<?> listarPorEstudiante(
            @PathVariable Long idEstudiante,
            Authentication authentication) {

        // Obtenemos el username del token JWT
        String usernameAutenticado = authentication.getName();

        // Si es ADMIN puede ver cualquier estudiante
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!esAdmin) {
            // Buscamos el estudiante autenticado por username
            Estudiante estudianteAutenticado = estudianteRepository
                    .findByUsername(usernameAutenticado)
                    .orElse(null);

            // Solo puede ver sus propias solicitudes
            if (estudianteAutenticado == null ||
                    !estudianteAutenticado.getId().equals(idEstudiante)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No tienes permiso para ver estas solicitudes");
            }
        }

        return ResponseEntity.ok(solicitudService.listarPorEstudiante(idEstudiante));
    }

    // 11 Marcar solicitud como atendida (Responsable)
    @PutMapping("/{id}/atender")
    public ResponseEntity<String> atender(
            @PathVariable Long id,
            @RequestParam(required = false) String observaciones) {
        solicitudService.marcarComoAtendida(id, observaciones);
        return ResponseEntity.ok("Solicitud marcada como atendida correctamente");
    }
}