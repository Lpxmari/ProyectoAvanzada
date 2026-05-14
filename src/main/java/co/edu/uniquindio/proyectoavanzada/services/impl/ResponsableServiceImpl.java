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
    public ResponsableDTO crearResponsable(ResponsableDTO dto) {
        Responsable nuevo = new Responsable();

        // 1. Campos propios de Responsable
        nuevo.setNombreCompleto(dto.nombreCompleto());
        nuevo.setCargo(dto.cargo());

        // 2. Campos heredados de Usuario (Obligatorios)
        // Como no están en el DTO, les asignamos valores por defecto para probar
        nuevo.setUsername(dto.nombreCompleto().toLowerCase().replace(" ", "."));
        nuevo.setPassword(passwordEncoder.encode("12345"));
        nuevo.setActivo(true);
        nuevo.setDeleted(false);

        // NOTA: No llamamos a setRol() porque el rol se determina
        // automáticamente por el tipo de clase (Responsable) en Usuario.getRol()

        Responsable guardado = responsableRepository.save(nuevo);
        return convertirADTO(guardado);
    }

    @Override
    public ResponsableDTO actualizarResponsable(Long id, ResponsableDTO datos) {
        Responsable existente = responsableRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Responsable no encontrado"));

        existente.setNombreCompleto(datos.nombreCompleto());
        existente.setCargo(datos.cargo());
        existente.setActivo(datos.activo());

        return convertirADTO(responsableRepository.save(existente));
    }

    @Override
    public List<ResponsableDTO> listarActivos() {
        return responsableRepository.findAll().stream()
                .filter(r -> !r.isDeleted())
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    public ResponsableDTO obtenerPorId(Long id) {
        Responsable r = responsableRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No encontrado"));
        return convertirADTO(r);
    }

    @Override
    public void eliminarResponsable(Long id) {
        Responsable r = responsableRepository.findById(id).orElseThrow();
        r.setDeleted(true);
        r.setDeletedAt(LocalDateTime.now());
        responsableRepository.save(r);
    }

    private ResponsableDTO convertirADTO(Responsable r) {
        return new ResponsableDTO(
                r.getId(),
                r.getNombreCompleto(),
                r.getCargo(),
                r.getActivo()
        );
    }
}