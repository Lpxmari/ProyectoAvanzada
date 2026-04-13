package co.edu.uniquindio.proyectoavanzada.entities;

import co.edu.uniquindio.proyectoavanzada.entities.enums.RolUsuario;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    //@Enumerated(EnumType.STRING)
    //private RolUsuario rol;

    @Column(columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean activo;

    public String getRol(){
        var rol = "";

        if(this instanceof Responsable){
            rol = "ROLE_RESPONSABLE";
        }else if(this instanceof Estudiante){
            rol = "ROLE_ESTUDIANTE";
        }else{
            rol = "ROLE_ADMIN";
        }

        return rol;
    }

}