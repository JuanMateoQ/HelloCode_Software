package GestionAprendizaje_Modulo.Controladores;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para manejar la configuración de usuarios (lenguaje, nivel, primera vez)
 * Este archivo es independiente del progreso de lecciones
 */
public class ConfiguracionUsuarioService {
    
    private static final String ARCHIVO_CONFIGURACION = "/GestionAprendizaje_Modulo/data/configuracion_usuarios.txt";
    private static ConfiguracionUsuarioService instancia;
    
    public static ConfiguracionUsuarioService getInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracionUsuarioService();
        }
        return instancia;
    }
    
    /**
     * Clase interna para representar la configuración de un usuario
     */
    public static class ConfiguracionUsuario {
        private String username;
        private String lenguaje;
        private String nivel;
        private boolean primeraVez;
        
        public ConfiguracionUsuario(String username, String lenguaje, String nivel, boolean primeraVez) {
            this.username = username;
            this.lenguaje = lenguaje;
            this.nivel = nivel;
            this.primeraVez = primeraVez;
        }
        
        // Getters
        public String getUsername() { return username; }
        public String getLenguaje() { return lenguaje; }
        public String getNivel() { return nivel; }
        public boolean isPrimeraVez() { return primeraVez; }
        
        // Setters
        public void setPrimeraVez(boolean primeraVez) { this.primeraVez = primeraVez; }
    }
    
    /**
     * Verifica si un usuario ya tiene configuración guardada
     */
    public boolean usuarioTieneConfiguracion(String username) {
        try {
            List<ConfiguracionUsuario> configuraciones = leerConfiguraciones();
            return configuraciones.stream().anyMatch(config -> config.getUsername().equals(username));
        } catch (Exception e) {
            System.err.println("Error al verificar configuración del usuario: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifica si es la primera vez que el usuario inicia sesión
     */
    public boolean esPrimeraVez(String username) {
        try {
            List<ConfiguracionUsuario> configuraciones = leerConfiguraciones();
            for (ConfiguracionUsuario config : configuraciones) {
                if (config.getUsername().equals(username)) {
                    return config.isPrimeraVez();
                }
            }
            return true; // Si no existe, es primera vez
        } catch (Exception e) {
            System.err.println("Error al verificar primera vez del usuario: " + e.getMessage());
            return true;
        }
    }
    
    /**
     * Obtiene la configuración de un usuario específico
     */
    public ConfiguracionUsuario obtenerConfiguracion(String username) {
        try {
            List<ConfiguracionUsuario> configuraciones = leerConfiguraciones();
            for (ConfiguracionUsuario config : configuraciones) {
                if (config.getUsername().equals(username)) {
                    return config;
                }
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error al obtener configuración del usuario: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Guarda la configuración inicial de un usuario después del diagnóstico
     */
    public void guardarConfiguracion(String username, String lenguaje, String nivel) {
        try {
            List<ConfiguracionUsuario> configuraciones = leerConfiguraciones();
            
            // Buscar si ya existe configuración para este usuario
            boolean encontrado = false;
            for (ConfiguracionUsuario config : configuraciones) {
                if (config.getUsername().equals(username)) {
                    // Actualizar configuración existente
                    configuraciones.remove(config);
                    configuraciones.add(new ConfiguracionUsuario(username, lenguaje, nivel, false));
                    encontrado = true;
                    break;
                }
            }
            
            // Si no existe, crear nueva configuración
            if (!encontrado) {
                configuraciones.add(new ConfiguracionUsuario(username, lenguaje, nivel, false));
            }
            
            escribirConfiguraciones(configuraciones);
            System.out.println("Configuración guardada para usuario: " + username + " - " + lenguaje + " - " + nivel);
            
        } catch (Exception e) {
            System.err.println("Error al guardar configuración del usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Marca a un usuario como recién registrado (primera vez = true)
     */
    public void marcarUsuarioNuevo(String username) {
        try {
            List<ConfiguracionUsuario> configuraciones = leerConfiguraciones();
            
            // Verificar si ya existe
            boolean existe = configuraciones.stream().anyMatch(config -> config.getUsername().equals(username));
            
            if (!existe) {
                // Agregar como nuevo usuario sin configuración
                configuraciones.add(new ConfiguracionUsuario(username, "", "", true));
                escribirConfiguraciones(configuraciones);
                System.out.println("Usuario marcado como nuevo: " + username);
            }
        } catch (Exception e) {
            System.err.println("Error al marcar usuario como nuevo: " + e.getMessage());
        }
    }
    
    /**
     * Lee todas las configuraciones del archivo
     */
    private List<ConfiguracionUsuario> leerConfiguraciones() throws IOException {
        List<ConfiguracionUsuario> configuraciones = new ArrayList<>();
        
        try (InputStream inputStream = getClass().getResourceAsStream(ARCHIVO_CONFIGURACION);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            
            if (inputStream == null) {
                System.out.println("Archivo de configuración no encontrado, creando uno nuevo...");
                return configuraciones;
            }
            
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (!linea.trim().isEmpty() && !linea.startsWith("#")) {
                    String[] partes = linea.split(";");
                    if (partes.length == 4) {
                        String username = partes[0].trim();
                        String lenguaje = partes[1].trim();
                        String nivel = partes[2].trim();
                        boolean primeraVez = Boolean.parseBoolean(partes[3].trim());
                        
                        configuraciones.add(new ConfiguracionUsuario(username, lenguaje, nivel, primeraVez));
                    }
                }
            }
        }
        
        return configuraciones;
    }
    
    /**
     * Escribe todas las configuraciones al archivo
     */
    private void escribirConfiguraciones(List<ConfiguracionUsuario> configuraciones) throws IOException {
        String rutaCompleta = System.getProperty("user.dir") + "/src/main/resources" + ARCHIVO_CONFIGURACION;
        
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(rutaCompleta), StandardCharsets.UTF_8)) {
            writer.write("# Archivo de configuración de usuarios\n");
            writer.write("# Formato: username;lenguaje;nivel;primeraVez\n");
            writer.write("# primeraVez: true = necesita configuración inicial, false = ya configurado\n");
            
            for (ConfiguracionUsuario config : configuraciones) {
                writer.write(String.format("%s;%s;%s;%s\n", 
                    config.getUsername(), 
                    config.getLenguaje(), 
                    config.getNivel(), 
                    config.isPrimeraVez()));
            }
        }
    }
}
