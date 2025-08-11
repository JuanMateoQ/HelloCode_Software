package Modulo_Ejercicios.Controladores;

import Conexion.MetodosFrecuentes;
import Modulo_Ejercicios.logic.EjercicioCompletarCodigo;
import Modulo_Ejercicios.logic.Respuesta;
import Modulo_Ejercicios.logic.RespuestaString;
import Modulo_Ejercicios.logic.ResultadoDeEvaluacion;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;


// Usar la clase Usuario principal del sistema con persistencia de datos
import Modulo_Usuario.Clases.Usuario;
import Conexion.SesionManager;

public class EjercicioCompletarController implements Initializable {

    @FXML
    private Text TexTipo;

    @FXML
    private Text TexVida;

    @FXML
    private ProgressBar ProgressBar;

    @FXML
    private Button btnComprobar;

    @FXML
    private TextField textEntrada;

    @FXML
    private TextArea Ejercicio;

    @FXML
    private Text TextInstruccion;

    @FXML
    private Label AvisoCorrecto;

    @FXML
    private Label AvisoIncorrecto;

    @FXML
    TextArea Avisos;

    @FXML
    Button btnSiguiente;

    @FXML
    Label labelRetroalimentacion;

    @FXML
    Label labelBien;

    @FXML
    Text txtLenguaje;

    @FXML
    private Button btnClose;


    /**
     * COMENTADO: Campo usado para listas de ejercicios, no necesario para ejercicios individuales
     */
    private EjercicioCompletarCodigo ejercicioIndividual; // Un solo ejercicio
    private List<Boolean> respuestasCorrectasUsuario = new ArrayList<>();
    // Callback para notificar el resultado a Lección (quien gestionará vidas/navegación)
    private Consumer<ResultadoDeEvaluacion> onResultado;

    // Inyectado por LeccionUIController: Lección escuchará el resultado para manejar vidas
    public void setOnResultado(Consumer<ResultadoDeEvaluacion> onResultado) {
        this.onResultado = onResultado;
    }


    /**
     * Configura un ejercicio individual de completar código
     * Optimizado para ejercicios con UNA sola respuesta esperada
     */
    public void setEjercicio(EjercicioCompletarCodigo ejercicio) {
        this.ejercicioIndividual = ejercicio;
        this.respuestasCorrectasUsuario.clear();

        if (ejercicio != null) {
            cargarEjercicio(ejercicio);
            // Configurar la barra de progreso para un solo ejercicio
            actualizarProgressBar();
            // La UI de vidas se establece en initialize() y después de restar vida
        }
    }

    /**
     * Carga un ejercicio individual en la interfaz
     */
    private void cargarEjercicio(EjercicioCompletarCodigo ejercicio) {
        if (TextInstruccion != null) {
            TextInstruccion.setText(ejercicio.getInstruccion());
        }

        if (Ejercicio != null) {
            Ejercicio.setText(ejercicio.obtenerCodigoIncompleto());
        }

        if (textEntrada != null) {
            textEntrada.clear();
        }

        if (AvisoCorrecto != null) {
            AvisoCorrecto.setVisible(false);
        }
        if (AvisoIncorrecto != null) {
            AvisoIncorrecto.setVisible(false);
        }
        if (labelRetroalimentacion != null) {
            labelRetroalimentacion.setVisible(false);
        }
        if (labelBien != null) {
            labelBien.setVisible(false);
        }
        if (Avisos != null) {
            Avisos.setVisible(false);
        }

        if (btnSiguiente != null) {
            btnSiguiente.setDisable(true);
            btnSiguiente.setOpacity(0.0);
        }

        if (btnComprobar != null) {
            btnComprobar.setDisable(false);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ProgressBar.setProgress(0);
        actualizarVidasUI();


        // Configurar eventos de botones
        btnComprobar.setOnAction(event -> comprobarCodigo());
        btnSiguiente.setOnAction(event -> cerrarVentanaYAvanzar());
        if (btnClose != null) {
            btnClose.setOnAction(e -> confirmarSalida());
        }

        // Inicialmente ocultar el botón siguiente
        btnSiguiente.setDisable(true);
        btnSiguiente.setOpacity(0.0);
    }

    private void confirmarSalida() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText(null);
        alert.setContentText("¿Seguro quiere salir del ejercicio?");

        ButtonType aceptar = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(aceptar, cancelar);

        var result = alert.showAndWait();
        if (result.isPresent() && result.get() == aceptar) {
            terminarEjecucion();
        }
    }

