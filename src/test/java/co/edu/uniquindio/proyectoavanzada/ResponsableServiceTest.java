package co.edu.uniquindio.proyectoavanzada;

import co.edu.uniquindio.proyectoavanzada.dto.ResponsableDTO;
import co.edu.uniquindio.proyectoavanzada.entities.Responsable;
import co.edu.uniquindio.proyectoavanzada.excepciones.RecursoNoEncontradoException;
import co.edu.uniquindio.proyectoavanzada.repositories.ResponsableRepository;
import co.edu.uniquindio.proyectoavanzada.services.impl.ResponsableServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


//  Cubre: listar activos, crear, obtener, actualizar y soft-delete

@ExtendWith(MockitoExtension.class)
@DisplayName("ResponsableService — Tests Unitarios Completos")
class ResponsableServiceTest {

    @Mock private ResponsableRepository responsableRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private ResponsableServiceImpl responsableService;

    private Responsable responsableActivo;
    private Responsable responsableInactivo;
    private Responsable responsableBorrado;

    @BeforeEach
    void setUp() {
        responsableActivo = crearResponsable(1204L, "Prof. Pedro Martínez",
                "Coordinador Académico", true, false);
        responsableActivo.setPassword("passwordPlano");

        responsableInactivo = crearResponsable(2806L, "Prof. Inactivo",
                "Asesor", false, false);

        responsableBorrado = crearResponsable(1306L, "Borrado Lógico",
                "Docente", true, true);
    }



    // listarActivos

    @Nested
    @DisplayName("listarActivos")
    class ListarActivosTests {

        @Test
        @DisplayName("should_retornarDosResponsables_when_hayDosActivosYUnoInactivo")
        void should_retornarDosResponsables_when_hayDosActivosYUnoInactivo() {
            Responsable activo2 = crearResponsable(1204L, "Prof. Florez",
                    "Asesor", true, false);
            when(responsableRepository.findAll())
                    .thenReturn(List.of(responsableActivo, activo2, responsableInactivo));

            List<ResponsableDTO> resultado = responsableService.listarActivos();

            assertEquals(2, resultado.size());
            assertThat(resultado).extracting(ResponsableDTO::nombreCompleto)
                    .containsExactly("Prof. Pedro Martínez", "Prof. Florez");
        }

        @Test
        @DisplayName("should_excluirBorradosLogicos_when_listarActivos")
        void should_excluirBorradosLogicos_when_listarActivos() {
            when(responsableRepository.findAll()).thenReturn(List.of(responsableBorrado));

            assertTrue(responsableService.listarActivos().isEmpty());
        }

        @Test
        @DisplayName("should_excluirInactivos_when_listarActivos")
        void should_excluirInactivos_when_listarActivos() {
            when(responsableRepository.findAll()).thenReturn(List.of(responsableInactivo));

            assertTrue(responsableService.listarActivos().isEmpty());
        }

        @Test
        @DisplayName("should_mapearCargoCorrectamente_when_listarResponsablesActivos")
        void should_mapearCargoCorrectamente_when_listarResponsablesActivos() {
            when(responsableRepository.findAll()).thenReturn(List.of(responsableActivo));

            assertEquals("Coordinador Académico",
                    responsableService.listarActivos().get(0).cargo());
        }

        @Test
        @DisplayName("should_marcarActivoTrue_when_responsableActivo")
        void should_marcarActivoTrue_when_responsableActivo() {
            when(responsableRepository.findAll()).thenReturn(List.of(responsableActivo));

            assertTrue(responsableService.listarActivos().get(0).activo());
        }

        @Test
        @DisplayName("should_retornarListaVacia_when_todosInactivosOBorrados")
        void should_retornarListaVacia_when_todosInactivosOBorrados() {
            when(responsableRepository.findAll())
                    .thenReturn(List.of(responsableInactivo, responsableBorrado));

            assertTrue(responsableService.listarActivos().isEmpty());
        }
    }



