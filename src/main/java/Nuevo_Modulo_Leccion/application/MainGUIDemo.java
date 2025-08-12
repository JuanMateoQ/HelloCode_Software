package Nuevo_Modulo_Leccion.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainGUIDemo extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Nuevo_Modulo_Leccion/views/ejemploDeUso.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Ejemplo de uso");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

