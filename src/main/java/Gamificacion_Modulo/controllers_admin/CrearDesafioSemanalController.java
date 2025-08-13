package Gamificacion_Modulo.controllers_admin;

import java.util.ArrayList;
import java.util.List;

import Gamificacion_Modulo.clases.Desafio;
import Gamificacion_Modulo.clases.DesafioSemanal;
import Gamificacion_Modulo.clases.Logro;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CrearDesafioSemanalController {

    @FXML
    private Slider sliderMeta;

    @FXML
    private Label lblMeta;

    @FXML
    private VBox vboxLogros;

    @FXML
    private Label lblLogrosSeleccionados;

    @FXML
    private VBox vboxVistaPrevia;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnCrear;

    @FXML
    private Slider sliderPuntos;

    @FXML
    private Label lblPuntos;

    private List<CheckBox> checkBoxesLogros = new ArrayList<>();
    private List<Logro> logrosSeleccionados = new ArrayList<>();

    @FXML
    public void initialize() {
        configurarSlider();
        cargarLogrosDisponibles();
        actualizarVistaPrevia();

        // Listener para actualización en tiempo real del slider
        sliderMeta.valueProperty().addListener((obs, oldVal, newVal) -> {
            lblMeta.setText(String.valueOf(newVal.intValue()));
            actualizarVistaPrevia();
        });

        if (sliderPuntos != null) {
            sliderPuntos.valueProperty().addListener((o, ov, nv) -> {
                if (lblPuntos != null) lblPuntos.setText(String.valueOf(nv.intValue()));
                actualizarVistaPrevia();
            });
            if (lblPuntos != null) lblPuntos.setText(String.valueOf((int) sliderPuntos.getValue()));
        }
    }

    private void configurarSlider() {
        sliderMeta.setValue(5); // Valor por defecto
        lblMeta.setText("5");
    }

    private void cargarLogrosDisponibles() {
        try {
            vboxLogros.getChildren().clear();
            checkBoxesLogros.clear();

            List<Logro> logrosDisponibles = Logro.getLogrosDisponibles();

            for (Logro logro : logrosDisponibles) {
                CheckBox checkBox = new CheckBox();
                checkBox.setText(logro.getNombre() + " - " + logro.getDescripcion()) ;
                checkBox.setUserData(logro);
                checkBox.setWrapText(true);

                // Listener para actualizar lista de logros seleccionados
                checkBox.setOnAction(e -> {
                    actualizarLogrosSeleccionados();
                    actualizarVistaPrevia();
                });

                checkBoxesLogros.add(checkBox);
                vboxLogros.getChildren().add(checkBox);
            }

            System.out.println(">>> Logros disponibles cargados: " + logrosDisponibles.size());

        } catch (Exception e) {
            System.err.println("Error al cargar logros: " + e.getMessage());
        }
    }

    private void actualizarLogrosSeleccionados() {
        logrosSeleccionados.clear();

        for (CheckBox checkBox : checkBoxesLogros) {
            if (checkBox.isSelected()) {
                Logro logro = (Logro) checkBox.getUserData();
                logrosSeleccionados.add(logro);
            }
        }

        lblLogrosSeleccionados.setText(logrosSeleccionados.size() + " logros seleccionados");
    }

    private void actualizarVistaPrevia() {
        vboxVistaPrevia.getChildren().clear();

        int meta = (int) sliderMeta.getValue();

        Label lblNombre = new Label("📋 Desafío Semanal");
        lblNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblDescripcion = new Label("📝 Descripción: Completar " + meta + " actividades en la semana");
        Label lblMetaInfo = new Label("🎯 Meta: " + meta + " actividades semanales");
        Label lblLogros = new Label("🏆 Logros asociados: " + logrosSeleccionados.size());
        Label lblEstado = new Label("📊 Estado: Disponible para asignar");
        int recompensa = sliderPuntos != null ? (int) sliderPuntos.getValue() : 0;
        Label lblRecompensa = new Label("💎 Recompensa: +" + recompensa + " pts");

        vboxVistaPrevia.getChildren().addAll(lblNombre, lblDescripcion, lblMetaInfo, lblLogros, lblRecompensa, lblEstado);

        // Mostrar logros seleccionados
        for (Logro logro : logrosSeleccionados) {
            Label lblLogro = new Label("   • " + logro.getNombre() );
            lblLogro.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 12px;");
            vboxVistaPrevia.getChildren().add(lblLogro);
        }

        if (logrosSeleccionados.isEmpty()) {
            Label lblSinLogros = new Label("   (Sin logros asociados)");
            lblSinLogros.setStyle("-fx-text-fill: #dc3545; -fx-font-size: 12px;");
            vboxVistaPrevia.getChildren().add(lblSinLogros);
        }
    }

    @FXML
    private void onCancelarClicked(ActionEvent event) {
        System.out.println(">>> Cancelando creación de desafío semanal");
        cerrarVentana();
    }

    @FXML
    private void onCrearClicked(ActionEvent event) {
        try {
            // Validaciones básicas
            int meta = (int) sliderMeta.getValue();

            if (meta < 1 || meta > 20) {
                mostrarAlerta("Error", "La meta debe estar entre 1 y 20 actividades");
                return;
            }

            if (logrosSeleccionados.isEmpty()) {
                boolean continuar = mostrarConfirmacion("Advertencia",
                        "¿Estás seguro de crear un desafío sin logros asociados?");
                if (!continuar) {
                    return;
                }
            }

            // Crear el desafío semanal
            int recompensa = sliderPuntos != null ? (int) sliderPuntos.getValue() : 0;
            DesafioSemanal desafio = new DesafioSemanal(meta, recompensa, new ArrayList<>(logrosSeleccionados));

            // Agregar a la lista central de desafíos
            Desafio.agregarDesafio(desafio);

            // Asignar automáticamente el desafío a todos los usuarios logueados (estudiantes)
            List<Modulo_Usuario.Clases.Usuario> usuarios = Gamificacion_Modulo.utils.GestorGamificacion.getUsuariosEstudiantes();
            for (Modulo_Usuario.Clases.Usuario usuario : usuarios) {
                Gamificacion_Modulo.clases.ProgresoEstudiante progreso = Gamificacion_Modulo.clases.Ranking.getProgresos().stream()
                        .filter(p -> p.getUsuario().getUsername().equals(usuario.getUsername()))
                        .findFirst().orElse(null);
                if (progreso != null) {
                    progreso.actualizarDesafios(new DesafioSemanal(desafio));

                }
            }
            System.out.println(">>> Desafío semanal asignado automáticamente a todos los usuarios logueados");

            // Mensaje de éxito
            String mensaje = String.format(
                    "¡Desafío semanal creado exitosamente!\n\n" +
                            "Meta: %d actividades semanales\n" +
                            "Logros asociados: %d\n" +
                            "Recompensa: +%d pts\n",
                    meta,
                    logrosSeleccionados.size(),
                    recompensa
            );

            mostrarAlerta("Éxito", mensaje);

            cerrarVentana();

        } catch (Exception e) {
            System.err.println("Error al crear desafío semanal: " + e.getMessage());
            mostrarAlerta("Error", "Error al crear el desafío: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(titulo.equals("Error") ? Alert.AlertType.ERROR :
                titulo.equals("Advertencia") ? Alert.AlertType.WARNING :
                        Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private boolean mostrarConfirmacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}