    // crearResponsable

    @Nested
    @DisplayName("crearResponsable")
    class CrearResponsableTests {

        @Test
        @DisplayName("should_retornarDTO_when_crearResponsableExitoso")
        void should_retornarDTO_when_crearResponsableExitoso() {
            when(passwordEncoder.encode("passwordPlano")).thenReturn("$2a$encoded");
            when(responsableRepository.save(any())).thenReturn(responsableActivo);

            ResponsableDTO resultado = responsableService.crearResponsable(responsableActivo);

            assertNotNull(resultado);
            assertEquals(1204L,                     resultado.id());
            assertEquals("Prof. Pedro Martínez", resultado.nombreCompleto());
            assertEquals("Coordinador Académico",resultado.cargo());
        }

        @Test
        @DisplayName("should_cifrarPassword_when_crearResponsable")
        void should_cifrarPassword_when_crearResponsable() {
            when(passwordEncoder.encode("passwordPlano")).thenReturn("$2a$hashedValue");
            when(responsableRepository.save(any())).thenReturn(responsableActivo);

            responsableService.crearResponsable(responsableActivo);

            verify(passwordEncoder, times(1)).encode("passwordPlano");
        }
    }



    // obtenerPorId

    @Nested
    @DisplayName("obtenerPorId")
    class ObtenerPorIdTests {

        @Test
        @DisplayName("should_retornarDTO_when_responsableActivoExiste")
        void should_retornarDTO_when_responsableActivoExiste() {
            when(responsableRepository.findById(1L))
                    .thenReturn(Optional.of(responsableActivo));

            ResponsableDTO resultado = responsableService.obtenerPorId(1L);

            assertAll(
                    () -> assertEquals(1204L,                     resultado.id()),
                    () -> assertEquals("Prof. Pedro Martínez", resultado.nombreCompleto()),
                    () -> assertTrue(resultado.activo())
            );
        }

        @Test
        @DisplayName("should_lanzarRecursoNoEncontrado_when_idInexistente")
        void should_lanzarRecursoNoEncontrado_when_idInexistente() {
            when(responsableRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(RecursoNoEncontradoException.class,
                    () -> responsableService.obtenerPorId(999L));
        }

        @Test
        @DisplayName("should_lanzarRecursoNoEncontrado_when_responsableBorradoLogicamente")
        void should_lanzarRecursoNoEncontrado_when_responsableBorradoLogicamente() {
            when(responsableRepository.findById(1306L))
                    .thenReturn(Optional.of(responsableBorrado));

            assertThrows(RecursoNoEncontradoException.class,
                    () -> responsableService.obtenerPorId(1306L));
        }
    }



    // actualizarResponsable

    @Nested
    @DisplayName("actualizarResponsable")
    class ActualizarResponsableTests {

