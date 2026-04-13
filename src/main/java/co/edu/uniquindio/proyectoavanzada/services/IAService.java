package co.edu.uniquindio.proyectoavanzada.services;

/**
 * Contrato del servicio de IA. Tiene dos implementaciones:
 * {@code IAServiceFallbackImpl} (reglas) y {@code IAServiceOpenAIImpl} (Spring AI).
 */
public interface IAService {
    String sugerirClasificacion(String descripcion);
    String generarResumen(Long solicitudId);
}
