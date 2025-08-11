package GestionAprendizaje_Modulo.Logica;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// Clase para leer la configuración del usuario
public class UsuarioConfig {
    private String username;
    private String lenguaje;
    private String nivel;
    private boolean primeraVez;

    // Constructor, getters y setters
    public UsuarioConfig(String username, String lenguaje, String nivel, boolean primeraVez) {
        this.username = username;
        this.lenguaje = lenguaje;
        this.nivel = nivel;
        this.primeraVez = primeraVez;
    }

    public static UsuarioConfig leerConfig(String path, String usernameActual) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.startsWith("#") || linea.trim().isEmpty()) continue;
                String[] partes = linea.split(";");
                if (partes[0].equals(usernameActual)) {
                    return new UsuarioConfig(
                            partes[0], partes[1], partes[2], Boolean.parseBoolean(partes[3])
                    );
                }
            }
        }
        return null;
    }

    public String getLenguaje() {
        return this.lenguaje;
    }

    public String getNivel() {
        return this.nivel;
    }
}
