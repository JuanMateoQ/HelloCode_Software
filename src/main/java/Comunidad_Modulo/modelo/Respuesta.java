package Comunidad_Modulo.modelo;

import Modulo_Usuario.Clases.UsuarioComunidad;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Respuesta {
    private String idRespuesta;
    private String contenido;
    private UsuarioComunidad autor;
    private LocalDateTime fechaPublicacion;
    private Map<String, Integer> votosUsuarios;
    private Boolean esSolucion;

    public Respuesta(String contenido, UsuarioComunidad autor) {
        this.idRespuesta = UUID.randomUUID().toString();
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
        this.votosUsuarios = (votosUsuarios != null) ? votosUsuarios : new HashMap<>();
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
        return votosUsuarios;
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

        if (tipoVoto == 1) {
            if (votosUsuarios.containsKey(username) && votosUsuarios.get(username) == 1) {
                votosUsuarios.remove(username); // Quitar el voto si ya existe
            } else {
                votosUsuarios.put(username, 1);
            }
        } else if (tipoVoto == -1) {
            if (votosUsuarios.containsKey(username) && votosUsuarios.get(username) == -1) {
                votosUsuarios.remove(username); // Quitar el voto si ya existe
            } else {
                votosUsuarios.put(username, -1);
            }
        } else if (tipoVoto == 0) { // Quitar voto
            votosUsuarios.remove(username);
        } else {
            throw new IllegalArgumentException("Tipo de voto inválido. Use 1 para voto positivo, -1 para voto negativo, o 0 para quitar voto.");
        }
    }

    public void marcarComoSolucion() {
        this.esSolucion = true;
        // Otorgar puntos de reputación al autor
        autor.incrementarReputacion(10);
    }

    public void desmarcarComoSolucion() {
        this.esSolucion = false;
        // Quitar puntos de reputación al autor
        autor.decrementarReputacion(10);
    }

    @Override
    public String toString() {
        String solucionMark = esSolucion ? " [SOLUCIÓN]" : "";
        return String.format("Respuesta por %s (\u2B06%d \u2B07%d)%s: %s",
                autor.getNombre(), getVotosPositivos(), getVotosNegativos(), solucionMark, contenido);
    }
}
