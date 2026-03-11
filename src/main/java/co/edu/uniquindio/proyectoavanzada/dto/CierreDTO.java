package co.edu.uniquindio.proyectoavanzada.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CierreDTO {
    private Long responsableId;
    private String observacion;
    private LocalDateTime fecha;
}
