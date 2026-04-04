package co.edu.uniquindio.proyectoavanzada;

import co.edu.uniquindio.proyectoavanzada.dto.ResponsableDTO;
import co.edu.uniquindio.proyectoavanzada.entities.Responsable;
import co.edu.uniquindio.proyectoavanzada.excepciones.RecursoNoEncontradoException;
import co.edu.uniquindio.proyectoavanzada.repositories.ResponsableRepository;
import co.edu.uniquindio.proyectoavanzada.services.impl.ResponsableServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Le decimos a JUnit que use Mockito para gestionar los mocks
@ExtendWith(MockitoExtension.class)
class ResponsableServiceTest {

    // Simulamos el repositorio — no toca la base de datos real
    @Mock
    private ResponsableRepository responsableRepository;

    // Inyectamos el mock en el servicio que vamos a probar
    @InjectMocks
    private ResponsableServiceImpl responsableService;

    // PRUEBA 1: Crear un responsable exitosamente
    @Test
    void crearResponsable_exitoso() {
        // Preparamos el objeto que vamos a guardar
        Responsable responsable = new Responsable();
        responsable.setNombreCompleto("Juan Pérez");
        responsable.setCargo("coordinador");
        responsable.setActivo(true);

        // Le decimos al mock qué debe retornar cuando se llame save()
        when(responsableRepository.save(responsable)).thenReturn(responsable);

        // Ejecutamos el método que queremos probar
        ResponsableDTO resultado = responsableService.crearResponsable(responsable);

        // Verificamos que el resultado es correcto
        assertNotNull(resultado);
        assertEquals("Juan Pérez", resultado.nombreCompleto());
        assertEquals("coordinador", resultado.cargo());
        assertTrue(resultado.activo());
    }

    // PRUEBA 2: Obtener responsable por ID exitosamente
    @Test
    void obtenerPorId_exitoso() {
        // Preparamos un responsable de prueba
        Responsable responsable = new Responsable();
        responsable.setNombreCompleto("María García");
        responsable.setCargo("secretaria");
        responsable.setActivo(true);
        responsable.setDeleted(false);

        // El mock retorna ese responsable cuando se busca por ID 1
        when(responsableRepository.findById(1L)).thenReturn(Optional.of(responsable));

        // Ejecutamos
        ResponsableDTO resultado = responsableService.obtenerPorId(1L);

        // Verificamos
        assertNotNull(resultado);
        assertEquals("María García", resultado.nombreCompleto());
    }

    // PRUEBA 3: Obtener responsable que no existe — debe lanzar excepción
    @Test
    void obtenerPorId_noExiste_lanzaExcepcion() {
        // El mock retorna vacío — simula que no hay nada en BD
        when(responsableRepository.findById(99L)).thenReturn(Optional.empty());

        // Verificamos que se lanza la excepción correcta
        assertThrows(RecursoNoEncontradoException.class, () -> {
            responsableService.obtenerPorId(99L);
        });
    }

    // PRUEBA 4: Eliminar responsable hace borrado lógico
    @Test
    void eliminarResponsable_borradoLogico() {
        Responsable responsable = new Responsable();
        responsable.setNombreCompleto("Carlos López");
        responsable.setDeleted(false);

        when(responsableRepository.findById(1L)).thenReturn(Optional.of(responsable));
        when(responsableRepository.save(any(Responsable.class))).thenReturn(responsable);

        // Ejecutamos el eliminar
        responsableService.eliminarResponsable(1L);

        // Verificamos que isDeleted quedó en true
        assertTrue(responsable.isDeleted());
        assertNotNull(responsable.getDeletedAt());

        // Verificamos que se llamó save() — no delete()
        verify(responsableRepository, times(1)).save(responsable);
        verify(responsableRepository, never()).delete(any());
    }
}