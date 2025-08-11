package Modulo_Ejercicios.Controladores;

import Conexion.MetodosFrecuentes;
import Modulo_Ejercicios.logic.EjercicioSeleccion;
import Modulo_Ejercicios.logic.Respuesta;
import Modulo_Ejercicios.logic.RespuestaString;
import Modulo_Ejercicios.logic.ResultadoDeEvaluacion;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.animation.PauseTransition;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.List;
import java.util.ResourceBundle;


// Usar la clase Usuario principal del sistema con persistencia de datos
import Modulo_Usuario.Clases.Usuario;
import Conexion.SesionManager;


public class EjercicioSeleccionController implements Initializable {

    // Enum para tipos de respuesta
    public enum TipoRespuesta {
        CORRECTO, INCORRECTO, PARCIALMENTE_CORRECTO
    }

    @FXML
    private ImageView imgFondo;

    @FXML
    private Text txtLiveCount;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label lblInstruccion;

    @FXML
    private Label lblPregunta;

    @FXML
    private Button btnComprobar;

    // Contenedor dinámico para los botones de opciones
    @FXML
    private VBox opcionesContainer;

    // Elementos del panel de feedback (Duolingo-style)
    @FXML
    private AnchorPane feedbackPanel;

    // Paneles específicos para cada tipo de respuesta
    @FXML
    private VBox panelCorrecto;

    @FXML
    private VBox panelParcial;

    @FXML
    private VBox panelIncorrecto;

    @FXML
    private VBox panelGameOver;

    // Textos para respuestas correctas
    @FXML
    private Text textCorrecto;

    @FXML
    private Text textCorrectoDetalle;

    // Textos para respuestas parciales
    @FXML
    private Text textParcial;

    @FXML
    private Text textParcialDetalle;

    @FXML
    private Text textRespuestasCorrectas;

    @FXML
    private Text textIncorrecto;

    @FXML
    private Text textIncorrectoDetalle;

    @FXML
    private Text textRespuestasCorrectasError;

    // Textos para Game Over
    @FXML
    private Text textGameOver;

    @FXML
    private Text textGameOverDetalle;

    // Botón siguiente
    @FXML
    private Button btnSiguiente;

    @FXML
    private Button btnClose;

    // Lista para almacenar los botones generados dinámicamente
    private List<Button> botonesOpciones = new ArrayList<>();

    private List<String> opcionesSeleccionadas = new ArrayList<>();

    // Lista para almacenar si cada respuesta fue correcta
    private List<Boolean> respuestasCorrectasUsuario = new ArrayList<>();

    private EjercicioSeleccion ejercicioActual; // Un solo ejercicio

    // Callback para notificar el resultado a Lección (quien gestionará vidas/navegación)
    private Consumer<ResultadoDeEvaluacion> onResultado;




    public EjercicioSeleccionController() {
        // Constructor requerido por FXMLLoader
    }

    // Inyectado por LeccionUIController: Lección escuchará el resultado para manejar vidas
    public void setOnResultado(Consumer<ResultadoDeEvaluacion> onResultado) {
        this.onResultado = onResultado;
    }


    // Método para generar botones dinámicamente según la cantidad de opciones
    public void setOpciones(List<String> opciones) {
        // Limpiar el contenedor y la lista de botones
        opcionesContainer.getChildren().clear();
        botonesOpciones.clear();

        if (opciones.size() <= 3) {
            crearBotonesVerticales(opciones);
        } else {
            crearBotonesMixtos(opciones);
        }
    }

    private void crearBotonesVerticales(List<String> opciones) {
        for (int i = 0; i < opciones.size(); i++) {
            Button btn = crearBoton(opciones.get(i), i);
            opcionesContainer.getChildren().add(btn);
            botonesOpciones.add(btn);
        }
    }

    private void crearBotonesMixtos(List<String> opciones) {
        for (int i = 0; i < opciones.size(); i += 2) {
            HBox fila = new HBox(20); // 20 px de spacing
            fila.setAlignment(javafx.geometry.Pos.CENTER);

            Button btn1 = crearBoton(opciones.get(i), i);
            fila.getChildren().add(btn1);
            botonesOpciones.add(btn1);

            if (i + 1 < opciones.size()) {
                Button btn2 = crearBoton(opciones.get(i + 1), i + 1);
                fila.getChildren().add(btn2);
                botonesOpciones.add(btn2);
            }

            opcionesContainer.getChildren().add(fila);
        }
    }

