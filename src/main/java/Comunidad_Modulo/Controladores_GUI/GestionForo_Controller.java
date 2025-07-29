package Comunidad_Modulo.Controladores_GUI;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

// Importar las clases del modelo y controladores
import Comunidad_Modulo.controladores.ContextoSistema;
import Comunidad_Modulo.modelo.*;
import Modulo_Usuario.Clases.NivelJava;
import Modulo_Usuario.Clases.UsuarioComunidad;
import Comunidad_Modulo.enums.TipoTema;
import Comunidad_Modulo.enums.TipoSolucion;
import Comunidad_Modulo.enums.EstadoHilo;
import Comunidad_Modulo.servicios.ComunidadService;
import Comunidad_Modulo.servicios.PersistenciaService;
import MetodosGlobales.MetodosFrecuentes;
import MetodosGlobales.SesionManager;

/**
 * Controlador para la gestión del foro en la interfaz JavaFX
 * Maneja grupos de discusión, grupos de compartir, hilos y soluciones
 * Implementa los principios SOLID y se integra con los controladores existentes
 */
public class GestionForo_Controller implements Initializable {

    // Referencias FXML a los elementos de la interfaz
    @FXML private TextField txtTituloGrupo;
    @FXML private ComboBox<NivelJava> comboNivelJava;
    @FXML private ComboBox<TipoTema> comboTema;

    @FXML private ComboBox<String> comboAccionHilo;
    @FXML private Button btnGestionarHilos;

    @FXML private ComboBox<String> comboAccionSolucion;
    @FXML private Button btnGestionarSoluciones;

    // Elementos de las nuevas pestañas y formulario overlay
    @FXML private VBox formCrearGrupo;
    @FXML private Label lblTituloFormulario;
    @FXML private Button btnConfirmarCrearGrupo;
    @FXML private Button btnCancelarCrearGrupo;
    @FXML private VBox vboxPrincipal;

    // Pestaña Discusión
    @FXML private Button btnUnirseGrupoDiscusion;
    @FXML private Button btnCrearGrupoDiscusion;
    @FXML private Button btnListarGruposDiscusion;
    @FXML private ComboBox<String> comboAccionRespuesta;
    @FXML private Button btnGestionarRespuestas;
    @FXML private TextArea txtInformacionDiscusion;

    // Pestaña Compartir
    @FXML private Button btnUnirseGrupoCompartir;
    @FXML private Button btnCrearGrupoCompartir;
    @FXML private Button btnListarGruposCompartir;
    @FXML private ComboBox<String> comboAccionLikesSolucion;
    @FXML private Button btnGestionarLikesSolucion;
    @FXML private ComboBox<String> comboAccionLikesComentario;
    @FXML private Button btnGestionarLikesComentario;
    @FXML private TextArea txtInformacionCompartir;
    @FXML private ComboBox<String> comboAccionComentario;

    // Elementos comunes
    @FXML private TabPane tabPane;
    @FXML private Label lblEstadoSistema;
    @FXML private Button btnVolver;

    // Controladores de negocio reutilizados del sistema existente
    private ContextoSistema contexto;
    private ComunidadService comunidadService;

    // Variables de estado para el formulario overlay
    private boolean creandoGrupoDiscusion = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Inicializando controlador de Gestión de Foro...");

        // Inicializar los componentes del sistema
        inicializarSistema();

        // Configurar la interfaz
        configurarInterfaz();

        // Actualizar información inicial
        actualizarInformacion();

