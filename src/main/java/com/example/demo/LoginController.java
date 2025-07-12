package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
            // Intentar cargar desde resources primero
            InputStream inputStream = getClass().getResourceAsStream("/usuarios.txt");
            if (inputStream == null) {
                // Si no está en resources, intentar desde el directorio del proyecto
                File file = new File("src/main/java/com/example/demo/usuarios.txt");
                if (file.exists()) {
                    inputStream = new FileInputStream(file);
                } else {
                    mensajeLabel.setText("Error: No se encontró el archivo usuarios.txt");
                    return;
                }
            }

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
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/crud.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
                
                // Obtener el controlador y configurar el usuario actual
                CrudController crudController = fxmlLoader.getController();
                crudController.setUsuarioActual(usuarioEncontrado.getUsername());
                
                Stage stage = new Stage();
                stage.setTitle("CRUD de Usuarios - " + usuarioEncontrado.getUsername());
                stage.setScene(scene);
                stage.setResizable(true);
                stage.show();

                Stage thisStage = (Stage) usuarioField.getScene().getWindow();
                thisStage.close();
            } catch (Exception e) {
                e.printStackTrace();
                mensajeLabel.setText("Error cargando CRUD: " + e.getMessage());
            }
        } else {
            mensajeLabel.setText("Credenciales incorrectas");
            // Limpiar campo de contraseña
            contrasenaField.clear();
        }
    }
}
