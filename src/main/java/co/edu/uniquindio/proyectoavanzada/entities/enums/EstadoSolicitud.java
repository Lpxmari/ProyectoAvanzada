package co.edu.uniquindio.proyectoavanzada.entities.enums;


public enum EstadoSolicitud {
    REGISTRADA,    // Recién creada por el estudiante
    CLASIFICADA,   // Ya pasó por el proceso de Triage (priorizada)
    EN_ATENCION,   // Tiene un responsable asignado y se está trabajando
    ATENDIDA,      // El responsable ya dio una respuesta
    CERRADA        // Proceso finalizado totalmente
}