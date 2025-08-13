package Modulo_Ejercicios.Controladores;

import Conexion.SesionManager;
import Modulo_Ejercicios.logic.EjercicioEmparejar;
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
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

public class EmparejarController implements Initializable {

    // Enum para tipos de respuesta
    public enum TipoRespuesta {
        CORRECTO, INCORRECTO, PARCIALMENTE_CORRECTO
    }

    // Elementos del header
    @FXML private Text txtLiveCount;
    @FXML private Button btnPista;
    @FXML private ImageView imgPista;
    @FXML private ProgressBar progressBar;

    // Elementos principales
    @FXML private Text txtLenguaje;
    @FXML private Label lblPregunta;
    @FXML private Label lblInstruccion;
    @FXML private VBox columnIzquierda;
    @FXML private VBox columnDerecha;
    @FXML private Button btnSiguiente;

    // Elementos de feedback
    @FXML private AnchorPane feedbackPanel;
    @FXML private VBox panelCorrecto;
    @FXML private VBox panelParcial;
    @FXML private VBox panelIncorrecto;
    @FXML private VBox panelGameOver;
    @FXML private Text textCorrecto;
    @FXML private Text textCorrectoDetalle;
    @FXML private Text textParcial;
    @FXML private Text textParcialDetalle;
    @FXML private Text textPuntuacion;
    @FXML private Text textIncorrecto;
    @FXML private Text textIncorrectoDetalle;
    @FXML private Button btnSiguienteCompleto;

    // Variables del juego
    private EjercicioEmparejar ejercicioActual;
    private Button opcionSeleccionadaIzquierda;
    private Button opcionSeleccionadaDerecha;
    private Map<String, String> emparejamientosCorrectos;
    private List<String> emparejamientosRealizadosEnOrden;
    private List<Button> botonesIzquierda;
    private List<Button> botonesDerecha;
    private int emparejamientosCompletados = 0;
    private int emparejamientosIncorrectos = 0;
    private boolean ejercicioCompletado = false;

    // Lista para almacenar si cada respuesta fue correcta
    private List<Boolean> respuestasCorrectasUsuario = new ArrayList<>();

    // Callback para notificar el resultado a Lección (quien gestionará vidas/navegación)
    private Consumer<ResultadoDeEvaluacion> onResultado;

    // Estilos inline
    private static final String ESTILO_NORMAL =
            "-fx-background-color: #DCD6F7; " +
                    "-fx-text-fill: #424874; " +
                    "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 12px; " +
                    "-fx-background-radius: 8; " +
                    "-fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, #424874, 4, 0.2, 0, 1);";

    private static final String ESTILO_SELECCIONADO =
            "-fx-background-color: #A6B1E1; " +
                    "-fx-text-fill: #424874; " +
                    "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 12px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-radius: 8; " +
                    "-fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, #424874, 6, 0.3, 0, 2);";

    private static final String ESTILO_CORRECTO =
            "-fx-background-color: #4CAF50; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 12px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-radius: 8; " +
                    "-fx-effect: dropshadow(gaussian, #2E7D32, 6, 0.3, 0, 2);";

    private static final String ESTILO_INCORRECTO =
            "-fx-background-color: #F44336; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 12px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-radius: 8; " +
                    "-fx-effect: dropshadow(gaussian, #C62828, 6, 0.3, 0, 2);";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializar estructuras de datos
        emparejamientosCorrectos = new HashMap<>();
        emparejamientosRealizadosEnOrden = new ArrayList<>();
        botonesIzquierda = new ArrayList<>();
        botonesDerecha = new ArrayList<>();

        // Configurar panel de feedback inicialmente oculto
        if (feedbackPanel != null) {
            feedbackPanel.setVisible(false);
        }

        // Configurar eventos de botones
        if (btnSiguiente != null) {
            btnSiguiente.setOnAction(event -> handleContinuar());
        }

        if (btnSiguienteCompleto != null) {
            btnSiguienteCompleto.setOnAction(event -> handleContinuar());
        }

