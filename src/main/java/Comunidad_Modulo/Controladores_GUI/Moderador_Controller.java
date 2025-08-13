package Comunidad_Modulo.Controladores_GUI;

import Comunidad_Modulo.controladores.ContextoSistema;
import Comunidad_Modulo.modelo.*;
import Comunidad_Modulo.enums.TipoSolucion;
import Conexion.MetodosFrecuentes;
import Modulo_Usuario.Clases.UsuarioComunidad;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;

import java.util.List;
import java.util.stream.Collectors;

public class Moderador_Controller implements Initializable {

    // ========== CONTROLES DE LA NUEVA INTERFAZ ==========
    
    // Tab 1: Vista General
    @FXML private TextArea txtAreaEstadoSistema;
    @FXML private TextArea txtAreaEstadisticas;
    @FXML private TextArea txtAreaSanciones;
    @FXML private TextArea txtAreaInformacion;
    @FXML private Button btnActualizarInfo;
    @FXML private Button btnMostrarEstadisticas;
    
    // Tab 2: Gestión de Usuarios
    @FXML private TextField txtNombreUsuarioEliminar;
    @FXML private TextField txtNombreComunidad;
    @FXML private Button btnEliminarUsuario;
    @FXML private TextField txtComunidadEliminar;
    @FXML private Button btnEliminarComunidad;
    @FXML private TextField txtUsuarioSancion;
    @FXML private TextField txtRazonSancion;
    @FXML private TextField txtDuracionSancion;
    @FXML private Button btnAplicarSancion;
    @FXML private Button btnLevantarSancion;
    @FXML private Button btnListarUsuarios;
    @FXML private TextArea txtAreaUsuariosDetalle;
    
    // Tab 3: Contenido de Comunidades
    @FXML private ComboBox<String> comboComunidades;
    @FXML private Button btnCargarContenido;
    @FXML private TextArea txtAreaGruposDiscusion;
    @FXML private TextArea txtAreaGruposCompartir;
    @FXML private TextArea txtAreaChatsPrivados;
    
    // Tab 4: Historial y Moderación
    @FXML private ComboBox<String> comboTipoGrupo;
    @FXML private ComboBox<String> comboGrupos;
    @FXML private Button btnVerHistorial;
    @FXML private TextArea txtAreaHistorial;
    @FXML private Button btnRevisarContenido;
    @FXML private Button btnEscanearForos;
    
    // Botón General
    @FXML private Button btnVolver;

