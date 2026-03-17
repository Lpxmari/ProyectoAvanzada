package co.edu.uniquindio.proyectoavanzada.controllers;

import co.edu.uniquindio.proyectoavanzada.dto.EstudianteDTO;
import co.edu.uniquindio.proyectoavanzada.services.EstudianteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estudiantes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EstudianteController {

    private final EstudianteService estudianteService;

    @PostMapping
    public ResponseEntity<String> crear(@Valid @RequestBody EstudianteDTO dto) {
        estudianteService.crearEstudiante(dto);
        return ResponseEntity.ok("Estudiante creado con éxito");
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstudianteDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(estudianteService.obtenerEstudiante(id));
    }

    @GetMapping
    public ResponseEntity<List<EstudianteDTO>> listar() {
        return ResponseEntity.ok(estudianteService.listarEstudiantes());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody EstudianteDTO dto) {
        estudianteService.actualizarEstudiante(id, dto);
        return ResponseEntity.ok("Estudiante actualizado correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        estudianteService.eliminarEstudiante(id);
        return ResponseEntity.ok("Estudiante eliminado");
    }
}