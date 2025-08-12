package Comunidad_Modulo.modelo;

import Modulo_Usuario.Clases.UsuarioComunidad;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Respuesta {
    private String idRespuesta;
    private String contenido;
    private UsuarioComunidad autor;
    private LocalDateTime fechaPublicacion;
    private Map<String, Integer> votosUsuarios;
    private Boolean esSolucion;

    // Constructor para crear una nueva respuesta (el ID se asignará desde PersistenciaService)
    public Respuesta(String idRespuesta, String contenido, UsuarioComunidad autor) {
        this.idRespuesta = idRespuesta;
        this.contenido = contenido;
        this.autor = autor;
        this.fechaPublicacion = LocalDateTime.now();
        this.votosUsuarios = new HashMap<>(); // Inicializar el mapa de votos
        this.esSolucion = false;
    }

    // Constructor adicional para cargar desde persistencia
    public Respuesta(String idRespuesta, String contenido, UsuarioComunidad autor, LocalDateTime fechaPublicacion, Map<String, Integer> votosUsuarios, Boolean esSolucion) {
        this.idRespuesta = idRespuesta;
        this.contenido = contenido;
        this.autor = autor;
        this.fechaPublicacion = fechaPublicacion;
        this.votosUsuarios = (votosUsuarios != null) ? new HashMap<>(votosUsuarios) : new HashMap<>(); // Asegurar que no sea null
        this.esSolucion = (esSolucion != null) ? esSolucion : false;
    }

    // Getters y setters
    public String getIdRespuesta() {
        return idRespuesta;
    }

    // Setter para idRespuesta
    public void setIdRespuesta(String idRespuesta) {
        this.idRespuesta = idRespuesta;
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

    // Nuevo getter para el mapa de votos
    public Map<String, Integer> getVotosUsuarios() {
        return new HashMap<>(votosUsuarios);
    }

    // Métodos para obtener el total de votos positivos y negativos
    public int getVotosPositivos() {
        return (int) votosUsuarios.values().stream().filter(v -> v == 1).count();
    }

    public int getVotosNegativos() {
        return (int) votosUsuarios.values().stream().filter(v -> v == -1).count();
    }

    public Boolean getEsSolucion() {
        return esSolucion;
    }

    // Métodos de negocio
    /**
     * Permite a un usuario votar por la respuesta.
     * @param usuarioVotante El usuario que emite el voto.
     * @param tipoVoto 1 para positivo, -1 para negativo, 0 para quitar voto.
     */
    public void votar(UsuarioComunidad usuarioVotante, int tipoVoto) {
        if (usuarioVotante == null) {
            throw new IllegalArgumentException("El usuario votante no puede ser nulo.");
        }
        String username = usuarioVotante.getUsername();
        int votoAnterior = votosUsuarios.getOrDefault(username, 0);

        if (tipoVoto == 1) { // Voto Positivo
            if (votoAnterior == 1) {
                System.out.println("ℹ️ El usuario " + username + " ya había dado voto positivo a esta respuesta.");
            } else {
                votosUsuarios.put(username, 1);
                if (votoAnterior == -1) { // Cambió de dislike a like
                    autor.incrementarReputacion(2); // Recupera el punto perdido y gana uno nuevo
                } else { // Voto nuevo o cambió de no votar a like
                    autor.incrementarReputacion(1);
                }
                System.out.println("✅ Usuario " + username + " dio voto positivo a la respuesta.");
            }
        } else if (tipoVoto == -1) { // Voto Negativo
            if (votoAnterior == -1) {
                System.out.println("ℹ️ El usuario " + username + " ya había dado voto negativo a esta respuesta.");
            } else {
                votosUsuarios.put(username, -1);
                if (votoAnterior == 1) { // Cambió de like a dislike
                    autor.decrementarReputacion(2); // Pierde el punto ganado y uno nuevo
                } else { // Voto nuevo o cambió de no votar a dislike
                    autor.decrementarReputacion(1);
                }
                System.out.println("✅ Usuario " + username + " dio voto negativo a la respuesta.");
            }
        } else if (tipoVoto == 0) { // Quitar Voto
            if (votoAnterior == 0) {
                System.out.println("ℹ️ El usuario " + username + " no había votado esta respuesta.");
            } else {
                votosUsuarios.remove(username);
                if (votoAnterior == 1) { // Quita voto positivo
                    autor.decrementarReputacion(1);
                } else if (votoAnterior == -1) { // Quita voto negativo
                    autor.incrementarReputacion(1);
                }
                System.out.println("✅ Usuario " + username + " quitó su voto de la respuesta.");
            }
        } else {
            throw new IllegalArgumentException("Tipo de voto inválido. Use 1 (voto positivo), -1 (voto negativo) o 0 (quitar voto).");
        }
    }

    public void marcarComoSolucion() {
        if (!this.esSolucion) {
            this.esSolucion = true;
            // Otorgar puntos de reputación al autor
            autor.incrementarReputacion(10);
        }
    }

    public void desmarcarComoSolucion() {
        if (this.esSolucion) {
            this.esSolucion = false;
            // Quitar puntos de reputación al autor
            autor.decrementarReputacion(10);
        }
    }

    @Override
    public String toString() {
        String solucionMark = esSolucion ? " [SOLUCIÓN]" : "";
        return String.format("Respuesta por %s (\u2B06%d \u2B07%d)%s: %s",
                autor.getNombre(), getVotosPositivos(), getVotosNegativos(), solucionMark, contenido);
    }
}
