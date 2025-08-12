package Nuevo_Modulo_Leccion.controllers;

import Conexion.MetodosFrecuentes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class seAcabaronVidasLeccionController {

    @FXML
    private Button buttonOk;


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

    public void inicializarDatos(long minutos, long segundos, double XP_ganada, double porcentajeAciertos, String nombreUsuarioActual) {
        // Mostrar valores en la interfaz con formato correcto
        Tiempo_Text.setText(String.format("%02d:%02d", minutos, segundos));
        PorcentajeAciertos_Text.setText(String.format("%.2f %%", porcentajeAciertos));
        XP_Text.setText(String.format("%.0f XP", XP_ganada));
        nombreUsuaio_text.setText(nombreUsuarioActual);
    }
}
