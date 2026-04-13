package co.edu.uniquindio.proyectoavanzada.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// Filtro que intercepta CADA petición HTTP antes de que llegue al controller
// Si la petición trae un token JWT válido, autentica al usuario automáticamente
// OncePerRequestFilter garantiza que el filtro se ejecute solo una vez por petición
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    // Inyectamos JwtUtil para poder validar y leer el token
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Leemos el header "Authorization" de la petición HTTP
        // El formato esperado es: "Bearer eyJhbGci..."
        String header = request.getHeader("Authorization");

        // Solo procesamos si el header existe y empieza con "Bearer "
        if (header != null && header.startsWith("Bearer ")) {

            // Quitamos "Bearer " para quedarnos solo con el token
            String token = header.substring(7);

            // Validamos que el token no esté expirado ni manipulado
            if (jwtUtil.esValido(token)) {

                // Extraemos quién es el usuario y qué rol tiene
                String username = jwtUtil.obtenerUsername(token);
                String rol = jwtUtil.obtenerRol(token);

                // Le decimos a Spring Security que este usuario está autenticado
                // SimpleGrantedAuthority convierte el rol en un permiso de Spring
                var auth = new UsernamePasswordAuthenticationToken(
                        username, null,
                        List.of(new SimpleGrantedAuthority(rol))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // Dejamos que la petición continúe hacia el controller
        filterChain.doFilter(request, response);
    }
}