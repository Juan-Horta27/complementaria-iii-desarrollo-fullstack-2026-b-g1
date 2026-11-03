package com.corhuila.scoresound.controller;

import com.corhuila.scoresound.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint del login.
 *
 * Igual que los demas controllers: recibe la peticion, le pasa el
 * trabajo al service y traduce el resultado a un codigo HTTP.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    /** POST /auth/login -> 200 con el token, 401 si usuario o clave son incorrectos */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest peticion) {
        return service.login(peticion.usuario(), peticion.clave())
                .map(token -> ResponseEntity.ok(new TokenResponse(token)))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    /** Cuerpo que recibe el login: { "usuario": "...", "clave": "..." } */
    public record LoginRequest(String usuario, String clave) {
    }

    /** Cuerpo que devuelve el login: { "token": "..." } */
    public record TokenResponse(String token) {
    }
}
