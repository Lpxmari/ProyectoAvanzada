package co.edu.uniquindio.proyectoavanzada.controllers;


import co.edu.uniquindio.proyectoavanzada.dto.LoginRequest;
import co.edu.uniquindio.proyectoavanzada.dto.LoginResponse;
import co.edu.uniquindio.proyectoavanzada.services.AuthService;
import co.edu.uniquindio.proyectoavanzada.services.IAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class IAController {

    private final IAService iaService;

    @GetMapping("/{idSolicitud}/resumir")
    public ResponseEntity<LoginResponse> resumir(@PathVariable Long idSolicitud) {
        String token = iaService.generarResumen(idSolicitud);
        return ResponseEntity.ok(new LoginResponse(token));
    }

}
