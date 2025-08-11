package GestionAprendizaje_Modulo.Controladores;

import java.io.IOException;
import java.util.List;

import Conexion.MetodosFrecuentes;
import Conexion.SesionManager;
import Modulo_Usuario.Clases.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MisLenguajesController {

    @FXML private VBox lenguajesContainer;
    @FXML private AnchorPane rootPane;
    @FXML private Button btnAtras;
    @FXML private Button btnHome;
    @FXML private Button btnAdd;
    @FXML private Button btnLibrary;

    private Usuario usuarioActual;

    @FXML
    private void initialize() {
        // Obtener el usuario actual
        this.usuarioActual = SesionManager.getInstancia().getUsuarioAutenticado();

        if (usuarioActual == null) {
            Label errorLabel = new Label("Error: No hay usuario en sesión");
            errorLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
            lenguajesContainer.getChildren().add(errorLabel);
            return;
        }

        cargarLenguajesDelUsuario();
        configurarBotones();
    }

    private void cargarLenguajesDelUsuario() {
        // Obtener la configuración del usuario
        ConfiguracionUsuarioService.ConfiguracionUsuario config =
            ConfiguracionUsuarioService.getInstancia().obtenerConfiguracion(usuarioActual.getUsername());

        if (config == null || config.getLenguajes().isEmpty()) {
            Label noLenguajesLabel = new Label("No tienes lenguajes configurados aún");
            noLenguajesLabel.setStyle("-fx-text-fill: #A6B1E1; -fx-font-size: 16px; -fx-padding: 20;");
            lenguajesContainer.getChildren().add(noLenguajesLabel);
            return;
        }

        List<String> lenguajes = config.getLenguajes();
        List<String> niveles = config.getNiveles();

        // Crear una tarjeta para cada lenguaje
        for (int i = 0; i < lenguajes.size(); i++) {
            String lenguaje = lenguajes.get(i);
            String nivel = i < niveles.size() ? niveles.get(i) : "Básico";

            VBox tarjetaLenguaje = crearTarjetaLenguaje(lenguaje, nivel);
            lenguajesContainer.getChildren().add(tarjetaLenguaje);
        }
    }

    private VBox crearTarjetaLenguaje(String lenguaje, String nivel) {
        VBox tarjeta = new VBox(10);
        tarjeta.setStyle("-fx-padding: 20; -fx-background-color: #FFFFFF1A; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 4);");

        // Título del lenguaje
        Label tituloLenguaje = new Label(lenguaje);
        tituloLenguaje.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        // Nivel
        Label nivelLabel = new Label("Nivel: " + nivel);
        nivelLabel.setStyle("-fx-text-fill: #A6B1E1; -fx-font-size: 16px;");

        // Botón para acceder a la ruta
        Button btnAccederRuta = new Button("🎯 Acceder a la Ruta");
        btnAccederRuta.setStyle("-fx-background-color: #5A9BD5; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-cursor: hand; -fx-padding: 10 20;");

        btnAccederRuta.setOnAction(event -> {
            // Establecer el lenguaje seleccionado en el DiagnosticoController
            DiagnosticoController.lenguajeSeleccionado = lenguaje;
            DiagnosticoController.nivelSeleccionado = nivel;

            // Ir a la vista de Ruta
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionAprendizaje_Modulo/Vistas/Ruta.fxml"));
                AnchorPane rutaPane = loader.load();
                Stage stage = (Stage) rootPane.getScene().getWindow();
                stage.setScene(new Scene(rutaPane));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        tarjeta.getChildren().addAll(tituloLenguaje, nivelLabel, btnAccederRuta);
        return tarjeta;
    }

    private void configurarBotones() {
        // Botón Atrás
        btnAtras.setOnAction(event -> {
            try {
                MetodosFrecuentes.cambiarVentana((Stage) rootPane.getScene().getWindow(), "/Modulo_Usuario/views/homeUsuario.fxml", "Home Usuario");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Botón Home
        btnHome.setOnAction(event -> {
            try {
                MetodosFrecuentes.cambiarVentana((Stage) rootPane.getScene().getWindow(), "/Modulo_Usuario/views/homeUsuario.fxml", "Home Usuario");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Botón Add
        btnAdd.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionAprendizaje_Modulo/Vistas/Cursos.fxml"));
                AnchorPane cursosPane = loader.load();
                Stage stage = (Stage) rootPane.getScene().getWindow();
                stage.setScene(new Scene(cursosPane));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Botón Library (mostrar esta misma vista)
        btnLibrary.setOnAction(event -> {
            // Ya estamos en la vista de biblioteca, no hacer nada o recargar
            cargarLenguajesDelUsuario();
        });
    }
}
