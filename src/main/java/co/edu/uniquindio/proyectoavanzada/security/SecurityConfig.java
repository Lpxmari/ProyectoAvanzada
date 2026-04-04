package co.edu.uniquindio.proyectoavanzada.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

// Clase principal de configuración de Spring Security
// Define qué endpoints son públicos y cuáles requieren autenticación y rol específico
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // Inyectamos el filtro JWT para registrarlo en la cadena de seguridad
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitamos CSRF porque usamos JWT (no sesiones)
                .csrf(csrf -> csrf.disable())

                // No usamos sesiones — cada request se autentica con el token
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Definimos las reglas de acceso por endpoint y rol
                .authorizeHttpRequests(auth -> auth

                        // Login es público
                        .requestMatchers("/api/auth/**").permitAll()

                        // Solo ADMIN gestiona responsables
                        .requestMatchers("/api/responsables/**").hasRole("ADMIN")

                        // ESTUDIANTE solo puede registrar solicitudes (POST)
                        .requestMatchers(HttpMethod.POST, "/api/solicitudes").hasAnyRole("ESTUDIANTE", "ADMIN")

                        // Solo ADMIN puede asignar responsable, priorizar y cerrar
                        .requestMatchers("/api/solicitudes/*/responsable").hasRole("ADMIN")
                        .requestMatchers("/api/solicitudes/*/priorizar").hasRole("ADMIN")
                        .requestMatchers("/api/solicitudes/*/cerrar").hasRole("ADMIN")

                        // RESPONSABLE y ADMIN pueden ver solicitudes e historial
                        .requestMatchers(HttpMethod.GET, "/api/solicitudes/**").hasAnyRole("RESPONSABLE", "ADMIN")

                        // Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated()
                )

                // Registramos el filtro JWT antes del filtro de autenticación estándar
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Bean para cifrar contraseñas con BCrypt
    // Se usa al crear usuarios y al validar el login
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}