package Modulo_Ejercicios.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainGUICRUD extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Cargar el archivo FXML del CRUD de ejercicios
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Modulo_Ejercicios/views/CrudEjercicios.fxml"));
            Parent root = loader.load();
            
            // Configurar la escena
            Scene scene = new Scene(root, 360, 720);
            
            // Configurar el stage
            primaryStage.setTitle("Hello Code Software - Gestión de Ejercicios");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            
            // Mostrar la ventana
            primaryStage.show();
            
        } catch (Exception e) {
            System.err.println("Error al cargar la aplicación CRUD de ejercicios:");
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        System.out.println("🚀 Iniciando CRUD de Ejercicios...");
        launch(args);
    }
}
