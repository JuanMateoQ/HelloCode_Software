package GestionAprendizaje_Modulo.Aplicacion;

import GestionAprendizaje_Modulo.Logica.AprendizajeManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUI_Contenido extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionAprendizaje_Modulo/Vistas/DashboardEstudiante.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Gestión de Aprendizaje");
        primaryStage.show();
    }




    public static void main(String[] args) {
        AprendizajeManager.getInstancia().construirDatosDePrueba();
        launch(args);
    }
}