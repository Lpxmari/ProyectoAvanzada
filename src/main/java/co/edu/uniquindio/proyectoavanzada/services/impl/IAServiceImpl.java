package co.edu.uniquindio.proyectoavanzada.services.impl;

import co.edu.uniquindio.proyectoavanzada.entities.Historial;
import co.edu.uniquindio.proyectoavanzada.entities.Solicitud;
import co.edu.uniquindio.proyectoavanzada.excepciones.RecursoNoEncontradoException;
import co.edu.uniquindio.proyectoavanzada.repositories.SolicitudRepository;
import co.edu.uniquindio.proyectoavanzada.services.IAService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
//import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IAServiceImpl implements IAService {

    // final ChatClient chatClient;
    private final SolicitudRepository solicitudRepository;

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url}")
    private String apiUrl;

    @Value("${spring.ai.openai.chat.options.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String PROMPT_CLASIFICACION = """
            Eres un asistente de gestion de proyectos de software.
            
            Categorias posibles: BUG, FEATURE, MEJORA, DOCUMENTACION, INVESTIGACION
            Prioridades posibles: BAJA, MEDIA, ALTA, URGENTE
            
            Analiza esta descripcion de tarea y responde SOLO con JSON (sin markdown):
            {"categoriaSugerida":"NOMBRE","prioridadSugerida":"NOMBRE","confianza":0.85,"explicacion":"..."}
            Descripcion: %s
            IMPORTANTE:
            - Responde únicamente en texto plano.
            - No uses Markdown.
            - No uses títulos con #.
            - No uses negritas ni listas Markdown.
            - Usa párrafos normales y texto limpio.
            """;

    private static final String PROMPT_RESUMEN = """
            Genera un resumen ejecutivo en espanol de esta tarea de desarrollo:
            %s
            Incluye: estado actual, puntos clave del historial y proximos pasos recomendados.
            Reglas obligatorias:
            - Responde en texto plano.
            - No uses Markdown.
            - No uses símbolos como #, *, -, ``` o listas.
            - No uses tablas.
            - Redacta en párrafos normales.
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

        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada"));
        String prompt = String.format(PROMPT_RESUMEN, construirDatos(solicitud));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        headers.set("HTTP-Referer", "http://localhost:8080");
        headers.set("X-Title", "Proyecto Avanzada");

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                )
        );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl + "/chat/completions",
                HttpMethod.POST,
                request,
                Map.class
        );

        System.out.println(response);

        List<Map<String, Object>> choices =
                (List<Map<String, Object>>) response.getBody().get("choices");

        Map<String, Object> message =
                (Map<String, Object>) choices.get(0).get("message");

        return message.get("content").toString();
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
