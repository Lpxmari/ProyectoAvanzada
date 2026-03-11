package co.edu.uniquindio.proyectoavanzada.repositories;

import co.edu.uniquindio.proyectoavanzada.entities.Prioridad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrioridadRepository extends JpaRepository<Prioridad, Long> {
}
