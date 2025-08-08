package GestionAprendizaje_Modulo.Controladores;

import Gamificacion_Modulo.clases.Main;
import MetodosGlobales.MetodosFrecuentes;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class DashboardEstudianteController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private ComboBox<String> lenguajeComboBox;

    @FXML
    private ComboBox<String> nivelComboBox;

    @FXML
    private Button btnInicio;
    @FXML
    private Button btnRanking;
    @FXML private Button btnPerfil;
    @FXML private Button btnComunidad;

    private static String lenguajeSeleccionado;

    public static String getLenguajeSeleccionado() {
        return lenguajeSeleccionado;
    }

    @FXML
    private void initialize() {
        lenguajeComboBox.getItems().addAll("Java", "Python", "C");
        nivelComboBox.getItems().addAll("Principiante", "Intermedio", "Avanzado");
    }

    @FXML
    private void manejarContinuar() {
        try {
            lenguajeSeleccionado = lenguajeComboBox.getValue();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionAprendizaje_Modulo/Vistas/Ruta.fxml"));
            AnchorPane listaNodosPane = loader.load();
            rootPane.getChildren().setAll(listaNodosPane);
        } catch (Exception e) {
            System.err.println("Error al cargar la vista Ruta.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void irARanking(ActionEvent event) {
        MetodosFrecuentes.cambiarVentana((Stage) btnRanking.getScene().getWindow(), "/Gamificacion_Modulo/fxml/Ranking.fxml", "Ranking");
    }

    @FXML
    private void irAHome(ActionEvent event) {
        MetodosFrecuentes.cambiarVentana((Stage) btnInicio.getScene().getWindow(), "/Modulo_Usuario/views/homeUsuario.fxml", "Ruta de Aprendizaje");
    }

    @FXML
    private void irAPerfil(ActionEvent event) {
        MetodosFrecuentes.cambiarVentana((Stage) btnPerfil.getScene().getWindow(), "/Gamificacion_Modulo/fxml/PerfilUsuario.fxml", "Perfil de Usuario");
    }

    @FXML
    private void irAComunidad(ActionEvent event) {
        MetodosFrecuentes.cambiarVentana((Stage) btnComunidad.getScene().getWindow(), "/Modulo_Comunidad/Views/Comunidad.fxml", "Comunidad");
        //mostrarMensaje("Comunidad", "Funcionalidad de comunidad próximamente");
    }

}
