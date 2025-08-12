package Comunidad_Modulo.modelo;

import Modulo_Usuario.Clases.UsuarioComunidad;

/**
 * Moderador Manual - Representa a un moderador humano que realiza acciones directas
 * Responsable de: expulsiones, eliminaciones de mensajes, suspensiones manuales, etc.
 */
public class ModeradorManual extends ModeradorBase {

    public ModeradorManual(String nombre, String username) {
        super(nombre, username);
    }

    public ModeradorManual(String nombre) {
        super(nombre);
    }

    // ========== OPERACIONES MANUALES ESPECÍFICAS ==========

    /**
     * Cierra un hilo de discusión manualmente
     */
    public void cerrarHilo(HiloDiscusion hilo) {
        hilo.cerrar();
        System.out.println("🔒 Hilo cerrado manualmente por moderador " + nombre + ": " + hilo.getTitulo());
    }

    /**
     * Elimina un mensaje de un chat privado
     */
    public void eliminarMensaje(ChatPrivado chat, String idMensaje) {
        // Lógica para eliminar mensaje (simulada)
        System.out.println("🗑️ Mensaje eliminado manualmente por moderador " + nombre + " en chat: " + chat.getIdChat());
    }

    /**
     * Suspende un usuario de forma manual
     */
    public void suspenderUsuario(UsuarioComunidad usuario) {
        System.out.println("⛔ Usuario suspendido manualmente por " + nombre + ": " + usuario.getNombre());
    }

    /**
     * Aplica una sanción manual a un usuario
     */
    public SancionUsuario aplicarSancionManual(UsuarioComunidad usuario, String razon, int duracionMinutos) {
        System.out.println("⚖️ Aplicando sanción manual por " + nombre + " a " + usuario.getUsername());
        return moderacionService.aplicarSancion(usuario, razon, duracionMinutos, this.nombre);
    }

    /**
     * Levanta una sanción de un usuario de forma manual
     */
    public boolean levantarSancion(UsuarioComunidad usuario) {
        System.out.println("🔓 Levantando sanción manual por " + nombre + " para " + usuario.getUsername());
        return moderacionService.levantarSancion(usuario, this.nombre);
    }

    /**
     * Expulsa a un usuario de una comunidad específica
     */
    public void expulsarUsuarioDeComunidad(UsuarioComunidad usuario, Comunidad comunidad) {
        // Remover el usuario de la comunidad
        comunidad.getUsuariosConectados().removeIf(u -> 
            u.getUsername().equalsIgnoreCase(usuario.getUsername()));
        comunidad.getUsuariosMiembros().remove(usuario);
        
        System.out.println("👥❌ Usuario " + usuario.getUsername() + 
                         " expulsado de la comunidad " + comunidad.getNombre() + 
                         " por el moderador " + nombre);
    }

    /**
     * Elimina una comunidad completa
     */
    public void eliminarComunidad(Comunidad comunidad) {
        // Desconectar a todos los usuarios
        for (UsuarioComunidad usuario : comunidad.getUsuariosConectados()) {
            System.out.println("🔌 Desconectando usuario: " + usuario.getUsername());
        }
        
        System.out.println("🌐❌ Comunidad " + comunidad.getNombre() + 
                         " eliminada por el moderador " + nombre);
    }

    /**
     * Obtiene estadísticas de las acciones manuales realizadas
     */
    public void mostrarEstadisticasAccionesManual() {
        System.out.println("\n👨‍💼 === ESTADÍSTICAS DEL MODERADOR MANUAL ===");
        System.out.println("Moderador: " + this.nombre + " (" + this.username + ")");
        System.out.println("Comunidades gestionadas: " + comunidadesGestionadas.size());
        
        // Mostrar estadísticas del servicio de moderación
        var stats = moderacionService.getEstadisticas();
        System.out.println("📊 " + stats.toString());
        
        // Mostrar sanciones activas aplicadas manualmente
        var sancionesActivas = getSancionesActivas();
        System.out.println("🚫 Sanciones activas aplicadas: " + sancionesActivas.size());
        
        System.out.println("=".repeat(45));
    }

    /**
     * Revisa manualmente el contenido reportado
     */
    public void revisarContenidoReportado(String contenido, UsuarioComunidad reportante, String razon) {
        System.out.println("🔍 Revisión manual de contenido reportado:");
        System.out.println("   Reportado por: " + reportante.getUsername());
        System.out.println("   Razón: " + razon);
        System.out.println("   Contenido: " + contenido.substring(0, Math.min(contenido.length(), 50)) + "...");
        System.out.println("   Moderador revisor: " + nombre);
    }
}
