package Modulo_Usuario.Controladores;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


public class HomeController {

    @FXML
    private void abrirLeccion(MouseEvent event) {
        // Aquí puedes cargar el módulo de lección cuando lo tengas
        mostrarMensaje("Módulo Lecciones", "Este módulo estará disponible próximamente.");
    }

    @FXML
    private void abrirUsuarios(MouseEvent event) {
        try {/*
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Modulo_Usuario/views/crud.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 360, 640);

            Stage stage = new Stage();
            stage.setTitle("Hello Code Software - Módulo Usuarios");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            // Cerrar la pantalla actual
            Stage thisStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            thisStage.close();*/
            mostrarMensaje("Módulo Lecciones", "Este módulo estará disponible próximamente.");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al abrir el módulo de usuarios: " + e.getMessage());
        }
    }

    @FXML
    private void abrirEjercicios(MouseEvent event) {

        // Aquí puedes cargar el módulo de ejercicios cuando lo tengas
        mostrarMensaje("Módulo Ejercicios", "Este módulo estará disponible próximamente.");

    }

    @FXML
    private void abrirReportes(MouseEvent event) {
        try {
            System.out.println(">>> Abriendo panel de administración...");
            Gamificacion_Modulo.clases.Main.inicializarDesdeModuloExterno();
            Gamificacion_Modulo.controllers_admin.AdminMainController.mostrarVentanaAdmin();
        } catch (Exception e) {
            System.err.println("Error al abrir panel de administración: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirComunidad(MouseEvent event) {
        try {
            /*
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Modulo_Comunidad/Views/Comunidad.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 360, 640);

            Stage stage = new Stage();
            stage.setTitle("Hello Code Software - Módulo Comunidad");

            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            // Cerrar la pantalla actual
            Stage thisStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            thisStage.close();
             */
            mostrarMensaje("Módulo Lecciones", "Este módulo estará disponible próximamente.");
        } catch (Exception e) {
            e.printStackTrace();

            mostrarError("Error al abrir el módulo de comunidad: " + e.getMessage());

        }
    }

    @FXML

    private void mostrarPerfil(ActionEvent event) {

        // Aquí puedes mostrar información del perfil del usuario
        mostrarMensaje("Perfil de Usuario", "Funcionalidad de perfil estará disponible próximamente.");
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        try {
            // Volver a la pantalla de login
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Modulo_Usuario/views/login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 360, 720);

            Stage stage = new Stage();
            stage.setTitle("Hello Code Software - Iniciar Sesión");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            // Cerrar la pantalla actual
            Stage thisStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            thisStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al cerrar sesión: " + e.getMessage());
        }
    }

    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

