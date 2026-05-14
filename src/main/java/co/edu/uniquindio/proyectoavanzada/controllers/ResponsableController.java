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

    @PostMapping
    public ResponseEntity<ResponsableDTO> crear(@RequestBody ResponsableDTO responsableDTO) {
        // Usamos la variable 'responsableDTO' que viene en el parámetro
        ResponsableDTO creado = responsableService.crearResponsable(responsableDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponsableDTO> actualizar(
            @PathVariable Long id,
            @RequestBody ResponsableDTO responsableDTO) {
        // Usamos la variable 'responsableDTO'
        return ResponseEntity.ok(responsableService.actualizarResponsable(id, responsableDTO));
    }

    // Eliminar responsable
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        responsableService.eliminarResponsable(id);
        return ResponseEntity.ok("Responsable eliminado correctamente");
    }
}