    private Button crearBoton(String texto, int indice) {
        Button btn = new Button(texto);
        btn.setPrefHeight(40.0);
        btn.setPrefWidth(140.0);
        btn.setWrapText(true); // Permite que el texto se ajuste si es muy largo
        btn.getStyleClass().add("boton-opcion");

        // Agregar evento de click
        btn.setOnAction(event -> manejarSeleccionOpcion(btn, indice, texto));

        return btn;
    }

    private void manejarSeleccionOpcion(Button botonSeleccionado, int indice, String texto) {
        if (opcionesSeleccionadas.contains(texto)) {
            // Deseleccionar - volver al estilo original
            opcionesSeleccionadas.remove(texto);
            botonSeleccionado.getStyleClass().clear();
            botonSeleccionado.getStyleClass().add("boton-opcion");
        } else {
            // Seleccionar - cambiar a estilo seleccionado
            opcionesSeleccionadas.add(texto);
            botonSeleccionado.getStyleClass().clear();
            botonSeleccionado.getStyleClass().add("boton-seleccionado");
        }

        // Mostrar u ocultar el botón comprobar según si hay selecciones
        actualizarVisibilidadBotonComprobar();

    }

    private void actualizarVisibilidadBotonComprobar() {
        if (btnComprobar != null) {
            btnComprobar.setVisible(!opcionesSeleccionadas.isEmpty());
        }
    }

    public void limpiarSelecciones() {
        opcionesSeleccionadas.clear();
        for (Button btn : botonesOpciones) {
            btn.getStyleClass().clear();
            btn.getStyleClass().add("boton-opcion");
        }
        actualizarVisibilidadBotonComprobar();
    }

    /**
     * Método para configurar un solo ejercicio individual
     */
    public void setEjercicio(EjercicioSeleccion ejercicio) {
        this.ejercicioActual = ejercicio;
        cargarInstruccion(ejercicio);
        actualizarProgressBar();
    }

    //Carga un ejercicio individual en la interfaz
    private void cargarInstruccion(EjercicioSeleccion ejercicio) {
        // Establecer la instrucción
        if (lblInstruccion != null) {
            lblInstruccion.setText(ejercicio.getInstruccion());
        }

        // Limpiar selecciones anteriores
        limpiarSelecciones();

        // Crear botones según las opciones disponibles
        List<String> opciones = ejercicio.getListOpciones();
        setOpciones(opciones);

        // Resetear estado de UI
        if (btnComprobar != null) {
            btnComprobar.setVisible(false);
            btnComprobar.setText("COMPROBAR");
        }
        if (feedbackPanel != null) {
            feedbackPanel.setVisible(false);
        }

    }



    // Método para manejar el click del botón Comprobar
    @FXML
    private void handleComprobar() {
        if (opcionesSeleccionadas.isEmpty()) {
            btnComprobar.setVisible(false);
            feedbackPanel.setVisible(true);
            return;
        }

        // Validar respuestas
        validarRespuestas();
    }

    // Método para actualizar los colores de los botones después de comprobar
    private void actualizarColoresBotones(List<String> respuestasCorrectas) {
        for (Button btn : botonesOpciones) {
            String textoBoton = btn.getText();
            boolean esCorrecta = respuestasCorrectas.contains(textoBoton);
            boolean fueSeleccionada = opcionesSeleccionadas.contains(textoBoton);

            btn.getStyleClass().clear();

            if (fueSeleccionada && esCorrecta) {
                // Seleccionada y correcta -> Verde
                btn.getStyleClass().clear();
                btn.getStyleClass().add("boton-correcto");
            } else if (fueSeleccionada && !esCorrecta) {
                // Seleccionada pero incorrecta -> Rojo
                btn.getStyleClass().add("boton-incorrecto");
            } else if (!fueSeleccionada && esCorrecta) {
                // No seleccionada pero era correcta -> Naranja
                btn.getStyleClass().add("boton-no-seleccionado-correcto");
            } else {
                // No seleccionada y no era correcta -> Mantener color original
                btn.getStyleClass().add("boton-opcion");
            }
        }
    }