    private void comprobarCodigo() {
        String input = textEntrada.getText().trim();

        if (input.isEmpty()) {
            mostrarMensajeError("Por favor, ingresa una respuesta antes de comprobar.");
            return;
        }

        ArrayList<Respuesta> respuestasUsuario = new ArrayList<>();
        respuestasUsuario.add(new RespuestaString(input));

        if (ejercicioIndividual != null) {
            ResultadoDeEvaluacion resultado = ejercicioIndividual.evaluarRespuestas(respuestasUsuario);

            if (resultado.getPorcentajeDeAcerto() == 100) {
                // Respuesta correcta
                respuestasCorrectasUsuario.add(true);
                mostrarFeedbackCorrecto();
            } else {
                // Respuesta incorrecta
                respuestasCorrectasUsuario.add(false);
                // Notificar primero a Lección para que gestione vidas
                if (onResultado != null) {
                    onResultado.accept(resultado);
                } else {
                    // Fallback por si no se inyecta el callback - usar el usuario actual
                    Usuario usuarioActual = SesionManager.getInstancia().getUsuarioAutenticado();
                    if (usuarioActual != null) {
                        usuarioActual.quitarVida();
                    }
                }

                actualizarVidasUI();

                Usuario usuarioActual = SesionManager.getInstancia().getUsuarioAutenticado();
                if (usuarioActual != null && usuarioActual.getVidas() > 0) {
                    mostrarFeedbackIncorrecto(ejercicioIndividual);
                } else {
                    mostrarGameOver();
                    return;
                }
            }

            // Mostrar botón siguiente SIEMPRE después de comprobar
            btnSiguiente.setDisable(false);
            btnSiguiente.setOpacity(1.0);
            btnComprobar.setDisable(true); // Deshabilitar comprobar para evitar múltiples clics

            // Limpiar el TextField para permitir nuevo intento
            textEntrada.clear();
        }
    }

    /**
     * Actualiza el texto de vidas en la UI.
     */
    private void actualizarVidasUI() {
        if (TexVida != null) {
            Usuario usuarioActual = SesionManager.getInstancia().getUsuarioAutenticado();
            if (usuarioActual != null) {
                // Recargar datos del archivo para asegurar sincronización
                usuarioActual.recargarDatosDesdeArchivo();
                TexVida.setText(String.valueOf(usuarioActual.getVidas()));
            } else {
                TexVida.setText("3"); // Valor por defecto
            }
        }
    }

    //Muestra feedback para respuesta correcta
    private void mostrarFeedbackCorrecto() {
        // Ocultar mensajes de error previos
        if (AvisoIncorrecto != null) {
            AvisoIncorrecto.setVisible(false);
        }
        if (labelRetroalimentacion != null) {
            labelRetroalimentacion.setVisible(false);
        }
        if (Avisos != null) {
            Avisos.setVisible(false);
        }

        // Mostrar feedback positivo
        if (AvisoCorrecto != null) {
            AvisoCorrecto.setVisible(true);
        }
        if (labelBien != null) {
            labelBien.setVisible(true);
        }

    }

    //Muestra feedback para respuesta incorrecta
    private void mostrarFeedbackIncorrecto(EjercicioCompletarCodigo ejercicio) {
        // Ocultar mensajes de correcto previos
        if (AvisoCorrecto != null) {
            AvisoCorrecto.setVisible(false);
        }
        if (labelBien != null) {
            labelBien.setVisible(false);
        }

        // Mostrar feedback de error
        if (AvisoIncorrecto != null) {
            AvisoIncorrecto.setVisible(true);
        }

        // Obtener LA respuesta esperada (sabemos que es una sola)
        ArrayList<String> respuestasEsperadas = ejercicio.obtenerRespuestasEsperadas();
        String respuestaCorrecta = respuestasEsperadas.isEmpty() ? "No disponible" : respuestasEsperadas.get(0);

        if (labelRetroalimentacion != null) {
            labelRetroalimentacion.setText("La respuesta correcta es: " + respuestaCorrecta);
            labelRetroalimentacion.setVisible(true);
        }

    }


    //Cierra la ventana actual y avanza al siguiente ejercicio en la secuencia
    private void cerrarVentanaYAvanzar() {
        try {
            // Llamar al LeccionUIController para avanzar al siguiente ejercicio
            Nuevo_Modulo_Leccion.controllers.LeccionUIController.avanzarAlSiguienteEjercicio();

            if (btnComprobar != null && btnComprobar.getScene() != null) {
                btnComprobar.getScene().getWindow().hide();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarGameOver() {
        Avisos.setText("¡Se han agotado tus vidas!");
        Avisos.setVisible(true);

        PauseTransition pauseAvisos = new PauseTransition(Duration.seconds(3));
        pauseAvisos.setOnFinished(event -> {
            Avisos.setVisible(false);
            terminarEjecucion();
        });
        pauseAvisos.play();
    }

    private void mostrarMensajeError(String mensaje) {
        Avisos.setText(mensaje);
        Avisos.setVisible(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> Avisos.setVisible(false));
        pause.play();
    }

    /**
     * Finaliza la ejecución y regresa al home
     */
    private void terminarEjecucion() {
        btnComprobar.setDisable(true);
        String destino = Nuevo_Modulo_Leccion.controllers.LeccionUIController.getRutaFXMLVentanaFinal();
        MetodosFrecuentes.cambiarVentana((Stage) btnComprobar.getScene().getWindow(), destino, "Menú de Lecciones");
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
                if (ProgressBar != null) {
                    ProgressBar.setProgress(progreso);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (ProgressBar != null) {
                ProgressBar.setProgress(1.0);
            }
        }
    }
}
