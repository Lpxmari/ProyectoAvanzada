package co.edu.uniquindio.proyectoavanzada.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

// Componente utilitario para todo lo relacionado con tokens JWT
// JWT = JSON Web Token: cadena cifrada que identifica al usuario
@Component
public class JwtUtil {

    // Clave secreta con la que se firma el token — nunca debe exponerse
    private final Key key = Keys.hmacShaKeyFor(
            "clave-super-secreta-para-jwt-2024-uniquindio".getBytes()
    );

    // Tiempo de expiración del token: 8 horas en milisegundos
    private final long EXPIRACION = 1000 * 60 * 60 * 8;

    // Genera un token JWT con el username y el rol del usuario
    // Este token se entrega al usuario cuando hace login exitoso
    public String generarToken(String username, String rol) {
        return Jwts.builder()
                .subject(username)           // quién es el usuario
                .claim("rol", rol)           // qué rol tiene
                .issuedAt(new Date())        // cuándo se generó
                .expiration(new Date(System.currentTimeMillis() + EXPIRACION)) // cuándo expira
                .signWith(key)               // firma con la clave secreta
                .compact();
    }

    // Extrae el username del token (para saber quién está haciendo la petición)
    public String obtenerUsername(String token) {
        return parsear(token).getPayload().getSubject();
    }

    // Extrae el rol del token (para saber qué permisos tiene)
    public String obtenerRol(String token) {
        return parsear(token).getPayload().get("rol", String.class);
    }

    // Verifica si el token es válido (no expirado, no manipulado)
    // Retorna true si es válido, false si no
    public boolean esValido(String token) {
        try {
            parsear(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // Método privado que descifra y verifica la firma del token
    private Jws<Claims> parsear(String token) {
        return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(
                "clave-super-secreta-para-jwt-2024-uniquindio".getBytes()
        )).build().parseSignedClaims(token);
    }
}