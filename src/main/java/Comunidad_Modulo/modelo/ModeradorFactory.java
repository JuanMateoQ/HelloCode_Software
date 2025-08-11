package Comunidad_Modulo.modelo;

import Comunidad_Modulo.controladores.ContextoSistema;

/**
 * Factory para crear diferentes tipos de moderadores
 * Facilita la creación y configuración de moderadores según las necesidades
 */
public class ModeradorFactory {
    
    private static ModeradorFactory instancia;
    private ContextoSistema contexto;
    
    private ModeradorFactory() {
        this.contexto = ContextoSistema.getInstance();
    }
    
    public static ModeradorFactory getInstance() {
        if (instancia == null) {
            instancia = new ModeradorFactory();
        }
        return instancia;
    }
    
    /**
     * Crea un moderador manual con las comunidades existentes asignadas
     */
    public ModeradorManual crearModeradorManual(String nombre, String username) {
        ModeradorManual moderador = new ModeradorManual(nombre, username);
        
        // Asignar todas las comunidades existentes
        for (Comunidad comunidad : contexto.getComunidades()) {
            moderador.asignarComunidad(comunidad);
        }
        
        // Agregar al contexto
        contexto.agregarModerador(moderador);
        
        System.out.println("✅ Moderador manual creado: " + nombre + " (" + username + ")");
        return moderador;
    }
    
    /**
     * Crea un moderador automático con configuración personalizada
     */
    public ModeradorAutomatico crearModeradorAutomatico(int nivelStricto) {
        ModeradorAutomatico moderador = new ModeradorAutomatico(nivelStricto);
        
        // Asignar todas las comunidades existentes
        for (Comunidad comunidad : contexto.getComunidades()) {
            moderador.asignarComunidad(comunidad);
        }
        
        // Agregar al contexto
        contexto.agregarModerador(moderador);
        
        System.out.println("✅ Moderador automático creado con nivel: " + 
                         (nivelStricto == 1 ? "Básico" : nivelStricto == 2 ? "Medio" : "Estricto"));
        return moderador;
    }
    
    /**
     * Crea un moderador automático con configuración por defecto
     */
    public ModeradorAutomatico crearModeradorAutomatico() {
        return crearModeradorAutomatico(2); // Nivel medio por defecto
    }
    
    /**
     * Crea un moderador principal que combina funcionalidades
     */
    public ModeradorManual crearModeradorPrincipal(String nombre) {
        return crearModeradorManual(nombre, "mod-principal");
    }
    
    /**
     * Obtiene o crea el moderador automático del sistema
     */
    public ModeradorAutomatico obtenerModeradorAutomaticoSistema() {
        // Buscar si ya existe un moderador automático
        for (IModerador moderador : contexto.getModeradores()) {
            if (moderador instanceof ModeradorAutomatico) {
                return (ModeradorAutomatico) moderador;
            }
        }
        
        // Si no existe, crear uno nuevo
        return crearModeradorAutomatico();
    }
    
    /**
     * Crea moderadores para una nueva comunidad
     */
    public void configurarModeradoresParaComunidad(Comunidad comunidad) {
        System.out.println("🔧 Configurando moderadores para la comunidad: " + comunidad.getNombre());
        
        // Asignar la nueva comunidad a todos los moderadores existentes
        for (IModerador moderador : contexto.getModeradores()) {
            moderador.asignarComunidad(comunidad);
            System.out.println("   ✓ " + moderador.getNombre() + " ahora gestiona la comunidad");
        }
        
        // Si no hay moderadores, crear uno automático por defecto
        if (contexto.getModeradores().isEmpty()) {
            ModeradorAutomatico modAuto = crearModeradorAutomatico();
            modAuto.asignarComunidad(comunidad);
            System.out.println("   ✓ Moderador automático creado automáticamente para la nueva comunidad");
        }
    }
    
    /**
     * Muestra información de todos los moderadores existentes
     */
    public void mostrarEstadoModeración() {
        System.out.println("\n🛡️ === ESTADO GENERAL DE MODERACIÓN ===");
        System.out.println("Total de moderadores: " + contexto.getModeradores().size());
        
        for (IModerador moderador : contexto.getModeradores()) {
            System.out.println("\n" + moderador.toString());
            System.out.println("   Sanciones activas: " + moderador.getSancionesActivas().size());
            
            if (moderador instanceof ModeradorManual) {
                System.out.println("   Tipo: Moderador Manual");
            } else if (moderador instanceof ModeradorAutomatico) {
                ModeradorAutomatico modAuto = (ModeradorAutomatico) moderador;
                System.out.println("   Tipo: Moderador Automático");
                System.out.println("   Filtro: " + (modAuto.isFiltroActivado() ? "Activo" : "Inactivo"));
                System.out.println("   Nivel: " + modAuto.getNivelStricto());
            }
        }
        System.out.println("=".repeat(40));
    }
}
