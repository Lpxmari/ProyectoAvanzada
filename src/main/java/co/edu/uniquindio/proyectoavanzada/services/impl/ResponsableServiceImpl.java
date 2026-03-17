package co.edu.uniquindio.proyectoavanzada.services.impl;

import co.edu.uniquindio.proyectoavanzada.entities.Responsable;
import co.edu.uniquindio.proyectoavanzada.repositories.ResponsableRepository;
import co.edu.uniquindio.proyectoavanzada.services.ResponsableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // Lombok crea el constructor para inyectar los repositorios

public class ResponsableServiceImpl implements ResponsableService {

    private final ResponsableRepository responsableRepository;

    @Override
    public List<Responsable> listarActivos() {
        return responsableRepository.findAll().stream()
                .filter(Responsable::isActivo)
                .toList();
    }

    @Override
    public Responsable crearResponsable(Responsable responsable) {
        return responsableRepository.save(responsable);
    }
}
