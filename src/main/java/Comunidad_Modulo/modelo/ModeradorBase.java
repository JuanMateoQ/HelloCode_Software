package Comunidad_Modulo.modelo;

import Modulo_Usuario.Clases.UsuarioComunidad;
import Comunidad_Modulo.servicios.ModeracionService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Clase base abstracta para todos los tipos de moderadores
 * Contiene la funcionalidad común compartida entre moderadores manuales y automáticos
 */
public abstract class ModeradorBase implements IModerador {
    
    protected String idModerador;
    protected String nombre;
    protected String username;
    protected List<Comunidad> comunidadesGestionadas;
    protected ModeracionService moderacionService;

    // Constructor que usa singleton
    public ModeradorBase(String nombre, String username) {
        this.idModerador = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.username = username;
        this.comunidadesGestionadas = new ArrayList<>();
        this.moderacionService = ModeracionService.getInstance(); // singleton
    }

    // Implementación de métodos de la interfaz IModerador
    @Override
    public String getIdModerador() {
        return idModerador;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public List<Comunidad> getComunidadesGestionadas() {
        return new ArrayList<>(comunidadesGestionadas);
    }

    @Override
    public void asignarComunidad(Comunidad comunidad) {
        if (!comunidadesGestionadas.contains(comunidad)) {
            comunidadesGestionadas.add(comunidad);
        }
    }

    @Override
    public void removerComunidad(Comunidad comunidad) {
        comunidadesGestionadas.remove(comunidad);
    }

    @Override
    public void moderarForo(ForoGeneral foro) {
        System.out.println("Moderando foro: " + foro.toString());
    }

    @Override
    public void supervisarChats(List<ChatPrivado> chats) {
        System.out.println("Supervisando " + chats.size() + " chats privados");
    }

    // Métodos de consulta de sanciones (comunes a todos los moderadores)
    @Override
    public boolean usuarioEstaSancionado(UsuarioComunidad usuario) {
        return moderacionService.usuarioEstaSancionado(usuario);
    }

    @Override
    public SancionUsuario getSancionActiva(UsuarioComunidad usuario) {
        return moderacionService.obtenerSancionActiva(usuario);
    }

    @Override
    public List<SancionUsuario> getSancionesActivas() {
        return moderacionService.getSancionesActivas();
    }

    @Override
    public List<SancionUsuario> getHistorialSanciones(UsuarioComunidad usuario) {
        return moderacionService.getHistorialSanciones(usuario);
    }

    // Métodos auxiliares protegidos para las subclases
    protected ModeracionService getModeracionService() {
        return moderacionService;
    }

    protected void setNombre(String nombre) {
        this.nombre = nombre;
    }

    protected void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return String.format("%s: %s (gestiona %d comunidades)",
                           getClass().getSimpleName(), nombre, comunidadesGestionadas.size());
    }
}