    private void validarRespuestas() {
        if (ejercicioActual != null) {
            // Convertir las opciones seleccionadas a objetos Respuesta
            ArrayList<Respuesta> respuestasUsuario = new ArrayList<>();
            for (String opcionSeleccionada : opcionesSeleccionadas) {
                respuestasUsuario.add(new RespuestaString(opcionSeleccionada));
            }

            ResultadoDeEvaluacion resultado = ejercicioActual.evaluarRespuestas(respuestasUsuario);

            // Notificar primero a Lección para que gestione vidas si corresponde (<100)
            if (onResultado != null) {
                onResultado.accept(resultado);
            }
            // Refrescar contador de vidas tras posible decremento
            actualizarVidasUI();

            // Obtener las respuestas correctas para mostrar retroalimentación
            List<String> respuestasCorrectas = ejercicioActual.obtenerRespuestasCorrectas();

            actualizarColoresBotones(respuestasCorrectas);

            TipoRespuesta tipoRespuesta = determinarTipoRespuesta(resultado.getPorcentajeDeAcerto(), respuestasUsuario.size(), respuestasCorrectas.size());

            switch (tipoRespuesta) {
                case CORRECTO:
                    respuestasCorrectasUsuario.add(true);
                    mostrarFeedback(tipoRespuesta, "¡Excelente trabajo! Tu respuesta es completamente correcta.", null);
                    break;
                case PARCIALMENTE_CORRECTO:
                    respuestasCorrectasUsuario.add(false);
                    int correctas = contarRespuestasCorrectas(respuestasUsuario, respuestasCorrectas);
                    String respuestasCorrectasStr = String.join(", ", respuestasCorrectas);
                    mostrarFeedback(tipoRespuesta, "Bien, pero incompleto. Tienes " + correctas + " respuesta(s) correcta(s) de " + respuestasCorrectas.size() + ".",
                            respuestasCorrectasStr);
                    break;
                case INCORRECTO:
                    respuestasCorrectasUsuario.add(false);
                    String respuestasCorrectasError = String.join(", ", respuestasCorrectas);
                    mostrarFeedback(tipoRespuesta, "Respuesta incorrecta. No te preocupes, inténtalo de nuevo.",
                            respuestasCorrectasError);
                    break;
            }

            // Ocultar el botón comprobar y mostrar el feedback
            btnComprobar.setVisible(false);
            feedbackPanel.setVisible(true);

            // El resultado ya fue notificado antes de mostrar el feedback

        }
    }

    //Determina el tipo de respuesta basado en el porcentaje de acierto
    private TipoRespuesta determinarTipoRespuesta(double porcentajeAcierto, int numRespuestasUsuario, int numRespuestasCorrectas) {
        // Correcto solo si el usuario seleccionó exactamente todas las correctas y nada más
        if (porcentajeAcierto == 100.0 && numRespuestasUsuario == numRespuestasCorrectas) {
            return TipoRespuesta.CORRECTO;
        }
        // Parcial si hay al menos una correcta, independientemente de que haya extras incorrectas
        if (porcentajeAcierto > 0.0) {
            return TipoRespuesta.PARCIALMENTE_CORRECTO;
        }
        // Incorrecto cuando no acertó ninguna
        return TipoRespuesta.INCORRECTO;
    }

    private int contarRespuestasCorrectas(ArrayList<Respuesta> respuestasUsuario, List<String> respuestasCorrectas) {
        int contador = 0;
        for (Respuesta respuesta : respuestasUsuario) {
            String respuestaStr = respuesta.getRespuesta().toString();
            if (respuestasCorrectas.contains(respuestaStr)) {
                contador++;
            }
        }
        return contador;
    }


