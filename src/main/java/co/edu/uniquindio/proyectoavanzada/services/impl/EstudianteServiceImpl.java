package co.edu.uniquindio.proyectoavanzada.services.impl;

import co.edu.uniquindio.proyectoavanzada.dto.EstudianteDTO;
import co.edu.uniquindio.proyectoavanzada.entities.Estudiante;
import co.edu.uniquindio.proyectoavanzada.entities.enums.ProgramaAcademico;
import co.edu.uniquindio.proyectoavanzada.excepciones.RecursoNoEncontradoException;
import co.edu.uniquindio.proyectoavanzada.repositories.EstudianteRepository;
import co.edu.uniquindio.proyectoavanzada.services.EstudianteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EstudianteServiceImpl implements EstudianteService {

    private final EstudianteRepository estudianteRepository;

    @Override
    public Long crearEstudiante(EstudianteDTO dto) {
        Estudiante nuevo = Estudiante.builder()
                .nombreCompleto(dto.getNombreCompleto())
                .correo(dto.getCorreo())
                .programa(ProgramaAcademico.valueOf(dto.getPrograma()))
                .build();
        return estudianteRepository.save(nuevo).getId();
    }

    @Override
    public void actualizarEstudiante(Long id, EstudianteDTO dto) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado"));

        estudiante.setNombreCompleto(dto.getNombreCompleto());
        estudiante.setCorreo(dto.getCorreo());
        estudiante.setPrograma(ProgramaAcademico.valueOf(dto.getPrograma()));

        estudianteRepository.save(estudiante);
    }

    @Override
    public void eliminarEstudiante(Long id) {
        if (!estudianteRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Estudiante no encontrado");
        }
        estudianteRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public EstudianteDTO obtenerEstudiante(Long id) {
        Estudiante e = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado"));

        return new EstudianteDTO(e.getId(), e.getNombreCompleto(), e.getCorreo(), e.getPrograma().name());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstudianteDTO> listarEstudiantes() {
        return estudianteRepository.findAll().stream()
                .map(e -> new EstudianteDTO(e.getId(), e.getNombreCompleto(), e.getCorreo(), e.getPrograma().name()))
                .collect(Collectors.toList());
    }
}