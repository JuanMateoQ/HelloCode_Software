package Modulo_Usuario.Controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HomeController {

    @FXML
    private void abrirLeccion(ActionEvent event) {
        // Aquí puedes cargar el módulo de lección cuando lo tengas
        System.out.println("Módulo Lección (no implementado aún)");
    }

    @FXML
    private void abrirUsuarios(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Modulo_Usuario/views/crud.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 360, 720);

            Stage stage = new Stage();
            stage.setTitle("Módulo Usuarios");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            // Cerrar la pantalla actual
            Stage thisStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            thisStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 