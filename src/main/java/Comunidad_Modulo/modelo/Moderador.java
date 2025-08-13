package Comunidad_Modulo.modelo;

import Modulo_Usuario.Clases.UsuarioComunidad;
import Comunidad_Modulo.servicios.ModeracionService;
import Comunidad_Modulo.servicios.ModeracionService.ResultadoModeracion;

import java.util.List;

/**
 * CLASE LEGACY - DEPRECATED
 * Esta clase se mantiene solo para compatibilidad con código existente.
 * Se recomienda usar ModeradorManual o ModeradorAutomatico según el caso.
 * 
 * @deprecated Usar {@link ModeradorManual} o {@link ModeradorAutomatico}
 */
@Deprecated
public class Moderador extends ModeradorBase {

    public Moderador(String nombre, String username) {
        super(nombre, username);
        System.out.println("⚠️ AVISO: Usando clase Moderador deprecated. Considere usar ModeradorManual o ModeradorAutomatico.");
    }

    public Moderador(String nombre) {
        super(nombre, "mod");  // usa username por defecto
    }

    // === MÉTODOS LEGACY MANTENIDOS PARA COMPATIBILIDAD ===

    public void cerrarHilo(HiloDiscusion hilo) {
        hilo.cerrar();
        System.out.println("Hilo cerrado por moderador: " + hilo.getTitulo());
    }

    public void eliminarMensaje(ChatPrivado chat, String idMensaje) {
        // Lógica para eliminar mensaje (simulada)
        System.out.println("Mensaje eliminado por moderador en chat: " + chat.getIdChat());
    }

    public void suspenderUsuario(UsuarioComunidad usuario) {
        // Lógica para suspender usuario (simulada)
        System.out.println("Usuario suspendido: " + usuario.getNombre());
    }

    /**
     * Modera un mensaje automáticamente antes de publicarlo
     * @deprecated Usar ModeradorAutomatico.moderarMensajeAutomatico()
     */
    @Deprecated
    public ResultadoModeracion moderarMensaje(String contenido, UsuarioComunidad autor) {
        System.out.println("⚠️ Método deprecated. Use ModeradorAutomatico para moderación automática.");
        return moderacionService.moderarMensaje(contenido, autor, this.nombre);
    }

    /**
     * Aplica una sanción manual a un usuario
     * @deprecated Usar ModeradorManual.aplicarSancionManual()
     */
    @Deprecated
    public SancionUsuario aplicarSancionManual(UsuarioComunidad usuario, String razon, int duracionMinutos) {
        System.out.println("⚠️ Método deprecated. Use ModeradorManual para sanciones manuales.");
        return moderacionService.aplicarSancion(usuario, razon, duracionMinutos, this.nombre);
    }

    /**
     * Levanta una sanción de un usuario
     * @deprecated Usar ModeradorManual.levantarSancion()
     */
    @Deprecated
    public boolean levantarSancion(UsuarioComunidad usuario) {
        System.out.println("⚠️ Método deprecated. Use ModeradorManual para gestión de sanciones.");
        return moderacionService.levantarSancion(usuario, this.nombre);
    }

    /**
     * Obtiene estadísticas de moderación
     * @deprecated Usar ModeradorManual.mostrarEstadisticasAccionesManual() o ModeradorAutomatico.mostrarEstadisticasAutomaticas()
     */
    @Deprecated
    public ModeracionService.EstadisticasModeración getEstadisticasModeración() {
        return moderacionService.getEstadisticas();
    }

    /**
     * Método auxiliar para mostrar el estado de moderación
     * @deprecated Usar ModeradorFactory.mostrarEstadoModeración()
     */
    @Deprecated
    public void mostrarEstadoModeración() {
        System.out.println("\n🛡️ === ESTADO DE MODERACIÓN (LEGACY) ===");
        System.out.println("⚠️ AVISO: Use ModeradorFactory.mostrarEstadoModeración() para información completa.");
        System.out.println("Moderador: " + this.nombre);

        ModeracionService.EstadisticasModeración stats = getEstadisticasModeración();
        System.out.println(stats.toString());

        List<SancionUsuario> sancionesActivas = getSancionesActivas();
        if (!sancionesActivas.isEmpty()) {
            System.out.println("\n🚫 Sanciones activas:");
            for (SancionUsuario sancion : sancionesActivas) {
                System.out.println("  - " + sancion.toString());
            }
        } else {
            System.out.println("\n✅ No hay sanciones activas");
        }
        System.out.println("=".repeat(35));
    }

    /**
     * Getter para el servicio de moderación (por si se necesita acceso directo)
     * @deprecated Acceder a través de ModeradorBase.getModeracionService()
     */
    @Deprecated
    public ModeracionService getModeracionService() {
        return moderacionService;
    }

    @Override
    public String toString() {
        return String.format("Moderador LEGACY: %s (gestiona %d comunidades) - ⚠️ DEPRECATED",
                           nombre, comunidadesGestionadas.size());
    }
}