package Modulo_Usuario.Clases;

import Conexion.SesionManager;

public class TestUsuarioVidas {
    public static void main(String[] args) {
        System.out.println("=== TEST DE VIDAS USUARIO ===");
        
        // Simular carga del usuario tarek desde archivo
        String lineaTarek = "tarek;tarek;tarek;tarek@god.com;0;3;USUARIO";
        System.out.println("📄 Línea del archivo: " + lineaTarek);
        
        // Crear usuario desde la línea
        Usuario tarek = Usuario.fromString(lineaTarek);
        System.out.println("✅ Usuario creado desde archivo:");
        System.out.println("   Username: " + tarek.getUsername());
        System.out.println("   Vidas: " + tarek.getVidas());
        System.out.println("   XP: " + tarek.getXp());
        
        // Probar tieneVidas()
        System.out.println("\n🔍 Probando tieneVidas():");
        boolean tieneVidas = tarek.tieneVidas();
        System.out.println("   Resultado: " + tieneVidas);
        
        // Probar getVidasSincronizadas()
        System.out.println("\n🔄 Probando getVidasSincronizadas():");
        int vidasSincronizadas = tarek.getVidasSincronizadas();
        System.out.println("   Vidas sincronizadas: " + vidasSincronizadas);
        
        // Simular inicio de sesión
        System.out.println("\n🚀 Simulando inicio de sesión:");
        SesionManager.getInstancia().iniciarSesion(tarek);
        Usuario usuarioSesion = SesionManager.getInstancia().getUsuarioAutenticado();
        System.out.println("   Usuario en sesión: " + usuarioSesion.getUsername());
        System.out.println("   Vidas en sesión: " + usuarioSesion.getVidas());
        System.out.println("   TieneVidas en sesión: " + usuarioSesion.tieneVidas());
        
        System.out.println("\n=== FIN DEL TEST ===");
    }
}
