package com.corhuila.scoresound.repository;

import com.corhuila.scoresound.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Acceso a datos de los usuarios.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Se usa al iniciar sesion. Devuelve Optional porque puede que
     * ese correo no exista, y asi se obliga a contemplar ese caso.
     */
    Optional<Usuario> findByCorreo(String correo);

    /** Evita que se registren dos cuentas con el mismo correo. */
    boolean existsByCorreo(String correo);
}
