package com.corhuila.scoresound.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

/**
 * Crea y valida los tokens JWT.
 *
 * Un JWT es una credencial firmada: lleva el nombre del usuario, la
 * fecha en que se creo y la fecha en que vence, y el servidor la firma
 * con una clave secreta. Si alguien cambia el contenido, la firma deja
 * de coincidir y el token se rechaza. El servidor no guarda nada en
 * memoria: solo comprueba la firma y la fecha en cada peticion.
 */
@Service
public class JwtService {

    private final SecretKey clave;
    private final long expiracionMs;

    public JwtService(@Value("${jwt.secret}") String secreto,
                      @Value("${jwt.expiracion-ms}") long expiracionMs) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.expiracionMs = expiracionMs;
    }

    /** Crea un token firmado para el usuario, con caducidad. */
    public String generarToken(String usuario) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(usuario)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + expiracionMs))
                .signWith(clave, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Devuelve el usuario que va dentro del token. Si el token esta
     * mal formado, la firma no coincide o ya vencio, devuelve vacio.
     */
    public Optional<String> usuarioDe(String token) {
        try {
            String usuario = Jwts.parser()
                    .verifyWith(clave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            return Optional.ofNullable(usuario);
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
