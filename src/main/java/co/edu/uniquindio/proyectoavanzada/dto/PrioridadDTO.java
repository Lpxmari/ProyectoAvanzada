package co.edu.uniquindio.proyectoavanzada.dto;

import co.edu.uniquindio.proyectoavanzada.entities.enums.NivelSolicitud;

import java.time.LocalDate;

public record PrioridadDTO(
        NivelSolicitud nivel,
        String impactoAcademico,
        String justificacion,
        LocalDate vigencia
) {}
