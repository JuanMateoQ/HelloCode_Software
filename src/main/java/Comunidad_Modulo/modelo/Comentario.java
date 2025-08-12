package Comunidad_Modulo.modelo;

import Modulo_Usuario.Clases.UsuarioComunidad;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Comentario {
    private String idComentario;
    private String contenido;
    private UsuarioComunidad autor;
    private LocalDateTime fechaPublicacion;
    private Map<String, Integer> votosUsuarios;

    // Constructor para crear un nuevo comentario
    public Comentario(String idComentario, String contenido, UsuarioComunidad autor) {
        this.idComentario = idComentario;
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

    // Setter para idComentario
    public void setIdComentario(String idComentario) {
        this.idComentario = idComentario;
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
     * @param usuarioVotante El usuario que vota.
     * @param voto El voto (1 para like, -1 para dislike, 0 para quitar voto).
     */
    public void votar(UsuarioComunidad usuarioVotante, int voto) {
        if (usuarioVotante == null) {
            throw new IllegalArgumentException("El usuario votante no puede ser nulo.");
        }
        String username = usuarioVotante.getUsername();
        int votoAnterior = votosUsuarios.getOrDefault(username, 0);

        if (voto == 1) {
            if (votoAnterior == 1) {
                System.out.println("ℹ️ El usuario " + username + " ya había dado voto positivo a este comentario.");
            } else {
                votosUsuarios.put(username, 1);
                if (votoAnterior == -1) {
                    autor.incrementarReputacion(2);
                } else {
                    autor.incrementarReputacion(1);
                }
                System.out.println("✅ Usuario " + username + " dio voto positivo al comentario.");
            }
        } else if (voto == -1) {
            if (votoAnterior == -1) {
                System.out.println("ℹ️ El usuario " + username + " ya había dado voto negativo a este comentario.");
            } else {
                votosUsuarios.put(username, -1);
                if (votoAnterior == 1) {
                    autor.decrementarReputacion(2);
                } else {
                    autor.decrementarReputacion(1);
                }
                System.out.println("✅ Usuario " + username + " dio voto negativo al comentario.");
            }
        } else if (voto == 0) { // Quitar Voto
            if (votoAnterior == 0) {
                System.out.println("ℹ️ El usuario " + username + " no había votado este comentario.");
            } else {
                votosUsuarios.remove(username);
                if (votoAnterior == 1) {
                    autor.decrementarReputacion(1);
                } else if (votoAnterior == -1) {
                    autor.incrementarReputacion(1);
                }
                System.out.println("✅ Usuario " + username + " quitó su voto del comentario.");
            }
        } else {
            System.out.println("⚠️ Tipo de voto inválido. Use 1 (voto positivo), -1 (voto negativo) o 0 (quitar voto).");
        }
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
