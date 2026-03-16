package co.edu.uniquindio.proyectoavanzada.entities;

import co.edu.uniquindio.proyectoavanzada.entities.enums.EstadoSolicitud;
import co.edu.uniquindio.proyectoavanzada.entities.enums.TipoSolicitud;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "solicitudes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcion;
    private LocalDateTime fechaHoraRegistro;
    private LocalDateTime fechaCierre;
    private String canalOrigen;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado; // Enum: REGISTRADA, CLASIFICADA...

    @Enumerated(EnumType.STRING)
    private TipoSolicitud tipo; // Enum: HOMOLOGACION, CUPOS...

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "estudiante_id")
    private Estudiante estudiante;

    @ManyToOne
    @JoinColumn(name = "responsable_id")
    private Responsable responsableAsignado;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "prioridad_id")
    private Prioridad prioridad;

    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL)
    private List<Historial> historiales;
}