package co.edu.uniquindio.proyectoavanzada.config;

import co.edu.uniquindio.proyectoavanzada.entities.Administrador;
import co.edu.uniquindio.proyectoavanzada.entities.Estudiante;
import co.edu.uniquindio.proyectoavanzada.entities.Responsable;
import co.edu.uniquindio.proyectoavanzada.entities.enums.ProgramaAcademico;
import co.edu.uniquindio.proyectoavanzada.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Se ejecuta automáticamente al arrancar la aplicación
// Precarga usuarios de prueba si no existen en la BD
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // Solo carga datos si la BD está vacía
        if (usuarioRepository.count() > 0) return;

        // ── ADMINISTRADOR ──
        Administrador admin = Administrador.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .activo(true)
                .nombreCompleto("Administrador del Sistema")
                .build();

        // ── RESPONSABLES ──
        Responsable coordinador = Responsable.builder()
                .username("coordinador1")
                .password(passwordEncoder.encode("resp123"))
                .activo(true)
                .nombreCompleto("Carlos Pérez")
                .cargo("coordinador")
                .isDeleted(false)
                .build();

        Responsable secretaria = Responsable.builder()
                .username("secretaria1")
                .password(passwordEncoder.encode("resp123"))
                .activo(true)
                .nombreCompleto("María López")
                .cargo("secretaria")
                .isDeleted(false)
                .build();

        // ── ESTUDIANTES ──
        Estudiante estudiante1 = Estudiante.builder()
                .username("est001")
                .password(passwordEncoder.encode("est123"))
                .activo(true)
                .nombreCompleto("Andrés Torres")
                .correo("andres.torres@uniquindio.edu.co")
                .programa(ProgramaAcademico.ING_SISTEMAS)
                .build();

        Estudiante estudiante2 = Estudiante.builder()
                .username("est002")
                .password(passwordEncoder.encode("est123"))
                .activo(true)
                .nombreCompleto("Laura Gómez")
                .correo("laura.gomez@uniquindio.edu.co")
                .programa(ProgramaAcademico.ING_SISTEMAS)
                .build();

        usuarioRepository.save(admin);
        usuarioRepository.save(coordinador);
        usuarioRepository.save(secretaria);
        usuarioRepository.save(estudiante1);
        usuarioRepository.save(estudiante2);

        System.out.println("✅ Datos iniciales cargados correctamente");
    }
}