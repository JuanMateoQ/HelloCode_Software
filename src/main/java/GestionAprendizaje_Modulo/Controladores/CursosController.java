package GestionAprendizaje_Modulo.Controladores;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class CursosController {

    @FXML
    private Label tituloLenguajeLabel;

    @FXML
    private Button btnJava;
    @FXML
    private Button btnPython;
    @FXML
    private Button btnPhp;
    @FXML
    private Button btnCplusplus;

    @FXML
    private Button btnVolver;

    @FXML
    private void initialize() {
        // Acción para el botón de "Volver"
        btnVolver.setOnAction(event -> {
            try {
                // Cargar el archivo FXML de la vista HomeUsuario (siempre volver al home)
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Modulo_Usuario/views/homeUsuario.fxml"));
                AnchorPane homePane = loader.load(); // Cargar la vista de home

                // Obtener el stage actual y cambiar la escena
                Stage stage = (Stage) btnVolver.getScene().getWindow();
                stage.setScene(new Scene(homePane)); // Cambiar la escena
                stage.show(); // Mostrar la nueva escena
            } catch (IOException e) {
                e.printStackTrace(); // Mostrar el error si algo falla
            }
        });

        // Lógica para el botón Java
        btnJava.setOnAction(event -> {
            abrirDiagnostico("Java");
        });

        // Lógica para el botón Python
        btnPython.setOnAction(event -> {
            abrirDiagnostico("Python");
        });

        // Lógica para el botón PHP
        btnPhp.setOnAction(event -> {
            abrirDiagnostico("PHP");
        });

        // Lógica para el botón C++
        btnCplusplus.setOnAction(event -> {
            abrirDiagnostico("C");
        });
    }

    private void abrirDiagnostico(String lenguaje) {
        try {
            // Cargar el archivo FXML de la vista Diagnóstico
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionAprendizaje_Modulo/Vistas/Diagnostico.fxml"));
            AnchorPane diagnosticoPane = loader.load();  // Cargar el contenido de la vista Diagnóstico

            // Obtener el controlador de la vista Diagnóstico
            DiagnosticoController diagnosticoController = loader.getController();
            diagnosticoController.setDiagnosticoText(lenguaje); // Pasar el nombre del lenguaje al controlador

            // Obtener el stage actual y cambiar la escena
            Stage stage = (Stage) btnJava.getScene().getWindow(); // Usamos cualquier botón para obtener el Stage
            stage.setScene(new Scene(diagnosticoPane));  // Cambiar la escena
            stage.show();  // Mostrar la nueva escena
        } catch (IOException e) {
            e.printStackTrace();  // Mostrar error si falla al cargar el FXML
        }
    }
}
