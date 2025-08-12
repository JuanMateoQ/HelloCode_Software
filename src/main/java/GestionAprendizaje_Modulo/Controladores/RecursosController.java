package GestionAprendizaje_Modulo.Controladores;

import java.util.List;

import GestionAprendizaje_Modulo.Modelo.RecursoAprendizaje;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

public class RecursosController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private ListView<String> recursosListView;

    @FXML
    private Button abrirRecursoButton;

    @FXML
    private Button btnAtras;

    @FXML
    private Label mensajeLabel;

    private List<RecursoAprendizaje> recursos;

    public void inicializarRecursos(List<RecursoAprendizaje> recursos) {
        this.recursos = recursos;
        for (RecursoAprendizaje recurso : recursos) {
            recursosListView.getItems().add(recurso.getTitulo());
        }
    }

    @FXML
    private void initialize() {
        recursosListView.getItems().addAll(
            "PDF: Introducción a los Bucles",
            "Video: Conceptos Básicos de Bucles",
            "Artículo: Mejores Prácticas con Bucles"
        );
    }

    @FXML
    private void manejarAbrirRecurso() {
        int indiceSeleccionado = recursosListView.getSelectionModel().getSelectedIndex();
        if (indiceSeleccionado < 0) {
            mensajeLabel.setText("Por favor, selecciona un recurso.");
        } else {
            mensajeLabel.setText("");
            RecursoAprendizaje recursoSeleccionado = recursos.get(indiceSeleccionado);
            abrirRecurso(recursoSeleccionado.getUrl());
        }
    }

    private void abrirRecurso(String url) {
        // Lógica para abrir el recurso en el navegador o aplicación correspondiente
        System.out.println("Abriendo recurso: " + url);
    }

    @FXML
    private void manejarAtras() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionAprendizaje_Modulo/Vistas/Ruta.fxml"));
            AnchorPane rutaPane = loader.load();
            rootPane.getChildren().setAll(rutaPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
