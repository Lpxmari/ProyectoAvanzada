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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResponsableServiceTest {

    @Mock
    private ResponsableRepository responsableRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ResponsableServiceImpl responsableService;

    @Test
    void crearResponsable_exitoso() {
        Responsable responsable = Responsable.builder()
                .nombreCompleto("Juan Pérez")
                .cargo("coordinador")
                .activo(true)
                .password("123456")
                .build();

        when(passwordEncoder.encode("123456")).thenReturn("hash123");
        when(responsableRepository.save(any(Responsable.class))).thenReturn(responsable);

        ResponsableDTO resultado = responsableService.crearResponsable(responsable);

        assertNotNull(resultado);
        assertEquals("Juan Pérez", resultado.nombreCompleto());
        assertEquals("coordinador", resultado.cargo());
    }

    @Test
    void obtenerPorId_exitoso() {
        Responsable responsable = Responsable.builder()
                .nombreCompleto("María García")
                .cargo("secretaria")
                .activo(true)
                .isDeleted(false)
                .build();

        when(responsableRepository.findById(1L)).thenReturn(Optional.of(responsable));

        ResponsableDTO resultado = responsableService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals("María García", resultado.nombreCompleto());
    }

    @Test
    void obtenerPorId_noExiste_lanzaExcepcion() {
        when(responsableRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () ->
                responsableService.obtenerPorId(99L)
        );
    }

    @Test
    void eliminarResponsable_borradoLogico() {
        Responsable responsable = Responsable.builder()
                .nombreCompleto("Carlos López")
                .isDeleted(false)
                .build();

        when(responsableRepository.findById(1L)).thenReturn(Optional.of(responsable));
        when(responsableRepository.save(any(Responsable.class))).thenReturn(responsable);

        responsableService.eliminarResponsable(1L);

        assertTrue(responsable.isDeleted());
        assertNotNull(responsable.getDeletedAt());
        verify(responsableRepository, times(1)).save(responsable);
        verify(responsableRepository, never()).delete(any());
    }
}