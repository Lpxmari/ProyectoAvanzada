package co.edu.uniquindio.proyectoavanzada.services.impl;

import co.edu.uniquindio.proyectoavanzada.entities.Historial;
import co.edu.uniquindio.proyectoavanzada.entities.Solicitud;
import co.edu.uniquindio.proyectoavanzada.excepciones.RecursoNoEncontradoException;
import co.edu.uniquindio.proyectoavanzada.repositories.SolicitudRepository;
import co.edu.uniquindio.proyectoavanzada.services.IAService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
//import org.springframework.ai.chat.client.ChatClient;


@RequiredArgsConstructor
public class IAServiceImpl implements IAService {

    // final ChatClient chatClient;
    private final SolicitudRepository solicitudRepository;

    private static final String PROMPT_CLASIFICACION = """
            Eres un asistente de gestion de proyectos de software.
            
            Categorias posibles: BUG, FEATURE, MEJORA, DOCUMENTACION, INVESTIGACION
            Prioridades posibles: BAJA, MEDIA, ALTA, URGENTE
            
            Analiza esta descripcion de tarea y responde SOLO con JSON (sin markdown):
            {"categoriaSugerida":"NOMBRE","prioridadSugerida":"NOMBRE","confianza":0.85,"explicacion":"..."}
            
            Descripcion: %s
            """;

    private static final String PROMPT_RESUMEN = """
            Genera un resumen ejecutivo en espanol de esta tarea de desarrollo:
            %s
            Incluye: estado actual, puntos clave del historial y proximos pasos recomendados.
            """;

    @Override
    public String sugerirClasificacion(String descripcion) {
        try {
            /*String respuesta = chatClient.prompt()
                    .user(String.format(PROMPT_CLASIFICACION, descripcion))
                    .call()
                    .content();*/

            return "";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    @Override
    public String generarResumen(Long solicitudId) {
        try {
            Solicitud tarea = solicitudRepository.findById(solicitudId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Tarea: "+solicitudId));

            String datos = construirDatos(tarea);
            /*String resumen = chatClient.prompt()
                    .user(String.format(PROMPT_RESUMEN, datos))
                    .call()
                    .content();*/

            return "";
        } catch (RecursoNoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    private String construirDatos(Solicitud tarea) {
        StringBuilder sb = new StringBuilder();
        sb.append("Descripcion: ").append(tarea.getDescripcion()).append("\n");
        sb.append("Estado: ").append(tarea.getEstado()).append("\n");
        if (tarea.getTipo() != null) sb.append("Tipo: ").append(tarea.getTipo()).append("\n");
        if (tarea.getPrioridad() != null) sb.append("Prioridad: ").append(tarea.getPrioridad()).append("\n");
        if (tarea.getResponsableAsignado() != null) sb.append("Asignado a: ").append(tarea.getResponsableAsignado().getNombreCompleto()).append("\n");
        sb.append("\nHistorial:\n");
        for (Historial h : tarea.getHistoriales()) {
            sb.append("- ").append(h.getFechaHora()).append(" | ").append(h.getEstadoNuevo());
            if (h.getObservaciones() != null) sb.append(" | ").append(h.getObservaciones());
            sb.append("\n");
        }
        return sb.toString();
    }
}
