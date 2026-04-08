package co.edu.uniquindio.proyectoavanzada.services;

import co.edu.uniquindio.proyectoavanzada.dto.CrearEstudianteDTO;
import co.edu.uniquindio.proyectoavanzada.dto.EstudianteDTO;
import co.edu.uniquindio.proyectoavanzada.entities.Estudiante;
import java.util.List;

public interface EstudianteService {

    Long crearEstudiante(CrearEstudianteDTO estudianteDTO);

    void actualizarEstudiante(Long id, EstudianteDTO estudianteDTO);

    void eliminarEstudiante(Long id);

    EstudianteDTO obtenerEstudiante(Long id);

    List<EstudianteDTO> listarEstudiantes();
}