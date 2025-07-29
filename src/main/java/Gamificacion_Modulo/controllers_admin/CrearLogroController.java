package Gamificacion_Modulo.controllers_admin;

import Gamificacion_Modulo.clases.Logro;
import Gamificacion_Modulo.clases.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CrearLogroController {
    
    @FXML
    private TextField txtNombre;
    
    @FXML
    private TextArea txtDescripcion;
    
    @FXML
    private Spinner<Integer> spinnerPuntosRequeridos;
    
    @FXML
    private Slider sliderPuntos;
    
    @FXML
    private Label lblPuntos;
    
    @FXML
    private VBox vboxVistaPrevia;
    
    @FXML
    private Button btnCancelar;
    
    @FXML
    private Button btnCrear;
    
    @FXML
    public void initialize() {
        configurarSpinners();
        configurarSlider();
        configurarListeners();
        actualizarVistaPrevia();
    }
    
    private void configurarSpinners() {
        // Configurar valores del spinner de puntos requeridos
        spinnerPuntosRequeridos.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 10000, 500, 100));
        
        // Hacer que el spinner sea editable
        spinnerPuntosRequeridos.setEditable(true);
    }
    
    private void configurarSlider() {
        sliderPuntos.setValue(150);
        lblPuntos.setText("150");
        
        sliderPuntos.valueProperty().addListener((obs, oldVal, newVal) -> {
            lblPuntos.setText(String.valueOf(newVal.intValue()));
            actualizarVistaPrevia();
        });
    }
    
    private void configurarListeners() {
        // Listeners para actualización en tiempo real
        txtNombre.textProperty().addListener((obs, oldVal, newVal) -> actualizarVistaPrevia());
        txtDescripcion.textProperty().addListener((obs, oldVal, newVal) -> actualizarVistaPrevia());
        
        spinnerPuntosRequeridos.valueProperty().addListener((obs, oldVal, newVal) -> actualizarVistaPrevia());
    }
    
    private void actualizarVistaPrevia() {
        vboxVistaPrevia.getChildren().clear();
        
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        int puntosRecompensa = (int) sliderPuntos.getValue();
        
        if (nombre.isEmpty() && descripcion.isEmpty()) {
            Label lblInfo = new Label("Completa los campos para ver la vista previa");
            lblInfo.setStyle("-fx-text-fill: #6c757d;");
            vboxVistaPrevia.getChildren().add(lblInfo);
            return;
        }
        
        // Título del logro
        Label lblTitulo = new Label("🏆 " + (nombre.isEmpty() ? "Nombre del Logro" : nombre));
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");
        
        // Descripción
        Label lblDesc = new Label("📝 " + (descripcion.isEmpty() ? "Descripción del logro" : descripcion));
        lblDesc.setWrapText(true);
        lblDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: #495057;");
        
        // Criterio
        String criterio = obtenerCriterioTexto();
        Label lblCriterio = new Label("🎯 Criterio: " + criterio);
        lblCriterio.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");
        
        // Puntos
        Label lblPuntosInfo = new Label("💎 Recompensa: +" + puntosRecompensa + " puntos");
        lblPuntosInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: #28a745; -fx-font-weight: bold;");
        
        vboxVistaPrevia.getChildren().addAll(lblTitulo, lblDesc, lblCriterio, lblPuntosInfo);
    }
    
    private String obtenerCriterioTexto() {
        return "Obtener " + spinnerPuntosRequeridos.getValue() + " puntos";
    }
    
    @FXML
    private void onCancelarClicked(ActionEvent event) {
        System.out.println(">>> Cancelando creación de logro");
        cerrarVentana();
    }
    
    @FXML
    private void onCrearClicked(ActionEvent event) {
        try {
            // Validaciones
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) {
                mostrarAlerta("Error", "El nombre del logro no puede estar vacío");
                return;
            }
            
            String descripcion = txtDescripcion.getText().trim();
            if (descripcion.isEmpty()) {
                mostrarAlerta("Error", "La descripción del logro no puede estar vacía");
                return;
            }
            
            // Verificar que no existe un logro con el mismo nombre
            for (Logro logro : Main.getLogrosDisponibles()) {
                if (logro.getNombre().equalsIgnoreCase(nombre)) {
                    mostrarAlerta("Error", "Ya existe un logro con el nombre '" + nombre + "'");
                    return;
                }
            }
            
            // Crear el logro con el puntaje umbral requerido
            int puntajeUmbral = spinnerPuntosRequeridos.getValue();
            int puntos = (int) sliderPuntos.getValue();
            
            Logro nuevoLogro = new Logro(nombre, descripcion, puntajeUmbral);
            Main.getLogrosDisponibles().add(nuevoLogro);
            
            // Mensaje de éxito
            String mensaje = String.format(
                "¡Logro creado exitosamente!\n\n" +
                "Nombre: %s\n" +
                "Descripción: %s\n" +
                "Criterio: %s\n" +
                "Recompensa: +%d puntos",
                nombre, descripcion, obtenerCriterioTexto(), puntos
            );
            
            mostrarAlerta("Éxito", mensaje);
            
            System.out.println(">>> Logro personalizado creado!");
            System.out.println("* " + nombre + " - " + descripcion);
            System.out.println("  Criterio: Obtener " + puntajeUmbral + " puntos | Recompensa: +" + puntos + " pts");
            
            cerrarVentana();
            
        } catch (Exception e) {
            System.err.println("Error al crear logro: " + e.getMessage());
            mostrarAlerta("Error", "Error al crear el logro: " + e.getMessage());
        }
    }
    
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(titulo.equals("Error") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
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