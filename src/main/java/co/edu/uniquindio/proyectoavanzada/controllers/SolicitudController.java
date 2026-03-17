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

    // 8. Cerrar Solicitud
    @PutMapping("/{id}/cerrar")
    public ResponseEntity<String> cerrarSolicitud(@PathVariable Long id) {
        solicitudService.cerrarSolicitud(id);
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
    public ResponseEntity<List<SolicitudDTO>> listarPorEstudiante(@PathVariable Long idEstudiante) {
        List<SolicitudDTO> respuesta = solicitudService.listarPorEstudiante(idEstudiante);
        return ResponseEntity.ok(respuesta);
    }
}