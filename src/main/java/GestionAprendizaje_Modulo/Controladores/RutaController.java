//package GestionAprendizaje_Modulo.Controladores;
//
//import java.io.IOException;
//import java.util.List;
//
//import GestionAprendizaje_Modulo.Logica.AprendizajeManager;
//import GestionAprendizaje_Modulo.Logica.Curso;
//import GestionAprendizaje_Modulo.Logica.NodoRuta;
//import GestionAprendizaje_Modulo.Logica.Ruta;
//import MetodosGlobales.MetodosFrecuentes;
//import MetodosGlobales.SesionManager;
//import Modulo_Usuario.Clases.Usuario;
//import Nuevo_Modulo_Leccion.controllers.LeccionUIController;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.Tooltip;
//import javafx.scene.layout.AnchorPane;
//import javafx.scene.layout.Pane;
//import javafx.stage.Stage;
//
///**
// * =================================================================================
// * Controlador de la Vi	sta de Ruta - Versión Final con Progreso de Usuario
// * =================================================================================
// * Este controlador es la capa de VISTA (presentación) de la ruta de aprendizaje.
// * Es consciente del usuario logueado y muestra su progreso individual,
// * permitiéndole interactuar con las lecciones y guardando su avance.
// */
//public class RutaController {
//
//    @FXML private Pane nodoContainer;    // El panel del FXML donde se dibujarán los nodos.
//    @FXML private AnchorPane rootPane;   // El panel raíz de la ventana, para obtener el Stage.
//    @FXML private Label tituloLenguajeLabel; // El label para mostrar el título de la ruta.
//
//    // Botones de la parte superior
//    @FXML private Button btnRecursos;
//
//    // Botones de la barra inferior
//    @FXML private Button btnHome;
//    @FXML private Button btnAdd;
//    @FXML private Button btnLibrary;
//
//    /**
//     * Atributo para mantener una referencia al usuario que está usando la pantalla.
//     * Se obtiene del SesionManager al iniciar.
//     */
//    private Usuario usuarioActual;
//
//    /**
//     * El método initialize se ejecuta automáticamente después de que el FXML ha sido cargado.
//     * Es el punto de partida para construir la vista dinámica.
//     */
//    @FXML
//    private void initialize() {
//        System.out.println("[RutaController] Inicializando...");
//        System.out.println("[RutaController] Inicializando...");
//
//        // Configuración de la acción del botón 'Recursos'
//        btnRecursos.setOnAction(event -> {
//            System.out.println("Botón de Recursos presionado");
//            abrirRecursos();  // Llamar al método para manejar la acción
//        });
//
//        // --- ¡ACCIÓN CLAVE! LA CARGA DE DATOS SE INICIA AQUÍ ---
//        try {
//            // 1. Llamamos al manager para que construya toda la estructura de datos.
//            AprendizajeManager.getInstancia().construirDatosDePrueba();
//        } catch (Exception e) {
//            System.err.println("!!! ERROR FATAL DURANTE LA CONSTRUCCIÓN DE DATOS en RutaController !!!");
//            e.printStackTrace();
//            // Mostrar un mensaje de error en la UI
//            tituloLenguajeLabel.setText("Error al cargar datos");
//            return; // Detener si la construcción falla.
//        }
//        // 1. OBTENER EL USUARIO ACTUAL DE LA SESIÓN
//        this.usuarioActual = SesionManager.getInstancia().getUsuarioAutenticado();
//
//        // Comprobación de seguridad: si no hay un usuario logueado, la funcionalidad
//        // de progreso no funcionará. Es crucial manejar este caso.
//        if (usuarioActual == null) {
//            System.err.println("[RutaController] ¡ERROR CRÍTICO! No hay ningún usuario en la sesión. No se puede mostrar ni guardar el progreso.");
//            tituloLenguajeLabel.setText("Usuario Desconocido");
//            // En una aplicación real, aquí podrías redirigir a la pantalla de login.
//            return;
//        }
//        System.out.println("[RutaController] Vista cargada para el usuario: " + usuarioActual.getUsername());
//
//        // 2. OBTENER LA ESTRUCTURA DE LA RUTA (la "plantilla")
//        // Se asume que AprendizajeManager ya fue inicializado al arrancar la app.
//        List<Curso> cursos = AprendizajeManager.getInstancia().getCursos();
//
//        if (cursos.isEmpty() || cursos.get(0).getRutas().isEmpty()) {
//            System.err.println("[RutaController] No hay cursos o rutas disponibles en el Manager.");
//            return;
//        }
//
//        // Para esta demo, mostramos la primera ruta del primer curso.
//        Ruta ruta = cursos.get(0).getRutas().get(0);
//        tituloLenguajeLabel.setText(ruta.getNombre()); // Actualizar el título de la vista
//
//        // 3. DIBUJAR LA VISTA, AHORA CONSCIENTE DEL PROGRESO DEL USUARIO
//        construirNodosVisuales(ruta.getNodos());
//
//        // Vincular los botones con sus respectivas acciones
//        configurarBotones();
//    }
//
//    // Función para configurar los botones de la barra inferior
//    private void configurarBotones() {
//        // Acción para el botón Home
//        btnHome.setOnAction(event -> {
//            try {
//                // Cargar el archivo FXML de la vista HomeUsuario
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Modulo_Usuario/views/homeUsuario.fxml"));
//                AnchorPane homePane = loader.load(); // Cargar el contenido del archivo FXML
//
//                // Obtener el stage actual y cambiar la escena
//                Stage stage = (Stage) rootPane.getScene().getWindow();
//                stage.setScene(new javafx.scene.Scene(homePane)); // Cambiar la escena por la nueva vista
//                stage.show(); // Mostrar la nueva escena
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//
//        // Acción para el botón Add
//        btnAdd.setOnAction(event -> {
//            try {
//                // Cargar el archivo FXML de la vista HomeUsuario
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionAprendizaje_Modulo/Vistas/Cursos.fxml"));
//                AnchorPane cursos = loader.load(); // Cargar el contenido del archivo FXML
//
//                // Obtener el stage actual y cambiar la escena
//                Stage stage = (Stage) rootPane.getScene().getWindow();
//                stage.setScene(new javafx.scene.Scene(cursos)); // Cambiar la escena por la nueva vista
//                stage.show(); // Mostrar la nueva escena
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//
//        // Acción para el botón Library
//        btnLibrary.setOnAction(event -> {
//            System.out.println("Library button clicked!");
//            // Aquí puedes agregar la acción para el botón de "📚".
//            // Ejemplo: Navegar a una vista de biblioteca o recursos
//        });
//    }
//
//    /**
//     * Dibuja los "noditos" en la pantalla, comprobando el progreso del usuario para cada uno.
//     * @param nodosDeLaRuta La lista de objetos NodoRuta que componen la ruta "plantilla".
//     */
//    private void construirNodosVisuales(List<NodoRuta> nodosDeLaRuta) {
//        // Limpiamos el contenedor antes de (re)dibujar para evitar duplicados al actualizar.
//        nodoContainer.getChildren().clear();
//
//        // Coordenadas (X, Y) para cada nodo.
//        double[][] posiciones = {
//                {50, 50}, {150, 120}, {80, 200}, {200, 280}, {120, 360},
//                {300, 100}, {400, 180}, {500, 260}, {350, 330}, {600, 400}  // Agregamos más posiciones
//        };
//
//
//        System.out.println("[RutaController] Dibujando " + nodosDeLaRuta.size() + " nodos para el usuario " + usuarioActual.getUsername());
//
//        for (NodoRuta nodo : nodosDeLaRuta) {
//            if (nodo.getOrden() > posiciones.length) break;
//
//            Button boton = new Button("L" + nodo.getOrden());
//
//            // --- LÓGICA DE VISUALIZACIÓN DE PROGRESO ---
//            // Le preguntamos al Manager si el USUARIO ACTUAL ha completado ESTE nodo.
//            boolean estaCompletado = AprendizajeManager.getInstancia().isNodoCompletadoParaUsuario(usuarioActual, nodo);
//
//            // Aplicamos un estilo diferente si el nodo está completado.
//            if (estaCompletado) {
//                // Estilo para nodo completado (ej. azul)
//                boton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 50%; -fx-min-width: 40px; -fx-min-height: 40px; -fx-max-width: 40px; -fx-max-height: 40px; -fx-cursor: hand;");
//                boton.setTooltip(new Tooltip("Lección COMPLETADA"));
//            } else {
//                // Estilo para nodo pendiente (ej. verde)
//                boton.setStyle("-fx-background-color: #50C878; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 50%; -fx-min-width: 40px; -fx-min-height: 40px; -fx-max-width: 40px; -fx-max-height: 40px; -fx-cursor: hand;");
//                boton.setTooltip(new Tooltip("Lección con " + nodo.getLeccion().getNumEjercicios() + " ejercicios"));
//            }
//
//            boton.setLayoutX(posiciones[nodo.getOrden() - 1][0]);
//            boton.setLayoutY(posiciones[nodo.getOrden() - 1][1]);
//
//            // Guardamos el objeto NODO completo en el botón para tener acceso a su ID y Leccion.
//            boton.setUserData(nodo);
//
//            // --- LÓGICA DE CLIC CON GUARDADO DE PROGRESO ---
//            boton.setOnAction(event -> {
//                NodoRuta nodoClicado = (NodoRuta) ((Button) event.getSource()).getUserData();
//
//                // Abrimos la ventana de la lección del otro módulo.
//                Stage stage = (Stage) rootPane.getScene().getWindow();
//                LeccionUIController.mostrarUnaLeccion(nodoClicado.getLeccion(), stage, "/GestionAprendizaje_Modulo/Vistas/Ruta.fxml");
//
//                // ¡ACCIÓN CLAVE! Marcamos el nodo como completado PARA EL USUARIO ACTUAL.
//                // Esto guardará el progreso en la memoria del AprendizajeManager.
//                AprendizajeManager.getInstancia().marcarNodoComoCompletado(usuarioActual, nodoClicado);
//
//                // Volvemos a dibujar toda la ruta para que el cambio de color sea instantáneo.
//                construirNodosVisuales(nodosDeLaRuta);
//            });
//
//            nodoContainer.getChildren().add(boton);
//        }
//    }
//
//    @FXML
//    private void manejarAtras() {
//        MetodosFrecuentes.cambiarVentana((Stage) rootPane.getScene().getWindow(), "/Modulo_Usuario/views/homeUsuario.fxml", "Perfil de Usuario");
//
//    }
//
//    private void abrirRecursos() {
//        try {
//            // Cargar el archivo FXML de la vista de recursos
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionAprendizaje_Modulo/Vistas/Recursos.fxml"));
//            AnchorPane recursosPane = loader.load();  // Cargar el contenido de la vista de recursos
//
//            // Cambiar la escena en el Stage actual
//            Stage stage = (Stage) rootPane.getScene().getWindow();
//            stage.getScene().setRoot(recursosPane); // Cambiar la escena al nuevo panel
//        } catch (IOException e) {
//            e.printStackTrace();  // Mostrar error si algo falla al cargar el FXML
//        }
//    }
//}

