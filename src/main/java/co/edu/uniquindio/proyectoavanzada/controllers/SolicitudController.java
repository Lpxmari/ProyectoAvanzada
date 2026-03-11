package co.edu.uniquindio.proyectoavanzada.controllers;

import co.edu.uniquindio.proyectoavanzada.dto.*;
import co.edu.uniquindio.proyectoavanzada.entities.Solicitud;
import co.edu.uniquindio.proyectoavanzada.services.SolicitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SolicitudController {

    private final SolicitudService solicitudService;

    // Registrar solicitud (Estudiante)
    @PostMapping
    public ResponseEntity<String> registrar(@RequestBody CrearSolicitudDTO dto) {
        // Aquí podrías convertir el DTO a Entidad antes de llamar al servicio
        solicitudService.registrarSolicitud(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Solicitud registrada con éxito");
    }

    // Consultar todas (Responsable)
    @GetMapping
    public ResponseEntity<List<Solicitud>> listar() {
        return ResponseEntity.ok(solicitudService.listarTodas());
    }

    // Triage: Asignar Prioridad
    @PutMapping("/{id}/priorizar")
    public ResponseEntity<String> priorizar(@PathVariable Long id, @RequestBody PrioridadDTO prioridadDTO) {
        // Lógica para convertir DTO y llamar al servicio
        return ResponseEntity.ok("Prioridad y Triage asignados correctamente");
    }

    // Asignar Responsable
    @PutMapping("/{id}/responsable")
    public ResponseEntity<String> asignarResponsable(@PathVariable Long id, @RequestBody Long responsableId) {
        solicitudService.asignarResponsable(id, responsableId);
        return ResponseEntity.ok("Responsable asignado al caso");
    }
}