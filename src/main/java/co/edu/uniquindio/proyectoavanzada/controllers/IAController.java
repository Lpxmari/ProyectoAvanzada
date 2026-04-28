package co.edu.uniquindio.proyectoavanzada.controllers;

import co.edu.uniquindio.proyectoavanzada.services.IAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ia")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class IAController {

    private final IAService iaService;

    // RF-10: Sugerir clasificacion y prioridad
    @PostMapping("/sugerir")
    public ResponseEntity<String> sugerirClasificacion(@RequestParam String descripcion) {
        return ResponseEntity.ok(iaService.sugerirClasificacion(descripcion));
    }

    // RF-09: Generar resumen del historial
    @GetMapping("/resumen/{solicitudId}")
    public ResponseEntity<String> generarResumen(@PathVariable Long solicitudId) {
        return ResponseEntity.ok(iaService.generarResumen(solicitudId));
    }
}