    private void mostrarFeedback(TipoRespuesta tipo, String mensaje, String explicacion) {
        // Ocultar todos los paneles primero
        ocultarTodosPanelesFeedback();

        // Mostrar el panel correspondiente según el tipo de respuesta
        Usuario usuarioActual = SesionManager.getInstancia().getUsuarioAutenticado();
        
        // Sincronizar datos antes de verificar vidas
        if (usuarioActual != null) {
            usuarioActual.sincronizarVidasDesdeArchivo();
        }
        
        switch (tipo) {
            case CORRECTO:
                mostrarPanelCorrecto(mensaje, explicacion);
                break;
            case INCORRECTO:
                if (usuarioActual != null && usuarioActual.getVidas() <= 0) {
                    System.out.println("🔴 Game Over - Vidas: " + usuarioActual.getVidas());
                    mostrarPanelGameOver();
                } else {
                    mostrarPanelIncorrecto(mensaje, explicacion);
                }
                break;
            case PARCIALMENTE_CORRECTO:
                if (usuarioActual != null && usuarioActual.getVidas() <= 0) {
                    // Si al quedar parcialmente correcto se agotaron las vidas, mostrar Game Over
                    System.out.println("🔴 Game Over (Parcial) - Vidas: " + usuarioActual.getVidas());
                    mostrarPanelGameOver();
                } else {
                    mostrarPanelParcial(mensaje, explicacion);
                }
                break;
        }

        // Configurar y mostrar el botón siguiente solo si NO es Game Over
        if (usuarioActual != null && usuarioActual.getVidas() <= 0) {
            if (btnSiguiente != null) {
                btnSiguiente.setVisible(false);
                btnSiguiente.setManaged(false);
            }
        } else {
            configurarBotonSiguiente();
            if (btnSiguiente != null) {
                btnSiguiente.setVisible(true);
                btnSiguiente.setManaged(true);
            }
        }

        // Mostrar el panel de feedback
        feedbackPanel.setVisible(true);
    }

    private void ocultarTodosPanelesFeedback() {
        if (panelCorrecto != null) panelCorrecto.setVisible(false);
        if (panelParcial != null) panelParcial.setVisible(false);
        if (panelIncorrecto != null) panelIncorrecto.setVisible(false);
        if (panelGameOver != null) panelGameOver.setVisible(false);
    }

    private void mostrarPanelCorrecto(String mensaje, String explicacion) {
        if (panelCorrecto != null) {
            panelCorrecto.setVisible(true);
            if (textCorrecto != null) {
                textCorrecto.setText("¡Perfecto!");
            }
            if (textCorrectoDetalle != null) {
                textCorrectoDetalle.setText(mensaje != null ? mensaje : "¡Has respondido todas las opciones correctamente!");
            }
        }
    }

    private void mostrarPanelParcial(String mensaje, String respuestasCorrectas) {
        if (panelParcial != null) {
            panelParcial.setVisible(true);
            if (textParcial != null) {
                textParcial.setText("¡Casi!");
            }
            if (textParcialDetalle != null) {
                textParcialDetalle.setText(mensaje != null ? mensaje : "Respuesta incompleta. Te faltan algunas opciones correctas.");
            }
            if (textRespuestasCorrectas != null && respuestasCorrectas != null) {
                textRespuestasCorrectas.setText("Respuestas correctas: " + respuestasCorrectas);
            }
        }
    }

    private void mostrarPanelIncorrecto(String mensaje, String respuestasCorrectas) {
        if (panelIncorrecto != null) {
            panelIncorrecto.setVisible(true);
            if (textIncorrecto != null) {
                textIncorrecto.setText("¡Inténtalo de nuevo!");
            }
            if (textIncorrectoDetalle != null) {
                textIncorrectoDetalle.setText(mensaje != null ? mensaje : "Respuesta incorrecta. Revisa las opciones.");
            }
            if (textRespuestasCorrectasError != null && respuestasCorrectas != null) {
                textRespuestasCorrectasError.setText("Las respuestas correctas son: " + respuestasCorrectas);
            }
        }
    }

