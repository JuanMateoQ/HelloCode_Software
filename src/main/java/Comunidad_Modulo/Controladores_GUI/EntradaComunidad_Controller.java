package Comunidad_Modulo.Controladores_GUI;

import Conexion.MetodosFrecuentes;
import Gamificacion_Modulo.utils.GestorGamificacion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class EntradaComunidad_Controller {

    @FXML
    private Button buttonCrearComunidad;

    @FXML
    private Button buttonMisComunidades;

    @FXML
    private Button buttonForos;

    @FXML
    private Button buttonChatsPrivados;

    @FXML
    private Button buttonSalirSistema;

    @FXML
    private Button buttonVolver;

    @FXML
    private Button btnRuta;

    @FXML
    private Button btnRanking;

    @FXML
    private Button btnHomeUsuario;

    @FXML
    private void crearComunidad() {
        MetodosFrecuentes.cambiarVentana((Stage) buttonCrearComunidad.getScene().getWindow(),
                "/Modulo_Comunidad/Views/CrearComunidad.fxml", "Crear COMUNIDAD");
    }

    @FXML
    private void misComunidades() {
        MetodosFrecuentes.cambiarVentana((Stage) buttonMisComunidades.getScene().getWindow(),
                "/Modulo_Comunidad/Views/MisComunidades.fxml", "MIS COMUNIDADES");
    }

    @FXML
    private void Foros() {
        MetodosFrecuentes.cambiarVentana((Stage) buttonForos.getScene().getWindow(),
                "/Modulo_Comunidad/Views/Foros.fxml", "FOROS");
    }

    @FXML
    private void chatsPrivados() {
        MetodosFrecuentes.cambiarVentana((Stage) buttonChatsPrivados.getScene().getWindow(),
                "/Modulo_Comunidad/Views/ChatsPrivados.fxml", "CHATS PRIVADOS");
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
            GestorGamificacion.inicializarDesdeModuloExterno();

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
    public void irARuta() {
        MetodosFrecuentes.cambiarVentana((Stage) btnRuta.getScene().getWindow(),
                "/GestionAprendizaje_Modulo/Vistas/Ruta.fxml", "Ir a Ruta de Usuario");
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
