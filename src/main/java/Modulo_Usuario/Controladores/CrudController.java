package Modulo_Usuario.Controladores;

import Modulo_Usuario.Clases.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CrudController {
    @FXML private ListView<Usuario> listaUsuarios;
    @FXML private TextField nuevoUsuario;
    @FXML private PasswordField nuevaContrasena;
    @FXML private TextField nuevoNombre;
    @FXML private TextField nuevoEmail;
    @FXML private Label mensajeLabel;
    @FXML private Label usuarioActualLabel;
    @FXML private Label contadorUsuarios;
    @FXML private VBox mensajeContainer;

    // Nuevos controles para tipos de usuario
    @FXML private ComboBox<String> tipoUsuarioCombo;
    @FXML private VBox camposComunidad;
    @FXML private ComboBox<NivelJava> nivelJavaCombo;
    @FXML private TextField reputacionField;

    private final String ARCHIVO_USUARIOS = "src/main/java/Modulo_Usuario/Usuarios/usuarios.txt";
    private List<Usuario> usuarios = new ArrayList<>();
    private Usuario usuarioSeleccionado = null;
    private String usuarioActual = "Admin";

    @FXML
    public void initialize() {
        configurarComboBoxes();
        configurarListView();
        configurarEventos();
        cargarUsuarios();
        usuarioActualLabel.setText("Panel de administración");

        // Inicializar el contador
        actualizarContadorUsuarios();

        // Ocultar mensaje inicialmente
        if (mensajeContainer != null) {
            mensajeContainer.setVisible(false);
        }
    }

    private void configurarComboBoxes() {
        // Configurar tipos de usuario
        ObservableList<String> tiposUsuario = FXCollections.observableArrayList(
                "Usuario Básico", "Usuario Comunidad"
        );
        tipoUsuarioCombo.setItems(tiposUsuario);
        tipoUsuarioCombo.getSelectionModel().selectFirst();

        // Configurar niveles de Java
        ObservableList<NivelJava> nivelesJava = FXCollections.observableArrayList(NivelJava.values());
        nivelJavaCombo.setItems(nivelesJava);
        nivelJavaCombo.getSelectionModel().selectFirst();
    }

    private void configurarEventos() {
        // Mostrar/ocultar campos según el tipo de usuario seleccionado
        tipoUsuarioCombo.setOnAction(e -> mostrarCamposEspecificos());
    }

    private void mostrarCamposEspecificos() {
        String tipoSeleccionado = tipoUsuarioCombo.getValue();

        // Ocultar campos específicos
        camposComunidad.setVisible(false);

        // Mostrar campos según el tipo
        switch (tipoSeleccionado) {
            case "Usuario Comunidad":
                camposComunidad.setVisible(true);
                break;
        }
    }

    private void configurarListView() {
        // Configurar la celda personalizada para mostrar información del usuario
        listaUsuarios.setCellFactory(param -> new ListCell<Usuario>() {
            @Override
            protected void updateItem(Usuario usuario, boolean empty) {
                super.updateItem(usuario, empty);
                if (empty || usuario == null) {
                    setText(null);
                    setStyle("");
                } else {
                    String tipo = obtenerTipoUsuario(usuario);
                    String info = String.format("👤 %s - %s (%s) [%s]",
                            usuario.getUsername(),
                            usuario.getNombre() != null ? usuario.getNombre() : "Sin nombre",
                            usuario.getEmail() != null ? usuario.getEmail() : "Sin email",
                            tipo);
                    setText(info);

                    // Estilo más visible para las celdas
                    setStyle("-fx-background-color: #f8f9fa; " +
                            "-fx-text-fill: #2d3436; " +
                            "-fx-font-size: 14px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-padding: 8px; " +
                            "-fx-border-color: #dee2e6; " +
                            "-fx-border-width: 0 0 1 0;");

                    // Resaltar selección
                    setOnMouseEntered(e -> {
                        if (!isEmpty()) {
                            setStyle("-fx-background-color: #e3f2fd; " +
                                    "-fx-text-fill: #1976d2; " +
                                    "-fx-font-size: 14px; " +
                                    "-fx-font-weight: bold; " +
                                    "-fx-padding: 8px; " +
                                    "-fx-border-color: #2196f3; " +
                                    "-fx-border-width: 0 0 1 0;");
                        }
                    });

                    setOnMouseExited(e -> {
                        if (!isEmpty() && !isSelected()) {
                            setStyle("-fx-background-color: #f8f9fa; " +
                                    "-fx-text-fill: #2d3436; " +
                                    "-fx-font-size: 14px; " +
                                    "-fx-font-weight: bold; " +
                                    "-fx-padding: 8px; " +
                                    "-fx-border-color: #dee2e6; " +
                                    "-fx-border-width: 0 0 1 0;");
                        }
                    });

                    // Estilo para elemento seleccionado
                    if (isSelected()) {
                        setStyle("-fx-background-color: #2196f3; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 14px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 8px; " +
                                "-fx-border-color: #1976d2; " +
                                "-fx-border-width: 0 0 1 0;");
                    }
                }
            }
        });
    }

    private String obtenerTipoUsuario(Usuario usuario) {
        if (usuario instanceof UsuarioComunidad) {
            return "Comunidad";
        } else {
            // Usar el rol para determinar el tipo
            if (usuario.getRol() == Roles.ADMINISTRADOR) {
                return "Admin";
            } else {
                return "Básico";
            }
        }
    }

    private void cargarUsuarios() {
        usuarios.clear();
        try {
            File file = new File(ARCHIVO_USUARIOS);
            if (!file.exists()) {
                mostrarMensaje("Error: No se encontró el archivo usuarios.txt en: " + ARCHIVO_USUARIOS, "error");
                return;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                String linea;
                int contador = 0;
                while ((linea = br.readLine()) != null) {
                    linea = linea.trim();
                    if (!linea.isEmpty()) {
                        Usuario usuario = Usuario.fromString(linea);
                        if (usuario != null) {
                            usuarios.add(usuario);
                            contador++;
                            System.out.println("Usuario cargado: " + usuario.getUsername() + " - Rol: " + usuario.getRol());
                        }
                    }
                }

                actualizarListView();
                actualizarContadorUsuarios();
                mostrarMensaje("✅ Usuarios cargados correctamente: " + contador, "success");

            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al cargar usuarios: " + e.getMessage(), "error");
        }
    }

    private void actualizarListView() {
        ObservableList<Usuario> observableList = FXCollections.observableArrayList(usuarios);
        listaUsuarios.setItems(observableList);

        // Debug: verificar que la lista se está actualizando
        System.out.println("ListView actualizado con " + usuarios.size() + " usuarios:");
        for (Usuario u : usuarios) {
            System.out.println("- " + u.getUsername() + " (" + u.getNombre() + ")");
        }

        // Forzar refresh del ListView
        listaUsuarios.refresh();

        actualizarContadorUsuarios();
    }

    private void actualizarContadorUsuarios() {
        if (contadorUsuarios != null) {
            int total = usuarios.size();
            contadorUsuarios.setText(total + (total == 1 ? " usuario" : " usuarios"));
        }
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
        String tipoSeleccionado = tipoUsuarioCombo.getValue();

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

        Usuario nuevoUsuario = crearUsuarioSegunTipo(username, password, nombre, email, tipoSeleccionado);

        if (nuevoUsuario != null) {
            usuarios.add(nuevoUsuario);
            guardarUsuarios();
            actualizarListView();
            limpiarCampos();
            mostrarMensaje("Usuario agregado correctamente", "success");
        }
    }

    private Usuario crearUsuarioSegunTipo(String username, String password, String nombre, String email, String tipo) {
        switch (tipo) {
            case "Usuario Básico":
                Roles rolBasico = Roles.USUARIO;
                return new Usuario(username, password, nombre, email, 0, rolBasico);

            case "Usuario Comunidad":
                NivelJava nivelJava = nivelJavaCombo.getValue();
                Integer reputacion = 0;
                try {
                    reputacion = Integer.parseInt(reputacionField.getText().trim());
                } catch (NumberFormatException e) {
                    reputacion = 0;
                }
                return new UsuarioComunidad(username, password, nombre, email, username, nivelJava, reputacion);

            default:
                mostrarMensaje("Tipo de usuario no válido", "error");
                return null;
        }
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

        // Configurar tipo de usuario
        String tipo = obtenerTipoUsuario(usuarioSeleccionado);
        switch (tipo) {
            case "Comunidad":
                tipoUsuarioCombo.setValue("Usuario Comunidad");
                if (usuarioSeleccionado instanceof UsuarioComunidad) {
                    UsuarioComunidad uc = (UsuarioComunidad) usuarioSeleccionado;
                    nivelJavaCombo.setValue(uc.getNivelJava());
                    reputacionField.setText(uc.getReputacion().toString());
                }
                break;
            case "Admin":
                // Los administradores ahora se manejan como usuarios básicos con rol ADMINISTRADOR
                tipoUsuarioCombo.setValue("Usuario Básico");
                break;
            default:
                tipoUsuarioCombo.setValue("Usuario Básico");
        }

        mostrarCamposEspecificos();
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
        reputacionField.clear();
        tipoUsuarioCombo.getSelectionModel().selectFirst();
        usuarioSeleccionado = null;
        listaUsuarios.getSelectionModel().clearSelection();
        mostrarCamposEspecificos();
        mostrarMensaje("Campos limpiados", "info");
    }

    @FXML
    public void regresarAlHome() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Modulo_Usuario/views/home.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 360, 640);

            Stage stage = new Stage();
            stage.setTitle("Hello Code Software - Panel Principal");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            // Cerrar la pantalla actual
            Stage thisStage = (Stage) listaUsuarios.getScene().getWindow();
            thisStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Error al regresar al home: " + e.getMessage(), "error");
        }
    }

    private void mostrarMensaje(String mensaje, String tipo) {
        if (mensajeLabel != null) {
            mensajeLabel.setText(mensaje);

            // Mostrar el contenedor de mensajes
            if (mensajeContainer != null) {
                mensajeContainer.setVisible(true);
            }

            switch (tipo) {
                case "error":
                    mensajeLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    if (mensajeContainer != null) {
                        mensajeContainer.setStyle("-fx-background-color: #ffe6e6; -fx-background-radius: 12; -fx-border-color: #e74c3c; -fx-border-radius: 12; -fx-border-width: 1; -fx-effect: dropshadow(three-pass-box, rgba(231,76,60,0.2), 6, 0, 0, 2);");
                    }
                    break;
                case "success":
                    mensajeLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    if (mensajeContainer != null) {
                        mensajeContainer.setStyle("-fx-background-color: #e8f5e8; -fx-background-radius: 12; -fx-border-color: #27ae60; -fx-border-radius: 12; -fx-border-width: 1; -fx-effect: dropshadow(three-pass-box, rgba(39,174,96,0.2), 6, 0, 0, 2);");
                    }
                    break;
                case "info":
                    mensajeLabel.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
                    if (mensajeContainer != null) {
                        mensajeContainer.setStyle("-fx-background-color: #e6f3ff; -fx-background-radius: 12; -fx-border-color: #3498db; -fx-border-radius: 12; -fx-border-width: 1; -fx-effect: dropshadow(three-pass-box, rgba(52,152,219,0.2), 6, 0, 0, 2);");
                    }
                    break;
            }

            // Auto-ocultar mensajes después de 5 segundos (excepto errores)
            if (!tipo.equals("error")) {
                new Thread(() -> {
                    try {
                        Thread.sleep(5000);
                        javafx.application.Platform.runLater(() -> {
                            if (mensajeContainer != null) {
                                mensajeContainer.setVisible(false);
                            }
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            }
        }
    }

    public void setUsuarioActual(String usuario) {
        this.usuarioActual = usuario;
        if (usuarioActualLabel != null) {
            usuarioActualLabel.setText("Usuario: " + usuario);
        }
    }
}
