package com.corhuila.scoresound.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
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

/**
 * Configuracion de seguridad de la API.
 *
 * - /auth/login es publico: ahi se pide el token.
 * - Todo lo demas (por ejemplo /usuarios) exige un token valido.
 * - Sin token, o con un token invalido, la respuesta es 401.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filtros(HttpSecurity http, JwtService jwtService) throws Exception {
        http
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
