package co.edu.uniquindio.proyectoavanzada.services.impl;

import co.edu.uniquindio.proyectoavanzada.entities.Historial;
import co.edu.uniquindio.proyectoavanzada.entities.Solicitud;
import co.edu.uniquindio.proyectoavanzada.excepciones.RecursoNoEncontradoException;
import co.edu.uniquindio.proyectoavanzada.repositories.SolicitudRepository;
import co.edu.uniquindio.proyectoavanzada.services.IAService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IAServiceImpl implements IAService {

    private final SolicitudRepository solicitudRepository;
    private final RestTemplate restTemplate;

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    @Value("${spring.ai.openai.chat.options.model}")
    private String modelo;

    private static final String PROMPT_CLASIFICACION = """
            Eres un asistente de gestion de solicitudes academicas universitarias.
            
            Tipos posibles: REGISTRO_ASIGNATURA, HOMOLOGACION, CANCELACION_ASIGNATURA, CUPOS, CONSULTA_ACADEMICA
            Prioridades posibles: BAJA, MEDIA, ALTA
            
            Analiza esta descripcion y responde SOLO con JSON (sin markdown):
            {"tipoSugerido":"NOMBRE","prioridadSugerida":"NOMBRE","confianza":0.85,"explicacion":"..."}
            
            Descripcion: %s
            """;

    private static final String PROMPT_RESUMEN = """
            Genera un resumen ejecutivo en espanol de esta solicitud academica universitaria:
            %s
            Incluye: estado actual, puntos clave del historial y proximos pasos recomendados.
            Responde en maximo 3 oraciones.
            """;

    @Override
    public String sugerirClasificacion(String descripcion) {
        try {
            String prompt = String.format(PROMPT_CLASIFICACION, descripcion);
            return llamarIA(prompt);
        } catch (Exception e) {
            System.out.println("❌ Error IA: " + e.getMessage());
            return fallbackClasificacion(descripcion);
        }
    }

    @Override
    public String generarResumen(Long solicitudId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Solicitud no encontrada con ID: " + solicitudId));
        try {
            String datos = construirDatos(solicitud);
            String prompt = String.format(PROMPT_RESUMEN, datos);
            return llamarIA(prompt);
        } catch (Exception e) {
            // Fallback si falla la IA
            return fallbackResumen(solicitud);
        }
    }

    private String llamarIA(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        headers.set("HTTP-Referer", "http://localhost:8080");
        headers.set("X-Title", "ProyectoAvanzada");

        Map<String, Object> body = Map.of(
                "model", modelo,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/chat/completions",
                request,
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        return (String) message.get("content");
    }

    private String construirDatos(Solicitud solicitud) {
        StringBuilder sb = new StringBuilder();
        sb.append("Descripcion: ").append(solicitud.getDescripcion()).append("\n");
        sb.append("Estado: ").append(solicitud.getEstado()).append("\n");
        if (solicitud.getTipo() != null) sb.append("Tipo: ").append(solicitud.getTipo()).append("\n");
        if (solicitud.getPrioridad() != null) sb.append("Prioridad: ").append(solicitud.getPrioridad()).append("\n");
        if (solicitud.getResponsableAsignado() != null) sb.append("Asignado a: ").append(solicitud.getResponsableAsignado().getNombreCompleto()).append("\n");
        sb.append("\nHistorial:\n");
        for (Historial h : solicitud.getHistoriales()) {
            sb.append("- ").append(h.getFechaHora()).append(" | ").append(h.getEstadoNuevo());
            if (h.getObservaciones() != null) sb.append(" | ").append(h.getObservaciones());
            sb.append("\n");
        }
        return sb.toString();
    }

    private String fallbackClasificacion(String descripcion) {
        String desc = descripcion.toLowerCase();
        String tipo = "CONSULTA_ACADEMICA";
        String prioridad = "MEDIA";

        if (desc.contains("cupo") || desc.contains("cupos")) { tipo = "CUPOS"; prioridad = "ALTA"; }
        else if (desc.contains("homolog")) { tipo = "HOMOLOGACION"; prioridad = "MEDIA"; }
        else if (desc.contains("cancel")) { tipo = "CANCELACION_ASIGNATURA"; prioridad = "ALTA"; }
        else if (desc.contains("registro") || desc.contains("inscripcion")) { tipo = "REGISTRO_ASIGNATURA"; prioridad = "ALTA"; }

        return String.format("{\"tipoSugerido\":\"%s\",\"prioridadSugerida\":\"%s\",\"confianza\":0.70,\"explicacion\":\"Sugerencia por palabras clave\",\"fuente\":\"fallback\"}", tipo, prioridad);
    }

    private String fallbackResumen(Solicitud solicitud) {
        return String.format("Solicitud #%d de tipo %s en estado %s registrada por %s.",
                solicitud.getId(), solicitud.getTipo(), solicitud.getEstado(),
                solicitud.getEstudiante().getNombreCompleto());
    }
}