package Nuevo_Modulo_Leccion.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class seAcabaronVidasLeccionController {

    @FXML
    private Button buttonOk;

    @FXML
    private void okvolver() {
        Stage stage = (Stage) buttonOk.getScene().getWindow();
        stage.close();
    }
}
