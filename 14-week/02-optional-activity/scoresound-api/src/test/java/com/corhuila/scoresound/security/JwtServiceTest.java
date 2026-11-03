package com.corhuila.scoresound.security;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pruebas unitarias del JwtService.
 *
 * Es una prueba unitaria: se crea el service a mano, sin levantar Spring,
 * y se comprueba una sola pieza aislada.
 */
class JwtServiceTest {

    private static final long UNA_HORA_MS = 3_600_000;

    private final JwtService jwtService =
            new JwtService("clave-de-prueba-del-servidor-scoresound", UNA_HORA_MS);

    /** Camino feliz: un token recien creado devuelve el usuario que lleva dentro. */
    @Test
    void tokenValidoDevuelveElUsuario() {
        String token = jwtService.generarToken("admin");

        assertEquals(Optional.of("admin"), jwtService.usuarioDe(token));
    }

    /** Caso de error: un token firmado con otra clave se rechaza y no devuelve usuario. */
    @Test
    void tokenFirmadoConOtraClaveSeRechaza() {
        JwtService otroServidor =
                new JwtService("otra-clave-distinta-para-el-atacante-1234", UNA_HORA_MS);
        String tokenFalso = otroServidor.generarToken("admin");

        assertEquals(Optional.empty(), jwtService.usuarioDe(tokenFalso));
    }
}
