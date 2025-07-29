package Gamificacion_Modulo.controllers;

import Gamificacion_Modulo.clases.Desafio;
import Gamificacion_Modulo.clases.DesafioMensual;
import Gamificacion_Modulo.clases.DesafioSemanal;
import Gamificacion_Modulo.clases.Logro;
import Gamificacion_Modulo.clases.Main;
import Gamificacion_Modulo.clases.ProgresoEstudiante;
import Modulo_Usuario.Clases.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PerfilUsuarioController {
    @FXML
    private VBox root;

    @FXML
    private Label userName;

    @FXML 
    private VBox contenido;
    
    @FXML
    private Label userEmail;
    
    @FXML
    private Label expPoints;
    
    @FXML
    private Label desafiosSemanales;
    
    @FXML
    private Label desafiosMensuales;
    
    @FXML
    private Hyperlink verProgresoDesafios;
    
    @FXML
    private Hyperlink verProgresoLogros;
    
    @FXML
    private HBox achievementList;
    
    @FXML
    private ImageView profileImage;

    @FXML
    private HBox navigationBar;
    
    @FXML
    private ComboBox<String> estudianteSelector;
    
    @FXML
    private Button volverComunidadBtn;
    
    @FXML
    private Button adminBtn;
    
    // Ya no necesitamos índice porque siempre mostramos el usuario logueado
    // private int estudianteActualIndex = 0;


    //TODO: BORRAR LOS PRINTLN DE DEBUG CUANDO SE HAYA TERMINADO EL DESARROLLO, TAMBIEN CORREGIR EL MAIN. PARA METODOS GLOBALES

    // Botón 1 (izquierda) - navegar a Desafíos
    @FXML
    private void navButton1() {
        System.out.println(">>> Navegando a Desafíos desde Perfil de Usuario");
        try {
            // Cargar Desafios.fxml desde la carpeta fxml
            Main.cambiarEscena("/Gamificacion_Modulo/fxml/Desafios.fxml");
        } catch (Exception e) {
            System.err.println("Error al navegar a Desafios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Botón 2 (centro) - Perfil - se queda en PerfilUsuario
    @FXML
    private void navButton2() {
        System.out.println(">>> Ya estás en la pantalla de Perfil de Usuario");
        // Ya estamos en la pantalla de perfil, actualizar datos
        actualizarDatosPerfil();
    }

    // Botón 3 (derecha) - navegar a Ranking
    @FXML
    private void navButton3() {
        System.out.println(">>> Navegando a Ranking desde Perfil de Usuario");
        try {
            // Cargar Ranking.fxml
            Main.cambiarEscena("/Gamificacion_Modulo/fxml/Ranking.fxml");
        } catch (Exception e) {
            System.err.println("Error al navegar a Ranking: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para cambiar de estudiante (ya no necesario, siempre mostramos el usuario logueado)
    @FXML
    private void cambiarEstudiante() {
        if (estudianteSelector != null && estudianteSelector.getSelectionModel().getSelectedIndex() >= 0) {
            // Ya no cambiamos índice, siempre es el usuario logueado
            System.out.println(">>> Usuario seleccionado (siempre el logueado): " + estudianteSelector.getValue());
            actualizarDatosPerfil();
        }
    }
    
    // Método para volver al módulo de usuarios
    @FXML
    private void regresarHomeUsuario() {
        try {
            System.out.println(">>> Regresando al módulo de usuarios");
            
            // Cargar la pantalla de Home del módulo de usuarios
            javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/Modulo_Usuario/views/homeUsuario.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(fxmlLoader.load(), 360, 720);

            // Obtener el Stage actual
            javafx.stage.Stage currentStage = (javafx.stage.Stage) volverComunidadBtn.getScene().getWindow();
            
            // Cambiar la escena
            currentStage.setTitle("Hello Code Software - Panel Principal");
            currentStage.setScene(scene);
            currentStage.centerOnScreen();
            
        } catch (Exception e) {
            System.err.println("Error al volver al módulo de usuarios: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // TODO: Corregir el Main.getprogresos() para que no use el Main directamente, sino que use un método de acceso
        // Método para mostrar progreso de desafíos
    @FXML
    private void verProgresoDesafios() {
        System.out.println(">>> Mostrando progreso de desafíos del usuario logueado");
        try {
            StringBuilder mensaje = new StringBuilder();
            mensaje.append("📋 PROGRESO DE DESAFÍOS\n");
            mensaje.append("========================\n\n");
            
            ProgresoEstudiante progresoLogueado = Main.getProgresoUsuarioLogueado();
            
            if (progresoLogueado != null) {
                int semanalesActivos = 0;
                int mensualesActivos = 0;
                
                for (Desafio desafio : progresoLogueado.getDesafiosActivos()) {
                    if (desafio instanceof DesafioSemanal) {
                        semanalesActivos++;
                    } else if (desafio instanceof DesafioMensual) {
                        mensualesActivos++;
                    }
                }
                
                mensaje.append("🎯 DESAFÍOS ACTIVOS:\n");
                mensaje.append("   Semanales: ").append(semanalesActivos).append("\n");
                mensaje.append("   Mensuales: ").append(mensualesActivos).append("\n");
                mensaje.append("   Total activos: ").append(progresoLogueado.getDesafiosActivos().size()).append("\n\n");
                
                mensaje.append("📊 TOTAL DESAFÍOS COMPLETADOS: ").append(progresoLogueado.getDesafiosCompletados()).append("\n\n");
                
                // Mostrar detalles de desafíos activos
                if (!progresoLogueado.getDesafiosActivos().isEmpty()) {
                    mensaje.append("📋 DETALLE DE DESAFÍOS ACTIVOS:\n");
                    int contadorDesafios = 1;
                    for (Desafio desafio : progresoLogueado.getDesafiosActivos()) {
                        String tipo = desafio instanceof DesafioSemanal ? "Semanal" : "Mensual";
                        String progreso_str = "";
                        String nombreDesafio = "";
                        
                        if (desafio instanceof DesafioSemanal) {
                            DesafioSemanal ds = (DesafioSemanal) desafio;
                            progreso_str = " - " + ds.getLeccionesCompletadas() + "/" + ds.getMetaSemanal() +
                                         " (" + String.format("%.1f", ds.getProgreso()) + "%)";
                            nombreDesafio = "Desafío Semanal #" + contadorDesafios + " (Meta: " + ds.getMetaSemanal() + " lecciones)";
                        } else if (desafio instanceof DesafioMensual) {
                            DesafioMensual dm = (DesafioMensual) desafio;
                            Integer objetivo = (dm.getObjetivoMensual() != null) ? dm.getObjetivoMensual() : 1;
                            Integer completadas = (dm.getActividadesCompletadas() != null) ? dm.getActividadesCompletadas() : 0;
                            progreso_str = " - " + completadas + "/" + objetivo + 
                                         " (" + String.format("%.1f", dm.getProgreso()) + "%)";
                            nombreDesafio = "Desafío Mensual #" + contadorDesafios + " (Meta: " + objetivo + " actividades)";
                        }
                        
                        mensaje.append("   • ").append(nombreDesafio).append(" (").append(tipo).append(")").append(progreso_str).append("\n");
                        contadorDesafios++;
                    }
                }
            } else {
                mensaje.append("No hay datos de progreso disponibles para el usuario logueado.");
            }
            
            mostrarAlerta("Progreso de Desafíos", mensaje.toString());
            
        } catch (Exception e) {
            System.err.println("Error al mostrar progreso de desafíos: " + e.getMessage());
            mostrarAlerta("Error", "No se pudo cargar el progreso de desafíos.");
        }
    }

    // Método para mostrar progreso de logros
    @FXML
    private void verProgresoLogros() {
        System.out.println(">>> Mostrando progreso de logros del usuario logueado");
        try {
            StringBuilder mensaje = new StringBuilder();
            mensaje.append("🏆 PROGRESO DE LOGROS\n");
            mensaje.append("=====================\n\n");
            
            ProgresoEstudiante progresoLogueado = Main.getProgresoUsuarioLogueado();
            
            if (progresoLogueado != null) {
                mensaje.append("🎖️ LOGROS OBTENIDOS (").append(progresoLogueado.getLogros().size()).append("):\n");
                if (progresoLogueado.getLogros().isEmpty()) {
                    mensaje.append("   Aún no has obtenido ningún logro.\n\n");
                } else {
                    for (Logro logro : progresoLogueado.getLogros()) {
                        mensaje.append("   ✅ ").append(logro.getNombre())
                               .append(" (+").append(logro.getPuntos()).append(" pts)\n");
                        mensaje.append("      ").append(logro.getDescripcion()).append("\n\n");
                    }
                }
                //TODO: Corregir el getLogrosDisponibles() para que no use el Main directamente, sino que use un método de acceso

                mensaje.append("🏅 LOGROS DISPONIBLES (").append(Main.getLogrosDisponibles().size()).append("):\n");
                for (Logro logro : Main.getLogrosDisponibles()) {
                    boolean obtenido = progresoLogueado.getLogros().contains(logro);
                    String estado = obtenido ? "✅" : "❌";
                    mensaje.append("   ").append(estado).append(" ").append(logro.getNombre())
                           .append(" (+").append(logro.getPuntos()).append(" pts)\n");
                    mensaje.append("      ").append(logro.getDescripcion()).append("\n\n");
                }
                
                int puntosLogros = progresoLogueado.getLogros().stream().mapToInt(Logro::getPuntos).sum();
                mensaje.append("💰 PUNTOS TOTALES POR LOGROS: ").append(puntosLogros);
            } else {
                mensaje.append("No hay datos de progreso disponibles para el usuario logueado.");
            }
            
            mostrarAlerta("Progreso de Logros", mensaje.toString());
            
        } catch (Exception e) {
            System.err.println("Error al mostrar progreso de logros: " + e.getMessage());
            mostrarAlerta("Error", "No se pudo cargar el progreso de logros.");
        }
    }

    @FXML
    private void initialize() {
        try {
            System.out.println(">>> Controlador de Perfil de Usuario inicializado");
            
            // Registrar este controlador para recibir notificaciones
            
            // Cargar estudiantes y datos iniciales
            cargarEstudiantesEnComboBox();
            actualizarDatosPerfil();

        } catch (Exception e) {
            System.err.println("Error al inicializar la interfaz de Perfil: " + e.getMessage());
        }
    }

    public void actualizarDatosPerfil() {
        try {
            // Obtener el usuario logueado y su progreso
            Usuario usuarioLogueado = Main.getUsuarioLogueado();
            ProgresoEstudiante progresoLogueado = Main.getProgresoUsuarioLogueado();
            
            if (usuarioLogueado == null) {
                System.out.println(">>> No hay usuario logueado, no se pueden actualizar datos del perfil");
                // Limpiar la interfaz
                if (userName != null) userName.setText("No hay usuario logueado");
                if (userEmail != null) userEmail.setText("");
                if (expPoints != null) expPoints.setText("0");
                if (desafiosSemanales != null) desafiosSemanales.setText("0");
                if (desafiosMensuales != null) desafiosMensuales.setText("0");
                return;
            }
            
            if (progresoLogueado == null) {
                System.out.println(">>> No hay progreso para el usuario logueado: " + usuarioLogueado.getNombre());
                // Mostrar datos básicos del usuario pero sin progreso
                if (userName != null) userName.setText(usuarioLogueado.getNombre());
                if (userEmail != null) userEmail.setText("@" + usuarioLogueado.getEmail().split("@")[0]);
                if (expPoints != null) expPoints.setText("0");
                if (desafiosSemanales != null) desafiosSemanales.setText("0");
                if (desafiosMensuales != null) desafiosMensuales.setText("0");
                return;
            }
            
            System.out.println(">>> Actualizando perfil del usuario logueado: " + usuarioLogueado.getNombre() + " con " + progresoLogueado.getPuntosTotal() + " puntos");
            
            // Actualizar ComboBox para mostrar solo el usuario logueado
            if (estudianteSelector != null && 
                (estudianteSelector.getItems().isEmpty() || 
                 !estudianteSelector.getItems().get(0).equals(usuarioLogueado.getNombre()))) {
                cargarEstudiantesEnComboBox();
            }
            
            // Actualizar información básica del usuario logueado
            if (userName != null) userName.setText(usuarioLogueado.getNombre());
            if (userEmail != null) userEmail.setText("@" + usuarioLogueado.getEmail().split("@")[0]);
            if (expPoints != null) expPoints.setText(String.valueOf(progresoLogueado.getPuntosTotal()));
            
            // Contar desafíos por tipo del usuario logueado
            int semanales = 0, mensuales = 0;
            for (Desafio desafio : progresoLogueado.getDesafiosActivos()) {
                if (desafio instanceof DesafioSemanal) {
                    semanales++;
                } else if (desafio instanceof DesafioMensual) {
                    mensuales++;
                }
            }
            
            if (desafiosSemanales != null) desafiosSemanales.setText(String.valueOf(semanales));
            if (desafiosMensuales != null) desafiosMensuales.setText(String.valueOf(mensuales));
            
            // Actualizar logros del usuario logueado
            actualizarLogrosVisibles(progresoLogueado);
            
        } catch (Exception e) {
            System.err.println("Error al actualizar datos del perfil: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Método para cargar usuarios en el ComboBox
    private void cargarEstudiantesEnComboBox() {
        try {
            // Obtener solo el usuario logueado actual
            Usuario usuarioLogueado = Main.getUsuarioLogueado();
            
            if (usuarioLogueado == null) {
                System.out.println(">>> No hay usuario logueado, no se puede cargar ComboBox");
                if (estudianteSelector != null) {
                    estudianteSelector.getItems().clear();
                    estudianteSelector.getItems().add("No hay usuario logueado");
                    estudianteSelector.getSelectionModel().selectFirst();
                }
                return;
            }
            
            System.out.println(">>> Cargando usuario logueado en ComboBox: " + usuarioLogueado.getNombre());
            
            if (estudianteSelector != null) {
                estudianteSelector.getItems().clear();
                // Solo agregar el usuario logueado actual
                estudianteSelector.getItems().add(usuarioLogueado.getNombre());
                estudianteSelector.getSelectionModel().selectFirst();
                // Ya no necesitamos índice
                System.out.println(">>> ComboBox cargado exitosamente con el usuario logueado: " + usuarioLogueado.getNombre());
            } else {
                System.out.println(">>> ComboBox no pudo cargarse - selector es null");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar usuario logueado en ComboBox: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void actualizarLogrosVisibles(ProgresoEstudiante progreso) {
        try {
            if (achievementList != null) {
                achievementList.getChildren().clear();
                
                // Mostrar hasta 3 logros obtenidos
                int logrosAMostrar = Math.min(3, progreso.getLogros().size());
                
                if (logrosAMostrar == 0) {
                    // Mostrar mensaje si no hay logros
                    Label noLogros = new Label("Sin logros aún");
                    noLogros.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
                    achievementList.getChildren().add(noLogros);
                } else {
                    for (int i = 0; i < logrosAMostrar; i++) {
                        Logro logro = progreso.getLogros().get(i);
                        VBox logroItem = crearElementoLogro(logro);
                        achievementList.getChildren().add(logroItem);
                    }
                    
                    // Si hay más logros, mostrar indicador
                    if (progreso.getLogros().size() > 3) {
                        Label masLogros = new Label("+" + (progreso.getLogros().size() - 3) + " más");
                        masLogros.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
                        achievementList.getChildren().add(masLogros);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al actualizar logros visibles: " + e.getMessage());
        }
    }
    
    private VBox crearElementoLogro(Logro logro) {
        VBox logroItem = new VBox();
        logroItem.setAlignment(javafx.geometry.Pos.CENTER);
        logroItem.getStyleClass().add("achievement-item");
        
        // Imagen del logro (usar una imagen por defecto basada en el nombre)
        ImageView imagen = new ImageView();
        imagen.setFitWidth(60);
        imagen.setFitHeight(60);
        
        String imagenUrl = "";
        if (logro.getNombre().toLowerCase().contains("principiante")) {
            imagenUrl = "/Gamificacion_Modulo/images/logro_principiante.png";
        } else if (logro.getNombre().toLowerCase().contains("dedicado")) {
            imagenUrl = "/Gamificacion_Modulo/images/logro_dedicado.png";
        } else if (logro.getNombre().toLowerCase().contains("acumulador")) {
            imagenUrl = "/Gamificacion_Modulo/images/logro_acumulador.png";
        } else {
            imagenUrl = "/Gamificacion_Modulo/images/logro_principiante.png"; // Por defecto
        }
        
        try {
            Image img = new Image(getClass().getResourceAsStream(imagenUrl));
            imagen.setImage(img);
        } catch (Exception e) {
            System.err.println("No se pudo cargar imagen del logro: " + imagenUrl);
        }
        
        Label nombre = new Label(logro.getNombre());
        nombre.setStyle("-fx-font-size: 10px; -fx-text-alignment: center;");
        
        logroItem.getChildren().addAll(imagen, nombre);
        return logroItem;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private Button createNavButton(String imageUrl, String text) {
        Button button = new Button();
        button.getStyleClass().add("nav-button");

        ImageView icon = new ImageView(new Image(getClass().getResource(imageUrl).toExternalForm()));
        icon.setFitWidth(24);
        icon.setFitHeight(24);

        button.setGraphic(icon);
        return button;
    }

    // Métodos públicos para actualizar la información
    public void actualizarDatosUsuario(String nombre, String tag, String experiencia) {
        if (userName != null) userName.setText(nombre);
        if (userEmail != null) userEmail.setText("@" + tag);
        if (expPoints != null) expPoints.setText(experiencia);
    }
    


}
