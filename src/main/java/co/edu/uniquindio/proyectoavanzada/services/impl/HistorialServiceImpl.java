package co.edu.uniquindio.proyectoavanzada.services.impl;

import co.edu.uniquindio.proyectoavanzada.dto.HistorialDTO;
import co.edu.uniquindio.proyectoavanzada.dto.ResponsableDTO;
import co.edu.uniquindio.proyectoavanzada.repositories.HistorialRepository;
import co.edu.uniquindio.proyectoavanzada.services.HistorialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class HistorialServiceImpl
        implements HistorialService {

    private final HistorialRepository historialRepository;

    @Override
    public List<HistorialDTO> obtenerHistorialEstudiante(
            Integer estudianteId,
            Integer solicitudId
    ) {

        return historialRepository
                .obtenerHistorialEstudiante(
                        estudianteId,
                        solicitudId
                )
                .stream()
                .map(h -> new HistorialDTO(

                        h.getId(),

                        h.getFechaHora(),

                        h.getEstadoAnterior(),

                        h.getEstadoNuevo(),

                        h.getObservaciones(),

                        h.getResponsableAccion() != null

                                ? new ResponsableDTO(

                                h.getResponsableAccion()
                                        .getId(),

                                h.getResponsableAccion()
                                        .getNombreCompleto(),

                                h.getResponsableAccion()
                                        .getCargo(),

                                !h.getResponsableAccion()
                                        .isDeleted()

                        )

                                : null

                ))                .toList();

    }

}