package Modulo_Ejercicios.Controladores;

import MetodosGlobales.MetodosFrecuentes;
import Modulo_Ejercicios.logic.EjercicioSeleccion;
import Modulo_Ejercicios.logic.Respuesta;
import Modulo_Ejercicios.logic.RespuestaString;
import Modulo_Ejercicios.logic.ResultadoDeEvaluacion;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EjercicioSeleccionController implements Initializable {

    // Enum para tipos de respuesta
    public enum TipoRespuesta {
        CORRECTO, INCORRECTO, PARCIALMENTE_CORRECTO
    }

    // Variables para el sistema de vidas
    private int vidasActuales = 20;
    private final int VIDAS_MAXIMAS = 3;

    @FXML
    private ImageView imgFondo;

    @FXML
    private Text txtLiveCount;

    @FXML
    private Button btnPista;

    @FXML
    private ImageView imgPista;

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

    // Lista para almacenar los botones generados dinámicamente
    private List<Button> botonesOpciones = new ArrayList<>();

    private List<String> opcionesSeleccionadas = new ArrayList<>();

    // Lista para almacenar si cada respuesta fue correcta
    private List<Boolean> respuestasCorrectasUsuario = new ArrayList<>();

    private EjercicioSeleccion ejercicioActual; // Un solo ejercicio
    private int totalEjercicios = 0;




    public EjercicioSeleccionController() {
        // Constructor requerido por FXMLLoader
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
        this.totalEjercicios = 1;
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

        }
    }
    
    //Determina el tipo de respuesta basado en el porcentaje de acierto
    private TipoRespuesta determinarTipoRespuesta(double porcentajeAcierto, int numRespuestasUsuario, int numRespuestasCorrectas) {
        if (porcentajeAcierto == 100.0) {
            return TipoRespuesta.CORRECTO;
        } else if (porcentajeAcierto > 0 && numRespuestasUsuario <= numRespuestasCorrectas) {
            // Si el usuario seleccionó algunas correctas pero no todas, y no seleccionó incorrectas
            return TipoRespuesta.PARCIALMENTE_CORRECTO;
        } else {
            return TipoRespuesta.INCORRECTO;
        }
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
        switch (tipo) {
            case CORRECTO:
                mostrarPanelCorrecto(mensaje, explicacion);
                break;
            case INCORRECTO:
                if (vidasActuales <= 0) {
                    mostrarPanelGameOver();
                } else {
                    mostrarPanelIncorrecto(mensaje, explicacion);
                }
                break;
            case PARCIALMENTE_CORRECTO:
                mostrarPanelParcial(mensaje, explicacion);
                break;
        }

        // Configurar y mostrar el botón siguiente
        configurarBotonSiguiente();

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
            if (textGameOver != null) {
                textGameOver.setText("¡Se agotaron las vidas!");
            }
            if (textGameOverDetalle != null) {
                textGameOverDetalle.setText("No te preocupes, puedes intentarlo nuevamente.");
            }
        }
    }

    private void configurarBotonSiguiente() {
        if (btnSiguiente != null) {
            btnSiguiente.setDisable(false);
            btnSiguiente.setOpacity(1.0);

            if (vidasActuales <= 0) {
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializar la lección al cargar el controller
        // Configurar el panel de feedback inicialmente oculto
        if (feedbackPanel != null) {
            feedbackPanel.setVisible(false);
        }

        btnComprobar.setOnAction(event -> handleComprobar());

        // Configurar el evento del botón siguiente
        if (btnSiguiente != null) {
            btnSiguiente.setOnAction(event -> handleContinuar());
        }
    }

    @FXML
    private void handleContinuar() {
        if (vidasActuales <= 0) {
            // Mostrar alerta de Game Over y regresar a la pantalla de ruta
            MetodosFrecuentes.mostrarAlerta("Game Over", "Se han agotado las vidas. Regresando a la pantalla de ruta.");
        } else {
            // Continuar al siguiente ejercicio
            feedbackPanel.setVisible(false);
            btnComprobar.setVisible(false); // Ocultar hasta que se haga una nueva selección
            cerrarVentanaYAvanzar();
        }
    }

        /**
     * Actualiza la barra de progreso basada en el progreso de la lección
     */
    private void actualizarProgressBar() {
        try {
            // Obtener el progreso actual de la lección desde LeccionUIController
            int indiceActual = Nuevo_Modulo_Leccion.controllers.LeccionUIController.getIndiceEjercicioActual() + 1;
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

}