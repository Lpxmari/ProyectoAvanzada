package co.edu.uniquindio.proyectoavanzada.dto;

import java.time.LocalDateTime;

public record CierreDTO(
    Long responsableId,
    String observacion,
    LocalDateTime fecha
) {}