//package GestionAprendizaje_Modulo.Controladores;
//
//import GestionAprendizaje_Modulo.Logica.AprendizajeManager;
//import GestionAprendizaje_Modulo.Logica.NodoRuta;
//import GestionAprendizaje_Modulo.Logica.Ruta;
//import GestionAprendizaje_Modulo.Modelo.RecursoAprendizaje;
//import GestionAprendizaje_Modulo.Repositorio.RecursoRepository;
//import MetodosGlobales.MetodosFrecuentes;
//import MetodosGlobales.SesionManager;
//import Nuevo_Modulo_Leccion.logic.TemaLeccion;
//import Modulo_Usuario.Clases.Usuario;
//import Nuevo_Modulo_Leccion.controllers.LeccionUIController;
//import Nuevo_Modulo_Leccion.logic.Leccion;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.Tooltip;
//import javafx.scene.layout.AnchorPane;
//import javafx.scene.layout.FlowPane;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
///**
// * =================================================================================
// * Controlador de la Vista de Ruta - Versión Final Autosuficiente
// * =================================================================================
// * Este controlador es autosuficiente. Al iniciarse, le pide al
// * AprendizajeManager que construya la estructura de rutas y luego dibuja
// * una ruta por defecto (Java) en la pantalla.
// */
//public class RutaController {
//
//    @FXML private VBox contenidoVBox;
//    @FXML private Label tituloLenguajeLabel;
//    @FXML private AnchorPane rootPane;
//    @FXML private Button btnAtras;
//    @FXML private Button btnRecursosGenerales;
//    private Usuario usuarioActual;
//    private Ruta rutaActual;
//
//    @FXML
//    private void initialize() {
//        System.out.println("[RutaController] Inicializando...");
//
//        // 1. INICIAR LA CONSTRUCCIÓN DE DATOS EN EL MANAGER
//        AprendizajeManager.getInstancia().construirDatosDePrueba();
//
//        // 2. OBTENER EL USUARIO ACTUAL DE LA SESIÓN
//        this.usuarioActual = SesionManager.getInstancia().getUsuarioAutenticado();
//        if (usuarioActual == null) {
//            tituloLenguajeLabel.setText("ERROR: No hay usuario en sesión");
//            return;
//        }
//
//        // 3. "JALAR" LA RUTA DE JAVA POR DEFECTO
//        this.rutaActual = AprendizajeManager.getInstancia().getRutaPorNombre("Java");
//
//        if (this.rutaActual == null) {
//            System.err.println("[RutaController] No se pudo obtener la ruta por defecto 'Java' del Manager.");
//            tituloLenguajeLabel.setText("Contenido no disponible");
//            return;
//        }
//
//        tituloLenguajeLabel.setText(rutaActual.getNombre());
//
//        // 4. Dibujar la UI con la información obtenida.
//        construirContenedoresVisuales();
//    }
//
//    private void construirContenedoresVisuales() {
//        contenidoVBox.getChildren().clear();
//        contenidoVBox.setSpacing(20);
//        if (rutaActual == null) return;
//
//        Map<TemaLeccion, List<NodoRuta>> nodosPorTema = rutaActual.getNodos().stream()
//                .filter(nodo -> nodo.getLeccion() != null && !nodo.getLeccion().getListEjercicios().isEmpty())
//                .collect(Collectors.groupingBy(nodo -> nodo.getLeccion().getListEjercicios().get(0).getTema()));
//
//        for (Map.Entry<TemaLeccion, List<NodoRuta>> grupo : nodosPorTema.entrySet()) {
//            String nombreTema = capitalizar(grupo.getKey().name());
//            List<NodoRuta> nodosDelTema = grupo.getValue();
//            VBox contenedorUI = crearContenedorUI(nombreTema, nodosDelTema);
//            contenidoVBox.getChildren().add(contenedorUI);
//        }
//    }
//
//    private VBox crearContenedorUI(String nombreTema, List<NodoRuta> nodos) {
//        VBox vbox = new VBox(10);
//        vbox.setStyle("-fx-padding: 15; -fx-background-color: #FFFFFF1A; -fx-background-radius: 10;");
//
//        Label tituloTema = new Label(nombreTema);
//        tituloTema.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
//
//        FlowPane nodosPane = new FlowPane(15, 15);
//        for (NodoRuta nodo : nodos) {
//            Button nodoBoton = new Button("L" + nodo.getOrden());
//
//            boolean estaCompletado = AprendizajeManager.getInstancia().isNodoCompletadoParaUsuario(usuarioActual, nodo);
//            if (estaCompletado) {
//                nodoBoton.setStyle("-fx-background-color: #3498DB; /* Estilo completado */");
//                nodoBoton.setTooltip(new Tooltip("Lección COMPLETADA"));
//            } else {
//                nodoBoton.setStyle("-fx-background-color: #50C878; /* Estilo pendiente */");
//                nodoBoton.setTooltip(new Tooltip("Lección con " + nodo.getLeccion().getNumEjercicios() + " ejercicios"));
//            }
//            nodoBoton.getStyleClass().add("nodo-boton");
//
//            nodoBoton.setUserData(nodo);
//            nodoBoton.setOnAction(e -> {
//                NodoRuta nodoClicado = (NodoRuta) ((Button) e.getSource()).getUserData();
//                Stage stage = (Stage) contenidoVBox.getScene().getWindow();
//                LeccionUIController.mostrarUnaLeccion(nodoClicado.getLeccion(), stage, "/GestionAprendizaje_Modulo/Vistas/Ruta.fxml");
//                AprendizajeManager.getInstancia().marcarNodoComoCompletado(usuarioActual, nodoClicado);
//                construirContenedoresVisuales();
//            });
//
//            // --- ¡NUEVO BOTÓN DE RECURSOS DENTRO DEL CONTENEDOR! ---
//            Button verRecursosBtn = new Button("💡 Ver Recursos de " + nombreTema);
//            verRecursosBtn.setStyle("-fx-background-color: #A6B1E1; -fx-text-fill: #424874; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand;");
//
//            verRecursosBtn.setOnAction(e -> {
//                // El 'nivel' de la Ruta contiene el LENGUAJE (ej. "JAVA")
//                String lenguajeDeLaRuta = rutaActual.getNivel();
//                // El 'name()' del enum del tema contiene el TEMA (ej. "CONTROL_DE_FLUJO")
//                String temaDelContenedor = nombreTema;
//
//                // Llamamos al método de búsqueda con los datos correctos.
//                List<RecursoAprendizaje> recursos = RecursoRepository.getInstancia()
//                        .buscarRecursosPorLenguajeYTema(lenguajeDeLaRuta, temaDelContenedor);
//
//                try {
//                    // Cargar el archivo FXML de la vista de recursos
//                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionAprendizaje_Modulo/Vistas/RecursosTema.fxml"));
//                    AnchorPane recursosPane = loader.load();  // Cargar el contenido de la vista de recursos
//
//                    // Cambiar la escena en el Stage actual
//                    Stage stage = (Stage) rootPane.getScene().getWindow();
//                    stage.getScene().setRoot(recursosPane); // Cambiar la escena al nuevo panel
//                } catch (IOException h) {
//                    h.printStackTrace();  // Mostrar error si algo falla al cargar el FXML
//                }
//
//            });
//
//            vbox.getChildren().addAll(tituloTema, nodosPane, verRecursosBtn);
//            return vbox;
//
//        }
//
//        vbox.getChildren().addAll(tituloTema, nodosPane);
//        return vbox;
//    }
//
//    private String capitalizar(String texto) {
//        if (texto == null || texto.isEmpty()) return texto;
//        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
//    }
//
//    @FXML
//    private void manejarAtras() {
//        MetodosFrecuentes.cambiarVentana((Stage) rootPane.getScene().getWindow(), "/Modulo_Usuario/views/homeUsuario.fxml", "Perfil de Usuario");
//
//    }
//
////    @FXML
////    private void abrirRecursos() {
////        try {
////            // Cargar el archivo FXML de la vista de recursos
////            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionAprendizaje_Modulo/Vistas/RecursosTema.fxml"));
////            AnchorPane recursosPane = loader.load();  // Cargar el contenido de la vista de recursos
////
////            // Cambiar la escena en el Stage actual
////            Stage stage = (Stage) rootPane.getScene().getWindow();
////            stage.getScene().setRoot(recursosPane); // Cambiar la escena al nuevo panel
////        } catch (IOException e) {
////            e.printStackTrace();  // Mostrar error si algo falla al cargar el FXML
////        }
////    }
//}


