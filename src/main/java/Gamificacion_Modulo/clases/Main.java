package Gamificacion_Modulo.clases;

import Gamificacion_Modulo.utils.GestorGamificacion;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        // Inicializar datos del sistema
        GestorGamificacion.inicializarDesdeModuloExterno();

        // Iniciar la interfaz gráfica en un hilo separado
        Thread guiThread = new Thread(() -> {
            try {
                launch();
            } catch (Exception e) {
                System.err.println("Error al iniciar interfaz gráfica: " + e.getMessage());
            }
        });

        guiThread.setDaemon(false); // Mantener la aplicación viva
        guiThread.start();

        // Esperar un momento para que la GUI se inicialice
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Establecer el Stage principal en GestorGamificacion
        GestorGamificacion.setPrimaryStage(stage);

        try {
            // Prioridad 1: Cargar PerfilUsuario.fxml (Progreso - interfaz principal)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Gamificacion_Modulo/fxml/PerfilUsuario.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 360, 640);
            stage.setTitle("Sistema de Gamificación - HelloCode");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();

            System.out.println(">>> Interfaz gráfica cargada correctamente: PerfilUsuario.fxml (Progreso)");
        } catch (Exception e) {
            System.err.println("Error al cargar PerfilUsuario.fxml, intentando con Desafios.fxml: " + e.getMessage());

            try {
                // Prioridad 2: Cargar Desafios.fxml como fallback
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Gamificacion_Modulo/fxml/Desafios.fxml"));
                Parent root = loader.load();

                Scene scene = new Scene(root, 360, 720);
                stage.setTitle("Sistema de Gamificación - HelloCode");
                stage.setScene(scene);
                stage.setResizable(false);
                stage.centerOnScreen();
                stage.show();

                System.out.println(">>> Interfaz gráfica cargada correctamente: Desafios.fxml (Fallback)");
            } catch (Exception e2) {
                System.err.println("Error al cargar Desafios.fxml: " + e2.getMessage());
                e2.printStackTrace();
            }
        }
        System.out.println("Main Gamificacion");
        for(ProgresoEstudiante progreso : Ranking.getInstance().obtenerRankingGeneral())
            System.out.println(progreso.getDesafiosActivos());


    }
}