        // Inicializar contador de vidas
        if (txtLiveCount != null) {
            actualizarVidasUI();
        }
        
    }

    /**
     * Método para configurar un solo ejercicio individual
     */
    public void setEjercicio(EjercicioEmparejar ejercicio) {
        this.ejercicioActual = ejercicio;
        cargarEjercicio(ejercicio);
        actualizarProgressBar();

        if(txtLenguaje != null) {
            txtLenguaje.setText(ejercicio.getLenguajeEjercicio());
        }
    }

    // Inyectado por LeccionUIController: Lección escuchará el resultado para manejar vidas
    public void setOnResultado(Consumer<ResultadoDeEvaluacion> onResultado) {
        this.onResultado = onResultado;
    }

    /**
     * Carga un ejercicio individual en la interfaz
     */
    private void cargarEjercicio(EjercicioEmparejar ejercicio) {
        // Establecer la instrucción
        if (lblInstruccion != null) {
            lblInstruccion.setText(ejercicio.getInstruccion());
        }

        // Reiniciar estado del ejercicio
        limpiarEstadoAnterior();

        // Obtener los datos de las columnas
        ArrayList<String> itemsIzquierda = ejercicio.obtenerColumnaIzquierda();
        ArrayList<String> itemsDerecha = ejercicio.obtenerColumnaDerecha();

        // Construir mapa de emparejamientos correctos
        construirMapaEmparejamientos(itemsIzquierda, itemsDerecha, ejercicio.obtenerRespuestasCorrectas());

        // Crear botones para ambas columnas
        crearBotonesColumnaIzquierda(itemsIzquierda);
        crearBotonesColumnaDerecha(itemsDerecha);

        // Ocultar panel de feedback
        if (feedbackPanel != null) {
            feedbackPanel.setVisible(false);
        }
    }

    private void construirMapaEmparejamientos(ArrayList<String> izquierda, ArrayList<String> derecha, ArrayList<String> respuestasCorrectas) {
        emparejamientosCorrectos.clear();

        // Si las respuestas correctas contienen el formato "itemIzq|itemDer"
        for (String respuesta : respuestasCorrectas) {
            if (respuesta.contains("|")) {
                String[] partes = respuesta.split("\\|");
                if (partes.length == 2) {
                    emparejamientosCorrectos.put(partes[0], partes[1]);
                }
            }
        }

        // Si el mapa está vacío, asumir que los índices coinciden
        if (emparejamientosCorrectos.isEmpty() && izquierda.size() == derecha.size()) {
            for (int i = 0; i < Math.min(izquierda.size(), derecha.size()); i++) {
                emparejamientosCorrectos.put(izquierda.get(i), derecha.get(i));
            }
        }
    }

    private void limpiarEstadoAnterior() {
        ejercicioCompletado = false;
        emparejamientosCompletados = 0;
        emparejamientosRealizadosEnOrden.clear();
        opcionSeleccionadaIzquierda = null;
        opcionSeleccionadaDerecha = null;

        if (columnIzquierda != null) {
            columnIzquierda.getChildren().clear();
        }
        if (columnDerecha != null) {
            columnDerecha.getChildren().clear();
        }

        botonesIzquierda.clear();
        botonesDerecha.clear();
    }

    private void crearBotonesColumnaIzquierda(ArrayList<String> items) {
        if (columnIzquierda == null) return;

        for (String item : items) {
            Button boton = crearBotonOpcion(item, true);
            boton.setOnAction(e -> {
                opcionSeleccionadaIzquierda = (Button) e.getSource();
                System.out.println("Botón izquierda seleccionado: " + opcionSeleccionadaIzquierda.getText());
                seleccionarOpcionIzquierda(opcionSeleccionadaIzquierda);
            });
            columnIzquierda.getChildren().add(boton);
            botonesIzquierda.add(boton);
        }
    }

    private void crearBotonesColumnaDerecha(ArrayList<String> items) {
        if (columnDerecha == null) return;

        // Barajar los items para mayor dificultad
        List<String> itemsBarajados = new ArrayList<>(items);
        Collections.shuffle(itemsBarajados);

        for (String item : itemsBarajados) {
            Button boton = crearBotonOpcion(item, false);
            boton.setOnAction(e -> {
                opcionSeleccionadaDerecha = (Button) e.getSource();
                System.out.println("Botón derecha seleccionado: " + opcionSeleccionadaDerecha.getText());
                seleccionarOpcionDerecha(opcionSeleccionadaDerecha);
            });
            columnDerecha.getChildren().add(boton);
            botonesDerecha.add(boton);
        }
    }

    private Button crearBotonOpcion(String texto, boolean esColumnaIzquierda) {
        Button boton = new Button(texto);
        boton.setPrefWidth(140);
        boton.setPrefHeight(40);
        boton.setMaxWidth(Double.MAX_VALUE);
        boton.setWrapText(true);

        // Aplicar estilo inicial
        boton.setStyle(ESTILO_NORMAL);

        return boton;
    }

    private void seleccionarOpcionIzquierda(Button boton) {
        if (ejercicioCompletado || boton.isDisabled()) return;

        // Deseleccionar opción anterior
        if (opcionSeleccionadaIzquierda != null) {
            resetearEstiloBoton(opcionSeleccionadaIzquierda);
        }

        opcionSeleccionadaIzquierda = boton;
        aplicarEstiloSeleccionado(boton);

        // Si ya hay una opción de la derecha seleccionada, hacer el emparejamiento
        if (opcionSeleccionadaDerecha != null) {
            realizarEmparejamiento();
        }
    }

    private void seleccionarOpcionDerecha(Button boton) {
        if (ejercicioCompletado || boton.isDisabled()) return;

        // Deseleccionar opción anterior
        if (opcionSeleccionadaDerecha != null) {
            resetearEstiloBoton(opcionSeleccionadaDerecha);
        }

        opcionSeleccionadaDerecha = boton;
        aplicarEstiloSeleccionado(boton);

        // Si ya hay una opción de la izquierda seleccionada, hacer el emparejamiento
        if (opcionSeleccionadaIzquierda != null) {
            realizarEmparejamiento();
        }
    }

    private void realizarEmparejamiento() {
        String itemIzquierda = opcionSeleccionadaIzquierda.getText();
        String itemDerecha = opcionSeleccionadaDerecha.getText();

        // Verificar si el emparejamiento es correcto
        boolean esCorrecto = verificarEmparejamiento(itemIzquierda, itemDerecha);

        if (esCorrecto) {
            // Emparejamiento correcto
            aplicarEstiloCorrecto(opcionSeleccionadaIzquierda);
            aplicarEstiloCorrecto(opcionSeleccionadaDerecha);

            // Deshabilitar los botones emparejados
            opcionSeleccionadaIzquierda.setDisable(true);
            opcionSeleccionadaDerecha.setDisable(true);

            // Registrar el emparejamiento en el orden correcto para evaluación
            String emparejamientoRealizado = itemIzquierda + "|" + itemDerecha;
            emparejamientosRealizadosEnOrden.add(emparejamientoRealizado);
            emparejamientosCompletados++;

            respuestasCorrectasUsuario.add(true);
            

        } else {
            // Emparejamiento incorrecto
            aplicarEstiloIncorrecto(opcionSeleccionadaIzquierda);
            aplicarEstiloIncorrecto(opcionSeleccionadaDerecha);

            // Notificar a Lección para que gestione la resta de vidas
            if (onResultado != null) {
                onResultado.accept(new ResultadoDeEvaluacion(0)); // Cualquier <100 indica fallo
                emparejamientosIncorrectos++;
            } else {
                // Fallback: decrementar directamente si no hay callback
                //SesionManager.getInstancia().getUsuarioAutenticado().quitarVida();
            }
            actualizarVidasUI();

            respuestasCorrectasUsuario.add(false);

            Button botonIzq = opcionSeleccionadaIzquierda;
            Button botonDer = opcionSeleccionadaDerecha;
            // Resetear después de un momento para mostrar el error
            javafx.concurrent.Task<Void> resetTask = new javafx.concurrent.Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Thread.sleep(1000);
                    return null;
                }
            };

            resetTask.setOnSucceeded(e -> {
                if (!ejercicioCompletado) {
                    resetearEstiloBoton(botonDer);
                    resetearEstiloBoton(botonIzq);
                }
            });

            new Thread(resetTask).start();
        }

        // Limpiar selecciones
        opcionSeleccionadaIzquierda = null;
        opcionSeleccionadaDerecha = null;

        // Actualizar progreso
        actualizarProgressBar();

        // Verificar si se completó el ejercicio o se agotaron las vidas
        verificarEstadoDelJuego();
    }

    private boolean verificarEmparejamiento(String itemIzquierda, String itemDerecha) {
        return emparejamientosCorrectos.containsKey(itemIzquierda) &&
                emparejamientosCorrectos.get(itemIzquierda).equals(itemDerecha);
    }

    private void aplicarEstiloSeleccionado(Button boton) {
        boton.setStyle(ESTILO_SELECCIONADO);
    }

    private void aplicarEstiloCorrecto(Button boton) {
        boton.setStyle(ESTILO_CORRECTO);
    }

    private void aplicarEstiloIncorrecto(Button boton) {
        boton.setStyle(ESTILO_INCORRECTO);
    }

    private void resetearEstiloBoton(Button boton) {
        if (!boton.isDisabled()) {
            if (boton == null) {
                System.out.println("resetearEstiloBoton: botón es null, no se puede resetear.");
                return;
            }
            boton.setStyle(ESTILO_NORMAL);
        }
    }

    private void verificarEstadoDelJuego() {
        if (getVidasActuales() <= 0) {
            // Game Over - Se agotaron las vidas
            ejercicioCompletado = true;
            mostrarFeedback(TipoRespuesta.INCORRECTO, "Se han agotado las vidas.", null);
        } else if (emparejamientosCompletados >= emparejamientosCorrectos.size()) {
            // Ejercicio completado exitosamente - Se completaron todos los emparejamientos
            ejercicioCompletado = true;
            validarRespuestasFinal();
            if (onResultado != null) {
                onResultado.accept(new ResultadoDeEvaluacion(100.0 - (double)emparejamientosIncorrectos/emparejamientosCompletados * 100)); // Porcentaje de acierto
            }
            System.out.println("Emparejamientos completados: " + emparejamientosCompletados);
            System.out.println("Emparejamientos correctos: " + emparejamientosCorrectos.size());
            System.out.println("Emparejamientos incorrectos: " + emparejamientosIncorrectos);
        }
        // Si aún tiene vidas y no ha completado todos los emparejamientos, continúa el juego
    }

    private void validarRespuestasFinal() {
        if (ejercicioActual != null) {
            // Convertir los emparejamientos realizados a respuestas
            ArrayList<Respuesta> respuestasUsuario = new ArrayList<>();
            for (String emparejamiento : emparejamientosRealizadosEnOrden) {
                respuestasUsuario.add(new RespuestaString(emparejamiento));
            }

            ResultadoDeEvaluacion resultado = ejercicioActual.evaluarRespuestas(respuestasUsuario);
            double porcentajeAcierto = resultado.getPorcentajeDeAcerto();

            // Determinar el mensaje basado en el rendimiento usando el ResultadoDeEvaluacion
            
            
            String mensaje;

            if (porcentajeAcierto == 100.0) {
                mensaje = "¡Perfecto! Has emparejado todos los elementos correctamente sin errores.";
            } else if (porcentajeAcierto >= 80.0) {
                mensaje = "¡Excelente trabajo! Has completado el ejercicio con muy pocos errores.";
            } else if (porcentajeAcierto >= 60.0) {
                mensaje = "¡Buen trabajo! Has completado el ejercicio aunque con algunos errores.";
            } else {
                mensaje = "¡Lo lograste! Completaste el ejercicio, pero tuviste varios errores. ¡Sigue practicando!";
            }

            // Siempre mostrar feedback correcto cuando se completan todos los emparejamientos
            mostrarFeedback(TipoRespuesta.CORRECTO, mensaje, null);
        }
    }

    private void mostrarFeedback(TipoRespuesta tipo, String mensaje, String puntuacion) {
        // Ocultar todos los paneles primero
        ocultarTodosPanelesFeedback();

    // Solo dos casos posibles: CORRECTO (completó todos los emparejamientos) o INCORRECTO (sin vidas u error)
        switch (tipo) {
            case CORRECTO:
                mostrarPanelCorrecto(mensaje);
                break;
            case INCORRECTO:
        // Mostrar SIEMPRE feedback de incorrecto (también cuando se agotan las vidas)
        mostrarPanelIncorrecto(mensaje);
                break;
            default:
                mostrarPanelCorrecto(mensaje);
                break;
        }

        // Configurar y mostrar el botón siguiente
        configurarBotonSiguiente();

        // Mostrar el panel de feedback
        if (feedbackPanel != null) {
            feedbackPanel.setVisible(true);
        }

        // Si es game over, tras una breve pausa abrir modal y volver a la vista final
        if (tipo == TipoRespuesta.INCORRECTO && getVidasActuales() <= 0) {
            // Asegurar que el botón no se muestre en Game Over
            if (btnSiguienteCompleto != null) {
                btnSiguienteCompleto.setDisable(true);
                btnSiguienteCompleto.setVisible(false);
                btnSiguienteCompleto.setManaged(false);
            }

            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
            pause.setOnFinished(e -> {
                javafx.stage.Stage stage = null;
                if (feedbackPanel != null && feedbackPanel.getScene() != null) {
                    stage = (javafx.stage.Stage) feedbackPanel.getScene().getWindow();
                } else if (progressBar != null && progressBar.getScene() != null) {
                    stage = (javafx.stage.Stage) progressBar.getScene().getWindow();
                } else if (btnSiguiente != null && btnSiguiente.getScene() != null) {
                    stage = (javafx.stage.Stage) btnSiguiente.getScene().getWindow();
                } else if (btnSiguienteCompleto != null && btnSiguienteCompleto.getScene() != null) {
                    stage = (javafx.stage.Stage) btnSiguienteCompleto.getScene().getWindow();
                }
                if (stage != null) {
                    Nuevo_Modulo_Leccion.controllers.LeccionUIController.mostrarSeAcabaronVidasYVolver(stage);
                }
            });
            pause.play();
        }
    }

    private void ocultarTodosPanelesFeedback() {
        if (panelCorrecto != null) panelCorrecto.setVisible(false);
        if (panelParcial != null) panelParcial.setVisible(false);
        if (panelIncorrecto != null) panelIncorrecto.setVisible(false);
        if (panelGameOver != null) panelGameOver.setVisible(false);
    }

    private void mostrarPanelCorrecto(String mensaje) {
        if (panelCorrecto != null) {
            panelCorrecto.setVisible(true);
            if (textCorrecto != null) {
                textCorrecto.setText("¡Excelente!");
            }
            if (textCorrectoDetalle != null) {
                textCorrectoDetalle.setText(mensaje != null ? mensaje : "¡Has emparejado todos los elementos correctamente!");
            }
        }
    }

    // Eliminado: no se usa flujo parcial en emparejar actualmente

    private void mostrarPanelIncorrecto(String mensaje) {
        if (panelIncorrecto != null) {
            panelIncorrecto.setVisible(true);
            if (textIncorrecto != null) {
                textIncorrecto.setText("¡Inténtalo otra vez!");
            }
            if (textIncorrectoDetalle != null) {
                textIncorrectoDetalle.setText(mensaje != null ? mensaje : "Los emparejamientos no son correctos. Revisa las conexiones.");
            }
        }
    }

    // Eliminado: se usa panelIncorrecto también para game over y se navega tras pausa

    private void configurarBotonSiguiente() {
        if (btnSiguienteCompleto != null) {
            if (getVidasActuales() <= 0) {
                // Game Over: no mostrar botón, se manejará con pausa y modal
                btnSiguienteCompleto.setDisable(true);
                btnSiguienteCompleto.setVisible(false);
                btnSiguienteCompleto.setManaged(false);
            } else {
                btnSiguienteCompleto.setDisable(false);
                btnSiguienteCompleto.setVisible(true);
                btnSiguienteCompleto.setManaged(true);
                btnSiguienteCompleto.setText("SIGUIENTE");
            }
        }
    }

    @FXML
    private void handleContinuar() {
        // Continuar al siguiente ejercicio
        if (feedbackPanel != null) {
            feedbackPanel.setVisible(false);
        }
        cerrarVentanaYAvanzar();
    }

    private void cerrarVentanaYAvanzar() {
        try {
            Nuevo_Modulo_Leccion.controllers.LeccionUIController.avanzarAlSiguienteEjercicio();

            if (btnSiguiente != null && btnSiguiente.getScene() != null) {
                btnSiguiente.getScene().getWindow().hide();
            } else if (btnSiguienteCompleto != null && btnSiguienteCompleto.getScene() != null) {
                btnSiguienteCompleto.getScene().getWindow().hide();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualiza la barra de progreso basada en el progreso de la lección
     * Hay que corregir esto, debe ser por el numero de ejercicos completados en la leccion
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
            // Fallback: progreso basado en emparejamientos completados
            if (progressBar != null && emparejamientosCorrectos.size() > 0) {
                double progreso = (double) emparejamientosCompletados / emparejamientosCorrectos.size();
                progressBar.setProgress(progreso);
            }
        }
    }

    private void actualizarVidasUI() {
        if (txtLiveCount != null) {
            txtLiveCount.setText(String.valueOf(getVidasActuales()));
        }
    }

    private int getVidasActuales() {
        return SesionManager.getInstancia().getUsuarioAutenticado().getVidas();
    }
}