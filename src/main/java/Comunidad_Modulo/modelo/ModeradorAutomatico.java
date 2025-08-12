package Comunidad_Modulo.modelo;

import Modulo_Usuario.Clases.UsuarioComunidad;
import Comunidad_Modulo.servicios.ModeracionService.ResultadoModeracion;

/**
 * Moderador Automático - Representa al sistema automático de moderación
 * Responsable de: filtrado de contenido, detección de spam, aplicación automática de reglas
 */
public class ModeradorAutomatico extends ModeradorBase {

    private boolean filtroActivado;
    private int nivelStricto; // 1=básico, 2=medio, 3=estricto

    public ModeradorAutomatico() {
        super("Sistema de Moderación Automática", "sistema-auto");
        this.filtroActivado = true;
        this.nivelStricto = 2; // Nivel medio por defecto
    }

    public ModeradorAutomatico(int nivelStricto) {
        this();
        this.nivelStricto = Math.max(1, Math.min(3, nivelStricto)); // Entre 1 y 3
    }

    // ========== OPERACIONES AUTOMÁTICAS ESPECÍFICAS ==========

    /**
     * Modera un mensaje automáticamente antes de publicarlo
     */
    public ResultadoModeracion moderarMensajeAutomatico(String contenido, UsuarioComunidad autor) {
        if (!filtroActivado) {
            return new ResultadoModeracion(true, "Filtro desactivado", null);
        }

        System.out.println("🤖 Moderación automática de mensaje de: " + autor.getUsername());
        
        ResultadoModeracion resultado = moderacionService.moderarMensaje(contenido, autor, this.nombre);
        
        if (!resultado.isAprobado()) {
            System.out.println("🚫 Mensaje bloqueado automáticamente: " + resultado.getMensaje());
            
            // Aplicar sanción automática si es necesario según el nivel de stricto
            if (nivelStricto >= 2 && debeAplicarSancionAutomatica(resultado.getMensaje())) {
                aplicarSancionAutomatica(autor, resultado.getMensaje());
            }
        }
        
        return resultado;
    }

    /**
     * Aplica una sanción automática basada en reglas del sistema
     */
    public SancionUsuario aplicarSancionAutomatica(UsuarioComunidad usuario, String razon) {
        int duracionMinutos = calcularDuracionSancion(razon);
        
        System.out.println("⚡ Aplicando sanción automática a " + usuario.getUsername() + 
                         " por " + duracionMinutos + " minutos");
        
        return moderacionService.aplicarSancion(usuario, 
                                              "AUTOMÁTICO: " + razon, 
                                              duracionMinutos, 
                                              this.nombre);
    }

    /**
     * Escanea automáticamente todos los mensajes de un foro
     */
    public void escanearForoAutomatico(ForoGeneral foro) {
        System.out.println("🔍 Escaneando foro automáticamente: " + foro.toString());
        
        // Aquí se podría implementar lógica para revisar todos los mensajes del foro
        // y aplicar moderación automática retroactiva
        
        System.out.println("✅ Escaneo automático completado");
    }

    /**
     * Detecta patrones de spam o comportamiento sospechoso
     */
    public boolean detectarSpam(UsuarioComunidad usuario, String contenido) {
        // Implementación básica de detección de spam
        
        // Verificar mensajes repetitivos
        if (contenido.length() < 5) {
            System.out.println("⚠️ Detectado mensaje muy corto de " + usuario.getUsername());
            return true;
        }
        
        // Verificar enlaces sospechosos
        if (contenido.toLowerCase().contains("http") && nivelStricto >= 2) {
            System.out.println("⚠️ Detectado enlace en mensaje de " + usuario.getUsername());
            return true;
        }
        
        // Verificar palabras en mayúsculas excesivas
        long mayusculas = contenido.chars().filter(Character::isUpperCase).count();
        if (mayusculas > contenido.length() * 0.7 && contenido.length() > 10) {
            System.out.println("⚠️ Detectado exceso de mayúsculas de " + usuario.getUsername());
            return true;
        }
        
        return false;
    }

    /**
     * Monitorea automáticamente la actividad de los usuarios
     */
    public void monitorearActividadUsuarios() {
        System.out.println("📊 Monitoreando actividad de usuarios automáticamente...");
        
        for (Comunidad comunidad : comunidadesGestionadas) {
            for (UsuarioComunidad usuario : comunidad.getUsuariosConectados()) {
                if (usuarioEstaSancionado(usuario)) {
                    continue; // Saltar usuarios ya sancionados
                }
                
                // Aquí se podría implementar lógica más compleja de monitoreo
                System.out.println("   ✓ Usuario monitoreado: " + usuario.getUsername());
            }
        }
    }

    // ========== MÉTODOS DE CONFIGURACIÓN ==========

    public void activarFiltro() {
        this.filtroActivado = true;
        System.out.println("🔧 Filtro automático activado");
    }

    public void desactivarFiltro() {
        this.filtroActivado = false;
        System.out.println("🔧 Filtro automático desactivado");
    }

    public void setNivelStricto(int nivel) {
        this.nivelStricto = Math.max(1, Math.min(3, nivel));
        System.out.println("🔧 Nivel de moderación establecido en: " + 
                         (nivel == 1 ? "Básico" : nivel == 2 ? "Medio" : "Estricto"));
    }

    // ========== MÉTODOS DE ESTADÍSTICAS ==========

    public void mostrarEstadisticasAutomaticas() {
        System.out.println("\n🤖 === ESTADÍSTICAS DEL MODERADOR AUTOMÁTICO ===");
        System.out.println("Estado del filtro: " + (filtroActivado ? "Activo" : "Inactivo"));
        System.out.println("Nivel de moderación: " + 
                         (nivelStricto == 1 ? "Básico" : nivelStricto == 2 ? "Medio" : "Estricto"));
        
        var stats = moderacionService.getEstadisticas();
        System.out.println("📊 " + stats.toString());
        
        System.out.println("🌐 Comunidades monitoreadas: " + comunidadesGestionadas.size());
        System.out.println("=".repeat(50));
    }

    // ========== MÉTODOS AUXILIARES PRIVADOS ==========

    private boolean debeAplicarSancionAutomatica(String razon) {
        // Define cuándo el sistema debe aplicar sanciones automáticas
        return razon.toLowerCase().contains("spam") || 
               razon.toLowerCase().contains("ofensivo") ||
               razon.toLowerCase().contains("repetitivo");
    }

    private int calcularDuracionSancion(String razon) {
        // Calcula la duración de la sanción basada en la razón
        if (razon.toLowerCase().contains("spam")) {
            return 30; // 30 minutos por spam
        } else if (razon.toLowerCase().contains("ofensivo")) {
            return 60; // 1 hora por contenido ofensivo
        } else {
            return 15; // 15 minutos por defecto
        }
    }

    // ========== GETTERS ==========

    public boolean isFiltroActivado() {
        return filtroActivado;
    }

    public int getNivelStricto() {
        return nivelStricto;
    }
}