// IGNORA EL CÓDIGO DE ARRIBA 

package GestionAprendizaje_Modulo.Controladores;

import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import GestionAprendizaje_Modulo.Logica.AprendizajeManager;
import GestionAprendizaje_Modulo.Logica.NodoRuta;
import GestionAprendizaje_Modulo.Logica.Ruta;
import GestionAprendizaje_Modulo.Modelo.RecursoAprendizaje;
import GestionAprendizaje_Modulo.Repositorio.RecursoRepository;
import Conexion.MetodosFrecuentes;
import Conexion.SesionManager;
import Modulo_Usuario.Clases.Usuario;
import Nuevo_Modulo_Leccion.controllers.LeccionUIController;
import Nuevo_Modulo_Leccion.logic.TemaLeccion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * =================================================================================
 * Controlador de la Vista de Ruta - Versión Final Corregida
 * =================================================================================
 */
public class    RutaController {

    @FXML private VBox contenidoVBox;
    @FXML private Label tituloLenguajeLabel;
    @FXML private AnchorPane rootPane;

    private Usuario usuarioActual;
    private Ruta rutaActual;

    // Variable para guardar el nivel desbloqueado actual del usuario
    private int nivelDesbloqueado = 1; // 1: Básico, 2: Intermedio, 3: Avanzado

