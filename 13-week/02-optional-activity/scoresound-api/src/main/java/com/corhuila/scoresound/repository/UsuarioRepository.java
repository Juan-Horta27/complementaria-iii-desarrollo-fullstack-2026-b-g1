package com.corhuila.scoresound.repository;

import com.corhuila.scoresound.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso a datos de los usuarios.
 *
 * Es una interface, no una clase: Spring genera la implementacion al
 * arrancar la aplicacion. Al extender JpaRepository ya vienen save(),
 * findAll(), findById(), deleteById() y existsById() sin escribir SQL.
 *
 * Los dos tipos entre <> son la entity que maneja (Usuario) y el tipo
 * de su clave primaria (Long).
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
