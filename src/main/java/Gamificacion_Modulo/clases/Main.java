package Gamificacion_Modulo.clases;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import Modulo_Usuario.Clases.Roles;
import Modulo_Usuario.Clases.Usuario;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        // Inicializar datos del sistema
        inicializarDatos();

        // Iniciar la interfaz gráfica en un hilo separado
        Thread guiThread = new Thread(() -> {
            try {
                launch();
            } catch (Exception e) {
                System.err.println("Error al iniciar interfaz gráfica: " + e.getMessage());
            }
        });

        guiThread.setDaemon(false); // Mantener la aplicación viva
        guiThread.start();

        // Esperar un momento para que la GUI se inicialice
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    //TODO: Cambiar todos estos datos a clases que controlen y llamen
    private static final List<Usuario> usuarios = new ArrayList<>();
    private static final List<Logro> logrosDisponibles = new ArrayList<>();
    private static final List<Desafio> desafiosDisponibles = new ArrayList<>();
    private static final Ranking ranking = Ranking.getInstance();
    private static final List<ProgresoEstudiante> progresos = new ArrayList<>();

    // Campo para almacenar el Stage principal para navegación
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage; // Almacenar referencia del Stage principal

        try {
            // Prioridad 1: Cargar PerfilUsuario.fxml (Progreso - interfaz principal)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Gamificacion_Modulo/fxml/PerfilUsuario.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 360, 720);
            stage.setTitle("Sistema de Gamificación - HelloCode");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();

            System.out.println(">>> Interfaz gráfica cargada correctamente: PerfilUsuario.fxml (Progreso)");
        } catch (Exception e) {
            System.err.println("Error al cargar PerfilUsuario.fxml, intentando con Desafios.fxml: " + e.getMessage());

            try {
                // Prioridad 2: Cargar Desafios.fxml como fallback
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Gamificacion_Modulo/fxml/Desafios.fxml"));
                Parent root = loader.load();

                Scene scene = new Scene(root, 393, 852);
                stage.setTitle("Sistema de Gamificación - HelloCode");
                stage.setScene(scene);
                stage.setResizable(false);
                stage.centerOnScreen();
                stage.show();

                System.out.println(">>> Interfaz gráfica cargada correctamente: Desafios.fxml (Fallback)");
            } catch (Exception e2) {
                System.err.println("Error al cargar Desafios.fxml: " + e2.getMessage());
                e2.printStackTrace();

            }
        }
    }

    //TODO: Este metodo cambiar por el metodo global cambiar ventana
    // Método estático para cambiar escenas desde los controladores
    public static void cambiarEscena(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root, 360, 720);

            // Encontrar el Stage activo en lugar de usar primaryStage
            Stage stageActivo = null;
            if (primaryStage != null && primaryStage.isShowing()) {
                stageActivo = primaryStage;
            } else {
                // Buscar cualquier Stage abierto de JavaFX
                for (javafx.stage.Window window : javafx.stage.Stage.getWindows()) {
                    if (window instanceof Stage && window.isShowing()) {
                        stageActivo = (Stage) window;
                        break;
                    }
                }
            }

            if (stageActivo != null) {
                stageActivo.setScene(scene);
                stageActivo.centerOnScreen();
                System.out.println(">>> Navegación exitosa a: " + fxmlPath);
            } else {
                System.err.println("No se encontró un Stage activo para cambiar la escena");
            }

        } catch (Exception e) {
            System.err.println("Error al cambiar a la escena: " + fxmlPath);
            e.printStackTrace();
        }
    }

    //TODO: METODO SUPER IMPORTANTE PARA CARGAR DATOS DESDE OTROS MÓDULOS

    // Método para inicializar datos cuando se navega desde otro módulo
    public static void inicializarDesdeModuloExterno() {
        if (usuarios.isEmpty()) {
            System.out.println(">>> Inicializando módulo de gamificación desde navegación externa");
            inicializarDatos();
        } else {
            System.out.println(">>> Módulo de gamificación ya inicializado (" + usuarios.size() + " usuarios)");
            // Recargar usuarios para sincronización automática
            crearProgresoEstudiante();
        }

        // Debug: Mostrar usuarios cargados
        System.out.println(">>> USUARIOS DISPONIBLES EN GAMIFICACIÓN:");
        for (int i = 0; i < usuarios.size(); i++) {
            Usuario u = usuarios.get(i);
            System.out.println("   " + (i + 1) + ". " + u.getNombre() + " (" + u.getUsername() + ") - " + u.getEmail());
        }

        // Debug: Mostrar progresos creados
        System.out.println(">>> PROGRESOS CREADOS: " + progresos.size());
        for (ProgresoEstudiante p : progresos) {
            System.out.println("   - " + p.getUsuario().getNombre() + ": " + p.getPuntosTotal() + " puntos");
        }
    }


    //TODO: METODOS QUE SI USAN OTRAS CLASES Y TOCA ARREGLAR

    // Métodos para acceder a los datos desde los controladores
    public static List<Usuario> getUsuarios() {
        return usuarios;
    }

    public static List<Logro> getLogrosDisponibles() {
        return logrosDisponibles;
    }

    public static List<Desafio> getDesafiosDisponibles() {
        return desafiosDisponibles;
    }

    public static void agregarDesafio(Desafio desafio) {
        desafiosDisponibles.add(desafio);
    }

    public static Ranking getRanking() {
        return ranking;
    }

    public static List<ProgresoEstudiante> getProgresos() {
        return progresos;
    }

    //TODO: CREAR UNA NUEVA CLASE PARA INICIALIZAR LOS DATOS, se quito el bucle porque hacia lo mismo de crearProgresoEstudiante
    private static void inicializarDatos() {
        // Cargar usuarios del módulo de usuarios
        cargarUsuariosDesdeArchivo();
        // Crear automáticamente progresos para todos los usuarios cargados
        crearProgresoEstudiante();
        // Inicializar logros predeterminados
        inicializarLogros();

    }

    // Método público para recargar usuarios (para sincronización)
    /**
     * Se cambio el nombre a recargarUsuarios para entender que este metodo se encarga de crear el progreso por cada estudiante
     * que haya en el sistema
     * */
    public static void crearProgresoEstudiante() {
        try {
            usuarios.clear();
            cargarUsuariosDesdeArchivo();

            // Crear progreso para usuarios nuevos
            for (Usuario usuario : usuarios) {

                boolean existeProgreso = progresos.stream()
                        .anyMatch(p -> p.getUsuario().getUsername().equals(usuario.getUsername()));
                if (!existeProgreso) {
                    ProgresoEstudiante nuevoProgreso = new ProgresoEstudiante(usuario);
                    // Asignar automáticamente los puntos totales basándose en la experiencia del usuario
                    nuevoProgreso.setPuntosTotal(usuario.getXp());
                    progresos.add(nuevoProgreso);
                    System.out.println(">>> Progreso creado para nuevo usuario: " + usuario.getNombre() + " con " + usuario.getXp() + " puntos de experiencia");
                }
            }

            System.out.println(">>> Usuarios recargados exitosamente. Total: " + usuarios.size());

        } catch (Exception e) {
            System.err.println(">>> Error al recargar usuarios: " + e.getMessage());
        }
    }

    // Método para cargar usuarios desde el archivo del módulo de usuarios
    private static void cargarUsuariosDesdeArchivo() {
        try {
            InputStream inputStream = new FileInputStream(new File("src/main/java/Modulo_Usuario/Usuarios/usuarios.txt"));

            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    linea = linea.trim();
                    if (!linea.isEmpty()) {
                        Usuario usuario = Usuario.fromString(linea);

                        if (usuario != null) {
                            // Completar información del usuario con datos por defecto si no tiene
                            if (usuario.getNombre() == null || usuario.getNombre().isEmpty() || usuario.getNombre().equals("null")) {
                                usuario.setNombre("Usuario " + usuario.getUsername());
                            }
                            if (usuario.getEmail() == null || usuario.getEmail().isEmpty() || usuario.getNombre().equals("null")) {
                                usuario.setEmail(usuario.getUsername() + "@email.com");
                            }
                            if (usuario.getRol() == Roles.USUARIO){
                                usuarios.add(usuario);
                                System.out.println(">>> Usuario cargado: " + usuario.getUsername() + " - " + usuario.getNombre());
                            }

                        }
                    }
                }
            }

            if (usuarios.isEmpty()) {
                System.out.println(">>> No se encontraron usuarios válidos. Creando usuarios por defecto.");

            }

        } catch (Exception e) {
            System.err.println(">>> Error al cargar usuarios: " + e.getMessage());
            e.printStackTrace();

        }
    }
    //TODO: Mover a alguna clase que maneje el inicializador de logros

    private static void inicializarLogros() {
        logrosDisponibles.add(new Logro("Principiante", "Completar tu primer desafio",  100));
        logrosDisponibles.add(new Logro("Dedicado", "Completar 3 desafios", 250));
        logrosDisponibles.add(new Logro("Acumulador", "Obtener 500 puntos", 150));
        logrosDisponibles.add(new Logro("Coleccionista", "Obtener 5 logros", 300));
        System.out.println(">>> Logros predeterminados cargados: " + logrosDisponibles.size());
    }
}