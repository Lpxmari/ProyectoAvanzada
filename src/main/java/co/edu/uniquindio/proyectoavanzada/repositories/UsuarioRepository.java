package co.edu.uniquindio.proyectoavanzada.repositories;

import co.edu.uniquindio.proyectoavanzada.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
}