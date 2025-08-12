package Gamificacion_Modulo.controllers_admin;

import Gamificacion_Modulo.clases.*;
import Gamificacion_Modulo.utils.GestorGamificacion;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import Modulo_Usuario.Clases.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SimularActividadController implements Initializable {

    @FXML private ComboBox<String> cbUsuarios;
    @FXML private Label lblInfoUsuario;
    @FXML private VBox vboxDesafiosActivos;
    @FXML private Label lblDesafioSeleccionado;
    @FXML private Spinner<Integer> spinnerActividades;
    @FXML private VBox vboxResultados;
    @FXML private Button btnCancelar;
    @FXML private Button btnSimular;

    private List<Usuario> usuarios;
    private List<RadioButton> radioButtonsDesafios;
    private ToggleGroup toggleGroupDesafios;
    private Usuario usuarioSeleccionado;
    private Desafio desafioSeleccionado;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usuarios = GestorGamificacion.getUsuariosEstudiantes();
        radioButtonsDesafios = new ArrayList<>();
        toggleGroupDesafios = new ToggleGroup();

        configurarSpinner();
        cargarUsuarios();
        configurarEventos();

        System.out.println(">>> Simulador de actividades inicializado");
        System.out.println(">>> Usuarios estudiantes disponibles: " + usuarios.size());
        System.out.println(">>> Desafíos disponibles en sistema: " + Desafio.getDesafiosDisponibles().size());
    }

    private void configurarSpinner() {
        spinnerActividades.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1));
        spinnerActividades.setEditable(true);
    }

    private void cargarUsuarios() {
        cbUsuarios.getItems().clear();
        for (int i = 0; i < usuarios.size(); i++) {
            Usuario usuario = usuarios.get(i);
            cbUsuarios.getItems().add((i+1) + " - " + usuario.getNombre());
        }
    }

    private void configurarEventos() {
        // Evento al seleccionar usuario
        cbUsuarios.setOnAction(e -> {
            String seleccion = cbUsuarios.getValue();
            if (seleccion != null) {
                int indice = Integer.parseInt(seleccion.split(" - ")[0]) - 1;
                usuarioSeleccionado = usuarios.get(indice);
                if (usuarioSeleccionado != null) {
                    actualizarInfoUsuario();
                    cargarDesafiosActivos();
                    validarFormulario();
                }
            }
        });

        // Evento al seleccionar desafío
        toggleGroupDesafios.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                desafioSeleccionado = (Desafio) newToggle.getUserData();
                actualizarDesafioSeleccionado();
                validarFormulario();
            }
        });

        // Evento del spinner
        spinnerActividades.valueProperty().addListener((obs, oldVal, newVal) -> validarFormulario());
    }

    private void actualizarInfoUsuario() {
        if (usuarioSeleccionado != null) {
            ProgresoEstudiante progreso = encontrarProgresoPorUsuario(usuarioSeleccionado);
            if (progreso != null) {
                lblInfoUsuario.setText(String.format(
                        "👤 Usuario: %s | 💎 Puntos: %d | 🏆 Logros: %d | 🎯 Desafíos activos: %d",
                        usuarioSeleccionado.getNombre(),
                        progreso.getPuntosTotal(),
                        progreso.getLogros().size(),
                        progreso.getDesafiosActivos().size()
                ));
                lblInfoUsuario.setStyle("-fx-text-fill: #4CAF50;");
            }
        }
    }

    private void cargarDesafiosActivos() {
        vboxDesafiosActivos.getChildren().clear();
        radioButtonsDesafios.clear();

        if (usuarioSeleccionado != null) {
            ProgresoEstudiante progreso = encontrarProgresoPorUsuario(usuarioSeleccionado);
            if (progreso != null) {
                List<Desafio> activos = progreso.getDesafiosActivos();

                if (activos.isEmpty()) {
                    Label lblVacio = new Label("Este usuario no tiene desafíos activos");
                    lblVacio.setStyle("-fx-text-fill: #666666;");
                    vboxDesafiosActivos.getChildren().add(lblVacio);

                    Label lblInfo = new Label("Usa 'Asignar Desafío a Usuario' para asignar desafíos");
                    lblInfo.setStyle("-fx-text-fill: #888888; -fx-font-size: 11px;");
                    vboxDesafiosActivos.getChildren().add(lblInfo);
                } else {
                    for (Desafio desafio : activos) {
                        String tipo = desafio instanceof DesafioSemanal ? "Semanal" : "Mensual";
                        String progreso_str = "";

                        if (desafio instanceof DesafioSemanal) {
                            DesafioSemanal ds = (DesafioSemanal) desafio;
                            progreso_str = " - Progreso: " + ds.getLeccionesCompletadas() + "/" + ds.getMetaSemanal();
                        } else if (desafio instanceof DesafioMensual) {
                            DesafioMensual dm = (DesafioMensual) desafio;
                            progreso_str = " - Progreso: " + dm.getLeccionesCompletadas() + "/" + dm.getMetaMensual();
                        }

                        RadioButton rb = new RadioButton();
                        rb.setToggleGroup(toggleGroupDesafios);
                        rb.setText("🎯 " + " (" + tipo + ")" + progreso_str);
                        rb.setWrapText(true);
                        rb.setUserData(desafio);

                        radioButtonsDesafios.add(rb);
                        vboxDesafiosActivos.getChildren().add(rb);
                    }
                }
            }
        }
    }

    private void actualizarDesafioSeleccionado() {
        if (desafioSeleccionado != null) {
            String tipo = desafioSeleccionado instanceof DesafioSemanal ? "Semanal" : "Mensual";
            lblDesafioSeleccionado.setText("✅ Desafío seleccionado: ");
            lblDesafioSeleccionado.setStyle("-fx-text-fill: #4CAF50;");
        } else {
            lblDesafioSeleccionado.setText("Selecciona un desafío de la lista");
            lblDesafioSeleccionado.setStyle("-fx-text-fill: #666666;");
        }
    }

    private void validarFormulario() {
        boolean valid = usuarioSeleccionado != null &&
                desafioSeleccionado != null &&
                spinnerActividades.getValue() > 0;
        btnSimular.setDisable(!valid);
    }

    @FXML
    private void onSimularClicked(ActionEvent event) {
        try {
            if (usuarioSeleccionado == null) {
                mostrarError("Selecciona un usuario");
                return;
            }

            if (desafioSeleccionado == null) {
                mostrarError("Selecciona un desafío");
                return;
            }

            int actividades = spinnerActividades.getValue();
            if (actividades <= 0) {
                mostrarError("El número de actividades debe ser mayor a 0");
                return;
            }

            ProgresoEstudiante progreso = encontrarProgresoPorUsuario(usuarioSeleccionado);
            if (progreso == null) {
                mostrarError("No se encontró el progreso del usuario");
                return;
            }

            // Simular completar actividades
            StringBuilder resultados = new StringBuilder();
            resultados.append("🎮 SIMULACIÓN DE ACTIVIDADES\n");
            resultados.append("===========================\n\n");
            resultados.append("👤 Usuario: ").append(usuarioSeleccionado.getNombre()).append("\n");
            resultados.append("📊 Actividades completadas: ").append(actividades).append("\n\n");

            // Progreso antes
            int puntosAntes = progreso.getPuntosTotal();
            int logrosAntes = progreso.getLogros().size();

            // Completar actividades en el desafío
            if (desafioSeleccionado instanceof DesafioSemanal) {
                ((DesafioSemanal) desafioSeleccionado).actualizarAvance(actividades);
            } else if (desafioSeleccionado instanceof DesafioMensual) {
                ((DesafioMensual) desafioSeleccionado).actualizarAvance(actividades);
            }

            // Evaluar progreso y logros
            progreso.actualizarProgreso(desafioSeleccionado);

            // Progreso después
            int puntosDespues = progreso.getPuntosTotal();
            int logrosDespues = progreso.getLogros().size();

            // Mostrar resultados
            resultados.append("📈 RESULTADOS:\n");
            resultados.append("• Puntos ganados: +").append(puntosDespues - puntosAntes).append("\n");
            resultados.append("• Logros obtenidos: +").append(logrosDespues - logrosAntes).append("\n");

            // Verificar si el desafío se completó
            if (desafioSeleccionado.estaCompletado()) {
                resultados.append("🎉 ¡DESAFÍO COMPLETADO!\n");
                resultados.append("• ").append("CC nombre desafio").append(" se ha completado exitosamente\n");
            }

            // Mostrar progreso del desafío
            if (desafioSeleccionado instanceof DesafioSemanal) {
                DesafioSemanal ds = (DesafioSemanal) desafioSeleccionado;
                resultados.append("• Progreso semanal: ").append(ds.getLeccionesCompletadas())
                        .append("/").append(ds.getMetaSemanal())
                        .append(" (").append(String.format("%.1f", ds.getProgreso())).append("%)\n");
            } else if (desafioSeleccionado instanceof DesafioMensual) {
                DesafioMensual dm = (DesafioMensual) desafioSeleccionado;
                resultados.append("• Progreso mensual: ").append(dm.getLeccionesCompletadas())
                        .append("/").append(dm.getMetaMensual())
                        .append(" (").append(String.format("%.1f", dm.getProgreso())).append("%)\n");
            }

            // Mostrar logros nuevos si los hay
            if (logrosDespues > logrosAntes) {
                resultados.append("\n🏆 NUEVOS LOGROS OBTENIDOS:\n");
                List<Logro> logrosActuales = progreso.getLogros();
                for (int i = logrosAntes; i < logrosDespues; i++) {
                    if (i < logrosActuales.size()) {
                        Logro logro = logrosActuales.get(i);
                        resultados.append("• ").append(logro.getNombre())
                                .append(" (+").append("CC 200").append(" pts)\n");
                    }
                }
            }


            // Actualizar información y desafíos
            actualizarInfoUsuario();
            cargarDesafiosActivos();

            // Limpiar selección
            toggleGroupDesafios.selectToggle(null);
            actualizarDesafioSeleccionado();
            spinnerActividades.getValueFactory().setValue(1);

            // Mostrar resultados
            mostrarResultados("Simulación Completada", resultados.toString());

            System.out.println(">>> Simulación completada para " + usuarioSeleccionado.getNombre());

        } catch (Exception e) {
            System.err.println("Error en simulación: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error durante la simulación: " + e.getMessage());
        }
    }

    @FXML
    private void onCancelarClicked(ActionEvent event) {
        System.out.println(">>> Cancelando simulación de actividad");
        cerrarVentana();
    }

    private ProgresoEstudiante encontrarProgresoPorUsuario(Usuario usuario) {
        return Ranking.getProgresos().stream()
                .filter(p -> p.getUsuario().getUsername().equals(usuario.getUsername()))
                .findFirst()
                .orElse(null);
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarResultados(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
