package Gamificacion_Modulo.clases;

import java.util.ArrayList;
import java.util.List;

import Conexion.SesionManager;
import Modulo_Usuario.Clases.Roles;
import Modulo_Usuario.Clases.Usuario;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    //TODO: VER PORQUE AHORA NO SE GUARDA LOS LOGROS ASIGNADOS DESPUES DE CERRAR SESION.
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


    // Campo para almacenar el Stage principal para navegación
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage; // Almacenar referencia del Stage principal
        try {
            // Prioridad 1: Cargar PerfilUsuario.fxml (Progreso - interfaz principal)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Gamificacion_Modulo/fxml/PerfilUsuario.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 360, 640);
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

                Scene scene = new Scene(root, 360, 720);
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

            Scene scene = new Scene(root, 360, 640);

            // Encontrar el Stage activo en lugar de usar primaryStage
            Stage stageActivo = null;
            if (primaryStage != null && primaryStage.isShowing()) {
                stageActivo = primaryStage;
            } else {
                // Buscar cualquier Stage abierto de JavaFX
                for (javafx.stage.Window window : Stage.getWindows()) {
                    if (window instanceof Stage && window.isShowing()) {
                        stageActivo = (Stage) window;
                        break;
                    }
                }
            }

            if (stageActivo != null) {
                // Configurar el primaryStage si no estaba configurado
                if (primaryStage == null) {
                    primaryStage = stageActivo;
                }
                
                stageActivo.setScene(scene);
                stageActivo.setTitle("Sistema de Gamificación - HelloCode");
                stageActivo.setResizable(false);  // Asegurar que no sea redimensionable
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
        SesionManager sesionManager = SesionManager.getInstancia();
        List<Usuario> usuariosDelSistema = sesionManager.getUsuarios();

        if (usuariosDelSistema == null || usuariosDelSistema.isEmpty()) {
            System.out.println(">>> Inicializando módulo de gamificación desde navegación externa");
            inicializarDatos();
        } else {
            System.out.println(">>> Módulo de gamificación ya inicializado (" + usuariosDelSistema.size() + " usuarios)");
            // Recargar usuarios para sincronización automática
            crearProgresoEstudiante();
        }

        // Debug: Mostrar usuarios cargados
        System.out.println(">>> USUARIOS DISPONIBLES EN GAMIFICACIÓN:");
        List<Usuario> usuariosParaDebug = sesionManager.getUsuarios();
        if (usuariosParaDebug != null) {
            for (int i = 0; i < usuariosParaDebug.size(); i++) {
                Usuario u = usuariosParaDebug.get(i);
                System.out.println("   " + (i + 1) + ". " + u.getNombre() + " (" + u.getUsername() + ") - " + u.getEmail());
            }
        }

        // Debug: Mostrar progresos creados
        System.out.println(">>> PROGRESOS CREADOS: " + ProgresoEstudiante.getProgresos().size());
        for (ProgresoEstudiante p : ProgresoEstudiante.getProgresos()) {
            System.out.println("   - " + p.getUsuario().getNombre() + ": " + p.getPuntosTotal() + " puntos");
        }
    }


    //TODO: ESTE GETUSUARIOS DEBEMOS MOVER A CADA UNA DE LAS CLASES QUE SE LLAMAN O CREAR LA CLASE INICIALIZADOR

    // Métodos para acceder a los datos desde los controladores
    public static List<Usuario> getUsuarios() {
        SesionManager sesionManager = SesionManager.getInstancia();
        return sesionManager.getUsuarios();
    }

    /**
     * Obtiene solo los usuarios con rol USUARIO (estudiantes) que tienen progreso en gamificación
     * @return Lista de usuarios con rol USUARIO únicamente
     */
    public static List<Usuario> getUsuariosEstudiantes() {
        try {
            SesionManager sesionManager = SesionManager.getInstancia();
            List<Usuario> todosLosUsuarios = sesionManager.getUsuarios();
            
            if (todosLosUsuarios == null) {
                return new ArrayList<>();
            }
            
            // Filtrar solo usuarios con rol USUARIO
            List<Usuario> usuariosEstudiantes = todosLosUsuarios.stream()
                    .filter(usuario -> usuario.getRol() == Roles.USUARIO)
                    .collect(java.util.stream.Collectors.toList());
                    
            System.out.println(">>> Usuarios estudiantes obtenidos: " + usuariosEstudiantes.size());
            return usuariosEstudiantes;
            
        } catch (Exception e) {
            System.err.println(">>> Error al obtener usuarios estudiantes: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene el usuario actualmente logueado
     * @return Usuario logueado o null si no hay usuario logueado
     */
    public static Usuario getUsuarioLogueado() {
        try {
            SesionManager sesionManager = SesionManager.getInstancia();
            return sesionManager.getUsuarioAutenticado();
        } catch (Exception e) {
            System.err.println(">>> Error al obtener usuario logueado: " + e.getMessage());
            return null;
        }
    }

    private static void inicializarDatos() {
        // Crear automáticamente progresos para todos los usuarios cargados desde SesionManager
        crearProgresoEstudiante();
        // Los logros se inicializan automáticamente en la clase Logro
    }

    // Método público para recargar usuarios (para sincronización)
    /**
     * Se cambio el nombre a recargarUsuarios para entender que este metodo se encarga de crear el progreso por cada estudiante
     * que haya en el sistema
     * */
    public static void crearProgresoEstudiante() {
        try {
            // Obtener usuarios desde SesionManager
            SesionManager sesionManager = SesionManager.getInstancia();
            List<Usuario> usuariosDesdeManager = sesionManager.getUsuarios();

            if (usuariosDesdeManager != null && !usuariosDesdeManager.isEmpty()) {
                // Crear progreso para usuarios nuevos
                for (Usuario usuario : usuariosDesdeManager) {
                    if (usuario.getRol() == Roles.USUARIO) {
                        // Completar información del usuario con datos por defecto si no tiene
                        if (usuario.getNombre() == null || usuario.getNombre().isEmpty() || usuario.getNombre().equals("null")) {
                            usuario.setNombre("Usuario " + usuario.getUsername());
                        }
                        if (usuario.getEmail() == null || usuario.getEmail().isEmpty() || usuario.getEmail().equals("null")) {
                            usuario.setEmail(usuario.getUsername() + "@email.com");
                        }

                        boolean existeProgreso = ProgresoEstudiante.getProgresos().stream()
                                .anyMatch(p -> p.getUsuario().getUsername().equals(usuario.getUsername()));
                        if (!existeProgreso) {
                            ProgresoEstudiante nuevoProgreso = new ProgresoEstudiante(usuario);
                            // Asignar automáticamente los puntos totales basándose en la experiencia del usuario
                            nuevoProgreso.setPuntosTotal(usuario.getXp());
                            ProgresoEstudiante.getProgresos().add(nuevoProgreso);
                            System.out.println(">>> Progreso creado para nuevo usuario: " + usuario.getNombre() + " con " + usuario.getXp() + " puntos de experiencia");
                        }
                        System.out.println(">>> Usuario cargado: " + usuario.getUsername() + " - " + usuario.getNombre());
                    }
                }
            } else {
                System.out.println(">>> No hay usuarios disponibles en SesionManager");
            }

            System.out.println(">>> Usuarios recargados exitosamente. Total: " + (usuariosDesdeManager != null ? usuariosDesdeManager.size() : 0));

        } catch (Exception e) {
            System.err.println(">>> Error al recargar usuarios: " + e.getMessage());
        }
    }
}