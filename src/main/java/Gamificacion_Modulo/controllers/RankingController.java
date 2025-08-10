package Gamificacion_Modulo.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import Gamificacion_Modulo.clases.Main;
import Gamificacion_Modulo.clases.ProgresoEstudiante;
import Modulo_Usuario.Clases.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class RankingController implements Initializable {

    @FXML private Label titleLabel;
    @FXML private ComboBox<String> usuarioSelector;
    @FXML private VBox rankingEntriesContainer;
    @FXML private javafx.scene.image.ImageView profilePicture;

    // Botones de navegación inferior (IGUALES que HomeUsuarioController)
    @FXML private Button btnHome;
    @FXML private Button btnPerfil2;
    @FXML private Button btnRanking;
    @FXML private Button btnComunidad;
    @FXML private Button btnHomeUsuario;
    
    // Usuario actual será siempre el logueado
    private String currentUserName = null;
    // Ya no necesitamos índice porque siempre mostramos el usuario logueado
    // private int usuarioActualIndex = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println(">>> Controlador de Ranking inicializado");




        // Cargar usuarios en ComboBox
        cargarUsuarios();

        // Cargar y mostrar ranking inicial
        cargarRanking();
    }



    private void configurarHoverButton(Button button, String normalColor, String hoverColor) {
        button.setOnMouseEntered(e ->
                button.setStyle("-fx-background-color: " + hoverColor + "; -fx-background-radius: 5; -fx-border-radius: 5;"));
        button.setOnMouseExited(e ->
                button.setStyle("-fx-background-color: " + normalColor + "; -fx-background-radius: 5; -fx-border-radius: 5;"));
    }

    public void cargarRanking() {
        try {
            // Obtener solo usuarios con rol USUARIO (estudiantes)
            List<Usuario> usuariosEstudiantes = Main.getUsuariosEstudiantes();
            List<ProgresoEstudiante> progresos = ProgresoEstudiante.getProgresos();

            if (usuariosEstudiantes.isEmpty()) {
                mostrarMensajeVacio();
                return;
            }

            // Crear lista de usuarios con sus puntuaciones
            List<EstudianteRanking> estudiantesRanking = new ArrayList<>();
            
            for (Usuario usuario : usuariosEstudiantes) {
                ProgresoEstudiante progreso = encontrarProgresoPorUsuario(usuario, progresos);
                int puntos = progreso != null ? progreso.getPuntosTotal() : 0;
                estudiantesRanking.add(new EstudianteRanking(usuario.getNombre(), puntos));
            }

            // Ordenar por puntuación descendente
            estudiantesRanking.sort((e1, e2) -> Integer.compare(e2.getPuntos(), e1.getPuntos()));

            // Encontrar posición del usuario actual
            int posicionUsuario = encontrarPosicionUsuario(estudiantesRanking);

            // Actualizar título con posición
            String mensaje = currentUserName != null ?
                    currentUserName + " ocupas el puesto #" + posicionUsuario :
                    "Selecciona un usuario para ver su posición";
            titleLabel.setText(mensaje);

            // Actualizar imagen según la posición
            actualizarImagenRanking(posicionUsuario);

            // Obtener estudiantes para mostrar
            List<EstudianteRanking> topEstudiantes = estudiantesRanking.stream()
                    .limit(100)
                    .collect(Collectors.toList());

            // Crear entradas de ranking
            crearEntradasRanking(topEstudiantes);

        } catch (Exception e) {
            System.err.println("Error al cargar ranking: " + e.getMessage());
            e.printStackTrace();
            mostrarMensajeError();
        }
    }

    private ProgresoEstudiante encontrarProgresoPorUsuario(Usuario usuario, List<ProgresoEstudiante> progresos) {
        return progresos.stream()
                .filter(p -> p.getUsuario().getUsername().equals(usuario.getUsername()))
                .findFirst()
                .orElse(null);
    }

    private int encontrarPosicionUsuario(List<EstudianteRanking> estudiantesRanking) {
        if (currentUserName == null) {
            return 1; // Posición por defecto
        }

        for (int i = 0; i < estudiantesRanking.size(); i++) {
            if (estudiantesRanking.get(i).getNombre().equals(currentUserName)) {
                return i + 1; // Posición 1-indexada
            }
        }
        return estudiantesRanking.size(); // Si no se encuentra, última posición
    }

    private void actualizarImagenRanking(int posicion) {
        try {
            String nombreImagen;
            
            // Determinar qué imagen usar según la posición
            switch (posicion) {
                case 1:
                    nombreImagen = "Top1.png";
                    break;
                case 2:
                    nombreImagen = "Top2.png";
                    break;
                case 3:
                    nombreImagen = "Top3.png";
                    break;
                default:
                    nombreImagen = "Top.png";
                    break;
            }
            
            // Actualizar la imagen del ImageView
            if (profilePicture != null) {
                javafx.scene.image.Image nuevaImagen = new javafx.scene.image.Image(
                    getClass().getResourceAsStream("/Gamificacion_Modulo/images/" + nombreImagen)
                );
                profilePicture.setImage(nuevaImagen);
                System.out.println(">>> Imagen actualizada a: " + nombreImagen + " para posición: " + posicion);
            }
        } catch (Exception e) {
            System.err.println("Error al actualizar imagen de ranking: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void crearEntradasRanking(List<EstudianteRanking> topEstudiantes) {
        rankingEntriesContainer.getChildren().clear();

        for (int i = 0; i < topEstudiantes.size(); i++) {
            EstudianteRanking estudiante = topEstudiantes.get(i);

            // Container para cada entrada
            HBox rankingItem = new HBox();
            rankingItem.setAlignment(Pos.CENTER_LEFT);
            rankingItem.setPrefWidth(295);

            // Nombre del estudiante
            Label nameLabel = new Label(estudiante.getNombre());
            nameLabel.setFont(Font.font("Inter", FontWeight.BOLD, 18));

            // Espaciador
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // Puntuación
            Label expLabel = new Label(estudiante.getPuntos() + " EXP");
            expLabel.setFont(Font.font("Inter", FontWeight.BOLD, 18));

            // Resaltar usuario actual
            if (estudiante.getNombre().equals(currentUserName)) {
                nameLabel.setTextFill(Color.BLUE);
                expLabel.setTextFill(Color.BLUE);
            } else {
                nameLabel.setTextFill(Color.web("#2C2C54")); // Color más oscuro para mejor contraste
                expLabel.setTextFill(Color.web("#2C2C54"));
            }

            rankingItem.getChildren().addAll(nameLabel, spacer, expLabel);
            rankingEntriesContainer.getChildren().add(rankingItem);
        }
    }

    private void mostrarMensajeVacio() {
        rankingEntriesContainer.getChildren().clear();
        Label mensajeVacio = new Label("No hay estudiantes registrados");
        mensajeVacio.setFont(Font.font("Inter", FontWeight.NORMAL, 16));
        mensajeVacio.setTextFill(Color.web("#6C6C6C")); // Gris más oscuro para mejor visibilidad
        rankingEntriesContainer.getChildren().add(mensajeVacio);

        titleLabel.setText("Ranking no disponible");
        
        // Mostrar imagen por defecto
        actualizarImagenRanking(5); // Posición > 3 para mostrar Top.png
    }

    private void mostrarMensajeError() {
        rankingEntriesContainer.getChildren().clear();
        Label mensajeError = new Label("Error al cargar el ranking");
        mensajeError.setFont(Font.font("Inter", FontWeight.NORMAL, 16));
        mensajeError.setTextFill(Color.RED);
        rankingEntriesContainer.getChildren().add(mensajeError);

        titleLabel.setText("Error en el ranking");
        
        // Mostrar imagen por defecto
        actualizarImagenRanking(5); // Posición > 3 para mostrar Top.png
    }


    // Métodos de navegación
    @FXML
    private void navButton1() {
        System.out.println(">>> Navegando a Desafíos desde Ranking");
        try {
            Main.cambiarEscena("/Gamificacion_Modulo/fxml/Desafios.fxml");
        } catch (Exception e) {
            System.err.println("Error al navegar a Desafios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void navButton2() {
        System.out.println(">>> Navegando a Perfil de Usuario desde Ranking");
        try {
            Main.cambiarEscena("/Gamificacion_Modulo/fxml/PerfilUsuario.fxml");
        } catch (Exception e) {
            System.err.println("Error al navegar a PerfilUsuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void navButton3() {
        System.out.println(">>> Ya estás en la pantalla de Ranking");
        // Refrescar ranking
        cargarRanking();
    }

    private void cargarUsuarios() {
        try {
            if (usuarioSelector != null) {
                usuarioSelector.getItems().clear();

                // Obtener solo el usuario logueado actual
                Usuario usuarioLogueado = Main.getUsuarioLogueado();

                if (usuarioLogueado != null) {
                    usuarioSelector.getItems().add(usuarioLogueado.getNombre());
                    usuarioSelector.getSelectionModel().selectFirst();
                    currentUserName = usuarioLogueado.getNombre();
                    System.out.println(">>> Usuario logueado cargado en ComboBox: " + usuarioLogueado.getNombre());
                } else {
                    usuarioSelector.getItems().add("No hay usuario logueado");
                    usuarioSelector.getSelectionModel().selectFirst();
                    currentUserName = "No hay usuario logueado";
                    System.out.println(">>> No hay usuario logueado");
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar usuario logueado: " + e.getMessage());
        }
    }

    @FXML
    private void cambiarUsuario() {
        try {
            if (usuarioSelector != null && usuarioSelector.getSelectionModel().getSelectedIndex() >= 0) {
                // Ya no cambiamos índice, siempre es el usuario logueado
                currentUserName = usuarioSelector.getValue();
                System.out.println(">>> Usuario seleccionado (siempre el logueado): " + currentUserName);

                // Recargar ranking con nuevo usuario
                cargarRanking();
            }
        } catch (Exception e) {
            System.err.println("Error al cambiar usuario: " + e.getMessage());
        }
    }

    // Método público para actualizar la información cuando se actualicen los datos
    public void actualizarDatosUsuario() {
        cargarUsuarios();
        cargarRanking();
    }

    // Métodos de navegación de la barra inferior (EXACTAMENTE IGUALES que PerfilUsuarioController)
    @FXML
    private void irAHome() {
        try {
            // Navegar al módulo de gestión de aprendizaje
            javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/GestionAprendizaje_Modulo/Vistas/Ruta.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(fxmlLoader.load(), 360, 640);

            javafx.stage.Stage currentStage = (javafx.stage.Stage) btnHome.getScene().getWindow();
            currentStage.setTitle("Ruta de Aprendizaje");
            currentStage.setScene(scene);
            currentStage.centerOnScreen();
        } catch (Exception e) {
            System.err.println("Error al navegar a Inicio: " + e.getMessage());
        }
    }

    @FXML
    private void irAPerfil() {
        try {
            // PASO 1: Inicializar el backend del módulo de gamificación
            Main.inicializarDesdeModuloExterno();
            // Cargar PerfilUsuario.fxml
            Main.cambiarEscena("/Gamificacion_Modulo/fxml/PerfilUsuario.fxml");
        } catch (Exception e) {
            System.err.println("Error al navegar a Perfil: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void irARanking() {
        // Ya estamos en ranking, solo actualizar datos
        cargarRanking();
    }

    @FXML
    private void irAComunidad() {
        try {
            // Navegar al módulo de comunidad
            javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/Modulo_Comunidad/Views/Comunidad.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(fxmlLoader.load(), 360, 640);

            javafx.stage.Stage currentStage = (javafx.stage.Stage) btnComunidad.getScene().getWindow();
            currentStage.setTitle("Comunidad");
            currentStage.setScene(scene);
            currentStage.centerOnScreen();
        } catch (Exception e) {
            System.err.println("Error al navegar a Comunidad: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void irAHomeUsuario() {
        try {
            // Navegar al módulo de usuario (homeUsuario)
            javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/Modulo_Usuario/views/homeUsuario.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(fxmlLoader.load(), 360, 640);

            javafx.stage.Stage currentStage = (javafx.stage.Stage) btnHomeUsuario.getScene().getWindow();
            currentStage.setTitle("Hello Code Software - Panel Principal");
            currentStage.setScene(scene);
            currentStage.centerOnScreen();
        } catch (Exception e) {
            System.err.println("Error al navegar a HomeUsuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Clase interna para manejar datos de ranking
    private static class EstudianteRanking {
        private String nombre;
        private int puntos;

        public EstudianteRanking(String nombre, int puntos) {
            this.nombre = nombre;
            this.puntos = puntos;
        }

        public String getNombre() { return nombre; }
        public int getPuntos() { return puntos; }
    }

} 