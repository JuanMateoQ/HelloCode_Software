package GestionAprendizaje_Modulo.Controladores;

import GestionAprendizaje_Modulo.Modelo.Articulo;
import GestionAprendizaje_Modulo.Modelo.DocumentoPDF;
import GestionAprendizaje_Modulo.Modelo.RecursoAprendizaje;
import GestionAprendizaje_Modulo.Modelo.Video;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

/**
 * =================================================================================
 * Controlador para la Ventana de Recursos por Tema - Versión Corregida
 * =================================================================================
 * Este controlador recibe una lista de recursos y se encarga de mostrarlos
 * de forma atractiva en la interfaz. Se ha corregido para usar 'instanceof'
 * en lugar del método getTipo() que no existía.
 */
public class RecursosTemaController {

    @FXML private Label tituloTemaLabel;
    @FXML private VBox recursosContainerVBox;

    /**
     * Punto de entrada principal. El RutaController llamará a este método
     * para pasarle los datos que debe mostrar.
     * @param tema El nombre del tema para mostrar en el título.
     * @param recursos La lista de recursos a visualizar.
     */
    public void setRecursos(String tema, List<RecursoAprendizaje> recursos) {
        tituloTemaLabel.setText("Recursos para " + tema);

        recursosContainerVBox.getChildren().clear(); // Limpiar contenido previo

        if (recursos.isEmpty()) {
            Label noRecursosLabel = new Label("No hay recursos de apoyo para este tema.");
            noRecursosLabel.setStyle("-fx-text-fill: white; -fx-font-style: italic;");
            recursosContainerVBox.getChildren().add(noRecursosLabel);
            return;
        }

        for (RecursoAprendizaje recurso : recursos) {
            // Creamos un pequeño panel para cada recurso
            VBox recursoCard = crearTarjetaDeRecurso(recurso);
            recursosContainerVBox.getChildren().add(recursoCard);
        }
    }

    /**
     * Crea un componente visual (una "tarjeta") para un único recurso.
     */
    private VBox crearTarjetaDeRecurso(RecursoAprendizaje recurso) {
        VBox card = new VBox(5.0);
        card.setStyle("-fx-padding: 10; -fx-background-color: #FFFFFF1A; -fx-background-radius: 8;");

        String icono = getIconoParaRecurso(recurso);

        Label titulo = new Label(icono + " " + recurso.getTitulo());
        titulo.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 14px;");

        Label detalle = new Label(recurso.obtenerDetalle());
        detalle.setStyle("-fx-text-fill: #DCD6F7;");

        // El enlace ahora abrirá el recurso en una nueva ventana (Web.fxml) dentro de la app
        Hyperlink link = new Hyperlink("Abrir Recurso →");
        link.setOnAction(e -> abrirEnlace(recurso.getUrl()));

        card.getChildren().addAll(titulo, detalle, link);
        return card;
    }

    /**
     * ¡MÉTODO CORREGIDO!
     * Utiliza el operador 'instanceof' para determinar el tipo real del objeto
     * y devolver el ícono correspondiente. Esta es la forma correcta y segura.
     */
    private String getIconoParaRecurso(RecursoAprendizaje recurso) {
        if (recurso instanceof Video) return "▶️";
        if (recurso instanceof DocumentoPDF) return "📄";
        if (recurso instanceof Articulo) return "🌐";
        return "🔗"; // Ícono por defecto para cualquier otro tipo de recurso
    }

    /**
     * Abre una URL en una nueva ventana interna usando Web.fxml.
     */
    private void abrirEnlace(String url) {
        try {
            // Cargar la vista Web.fxml y su controlador
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionAprendizaje_Modulo/Vistas/Web.fxml"));
            Parent root = loader.load();
            WebController controller = loader.getController();

            // Normalizar la URL (agregar https:// si no hay protocolo)
            if (url != null && !url.matches("^(?i)(https?|ftp)://.*$")) {
                url = "https://" + url.trim();
            }

            // Pasar la URL al controlador para que la cargue en el WebView
            controller.loadUrl(url);
            controller.setWindowTitleFallback("Recurso");

            // Mostrar en una nueva ventana (formato móvil 360x640)
            Stage stage = new Stage();
            stage.setTitle("Recurso");
            stage.setScene(new Scene(root, 360, 640));
            stage.show();
        } catch (Exception e) {
            System.err.println("Error al abrir el recurso en la vista web: " + url);
            e.printStackTrace();
        }
    }

    @FXML
    private void volverARuta() {
        try {
            Stage stage = (Stage) recursosContainerVBox.getScene().getWindow();
            Conexion.MetodosFrecuentes.cambiarVentana(stage, "/GestionAprendizaje_Modulo/Vistas/Ruta.fxml", "Ruta");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}