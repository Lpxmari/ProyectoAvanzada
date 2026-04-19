Sistema de Gestión de Triage Universitario

Descripción del Proyecto
Este sistema es una solución desarrollada con Java 21 y Spring Boot 3 para gestionar las solicitudes académicas de los estudiantes de la Universidad del Quindío. 
El enfoque de esta entrega es la implementación de la lógica de Triage, permitiendo la clasificación, priorización y seguimiento detallado de cada caso.

Tecnologías Utilizadas:
- Lenguaje: Java 21
- Framework: Spring Boot 3.x
- Persistencia: Spring Data JPA
- Base de Datos: MariaDB
- Seguridad: Spring Security con implementación de JSON Web Token (JWT)
- Mapeo de Datos: Patrón DTO con Lombok (Builder Pattern)
- Pruebas: 
- JUnit y Mockito

Estructura del Proyecto: 
El proyecto sigue una arquitectura organizada en capas:
- Controllers: Endpoints REST para la comunicación con el cliente.
- Services (Interfaces e Impl): Capa donde residen las reglas de negocio y el procesamiento de Triage.
- Entities: Modelado de datos (Estudiante, Solicitud, Historial, Prioridad, Responsable).
- DTOs: Objetos de transferencia de datos utilizados para desacoplar la API de la persistencia y manejar correctamente los Enums.
- Repositories: Interfaces para la gestión de datos con MariaDB.


Justificación de la Lógica de Negocio (Hito 2)

El desarrollo se ha basado en una visión de autonomía técnica y funcional, destacando los siguientes puntos:

1. Autonomía del Estudiante: El sistema permite que el estudiante no solo registre solicitudes, sino que acceda a su propio historial de estados. 
Esta transparencia garantiza que el usuario conozca el progreso y los responsables de su trámite en tiempo real.

2. Gestión de Triage Avanzada: Se ha implementado una clasificación de prioridad detallada que considera el impacto académico y la vigencia, permitiendo una
resolución eficiente de los casos según su criticidad.

4. Trazabilidad Total: Cada movimiento de la solicitud queda registrado en una entidad de Historial, capturando estados anteriores, estados nuevos, observaciones y
el responsable de la acción.

5. Valor Agregado en Seguridad: A pesar de no ser un requisito obligatorio para este hito, se ha adelantado la integración de Spring Security y JWT para garantizar
la protección de los endpoints y la autenticación basada en roles desde esta etapa del desarrollo.

7. Estandarización con DTOs: Se evitó la exposición de entidades directamente en los controladores. El uso de DTOs permitió resolver conflictos de tipos entre Enums
y Strings, facilitando el consumo de la API por parte del frontend.

Pruebas Unitarias: 
Se ha dado cumplimiento a la cobertura de pruebas requerida, validando los componentes principales:
- AuthServiceTest: Pruebas de autenticación y generación de tokens.
- EstudianteServiceTest: Pruebas del CRUD y reglas de validación de estudiantes.
- SolicitudServiceTest: Validación del flujo de Triage y transiciones de estado.
- ResponsableServiceTest: Gestión y asignación de responsables.
