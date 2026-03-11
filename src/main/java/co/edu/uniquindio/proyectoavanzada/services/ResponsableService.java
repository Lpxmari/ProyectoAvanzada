package co.edu.uniquindio.proyectoavanzada.services;

import co.edu.uniquindio.proyectoavanzada.entities.Responsable;
import co.edu.uniquindio.proyectoavanzada.repositories.ResponsableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResponsableService {

    private final ResponsableRepository responsableRepository;

    public List<Responsable> listarActivos() {
        return responsableRepository.findAll().stream()
                .filter(Responsable::isActivo)
                .toList();
    }

    public Responsable crearResponsable(Responsable responsable) {
        return responsableRepository.save(responsable);
    }
}