package GestionAprendizaje_Modulo.Controladores;

import java.io.IOException;

import Conexion.SesionManager;
import Modulo_Usuario.Clases.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class DiagnosticoController {

    @FXML private Label tituloLabel;
    @FXML private RadioButton rbNivelBasico;
    @FXML private RadioButton rbNivelIntermedio;
    @FXML private RadioButton rbNivelAvanzado;
    @FXML private Button btnContinuar;

    private ToggleGroup nivelGroup;

    public static String nivelSeleccionado = null;
    public static String lenguajeSeleccionado = null;

    @FXML
    private void initialize() {
        nivelGroup = new ToggleGroup();
        rbNivelBasico.setToggleGroup(nivelGroup);
        rbNivelIntermedio.setToggleGroup(nivelGroup);
        rbNivelAvanzado.setToggleGroup(nivelGroup);

        btnContinuar.setOnAction(event -> {
            RadioButton seleccionado = (RadioButton) nivelGroup.getSelectedToggle();
            if (seleccionado == null) {
                Alert alerta = new Alert(Alert.AlertType.WARNING);
                alerta.setTitle("Nivel no seleccionado");
                alerta.setHeaderText(null);
                alerta.setContentText("Por favor, selecciona tu nivel de conocimiento antes de continuar.");
                alerta.showAndWait();
                return;
            }

            String nivel = seleccionado.getText();
            nivelSeleccionado = nivel;

            try {
                Usuario usuarioActual = SesionManager.getInstancia().getUsuarioAutenticado();
                if (usuarioActual != null && lenguajeSeleccionado != null) {
                    ConfiguracionUsuarioService.getInstancia().guardarConfiguracion(
                            usuarioActual.getUsername(),
                            lenguajeSeleccionado,
                            nivel
                    );
                    System.out.println("Configuración guardada para: " + usuarioActual.getUsername());
                }
            } catch (Exception e) {
                System.err.println("Error al guardar configuración: " + e.getMessage());
                e.printStackTrace();
            }

            // Ir DIRECTAMENTE a la Ruta
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/GestionAprendizaje_Modulo/Vistas/Ruta.fxml")
                );
                AnchorPane rutaPane = loader.load();
                Stage stage = (Stage) btnContinuar.getScene().getWindow();
                stage.setScene(new Scene(rutaPane));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void setDiagnosticoText(String texto) {
        tituloLabel.setText(texto);
        lenguajeSeleccionado = texto;
    }
}