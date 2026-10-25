package com.corhuila.scoresound.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuracion de seguridad de la API.
 *
 * - /auth/login es publico: ahi se pide el token.
 * - Todo lo demas (por ejemplo /usuarios) exige un token valido.
 * - Sin token, o con un token invalido, la respuesta es 401.
 * - CORS (semana 12): se acepta el frontend que corre en localhost:4200.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filtros(HttpSecurity http, JwtService jwtService) throws Exception {
        http
                // Activa CORS dentro de la cadena de seguridad (ver corsConfigurationSource)
                .cors(Customizer.withDefaults())
                // No hay sesion ni formularios: la identidad viaja en el token,
                // por eso CSRF no hace falta y cada peticion se valida sola.
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(rutas -> rutas
                        .requestMatchers("/auth/login").permitAll()
                        // Swagger sigue abierto para poder ver la documentacion
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // /error es la ruta interna donde Spring muestra los errores
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())
                // Sin autenticacion Spring respondia 403; aqui se cambia a 401
                .exceptionHandling(errores -> errores
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .addFilterBefore(new JwtFilter(jwtService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS: el navegador bloquea que una pagina de un origen (el frontend en
     * localhost:4200) llame a una API de otro origen (esta, en localhost:8080)
     * si la API no lo permite. Aqui se le dice a la API que acepte ese origen.
     *
     * Se configura en la seguridad y no con @CrossOrigin en cada controller
     * porque, antes del GET real, el navegador manda una peticion previa
     * (OPTIONS) sin token; si Spring Security la viera primero, respondia 401.
     * Authorization debe estar en los headers permitidos porque ahi viaja el token.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/**", config);
        return fuente;
    }

    /** Cifra y compara contrasenas con BCrypt (nunca se guardan en texto plano). */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Usuario de prueba para el login: admin / admin123 (guardado cifrado). */
    @Bean
    public UserDetailsService usuarios(PasswordEncoder encoder) {
        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("admin123"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }

    /** Es el que verifica usuario y clave cuando alguien hace login. */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