        System.out.println("✅ Controlador de Gestión de Foro inicializado correctamente");
    }

    /**
     * Inicializar el sistema con los componentes existentes
     */
    private void inicializarSistema() {
        // Obtener la instancia del contexto (singleton)
        this.contexto = ContextoSistema.getInstance();

        // Asegurar que hay una comunidad activa para el usuario actual
        contexto.establecerComunidadActivaParaUsuario();

        // Inicializar controladores de negocio
        this.comunidadService = new ComunidadService();

        // Verificar estado de carga de grupos
        if (contexto.tieneComunidadActiva()) {
            ForoGeneral foro = contexto.getComunidadActual().getForoGeneral();
            System.out.println("🔍 Foro cargado con " + foro.getGruposDiscusion().size() +
                    " grupos de discusión y " + foro.getGruposCompartir().size() + " grupos de compartir");

            // 🚀 CARGAR DATOS PERSISTIDOS
            String nombreComunidad = contexto.getComunidadActual().getNombre();
            System.out.println("🔄 Iniciando carga de datos persistidos para: " + nombreComunidad);
            PersistenciaService.cargarTodosLosDatosPersistidos(foro, nombreComunidad);

            System.out.println("✅ Datos persistidos cargados. Estado actual del foro:");
            System.out.println("  📝 Grupos de discusión: " + foro.getGruposDiscusion().size());
            System.out.println("  🔄 Grupos de compartir: " + foro.getGruposCompartir().size());

            // Mostrar estadísticas de hilos y soluciones cargados
            int totalHilos = 0;
            int totalSoluciones = 0;
            for (GrupoDiscusion grupo : foro.getGruposDiscusion()) {
                totalHilos += grupo.getHilos().size();
            }
            for (GrupoCompartir grupo : foro.getGruposCompartir()) {
                totalSoluciones += grupo.getSoluciones().size();
            }
            System.out.println("  🧵 Hilos cargados: " + totalHilos);
            System.out.println("  💡 Soluciones cargadas: " + totalSoluciones);
        }
    }

    /**
     * Configurar elementos de la interfaz
     */
    private void configurarInterfaz() {
        // Configurar ComboBox de niveles de Java
        comboNivelJava.setItems(FXCollections.observableArrayList(NivelJava.values()));
        comboNivelJava.setValue(NivelJava.PRINCIPIANTE); // Valor por defecto

        // Configurar ComboBox de temas
        comboTema.setItems(FXCollections.observableArrayList(TipoTema.values()));
        comboTema.setValue(TipoTema.SINTAXIS); // Valor por defecto

        // Configurar ComboBox de acciones para hilos (Discusión)
        comboAccionHilo.setItems(FXCollections.observableArrayList(
                "Crear Hilo",
                "Ver Hilos",
                "Votar Hilo",
                "Cambiar Estado Hilo" // Nueva opción
        ));
        comboAccionHilo.setValue("Crear Hilo");

        // Configurar ComboBox de acciones para respuestas (Discusión)
        comboAccionRespuesta.setItems(FXCollections.observableArrayList(
                "Responder Hilo",
                "Marcar como Solución",
                "Desmarcar como Solución",
                "Votar Respuesta"
        ));
        comboAccionRespuesta.setValue("Responder Hilo");

        // Configurar ComboBox de acciones para soluciones (Compartir)
        comboAccionSolucion.setItems(FXCollections.observableArrayList(
                "Compartir Solución",
                "Votar Solución",
                "Ver Soluciones"
        ));
        comboAccionSolucion.setValue("Compartir Solución");

        // Configurar ComboBox de acciones para comentarios (Compartir)
        comboAccionComentario.setItems(FXCollections.observableArrayList(
                "Comentar Solución",
                "Eliminar Comentario",
                "Votar Comentario"
        ));
        comboAccionComentario.setValue("Comentar Solución");

        // Configurar áreas de texto
        txtInformacionDiscusion.setEditable(false);
        txtInformacionDiscusion.setWrapText(true);
        txtInformacionCompartir.setEditable(false);
        txtInformacionCompartir.setWrapText(true);

        // Establecer estilos para las áreas de texto
        String textAreaStyle = "-fx-background-color: #f4f4f4; " +
                "-fx-border-color: #cccccc; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 10;";

        txtInformacionDiscusion.setStyle(textAreaStyle);
        txtInformacionCompartir.setStyle(textAreaStyle);

        // Configurar el formulario overlay
        formCrearGrupo.setVisible(false);
        formCrearGrupo.setStyle("-fx-background-color: rgba(233,239,245,0.95); " +
                "-fx-background-radius: 15; " +
                "-fx-border-color: #4a6fa5; " +
                "-fx-border-radius: 15; " +
                "-fx-border-width: 2; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0.5, 0, 0);");

        // Configurar el efecto de desenfoque
        BoxBlur blur = new BoxBlur(5, 5, 3);
        blur.setInput(new ColorAdjust(0, 0, -0.2, 0));

        // Vincular el efecto de desenfoque a la visibilidad del formulario
        formCrearGrupo.visibleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                vboxPrincipal.setEffect(blur);
            } else {
                vboxPrincipal.setEffect(null);
            }
        });

        // Configurar el comportamiento del TabPane
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            actualizarInformacion();
        });

        // Configurar el foco del formulario
        formCrearGrupo.setOnMouseClicked(e -> {
            if (!formCrearGrupo.getBoundsInParent().contains(e.getX(), e.getY())) {
                ocultarFormulario();
            }
        });

        // Actualizar estado del sistema
        actualizarEstadoSistema();
    }

    /**
     * Método FXML para mostrar el formulario de creación de grupo de discusión
     */
    @FXML
    private void mostrarFormularioDiscusion() {
        mostrarFormulario(true);
    }

    /**
     * Método FXML para mostrar el formulario de creación de grupo de compartir
     */
    @FXML
    private void mostrarFormularioCompartir() {
        mostrarFormulario(false);
    }

    /**
     * Método para mostrar el formulario de creación de grupo
     */
    private void mostrarFormulario(boolean esDiscusion) {
        creandoGrupoDiscusion = esDiscusion;
        lblTituloFormulario.setText(esDiscusion ? "📝 Crear Nuevo Grupo de Discusión" : "📝 Crear Nuevo Grupo de Compartir");

        // Aplicar efecto de desenfoque al contenido principal
        vboxPrincipal.setEffect(new BoxBlur(5, 5, 3));

        // Mostrar el formulario
        formCrearGrupo.setVisible(true);
        formCrearGrupo.toFront();
    }

    /**
     * Método para ocultar el formulario de creación de grupo
     */
    @FXML
    private void ocultarFormulario() {
        // Quitar efecto de desenfoque
        vboxPrincipal.setEffect(null);

        // Ocultar formulario
        formCrearGrupo.setVisible(false);
        txtTituloGrupo.clear();
    }

    /**
     * Método FXML para confirmar la creación de un grupo (desde el formulario overlay)
     */
    @FXML
    private void confirmarCrearGrupo() {
        if (creandoGrupoDiscusion) {
            crearGrupoDiscusionLogica();
        } else {
            crearGrupoCompartirLogica();
        }
        ocultarFormulario();
    }

    /**
     * Lógica para crear un grupo de discusión (llamado desde confirmarCrearGrupo)
     */
    private void crearGrupoDiscusionLogica() {
        try {
            // Validar datos de entrada
            if (!validarDatosGrupo(txtInformacionDiscusion)) {
                return;
            }

            // Verificar que hay una comunidad activa
            if (!contexto.tieneComunidadActiva()) {
                mostrarMensajeError("⚠️ No hay una comunidad activa. Primero debe crear o seleccionar una comunidad.", txtInformacionDiscusion);
                return;
            }

            String titulo = txtTituloGrupo.getText().trim();
            NivelJava nivel = comboNivelJava.getValue();
            TipoTema tema = comboTema.getValue();

            // Crear el grupo directamente usando el foro
            ForoGeneral foro = contexto.getComunidadActual().getForoGeneral();
            GrupoDiscusion grupo = foro.crearGrupoDiscusion(titulo, nivel, tema);

            if (grupo != null) {
                // Obtener usuario actual para guardarlo como creador
                String creador = "Sistema";
                try {
                    SesionManager sesion = SesionManager.getInstancia();
                    if (sesion.hayUsuarioAutenticado()) {
                        creador = sesion.getUsuarioComunidad().getUsername();
                    }
                } catch (Exception e) {
                    System.err.println("No se pudo obtener usuario actual: " + e.getMessage());
                }

                // Guardar en persistencia
                contexto.guardarGrupoForo(contexto.getComunidadActual(), "DISCUSION", titulo, nivel, tema, creador);

                // Limpiar campos
                txtTituloGrupo.clear();

                mostrarMensajeExito("✅ Grupo de discusión '" + titulo + "' creado exitosamente por " + creador, txtInformacionDiscusion);
                actualizarInformacion();
            } else {
                mostrarMensajeError("❌ Error al crear el grupo de discusión", txtInformacionDiscusion);
            }

        } catch (Exception e) {
            mostrarMensajeError("❌ Error: " + e.getMessage(), txtInformacionDiscusion);
            System.err.println("Error al crear grupo de discusión: " + e.getMessage());
        }
    }

    /**
     * Lógica para crear un grupo de compartir (llamado desde confirmarCrearGrupo)
     */
    private void crearGrupoCompartirLogica() {
        try {
            // Validar datos de entrada
            if (!validarDatosGrupo(txtInformacionCompartir)) {
                return;
            }

            // Verificar que hay una comunidad activa
            if (!contexto.tieneComunidadActiva()) {
                mostrarMensajeError("⚠️ No hay una comunidad activa. Primero debe crear o seleccionar una comunidad.", txtInformacionCompartir);
                return;
            }

            String titulo = txtTituloGrupo.getText().trim();
            NivelJava nivel = comboNivelJava.getValue();
            TipoTema tema = comboTema.getValue();

            // Crear el grupo directamente usando el foro
            ForoGeneral foro = contexto.getComunidadActual().getForoGeneral();
            GrupoCompartir grupo = foro.crearGrupoCompartir(titulo, nivel, tema);

            if (grupo != null) {
                // Obtener usuario actual para guardarlo como creador
                String creador = "Sistema";
                try {
                    SesionManager sesion = SesionManager.getInstancia();
                    if (sesion.hayUsuarioAutenticado()) {
                        creador = sesion.getUsuarioComunidad().getUsername();
                    }
                } catch (Exception e) {
                    System.err.println("No se pudo obtener usuario actual: " + e.getMessage());
                }

                // Guardar en persistencia
                contexto.guardarGrupoForo(contexto.getComunidadActual(), "COMPARTIR", titulo, nivel, tema, creador);

                // Limpiar campos
                txtTituloGrupo.clear();

                mostrarMensajeExito("✅ Grupo de compartir '" + titulo + "' creado exitosamente por " + creador, txtInformacionCompartir);
                actualizarInformacion();
            } else {
                mostrarMensajeError("❌ Error al crear el grupo de compartir", txtInformacionCompartir);
            }

        } catch (Exception e) {
            mostrarMensajeError("❌ Error: " + e.getMessage(), txtInformacionCompartir);
            System.err.println("Error al crear grupo de compartir: " + e.getMessage());
        }
    }

    /**
     * Método FXML para listar grupos de discusión
     */
    @FXML
    private void listarGruposDiscusion() {
        try {
            if (!contexto.tieneComunidadActiva()) {
                mostrarMensajeError("⚠️ No hay una comunidad activa.", txtInformacionDiscusion);
                return;
            }

            UsuarioComunidad usuarioActual = SesionManager.getInstancia().getUsuarioComunidad();
            if (usuarioActual == null) {
                mostrarMensajeError("⚠️ No hay un usuario autenticado. Inicie sesión para ver los grupos.", txtInformacionDiscusion);
                return;
            }

            ForoGeneral foro = contexto.getComunidadActual().getForoGeneral();
            List<GrupoDiscusion> gruposDelUsuario = foro.getGruposDiscusion().stream()
                    .filter(grupo -> grupo.getMiembros().contains(usuarioActual))
                    .collect(Collectors.toList());

            if (gruposDelUsuario.isEmpty()) {
                mostrarMensajeInfo("ℹ️ No eres miembro de ningún grupo de discusión.", txtInformacionDiscusion);
            } else {
                StringBuilder info = new StringBuilder("=== TUS GRUPOS DE DISCUSIÓN ===\n\n");
                for (int i = 0; i < gruposDelUsuario.size(); i++) {
                    GrupoDiscusion grupo = gruposDelUsuario.get(i);
                    info.append(String.format("%d. %s\n", (i + 1), grupo.toString()));
                    info.append(String.format("   Miembros: %d | Hilos: %d\n",
                            grupo.getMiembros().size(), grupo.getHilos().size()));
                    info.append("   ────────────────────────────────\n");
                }

                mostrarInformacion(info.toString(), txtInformacionDiscusion);
            }

        } catch (Exception e) {
            mostrarMensajeError("❌ Error al listar grupos de discusión: " + e.getMessage(), txtInformacionDiscusion);
        }
    }

    /**
     * Método FXML para listar grupos de compartir
     */
    @FXML
    private void listarGruposCompartir() {
        try {
            if (!contexto.tieneComunidadActiva()) {
                mostrarMensajeError("⚠️ No hay una comunidad activa.", txtInformacionCompartir);
                return;
            }

            UsuarioComunidad usuarioActual = SesionManager.getInstancia().getUsuarioComunidad();
            if (usuarioActual == null) {
                mostrarMensajeError("⚠️ No hay un usuario autenticado. Inicie sesión para ver los grupos.", txtInformacionCompartir);
                return;
            }

            ForoGeneral foro = contexto.getComunidadActual().getForoGeneral();
            List<GrupoCompartir> gruposDelUsuario = foro.getGruposCompartir().stream()
                    .filter(grupo -> grupo.getMiembros().contains(usuarioActual))
                    .collect(Collectors.toList());

            if (gruposDelUsuario.isEmpty()) {
                mostrarMensajeInfo("ℹ️ No eres miembro de ningún grupo de compartir.", txtInformacionCompartir);
            } else {
                StringBuilder info = new StringBuilder("=== TUS GRUPOS DE COMPARTIR ===\n\n");
                for (int i = 0; i < gruposDelUsuario.size(); i++) {
                    GrupoCompartir grupo = gruposDelUsuario.get(i);
                    info.append(String.format("%d. %s\n", (i + 1), grupo.toString()));
                    info.append(String.format("   Miembros: %d | Soluciones: %d\n",
                            grupo.getMiembros().size(), grupo.getSoluciones().size()));
                    info.append("   ────────────────────────────────\n");
                }

                mostrarInformacion(info.toString(), txtInformacionCompartir);
            }

        } catch (Exception e) {
            mostrarMensajeError("❌ Error al listar grupos de compartir: " + e.getMessage(), txtInformacionCompartir);
        }
    }

    /**
     * Método FXML para unirse a un grupo de discusión
     */
    @FXML
    private void unirseAGrupoDiscusion() {
        try {
            // Verificar que hay una comunidad activa
            if (!contexto.tieneComunidadActiva()) {
                mostrarMensajeError("⚠️ No hay una comunidad activa.", txtInformacionDiscusion);
                return;
            }

            // Obtener usuario actual (autenticado)
            UsuarioComunidad usuario = SesionManager.getInstancia().getUsuarioComunidad();
            if (usuario == null) {
                mostrarMensajeError("⚠️ No hay un usuario autenticado. Inicie sesión primero.", txtInformacionDiscusion);
                return;
            }

            ForoGeneral foro = contexto.getComunidadActual().getForoGeneral();
            List<GrupoDiscusion> grupos = foro.getGruposDiscusion(); // Mostrar TODOS los grupos

            if (grupos.isEmpty()) {
                mostrarMensajeError("⚠️ No hay grupos de discusión disponibles. Primero cree uno.", txtInformacionDiscusion);
                return;
            }

            // Mostrar grupos disponibles
            StringBuilder info = new StringBuilder("=== GRUPOS DE DISCUSIÓN DISPONIBLES ===\n\n");
            for (int i = 0; i < grupos.size(); i++) {
                GrupoDiscusion grupo = grupos.get(i);
                info.append(String.format("%d. %s\n", (i + 1), grupo.toString()));
                info.append(String.format("   Miembros: %d | Hilos: %d\n", grupo.getMiembros().size(), grupo.getHilos().size()));
                info.append("   ────────────────────────────────\n");
            }

            // Solicitar índice del grupo
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Unirse a Grupo de Discusión");
            dialog.setHeaderText(info.toString());
            dialog.setContentText("Seleccione el número del grupo:");

            Optional<String> result = dialog.showAndWait();
            if (!result.isPresent()) return;

            try {
                int indice = Integer.parseInt(result.get().trim());
                if (indice < 1 || indice > grupos.size()) {
                    mostrarMensajeError("❌ Índice inválido. Debe estar entre 1 y " + grupos.size(), txtInformacionDiscusion);
                    return;
                }

                GrupoDiscusion grupo = grupos.get(indice - 1);

                // Verificar si el usuario cumple con el nivel requerido
                if (!grupo.esApropiado(usuario)) {
                    mostrarMensajeError("⚠️ No cumples con el nivel requerido para unirte a este grupo.", txtInformacionDiscusion);
                    return;
                }

                // Verificar si ya está en el grupo
                if (grupo.getMiembros().contains(usuario)) {
                    mostrarMensajeInfo("ℹ️ El usuario ya pertenece a este grupo.", txtInformacionDiscusion);
                    return;
                }

                // Unirse al grupo
                comunidadService.procesarUsuarioEnGrupo(usuario, grupo);

                // Guardar la membresía en persistencia
                String nombreComunidad = ContextoSistema.getInstance().getComunidadActual().getNombre();
                PersistenciaService.guardarMiembroGrupo(nombreComunidad, "DISCUSION", grupo.getTitulo(), usuario.getUsername());

                mostrarMensajeExito("✅ Usuario '" + usuario.getNombre() + "' se unió exitosamente al grupo '" + grupo.getTitulo() + "'", txtInformacionDiscusion);
                actualizarInformacion();

            } catch (NumberFormatException e) {
                mostrarMensajeError("❌ Debe ingresar un número válido.", txtInformacionDiscusion);
            }

        } catch (Exception e) {
            mostrarMensajeError("❌ Error al unirse al grupo de discusión: " + e.getMessage(), txtInformacionDiscusion);
        }
    }



    /**
     * Método FXML para unirse a un grupo de compartir
     */
    @FXML
    private void unirseAGrupoCompartir() {
        try {
            // Verificar que hay una comunidad activa
            if (!contexto.tieneComunidadActiva()) {
                mostrarMensajeError("⚠️ No hay una comunidad activa.", txtInformacionCompartir);
                return;
            }

            // Obtener usuario actual (autenticado)
            UsuarioComunidad usuario = SesionManager.getInstancia().getUsuarioComunidad();
            if (usuario == null) {
                mostrarMensajeError("⚠️ No hay un usuario autenticado. Inicie sesión primero.", txtInformacionCompartir);
                return;
            }

            ForoGeneral foro = contexto.getComunidadActual().getForoGeneral();
            List<GrupoCompartir> grupos = foro.getGruposCompartir(); // Mostrar TODOS los grupos

            if (grupos.isEmpty()) {
                mostrarMensajeError("⚠️ No hay grupos de compartir disponibles. Primero cree uno.", txtInformacionCompartir);
                return;
            }

            // Mostrar grupos disponibles
            StringBuilder info = new StringBuilder("=== GRUPOS DE COMPARTIR DISPONIBLES ===\n\n");
            for (int i = 0; i < grupos.size(); i++) {
                GrupoCompartir grupo = grupos.get(i);
                info.append(String.format("%d. %s\n", (i + 1), grupo.toString()));
                info.append(String.format("   Miembros: %d | Soluciones: %d\n", grupo.getMiembros().size(), grupo.getSoluciones().size()));
                info.append("   ────────────────────────────────\n");
            }

            // Solicitar índice del grupo
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Unirse a Grupo de Compartir");
            dialog.setHeaderText(info.toString());
            dialog.setContentText("Seleccione el número del grupo:");

            Optional<String> result = dialog.showAndWait();
            if (!result.isPresent()) return;

            try {
                int indice = Integer.parseInt(result.get().trim());
                if (indice < 1 || indice > grupos.size()) {
                    mostrarMensajeError("❌ Índice inválido. Debe estar entre 1 y " + grupos.size(), txtInformacionCompartir);
                    return;
                }

                GrupoCompartir grupo = grupos.get(indice - 1);

                // Verificar si el usuario cumple con el nivel requerido
                if (!grupo.esApropiado(usuario)) {
                    mostrarMensajeError("⚠️ No cumples con el nivel requerido para unirte a este grupo.", txtInformacionCompartir);
                    return;
                }

                // Verificar si ya está en el grupo
                if (grupo.getMiembros().contains(usuario)) {
                    mostrarMensajeInfo("ℹ️ El usuario ya pertenece a este grupo.", txtInformacionCompartir);
                    return;
                }

                // Unirse al grupo
                comunidadService.procesarUsuarioEnGrupoCompartir(usuario, grupo);

                // Guardar la membresía en persistencia
                String nombreComunidad = ContextoSistema.getInstance().getComunidadActual().getNombre();
                PersistenciaService.guardarMiembroGrupo(nombreComunidad, "COMPARTIR", grupo.getTitulo(), usuario.getUsername());
                mostrarMensajeExito("✅ Usuario '" + usuario.getNombre() + "' se unió exitosamente al grupo '" + grupo.getTitulo() + "'", txtInformacionCompartir);
                actualizarInformacion();

            } catch (NumberFormatException e) {
                mostrarMensajeError("❌ Debe ingresar un número válido.", txtInformacionCompartir);
            }

        } catch (Exception e) {
            mostrarMensajeError("❌ Error al unirse al grupo de compartir: " + e.getMessage(), txtInformacionCompartir);
        }
    }


    /**
     * Método FXML para gestionar hilos de discusión
     */
    @FXML
    private void gestionarHilos() {
        try {
            // Verificar que hay una comunidad activa
            if (!contexto.tieneComunidadActiva()) {
                mostrarMensajeError("⚠️ No hay una comunidad activa.", txtInformacionDiscusion);
                return;
            }

            String accion = comboAccionHilo.getValue();
            ForoGeneral foro = contexto.getComunidadActual().getForoGeneral();
            UsuarioComunidad usuarioActual = SesionManager.getInstancia().getUsuarioComunidad();

            if (usuarioActual == null) {
                mostrarMensajeError("⚠️ No hay un usuario autenticado. Inicie sesión para gestionar hilos.", txtInformacionDiscusion);
                return;
            }

            switch (accion) {
                case "Crear Hilo":
                    crearHiloEnGrupo(foro);
                    break;
                case "Ver Hilos":
                    verHilosEnGrupos(foro, usuarioActual);
                    break;
                case "Votar Hilo":
                    votarHilo(foro, usuarioActual);
                    break;
                case "Cambiar Estado Hilo":
                    cambiarEstadoHilo(foro, usuarioActual);
                    break;
                default:
                    mostrarMensajeError("⚠️ Seleccione una acción válida.", txtInformacionDiscusion);
            }
        } catch (Exception e) {
            mostrarMensajeError("❌ Error al gestionar hilos: " + e.getMessage(), txtInformacionDiscusion);
        }
    }

    /**
     * Crear un hilo en un grupo de discusión específico
     */
    private void crearHiloEnGrupo(ForoGeneral foro) {
        UsuarioComunidad autor = SesionManager.getInstancia().getUsuarioComunidad();
        if (autor == null) {
            mostrarMensajeError("⚠️ No hay un usuario autenticado. Inicie sesión para crear un hilo.", txtInformacionDiscusion);
            return;
        }

        List<GrupoDiscusion> gruposDelUsuario = foro.getGruposDiscusion().stream()
                .filter(grupo -> grupo.getMiembros().contains(autor))
                .collect(Collectors.toList());

        if (gruposDelUsuario.isEmpty()) {
            mostrarMensajeError("⚠️ No eres miembro de ningún grupo de discusión para crear hilos.", txtInformacionDiscusion);
            return;
        }

        // Mostrar solo los grupos a los que el usuario pertenece
        StringBuilder info = new StringBuilder("=== TUS GRUPOS DE DISCUSIÓN DISPONIBLES ===\n\n");
        for (int i = 0; i < gruposDelUsuario.size(); i++) {
            GrupoDiscusion grupo = gruposDelUsuario.get(i);
            info.append(String.format("%d. %s\n", (i + 1), grupo.toString()));
            info.append(String.format("   Miembros: %d | Hilos: %d\n",
                    grupo.getMiembros().size(), grupo.getHilos().size()));
            info.append("   ────────────────────────────────\n");
        }

        // Seleccionar grupo
        TextInputDialog grupoDialog = new TextInputDialog();
        grupoDialog.setTitle("Crear Hilo");
        grupoDialog.setHeaderText(info.toString());
        grupoDialog.setContentText("Seleccione el grupo (número):");

        Optional<String> grupoResult = grupoDialog.showAndWait();
        if (!grupoResult.isPresent()) return;

        try {
            int indiceGrupo = Integer.parseInt(grupoResult.get().trim()) - 1;
            if (indiceGrupo < 0 || indiceGrupo >= gruposDelUsuario.size()) {
                mostrarMensajeError("❌ Índice de grupo inválido.", txtInformacionDiscusion);
                return;
            }

            GrupoDiscusion grupo = gruposDelUsuario.get(indiceGrupo);

            // Solicitar datos del hilo
            TextInputDialog tituloDialog = new TextInputDialog();
            tituloDialog.setTitle("Crear Hilo");
            tituloDialog.setHeaderText("Nuevo Hilo en: " + grupo.getTitulo() + "\nAutor: " + autor.getNombre());
            tituloDialog.setContentText("Título del hilo:");

            Optional<String> tituloResult = tituloDialog.showAndWait();
            if (!tituloResult.isPresent() || tituloResult.get().trim().isEmpty()) {
                return;
            }

            TextInputDialog problemaDialog = new TextInputDialog();
            problemaDialog.setTitle("Crear Hilo");
            problemaDialog.setHeaderText("Descripción del Problema");
            problemaDialog.setContentText("Descripción:");

            Optional<String> problemaResult = problemaDialog.showAndWait();
            if (!problemaResult.isPresent() || problemaResult.get().trim().isEmpty()) {
                return;
            }

            String titulo = tituloResult.get().trim();
            String problema = problemaResult.get().trim();

            // Crear el hilo
            HiloDiscusion nuevoHilo = new HiloDiscusion(titulo, problema, autor);
            grupo.addHilo(nuevoHilo); // Usar el nuevo método addHilo en GrupoDiscusion

            // Guardar el hilo en persistencia
            String nombreComunidad = ContextoSistema.getInstance().getComunidadActual().getNombre();
            String idHilo = PersistenciaService.guardarHiloDiscusion(nombreComunidad, grupo.getTitulo(), titulo, problema, autor.getUsername(), nuevoHilo.getEstado(), nuevoHilo.getVotosUsuarios());

            mostrarMensajeExito("✅ Hilo '" + titulo + "' creado exitosamente por " + autor.getNombre() + " en '" + grupo.getTitulo() + "' (ID: " + idHilo + ")", txtInformacionDiscusion);
            actualizarInformacion();

        } catch (NumberFormatException e) {
            mostrarMensajeError("❌ Debe ingresar un número válido.", txtInformacionDiscusion);
        } catch (IllegalArgumentException e) {
            mostrarMensajeError("❌ " + e.getMessage(), txtInformacionDiscusion);
        }
    }

    /**
     * Ver hilos en grupos de discusión a los que el usuario pertenece
     */
    private void verHilosEnGrupos(ForoGeneral foro, UsuarioComunidad usuarioActual) {
        List<GrupoDiscusion> gruposDelUsuario = foro.getGruposDiscusion().stream()
                .filter(grupo -> grupo.getMiembros().contains(usuarioActual))
                .collect(Collectors.toList());

        if (gruposDelUsuario.isEmpty()) {
            mostrarMensajeInfo("ℹ️ No eres miembro de ningún grupo de discusión para ver hilos.", txtInformacionDiscusion);
            return;
        }

        StringBuilder info = new StringBuilder("=== TUS HILOS DE DISCUSIÓN ===\n\n");
        boolean hayHilos = false;

        for (GrupoDiscusion grupo : gruposDelUsuario) {
            if (!grupo.getHilos().isEmpty()) {
                hayHilos = true;
                info.append("📋 GRUPO: ").append(grupo.getTitulo()).append("\n");
                info.append("   Nivel: ").append(grupo.getNivelJava()).append(" | Tema: ").append(grupo.getTipoTema()).append("\n\n");

                for (int i = 0; i < grupo.getHilos().size(); i++) {
                    HiloDiscusion hilo = grupo.getHilos().get(i);
                    info.append(String.format("   %d. %s\n", (i + 1), hilo.getTitulo()));
                    info.append(String.format("      Estado: %s | Autor: %s | \u2B06%d \u2B07%d\n", // Mostrar likes y dislikes
                            hilo.getEstado(), hilo.getAutor().getNombre(), hilo.getVotosPositivos(), hilo.getVotosNegativos()));
                    info.append(String.format("      Respuestas: %d\n", hilo.getRespuestas().size()));
                    // Mostrar respuestas y si son solución
                    for (int j = 0; j < hilo.getRespuestas().size(); j++) {
                        Respuesta resp = hilo.getRespuestas().get(j);
                        info.append(String.format("         -> %d. %s: %.30s... (\u2B06%d \u2B07%d)%s\n", // Modificado para mostrar likes y dislikes de respuesta
                                (j + 1), resp.getAutor().getNombre(), resp.getContenido(), resp.getVotosPositivos(), resp.getVotosNegativos(),
                                resp.getEsSolucion() ? " [SOLUCIÓN]" : ""));
                    }
                }
                info.append("\n   ════════════════════════════════\n\n");
            }
        }

        if (!hayHilos) {
            mostrarMensajeInfo("ℹ️ No hay hilos en tus grupos de discusión.", txtInformacionDiscusion);
        } else {
            mostrarInformacion(info.toString(), txtInformacionDiscusion);
        }
    }

    /**
     * Votar un hilo de discusión (solo en grupos a los que el usuario pertenece)
     */
    private void votarHilo(ForoGeneral foro, UsuarioComunidad usuarioVotante) {
        List<GrupoDiscusion> gruposDelUsuario = foro.getGruposDiscusion().stream()
                .filter(grupo -> grupo.getMiembros().contains(usuarioVotante))
                .collect(Collectors.toList());

        if (gruposDelUsuario.isEmpty()) {
            mostrarMensajeError("⚠️ No eres miembro de ningún grupo de discusión para votar hilos.", txtInformacionDiscusion);
            return;
        }

        GrupoDiscusion grupoSeleccionado = seleccionarGrupoDiscusion(gruposDelUsuario, txtInformacionDiscusion); // Usar lista filtrada
        if (grupoSeleccionado == null) return;

        List<HiloDiscusion> hilos = grupoSeleccionado.getHilos();
        if (hilos.isEmpty()) {
            mostrarMensajeError("⚠️ No hay hilos en este grupo.", txtInformacionDiscusion);
            return;
        }

        HiloDiscusion hiloSeleccionado = seleccionarHilo(hilos, txtInformacionDiscusion);
        if (hiloSeleccionado == null) return;

        Alert votoAlert = new Alert(Alert.AlertType.CONFIRMATION);
        votoAlert.setTitle("Votar Hilo");
        votoAlert.setHeaderText("Hilo: " + hiloSeleccionado.getTitulo());
        votoAlert.setContentText("¿Desea dar un voto o quitar su voto?");

        ButtonType darLikeBtn = new ButtonType("\u2B06 Dar Voto Positivo");
        ButtonType darDislikeBtn = new ButtonType("\u2B07 Dar Voto Negativo");
        ButtonType quitarVotoBtn = new ButtonType("❌ Quitar Voto");
        ButtonType cancelarBtn = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        List<ButtonType> botones = new ArrayList<>();
        botones.add(darLikeBtn);
        botones.add(darDislikeBtn);
        botones.add(quitarVotoBtn);
        botones.add(cancelarBtn);
        votoAlert.getButtonTypes().setAll(botones);

        Optional<ButtonType> result = votoAlert.showAndWait();
        if (result.isPresent()) {
            int tipoVoto = 0; // 0 para quitar voto
            String mensaje = "";

            if (result.get() == darLikeBtn) {
                tipoVoto = 1;
                mensaje = "✅ Voto positivo registrado para el hilo '" + hiloSeleccionado.getTitulo() + "'";
            } else if (result.get() == darDislikeBtn) {
                tipoVoto = -1;
                mensaje = "✅ Voto negativo registrado para el hilo '" + hiloSeleccionado.getTitulo() + "'";
            } else if (result.get() == quitarVotoBtn) {
                tipoVoto = 0;
                mensaje = "✅ Voto quitado para el hilo '" + hiloSeleccionado.getTitulo() + "'";
            } else {
                mostrarMensajeInfo("ℹ️ Votación cancelada.", txtInformacionDiscusion);
                return;
            }

            hiloSeleccionado.votar(usuarioVotante, tipoVoto);
            mostrarMensajeExito(mensaje, txtInformacionDiscusion);

            // Actualizar persistencia después de cada voto
            String nombreComunidad = ContextoSistema.getInstance().getComunidadActual().getNombre();
            PersistenciaService.actualizarHilo(nombreComunidad, grupoSeleccionado.getTitulo(), hiloSeleccionado);
            actualizarInformacion();
        }
    }

    /**
     * Cambiar el estado de un hilo (solo el autor del hilo puede hacerlo)
     */
    private void cambiarEstadoHilo(ForoGeneral foro, UsuarioComunidad usuarioActual) {
        List<GrupoDiscusion> gruposDelUsuario = foro.getGruposDiscusion().stream()
                .filter(grupo -> grupo.getMiembros().contains(usuarioActual))
                .collect(Collectors.toList());

        if (gruposDelUsuario.isEmpty()) {
            mostrarMensajeError("⚠️ No eres miembro de ningún grupo de discusión para cambiar el estado de hilos.", txtInformacionDiscusion);
            return;
        }

        GrupoDiscusion grupoSeleccionado = seleccionarGrupoDiscusion(gruposDelUsuario, txtInformacionDiscusion);
        if (grupoSeleccionado == null) return;

        List<HiloDiscusion> hilos = grupoSeleccionado.getHilos();
        if (hilos.isEmpty()) {
            mostrarMensajeError("⚠️ No hay hilos en este grupo.", txtInformacionDiscusion);
            return;
        }

        HiloDiscusion hiloSeleccionado = seleccionarHilo(hilos, txtInformacionDiscusion);
        if (hiloSeleccionado == null) return;

        // Solo el autor del hilo puede cambiar su estado
        if (!usuarioActual.equals(hiloSeleccionado.getAutor())) {
            mostrarMensajeError("⚠️ Solo el autor del hilo puede cambiar su estado.", txtInformacionDiscusion);
            return;
        }

        ChoiceDialog<EstadoHilo> estadoDialog = new ChoiceDialog<>(hiloSeleccionado.getEstado(), EstadoHilo.values());
        estadoDialog.setTitle("Cambiar Estado del Hilo");
        estadoDialog.setHeaderText("Hilo: " + hiloSeleccionado.getTitulo() + "\nEstado actual: " + hiloSeleccionado.getEstado());
        estadoDialog.setContentText("Seleccione el nuevo estado:");

        Optional<EstadoHilo> result = estadoDialog.showAndWait();
        if (result.isPresent()) {
            EstadoHilo nuevoEstado = result.get();

            if (nuevoEstado == EstadoHilo.RESUELTO) {
                hiloSeleccionado.marcarResuelto();
            } else if (nuevoEstado == EstadoHilo.CERRADO) {
                hiloSeleccionado.cerrar();
            } else if (nuevoEstado == EstadoHilo.ABIERTO) {
                hiloSeleccionado.reabrir();
            }

            mostrarMensajeExito("✅ Estado del hilo '" + hiloSeleccionado.getTitulo() + "' cambiado a " + nuevoEstado + ".", txtInformacionDiscusion);

            // Actualizar persistencia
            String nombreComunidad = ContextoSistema.getInstance().getComunidadActual().getNombre();
            PersistenciaService.actualizarHilo(nombreComunidad, grupoSeleccionado.getTitulo(), hiloSeleccionado);
            actualizarInformacion();
        } else {
            mostrarMensajeInfo("ℹ️ Cambio de estado cancelado.", txtInformacionDiscusion);
        }
    }


    /**
     * Método FXML para gestionar respuestas
     */
    @FXML
    private void gestionarRespuestas() {
        try {
            if (!contexto.tieneComunidadActiva()) {
                mostrarMensajeError("⚠️ No hay una comunidad activa.", txtInformacionDiscusion);
                return;
            }

            String accion = comboAccionRespuesta.getValue();
            ForoGeneral foro = contexto.getComunidadActual().getForoGeneral();
            UsuarioComunidad usuarioActual = SesionManager.getInstancia().getUsuarioComunidad();

            if (usuarioActual == null) {
                mostrarMensajeError("⚠️ No hay un usuario autenticado. Inicie sesión para gestionar respuestas.", txtInformacionDiscusion);
                return;
            }

            switch (accion) {
                case "Responder Hilo":
                    responderHiloEnGrupo(foro, usuarioActual);
                    break;
                case "Marcar como Solución":
                    marcarDesmarcarSolucion(foro, usuarioActual, true);
                    break;
                case "Desmarcar como Solución":
                    marcarDesmarcarSolucion(foro, usuarioActual, false);
                    break;
                case "Votar Respuesta":
                    votarRespuesta(foro, usuarioActual);
                    break;
                default:
                    mostrarMensajeError("⚠️ Seleccione una acción válida para respuestas.", txtInformacionDiscusion);
            }
        } catch (Exception e) {
            mostrarMensajeError("❌ Error al gestionar respuestas: " + e.getMessage(), txtInformacionDiscusion);
        }
    }

    /**
     * Responder a un hilo de discusión específico (solo en grupos a los que el usuario pertenece)
     */
    private void responderHiloEnGrupo(ForoGeneral foro, UsuarioComunidad autor) {
        List<GrupoDiscusion> gruposDelUsuario = foro.getGruposDiscusion().stream()
                .filter(grupo -> grupo.getMiembros().contains(autor))
                .collect(Collectors.toList());

        if (gruposDelUsuario.isEmpty()) {
            mostrarMensajeError("⚠️ No eres miembro de ningún grupo de discusión para responder hilos.", txtInformacionDiscusion);
            return;
        }

        // Mostrar solo los grupos a los que el usuario pertenece
        StringBuilder info = new StringBuilder("=== TUS GRUPOS DE DISCUSIÓN DISPONIBLES ===\n\n");
        for (int i = 0; i < gruposDelUsuario.size(); i++) {
            GrupoDiscusion grupo = gruposDelUsuario.get(i);
            info.append(String.format("%d. %s (%d hilos)\n",
                    (i + 1), grupo.getTitulo(), grupo.getHilos().size()));
        }

        // Seleccionar grupo
        TextInputDialog grupoDialog = new TextInputDialog();
        grupoDialog.setTitle("Responder Hilo");
        grupoDialog.setHeaderText(info.toString());
        grupoDialog.setContentText("Seleccione el grupo (número):");

        Optional<String> grupoResult = grupoDialog.showAndWait();
        if (!grupoResult.isPresent()) return;

        try {
            int indiceGrupo = Integer.parseInt(grupoResult.get().trim()) - 1;
            if (indiceGrupo < 0 || indiceGrupo >= gruposDelUsuario.size()) {
                mostrarMensajeError("❌ Índice de grupo inválido.", txtInformacionDiscusion);
                return;
            }

            GrupoDiscusion grupo = gruposDelUsuario.get(indiceGrupo);
            List<HiloDiscusion> hilos = grupo.getHilos();

            if (hilos.isEmpty()) {
                mostrarMensajeError("⚠️ No hay hilos en este grupo.", txtInformacionDiscusion);
                return;
            }

            // Mostrar hilos disponibles
            StringBuilder hilosInfo = new StringBuilder("=== HILOS DISPONIBLES EN TU GRUPO ===\n\n");
            for (int i = 0; i < hilos.size(); i++) {
                HiloDiscusion hilo = hilos.get(i);
                hilosInfo.append(String.format("%d. %s\n", (i + 1), hilo.getTitulo()));
                hilosInfo.append(String.format("   Respuestas: %d | Estado: %s\n",
                        hilo.getRespuestas().size(), hilo.getEstado()));
                hilosInfo.append("   ────────────────────────────────\n");
            }

            // Seleccionar hilo
            TextInputDialog hiloDialog = new TextInputDialog();
            hiloDialog.setTitle("Responder Hilo");
            hiloDialog.setHeaderText(hilosInfo.toString());
            hiloDialog.setContentText("Seleccione el hilo (número):");

            Optional<String> hiloResult = hiloDialog.showAndWait();
            if (!hiloResult.isPresent()) return;

            int indiceHilo = Integer.parseInt(hiloResult.get().trim()) - 1;
            if (indiceHilo < 0 || indiceHilo >= hilos.size()) {
                mostrarMensajeError("❌ Índice de hilo inválido.", txtInformacionDiscusion);
                return;
            }

            HiloDiscusion hilo = hilos.get(indiceHilo);

            // Verificar si el hilo está cerrado
            if (hilo.getEstado() == EstadoHilo.CERRADO) {
                mostrarMensajeError("🚫 No se puede responder a un hilo cerrado.", txtInformacionDiscusion);
                return;
            }

            // Solicitar contenido de la respuesta
            TextInputDialog respuestaDialog = new TextInputDialog();
            respuestaDialog.setTitle("Responder Hilo");
            respuestaDialog.setHeaderText("Respondiendo a: " + hilo.getTitulo() + "\nPor: " + autor.getNombre() +
                    "\n\nProblema: " + hilo.getProblema());
            respuestaDialog.setContentText("Su respuesta:");

            Optional<String> respuestaResult = respuestaDialog.showAndWait();
            if (!respuestaResult.isPresent() || respuestaResult.get().trim().isEmpty()) {
                return;
            }

            String contenido = respuestaResult.get().trim();

            // Procesar la respuesta con moderación
            Moderador moderador = contexto.getComunidadActual().getModerador();
            boolean respuestaEnviada = hilo.responder(contenido, autor, moderador);

            if (respuestaEnviada) {
                // Guardar la respuesta en persistencia
                String nombreComunidad = ContextoSistema.getInstance().getComunidadActual().getNombre();
                // Se pasa el mapa de votos y el estado de esSolucion
                String idRespuesta = PersistenciaService.guardarRespuestaHilo(nombreComunidad, grupo.getTitulo(), hilo.getIdHilo(), contenido, autor.getUsername(), new Respuesta(contenido, autor).getVotosUsuarios(), false);

                mostrarMensajeExito("✅ Respuesta agregada exitosamente por " + autor.getNombre() + " (ID: " + idRespuesta + ")", txtInformacionDiscusion);
            } else {
                mostrarMensajeError("🚫 La respuesta no pudo ser enviada (posible contenido inapropiado).", txtInformacionDiscusion);
            }

            actualizarInformacion();

        } catch (NumberFormatException e) {
            mostrarMensajeError("❌ Debe ingresar un número válido.", txtInformacionDiscusion);
        }
    }

    /**
     * Votar una respuesta (solo en grupos a los que el usuario pertenece)
     */
    private void votarRespuesta(ForoGeneral foro, UsuarioComunidad usuarioVotante) {
        List<GrupoDiscusion> gruposDelUsuario = foro.getGruposDiscusion().stream()
                .filter(grupo -> grupo.getMiembros().contains(usuarioVotante))
                .collect(Collectors.toList());

        if (gruposDelUsuario.isEmpty()) {
            mostrarMensajeError("⚠️ No eres miembro de ningún grupo de discusión para votar respuestas.", txtInformacionDiscusion);
            return;
        }

        GrupoDiscusion grupoSeleccionado = seleccionarGrupoDiscusion(gruposDelUsuario, txtInformacionDiscusion);
        if (grupoSeleccionado == null) return;

        List<HiloDiscusion> hilos = grupoSeleccionado.getHilos();
        if (hilos.isEmpty()) {
            mostrarMensajeError("⚠️ No hay hilos en este grupo.", txtInformacionDiscusion);
            return;
        }

        HiloDiscusion hiloSeleccionado = seleccionarHilo(hilos, txtInformacionDiscusion);
        if (hiloSeleccionado == null) return;

        List<Respuesta> respuestas = hiloSeleccionado.getRespuestas();
        if (respuestas.isEmpty()) {
            mostrarMensajeError("⚠️ No hay respuestas en este hilo.", txtInformacionDiscusion);
            return;
        }

        Respuesta respuestaSeleccionada = seleccionarRespuesta(respuestas, txtInformacionDiscusion);
        if (respuestaSeleccionada == null) return;

        Alert votoAlert = new Alert(Alert.AlertType.CONFIRMATION);
        votoAlert.setTitle("Votar Respuesta");
        votoAlert.setHeaderText("Respuesta de: " + respuestaSeleccionada.getAutor().getNombre());
        votoAlert.setContentText("¿Desea dar like, dislike o quitar su voto?");

        ButtonType darLikeBtn = new ButtonType("\u2B06 Voto Positivo");
        ButtonType darDislikeBtn = new ButtonType("\u2B06 Voto Negativo");
        ButtonType quitarVotoBtn = new ButtonType("❌ Quitar Voto");
        ButtonType cancelarBtn = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        List<ButtonType> botones = new ArrayList<>();
        botones.add(darLikeBtn);
        botones.add(darDislikeBtn);
        botones.add(quitarVotoBtn);
        botones.add(cancelarBtn);
        votoAlert.getButtonTypes().setAll(botones);

        Optional<ButtonType> result = votoAlert.showAndWait();
        if (result.isPresent()) {
            int tipoVoto = 0; // 0 para quitar voto
            String mensaje = "";

            if (result.get() == darLikeBtn) {
                tipoVoto = 1;
                mensaje = "✅ Voto Positivo registrado para la respuesta de '" + respuestaSeleccionada.getAutor().getNombre() + "'";
            } else if (result.get() == darDislikeBtn) {
                tipoVoto = -1;
                mensaje = "✅ Voto Negativo registrado para la respuesta de '" + respuestaSeleccionada.getAutor().getNombre() + "'";
            } else if (result.get() == quitarVotoBtn) {
                tipoVoto = 0;
                mensaje = "✅ Voto quitado para la respuesta de '" + respuestaSeleccionada.getAutor().getNombre() + "'";
            } else {
                mostrarMensajeInfo("ℹ️ Votación cancelada.", txtInformacionDiscusion);
                return;
            }

            respuestaSeleccionada.votar(usuarioVotante, tipoVoto);
            mostrarMensajeExito(mensaje, txtInformacionDiscusion);

            // Actualizar persistencia después de cada voto
            String nombreComunidad = ContextoSistema.getInstance().getComunidadActual().getNombre();
            PersistenciaService.actualizarRespuesta(nombreComunidad, grupoSeleccionado.getTitulo(), hiloSeleccionado.getIdHilo(), respuestaSeleccionada);
            actualizarInformacion();
        }
    }

    /**
     * Marcar o desmarcar una respuesta como solución (solo en grupos a los que el usuario pertenece)
     */
    private void marcarDesmarcarSolucion(ForoGeneral foro, UsuarioComunidad usuarioActual, boolean marcar) {
        List<GrupoDiscusion> gruposDelUsuario = foro.getGruposDiscusion().stream()
                .filter(grupo -> grupo.getMiembros().contains(usuarioActual))
                .collect(Collectors.toList());

        if (gruposDelUsuario.isEmpty()) {
            mostrarMensajeError("⚠️ No eres miembro de ningún grupo de discusión para marcar soluciones.", txtInformacionDiscusion);
            return;
        }

        GrupoDiscusion grupoSeleccionado = seleccionarGrupoDiscusion(gruposDelUsuario, txtInformacionDiscusion);
        if (grupoSeleccionado == null) return;

        List<HiloDiscusion> hilos = grupoSeleccionado.getHilos();
        if (hilos.isEmpty()) {
            mostrarMensajeError("⚠️ No hay hilos en este grupo.", txtInformacionDiscusion);
            return;
        }

        HiloDiscusion hiloSeleccionado = seleccionarHilo(hilos, txtInformacionDiscusion);
        if (hiloSeleccionado == null) return;

        List<Respuesta> respuestas = hiloSeleccionado.getRespuestas();
        if (respuestas.isEmpty()) {
            mostrarMensajeError("⚠️ No hay respuestas en este hilo.", txtInformacionDiscusion);
            return;
        }

        Respuesta respuestaSeleccionada = seleccionarRespuesta(respuestas, txtInformacionDiscusion);
        if (respuestaSeleccionada == null) return;

        // Solo el autor del hilo puede marcar una respuesta como solución
        if (!usuarioActual.equals(hiloSeleccionado.getAutor())) {
            mostrarMensajeError("⚠️ Solo el autor del hilo puede marcar/desmarcar una solución.", txtInformacionDiscusion);
            return;
        }

        if (marcar) {
            respuestaSeleccionada.marcarComoSolucion();
            hiloSeleccionado.marcarResuelto();
            mostrarMensajeExito("✅ Respuesta marcada como solución y hilo resuelto.", txtInformacionDiscusion);
        } else {
            respuestaSeleccionada.desmarcarComoSolucion();
            if (hiloSeleccionado.getSoluciones().isEmpty()) {
                hiloSeleccionado.reabrir();
            }
            mostrarMensajeExito("✅ Respuesta desmarcada como solución.", txtInformacionDiscusion);
        }
        // Actualizar persistencia del hilo y la respuesta después de cambiar su estado a RESUELTO/ABIERTO
        String nombreComunidad = ContextoSistema.getInstance().getComunidadActual().getNombre();
        PersistenciaService.actualizarHilo(nombreComunidad, grupoSeleccionado.getTitulo(), hiloSeleccionado);
        PersistenciaService.actualizarRespuesta(nombreComunidad, grupoSeleccionado.getTitulo(), hiloSeleccionado.getIdHilo(), respuestaSeleccionada);
        actualizarInformacion();
    }

    /**
     * Método FXML para gestionar soluciones
     */
    @FXML
    private void gestionarSoluciones() {
        try {
            // Verificar que hay una comunidad activa
            if (!contexto.tieneComunidadActiva()) {
                mostrarMensajeError("⚠️ No hay una comunidad activa.", txtInformacionCompartir);
                return;
            }

            String accion = comboAccionSolucion.getValue();
            ForoGeneral foro = contexto.getComunidadActual().getForoGeneral();
            UsuarioComunidad usuarioActual = SesionManager.getInstancia().getUsuarioComunidad();

            if (usuarioActual == null) {
                mostrarMensajeError("⚠️ No hay un usuario autenticado. Inicie sesión para gestionar soluciones.", txtInformacionCompartir);
                return;
            }

            switch (accion) {
                case "Compartir Solución":
                    compartirSolucionEnGrupo(foro, usuarioActual);
                    break;
                case "Votar Solución":
                    votarSolucion(foro, usuarioActual);
                    break;
                case "Ver Soluciones":
                    verSolucionesEnGrupos(foro, usuarioActual);
                    break;
                default:
                    mostrarMensajeError("⚠️ Seleccione una acción válida.", txtInformacionCompartir);
            }
        } catch (Exception e) {
            mostrarMensajeError("❌ Error al gestionar soluciones: " + e.getMessage(), txtInformacionCompartir);
        }
    }

    /**
     * Compartir una solución en un grupo específico (solo en grupos a los que el usuario pertenece)
     */
    private void compartirSolucionEnGrupo(ForoGeneral foro, UsuarioComunidad autor) {
        List<GrupoCompartir> gruposDelUsuario = foro.getGruposCompartir().stream()
                .filter(grupo -> grupo.getMiembros().contains(autor))
                .collect(Collectors.toList());

        if (gruposDelUsuario.isEmpty()) {
            mostrarMensajeError("⚠️ No eres miembro de ningún grupo de compartir para compartir soluciones.", txtInformacionCompartir);
            return;
        }

        // Mostrar solo los grupos a los que el usuario pertenece
        StringBuilder info = new StringBuilder("=== TUS GRUPOS DE COMPARTIR DISPONIBLES ===\n\n");
        for (int i = 0; i < gruposDelUsuario.size(); i++) {
            GrupoCompartir grupo = gruposDelUsuario.get(i);
            info.append(String.format("%d. %s\n", (i + 1), grupo.toString()));
            info.append(String.format("   Miembros: %d | Soluciones: %d\n",
                    grupo.getMiembros().size(), grupo.getSoluciones().size()));
            info.append("   ────────────────────────────────\n");
        }

        // Seleccionar grupo
        TextInputDialog grupoDialog = new TextInputDialog();
        grupoDialog.setTitle("Compartir Solución");
        grupoDialog.setHeaderText(info.toString());
        grupoDialog.setContentText("Seleccione el grupo (número):");

        Optional<String> grupoResult = grupoDialog.showAndWait();
        if (!grupoResult.isPresent()) return;

        try {
            int indiceGrupo = Integer.parseInt(grupoResult.get().trim()) - 1;
            if (indiceGrupo < 0 || indiceGrupo >= gruposDelUsuario.size()) {
                mostrarMensajeError("❌ Índice de grupo inválido.", txtInformacionCompartir);
                return;
            }

            GrupoCompartir grupo = gruposDelUsuario.get(indiceGrupo);

            // Solicitar datos de la solución
            TextInputDialog tituloDialog = new TextInputDialog();
            tituloDialog.setTitle("Compartir Solución");
            tituloDialog.setHeaderText("Nueva Solución en: " + grupo.getTitulo() + "\nAutor: " + autor.getNombre());
            tituloDialog.setContentText("Título de la solución:");

            Optional<String> tituloResult = tituloDialog.showAndWait();
            if (!tituloResult.isPresent() || tituloResult.get().trim().isEmpty()) {
                return;
            }

            TextInputDialog contenidoDialog = new TextInputDialog();
            contenidoDialog.setTitle("Compartir Solución");
            contenidoDialog.setHeaderText("Contenido de la Solución");
            contenidoDialog.setContentText("Descripción/Código:");

            Optional<String> contenidoResult = contenidoDialog.showAndWait();
            if (!contenidoResult.isPresent() || contenidoResult.get().trim().isEmpty()) {
                return;
            }

            // Solicitar tipo de solución
            ChoiceDialog<TipoSolucion> tipoSolucionDialog = new ChoiceDialog<>(TipoSolucion.CODIGO, TipoSolucion.values());
            tipoSolucionDialog.setTitle("Tipo de Solución");
            tipoSolucionDialog.setHeaderText("Seleccione el tipo de solución:");
            tipoSolucionDialog.setContentText("Tipo:");
            Optional<TipoSolucion> tipoSolucionResult = tipoSolucionDialog.showAndWait();
            if (!tipoSolucionResult.isPresent()) {
                return;
            }
            TipoSolucion tipoSolucion = tipoSolucionResult.get();


            String titulo = tituloResult.get().trim();
            String contenido = contenidoResult.get().trim();

            // Crear y compartir la solución
            Solucion solucion = new Solucion(titulo, contenido, autor, tipoSolucion);
            comunidadService.procesarSolucionEnGrupo(grupo, solucion);

            // Guardar la solución en persistencia
            String nombreComunidad = ContextoSistema.getInstance().getComunidadActual().getNombre();
            String idSolucion = PersistenciaService.guardarSolucionCompartida(nombreComunidad, grupo.getTitulo(), solucion.getIdSolucion(), titulo, contenido, autor.getUsername(), tipoSolucion.getDescripcion(), solucion.getVotosUsuarios());

            mostrarMensajeExito("✅ Solución '" + titulo + "' compartida exitosamente por " + autor.getNombre() + " en '" + grupo.getTitulo() + "' (ID: " + idSolucion + ")", txtInformacionCompartir);
            actualizarInformacion();

        } catch (NumberFormatException e) {
            mostrarMensajeError("❌ Debe ingresar un número válido.", txtInformacionCompartir);
        }
    }

    /**
     * Ver soluciones en grupos de compartir a los que el usuario pertenece
     */
    private void verSolucionesEnGrupos(ForoGeneral foro, UsuarioComunidad usuarioActual) {
        List<GrupoCompartir> gruposDelUsuario = foro.getGruposCompartir().stream()
                .filter(grupo -> grupo.getMiembros().contains(usuarioActual))
                .collect(Collectors.toList());

        if (gruposDelUsuario.isEmpty()) {
            mostrarMensajeInfo("ℹ️ No eres miembro de ningún grupo de compartir para ver soluciones.", txtInformacionCompartir);
            return;
        }

        StringBuilder info = new StringBuilder("=== TUS SOLUCIONES COMPARTIDAS ===\n\n");
        boolean haySoluciones = false;

        for (GrupoCompartir grupo : gruposDelUsuario) {
            if (!grupo.getSoluciones().isEmpty()) {
                haySoluciones = true;
                info.append("🔄 GRUPO: ").append(grupo.getTitulo()).append("\n");
                info.append("   Nivel: ").append(grupo.getNivelJava()).append(" | Tema: ").append(grupo.getTipoTema()).append("\n\n");

                for (int i = 0; i < grupo.getSoluciones().size(); i++) {
                    Solucion solucion = grupo.getSoluciones().get(i);
                    info.append(String.format("   %d. %s\n", (i + 1), solucion.getTitulo()));
                    info.append(String.format("      Autor: %s | Tipo: %s | 👍%d 👎%d\n", // Modificado para mostrar likes y dislikes
                            solucion.getAutor().getNombre(), solucion.getTipoSolucion(), solucion.getLikes(), solucion.getDislikes()));
                    info.append(String.format("      Contenido: %.50s%s\n",
                            solucion.getContenido(),
                            solucion.getContenido().length() > 50 ? "..." : ""));
                    // Mostrar comentarios
                    if (!solucion.getComentarios().isEmpty()) {
                        info.append("      Comentarios:\n");
                        for (Comentario comentario : solucion.getComentarios()) {
                            info.append(String.format("         -> %s\n", comentario.toString())); // Comentario.toString() ya incluye likes/dislikes
                        }
                    }
                }
                info.append("\n   ════════════════════════════════\n\n");
            }
        }

        if (!haySoluciones) {
            mostrarMensajeInfo("ℹ️ No hay soluciones en tus grupos de compartir.", txtInformacionCompartir);
        } else {
            mostrarInformacion(info.toString(), txtInformacionCompartir);
        }
    }

    /**
     * Gestionar likes/dislikes de una solución (solo en grupos a los que el usuario pertenece)
     */
    private void votarSolucion(ForoGeneral foro, UsuarioComunidad usuario) {
        List<GrupoCompartir> gruposDelUsuario = foro.getGruposCompartir().stream()
                .filter(grupo -> grupo.getMiembros().contains(usuario))
                .collect(Collectors.toList());

        if (gruposDelUsuario.isEmpty()) {
            mostrarMensajeError("⚠️ No eres miembro de ningún grupo de compartir para votar soluciones.", txtInformacionCompartir);
            return;
        }

        GrupoCompartir grupoSeleccionado = seleccionarGrupoCompartir(gruposDelUsuario, txtInformacionCompartir);
        if (grupoSeleccionado == null) return;

        List<Solucion> soluciones = grupoSeleccionado.getSoluciones();
        if (soluciones.isEmpty()) {
            mostrarMensajeError("⚠️ No hay soluciones en este grupo.", txtInformacionCompartir);
            return;
        }

        Solucion solucionSeleccionada = seleccionarSolucion(soluciones, txtInformacionCompartir);
        if (solucionSeleccionada == null) return;

        Alert votoAlert = new Alert(Alert.AlertType.CONFIRMATION);
        votoAlert.setTitle("Votar Solución");
        votoAlert.setHeaderText("Solución: " + solucionSeleccionada.getTitulo());
        votoAlert.setContentText("¿Desea dar like, dislike o quitar su voto?");

        ButtonType darLikeBtn = new ButtonType("👍 Dar Like");
        ButtonType darDislikeBtn = new ButtonType("👎 Dar Dislike");
        ButtonType quitarVotoBtn = new ButtonType("❌ Quitar Voto");
        ButtonType cancelarBtn = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        List<ButtonType> botones = new ArrayList<>();
        botones.add(darLikeBtn);
        botones.add(darDislikeBtn);
        botones.add(quitarVotoBtn);
        botones.add(cancelarBtn);
        votoAlert.getButtonTypes().setAll(botones);

        Optional<ButtonType> result = votoAlert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == darLikeBtn) {
                solucionSeleccionada.votar(usuario, 1);
                mostrarMensajeExito("✅ Like registrado para la solución '" + solucionSeleccionada.getTitulo() + "'", txtInformacionCompartir);
            } else if (result.get() == darDislikeBtn) {
                solucionSeleccionada.votar(usuario, -1);
                mostrarMensajeExito("✅ Dislike registrado para la solución '" + solucionSeleccionada.getTitulo() + "'", txtInformacionCompartir);
            } else if (result.get() == quitarVotoBtn) {
                solucionSeleccionada.votar(usuario, 0);
                mostrarMensajeExito("✅ Voto quitado para la solución '" + solucionSeleccionada.getTitulo() + "'", txtInformacionCompartir);
            } else {
                mostrarMensajeInfo("ℹ️ Acción cancelada.", txtInformacionCompartir);
            }
            // Actualizar persistencia después de cada voto
            String nombreComunidad = ContextoSistema.getInstance().getComunidadActual().getNombre();
            PersistenciaService.actualizarSolucion(nombreComunidad, grupoSeleccionado.getTitulo(), solucionSeleccionada);
            actualizarInformacion();
        }
    }

    /**
     * Método FXML para gestionar comentarios
     */
    @FXML
    private void gestionarComentarios() {
        try {
            if (!contexto.tieneComunidadActiva()) {
                mostrarMensajeError("⚠️ No hay una comunidad activa.", txtInformacionCompartir);
                return;
            }

            String accion = comboAccionComentario.getValue();
            ForoGeneral foro = contexto.getComunidadActual().getForoGeneral();
            UsuarioComunidad usuarioActual = SesionManager.getInstancia().getUsuarioComunidad();

            if (usuarioActual == null) {
                mostrarMensajeError("⚠️ No hay un usuario autenticado. Inicie sesión para gestionar comentarios.", txtInformacionCompartir);
                return;
            }

            switch (accion) {
                case "Comentar Solución":
                    comentarSolucion(foro, usuarioActual);
                    break;
                case "Eliminar Comentario":
                    eliminarComentarioSolucion(foro, usuarioActual);
                    break;
                case "Votar Comentario":
                    votarComentario(foro, usuarioActual); // Nuevo método
                    break;
                default:
                    mostrarMensajeError("⚠️ Seleccione una acción válida para comentarios.", txtInformacionCompartir);
            }
        } catch (Exception e) {
            mostrarMensajeError("❌ Error al gestionar comentarios: " + e.getMessage(), txtInformacionCompartir);
        }
    }

    /**
     * Comentar una solución (solo en grupos a los que el usuario pertenece)
     */
    private void comentarSolucion(ForoGeneral foro, UsuarioComunidad autorComentario) {
        List<GrupoCompartir> gruposDelUsuario = foro.getGruposCompartir().stream()
                .filter(grupo -> grupo.getMiembros().contains(autorComentario))
                .collect(Collectors.toList());

        if (gruposDelUsuario.isEmpty()) {
            mostrarMensajeError("⚠️ No eres miembro de ningún grupo de compartir para comentar soluciones.", txtInformacionCompartir);
            return;
        }

        GrupoCompartir grupoSeleccionado = seleccionarGrupoCompartir(gruposDelUsuario, txtInformacionCompartir);
        if (grupoSeleccionado == null) return;

        List<Solucion> soluciones = grupoSeleccionado.getSoluciones();
        if (soluciones.isEmpty()) {
            mostrarMensajeError("⚠️ No hay soluciones en este grupo.", txtInformacionCompartir);
            return;
        }

        Solucion solucionSeleccionada = seleccionarSolucion(soluciones, txtInformacionCompartir);
        if (solucionSeleccionada == null) return;

        TextInputDialog comentarioDialog = new TextInputDialog();
        comentarioDialog.setTitle("Comentar Solución");
        comentarioDialog.setHeaderText("Comentando la solución: '" + solucionSeleccionada.getTitulo() + "'");
        comentarioDialog.setContentText("Escriba su comentario:");

        Optional<String> result = comentarioDialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String contenidoComentario = result.get().trim();
            solucionSeleccionada.comentar(contenidoComentario, autorComentario);

            // Guardar el comentario en persistencia
            String nombreComunidad = ContextoSistema.getInstance().getComunidadActual().getNombre();
            Comentario nuevoComentario = solucionSeleccionada.getComentarios().stream()
                    .filter(c -> c.getContenido().equals(contenidoComentario) && c.getAutor().equals(autorComentario))
                    .findFirst().orElse(null);
            if (nuevoComentario != null) {
                PersistenciaService.guardarComentarioSolucion(nombreComunidad, grupoSeleccionado.getTitulo(), solucionSeleccionada.getIdSolucion(), nuevoComentario.getIdComentario(), nuevoComentario.getContenido(), nuevoComentario.getAutor().getUsername(), nuevoComentario.getVotosUsuarios());
                mostrarMensajeExito("✅ Comentario añadido a la solución.", txtInformacionCompartir);
            } else {
                mostrarMensajeError("❌ Error al añadir comentario.", txtInformacionCompartir);
            }
        } else {
            mostrarMensajeInfo("ℹ️ Comentario cancelado o vacío.", txtInformacionCompartir);
        }
        actualizarInformacion();
    }

    /**
     * Eliminar un comentario de una solución (solo comentarios del usuario actual en grupos a los que pertenece)
     */
    private void eliminarComentarioSolucion(ForoGeneral foro, UsuarioComunidad usuarioActual) {
        List<GrupoCompartir> gruposDelUsuario = foro.getGruposCompartir().stream()
                .filter(grupo -> grupo.getMiembros().contains(usuarioActual))
                .collect(Collectors.toList());

        if (gruposDelUsuario.isEmpty()) {
            mostrarMensajeError("⚠️ No eres miembro de ningún grupo de compartir para eliminar comentarios.", txtInformacionCompartir);
            return;
        }

        GrupoCompartir grupoSeleccionado = seleccionarGrupoCompartir(gruposDelUsuario, txtInformacionCompartir);
        if (grupoSeleccionado == null) return;

        List<Solucion> soluciones = grupoSeleccionado.getSoluciones();
        if (soluciones.isEmpty()) {
            mostrarMensajeError("⚠️ No hay soluciones en este grupo.", txtInformacionCompartir);
            return;
        }

        Solucion solucionSeleccionada = seleccionarSolucion(soluciones, txtInformacionCompartir);
        if (solucionSeleccionada == null) return;

        List<Comentario> comentarios = solucionSeleccionada.getComentarios();
        if (comentarios.isEmpty()) {
            mostrarMensajeError("⚠️ No hay comentarios en esta solución.", txtInformacionCompartir);
            return;
        }

        StringBuilder comentariosInfo = new StringBuilder("=== TUS COMENTARIOS EN SOLUCIÓN ===\n\n");
        List<Comentario> comentariosDelUsuarioEnSolucion = comentarios.stream()
                .filter(c -> c.getAutor().equals(usuarioActual))
                .collect(Collectors.toList());

        if (comentariosDelUsuarioEnSolucion.isEmpty()) {
            mostrarMensajeInfo("ℹ️ No hay comentarios tuyos para eliminar en esta solución.", txtInformacionCompartir);
            return;
        }

        for (int i = 0; i < comentariosDelUsuarioEnSolucion.size(); i++) {
            Comentario comentario = comentariosDelUsuarioEnSolucion.get(i);
            comentariosInfo.append(String.format("%d. %s\n",
                    (i + 1), comentario.toString())); // Usar toString de Comentario
        }

        TextInputDialog comentarioDialog = new TextInputDialog();
        comentarioDialog.setTitle("Eliminar Comentario");
        comentarioDialog.setHeaderText(comentariosInfo.toString());
        comentarioDialog.setContentText("Seleccione el número del comentario a eliminar:");

        Optional<String> result = comentarioDialog.showAndWait();
        if (result.isPresent()) {
            try {
                int indiceComentario = Integer.parseInt(result.get().trim()) - 1;
                if (indiceComentario >= 0 && indiceComentario < comentariosDelUsuarioEnSolucion.size()) {
                    Comentario comentarioAEliminar = comentariosDelUsuarioEnSolucion.get(indiceComentario);
                    solucionSeleccionada.eliminarComentario(comentarioAEliminar.getIdComentario());

                    // Eliminar de persistencia
                    String nombreComunidad = ContextoSistema.getInstance().getComunidadActual().getNombre();
                    PersistenciaService.eliminarComentarioPersistido(nombreComunidad, grupoSeleccionado.getTitulo(), solucionSeleccionada.getIdSolucion(), comentarioAEliminar.getIdComentario());

                    mostrarMensajeExito("✅ Comentario eliminado exitosamente.", txtInformacionCompartir);
                } else {
                    mostrarMensajeError("❌ Índice de comentario inválido.", txtInformacionCompartir);
                }
            } catch (NumberFormatException e) {
                mostrarMensajeError("❌ Debe ingresar un número válido.", txtInformacionCompartir);
            }
        } else {
            mostrarMensajeInfo("ℹ️ Eliminación de comentario cancelada.", txtInformacionCompartir);
        }
        actualizarInformacion();
    }

    /**
     * Gestionar likes/dislikes de un comentario (solo en grupos a los que el usuario pertenece)
     */
    private void votarComentario(ForoGeneral foro, UsuarioComunidad usuario) {
        List<GrupoCompartir> gruposDelUsuario = foro.getGruposCompartir().stream()
                .filter(grupo -> grupo.getMiembros().contains(usuario))
                .collect(Collectors.toList());

        if (gruposDelUsuario.isEmpty()) {
            mostrarMensajeError("⚠️ No eres miembro de ningún grupo de compartir para gestionar likes de comentarios.", txtInformacionCompartir);
            return;
        }

        GrupoCompartir grupoSeleccionado = seleccionarGrupoCompartir(gruposDelUsuario, txtInformacionCompartir);
        if (grupoSeleccionado == null) return;

        List<Solucion> soluciones = grupoSeleccionado.getSoluciones();
        if (soluciones.isEmpty()) {
            mostrarMensajeError("⚠️ No hay soluciones en este grupo.", txtInformacionCompartir);
            return;
        }

        Solucion solucionSeleccionada = seleccionarSolucion(soluciones, txtInformacionCompartir);
        if (solucionSeleccionada == null) return;

        List<Comentario> comentarios = solucionSeleccionada.getComentarios();
        if (comentarios.isEmpty()) {
            mostrarMensajeError("⚠️ No hay comentarios en esta solución.", txtInformacionCompartir);
            return;
        }

        Comentario comentarioSeleccionado = seleccionarComentario(comentarios, txtInformacionCompartir);
        if (comentarioSeleccionado == null) return;

        Alert votoAlert = new Alert(Alert.AlertType.CONFIRMATION);
        votoAlert.setTitle("Votar Comentario");
        votoAlert.setHeaderText("Comentario de: " + comentarioSeleccionado.getAutor().getNombre());
        votoAlert.setContentText("¿Desea dar like, dislike o quitar su voto?");

        ButtonType darLikeBtn = new ButtonType("👍 Dar Like");
        ButtonType darDislikeBtn = new ButtonType("👎 Dar Dislike");
        ButtonType quitarVotoBtn = new ButtonType("❌ Quitar Voto");
        ButtonType cancelarBtn = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        List<ButtonType> botones = new ArrayList<>();
        botones.add(darLikeBtn);
        botones.add(darDislikeBtn);
        botones.add(quitarVotoBtn);
        botones.add(cancelarBtn);
        votoAlert.getButtonTypes().setAll(botones);

        Optional<ButtonType> result = votoAlert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == darLikeBtn) {
                comentarioSeleccionado.votar(usuario, 1);
                mostrarMensajeExito("✅ Like dado al comentario de '" + comentarioSeleccionado.getAutor().getNombre() + "'", txtInformacionCompartir);
            } else if (result.get() == darDislikeBtn) {
                comentarioSeleccionado.votar(usuario, -1);
                mostrarMensajeExito("✅ Dislike dado al comentario de '" + comentarioSeleccionado.getAutor().getNombre() + "'", txtInformacionCompartir);
            } else if (result.get() == quitarVotoBtn) {
                comentarioSeleccionado.votar(usuario, 0);
                mostrarMensajeExito("✅ Voto quitado para el comentario de '" + comentarioSeleccionado.getAutor().getNombre() + "'", txtInformacionCompartir);
            } else {
                mostrarMensajeInfo("ℹ️ Acción cancelada.", txtInformacionCompartir);
            }
            // Actualizar persistencia después de cada voto
            String nombreComunidad = ContextoSistema.getInstance().getComunidadActual().getNombre();
            PersistenciaService.actualizarComentario(nombreComunidad, grupoSeleccionado.getTitulo(), solucionSeleccionada.getIdSolucion(), comentarioSeleccionado);
            actualizarInformacion();
        }
    }

    // Métodos auxiliares para selección de elementos (adaptados para usar TextArea)
    // Estos métodos ahora reciben una lista de grupos ya filtrada por la membresía del usuario
    private GrupoDiscusion seleccionarGrupoDiscusion(List<GrupoDiscusion> gruposDisponibles, TextArea areaTexto) {
        StringBuilder info = new StringBuilder("=== GRUPOS DE DISCUSIÓN DISPONIBLES ===\n\n");
        for (int i = 0; i < gruposDisponibles.size(); i++) {
            info.append(String.format("%d. %s\n", (i + 1), gruposDisponibles.get(i).getTitulo()));
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Seleccionar Grupo de Discusión");
        dialog.setHeaderText(info.toString());
        dialog.setContentText("Seleccione el grupo (número):");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int indice = Integer.parseInt(result.get().trim()) - 1;
                if (indice >= 0 && indice < gruposDisponibles.size()) {
                    return gruposDisponibles.get(indice);
                }
            } catch (NumberFormatException e) { /* Ignorar */ }
        }
        mostrarMensajeError("❌ Selección de grupo inválida o cancelada.", areaTexto);
        return null;
    }

    private GrupoCompartir seleccionarGrupoCompartir(List<GrupoCompartir> gruposDisponibles, TextArea areaTexto) {
        StringBuilder info = new StringBuilder("=== GRUPOS DE COMPARTIR DISPONIBLES ===\n\n");
        for (int i = 0; i < gruposDisponibles.size(); i++) {
            info.append(String.format("%d. %s\n", (i + 1), gruposDisponibles.get(i).getTitulo()));
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Seleccionar Grupo de Compartir");
        dialog.setHeaderText(info.toString());
        dialog.setContentText("Seleccione el grupo (número):");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int indice = Integer.parseInt(result.get().trim()) - 1;
                if (indice >= 0 && indice < gruposDisponibles.size()) {
                    return gruposDisponibles.get(indice);
                }
            } catch (NumberFormatException e) { /* Ignorar */ }
        }
        mostrarMensajeError("❌ Selección de grupo inválida o cancelada.", areaTexto);
        return null;
    }

    private HiloDiscusion seleccionarHilo(List<HiloDiscusion> hilos, TextArea areaTexto) {
        StringBuilder info = new StringBuilder("=== HILOS DISPONIBLES ===\n\n");
        for (int i = 0; i < hilos.size(); i++) {
            info.append(String.format("%d. %s (Autor: %s, 👍%d 👎%d, Estado: %s)\n", // Mostrar likes/dislikes y estado
                    (i + 1), hilos.get(i).getTitulo(), hilos.get(i).getAutor().getNombre(), hilos.get(i).getVotosPositivos(), hilos.get(i).getVotosNegativos(), hilos.get(i).getEstado()));
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Seleccionar Hilo");
        dialog.setHeaderText(info.toString());
        dialog.setContentText("Seleccione el hilo (número):");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int indice = Integer.parseInt(result.get().trim()) - 1;
                if (indice >= 0 && indice < hilos.size()) {
                    return hilos.get(indice);
                }
            } catch (NumberFormatException e) { /* Ignorar */ }
        }
        mostrarMensajeError("❌ Selección de hilo inválida o cancelada.", areaTexto);
        return null;
    }

    private Respuesta seleccionarRespuesta(List<Respuesta> respuestas, TextArea areaTexto) {
        StringBuilder info = new StringBuilder("=== RESPUESTAS DISPONIBLES ===\n\n");
        for (int i = 0; i < respuestas.size(); i++) {
            info.append(String.format("%d. %s: %.50s... (👍%d 👎%d, Solución: %b)\n", // Modificado para mostrar likes y dislikes
                    (i + 1), respuestas.get(i).getAutor().getNombre(), respuestas.get(i).getContenido(),
                    respuestas.get(i).getVotosPositivos(), respuestas.get(i).getVotosNegativos(), respuestas.get(i).getEsSolucion()));
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Seleccionar Respuesta");
        dialog.setHeaderText(info.toString());
        dialog.setContentText("Seleccione la respuesta (número):");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int indice = Integer.parseInt(result.get().trim()) - 1;
                if (indice >= 0 && indice < respuestas.size()) {
                    return respuestas.get(indice);
                }
            } catch (NumberFormatException e) { /* Ignorar */ }
        }
        mostrarMensajeError("❌ Selección de respuesta inválida o cancelada.", areaTexto);
        return null;
    }

    private Solucion seleccionarSolucion(List<Solucion> soluciones, TextArea areaTexto) {
        StringBuilder info = new StringBuilder("=== SOLUCIONES DISPONIBLES ===\n\n");
        for (int i = 0; i < soluciones.size(); i++) {
            info.append(String.format("%d. %s (Autor: %s, 👍%d 👎%d)\n", // Modificado
                    (i + 1), soluciones.get(i).getTitulo(), soluciones.get(i).getAutor().getNombre(), soluciones.get(i).getLikes(), soluciones.get(i).getDislikes()));
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Seleccionar Solución");
        dialog.setHeaderText(info.toString());
        dialog.setContentText("Seleccione la solución (número):");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int indice = Integer.parseInt(result.get().trim()) - 1;
                if (indice >= 0 && indice < soluciones.size()) {
                    return soluciones.get(indice);
                }
            } catch (NumberFormatException e) { /* Ignorar */ }
        }
        mostrarMensajeError("❌ Selección de solución inválida o cancelada.", areaTexto);
        return null;
    }

    private Comentario seleccionarComentario(List<Comentario> comentarios, TextArea areaTexto) {
        StringBuilder info = new StringBuilder("=== COMENTARIOS DISPONIBLES ===\n\n");
        for (int i = 0; i < comentarios.size(); i++) {
            info.append(String.format("%d. %s\n",
                    (i + 1), comentarios.get(i).toString())); // Usar toString de Comentario
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Seleccionar Comentario");
        dialog.setHeaderText(info.toString());
        dialog.setContentText("Seleccione el comentario (número):");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int indice = Integer.parseInt(result.get().trim()) - 1;
                if (indice >= 0 && indice < comentarios.size()) {
                    return comentarios.get(indice);
                }
            } catch (NumberFormatException e) { /* Ignorar */ }
        }
        mostrarMensajeError("❌ Selección de comentario inválida o cancelada.", areaTexto);
        return null;
    }

    /**
     * Valida los datos de entrada para crear un grupo
     */
    private boolean validarDatosGrupo(TextArea areaTexto) {
        String titulo = txtTituloGrupo.getText().trim();

        if (titulo.isEmpty()) {
            mostrarMensajeError("⚠️ El título del grupo no puede estar vacío.", areaTexto);
            return false;
        }

        if (titulo.length() < 3) {
            mostrarMensajeError("⚠️ El título debe tener al menos 3 caracteres.", areaTexto);
            return false;
        }

        if (comboNivelJava.getValue() == null) {
            mostrarMensajeError("⚠️ Debe seleccionar un nivel de Java.", areaTexto);
            return false;
        }

        if (comboTema.getValue() == null) {
            mostrarMensajeError("⚠️ Debe seleccionar un tema.", areaTexto);
            return false;
        }

        return true;
    }

    /**
     * Actualiza la información mostrada en la interfaz
     */
    private void actualizarInformacion() {
        StringBuilder infoDiscusion = new StringBuilder();
        StringBuilder infoCompartir = new StringBuilder();

        if (contexto.tieneComunidadActiva()) {
            Comunidad comunidad = contexto.getComunidadActual();
            ForoGeneral foro = comunidad.getForoGeneral();
            UsuarioComunidad usuarioActual = SesionManager.getInstancia().getUsuarioComunidad();

            // Información para pestaña Discusión
            infoDiscusion.append("=== INFORMACIÓN DE DISCUSIÓN ===\n\n");
            infoDiscusion.append("📌 Comunidad: ").append(comunidad.getNombre()).append("\n");
            infoDiscusion.append("👥 Usuarios conectados: ").append(comunidad.getUsuariosConectados().size()).append("\n");

            List<GrupoDiscusion> gruposDiscusionDelUsuario = new ArrayList<>();
            if (usuarioActual != null) {
                gruposDiscusionDelUsuario = foro.getGruposDiscusion().stream()
                        .filter(grupo -> grupo.getMiembros().contains(usuarioActual))
                        .collect(Collectors.toList());
            }
            infoDiscusion.append("📋 Tus Grupos de Discusión: ").append(gruposDiscusionDelUsuario.size()).append("\n");

            // Mostrar grupos recientes de discusión del usuario
            if (!gruposDiscusionDelUsuario.isEmpty()) {
                infoDiscusion.append("\n🔶 Tus Grupos de Discusión Recientes:\n");
                for (int i = 0; i < Math.min(3, gruposDiscusionDelUsuario.size()); i++) {
                    GrupoDiscusion grupo = gruposDiscusionDelUsuario.get(i);
                    infoDiscusion.append("   • ").append(grupo.getTitulo())
                            .append(" (").append(grupo.getNivelJava()).append(")\n");
                }
            }

            // Mostrar estadísticas de hilos y respuestas en discusión de los grupos del usuario
            int totalHilosDiscusion = 0;
            int totalRespuestasDiscusion = 0;
            for (GrupoDiscusion grupo : gruposDiscusionDelUsuario) {
                totalHilosDiscusion += grupo.getHilos().size();
                for (HiloDiscusion hilo : grupo.getHilos()) {
                    totalRespuestasDiscusion += hilo.getRespuestas().size();
                }
            }
            infoDiscusion.append("\n📊 Actividad de Discusión en tus grupos:\n");
            infoDiscusion.append("   • Hilos de discusión: ").append(totalHilosDiscusion).append("\n");
            infoDiscusion.append("   • Respuestas a hilos: ").append(totalRespuestasDiscusion).append("\n");


            // Información para pestaña Compartir
            infoCompartir.append("=== INFORMACIÓN DE COMPARTIR ===\n\n");
            infoCompartir.append("📌 Comunidad: ").append(comunidad.getNombre()).append("\n");
            infoCompartir.append("👥 Usuarios conectados: ").append(comunidad.getUsuariosConectados().size()).append("\n");

            List<GrupoCompartir> gruposCompartirDelUsuario = new ArrayList<>();
            if (usuarioActual != null) {
                gruposCompartirDelUsuario = foro.getGruposCompartir().stream()
                        .filter(grupo -> grupo.getMiembros().contains(usuarioActual))
                        .collect(Collectors.toList());
            }
            infoCompartir.append("🔄 Tus Grupos de Compartir: ").append(gruposCompartirDelUsuario.size()).append("\n");

            // Mostrar grupos recientes de compartir del usuario
            if (!gruposCompartirDelUsuario.isEmpty()) {
                infoCompartir.append("\n🔷 Tus Grupos de Compartir Recientes:\n");
                for (int i = 0; i < Math.min(3, gruposCompartirDelUsuario.size()); i++) {
                    GrupoCompartir grupo = gruposCompartirDelUsuario.get(i);
                    infoCompartir.append("   • ").append(grupo.getTitulo())
                            .append(" (").append(grupo.getNivelJava()).append(")\n");
                }
            }

            // Mostrar estadísticas de soluciones y comentarios en compartir de los grupos del usuario
            int totalSolucionesCompartir = 0;
            int totalComentariosCompartir = 0;
            for (GrupoCompartir grupo : gruposCompartirDelUsuario) {
                totalSolucionesCompartir += grupo.getSoluciones().size();
                for (Solucion solucion : grupo.getSoluciones()) {
                    totalComentariosCompartir += solucion.getComentarios().size();
                }
            }
            infoCompartir.append("\n📊 Actividad de Compartir en tus grupos:\n");
            infoCompartir.append("   • Soluciones compartidas: ").append(totalSolucionesCompartir).append("\n");
            infoCompartir.append("   • Comentarios en soluciones: ").append(totalComentariosCompartir).append("\n");

            // Mostrar estadísticas de persistencia (se puede consolidar o mostrar en una sección aparte)
            try {
                String nombreComunidad = comunidad.getNombre();
                int membresiasPersistidas = 0;
                int hilosPersistidos = 0;
                int solucionesPersistidas = 0;
                int respuestasPersistidas = 0;
                int comentariosPersistidos = 0; // Nuevo

                // Contar membresías y contenido de grupos de discusión
                for (GrupoDiscusion grupo : foro.getGruposDiscusion()) {
                    membresiasPersistidas += PersistenciaService.obtenerMiembrosDelGrupo(nombreComunidad, grupo.getTitulo()).size();
                    List<Map<String, String>> hilos = PersistenciaService.obtenerHilosDelGrupo(nombreComunidad, grupo.getTitulo());
                    hilosPersistidos += hilos.size();

                    for (Map<String, String> hilo : hilos) {
                        respuestasPersistidas += PersistenciaService.obtenerRespuestasDelHilo(nombreComunidad, grupo.getTitulo(), hilo.get("id")).size();
                    }
                }

                for (GrupoCompartir grupo : foro.getGruposCompartir()) {
                    membresiasPersistidas += PersistenciaService.obtenerMiembrosDelGrupo(nombreComunidad, grupo.getTitulo()).size();
                    List<Map<String, String>> soluciones = PersistenciaService.obtenerSolucionesDelGrupo(nombreComunidad, grupo.getTitulo());
                    solucionesPersistidas += soluciones.size();
                    for (Map<String, String> solucion : soluciones) {
                        comentariosPersistidos += PersistenciaService.obtenerComentariosDeSolucion(nombreComunidad, grupo.getTitulo(), solucion.get("id")).size();
                    }
                }

                infoDiscusion.append("\n💾 Datos Persistidos (Discusión):\n");
                infoDiscusion.append("   • Membresías guardadas: ").append(membresiasPersistidas).append("\n");
                infoDiscusion.append("   • Hilos persistidos: ").append(hilosPersistidos).append("\n");
                infoDiscusion.append("   • Respuestas persistidas: ").append(respuestasPersistidas).append("\n");

                infoCompartir.append("\n💾 Datos Persistidos (Compartir):\n");
                infoCompartir.append("   • Membresías guardadas: ").append(membresiasPersistidas).append("\n");
                infoCompartir.append("   • Soluciones persistidas: ").append(solucionesPersistidas).append("\n");
                infoCompartir.append("   • Comentarios persistidos: ").append(comentariosPersistidos).append("\n"); // Nuevo

            } catch (Exception e) {
                infoDiscusion.append("\n⚠️ Error al cargar datos persistidos: ").append(e.getMessage()).append("\n");
                infoCompartir.append("\n⚠️ Error al cargar datos persistidos: ").append(e.getMessage()).append("\n");
            }

        } else {
            infoDiscusion.append("⚠️ No hay comunidad activa\n");
            infoDiscusion.append("Primero debe crear o seleccionar una comunidad");
            infoCompartir.append("⚠️ No hay comunidad activa\n");
            infoCompartir.append("Primero debe crear o seleccionar una comunidad");
        }

        txtInformacionDiscusion.setText(infoDiscusion.toString());
        txtInformacionCompartir.setText(infoCompartir.toString());

        // Actualizar también el estado del sistema
        actualizarEstadoSistema();
    }

    /**
     * Muestra información general en el área de texto especificada
     */
    private void mostrarInformacion(String mensaje, TextArea areaTexto) {
        areaTexto.setText(mensaje);
    }

    /**
     * Muestra un mensaje de éxito con color verde en el área de texto especificada
     */
    private void mostrarMensajeExito(String mensaje, TextArea areaTexto) {
        areaTexto.setStyle("-fx-text-fill: green; -fx-background-color: #f0fff0; -fx-border-color: #90ee90;");
        areaTexto.setText(mensaje);
        restaurarEstiloAreaTexto(areaTexto);
    }

    /**
     * Muestra un mensaje de error con color rojo en el área de texto especificada
     */
    private void mostrarMensajeError(String mensaje, TextArea areaTexto) {
        areaTexto.setStyle("-fx-text-fill: red; -fx-background-color: #fff0f0; -fx-border-color: #ffcccc;");
        areaTexto.setText(mensaje);
        restaurarEstiloAreaTexto(areaTexto);
    }

    /**
     * Muestra un mensaje de información con color azul en el área de texto especificada
     */
    private void mostrarMensajeInfo(String mensaje, TextArea areaTexto) {
        areaTexto.setStyle("-fx-text-fill: blue; -fx-background-color: #f0f8ff; -fx-border-color: #add8e6;");
        areaTexto.setText(mensaje);
        restaurarEstiloAreaTexto(areaTexto);
    }

    /**
     * Restaura el estilo del área de texto después de un tiempo
     */
    private void restaurarEstiloAreaTexto(TextArea areaTexto) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            areaTexto.setStyle("-fx-text-fill: black; -fx-background-color: #f4f4f4; -fx-border-color: #cccccc;");
            actualizarInformacion();
        }));
        timeline.play();
    }

    /**
     * Actualiza el estado del sistema en la etiqueta
     */
    private void actualizarEstadoSistema() {
        StringBuilder estado = new StringBuilder();

        if (contexto.tieneComunidadActiva()) {
            Comunidad comunidad = contexto.getComunidadActual();
            estado.append("✅ Comunidad: ").append(comunidad.getNombre());

            int usuariosConectados = comunidad.getUsuariosConectados().size();
            estado.append(usuariosConectados > 0 ?
                    " | ✅ " + usuariosConectados + " usuario(s) conectado(s)" :
                    " | ⚠️ Sin usuarios conectados");
        } else {
            estado.append("⚠️ Sin comunidad activa");
        }

        lblEstadoSistema.setText(estado.toString());
    }

    /**
     * Método FXML para volver al menú principal
     */
    @FXML
    private void volver() {
        try {
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            MetodosFrecuentes.cambiarVentana(stage, "/Modulo_Comunidad/Views/Comunidad.fxml");
        } catch (Exception e) {
            System.err.println("Error al volver: " + e.getMessage());
        }
    }

    /**
     * Método para verificar si un usuario es miembro de un grupo de discusión
     */
    private boolean esMiembroGrupoDiscusion(UsuarioComunidad usuario, GrupoDiscusion grupo) {
        return grupo.getMiembros().contains(usuario);
    }

    /**
     * Método para verificar si un usuario es miembro de un grupo de compartir
     */
    private boolean esMiembroGrupoCompartir(UsuarioComunidad usuario, GrupoCompartir grupo) {
        return grupo.getMiembros().contains(usuario);
    }

    // ===== MÉTODOS PARA MOSTRAR DATOS PERSISTIDOS (se mantienen) =====

    /**
     * Método adicional para mostrar estadísticas de persistencia del foro
     */
    @FXML
    private void mostrarEstadisticasPersistencia() {
        try {
            Comunidad comunidadActual = ContextoSistema.getInstance().getComunidadActual();
            if (comunidadActual == null) {
                mostrarMensajeError("❌ No hay comunidad activa.", txtInformacionDiscusion); // Se elige una de las áreas
                return;
            }

            StringBuilder info = new StringBuilder();
            info.append("=== ESTADÍSTICAS DE PERSISTENCIA DEL FORO ===\n\n");
            info.append("Comunidad: ").append(comunidadActual.getNombre()).append("\n\n");

            // Estadísticas de membresías
            String nombreComunidad = comunidadActual.getNombre();
            List<String> gruposDiscusion = comunidadActual.getForoGeneral().getGruposDiscusion().stream()
                    .map(g -> g.getTitulo()).collect(Collectors.toList());
            List<String> gruposCompartir = comunidadActual.getForoGeneral().getGruposCompartir().stream()
                    .map(g -> g.getTitulo()).collect(Collectors.toList());

            int totalMembresiasDis = 0;
            int totalMembresiasCom = 0;
            int totalHilos = 0;
            int totalSoluciones = 0;
            int totalRespuestas = 0;
            int totalComentarios = 0; // Nuevo

            // Contar membresías y contenido de grupos de discusión
            for (String grupo : gruposDiscusion) {
                List<String> miembros = PersistenciaService.obtenerMiembrosDelGrupo(nombreComunidad, grupo);
                totalMembresiasDis += miembros.size();

                List<Map<String, String>> hilos = PersistenciaService.obtenerHilosDelGrupo(nombreComunidad, grupo);
                totalHilos += hilos.size();

                for (Map<String, String> hilo : hilos) {
                    List<Map<String, String>> respuestas = PersistenciaService.obtenerRespuestasDelHilo(nombreComunidad, grupo, hilo.get("id"));
                    totalRespuestas += respuestas.size();
                }
            }

            // Contar membresías y contenido de grupos de compartir
            for (String grupo : gruposCompartir) {
                List<String> miembros = PersistenciaService.obtenerMiembrosDelGrupo(nombreComunidad, grupo);
                totalMembresiasCom += miembros.size();

                List<Map<String, String>> soluciones = PersistenciaService.obtenerSolucionesDelGrupo(nombreComunidad, grupo);
                totalSoluciones += soluciones.size();
                for (Map<String, String> solucion : soluciones) {
                    totalComentarios += PersistenciaService.obtenerComentariosDeSolucion(nombreComunidad, grupo, solucion.get("id")).size();
                }
            }

            info.append("📊 MEMBRESÍAS:\n");
            info.append("  • Grupos de Discusión: ").append(totalMembresiasDis).append(" membresías\n");
            info.append("  • Grupos de Compartir: ").append(totalMembresiasCom).append(" membresías\n\n");

            info.append("📝 CONTENIDO:\n");
            info.append("  • Hilos de Discusión: ").append(totalHilos).append("\n");
            info.append("  • Respuestas a Hilos: ").append(totalRespuestas).append("\n");
            info.append("  • Soluciones Compartidas: ").append(totalSoluciones).append("\n");
            info.append("  • Comentarios en Soluciones: ").append(totalComentarios).append("\n\n"); // Nuevo

            info.append("✅ Toda la actividad del foro se está guardando correctamente.\n");
            info.append("📁 Los datos persisten entre sesiones de la aplicación.");

            mostrarMensajeInfo(info.toString(), txtInformacionDiscusion); // Se elige una de las áreas

        } catch (Exception e) {
            mostrarMensajeError("❌ Error al obtener estadísticas: " + e.getMessage(), txtInformacionDiscusion); // Se elige una de las áreas
        }
    }

    /**
     * Método para verificar si un usuario es miembro de un grupo específico
     */
    public boolean verificarMembershipPersistida(String tituloGrupo, String username) {
        try {
            Comunidad comunidadActual = ContextoSistema.getInstance().getComunidadActual();
            if (comunidadActual == null) return false;

            return PersistenciaService.esUsuarioMiembroDelGrupo(comunidadActual.getNombre(), tituloGrupo, username);
        } catch (Exception e) {
            System.err.println("Error al verificar membresía persistida: " + e.getMessage());
            return false;
        }
    }
}
