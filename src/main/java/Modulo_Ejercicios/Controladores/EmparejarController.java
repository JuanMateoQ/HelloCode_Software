package Modulo_Ejercicios.Controladores;

import Modulo_Ejercicios.logic.EjercicioEmparejar;
import Modulo_Ejercicios.logic.Respuesta;
import Modulo_Ejercicios.logic.RespuestaString;
import Modulo_Ejercicios.logic.ResultadoDeEvaluacion;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.ArrayList;

public class EmparejarController {
    @FXML private Label lblInstruccion;
    @FXML private ListView<String> listIzquierda;
    @FXML private ListView<String> listDerecha;
    @FXML private Label lblResultado;
    @FXML private Button btnEvaluar;
    @FXML private Button btnSiguiente;

    private EjercicioEmparejar ejercicio;

    public void setEjercicio(EjercicioEmparejar ejercicio) {
        this.ejercicio = ejercicio;
        cargarDatos();
    }

    private void cargarDatos() {
        if (ejercicio == null) return;
        lblInstruccion.setText(ejercicio.getInstruccion());
        listIzquierda.getItems().setAll(ejercicio.obtenerColumnaIzquierda());
        listDerecha.getItems().setAll(ejercicio.obtenerColumnaDerecha()); // TODO: barajar si se desea
        lblResultado.setText("");
    }

    @FXML
    private void initialize() {
        btnEvaluar.setOnAction(e -> evaluar());
        btnSiguiente.setOnAction(e -> avanzar());
    }

    private void evaluar() {
        if (ejercicio == null) return;
        // El usuario ordena la lista de la derecha manualmente (drag & drop no implementado todavía)
        ArrayList<Respuesta> respuestas = new ArrayList<>();
        for (String item : listDerecha.getItems()) {
            respuestas.add(new RespuestaString(item));
        }
        ResultadoDeEvaluacion r = ejercicio.evaluarRespuestas(respuestas);
        lblResultado.setText("Resultado: " + r.getPorcentajeDeAcerto() + "%");
    }

    private void avanzar() {
        // Llamar al controlador de lección para avanzar
        Nuevo_Modulo_Leccion.controllers.LeccionUIController.avanzarAlSiguienteEjercicio();
        // Cerrar ventana actual
        btnSiguiente.getScene().getWindow().hide();
    }
}
