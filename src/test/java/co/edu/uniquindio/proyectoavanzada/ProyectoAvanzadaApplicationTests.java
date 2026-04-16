package co.edu.uniquindio.proyectoavanzada;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Test de integración deshabilitado — requiere conexión a MariaDB
// Para ejecutar: levantar la BD primero y quitar @Disabled

@Disabled("Requiere conexión a base de datos MariaDB en localhost:3307")
@SpringBootTest
class ProyectoAvanzadaApplicationTests {

    @Test
    void contextLoads() {
    }
}
