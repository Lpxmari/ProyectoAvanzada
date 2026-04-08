package co.edu.uniquindio.proyectoavanzada.services.impl;

import co.edu.uniquindio.proyectoavanzada.dto.ResponsableDTO;
import co.edu.uniquindio.proyectoavanzada.entities.Responsable;
import co.edu.uniquindio.proyectoavanzada.excepciones.RecursoNoEncontradoException;
import co.edu.uniquindio.proyectoavanzada.repositories.ResponsableRepository;
import co.edu.uniquindio.proyectoavanzada.services.ResponsableService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResponsableServiceImpl implements ResponsableService {

    private final ResponsableRepository responsableRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<ResponsableDTO> listarActivos() {
        return responsableRepository.findAll().stream()
                .filter(r -> !r.isDeleted())
                .filter(r -> Boolean.TRUE.equals(r.getActivo()))
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    public ResponsableDTO crearResponsable(Responsable responsable) {
        responsable.setPassword( passwordEncoder.encode(responsable.getPassword()) );
        Responsable guardado = responsableRepository.save(responsable);
        return convertirADTO(guardado);
    }

    @Override
    public ResponsableDTO obtenerPorId(Long id) {
        Responsable responsable = responsableRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Responsable no encontrado con ID: " + id));

        if(responsable.isDeleted()){
            throw new RecursoNoEncontradoException("El responsable no existe");
        }

        return convertirADTO(responsable);
    }

    @Override
    public ResponsableDTO actualizarResponsable(Long id, Responsable datos) {
        Responsable existente = responsableRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Responsable no encontrado con ID: " + id));

        existente.setNombreCompleto(datos.getNombreCompleto());
        existente.setCargo(datos.getCargo());
        existente.setActivo(datos.getActivo());

        return convertirADTO(responsableRepository.save(existente));
    }

    @Override
    public void eliminarResponsable(Long id) {
        Responsable existente = responsableRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Responsable no encontrado con ID: " + id));

        existente.setDeleted(true);
        existente.setDeletedAt(LocalDateTime.now());

        responsableRepository.save(existente);

    }

    // Método auxiliar de conversión — igual al patrón de SolicitudServiceImpl
    private ResponsableDTO convertirADTO(Responsable r) {
        return new ResponsableDTO(
                r.getId(),
                r.getNombreCompleto(),
                r.getCargo(),
                Boolean.TRUE.equals(r.getActivo())
        );
    }
}