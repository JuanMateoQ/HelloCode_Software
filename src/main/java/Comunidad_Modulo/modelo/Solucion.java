package Comunidad_Modulo.modelo;

import Modulo_Usuario.Clases.UsuarioComunidad;
import Comunidad_Modulo.enums.TipoSolucion;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Solucion {
    private String idSolucion;
    private String titulo;
    private String contenido;
    private UsuarioComunidad autor;
    private TipoSolucion tipoSolucion;
    private String archivo;
    private LocalDateTime fechaPublicacion;
    private Map<String, Integer> votosUsuarios;
    private List<Comentario> comentarios;

    public Solucion(String titulo, String contenido, UsuarioComunidad autor, TipoSolucion tipoSolucion) {
        this.idSolucion = UUID.randomUUID().toString();
        this.titulo = titulo;
        this.contenido = contenido;
        this.autor = autor;
        this.tipoSolucion = tipoSolucion;
        this.fechaPublicacion = LocalDateTime.now();
        this.votosUsuarios = new HashMap<>();
        this.comentarios = new ArrayList<>();
    }

    // Constructor para cargar desde persistencia
    public Solucion(String idSolucion, String titulo, String contenido, UsuarioComunidad autor, TipoSolucion tipoSolucion, String archivo, LocalDateTime fechaPublicacion, Map<String, Integer> votosUsuarios, List<Comentario> comentarios) {
        this.idSolucion = idSolucion;
        this.titulo = titulo;
        this.contenido = contenido;
        this.autor = autor;
        this.tipoSolucion = tipoSolucion;
        this.archivo = archivo;
        this.fechaPublicacion = fechaPublicacion;
        this.votosUsuarios = (votosUsuarios != null) ? new HashMap<>(votosUsuarios) : new HashMap<>();
        this.comentarios = (comentarios != null) ? new ArrayList<>(comentarios) : new ArrayList<>();
    }

    // Getters y setters
    public String getIdSolucion() {
        return idSolucion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
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

    public TipoSolucion getTipoSolucion() {
        return tipoSolucion;
    }

    public void setTipoSolucion(TipoSolucion tipoSolucion) {
        this.tipoSolucion = tipoSolucion;
    }

    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
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

    public List<Comentario> getComentarios() {
        return new ArrayList<>(comentarios);
    }

    // Métodos de negocio

    /**
     * Permite a un usuario votar (like o dislike) una solución.
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

    public void comentar(String contenido, UsuarioComunidad autor) {
        Comentario comentario = new Comentario(contenido, autor);
        comentarios.add(comentario);
    }

    public void eliminarComentario(String idComentario) {
        comentarios.removeIf(c -> c.getIdComentario().equals(idComentario));
    }

    @Override
    public String toString() {
        return String.format("Solución: %s por %s (👍%d 👎%d, %d comentarios)",
                titulo, autor.getNombre(), getLikes(), getDislikes(), comentarios.size());
    }
}
