package Nuevo_Modulo_Leccion.controllers;

import Modulo_Ejercicios.logic.Lenguaje;
import Modulo_Ejercicios.logic.NivelDificultad;
import Nuevo_Modulo_Leccion.dataBase.LeccionRepository;
import Nuevo_Modulo_Leccion.logic.Leccion;
import Nuevo_Modulo_Leccion.logic.TemaLeccion;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.List;

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
        List<Leccion> leccionList = LeccionRepository.getListaLecciones(Lenguaje.JAVA, NivelDificultad.BASICO, TemaLeccion.CONTROL_DE_FLUJO, 4);
        LeccionUIController.mostrarUnaLeccion(leccionList.get(0),stage,"/Nuevo_Modulo_Leccion/views/ejemploDeUso.fxml");
        //LeccionUIController.mostrarUnaLeccion(leccionList.get(1),stage,"/Nuevo_Modulo_Leccion/views/ejemploDeUso.fxml");
        //LeccionUIController.mostrarUnaLeccion(leccionList.get(2),stage,"/Nuevo_Modulo_Leccion/views/ejemploDeUso.fxml");

    }



}