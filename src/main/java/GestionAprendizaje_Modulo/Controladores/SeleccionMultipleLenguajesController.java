package GestionAprendizaje_Modulo.Controladores;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Conexion.SesionManager;
import GestionAprendizaje_Modulo.Logica.ConfiguracionUsuarioService;
import Modulo_Usuario.Clases.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SeleccionMultipleLenguajesController {

    @FXML private VBox lenguajesContainer;
    @FXML private Button btnContinuar;
    @FXML private Button btnVolver;

    private Map<String, CheckBox> checkBoxLenguajes = new HashMap<>();
    private Map<String, ComboBox<String>> comboBoxNiveles = new HashMap<>();

    @FXML
    private void initialize() {
        crearOpcionesLenguajes();
        configurarBotones();
        deshabilitarLenguajesExistentes(); // Nuevo método para deshabilitar lenguajes ya existentes
    }

    private void crearOpcionesLenguajes() {
        String[] lenguajes = {"Java", "Python", "PHP", "C"};
        String[] niveles = {"Básico", "Intermedio", "Avanzado"};

        for (String lenguaje : lenguajes) {
            // Crear container para cada lenguaje
            VBox lenguajeContainer = new VBox(10);
            lenguajeContainer.setStyle("-fx-padding: 15; -fx-background-color: #FFFFFF1A; -fx-background-radius: 10;");

            // CheckBox para seleccionar el lenguaje
            CheckBox checkBox = new CheckBox(lenguaje);
            checkBox.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
            checkBoxLenguajes.put(lenguaje, checkBox);

            // ComboBox para seleccionar el nivel
            ComboBox<String> comboBox = new ComboBox<>();
            comboBox.getItems().addAll(niveles);
            comboBox.setValue("Básico"); // Valor por defecto
            comboBox.setStyle("-fx-font-size: 14px;");
            comboBox.setPrefWidth(150);
            comboBox.setDisable(true); // Inicialmente deshabilitado
            comboBoxNiveles.put(lenguaje, comboBox);

            // Label para el nivel
            Label labelNivel = new Label("Nivel:");
            labelNivel.setStyle("-fx-text-fill: #A6B1E1; -fx-font-size: 14px;");

            // Container horizontal para nivel
            HBox nivelContainer = new HBox(10);
            nivelContainer.getChildren().addAll(labelNivel, comboBox);

            // Agregar todo al container del lenguaje
            lenguajeContainer.getChildren().addAll(checkBox, nivelContainer);

            // Configurar evento del checkbox
            checkBox.setOnAction(event -> {
                comboBox.setDisable(!checkBox.isSelected());
                if (!checkBox.isSelected()) {
                    comboBox.setValue("Básico"); // Reset al valor por defecto
                }
            });

            lenguajesContainer.getChildren().add(lenguajeContainer);
        }
    }

    // Nuevo método para deshabilitar lenguajes que el usuario ya tiene
    private void deshabilitarLenguajesExistentes() {
        try {
            Usuario usuarioActual = SesionManager.getInstancia().getUsuarioAutenticado();
            if (usuarioActual != null) {
                ConfiguracionUsuarioService.ConfiguracionUsuario config =
                    ConfiguracionUsuarioService.getInstancia().obtenerConfiguracion(usuarioActual.getUsername());

                if (config != null && !config.getLenguajes().isEmpty()) {
                    List<String> lenguajesExistentes = config.getLenguajes();

                    // Deshabilitar checkboxes de lenguajes ya existentes
                    for (String lenguajeExistente : lenguajesExistentes) {
                        CheckBox checkBox = checkBoxLenguajes.get(lenguajeExistente);
                        ComboBox<String> comboBox = comboBoxNiveles.get(lenguajeExistente);

                        if (checkBox != null) {
                            checkBox.setSelected(false);
                            checkBox.setDisable(true);
                            checkBox.setText(lenguajeExistente + " (Ya configurado)");
                            checkBox.setStyle("-fx-text-fill: #888888; -fx-font-size: 16px; -fx-opacity: 0.6;");

                            if (comboBox != null) {
                                comboBox.setDisable(true);
                                comboBox.setValue(config.getNivelParaLenguaje(lenguajeExistente));
                                comboBox.setStyle("-fx-opacity: 0.6;");
                            }
                        }
                    }

                    System.out.println("[SeleccionMultiple] Lenguajes ya configurados: " + lenguajesExistentes);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al verificar lenguajes existentes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarBotones() {
        // Acción para el botón de "Volver"
        btnVolver.setOnAction(event -> {
            try {
                // Cargar el archivo FXML de la vista HomeUsuario (siempre volver al home)
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionAprendizaje_Modulo/Vistas/Ruta.fxml"));
                AnchorPane homePane = loader.load(); // Cargar la vista de home

                // Obtener el stage actual y cambiar la escena
                Stage stage = (Stage) btnVolver.getScene().getWindow();
                stage.setScene(new Scene(homePane)); // Cambiar la escena
                stage.show(); // Mostrar la nueva escena
            } catch (IOException e) {
                e.printStackTrace(); // Mostrar el error si algo falla
            }
        });
        btnContinuar.setOnAction(event -> {
            List<String> lenguajesSeleccionados = new ArrayList<>();
            List<String> nivelesSeleccionados = new ArrayList<>();

            // Recopilar lenguajes y niveles seleccionados
            for (Map.Entry<String, CheckBox> entry : checkBoxLenguajes.entrySet()) {
                String lenguaje = entry.getKey();
                CheckBox checkBox = entry.getValue();

                if (checkBox.isSelected()) {
                    lenguajesSeleccionados.add(lenguaje);
                    String nivel = comboBoxNiveles.get(lenguaje).getValue();
                    nivelesSeleccionados.add(nivel);
                }
            }

            // Validar que se haya seleccionado al menos un lenguaje
            if (lenguajesSeleccionados.isEmpty()) {
                Alert alerta = new Alert(Alert.AlertType.WARNING);
                alerta.setTitle("Sin selección");
                alerta.setHeaderText(null);
                alerta.setContentText("Por favor, selecciona al menos un lenguaje antes de continuar.");
                alerta.showAndWait();
                return;
            }

            // Guardar la configuración múltiple AGREGANDO a los existentes
            try {
                Usuario usuarioActual = SesionManager.getInstancia().getUsuarioAutenticado();
                if (usuarioActual != null) {
                    // Usar el nuevo método que AGREGA lenguajes en lugar de reemplazarlos
                    ConfiguracionUsuarioService.getInstancia().agregarLenguajesAlUsuario(
                            usuarioActual.getUsername(),
                            lenguajesSeleccionados,
                            nivelesSeleccionados
                    );
                    System.out.println("Lenguajes agregados para: " + usuarioActual.getUsername());
                    System.out.println("Nuevos lenguajes: " + lenguajesSeleccionados);
                    System.out.println("Nuevos niveles: " + nivelesSeleccionados);

                    // Establecer el primer lenguaje como el actual para el DiagnosticoController
                    DiagnosticoController.lenguajeSeleccionado = lenguajesSeleccionados.get(0);
                    DiagnosticoController.nivelSeleccionado = nivelesSeleccionados.get(0);
                }
            } catch (Exception e) {
                System.err.println("Error al agregar lenguajes: " + e.getMessage());
                e.printStackTrace();
            }

            // Ir directamente a la vista de Mis Lenguajes para mostrar todos los lenguajes
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionAprendizaje_Modulo/Vistas/MisLenguajes.fxml"));
                AnchorPane misLenguajesPane = loader.load();
                Stage stage = (Stage) btnContinuar.getScene().getWindow();
                stage.setScene(new Scene(misLenguajesPane));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
