package GestionAprendizaje_Modulo.Controladores;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Servicio para manejar la configuración de usuarios con múltiples lenguajes
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

    public static class ConfiguracionUsuario {
        private String username;
        private List<String> lenguajes;
        private List<String> niveles;
        private boolean primeraVez;

        public ConfiguracionUsuario(String username, List<String> lenguajes, List<String> niveles, boolean primeraVez) {
            this.username = username;
            this.lenguajes = new ArrayList<>(lenguajes);
            this.niveles = new ArrayList<>(niveles);
            this.primeraVez = primeraVez;
        }

        // Constructor para un solo lenguaje (compatibilidad)
        public ConfiguracionUsuario(String username, String lenguaje, String nivel, boolean primeraVez) {
            this.username = username;
            this.lenguajes = new ArrayList<>();
            this.niveles = new ArrayList<>();
            if (lenguaje != null && !lenguaje.isEmpty()) {
                this.lenguajes.add(lenguaje);
                this.niveles.add(nivel);
            }
            this.primeraVez = primeraVez;
        }

        public String getUsername() { return username; }
        public List<String> getLenguajes() { return lenguajes; }
        public List<String> getNiveles() { return niveles; }
        public boolean isPrimeraVez() { return primeraVez; }

        // Métodos de compatibilidad para obtener el primer lenguaje/nivel
        public String getLenguaje() {
            return lenguajes.isEmpty() ? "" : lenguajes.get(0);
        }
        public String getNivel() {
            return niveles.isEmpty() ? "" : niveles.get(0);
        }

        public void setPrimeraVez(boolean primeraVez) { this.primeraVez = primeraVez; }

        public void agregarLenguaje(String lenguaje, String nivel) {
            if (!lenguajes.contains(lenguaje)) {
                lenguajes.add(lenguaje);
                niveles.add(nivel);
            }
        }

        public boolean tieneLinquaje(String lenguaje) {
            return lenguajes.contains(lenguaje);
        }

        public String getNivelParaLenguaje(String lenguaje) {
            int index = lenguajes.indexOf(lenguaje);
            return index >= 0 ? niveles.get(index) : "";
        }
    }

    public boolean usuarioTieneConfiguracion(String username) {
        try {
            List<ConfiguracionUsuario> configuraciones = leerConfiguraciones();
            return configuraciones.stream().anyMatch(config -> config.getUsername().equals(username));
        } catch (Exception e) {
            System.err.println("Error al verificar configuración del usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean esPrimeraVez(String username) {
        try {
            List<ConfiguracionUsuario> configuraciones = leerConfiguraciones();
            for (ConfiguracionUsuario config : configuraciones) {
                if (config.getUsername().equals(username)) {
                    return config.isPrimeraVez();
                }
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error al verificar primera vez del usuario: " + e.getMessage());
            return true;
        }
    }

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

    // Método para guardar un solo lenguaje (compatibilidad)
    public void guardarConfiguracion(String username, String lenguaje, String nivel) {
        List<String> lenguajes = Arrays.asList(lenguaje);
        List<String> niveles = Arrays.asList(nivel);
        guardarConfiguracionMultiple(username, lenguajes, niveles);
    }

    // Método principal para guardar múltiples lenguajes
    public void guardarConfiguracionMultiple(String username, List<String> lenguajes, List<String> niveles) {
        try {
            List<ConfiguracionUsuario> configuraciones = leerConfiguraciones();

            int index = -1;
            for (int i = 0; i < configuraciones.size(); i++) {
                if (configuraciones.get(i).getUsername().equals(username)) {
                    index = i;
                    break;
                }
            }

            ConfiguracionUsuario nueva = new ConfiguracionUsuario(username, lenguajes, niveles, false);
            if (index >= 0) {
                configuraciones.set(index, nueva);
            } else {
                configuraciones.add(nueva);
            }

            escribirConfiguraciones(configuraciones);
            System.out.println("Configuración guardada para usuario: " + username + " - Lenguajes: " + lenguajes + " - Niveles: " + niveles);

        } catch (Exception e) {
            System.err.println("Error al guardar configuración del usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Nuevo método para AGREGAR lenguajes en lugar de reemplazarlos
    public void agregarLenguajesAlUsuario(String username, List<String> nuevosLenguajes, List<String> nuevosNiveles) {
        try {
            ConfiguracionUsuario configExistente = obtenerConfiguracion(username);

            List<String> lenguajesFinales = new ArrayList<>();
            List<String> nivelesFinales = new ArrayList<>();

            // Si el usuario ya tiene configuración, combinar con los nuevos
            if (configExistente != null) {
                lenguajesFinales.addAll(configExistente.getLenguajes());
                nivelesFinales.addAll(configExistente.getNiveles());
            }

            // Agregar solo los lenguajes que no existen ya
            for (int i = 0; i < nuevosLenguajes.size(); i++) {
                String nuevoLenguaje = nuevosLenguajes.get(i);
                String nuevoNivel = i < nuevosNiveles.size() ? nuevosNiveles.get(i) : "Básico";

                if (!lenguajesFinales.contains(nuevoLenguaje)) {
                    lenguajesFinales.add(nuevoLenguaje);
                    nivelesFinales.add(nuevoNivel);
                } else {
                    // Si el lenguaje ya existe, actualizar solo el nivel
                    int indexExistente = lenguajesFinales.indexOf(nuevoLenguaje);
                    nivelesFinales.set(indexExistente, nuevoNivel);
                }
            }

            // Guardar la configuración combinada
            guardarConfiguracionMultiple(username, lenguajesFinales, nivelesFinales);

        } catch (Exception e) {
            System.err.println("Error al agregar lenguajes al usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void marcarUsuarioNuevo(String username) {
        try {
            List<ConfiguracionUsuario> configuraciones = leerConfiguraciones();
            boolean existe = configuraciones.stream().anyMatch(config -> config.getUsername().equals(username));
            if (!existe) {
                configuraciones.add(new ConfiguracionUsuario(username, new ArrayList<>(), new ArrayList<>(), true));
                escribirConfiguraciones(configuraciones);
                System.out.println("Usuario marcado como nuevo: " + username);
            }
        } catch (Exception e) {
            System.err.println("Error al marcar usuario como nuevo: " + e.getMessage());
        }
    }

    private List<ConfiguracionUsuario> leerConfiguraciones() throws IOException {
        List<ConfiguracionUsuario> configuraciones = new ArrayList<>();

        String rutaCompleta = System.getProperty("user.dir") + "/src/main/resources" + ARCHIVO_CONFIGURACION;
        Path path = Paths.get(rutaCompleta);

        if (Files.exists(path)) {
            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    parsearLinea(configuraciones, linea);
                }
            }
            return configuraciones;
        }

        // Fallback: intentar leer desde el classpath
        try (InputStream inputStream = getClass().getResourceAsStream(ARCHIVO_CONFIGURACION)) {
            if (inputStream == null) {
                System.out.println("Archivo de configuración no encontrado, se creará al guardar.");
                return configuraciones;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    parsearLinea(configuraciones, linea);
                }
            }
        }
        return configuraciones;
    }

    private void parsearLinea(List<ConfiguracionUsuario> configuraciones, String linea) {
        if (!linea.trim().isEmpty() && !linea.startsWith("#")) {
            String[] partes = linea.split(";");
            if (partes.length == 4) {
                String username = partes[0].trim();

                // Parsear lenguajes (separados por comas)
                List<String> lenguajes = new ArrayList<>();
                if (!partes[1].trim().isEmpty()) {
                    String[] lenguajesArray = partes[1].split(",");
                    for (String lang : lenguajesArray) {
                        lenguajes.add(lang.trim());
                    }
                }

                // Parsear niveles (separados por comas, en el mismo orden que los lenguajes)
                List<String> niveles = new ArrayList<>();
                if (!partes[2].trim().isEmpty()) {
                    String[] nivelesArray = partes[2].split(",");
                    for (String nivel : nivelesArray) {
                        niveles.add(nivel.trim());
                    }
                }

                boolean primeraVez = Boolean.parseBoolean(partes[3].trim());
                configuraciones.add(new ConfiguracionUsuario(username, lenguajes, niveles, primeraVez));
            }
        }
    }

    private void escribirConfiguraciones(List<ConfiguracionUsuario> configuraciones) throws IOException {
        String rutaCompleta = System.getProperty("user.dir") + "/src/main/resources" + ARCHIVO_CONFIGURACION;
        Path path = Paths.get(rutaCompleta);
        if (path.getParent() != null && !Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("# Archivo de configuración de usuarios\n");
            writer.write("# Formato: username;lenguajes;niveles;primeraVez\n");
            writer.write("# lenguajes: separados por comas (Java,Python,C)\n");
            writer.write("# niveles: separados por comas en el mismo orden que los lenguajes (Básico,Intermedio,Avanzado)\n");
            writer.write("# primeraVez: true = necesita configuración inicial, false = ya configurado\n");

            for (ConfiguracionUsuario config : configuraciones) {
                String lenguajesStr = String.join(",", config.getLenguajes());
                String nivelesStr = String.join(",", config.getNiveles());

                writer.write(String.format("%s;%s;%s;%s\n",
                        config.getUsername(),
                        lenguajesStr,
                        nivelesStr,
                        config.isPrimeraVez()));
            }
        }
    }
}