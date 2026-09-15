package com.corhuila.scoresound.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Partitura subida por un usuario a su biblioteca.
 *
 * Se mapea a la tabla "partituras". El PDF no se guarda dentro de
 * la base de datos: solo se guarda la ruta donde quedo el archivo
 * en el servidor, para que la tabla se mantenga liviana.
 */
@Entity
@Table(name = "partituras")
public class Partitura {

    /** Clave primaria. La genera la base de datos. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(length = 150)
    private String compositor;

    /** Bateria, guitarra, congas, timbales... */
    @Column(nullable = false, length = 60)
    private String instrumento;

    /** Do mayor, La menor, etc. */
    @Column(length = 40)
    private String tonalidad;

    /** Donde quedo guardado el PDF en el servidor. */
    @Column(name = "ruta_archivo", nullable = false, length = 255)
    private String rutaArchivo;

    /**
     * Indica si el PDF ya se pudo convertir a notas.
     * Si la conversion falla, la partitura igual se guarda con
     * este campo en false y se puede reintentar despues.
     */
    @Column(nullable = false)
    private boolean procesada;

    @Column(name = "fecha_subida", nullable = false)
    private LocalDateTime fechaSubida;

    /**
     * Dueño de la partitura.
     * Muchas partituras pertenecen a un mismo usuario, por eso es
     * ManyToOne. En la tabla se guarda como la columna usuario_id.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public Partitura() {
    }

    public Partitura(String titulo, String instrumento, String rutaArchivo, Usuario usuario) {
        this.titulo = titulo;
        this.instrumento = instrumento;
        this.rutaArchivo = rutaArchivo;
        this.usuario = usuario;
        this.procesada = false;
        this.fechaSubida = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCompositor() {
        return compositor;
    }

    public void setCompositor(String compositor) {
        this.compositor = compositor;
    }

    public String getInstrumento() {
        return instrumento;
    }

    public void setInstrumento(String instrumento) {
        this.instrumento = instrumento;
    }

    public String getTonalidad() {
        return tonalidad;
    }

    public void setTonalidad(String tonalidad) {
        this.tonalidad = tonalidad;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public boolean isProcesada() {
        return procesada;
    }

    public void setProcesada(boolean procesada) {
        this.procesada = procesada;
    }

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
