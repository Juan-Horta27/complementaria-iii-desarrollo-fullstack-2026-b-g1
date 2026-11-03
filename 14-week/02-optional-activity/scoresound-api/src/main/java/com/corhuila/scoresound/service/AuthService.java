package com.corhuila.scoresound.service;

import com.corhuila.scoresound.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Logica del login: comprueba usuario y clave y, si son correctos,
 * entrega un token JWT.
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /** Devuelve el token si usuario y clave son correctos; si no, vacio. */
    public Optional<String> login(String usuario, String clave) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usuario, clave));
            return Optional.of(jwtService.generarToken(usuario));
        } catch (AuthenticationException e) {
            return Optional.empty();
        }
    }
}
