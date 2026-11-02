package com.corhuila.scoresound.service;

import com.corhuila.scoresound.entity.Usuario;
import com.corhuila.scoresound.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Logica de negocio de los usuarios.
 *
 * Es el unico que usa el repository. El controller nunca habla
 * directo con la base de datos: siempre pasa por aqui.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public List<Usuario> listar() {
        return repository.findAll();
    }

    public Optional<Usuario> buscar(Long id) {
        return repository.findById(id);
    }

    public Usuario crear(Usuario usuario) {
        // Un POST siempre crea. Si el cliente mandara un id en el JSON,
        // save() haria un UPDATE sobre ese registro; por eso se anula.
        usuario.setId(null);
        // La fecha la pone el servidor, no el cliente.
        usuario.setFechaRegistro(LocalDateTime.now());
        return repository.save(usuario);
    }

    public Optional<Usuario> actualizar(Long id, Usuario datos) {
        // Solo se cambian los campos editables; el id y la fecha de
        // registro se conservan. Si el id no existe, devuelve vacio.
        return repository.findById(id).map(existente -> {
            existente.setNombre(datos.getNombre());
            existente.setCorreo(datos.getCorreo());
            return repository.save(existente);
        });
    }

    public boolean eliminar(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }
}
