package Modulo_Usuario.Controladores;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import MetodosGlobales.SesionManager;
import Modulo_Usuario.Clases.Roles;
import Modulo_Usuario.Clases.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField usuarioField;
    @FXML private PasswordField contrasenaField;
    @FXML private Label mensajeLabel;

    private List<Usuario> usuarios = new ArrayList<>();

    @FXML
    public void initialize() {
        cargarUsuarios();
    }

    private void cargarUsuarios() {
        try {
            File file = new File("src/main/java/Modulo_Usuario/Usuarios/usuarios.txt");
            if (!file.exists()) {
                mensajeLabel.setText("Error: No se encontró el archivo usuarios.txt");
                return;
            }
            InputStream inputStream = new FileInputStream(file);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    linea = linea.trim();
                    if (!linea.isEmpty()) {
                        Usuario usuario = Usuario.fromString(linea);
                        if (usuario != null) {
                            usuarios.add(usuario);
                        }
                    }
                }
            }

            System.out.println("Usuarios cargados: " + usuarios.size());
            SesionManager.getInstancia().guardarUsuarios(usuarios);

        } catch (Exception e) {
            e.printStackTrace();
            mensajeLabel.setText("Error al cargar usuarios: " + e.getMessage());
        }
    }

    @FXML
    protected void handleLogin(ActionEvent event) {
        String username = usuarioField.getText().trim();
        String password = contrasenaField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            mensajeLabel.setText("Por favor complete todos los campos");
            return;
        }

        // Buscar usuario en la lista
        Usuario usuarioEncontrado = null;
        for (Usuario usuario : usuarios) {
            if (usuario.getUsername().equals(username) && usuario.getPassword().equals(password)) {
                usuarioEncontrado = usuario;
                break;
            }
        }

        if (usuarioEncontrado != null) {
            try {
                SesionManager.getInstancia().iniciarSesion(usuarioEncontrado);
                
                // Debug: verificar rol del usuario
                System.out.println("Login exitoso - Usuario: " + usuarioEncontrado.getUsername() + 
                                 ", Rol: " + usuarioEncontrado.getRol());
                
                // Si es usuario normal, va a homeUsuario.fxml
                if (usuarioEncontrado.getRol() == Roles.USUARIO) {
                    System.out.println("Redirigiendo a Panel Usuario");
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Modulo_Usuario/views/homeUsuario.fxml"));
                    Parent root = fxmlLoader.load();
                    HomeUsuarioController homeController = fxmlLoader.getController();
                    homeController.setUsuario(usuarioEncontrado);
                    Scene scene = new Scene(root, 360, 640);
                    Stage stage = new Stage();
                    stage.setTitle("Hello Code Software - Panel Usuario");
                    stage.setScene(scene);
                    stage.setResizable(false);
                    stage.show();
                } else if (usuarioEncontrado.getRol() == Roles.ADMINISTRADOR) {
                    // Si es administrador, va a home.fxml con HomeController
                    System.out.println("Redirigiendo a Panel Administrador");
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Modulo_Usuario/views/home.fxml"));
                    Parent root = fxmlLoader.load();
                    HomeController homeController = fxmlLoader.getController();
                    homeController.setUsuario(usuarioEncontrado);
                    Scene scene = new Scene(root, 360, 640);
                    Stage stage = new Stage();
                    stage.setTitle("Hello Code Software - Panel Administrador");
                    stage.setScene(scene);
                    stage.setResizable(false);
                    stage.show();
                } else {
                    // Para cualquier otro rol futuro, también va al panel admin
                    System.out.println("Rol no reconocido, redirigiendo a Panel Admin");
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Modulo_Usuario/views/home.fxml"));
                    Parent root = fxmlLoader.load();
                    HomeController homeController = fxmlLoader.getController();
                    homeController.setUsuario(usuarioEncontrado);
                    Scene scene = new Scene(root, 360, 640);
                    Stage stage = new Stage();
                    stage.setTitle("Hello Code Software - Panel Admin");
                    stage.setScene(scene);
                    stage.setResizable(false);
                    stage.show();
                }
                Stage thisStage = (Stage) usuarioField.getScene().getWindow();
                thisStage.close();
            } catch (Exception e) {
                e.printStackTrace();
                mensajeLabel.setText("Error cargando pantalla de inicio: " + e.getMessage());
            }
        } else {
            mensajeLabel.setText("Credenciales incorrectas");
            // Limpiar campo de contraseña
            contrasenaField.clear();
        }
    }

    @FXML
    public void irARegistro(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Modulo_Usuario/views/register.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 360, 720);

            Stage stage = new Stage();
            stage.setTitle("Hello Code Software - Registro de Usuario");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            Stage thisStage = (Stage) usuarioField.getScene().getWindow();
            thisStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            mensajeLabel.setText("Error cargando pantalla de registro: " + e.getMessage());
        }
    }
}
