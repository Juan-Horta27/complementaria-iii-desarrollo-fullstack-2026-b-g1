package com.corhuila.scoresound.repository;

import com.corhuila.scoresound.entity.Partitura;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Acceso a datos de las partituras.
 *
 * Es una interface, no una clase: Spring genera la implementacion
 * al arrancar la aplicacion. Al extender JpaRepository ya vienen
 * save(), findAll(), findById(), deleteById(), count() y
 * existsById() sin escribir una linea de SQL.
 *
 * Los dos tipos entre <> son la entity que maneja (Partitura) y
 * el tipo de su clave primaria (Long).
 */
public interface PartituraRepository extends JpaRepository<Partitura, Long> {

    /**
     * Biblioteca de un usuario, de la mas reciente a la mas antigua.
     * Spring traduce el nombre del metodo a la consulta: navega de
     * Partitura a su Usuario y compara el id de este.
     */
    List<Partitura> findByUsuarioIdOrderByFechaSubidaDesc(Long usuarioId);

    /**
     * Regla de negocio: no permitir dos partituras con el mismo
     * titulo para el mismo usuario. El service la consulta antes
     * de guardar.
     */
    boolean existsByTituloAndUsuarioId(String titulo, Long usuarioId);

    /** Buscador de la biblioteca, sin distinguir mayusculas. */
    List<Partitura> findByTituloContainingIgnoreCase(String texto);

    /** Filtro por instrumento, solo las que ya se pudieron convertir. */
    List<Partitura> findByInstrumentoAndProcesadaTrue(String instrumento);
}
