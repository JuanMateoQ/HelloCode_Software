package Nuevo_Modulo_Leccion.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.text.Text;

public class ResumenLeccionController {
    @FXML
    private Button buttonOk;
    private long minutos;
    private long segundos;
    private double xpGanada;
    private double porcentajeAcierto;
    private String nombreUsuarioActual;


    @FXML
    private Text Tiempo_Text;
    @FXML
    private Text PorcentajeAciertos_Text;
    @FXML
    private Text XP_Text;
    @FXML
    private Label nombreUsuaio_text;

    @FXML
    private void okvolver() {
        Stage stage = (Stage) buttonOk.getScene().getWindow();
        stage.close();
    }

    public void inicializarDatos(long minutos, long segundos, double XP_ganada, double porcentajeAcierto, String nombreUsuarioActual, boolean completada) {
        this.minutos = minutos;
        this.segundos = segundos;
        this.xpGanada = XP_ganada;
        this.porcentajeAcierto = porcentajeAcierto;
        this.nombreUsuarioActual = nombreUsuarioActual;

        // Mostrar valores en la interfaz con formato correcto
        Tiempo_Text.setText(String.format("%02d:%02d", this.minutos, this.segundos));
        PorcentajeAciertos_Text.setText(String.format("%.2f %%", this.porcentajeAcierto));
        XP_Text.setText(String.format("%.0f", xpGanada));
        nombreUsuaio_text.setText(this.nombreUsuarioActual);

        // Cambiar color según completada
        if (completada) {
            PorcentajeAciertos_Text.setFill(javafx.scene.paint.Color.GREEN);
        } else {
            PorcentajeAciertos_Text.setFill(javafx.scene.paint.Color.RED);
        }

    }



}
