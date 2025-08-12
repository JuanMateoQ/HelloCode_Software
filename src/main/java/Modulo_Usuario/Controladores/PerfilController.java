package Modulo_Usuario.Controladores;

import Conexion.MetodosFrecuentes;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class PerfilController {

    @FXML private Label lblUsername;
    @FXML private Label lblNombre;
    @FXML private Label lblEmail;
    @FXML private Label lblXp;
    @FXML private Button btnBack;
    private Modulo_Usuario.Clases.Usuario usuario;

    @FXML
    public void initialize() {
        // inicialización básica
    }

    /**
     * Configura la vista con los datos del usuario.
     */
    public void setUsuario(Modulo_Usuario.Clases.Usuario usuario) {
        this.usuario = usuario;
        lblUsername.setText(usuario.getUsername());
        lblNombre.setText(usuario.getNombre());
        lblEmail.setText(usuario.getEmail());
        if (lblXp != null) {
            lblXp.setText(String.valueOf(usuario.getXp()) + " XP");
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        MetodosFrecuentes.cambiarVentana((Stage) btnBack.getScene().getWindow(), "/Modulo_Usuario/views/homeUsuario.fxml", "Perfil de Usuario");
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Modulo_Usuario/views/homeUsuario.fxml"));
//            Parent root = loader.load();
//            // Pasar el mismo usuario al HomeUsuarioController
//            HomeUsuarioController homeCtrl = loader.getController();
//            homeCtrl.setUsuario(this.usuario);
//            Scene scene = new Scene(root, 360, 640);
//            Stage stage = new Stage();
//            stage.setTitle("Hello Code Software - Inicio");
//            stage.setScene(scene);
//            stage.setResizable(false);
//            stage.show();
//            // Cerrar ventana actual
//            Stage thisStage = (Stage) btnBack.getScene().getWindow();
//            thisStage.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
