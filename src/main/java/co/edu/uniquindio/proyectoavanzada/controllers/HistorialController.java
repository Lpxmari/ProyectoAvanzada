package co.edu.uniquindio.proyectoavanzada.controllers;

import co.edu.uniquindio.proyectoavanzada.dto.HistorialDTO;
import co.edu.uniquindio.proyectoavanzada.services.HistorialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historiales")
@RequiredArgsConstructor
@CrossOrigin("*")
public class HistorialController {

    private final HistorialService historialService;

    @GetMapping(
            "/estudiante/{estudianteId}/solicitud/{solicitudId}"
    )
    public ResponseEntity<List<HistorialDTO>>
    obtenerHistorialEstudiante(

            @PathVariable Integer estudianteId,

            @PathVariable Integer solicitudId

    ) {

        return ResponseEntity.ok(

                historialService
                        .obtenerHistorialEstudiante(
                                estudianteId,
                                solicitudId
                        )

        );

    }

}