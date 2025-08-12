package Comunidad_Modulo.modelo;

import Modulo_Usuario.Clases.UsuarioComunidad;
import java.util.List;

/**
 * Interfaz que define las operaciones básicas de moderación
 * Implementada por diferentes tipos de moderadores
 */
public interface IModerador {
    
    // Información básica
    String getIdModerador();
    String getNombre();
    String getUsername();
    
    // Gestión de comunidades
    List<Comunidad> getComunidadesGestionadas();
    void asignarComunidad(Comunidad comunidad);
    void removerComunidad(Comunidad comunidad);
    
    // Operaciones de moderación básicas
    void moderarForo(ForoGeneral foro);
    void supervisarChats(List<ChatPrivado> chats);
    
    // Verificación de sanciones
    boolean usuarioEstaSancionado(UsuarioComunidad usuario);
    SancionUsuario getSancionActiva(UsuarioComunidad usuario);
    List<SancionUsuario> getSancionesActivas();
    List<SancionUsuario> getHistorialSanciones(UsuarioComunidad usuario);
}
