package Comunidad_Modulo.Controladores_GUI;

import Conexion.MetodosFrecuentes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class EntradaAdminComunidad_Controller {

    @FXML
    private Button buttonModerador;

    @FXML
    private Button buttonSalirSistema;

    @FXML
    private Button buttonVolver;


    @FXML
    private void moderadorComunidad() {
        MetodosFrecuentes.cambiarVentana((Stage) buttonModerador.getScene().getWindow(),
                "/Modulo_Comunidad/Views/Moderador.fxml", "MODERADOR");
    }

    @FXML
    private void volverAHome(){
        MetodosFrecuentes.cambiarVentana((Stage) buttonVolver.getScene().getWindow(),
                "/Modulo_Usuario/views/home.fxml", "Volver a Home - Modulos");
    }

    @FXML
    private void salir() {

//        Stage stage = (Stage) buttonSalirSistema.getScene().getWindow();
//        stage.close();
        MetodosFrecuentes.cambiarVentana((Stage) buttonSalirSistema.getScene().getWindow(), "/Modulo_Usuario/views/login.fxml", "Login");
    }

}