    // ========== VARIABLES DE INSTANCIA ==========
    private ContextoSistema contexto;
    private ModeradorManual moderadorManual;
    private ModeradorAutomatico moderadorAutomatico;

    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("🔄 Inicializando controlador de Moderador...");
        inicializarSistema();
        configuracionInterfazModerador();
        actualizarInformacion();
        configurarControlesHistorial();
    }

    private void inicializarSistema() {
        this.contexto = ContextoSistema.getInstance();
        
        // Usar la Factory para crear los moderadores
        ModeradorFactory factory = ModeradorFactory.getInstance();
        this.moderadorManual = factory.crearModeradorManual("Moderador GUI", "mod-gui");
        this.moderadorAutomatico = factory.obtenerModeradorAutomaticoSistema();
        
        System.out.println("✅ Sistema de moderación inicializado con nueva arquitectura completa");
    }

    private void configuracionInterfazModerador() {
        // Configurar áreas de texto como solo lectura
        txtAreaEstadoSistema.setEditable(false);
        txtAreaEstadisticas.setEditable(false);
        txtAreaSanciones.setEditable(false);
        txtAreaInformacion.setEditable(false);
        txtAreaUsuariosDetalle.setEditable(false);
        txtAreaGruposDiscusion.setEditable(false);
        txtAreaGruposCompartir.setEditable(false);
        txtAreaChatsPrivados.setEditable(false);
        txtAreaHistorial.setEditable(false);
        
        // Configurar wrap text
        txtAreaEstadoSistema.setWrapText(true);
        txtAreaEstadisticas.setWrapText(true);
        txtAreaSanciones.setWrapText(true);
        txtAreaInformacion.setWrapText(true);
        txtAreaUsuariosDetalle.setWrapText(true);
        txtAreaGruposDiscusion.setWrapText(true);
        txtAreaGruposCompartir.setWrapText(true);
        txtAreaChatsPrivados.setWrapText(true);
        txtAreaHistorial.setWrapText(true);
        
        // Configurar prompts
        txtNombreUsuarioEliminar.setPromptText("Username del usuario");
        txtNombreComunidad.setPromptText("Nombre de la Comunidad");
        txtComunidadEliminar.setPromptText("Nombre de la Comunidad a Eliminar");
        txtUsuarioSancion.setPromptText("Username para sancionar");
        txtRazonSancion.setPromptText("Razón de la sanción");
        txtDuracionSancion.setPromptText("Duración en minutos");
        
        // Configurar listeners para clics en las áreas de texto (para ver imágenes)
        txtAreaGruposCompartir.setOnMouseClicked(this::manejarClicEnAreaTexto);
        txtAreaHistorial.setOnMouseClicked(this::manejarClicEnAreaTexto);
        txtAreaInformacion.setOnMouseClicked(this::manejarClicEnAreaTexto);
        
        // 🔄 CARGAR DATOS PERSISTIDOS PARA EL MODERADOR
        System.out.println("🔄 Moderador: Iniciando carga de datos persistidos...");
        cargarDatosPersistidosParaModerador();
    }
    
    /**
     * Carga datos persistidos específicamente para que el moderador pueda ver toda la información
     */
    private void cargarDatosPersistidosParaModerador() {
        try {
            System.out.println("🔄 Moderador: Cargando datos persistidos...");
            
            // Cargar datos para todas las comunidades
            for (Comunidad comunidad : contexto.getComunidades()) {
                if (comunidad.getForoGeneral() != null) {
                    System.out.println("🔄 Cargando datos de foro para: " + comunidad.getNombre());
                    
                    // Usar PersistenciaService para cargar todos los datos
                    Comunidad_Modulo.servicios.PersistenciaService.cargarTodosLosDatosPersistidos(
                        comunidad.getForoGeneral(), 
                        comunidad.getNombre()
                    );
                    
                    System.out.println("✅ Datos cargados para " + comunidad.getNombre() + 
                                     " - Grupos de compartir: " + comunidad.getForoGeneral().getGruposCompartir().size());
                }
            }
            
            System.out.println("✅ Moderador: Todos los datos persistidos cargados correctamente");
            
        } catch (Exception e) {
            System.err.println("❌ Error al cargar datos persistidos para moderador: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /* Eliminación de un Usuario de una Comunidad */
    @FXML
    public void eliminarUsuario() {
        String nombreUsuario = txtNombreUsuarioEliminar.getText().trim();
        String nombreComunidad = txtNombreComunidad.getText().trim();

        if (nombreUsuario.isEmpty()) {
            mostrarMensajeError("Por favor, ingrese el nombre del usuario que desea eliminar.");
            return;
        }

        if (nombreComunidad.isEmpty()) {
            mostrarMensajeError("Por favor, ingrese el nombre de la comunidad.");
            return;
        }

        // Buscar la comunidad específica
        Optional<Comunidad> comunidadOpt = contexto.getComunidades().stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(nombreComunidad))
                .findFirst();

        if (!comunidadOpt.isPresent()) {
            mostrarMensajeError("No se encontró la comunidad '" + nombreComunidad + "'.");
            return;
        }

        Comunidad comunidad = comunidadOpt.get();
        
        // Buscar el usuario en la comunidad específica
        Optional<UsuarioComunidad> usuarioOpt = comunidad.getUsuariosMiembros().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(nombreUsuario))
                .findFirst();

        if (!usuarioOpt.isPresent()) {
            mostrarMensajeError("El usuario '" + nombreUsuario + "' no se encuentra en la comunidad '" +
                        nombreComunidad + "'.");
            return;
        }

        UsuarioComunidad usuario = usuarioOpt.get();

        // ===== USAR EL MODERADOR MANUAL PARA LA EXPULSIÓN =====
        moderadorManual.expulsarUsuarioDeComunidad(usuario, comunidad);
        
        // Actualizar la comunidad en el contexto
        contexto.actualizarComunidad(comunidad);
        
        // Desconectar al usuario del sistema
        contexto.desconectarUsuarioDeComunidad(usuario, comunidad);
        
        mostrarMensajeExito("Usuario '" + nombreUsuario + "' eliminado exitosamente de la comunidad '" + 
                           nombreComunidad + "'");

        // Limpiar los campos
        txtNombreUsuarioEliminar.clear();
        txtNombreComunidad.clear();
        actualizarInformacion();
    }

    /**
     * Eliminacion de una Comunidad
     * */

    @FXML
    public void eliminarComunidad() {
        String nombreComunidad = txtComunidadEliminar.getText().trim();

        if (nombreComunidad.isEmpty()) {
            mostrarMensajeError("Por favor, ingrese el nombre de la comunidad que desea eliminar.");
            return;
        }

        // Buscar la comunidad
        Optional<Comunidad> comunidadOpt = contexto.getComunidades().stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(nombreComunidad))
                .findFirst();

        if (!comunidadOpt.isPresent()) {
            mostrarMensajeError("No se encontró la comunidad '" + nombreComunidad + "'.");
            return;
        }

        // Mostrar diálogo de confirmación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro que desea eliminar la comunidad '" + nombreComunidad + "'?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            Comunidad comunidad = comunidadOpt.get();

            // ===== USAR EL MODERADOR MANUAL PARA ELIMINAR LA COMUNIDAD =====
            moderadorManual.eliminarComunidad(comunidad);

            // Eliminar la comunidad del contexto
            contexto.eliminarComunidad(comunidad);

            mostrarMensajeExito("Comunidad '" + nombreComunidad + "' eliminada exitosamente por moderador manual");

            // Limpiar el campo y actualizar la información
            txtComunidadEliminar.clear();
            actualizarInformacion();

            // Actualizar los ComboBox de comunidades
            cargarComunidades();
        }
    }

    @FXML
    public void listarUsuarios() {
        StringBuilder lista = new StringBuilder("👥 Lista de Usuarios por Comunidad:\n\n");

        for (Comunidad comunidad : contexto.getComunidades()) {
            lista.append("🌐 Comunidad: ").append(comunidad.getNombre()).append("\n");
            
            if (comunidad.getUsuariosMiembros().isEmpty()) {
                lista.append("   • Sin usuarios registrados\n");
            } else {
                for (UsuarioComunidad usuario : comunidad.getUsuariosMiembros()) {
                    boolean estaConectado = comunidad.getUsuariosConectados().stream()
                            .anyMatch(u -> u.getUsername().equals(usuario.getUsername()));
                    
                    // Verificar si tiene sanciones
                    boolean estaSancionado = moderadorManual.usuarioEstaSancionado(usuario);
                    String estadoSancion = estaSancionado ? " 🚫 SANCIONADO" : "";
                    
                    lista.append("   • ").append(usuario.getUsername())
                         .append(" [").append(estaConectado ? "✅ Conectado" : "❌ Desconectado").append("]")
                         .append(estadoSancion).append("\n");
                }
            }
            lista.append("\n");
        }

        // Mostrar en ambas áreas para compatibilidad
        txtAreaInformacion.setText(lista.toString());
        if (txtAreaUsuariosDetalle != null) {
            txtAreaUsuariosDetalle.setText(lista.toString());
        }
    }

    @FXML
    private void volver() {
        try {
            MetodosFrecuentes.cambiarVentana(
                    (Stage) btnVolver.getScene().getWindow(),
                    "/Modulo_Comunidad/Views/Admin/AdminComunidad.fxml",
                    "Sistema de Comunidad"
            );
        } catch (Exception e) {
            mostrarMensajeError("Error al volver: " + e.getMessage());
        }
    }

    private void actualizarInformacion() {
        // Actualizar toda la información de la interfaz
        actualizarEstadoSistema();
        actualizarEstadisticas();
        actualizarSanciones();
        listarUsuarios();
        cargarComunidades();
    }
    
    // ========== NUEVOS MÉTODOS PARA LA INTERFAZ AVANZADA ==========
    
    /**
     * Actualiza el estado general del sistema de moderación
     */
    private void actualizarEstadoSistema() {
        StringBuilder estado = new StringBuilder();
        estado.append("🛡️ SISTEMA DE MODERACIÓN ACTIVO\n\n");
        estado.append("👨‍💼 Moderador Manual: ").append(moderadorManual.getNombre()).append("\n");
        estado.append("🤖 Moderador Automático: ").append(moderadorAutomatico.getNombre()).append("\n");
        estado.append("🌐 Comunidades Gestionadas: ").append(contexto.getComunidades().size()).append("\n");
        estado.append("👥 Total de Usuarios: ").append(contexto.getUsuarios().size()).append("\n");
        estado.append("⚖️ Moderadores Activos: ").append(contexto.getModeradores().size()).append("\n\n");
        
        estado.append("🔧 Estado del Filtro Automático: ");
        estado.append(moderadorAutomatico.isFiltroActivado() ? "✅ ACTIVO" : "❌ INACTIVO").append("\n");
        estado.append("📊 Nivel de Moderación: ");
        int nivel = moderadorAutomatico.getNivelStricto();
        estado.append(nivel == 1 ? "🟢 Básico" : nivel == 2 ? "🟡 Medio" : "🔴 Estricto").append("\n");
        
        txtAreaEstadoSistema.setText(estado.toString());
    }
    
    /**
     * Actualiza las estadísticas generales
     */
    private void actualizarEstadisticas() {
        StringBuilder stats = new StringBuilder();
        stats.append("📈 ESTADÍSTICAS GENERALES\n\n");
        
        int totalUsuarios = 0;
        int usuariosConectados = 0;
        int totalGruposDiscusion = 0;
        int totalGruposCompartir = 0;
        int totalChatsPrivados = 0;
        
        for (Comunidad comunidad : contexto.getComunidades()) {
            totalUsuarios += comunidad.getUsuariosMiembros().size();
            usuariosConectados += comunidad.getUsuariosConectados().size();
            
            if (comunidad.getForoGeneral() != null) {
                totalGruposDiscusion += comunidad.getForoGeneral().getGruposDiscusion().size();
                totalGruposCompartir += comunidad.getForoGeneral().getGruposCompartir().size();
            }
            
            totalChatsPrivados += comunidad.getChatsPrivados().size();
        }
        
        stats.append("👥 Usuarios Totales: ").append(totalUsuarios).append("\n");
        stats.append("🟢 Usuarios Conectados: ").append(usuariosConectados).append("\n");
        stats.append("💬 Grupos de Discusión: ").append(totalGruposDiscusion).append("\n");
        stats.append("📚 Grupos de Compartir: ").append(totalGruposCompartir).append("\n");
        stats.append("💌 Chats Privados: ").append(totalChatsPrivados).append("\n\n");
        
        stats.append("📊 Actividad:\n");
        double tasaConexion = totalUsuarios > 0 ? (double) usuariosConectados / totalUsuarios * 100 : 0;
        stats.append("📶 Tasa de Conexión: ").append(String.format("%.1f%%", tasaConexion)).append("\n");
        
        txtAreaEstadisticas.setText(stats.toString());
    }
    
    /**
     * Actualiza la información de sanciones activas
     */
    private void actualizarSanciones() {
        StringBuilder sanciones = new StringBuilder();
        sanciones.append("🚫 SANCIONES ACTIVAS\n\n");
        
        List<SancionUsuario> sancionesActivas = moderadorManual.getSancionesActivas();
        
        if (sancionesActivas.isEmpty()) {
            sanciones.append("✅ No hay sanciones activas en el sistema\n");
            sanciones.append("🎉 Todos los usuarios están en buen estado");
        } else {
            sanciones.append("⚠️ Total de sanciones activas: ").append(sancionesActivas.size()).append("\n\n");
            
            for (SancionUsuario sancion : sancionesActivas) {
                sanciones.append("👤 Usuario: ").append(sancion.getUsuario().getUsername()).append("\n");
                sanciones.append("📝 Razón: ").append(sancion.getRazon()).append("\n");
                sanciones.append("⏰ Tiempo restante: ").append(sancion.getMinutosRestantes()).append(" min\n");
                sanciones.append("👨‍💼 Moderador: ").append(sancion.getModeradorResponsable()).append("\n");
                sanciones.append("─".repeat(30)).append("\n");
            }
        }
        
        txtAreaSanciones.setText(sanciones.toString());
    }
    
    @FXML
    public void actualizarInformacionCompleta() {
        actualizarInformacion();
        mostrarMensajeExito("✅ Información actualizada correctamente");
    }
    
    @FXML
    public void mostrarEstadisticasDetalladas() {
        // Mostrar estadísticas completas del moderador manual y automático
        moderadorManual.mostrarEstadisticasAccionesManual();
        moderadorAutomatico.mostrarEstadisticasAutomaticas();
        
        StringBuilder detalle = new StringBuilder();
        detalle.append("📊 ESTADÍSTICAS DETALLADAS DEL SISTEMA\n\n");
        detalle.append("=".repeat(50)).append("\n");
        detalle.append("Ver consola para estadísticas completas del sistema de moderación\n");
        detalle.append("=".repeat(50)).append("\n");
        
        txtAreaEstadisticas.setText(detalle.toString());
        mostrarMensajeExito("📊 Estadísticas detalladas mostradas en consola");
    }
    
    @FXML
    public void aplicarSancionManual() {
        String username = txtUsuarioSancion.getText().trim();
        String razon = txtRazonSancion.getText().trim();
        String duracionStr = txtDuracionSancion.getText().trim();
        
        if (username.isEmpty() || razon.isEmpty() || duracionStr.isEmpty()) {
            mostrarMensajeError("Por favor, complete todos los campos para aplicar la sanción");
            return;
        }
        
        try {
            int duracionMinutos = Integer.parseInt(duracionStr);
            
            // Buscar el usuario
            UsuarioComunidad usuario = buscarUsuario(username);
            if (usuario == null) {
                mostrarMensajeError("Usuario '" + username + "' no encontrado en el sistema");
                return;
            }
            
            // Aplicar sanción manual
            moderadorManual.aplicarSancionManual(usuario, razon, duracionMinutos);
            
            mostrarMensajeExito("⚖️ Sanción aplicada exitosamente a " + username + " por " + duracionMinutos + " minutos");
            
            // Limpiar campos
            txtUsuarioSancion.clear();
            txtRazonSancion.clear();
            txtDuracionSancion.clear();
            
            // Actualizar información
            actualizarSanciones();
            
        } catch (NumberFormatException e) {
            mostrarMensajeError("La duración debe ser un número válido en minutos");
        }
    }
    
    @FXML
    public void levantarSancionUsuario() {
        String username = txtUsuarioSancion.getText().trim();
        
        if (username.isEmpty()) {
            mostrarMensajeError("Por favor, ingrese el username del usuario");
            return;
        }
        
        // Buscar el usuario
        UsuarioComunidad usuario = buscarUsuario(username);
        if (usuario == null) {
            mostrarMensajeError("Usuario '" + username + "' no encontrado en el sistema");
            return;
        }
        
        // Verificar si tiene sanción activa
        if (!moderadorManual.usuarioEstaSancionado(usuario)) {
            mostrarMensajeError("El usuario '" + username + "' no tiene sanciones activas");
            return;
        }
        
        // Levantar sanción
        boolean levantada = moderadorManual.levantarSancion(usuario);
        
        if (levantada) {
            mostrarMensajeExito("🔓 Sanción levantada exitosamente para " + username);
            txtUsuarioSancion.clear();
            actualizarSanciones();
        } else {
            mostrarMensajeError("Error al levantar la sanción del usuario");
        }
    }
    
    @FXML
    public void cargarContenidoComunidad() {
        String nombreComunidad = comboComunidades.getValue();
        
        if (nombreComunidad == null || nombreComunidad.isEmpty()) {
            mostrarMensajeError("Por favor, seleccione una comunidad");
            return;
        }
        
        Optional<Comunidad> comunidadOpt = contexto.getComunidades().stream()
                .filter(c -> c.getNombre().equals(nombreComunidad))
                .findFirst();
                
        if (!comunidadOpt.isPresent()) {
            mostrarMensajeError("Comunidad no encontrada");
            return;
        }
        
        Comunidad comunidad = comunidadOpt.get();
        
        // 🔄 REFRESCAR DATOS PERSISTIDOS ANTES DE MOSTRAR
        if (comunidad.getForoGeneral() != null) {
            System.out.println("🔄 Moderador: Refrescando datos para " + nombreComunidad);
            Comunidad_Modulo.servicios.PersistenciaService.cargarTodosLosDatosPersistidos(
                comunidad.getForoGeneral(), 
                nombreComunidad
            );
        }
        
        cargarGruposDiscusion(comunidad);
        cargarGruposCompartir(comunidad);
        cargarChatsPrivados(comunidad);
        
        mostrarMensajeExito("📋 Contenido de '" + nombreComunidad + "' cargado exitosamente (datos actualizados desde persistencia)");
    }
    
    private void cargarGruposDiscusion(Comunidad comunidad) {
        StringBuilder grupos = new StringBuilder();
        grupos.append("💬 GRUPOS DE DISCUSIÓN\n");
        grupos.append("Comunidad: ").append(comunidad.getNombre()).append("\n\n");
        
        if (comunidad.getForoGeneral() == null || comunidad.getForoGeneral().getGruposDiscusion().isEmpty()) {
            grupos.append("📭 No hay grupos de discusión creados\n");
            grupos.append("💡 Los usuarios pueden crear nuevos grupos desde 'Gestión de Foro'");
        } else {
            grupos.append("📊 Total de grupos: ").append(comunidad.getForoGeneral().getGruposDiscusion().size()).append("\n\n");
            
            for (GrupoDiscusion grupo : comunidad.getForoGeneral().getGruposDiscusion()) {
                grupos.append("🏷️ Título: ").append(grupo.getTitulo()).append("\n");
                grupos.append("📚 Nivel: ").append(grupo.getNivelJava()).append("\n");
                grupos.append("🎯 Tema: ").append(grupo.getTipoTema()).append("\n");
                grupos.append("👥 Miembros: ").append(grupo.getMiembros().size()).append("\n");
                grupos.append("📑 Hilos: ").append(grupo.getHilos().size()).append("\n");
                
                // Mostrar hilos de discusión
                if (!grupo.getHilos().isEmpty()) {
                    grupos.append("📋 Hilos de discusión:\n");
                    for (HiloDiscusion hilo : grupo.getHilos()) {
                        grupos.append("  🔸 ").append(hilo.getTitulo())
                               .append(" por ").append(hilo.getAutor().getUsername())
                               .append(" [").append(hilo.getEstado()).append("]\n");
                        grupos.append("    👍").append(hilo.getVotosPositivos())
                               .append(" 👎").append(hilo.getVotosNegativos()).append("\n");
                        
                        // Mostrar respuestas si las hay
                        if (!hilo.getRespuestas().isEmpty()) {
                            grupos.append("    💬 ").append(hilo.getRespuestas().size()).append(" respuestas\n");
                        }
                    }
                }
                
                // Mostrar miembros
                if (!grupo.getMiembros().isEmpty()) {
                    grupos.append("📋 Lista de miembros:\n");
                    for (UsuarioComunidad miembro : grupo.getMiembros()) {
                        boolean estaConectado = comunidad.getUsuariosConectados().stream()
                                .anyMatch(u -> u.getUsername().equals(miembro.getUsername()));
                        grupos.append("  • ").append(miembro.getUsername())
                               .append(estaConectado ? " 🟢" : " 🔴").append("\n");
                    }
                }
                grupos.append("─".repeat(40)).append("\n");
            }
        }
        
        txtAreaGruposDiscusion.setText(grupos.toString());
    }
    
    private void cargarGruposCompartir(Comunidad comunidad) {
        StringBuilder grupos = new StringBuilder();
        grupos.append("📚 GRUPOS DE COMPARTIR\n");
        grupos.append("Comunidad: ").append(comunidad.getNombre()).append("\n\n");
        
        if (comunidad.getForoGeneral() == null || comunidad.getForoGeneral().getGruposCompartir().isEmpty()) {
            grupos.append("📭 No hay grupos de compartir creados\n");
            grupos.append("💡 Los usuarios pueden crear nuevos grupos desde 'Gestión de Foro'");
        } else {
            grupos.append("📊 Total de grupos: ").append(comunidad.getForoGeneral().getGruposCompartir().size()).append("\n\n");
            
            for (GrupoCompartir grupo : comunidad.getForoGeneral().getGruposCompartir()) {
                grupos.append("🏷️ Título: ").append(grupo.getTitulo()).append("\n");
                grupos.append("📚 Nivel: ").append(grupo.getNivelJava()).append("\n");
                grupos.append("🎯 Tema: ").append(grupo.getTipoTema()).append("\n");
                grupos.append("👥 Miembros: ").append(grupo.getMiembros().size()).append("\n");
                grupos.append(" Soluciones: ").append(grupo.getSoluciones().size()).append("\n");
                
                // Mostrar soluciones compartidas
                if (!grupo.getSoluciones().isEmpty()) {
                    grupos.append("📋 Soluciones compartidas:\n");
                    for (Solucion solucion : grupo.getSoluciones()) {
                        grupos.append("  🔹 ").append(solucion.getTitulo())
                               .append(" por ").append(solucion.getAutor().getUsername())
                               .append(" (").append(solucion.getTipoSolucion()).append(")\n");
                        
                        // Si es una imagen, mostrar información específica
                        if (solucion.getTipoSolucion() == TipoSolucion.IMAGEN && solucion.getArchivo() != null) {
                            if (esImagenValida(solucion.getArchivo())) {
                                String vistaPrevia = crearVistaPreviaImagen(solucion.getArchivo());
                                grupos.append("    📸 ").append(vistaPrevia).append("\n");
                                grupos.append("    📁 Ruta: ").append(solucion.getArchivo()).append("\n");
                            } else {
                                grupos.append("    ❌ Imagen no válida: ").append(solucion.getArchivo()).append("\n");
                            }
                        }
                        
                        // Mostrar likes/dislikes
                        grupos.append("    👍").append(solucion.getLikes())
                               .append(" 👎").append(solucion.getDislikes()).append("\n");
                        
                        // Mostrar comentarios si los hay
                        if (!solucion.getComentarios().isEmpty()) {
                            grupos.append("    💬 ").append(solucion.getComentarios().size()).append(" comentarios\n");
                        }
                    }
                }
                grupos.append("─".repeat(40)).append("\n");
            }
        }
        
        txtAreaGruposCompartir.setText(grupos.toString());
    }
    
    private void cargarChatsPrivados(Comunidad comunidad) {
        StringBuilder chats = new StringBuilder();
        chats.append("💌 CHATS PRIVADOS\n");
        chats.append("Comunidad: ").append(comunidad.getNombre()).append("\n\n");
        
        if (comunidad.getChatsPrivados().isEmpty()) {
            chats.append("📭 No hay chats privados activos\n");
            chats.append("💡 Los usuarios pueden iniciar chats desde 'Chat Privado'");
        } else {
            chats.append("📊 Total de chats: ").append(comunidad.getChatsPrivados().size()).append("\n\n");
            
            for (ChatPrivado chat : comunidad.getChatsPrivados()) {
                chats.append("🆔 ID Chat: ").append(chat.getIdChat()).append("\n");
                chats.append("👥 Participantes: ").append(chat.getParticipantes().size()).append("\n");
                
                // Mostrar participantes
                chats.append("📋 Lista de participantes:\n");
                for (UsuarioComunidad participante : chat.getParticipantes()) {
                    boolean estaConectado = comunidad.getUsuariosConectados().stream()
                            .anyMatch(u -> u.getUsername().equals(participante.getUsername()));
                    chats.append("  • ").append(participante.getUsername())
                         .append(estaConectado ? " 🟢 Conectado" : " 🔴 Desconectado").append("\n");
                }
                
                chats.append("💬 Mensajes: ").append(chat.getMensajes().size()).append("\n");
                chats.append("─".repeat(40)).append("\n");
            }
        }
        
        txtAreaChatsPrivados.setText(chats.toString());
    }
    
    @FXML
    public void revisarContenidoReportado() {
        // Simular revisión de contenido reportado
        String contenidoEjemplo = "Contenido reportado por comportamiento inapropiado...";
        UsuarioComunidad reportante = contexto.getUsuarios().isEmpty() ? 
            null : contexto.getUsuarios().get(0);
        
        if (reportante != null) {
            moderadorManual.revisarContenidoReportado(
                contenidoEjemplo, 
                reportante, 
                "Lenguaje inapropiado"
            );
            mostrarMensajeExito("🔍 Revisión de contenido reportado completada. Ver consola para detalles.");
        } else {
            mostrarMensajeError("No hay usuarios en el sistema para generar reportes");
        }
    }
    
    @FXML
    public void escanearForosAutomatico() {
        int forosEscaneados = 0;
        
        for (Comunidad comunidad : contexto.getComunidades()) {
            if (comunidad.getForoGeneral() != null) {
                moderadorAutomatico.escanearForoAutomatico(comunidad.getForoGeneral());
                forosEscaneados++;
            }
        }
        
        if (forosEscaneados > 0) {
            mostrarMensajeExito("🤖 Escaneo automático completado en " + forosEscaneados + " foros. Ver consola para detalles.");
        } else {
            mostrarMensajeError("No hay foros disponibles para escanear");
        }
    }
    
    /**
     * Busca un usuario en todas las comunidades
     */
    private UsuarioComunidad buscarUsuario(String username) {
        for (Comunidad comunidad : contexto.getComunidades()) {
            for (UsuarioComunidad usuario : comunidad.getUsuariosMiembros()) {
                if (usuario.getUsername().equalsIgnoreCase(username)) {
                    return usuario;
                }
            }
        }
        return null;
    }

    private void mostrarMensajeError(String mensaje) {
        txtAreaInformacion.setStyle("-fx-text-fill: red; -fx-background-color: #fff0f0; -fx-border-color: #ffcccc;");
        txtAreaInformacion.setText("❌ " + mensaje);
        restaurarEstilosDespuesDe(4);
    }

    private void mostrarMensajeExito(String mensaje) {
        txtAreaInformacion.setStyle("-fx-text-fill: green; -fx-background-color: #f0fff0; -fx-border-color: #90ee90;");
        txtAreaInformacion.setText("✅ " + mensaje);
        restaurarEstilosDespuesDe(3);
    }

    private void restaurarEstilosDespuesDe(int segundos) {
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(segundos), e -> {
                    txtAreaInformacion.setStyle("");
                    actualizarInformacion();
                })
        );
        timeline.play();
    }
    
    private void configurarControlesHistorial() {
        // Configurar los ComboBox
        comboComunidades.setItems(FXCollections.observableArrayList());
        comboTipoGrupo.setItems(FXCollections.observableArrayList(
            "Grupos de Discusión", "Grupos de Compartir"
        ));
        comboGrupos.setItems(FXCollections.observableArrayList());
        
        // Cargar comunidades disponibles
        cargarComunidades();
        
        // Listeners para actualizar las opciones dependientes
        comboComunidades.setOnAction(e -> comboTipoGrupo.setDisable(false));
        comboTipoGrupo.setOnAction(e -> cargarGruposDeComunidad());
        
        // Deshabilitar controles dependientes inicialmente
        comboTipoGrupo.setDisable(true);
        comboGrupos.setDisable(true);
        btnVerHistorial.setDisable(true);
    }
    
    private void cargarComunidades() {
        List<String> nombresComunidades = contexto.getComunidades().stream()
                .map(Comunidad::getNombre)
                .collect(Collectors.toList());
        comboComunidades.setItems(FXCollections.observableArrayList(nombresComunidades));
    }
    
    private void cargarGruposDeComunidad() {
        comboGrupos.getItems().clear();
        
        String nombreComunidad = comboComunidades.getValue();
        String tipoGrupo = comboTipoGrupo.getValue();
        if (nombreComunidad == null || tipoGrupo == null) return;
        
        Optional<Comunidad> comunidadOpt = contexto.getComunidades().stream()
                .filter(c -> c.getNombre().equals(nombreComunidad))
                .findFirst();
                
        if (comunidadOpt.isPresent()) {
            Comunidad comunidad = comunidadOpt.get();
            ForoGeneral foro = comunidad.getForoGeneral();
            
            List<String> nombresGrupos;
            if (tipoGrupo.equals("Grupos de Discusión")) {
                nombresGrupos = foro.getGruposDiscusion().stream()
                        .map(GrupoDiscusion::getTitulo)
                        .collect(Collectors.toList());
            } else {
                nombresGrupos = foro.getGruposCompartir().stream()
                        .map(GrupoCompartir::getTitulo)
                        .collect(Collectors.toList());
            }
            
            comboGrupos.setItems(FXCollections.observableArrayList(nombresGrupos));
            comboGrupos.setDisable(false);
            btnVerHistorial.setDisable(false);
        }
    }
    
    @FXML
    private void verHistorial() {
        String nombreComunidad = comboComunidades.getValue();
        String tipoGrupo = comboTipoGrupo.getValue();
        String nombreGrupo = comboGrupos.getValue();
        
        if (nombreComunidad == null || tipoGrupo == null || nombreGrupo == null) {
            mostrarMensajeError("Por favor, complete todas las selecciones.");
            return;
        }
        
        try {
            // 🔄 REFRESCAR DATOS PERSISTIDOS ANTES DE MOSTRAR HISTORIAL
            Optional<Comunidad> comunidadParaRefresh = contexto.getComunidades().stream()
                    .filter(c -> c.getNombre().equals(nombreComunidad))
                    .findFirst();
            
            if (comunidadParaRefresh.isPresent() && comunidadParaRefresh.get().getForoGeneral() != null) {
                System.out.println("🔄 Moderador: Refrescando datos para historial de " + nombreComunidad);
                Comunidad_Modulo.servicios.PersistenciaService.cargarTodosLosDatosPersistidos(
                    comunidadParaRefresh.get().getForoGeneral(), 
                    nombreComunidad
                );
            }
            
            StringBuilder historial = new StringBuilder();
            historial.append("📜 Historial de la Comunidad\n\n");
            historial.append("🌐 Comunidad: ").append(nombreComunidad).append("\n");
            historial.append("📋 Tipo: ").append(tipoGrupo).append("\n");
            historial.append("📝 Grupo: ").append(nombreGrupo).append("\n\n");
            
            Comunidad comunidad = contexto.getComunidades().stream()
                    .filter(c -> c.getNombre().equals(nombreComunidad))
                    .findFirst().get();
                    
            ForoGeneral foro = comunidad.getForoGeneral();
            
            if (tipoGrupo.equals("Grupos de Discusión")) {
                GrupoDiscusion grupo = foro.getGruposDiscusion().stream()
                        .filter(g -> g.getTitulo().equals(nombreGrupo))
                        .findFirst().get();
                        
                // Mostrar información del grupo de discusión
                historial.append("Nivel Java: ").append(grupo.getNivelJava()).append("\n");
                historial.append("Tema: ").append(grupo.getTipoTema()).append("\n\n");
                
                // Mostrar miembros específicos del grupo
                historial.append("👥 Miembros del Grupo: ").append(grupo.getMiembros().size()).append("\n");
                for (UsuarioComunidad miembro : grupo.getMiembros()) {
                    historial.append("  • ").append(miembro.getUsername()).append(" [Unido al grupo]\n");
                }
                
                // Mostrar todos los miembros de la comunidad
                historial.append("\n👥 Todos los miembros de la comunidad: ").append(comunidad.getUsuariosMiembros().size()).append("\n");
                for (UsuarioComunidad miembro : comunidad.getUsuariosMiembros()) {
                    boolean estaEnGrupo = grupo.getMiembros().stream()
                            .anyMatch(m -> m.getUsername().equals(miembro.getUsername()));
                    boolean estaConectado = comunidad.getUsuariosConectados().stream()
                            .anyMatch(u -> u.getUsername().equals(miembro.getUsername()));
                    
                    String estado = estaEnGrupo ? "[✅ En grupo]" : "[⚪ Necesita unirse al grupo]";
                    String conexion = estaConectado ? " [🟢 Conectado]" : " [🔴 Desconectado]";
                    historial.append("  • ").append(miembro.getUsername()).append(" ").append(estado).append(conexion).append("\n");
                }
                
                if (grupo.getMiembros().isEmpty()) {
                    historial.append("\n💡 Tip: Los usuarios deben ir a 'Gestión de Foro' y usar 'Unirse a Grupo' para participar en este grupo específico.\n");
                }
                
            } else {
                GrupoCompartir grupo = foro.getGruposCompartir().stream()
                        .filter(g -> g.getTitulo().equals(nombreGrupo))
                        .findFirst().get();
                        
                // Mostrar información del grupo de compartir
                historial.append("Nivel Java: ").append(grupo.getNivelJava()).append("\n");
                historial.append("Tema: ").append(grupo.getTipoTema()).append("\n\n");
                
                // Mostrar miembros específicos del grupo
                historial.append("👥 Miembros del Grupo: ").append(grupo.getMiembros().size()).append("\n");
                for (UsuarioComunidad miembro : grupo.getMiembros()) {
                    historial.append("  • ").append(miembro.getUsername()).append(" [Unido al grupo]\n");
                }
                
                // Mostrar todos los miembros de la comunidad
                historial.append("\n👥 Todos los miembros de la comunidad: ").append(comunidad.getUsuariosMiembros().size()).append("\n");
                for (UsuarioComunidad miembro : comunidad.getUsuariosMiembros()) {
                    boolean estaEnGrupo = grupo.getMiembros().stream()
                            .anyMatch(m -> m.getUsername().equals(miembro.getUsername()));
                    boolean estaConectado = comunidad.getUsuariosConectados().stream()
                            .anyMatch(u -> u.getUsername().equals(miembro.getUsername()));
                    
                    String estado = estaEnGrupo ? "[✅ En grupo]" : "[⚪ Necesita unirse al grupo]";
                    String conexion = estaConectado ? " [🟢 Conectado]" : " [🔴 Desconectado]";
                    historial.append("  • ").append(miembro.getUsername()).append(" ").append(estado).append(conexion).append("\n");
                }
                
                if (grupo.getMiembros().isEmpty()) {
                    historial.append("\n💡 Tip: Los usuarios deben ir a 'Gestión de Foro' y usar 'Unirse a Grupo' para participar en este grupo específico.\n");
                }
                
                historial.append("\n💡 Soluciones Compartidas:\n");
                for (Solucion solucion : grupo.getSoluciones()) {
                    historial.append("  � Título: ").append(solucion.getTitulo()).append("\n");
                    historial.append("  �👤 Autor: ").append(solucion.getAutor().getUsername()).append("\n");
                    historial.append("  🏷️ Tipo: ").append(solucion.getTipoSolucion()).append("\n");
                    
                    // Si es una imagen, mostrar información específica
                    if (solucion.getTipoSolucion() == TipoSolucion.IMAGEN && solucion.getArchivo() != null) {
                        if (esImagenValida(solucion.getArchivo())) {
                            String vistaPrevia = crearVistaPreviaImagen(solucion.getArchivo());
                            historial.append("  📸 ").append(vistaPrevia).append("\n");
                            historial.append("  📁 Ruta: ").append(solucion.getArchivo()).append("\n");
                        } else {
                            historial.append("  ❌ Imagen no válida: ").append(solucion.getArchivo()).append("\n");
                        }
                    }
                    
                    historial.append("  👍 Likes: ").append(solucion.getLikes())
                               .append(" 👎 Dislikes: ").append(solucion.getDislikes()).append("\n");
                    
                    // Mostrar comentarios si los hay
                    if (!solucion.getComentarios().isEmpty()) {
                        historial.append("  💬 Comentarios: ").append(solucion.getComentarios().size()).append("\n");
                    }
                    historial.append("\n");
                }
            }
            
            txtAreaHistorial.setText(historial.toString());
            
        } catch (Exception e) {
            mostrarMensajeError("Error al cargar el historial: " + e.getMessage());
        }
    }
    
    // ========== MÉTODOS PARA MANEJO DE IMÁGENES (MODERACIÓN) ==========
    
    /**
     * Método para mostrar una ventana emergente con la imagen completa
     */
    private void mostrarImagenCompleta(String rutaArchivo) {
        try {
            File archivo = new File(rutaArchivo);
            if (!archivo.exists()) {
                mostrarMensajeError("El archivo de imagen no existe: " + rutaArchivo);
                return;
            }

            // Crear nueva ventana para mostrar la imagen
            Stage ventanaImagen = new Stage();
            ventanaImagen.setTitle("Moderador - Visor de Imagen: " + archivo.getName());
            ventanaImagen.initModality(Modality.APPLICATION_MODAL);

            // Cargar la imagen
            Image imagen = new Image(archivo.toURI().toString());
            ImageView imageView = new ImageView(imagen);

            // Configurar el ImageView para que se ajuste al tamaño de la ventana
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setCache(true);

            // Establecer tamaño máximo para la imagen
            double maxWidth = 800;
            double maxHeight = 600;
            
            if (imagen.getWidth() > maxWidth || imagen.getHeight() > maxHeight) {
                imageView.setFitWidth(maxWidth);
                imageView.setFitHeight(maxHeight);
            }

            // Crear contenedor para la imagen
            StackPane contenedor = new StackPane();
            contenedor.getChildren().add(imageView);
            contenedor.setAlignment(Pos.CENTER);
            contenedor.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 20;");

            // Crear escena y mostrar ventana
            Scene escena = new Scene(contenedor);
            ventanaImagen.setScene(escena);
            ventanaImagen.setResizable(true);
            ventanaImagen.show();

        } catch (Exception e) {
            System.err.println("Error al mostrar imagen: " + e.getMessage());
            mostrarMensajeError("Error al cargar la imagen: " + e.getMessage());
        }
    }

    /**
     * Método para crear una miniatura de imagen clickeable
     */
    private String crearVistaPreviaImagen(String rutaArchivo) {
        try {
            File archivo = new File(rutaArchivo);
            if (!archivo.exists()) {
                return "❌ [Imagen no encontrada]";
            }

            // Crear texto clickeable que simule un botón
            String nombreArchivo = archivo.getName();
            return "🖼️ [" + nombreArchivo + "] (Click para ver)";

        } catch (Exception e) {
            System.err.println("Error al crear vista previa: " + e.getMessage());
            return "❌ [Error al cargar imagen]";
        }
    }

    /**
     * Método para validar si un archivo es una imagen válida
     */
    private boolean esImagenValida(String rutaArchivo) {
        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            return false;
        }

        try {
            File archivo = new File(rutaArchivo);
            if (!archivo.exists()) {
                return false;
            }

            String extension = rutaArchivo.toLowerCase();
            return extension.endsWith(".png") || extension.endsWith(".jpg") || 
                   extension.endsWith(".jpeg") || extension.endsWith(".gif") || 
                   extension.endsWith(".bmp");
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Método para manejar clics en las áreas de texto y detectar si se hace clic en una imagen
     */
    private void manejarClicEnAreaTexto(MouseEvent event) {
        TextArea areaTexto = (TextArea) event.getSource();
        String texto = areaTexto.getText();
        
        // Obtener la posición del clic
        int posicionClic = areaTexto.getCaretPosition();
        
        // Buscar líneas que contengan imágenes cerca de la posición del clic
        String[] lineas = texto.split("\n");
        int posicionActual = 0;
        
        for (String linea : lineas) {
            int inicioLinea = posicionActual;
            int finLinea = posicionActual + linea.length();
            
            // Si el clic está en esta línea
            if (posicionClic >= inicioLinea && posicionClic <= finLinea) {
                // Verificar si la línea contiene una imagen clickeable
                if (linea.contains("🖼️") && linea.contains("(Click para ver)")) {
                    // Extraer la ruta del archivo de las líneas siguientes
                    String rutaImagen = extraerRutaImagenDesdeLista(lineas, texto, inicioLinea);
                    if (rutaImagen != null && esImagenValida(rutaImagen)) {
                        mostrarImagenCompleta(rutaImagen);
                        return;
                    }
                }
            }
            
            posicionActual = finLinea + 1; // +1 por el \n
        }
    }

    /**
     * Extrae la ruta de la imagen de las líneas de texto
     */
    private String extraerRutaImagenDesdeLista(String[] lineas, String textoCompleto, int posicionLinea) {
        // Buscar la línea que contiene "📁 Ruta:" después de la línea clickeada
        for (int i = 0; i < lineas.length; i++) {
            String linea = lineas[i];
            
            // Si encontramos la línea con la imagen clickeable
            if (linea.contains("🖼️") && linea.contains("(Click para ver)")) {
                // Buscar la siguiente línea que contenga "📁 Ruta:"
                for (int j = i + 1; j < lineas.length && j < i + 3; j++) {
                    if (lineas[j].contains("📁 Ruta:")) {
                        String lineaRuta = lineas[j];
                        int inicioRuta = lineaRuta.indexOf("📁 Ruta:") + "📁 Ruta:".length();
                        if (inicioRuta < lineaRuta.length()) {
                            return lineaRuta.substring(inicioRuta).trim();
                        }
                    }
                }
            }
        }
        return null;
    }
}