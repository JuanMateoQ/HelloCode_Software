package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CrudController {
    @FXML private ListView<Usuario> listaUsuarios;
    @FXML private TextField nuevoUsuario;
    @FXML private PasswordField nuevaContrasena;
    @FXML private TextField nuevoNombre;
    @FXML private TextField nuevoEmail;
    @FXML private Label mensajeLabel;
    @FXML private Label usuarioActualLabel;
    
    private final String ARCHIVO_USUARIOS = "src/main/resources/usuarios.txt";
    private List<Usuario> usuarios = new ArrayList<>();
    private Usuario usuarioSeleccionado = null;
    private String usuarioActual = "Admin";

    @FXML
    public void initialize() {
        cargarUsuarios();
        configurarListView();
        usuarioActualLabel.setText("Usuario: " + usuarioActual);
    }

    private void configurarListView() {
        // Configurar la celda personalizada para mostrar información del usuario
        listaUsuarios.setCellFactory(param -> new ListCell<Usuario>() {
            @Override
            protected void updateItem(Usuario usuario, boolean empty) {
                super.updateItem(usuario, empty);
                if (empty || usuario == null) {
                    setText(null);
                } else {
                    setText(String.format("👤 %s - %s (%s)", 
                        usuario.getUsername(), 
                        usuario.getNombre() != null ? usuario.getNombre() : "Sin nombre",
                        usuario.getEmail() != null ? usuario.getEmail() : "Sin email"));
                }
            }
        });
    }

    private void cargarUsuarios() {
        usuarios.clear();
        try {
            InputStream inputStream = getClass().getResourceAsStream("/usuarios.txt");
            if (inputStream == null) {
                File file = new File(ARCHIVO_USUARIOS);
                if (file.exists()) {
                    inputStream = new FileInputStream(file);
                } else {
                    mostrarMensaje("Error: No se encontró el archivo usuarios.txt", "error");
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
            
            actualizarListView();
            mostrarMensaje("Usuarios cargados: " + usuarios.size(), "info");
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Error al cargar usuarios: " + e.getMessage(), "error");
        }
    }

    private void actualizarListView() {
        ObservableList<Usuario> observableList = FXCollections.observableArrayList(usuarios);
        listaUsuarios.setItems(observableList);
    }

    private void guardarUsuarios() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_USUARIOS))) {
            for (Usuario usuario : usuarios) {
                bw.write(usuario.toString() + "\n");
            }
            mostrarMensaje("Usuarios guardados correctamente", "success");
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al guardar usuarios: " + e.getMessage(), "error");
        }
    }

    @FXML
    private void agregarUsuario() {
        String username = nuevoUsuario.getText().trim();
        String password = nuevaContrasena.getText().trim();
        String nombre = nuevoNombre.getText().trim();
        String email = nuevoEmail.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            mostrarMensaje("Usuario y contraseña son obligatorios", "error");
            return;
        }

        // Verificar si el usuario ya existe
        for (Usuario usuario : usuarios) {
            if (usuario.getUsername().equals(username)) {
                mostrarMensaje("El usuario ya existe", "error");
                return;
            }
        }

        Usuario nuevoUsuario = new Usuario(username, password, nombre, email);
        usuarios.add(nuevoUsuario);
        guardarUsuarios();
        actualizarListView();
        limpiarCampos();
        mostrarMensaje("Usuario agregado correctamente", "success");
    }

    @FXML
    private void editarUsuario() {
        usuarioSeleccionado = listaUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado == null) {
            mostrarMensaje("Seleccione un usuario para editar", "error");
            return;
        }

        // Llenar campos con datos del usuario seleccionado
        nuevoUsuario.setText(usuarioSeleccionado.getUsername());
        nuevaContrasena.setText(usuarioSeleccionado.getPassword());
        nuevoNombre.setText(usuarioSeleccionado.getNombre() != null ? usuarioSeleccionado.getNombre() : "");
        nuevoEmail.setText(usuarioSeleccionado.getEmail() != null ? usuarioSeleccionado.getEmail() : "");
        
        mostrarMensaje("Modo edición: " + usuarioSeleccionado.getUsername(), "info");
    }

    @FXML
    private void eliminarUsuario() {
        Usuario usuarioSeleccionado = listaUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado == null) {
            mostrarMensaje("Seleccione un usuario para eliminar", "error");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro?");
        alert.setContentText("¿Desea eliminar el usuario: " + usuarioSeleccionado.getUsername() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                usuarios.remove(usuarioSeleccionado);
                guardarUsuarios();
                actualizarListView();
                limpiarCampos();
                mostrarMensaje("Usuario eliminado correctamente", "success");
            }
        });
    }

    @FXML
    private void actualizarLista() {
        cargarUsuarios();
    }

    @FXML
    private void limpiarCampos() {
        nuevoUsuario.clear();
        nuevaContrasena.clear();
        nuevoNombre.clear();
        nuevoEmail.clear();
        usuarioSeleccionado = null;
        listaUsuarios.getSelectionModel().clearSelection();
        mostrarMensaje("Campos limpiados", "info");
    }

    private void mostrarMensaje(String mensaje, String tipo) {
        mensajeLabel.setText(mensaje);
        switch (tipo) {
            case "error":
                mensajeLabel.setStyle("-fx-text-fill: #e74c3c;");
                break;
            case "success":
                mensajeLabel.setStyle("-fx-text-fill: #27ae60;");
                break;
            case "info":
                mensajeLabel.setStyle("-fx-text-fill: #3498db;");
                break;
        }
    }

    public void setUsuarioActual(String usuario) {
        this.usuarioActual = usuario;
        if (usuarioActualLabel != null) {
            usuarioActualLabel.setText("Usuario: " + usuario);
        }
    }
}
