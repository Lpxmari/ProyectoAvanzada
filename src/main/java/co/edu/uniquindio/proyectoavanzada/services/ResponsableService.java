package co.edu.uniquindio.proyectoavanzada.services;

import co.edu.uniquindio.proyectoavanzada.entities.Responsable;
import co.edu.uniquindio.proyectoavanzada.repositories.ResponsableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

public interface ResponsableService {

    List<Responsable> listarActivos();

    Responsable crearResponsable(Responsable responsable);
}