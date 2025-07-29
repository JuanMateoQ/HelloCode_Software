package Comunidad_Modulo.modelo;

import Modulo_Usuario.Clases.UsuarioComunidad;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Comentario {
    private String idComentario;
    private String contenido;
    private UsuarioComunidad autor;
    private LocalDateTime fechaPublicacion;
    private Map<String, Integer> votosUsuarios;

    public Comentario(String contenido, UsuarioComunidad autor) {
        this.idComentario = UUID.randomUUID().toString();
        this.contenido = contenido;
        this.autor = autor;
        this.fechaPublicacion = LocalDateTime.now();
        this.votosUsuarios = new HashMap<>();
    }

    // Constructor para cargar desde persistencia
    public Comentario(String idComentario, String contenido, UsuarioComunidad autor, LocalDateTime fechaPublicacion, Map<String, Integer> votosUsuarios) {
        this.idComentario = idComentario;
        this.contenido = contenido;
        this.autor = autor;
        this.fechaPublicacion = fechaPublicacion;
        this.votosUsuarios = (votosUsuarios != null) ? new HashMap<>(votosUsuarios) : new HashMap<>();
    }

    // Getters y setters
    public String getIdComentario() {
        return idComentario;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public UsuarioComunidad getAutor() {
        return autor;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public Map<String, Integer> getVotosUsuarios() {
        return new HashMap<>(votosUsuarios);
    }

    /**
     * Calcula el número total de likes.
     * @return Número de likes.
     */
    public int getLikes() {
        return (int) votosUsuarios.values().stream().filter(voto -> voto == 1).count();
    }

    /**
     * Calcula el número total de dislikes.
     * @return Número de dislikes.
     */
    public int getDislikes() {
        return (int) votosUsuarios.values().stream().filter(voto -> voto == -1).count();
    }

    // Métodos de negocio

    /**
     * Permite a un usuario votar (like o dislike) un comentario.
     * Si el usuario ya votó, su voto se actualiza. Si el voto es 0, se quita el voto.
     * @param usuario El usuario que vota.
     * @param voto El voto (1 para like, -1 para dislike, 0 para quitar voto).
     */
    public void votar(UsuarioComunidad usuario, int voto) {
        if (voto == 1 || voto == -1) {
            votosUsuarios.put(usuario.getUsername(), voto);
        } else if (voto == 0) {
            votosUsuarios.remove(usuario.getUsername());
        } else {
            throw new IllegalArgumentException("El voto debe ser 1 (like), -1 (dislike) o 0 (quitar voto).");
        }
    }

    public void editar(String nuevoContenido) {
        this.contenido = nuevoContenido;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s (👍%d 👎%d)",
                fechaPublicacion.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                autor.getNombre(),
                contenido,
                getLikes(),
                getDislikes());
    }
}
