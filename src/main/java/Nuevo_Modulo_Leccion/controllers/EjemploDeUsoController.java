package Nuevo_Modulo_Leccion.controllers;

import MetodosGlobales.MetodosFrecuentes;
import Nuevo_Modulo_Leccion.logic.Leccion;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class EjemploDeUsoController {
    @FXML
    private Button buttonBack;
    @FXML
    private Button buttonLeccionUno;
    @FXML
    private void VolverALaAnterior() {
        System.out.println("Volver a lo que necesiten");
    }
    @FXML
    private void irLeccionUno() {
        Stage stage = (Stage) buttonLeccionUno.getScene().getWindow();

        LeccionUIController.mostrarUnaLeccion(new Leccion(),stage,"/Nuevo_Modulo_Leccion/views/ejemploDeUso.fxml");
    }



}