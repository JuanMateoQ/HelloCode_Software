package GestionAprendizaje_Modulo.Controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class CursosSeguidosController {

    @FXML
    private VBox contenedorCursos;

    private Stage stageAnterior; // Para volver

    // Cargar cursos en el VBox
    public void cargarCursos(List<String> cursosTomados) {
        contenedorCursos.getChildren().clear();

        for (String curso : cursosTomados) {
            Label etiqueta = crearEtiquetaCurso(curso);
            contenedorCursos.getChildren().add(etiqueta);
        }
    }

    // Guardar la ventana anterior para regresar
    public void setStageAnterior(Stage stageAnterior) {
        this.stageAnterior = stageAnterior;
    }

    // Crear etiqueta de curso con estilo
    private Label crearEtiquetaCurso(String curso) {
        Label etiqueta = new Label(curso);
        etiqueta.setPrefWidth(250);
        etiqueta.setAlignment(javafx.geometry.Pos.CENTER);
        etiqueta.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 15px;" +
                        "-fx-padding: 10;" +
                        "-fx-background-color: " + obtenerColorCurso(curso) + ";"
        );
        return etiqueta;
    }

    // Asignar color a cada curso
    private String obtenerColorCurso(String curso) {
        switch (curso.toUpperCase()) {
            case "JAVA":
                return "#6C63FF";
            case "PYTHON":
                return "#3B97D3";
            case "PHP":
                return "#FF4081";
            case "C++":
                return "#FF7043";
            default:
                return "#888888";
        }
    }

    // Botón volver
    @FXML
    private void volver() {
        if (stageAnterior != null) {
            stageAnterior.show();
            ((Stage) contenedorCursos.getScene().getWindow()).close();
        }
    }

    public void setCursos(List<String> cursosTomados) {
        if (contenedorCursos != null) {
            cargarCursos(cursosTomados);
        } else {
            System.err.println("El contenedor de cursos no está inicializado.");
        }
    }
}
