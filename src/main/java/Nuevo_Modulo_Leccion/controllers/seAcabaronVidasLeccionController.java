package Nuevo_Modulo_Leccion.controllers;

import Conexion.MetodosFrecuentes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class seAcabaronVidasLeccionController {

    @FXML
    private Button buttonOk;

    @FXML
    private void okvolver() {
        Stage stage = (Stage) buttonOk.getScene().getWindow();
        try {
            String destino = LeccionUIController.getRutaFXMLVentanaFinal();
            if (destino != null && !destino.isEmpty()) {
                // Cambia la escena de la ventana que mostró el modal
                MetodosFrecuentes.mostrarVentana(destino,"Menú de Lecciones");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stage.close();
        }
    }
}
