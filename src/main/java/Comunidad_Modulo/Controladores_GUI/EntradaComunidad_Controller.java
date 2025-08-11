package Comunidad_Modulo.Controladores_GUI;

import Gamificacion_Modulo.clases.Main;
import Conexion.MetodosFrecuentes;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class EntradaComunidad_Controller {

    @FXML
    private Button buttonModerador;

    @FXML
    private Button buttonGestionarComunidad;

    @FXML
    private Button buttonGestionarUsuarios;

    @FXML
    private Button buttonGestionarForo;

    @FXML
    private Button buttonGestionarChatsPrivados;

    @FXML
    private Button buttonSalirSistema;

    @FXML
    private Button buttonVolver;

    @FXML
    private Button btnPerfil2;

    @FXML
    private Button btnRanking;

    @FXML
    private Button btnComunidad;
    @FXML
    private Button btnHomeUsuario;

    @FXML
    private void gestionarComunidad() {
        MetodosFrecuentes.cambiarVentana((Stage) buttonGestionarComunidad.getScene().getWindow(),
                "/Modulo_Comunidad/Views/GestionComunidad.fxml", "GESTIONAR COMUNIDAD");
    }

    @FXML
    private void gestionarUsuarios() {
        MetodosFrecuentes.cambiarVentana((Stage) buttonGestionarUsuarios.getScene().getWindow(),
                "/Modulo_Comunidad/Views/GestionUsuarios.fxml", "GESTIONAR USUARIOS");
    }

    @FXML
    private void gestionarForo() {
        MetodosFrecuentes.cambiarVentana((Stage) buttonGestionarForo.getScene().getWindow(),
                "/Modulo_Comunidad/Views/GestionForo.fxml", "GESTIONAR FORO");
    }

    @FXML
    private void gestionarChatsPrivados() {
        MetodosFrecuentes.cambiarVentana((Stage) buttonGestionarChatsPrivados.getScene().getWindow(),
                "/Modulo_Comunidad/Views/GestionChatsPrivados.fxml", "GESTIONAR CHATS PRIVADOS");
    }

    @FXML
    private void moderadorComunidad() {
        MetodosFrecuentes.cambiarVentana((Stage) buttonModerador.getScene().getWindow(),
                "/Modulo_Comunidad/Views/Moderador.fxml", "MODERADOR");
    }

    @FXML
    private void volverAHome() {
        mostrarMensaje("Ruta", "Funcionalidad de la ruta pronto será mostrada");
    }
    @FXML
    private void salir() {
        MetodosFrecuentes.cambiarVentana((Stage) buttonSalirSistema.getScene().getWindow(), "/Modulo_Usuario/views/login.fxml", "Login");

    }

    @FXML
    public void irAPerfil(ActionEvent event) {
        try {
            // PASO 1: Inicializar el backend del módulo de gamificación
            Main.inicializarDesdeModuloExterno();

            // PASO 2: Cargar la interfaz gráfica
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Gamificacion_Modulo/fxml/PerfilUsuario.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 360, 720);

            Stage stage = new Stage();
            stage.setTitle("Hello Code Software - Gamificación");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            // Cerrar la pantalla actual
            Stage thisStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            thisStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al abrir el módulo de gamificación: " + e.getMessage());
        }
    }

    @FXML
    public void irARanking() {
        MetodosFrecuentes.cambiarVentana((Stage) btnRanking.getScene().getWindow(),
                "/Gamificacion_Modulo/fxml/Ranking.fxml", "Ir a Ranking de Usuario");
    }

    @FXML
    public void irAComunidad() {
        MetodosFrecuentes.cambiarVentana((Stage) btnComunidad.getScene().getWindow(),
                "/Modulo_Comunidad/Views/Comunidad.fxml", "Ir a Menu Comunidad");
    }
    @FXML
    void irAHomeUsuario(ActionEvent event){
            MetodosFrecuentes.cambiarVentana((Stage) btnHomeUsuario.getScene().getWindow(),
                    "/Modulo_Usuario/views/homeUsuario.fxml", "Volver a Home - Modulos");

    }
    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