    @FXML
    private void initialize() {
        System.out.println("[RutaController] Inicializando...");
        RecursoRepository.getInstancia().cargarRecursosDesdeTXT();
        
        // Línea de depuración para verificar la carga de recursos
        List<RecursoAprendizaje> todosLosRecursos = RecursoRepository.getInstancia().buscarRecursosPorLenguajeYTema("JAVA", "TIPOS_Y_OPERADORES");
        System.out.println("[DEBUG] Recursos encontrados para JAVA/TIPOS_Y_OPERADORES: " + todosLosRecursos.size());
        
        // 1. INICIAR LA CONSTRUCCIÓN DE DATOS EN EL MANAGER
        AprendizajeManager.getInstancia().construirDatosDePrueba();

        // 2. OBTENER EL USUARIO ACTUAL DE LA SESIÓN
        this.usuarioActual = SesionManager.getInstancia().getUsuarioAutenticado();
        if (usuarioActual == null) {
            tituloLenguajeLabel.setText("ERROR: No hay usuario en sesión");
            return;
        }

        // 3. "JALAR" LA RUTA SEGÚN EL LENGUAJE SELECCIONADO
        String lenguaje = DiagnosticoController.lenguajeSeleccionado;
        String nivel = DiagnosticoController.nivelSeleccionado;
        
        // NUEVA LÓGICA: Si no hay selección previa, usar la configuración del usuario
        if (lenguaje == null || lenguaje.isEmpty()) {
            ConfiguracionUsuarioService.ConfiguracionUsuario config = 
                ConfiguracionUsuarioService.getInstancia().obtenerConfiguracion(usuarioActual.getUsername());
            if (config != null) {
                lenguaje = config.getLenguaje();
                nivel = config.getNivel();
                System.out.println("[RutaController] Cargando configuración guardada: " + lenguaje + " - " + nivel);
            }
        }
        
        this.rutaActual = AprendizajeManager.getInstancia().getRutaPorNombre(lenguaje);

        if (this.rutaActual == null) {
            System.err.println("[RutaController] No se pudo obtener la ruta por defecto 'Java' del Manager.");
            tituloLenguajeLabel.setText("Contenido no disponible");
            return;
        }
        
        // Actualizar el título con el lenguaje y nivel
        if (lenguaje != null && !lenguaje.isEmpty()) {
            tituloLenguajeLabel.setText("Ruta de " + lenguaje + " - " + (nivel != null ? nivel : "Nivel"));
        } else {
            tituloLenguajeLabel.setText(rutaActual.getNombre());
        }
        
        // 4. Dibujar la UI con la información obtenida.
        construirContenedoresVisuales();
    }

private void construirContenedoresVisuales() {
        contenidoVBox.getChildren().clear();
        contenidoVBox.setSpacing(20);
        if (rutaActual == null) return;

        // Leer nivel seleccionado del diagnóstico o de la configuración guardada
        String nivel = DiagnosticoController.nivelSeleccionado;
        
        // NUEVA LÓGICA: Si no hay nivel en el diagnóstico, usar la configuración guardada
        if (nivel == null || nivel.isEmpty()) {
            ConfiguracionUsuarioService.ConfiguracionUsuario config = 
                ConfiguracionUsuarioService.getInstancia().obtenerConfiguracion(usuarioActual.getUsername());
            if (config != null) {
                nivel = config.getNivel();
            }
        }

        // Determinar el nivel desbloqueado actual
        nivelDesbloqueado = calcularNivelDesbloqueado();

        // Por defecto, solo 1 tema
        int maxTemas = 1;

        // Lógica por diagnóstico
        if ("Intermedio".equalsIgnoreCase(nivel)) {
            maxTemas = 2;
        } else if ("Avanzado".equalsIgnoreCase(nivel)) {
            maxTemas = Integer.MAX_VALUE;
        }

        // Lógica por nivel desbloqueado
        if (nivelDesbloqueado == 2 && maxTemas < 2) {
            maxTemas = 2;
        } else if (nivelDesbloqueado == 3 && maxTemas < Integer.MAX_VALUE) {
            maxTemas = Integer.MAX_VALUE;
        }

        Map<TemaLeccion, List<NodoRuta>> nodosPorTema = rutaActual.getNodos().stream()
                .filter(nodo -> nodo.getLeccion() != null && !nodo.getLeccion().getListEjercicios().isEmpty())
                .collect(Collectors.groupingBy(
                        nodo -> nodo.getLeccion().getListEjercicios().get(0).getTema(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        int temaIndex = 0;
        for (Map.Entry<TemaLeccion, List<NodoRuta>> grupo : nodosPorTema.entrySet()) {
            TemaLeccion temaEnum = grupo.getKey();
            List<NodoRuta> nodosDelTema = grupo.getValue();
            nodosDelTema.sort(Comparator.comparingInt(NodoRuta::getOrden));
            boolean desbloqueado = temaIndex < maxTemas;
            VBox contenedorUI = crearContenedorUI(temaEnum, nodosDelTema, desbloqueado);
            contenidoVBox.getChildren().add(contenedorUI);
            temaIndex++;
        }
    }


    private VBox crearContenedorUI(TemaLeccion temaEnum, List<NodoRuta> nodos, boolean desbloqueado) {
        VBox vbox = new VBox(10);
        vbox.setStyle("-fx-padding: 15; -fx-background-color: #FFFFFF1A; -fx-background-radius: 10;");

        String nombreTema = capitalizar(temaEnum.name());
        Label tituloTema = new Label(nombreTema);
        tituloTema.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        FlowPane nodosPane = new FlowPane(15, 15);

        for (NodoRuta nodo : nodos) {
            Button nodoBoton = new Button("L" + nodo.getOrden());
            boolean estaCompletado = AprendizajeManager.getInstancia().isNodoCompletadoParaUsuario(usuarioActual, nodo);
            if (estaCompletado) {
                nodoBoton.setStyle("-fx-background-color: #3498DB;");
                nodoBoton.setTooltip(new Tooltip("Lección COMPLETADA"));
            } else {
                nodoBoton.setStyle("-fx-background-color: #50C878;");
                nodoBoton.setTooltip(new Tooltip("Lección con " + nodo.getLeccion().getNumEjercicios() + " ejercicios"));
            }
            nodoBoton.getStyleClass().add("nodo-boton");
            nodoBoton.setUserData(nodo);
            nodoBoton.setDisable(!desbloqueado);
            if (!desbloqueado) {
                nodoBoton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #888;");
                nodoBoton.setTooltip(new Tooltip("Desbloquea este tema avanzando de nivel"));
            }
            nodoBoton.setOnAction(e -> {
                NodoRuta nodoClicado = (NodoRuta) ((Button) e.getSource()).getUserData();
                Stage stage = (Stage) contenidoVBox.getScene().getWindow();
                LeccionUIController.mostrarUnaLeccion(nodoClicado.getLeccion(), stage, "/GestionAprendizaje_Modulo/Vistas/Ruta.fxml");
                AprendizajeManager.getInstancia().marcarNodoComoCompletado(usuarioActual, nodoClicado);
                construirContenedoresVisuales();
            });
            nodosPane.getChildren().add(nodoBoton);
        }

        Button verRecursosBtn = new Button("💡 Ver Recursos de " + nombreTema);
        verRecursosBtn.setStyle("-fx-background-color: #A6B1E1; -fx-text-fill: #424874; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand;");
        verRecursosBtn.setDisable(!desbloqueado);
        verRecursosBtn.setOnAction(e -> {
            List<RecursoAprendizaje> recursos = RecursoRepository.getInstancia().buscarRecursosPorLenguajeYTema(
                    rutaActual.getNivel(),
                    temaEnum.name()
            );
            abrirVentanaDeRecursos(nombreTema, recursos);
        });

        vbox.getChildren().addAll(tituloTema, nodosPane, verRecursosBtn);
        return vbox;
    }

    /**
     * Cambia la escena actual para mostrar la lista de recursos.
     */
    private void abrirVentanaDeRecursos(String tema, List<RecursoAprendizaje> recursos) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionAprendizaje_Modulo/Vistas/RecursosTema.fxml"));
            Parent root = loader.load();

            RecursosTemaController controller = loader.getController();
            controller.setRecursos(tema, recursos);

            Stage stage = (Stage) rootPane.getScene().getWindow();
            if (stage.getScene() == null) {
                stage.setScene(new Scene(root, 360, 640));
            } else {
                stage.getScene().setRoot(root);
            }
            stage.setTitle("Recursos para " + tema);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Lógica para calcular el nivel desbloqueado según el progreso del usuario
    private int calcularNivelDesbloqueado() {
        // 1: Básico, 2: Intermedio, 3: Avanzado
        // Si todos los temas del nivel actual están completos, desbloquea el siguiente
        int nivel = 1;
        Map<TemaLeccion, List<NodoRuta>> nodosPorTema = rutaActual.getNodos().stream()
                .filter(nodo -> nodo.getLeccion() != null && !nodo.getLeccion().getListEjercicios().isEmpty())
                .collect(Collectors.groupingBy(
                        nodo -> nodo.getLeccion().getListEjercicios().get(0).getTema(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
        // Básico: solo el primer tema
        if (estanTodosCompletos(nodosPorTema, 0, 1)) {
            nivel = 2; // Intermedio
            // Intermedio: los dos primeros temas
            if (estanTodosCompletos(nodosPorTema, 0, 2)) {
                nivel = 3; // Avanzado
            }
        }
        return nivel;
    }

    // Verifica si todos los nodos de los temas [start, end) están completos
    private boolean estanTodosCompletos(Map<TemaLeccion, List<NodoRuta>> nodosPorTema, int start, int end) {
        int idx = 0;
        for (List<NodoRuta> nodos : nodosPorTema.values()) {
            if (idx >= start && idx < end) {
                for (NodoRuta nodo : nodos) {
                    if (!AprendizajeManager.getInstancia().isNodoCompletadoParaUsuario(usuarioActual, nodo)) {
                        return false;
                    }
                }
            }
            idx++;
        }
        return true;
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        String conEspacios = texto.replace('_', ' ');
        return conEspacios.substring(0, 1).toUpperCase() + conEspacios.substring(1).toLowerCase();
    }

    @FXML
    private void manejarAtras() {
        try {
            MetodosFrecuentes.cambiarVentana((Stage) rootPane.getScene().getWindow(), "/Modulo_Usuario/views/homeUsuario.fxml", "Perfil de Usuario");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