        @Test
        @DisplayName("should_retornarDTOActualizado_when_datosNuevosValidos")
        void should_retornarDTOActualizado_when_datosNuevosValidos() {
            Responsable datosNuevos = new Responsable();
            datosNuevos.setNombreCompleto("Prof. P. Martínez Actualizado");
            datosNuevos.setCargo("Director de Programa");
            datosNuevos.setActivo(true);

            when(responsableRepository.findById(1204L))
                    .thenReturn(Optional.of(responsableActivo));
            when(responsableRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            ResponsableDTO resultado =
                    responsableService.actualizarResponsable(1204L, datosNuevos);

            assertAll(
                    () -> assertEquals("Prof. P. Martínez Actualizado", resultado.nombreCompleto()),
                    () -> assertEquals("Director de Programa", resultado.cargo()),
                    () -> assertTrue(resultado.activo())
            );
        }

        @Test
        @DisplayName("should_desactivarResponsable_when_activoSetFalse")
        void should_desactivarResponsable_when_activoSetFalse() {
            Responsable datosNuevos = new Responsable();
            datosNuevos.setNombreCompleto("Prof. Martínez");
            datosNuevos.setCargo("Coordinador");
            datosNuevos.setActivo(false);

            when(responsableRepository.findById(1204L))
                    .thenReturn(Optional.of(responsableActivo));
            when(responsableRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            ResponsableDTO resultado =
                    responsableService.actualizarResponsable(1204L, datosNuevos);

            assertFalse(resultado.activo());
        }

        @Test
        @DisplayName("should_noModificarId_when_actualizarResponsable")
        void should_noModificarId_when_actualizarResponsable() {
            Responsable datosNuevos = new Responsable();
            datosNuevos.setNombreCompleto("Nuevo nombre");
            datosNuevos.setCargo("Nuevo cargo");
            datosNuevos.setActivo(true);

            when(responsableRepository.findById(1204L))
                    .thenReturn(Optional.of(responsableActivo));
            when(responsableRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            assertEquals(1204L,
                    responsableService.actualizarResponsable(1204L, datosNuevos).id());
        }

        @Test
        @DisplayName("should_lanzarRecursoNoEncontrado_when_idInexistenteEnActualizar")
        void should_lanzarRecursoNoEncontrado_when_idInexistenteEnActualizar() {
            when(responsableRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(RecursoNoEncontradoException.class,
                    () -> responsableService.actualizarResponsable(999L, new Responsable()));
            verify(responsableRepository, never()).save(any());
        }
    }



    // eliminarResponsable (soft delete)
    @Nested
    @DisplayName("eliminarResponsable — Soft Delete")
    class EliminarResponsableTests {

        @Test
        @DisplayName("should_marcarComoDeleted_when_eliminarResponsable")
        void should_marcarComoDeleted_when_eliminarResponsable() {
            ArgumentCaptor<Responsable> captor = ArgumentCaptor.forClass(Responsable.class);
            when(responsableRepository.findById(1204L))
                    .thenReturn(Optional.of(responsableActivo));
            when(responsableRepository.save(captor.capture())).thenReturn(responsableActivo);

            responsableService.eliminarResponsable(1204L);

            assertTrue(captor.getValue().isDeleted());
            verify(responsableRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("should_asignarFechaDeletedAt_when_softDelete")
        void should_asignarFechaDeletedAt_when_softDelete() {
            ArgumentCaptor<Responsable> captor = ArgumentCaptor.forClass(Responsable.class);
            LocalDateTime antes = LocalDateTime.now().minusSeconds(1);

            when(responsableRepository.findById(1204L))
                    .thenReturn(Optional.of(responsableActivo));
            when(responsableRepository.save(captor.capture())).thenReturn(responsableActivo);

            responsableService.eliminarResponsable(1204L);

            assertNotNull(captor.getValue().getDeletedAt());
            assertThat(captor.getValue().getDeletedAt()).isAfter(antes);
        }

        @Test
        @DisplayName("should_lanzarRecursoNoEncontrado_when_idInexistenteEnEliminar")
        void should_lanzarRecursoNoEncontrado_when_idInexistenteEnEliminar() {
            when(responsableRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(RecursoNoEncontradoException.class,
                    () -> responsableService.eliminarResponsable(999L));
            verify(responsableRepository, never()).save(any());
        }
    }


    // helper (Es un método helper privado que fabrica objetos Responsable listos
    //  para usar en las pruebas, evitando repetir las mismas líneas de construcción
    //  cada vez que necesito crear uno nuevo. Este método maneja dos campos
    //  extra: activo y deleted, porque Responsable los necesita para las pruebas de
    //  soft delete y filtrado de activos.)

    private Responsable crearResponsable(Long id, String nombre,
                                         String cargo, boolean activo, boolean deleted) {
        Responsable r = new Responsable();
        r.setId(id);
        r.setNombreCompleto(nombre);
        r.setCargo(cargo);
        r.setActivo(activo);
        r.setDeleted(deleted);
        return r;
    }
}
