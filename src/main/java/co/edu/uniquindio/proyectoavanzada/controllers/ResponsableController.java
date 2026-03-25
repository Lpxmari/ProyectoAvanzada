package co.edu.uniquindio.proyectoavanzada.controllers;

import co.edu.uniquindio.proyectoavanzada.dto.ResponsableDTO;
import co.edu.uniquindio.proyectoavanzada.entities.Responsable;
import co.edu.uniquindio.proyectoavanzada.services.ResponsableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/responsables")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ResponsableController {

    private final ResponsableService responsableService;

    // Listar responsables activos
    @GetMapping
    public ResponseEntity<List<ResponsableDTO>> listarActivos() {
        return ResponseEntity.ok(responsableService.listarActivos());
    }

    // Obtener uno por ID
    @GetMapping("/{id}")
    public ResponseEntity<ResponsableDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(responsableService.obtenerPorId(id));
    }

    // Crear nuevo responsable
    @PostMapping
    public ResponseEntity<ResponsableDTO> crear(@RequestBody Responsable responsable) {
        ResponsableDTO creado = responsableService.crearResponsable(responsable);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // Actualizar responsable
    @PutMapping("/{id}")
    public ResponseEntity<ResponsableDTO> actualizar(
            @PathVariable Long id,
            @RequestBody Responsable responsable) {
        return ResponseEntity.ok(responsableService.actualizarResponsable(id, responsable));
    }

    // Eliminar responsable
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        responsableService.eliminarResponsable(id);
        return ResponseEntity.ok("Responsable eliminado correctamente");
    }
}