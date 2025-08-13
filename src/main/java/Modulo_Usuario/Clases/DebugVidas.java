package Modulo_Usuario.Clases;

import Conexion.SesionManager;

public class DebugVidas {
    public static void main(String[] args) {
        System.out.println("=== DEBUG PROBLEMA VIDAS ===");
        
        // Test 1: Cargar usuario desde archivo
        String lineaTarek = "tarek;tarek;tarek;tarek@god.com;0;3;USUARIO";
        Usuario tarek = Usuario.fromString(lineaTarek);
        System.out.println("1. Usuario cargado desde línea:");
        System.out.println("   Username: " + tarek.getUsername());
        System.out.println("   Vidas: " + tarek.getVidas());
        System.out.println("   TieneVidas: " + tarek.tieneVidas());
        
        // Test 2: Iniciar sesión
        System.out.println("\n2. Iniciando sesión...");
        SesionManager.getInstancia().iniciarSesion(tarek);
        
        // Test 3: Obtener usuario de sesión
        System.out.println("\n3. Usuario desde sesión:");
        Usuario usuarioSesion = SesionManager.getInstancia().getUsuarioAutenticado();
        if (usuarioSesion != null) {
            System.out.println("   Username: " + usuarioSesion.getUsername());
            System.out.println("   Vidas: " + usuarioSesion.getVidas());
            System.out.println("   TieneVidas: " + usuarioSesion.tieneVidas());
            System.out.println("   Condición > 0: " + (usuarioSesion.getVidas() > 0));
            
            // Test 4: Sincronizar y verificar
            System.out.println("\n4. Después de sincronizar:");
            usuarioSesion.sincronizarVidasDesdeArchivo();
            System.out.println("   Vidas: " + usuarioSesion.getVidas());
            System.out.println("   TieneVidas: " + usuarioSesion.tieneVidas());
            System.out.println("   Condición > 0: " + (usuarioSesion.getVidas() > 0));
        } else {
            System.out.println("   ERROR: Usuario de sesión es null!");
        }
    }
}