    private void mostrarPanelGameOver() {
        if (panelGameOver != null) {
            panelGameOver.setVisible(true);
            
            Usuario usuarioActual = SesionManager.getInstancia().getUsuarioAutenticado();
            if (usuarioActual != null) {
                usuarioActual.sincronizarVidasDesdeArchivo();
            }
            
            if (textGameOver != null) {
                // Mensaje dinámico basado en las vidas reales del usuario
                if (usuarioActual != null && usuarioActual.getVidas() <= 0) {
                    textGameOver.setText("¡Se agotaron las vidas!");
                } else {
                    textGameOver.setText("¡Ejercicio fallido!");
                }
            }
            if (textGameOverDetalle != null) {
                String detalle = "No te preocupes, puedes intentarlo nuevamente.";
                if (usuarioActual != null) {
                    detalle += "\nVidas actuales: " + usuarioActual.getVidas();
                }
                textGameOverDetalle.setText(detalle);
            }
        }
        // Mostrar panel unos segundos y volver a la ventana anterior (como en EjercicioCompletarController)
        actualizarVidasUI();
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> terminarEjecucion());
        pause.play();
    }

    private void configurarBotonSiguiente() {
        if (btnSiguiente != null) {
            btnSiguiente.setDisable(false);
            btnSiguiente.setOpacity(1.0);

            Usuario usuarioActual = SesionManager.getInstancia().getUsuarioAutenticado();
            if (usuarioActual != null && usuarioActual.getVidas() <= 0) {
                btnSiguiente.setText("REINICIAR");
            } else {
                btnSiguiente.setText("SIGUIENTE");
            }
        }
    }

    private void cerrarVentanaYAvanzar() {
        try {
            Nuevo_Modulo_Leccion.controllers.LeccionUIController.avanzarAlSiguienteEjercicio();

            if (btnComprobar != null && btnComprobar.getScene() != null) {
                btnComprobar.getScene().getWindow().hide();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Finaliza y regresa a la ventana anterior (home), replicando la lógica de EjercicioCompletarController
    private void terminarEjecucion() {
        try {
            Button ref = (btnSiguiente != null) ? btnSiguiente : btnComprobar;
            if (ref != null && ref.getScene() != null) {
                Stage stage = (Stage) ref.getScene().getWindow();
                String destino = Nuevo_Modulo_Leccion.controllers.LeccionUIController.getRutaFXMLVentanaFinal();
                MetodosFrecuentes.cambiarVentana(stage, destino, "Menú de Lecciones");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializar la lección al cargar el controller
        // Configurar el panel de feedback inicialmente oculto
        if (feedbackPanel != null) {
            feedbackPanel.setVisible(false);
        }

        btnComprobar.setOnAction(event -> handleComprobar());

        if (btnClose != null) {
            btnClose.setOnAction(e -> confirmarSalida());
        }

        // Configurar el evento del botón siguiente
        if (btnSiguiente != null) {
            btnSiguiente.setOnAction(event -> handleContinuar());
        }

        actualizarVidasUI();

    }

    private void confirmarSalida() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText(null);
        alert.setContentText("¿Seguro quiere salir del ejercicio?");

        javafx.scene.control.ButtonType aceptar = new javafx.scene.control.ButtonType("Aceptar", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        javafx.scene.control.ButtonType cancelar = new javafx.scene.control.ButtonType("Cancelar", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(aceptar, cancelar);

        var result = alert.showAndWait();
        if (result.isPresent() && result.get() == aceptar) {
            terminarEjecucion();
        }
    }

    @FXML
    private void handleContinuar() {

        feedbackPanel.setVisible(false);
        btnComprobar.setVisible(false); // Ocultar hasta que se haga una nueva selección
        cerrarVentanaYAvanzar();

    }

    /**
     * Actualiza la barra de progreso basada en el progreso de la lección
     */
    private void actualizarProgressBar() {
        try {
            // Obtener el progreso actual de la lección desde LeccionUIController
            int indiceActual = Nuevo_Modulo_Leccion.controllers.LeccionUIController.getIndiceEjercicioActual();
            int totalEjercicios = Nuevo_Modulo_Leccion.controllers.LeccionUIController.getTotalEjercicios();

            if (totalEjercicios > 0) {
                double progreso = (double) indiceActual / totalEjercicios;
                if (progressBar != null) {
                    progressBar.setProgress(progreso);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback: progreso completo para ejercicio individual
            if (progressBar != null) {
                progressBar.setProgress(1.0);
            }
        }
    }

    private void actualizarVidasUI() {
        if (txtLiveCount != null) {
            Usuario usuarioActual = SesionManager.getInstancia().getUsuarioAutenticado();
            if (usuarioActual != null) {
                // Sincronizar vidas desde archivo para asegurar datos actualizados
                usuarioActual.sincronizarVidasDesdeArchivo();
                int vidasActuales = usuarioActual.getVidas();
                txtLiveCount.setText(String.valueOf(vidasActuales));
                System.out.println("🔄 UI actualizada - Vidas: " + vidasActuales);
            } else {
                txtLiveCount.setText("3"); // Valor por defecto
            }
        }
    }
}