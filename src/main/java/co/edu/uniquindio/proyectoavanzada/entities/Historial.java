package co.edu.uniquindio.proyectoavanzada.entities;



import co.edu.uniquindio.proyectoavanzada.entities.enums.EstadoSolicitud;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historiales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Historial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaHora;
    private String observaciones;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estadoAnterior;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estadoNuevo;

    @ManyToOne
    @JoinColumn(name = "solicitud_id")
    private Solicitud solicitud;

    @ManyToOne
    @JoinColumn(name = "responsable_id")
    private Responsable responsableAccion;
}