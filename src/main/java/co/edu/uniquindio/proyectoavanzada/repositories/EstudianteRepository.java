package co.edu.uniquindio.proyectoavanzada.repositories;

import co.edu.uniquindio.proyectoavanzada.entities.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    // Para buscar por correo si lo necesitas más adelante
    Optional<Estudiante> findByCorreo(String correo);
    Optional<Estudiante> findByUsername(String username);
}