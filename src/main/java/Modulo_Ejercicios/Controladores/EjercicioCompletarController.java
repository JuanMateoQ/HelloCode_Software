package Modulo_Ejercicios.Controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class EjercicioCompletarController {

    // FXML elements are associated with this controller

    @FXML
    private Text TexTipo; // Text for displaying instruction

    @FXML
    private Text TexVida; // Text for displaying number of lives

    @FXML
    private ProgressBar ProgressBar; // Progress bar for showing progress

    @FXML
    private Button btnComprobar; // Button to check the entered code

    @FXML
    private Button btnRegresar; // Button to go back

    @FXML
    private ImageView btnRegresarImage; // Image for the go back button icon

    @FXML
    private TextField textEntrada; // Input field for the code

    @FXML
    private Label Ejercicio; // Label for the exercise instruction

    @FXML
    private Text TextInstruccion; // Text for instructions

    @FXML
    private ImageView fondoImageView; // Background image for the scene

    @FXML
    private ImageView tecladoImageView; // Image for the keyboard

    // Initialize method that runs when the controller is initialized
    public void initialize() {
        // Set up default values or event handlers
        TexTipo.setText("Completa el código correctamente:");
        TexVida.setText("5");  // Example: setting number of lives to 5
        ProgressBar.setProgress(0.29);  // Set the progress bar to a default value
        Ejercicio.setText("Ejercicio");

        // Button actions
        btnComprobar.setOnAction(event -> comprobarCodigo());
        btnRegresar.setOnAction(event -> regresar());
    }

    // Method to handle the "Comprobar" button action
    private void comprobarCodigo() {
        // Logic to check the code entered by the user
        String input = textEntrada.getText();
        if (input.equals("correctCode")) {
            // Handle correct code entry
            TexVida.setText("4");  // Decrease life by 1
            ProgressBar.setProgress(0.5);  // Update progress bar
        } else {
            // Handle incorrect code entry
            TexVida.setText("4");  // Decrease life by 1
        }
    }

    // Method to handle the "Regresar" button action
    private void regresar() {
        // Logic to handle the "back" action
        System.out.println("Regresando...");
        // You could close the window or navigate back to another screen
    }
}
