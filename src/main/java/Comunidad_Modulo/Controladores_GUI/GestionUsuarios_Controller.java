package Comunidad_Modulo.Controladores_GUI;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import Comunidad_Modulo.controladores.ContextoSistema;
import Modulo_Usuario.Clases.UsuarioComunidad;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;

public class GestionUsuarios_Controller implements Initializable {
    
    // FXML Components que realmente existen en el archivo FXML
    @FXML private Button btnListarUsuarios;
    @FXML private TextField txtIndiceUsuario;  // Ahora para seleccionar comunidades
    @FXML private Button btnConectarUsuario;   // Conectarme a comunidad
    @FXML private Button btnDesconectarUsuario; // Desconectarme de comunidad
    @FXML private TextArea txtComunidades;   // Ahora mostrará comunidades disponibles
    @FXML private TextArea txtInformacion;
    @FXML private Button btnVolver;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar e inicializar el módulo de usuarios si no existe
        inicializarModuloUsuarios();
        
        // Configurar áreas de texto
        configurarAreasTexto();
        
        // Configurar etiquetas y textos para el nuevo flujo
        configurarInterfazParaConexionUsuario();
        
        // Cargar información inicial
        actualizarInformacionSistema();
    }
    
    /**
     * Configura la interfaz para que el usuario actual pueda conectarse a comunidades
     */
    private void configurarInterfazParaConexionUsuario() {
        
        // Configurar tooltip explicativo
        Tooltip tooltipConectar = new Tooltip("Unirse a una nueva comunidad o activar una comunidad existente");
        btnConectarUsuario.setTooltip(tooltipConectar);
        
        Tooltip tooltipDesconectar = new Tooltip("Salir permanentemente de una comunidad");
        btnDesconectarUsuario.setTooltip(tooltipDesconectar);
        
        // Configurar placeholder para el campo de índice
        txtIndiceUsuario.setPromptText("Número de comunidad");
    }
    
    /**
     * Inicializa el módulo de usuarios real si no está configurado
     */
    private void inicializarModuloUsuarios() {
        ContextoSistema contexto = ContextoSistema.getInstancia();
        
        if (contexto.getModuloUsuarios() == null) {
            try {
                // Crear e inicializar el módulo de usuarios real
                Comunidad_Modulo.integracion.ModuloUsuariosReal moduloReal = 
                    new Comunidad_Modulo.integracion.ModuloUsuariosReal();
                
                // Configurarlo en el contexto
                contexto.setModuloUsuarios(moduloReal);
                
                System.out.println("ModuloUsuariosReal inicializado correctamente");
            } catch (Exception e) {
                System.err.println("Error al inicializar ModuloUsuariosReal: " + e.getMessage());
                e.printStackTrace();
                
                // Como fallback, usar el simulado
                contexto.setModuloUsuarios(new Comunidad_Modulo.integracion.ModuloUsuariosSimulado());
                System.out.println("Usando ModuloUsuariosSimulado como fallback");
            }
        }
    }

    
    private void configurarAreasTexto() {
        // Configurar área de información como solo lectura
        txtInformacion.setEditable(false);
        txtInformacion.setWrapText(true);

        // Configurar área de lista de usuarios como solo lectura
        txtComunidades.setEditable(false);
        txtComunidades.setWrapText(true);
    }


    @FXML
    private void listarUsuarios() {
        actualizarInformacionSistema();
        mostrarListaComunidadesDisponibles();
        mostrarMensajeExito("Lista de comunidades actualizada");
    }
    
    @FXML
    private void conectarUsuario() {
        try {
            // Obtener el usuario actual desde SesionManager
            Conexion.SesionManager sesion = Conexion.SesionManager.getInstancia();
            
            if (!sesion.hayUsuarioAutenticado()) {
                mostrarMensajeError("No hay usuario autenticado. Inicie sesión primero.");
                return;
            }
            
            UsuarioComunidad usuarioActual = sesion.getUsuarioComunidad();
            if (usuarioActual == null) {
                mostrarMensajeError("Error al obtener información del usuario.");
                return;
            }
            
            // Obtener el índice de la comunidad
            String indiceTexto = txtIndiceUsuario.getText().trim();
            if (indiceTexto.isEmpty()) {
                mostrarMensajeError("Por favor ingrese el número de la comunidad a la que desea conectarse");
                return;
            }

            int indiceComunidad = Integer.parseInt(indiceTexto);
            ContextoSistema contexto = ContextoSistema.getInstancia();
            List<Comunidad_Modulo.modelo.Comunidad> comunidades = contexto.getComunidades();
            
            if (comunidades.isEmpty()) {
                mostrarMensajeError("No hay comunidades disponibles. Cree una comunidad primero.");
                return;
            }
            
            if (indiceComunidad < 1 || indiceComunidad > comunidades.size()) {
                mostrarMensajeError("Número de comunidad inválido. Use un número entre 1 y " + comunidades.size());
                return;
            }
            
            // Obtener la comunidad seleccionada
            Comunidad_Modulo.modelo.Comunidad comunidadSeleccionada = comunidades.get(indiceComunidad - 1);
            
            // Verificar si ya está conectado a esta comunidad
            boolean yaEsMiembro = comunidadSeleccionada.getUsuariosMiembros().stream()
                    .anyMatch(u -> u.getUsername().equals(usuarioActual.getUsername()));
            
            if (yaEsMiembro) {
                // Si ya es miembro, simplemente activar la comunidad y conectar
                contexto.setComunidadActual(comunidadSeleccionada);
                
                // Asegurar que esté conectado activamente
                if (!comunidadSeleccionada.getUsuariosConectados().stream()
                        .anyMatch(u -> u.getUsername().equals(usuarioActual.getUsername()))) {
                    comunidadSeleccionada.conectarUsuario(usuarioActual);
                }
                
                mostrarMensajeExito("¡Comunidad activada! 🎯\n" +
                                  "'" + comunidadSeleccionada.getNombre() + "' es ahora tu comunidad activa.\n" +
                                  "✅ Estado: MIEMBRO ACTIVO");
            } else {
                // Si no es miembro, unirse a la comunidad
                contexto.conectarUsuarioAComunidad(usuarioActual, comunidadSeleccionada);
                
                // Establecer esta comunidad como activa automáticamente
                contexto.setComunidadActual(comunidadSeleccionada);
                
                mostrarMensajeExito("¡Perfecto! Te has unido a la comunidad '" + 
                                  comunidadSeleccionada.getNombre() + "'.\n" +
                                  "✅ Ahora eres MIEMBRO ACTIVO de esta comunidad.\n" +
                                  "🎯 Esta comunidad está establecida como tu comunidad activa.");
            }
            
            // Actualizar información
            actualizarInformacionSistema();
            
        } catch (NumberFormatException e) {
            mostrarMensajeError("Por favor ingrese un número válido");
        } catch (Exception e) {
            mostrarMensajeError("Error al conectarse a la comunidad: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML 
    private void desconectarUsuario() {
        try {
            // Obtener el usuario actual desde SesionManager
            Conexion.SesionManager sesion = Conexion.SesionManager.getInstancia();
            
            if (!sesion.hayUsuarioAutenticado()) {
                mostrarMensajeError("No hay usuario autenticado. Inicie sesión primero.");
                return;
            }
            
            UsuarioComunidad usuarioActual = sesion.getUsuarioComunidad();
            if (usuarioActual == null) {
                mostrarMensajeError("Error al obtener información del usuario actual.");
                return;
            }
            
            ContextoSistema contexto = ContextoSistema.getInstancia();
            
            // Buscar en qué comunidades es miembro el usuario
            List<Comunidad_Modulo.modelo.Comunidad> comunidadesMiembro = new java.util.ArrayList<>();
            for (Comunidad_Modulo.modelo.Comunidad comunidad : contexto.getComunidades()) {
                if (comunidad.getUsuariosMiembros().stream()
                        .anyMatch(u -> u.getUsername().equals(usuarioActual.getUsername()))) {
                    comunidadesMiembro.add(comunidad);
                }
            }
            
            if (comunidadesMiembro.isEmpty()) {
                mostrarMensajeError("No eres miembro de ninguna comunidad");
                return;
            }
            
            // Si hay un índice especificado, salir de esa comunidad específica
            String indiceTexto = txtIndiceUsuario.getText().trim();
            if (!indiceTexto.isEmpty()) {
                try {
                    int indiceComunidad = Integer.parseInt(indiceTexto);
                    if (indiceComunidad >= 1 && indiceComunidad <= comunidadesMiembro.size()) {
                        Comunidad_Modulo.modelo.Comunidad comunidadASalir = comunidadesMiembro.get(indiceComunidad - 1);
                        contexto.desconectarUsuarioDeComunidad(usuarioActual, comunidadASalir);
                        mostrarMensajeExito("Has salido de la comunidad '" + 
                                          comunidadASalir.getNombre() + "'");
                    } else {
                        mostrarMensajeError("Número de comunidad inválido para salir");
                        return;
                    }
                } catch (NumberFormatException e) {
                    mostrarMensajeError("Por favor ingrese un número válido para la comunidad");
                    return;
                }
            } else {
                // Si no hay índice, salir de todas las comunidades
                for (Comunidad_Modulo.modelo.Comunidad comunidad : comunidadesMiembro) {
                    contexto.desconectarUsuarioDeComunidad(usuarioActual, comunidad);
                }
                mostrarMensajeExito("Has salido de todas las comunidades (" + 
                                  comunidadesMiembro.size() + " comunidades)");
            }
            
            // Actualizar información
            actualizarInformacionSistema();
            
        } catch (Exception e) {
            mostrarMensajeError("Error al desconectarse: " + e.getMessage());
            e.printStackTrace();
        }
    }    @FXML
    private void volver() {
        try {
            // Importar MetodosFrecuentes para cambiar ventana
            Conexion.MetodosFrecuentes.cambiarVentana(
                (javafx.stage.Stage) btnVolver.getScene().getWindow(),
                "/Modulo_Comunidad/Views/Comunidad.fxml",
                "Sistema de Comunidad"
            );
        } catch (Exception e) {
            mostrarMensajeError("Error al volver: " + e.getMessage());
        }
    }
    
    /**
     * Actualiza la información del sistema mostrando el usuario actual y su estado
     */
    private void actualizarInformacionSistema() {
        try {
            ContextoSistema contexto = ContextoSistema.getInstancia();
            Conexion.SesionManager sesion = Conexion.SesionManager.getInstancia();
            
            StringBuilder info = new StringBuilder("Mis Comunidades\n\n");
            
            // Información del usuario actual autenticado
            if (sesion.hayUsuarioAutenticado()) {
                UsuarioComunidad usuarioActual = sesion.getUsuarioComunidad();
                info.append("Mi Info:\n");
                info.append("   • Nombre: ").append(usuarioActual.getNombre()).append("\n");
                info.append("   • Username: ").append(usuarioActual.getUsername()).append("\n");
                info.append("   • Email: ").append(usuarioActual.getEmail() != null && !usuarioActual.getEmail().isEmpty() ? 
                           usuarioActual.getEmail() : "No especificado").append("\n");
                info.append("   • Nivel Java: ").append(usuarioActual.getNivelJava()).append("\n");
                info.append("   • Reputación: ").append(usuarioActual.getReputacion()).append(" puntos\n\n");
                
                // Mostrar comunidades a las que está conectado
                List<Comunidad_Modulo.modelo.Comunidad> comunidadesConectadas = new java.util.ArrayList<>();
                for (Comunidad_Modulo.modelo.Comunidad comunidad : contexto.getComunidades()) {
                    if (comunidad.getUsuariosConectados().stream()
                            .anyMatch(u -> u.getUsername().equals(usuarioActual.getUsername()))) {
                        comunidadesConectadas.add(comunidad);
                    }
                }
                
                info.append("🏘️ Comunidades Conectadas:\n");
                if (comunidadesConectadas.isEmpty()) {
                    info.append("   • No estás conectado a ninguna comunidad\n");
                    info.append("   • ¿Buscas una comunidad? Pulsa 'Ver Comunidades' para verlas todas\n");
                } else {
                    for (int i = 0; i < comunidadesConectadas.size(); i++) {
                        Comunidad_Modulo.modelo.Comunidad comunidad = comunidadesConectadas.get(i);
                        info.append("   ").append(i + 1).append(". ").append(comunidad.getNombre())
                            .append(" (").append(comunidad.getUsuariosConectados().size()).append(" usuarios)\n");
                    }
                }
                info.append("\n");
                
            } else {
                info.append("⚠️ Mi Info:\n");
                info.append("No hay usuario autenticado.\n");
                info.append("Inicie sesión en el sistema principal primero.\n\n");
            }
            
            info.append("💡 Instrucciones:\n");
            info.append("1. Use 'Comunidades' para listar las comunidades disponibles\n");
            info.append("2. Ingrese el número de la comunidad en el campo\n");
            info.append("3. Use 'Unirse' para unirse a una nueva o activar una existente\n");
            info.append("4. Use 'Abandonar' para abandonar permanentemente una comunidad\n");
            info.append("5. Solo puede tener una comunidad activa a la vez");
            
            txtInformacion.setText(info.toString());
            
            // Actualizar la lista de comunidades disponibles
            mostrarListaComunidadesDisponibles();
            
        } catch (Exception e) {
            mostrarMensajeError("Error al cargar información del sistema: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Muestra la lista de comunidades disponibles para conectarse
     */
    private void mostrarListaComunidadesDisponibles() {
        try {
            ContextoSistema contexto = ContextoSistema.getInstancia();
            Conexion.SesionManager sesion = Conexion.SesionManager.getInstancia();
            
            StringBuilder listaComunidades = new StringBuilder("🏘️ Comunidades Disponibles\n");

            List<Comunidad_Modulo.modelo.Comunidad> comunidades = contexto.getComunidades();
            
            if (comunidades.isEmpty()) {
                listaComunidades.append("No hay comunidades creadas.\n");
                listaComunidades.append("Vaya a 'Gestionar Comunidad' para crear una nueva.\n\n");
            } else {
                listaComunidades.append("Total: ").append(comunidades.size()).append(" comunidad(es)\n\n");
                
                for (int i = 0; i < comunidades.size(); i++) {
                    Comunidad_Modulo.modelo.Comunidad comunidad = comunidades.get(i);
                    listaComunidades.append(String.format("%d. %s\n", (i + 1), comunidad.getNombre()));
                    listaComunidades.append("   Descripción: ").append(comunidad.getDescripcion()).append("\n");
                    listaComunidades.append("   Miembros totales: ").append(comunidad.getUsuariosMiembros().size()).append("\n");
                    listaComunidades.append("   Usuarios activos: ").append(comunidad.getUsuariosConectados().size()).append("\n");
                    listaComunidades.append("   Moderador: ").append(comunidad.getModerador().getNombre()).append("\n");
                    
                    // Verificar si el usuario actual es miembro de esta comunidad
                    boolean esMiembro = false;
                    boolean estaConectado = false;
                    if (sesion.hayUsuarioAutenticado()) {
                        UsuarioComunidad usuarioActual = sesion.getUsuarioComunidad();
                        esMiembro = comunidad.getUsuariosMiembros().stream()
                                  .anyMatch(u -> u.getUsername().equals(usuarioActual.getUsername()));
                        estaConectado = comunidad.getUsuariosConectados().stream()
                                      .anyMatch(u -> u.getUsername().equals(usuarioActual.getUsername()));
                    }
                    
                    listaComunidades.append("   Estado: ");
                    if (esMiembro) {
                        if (estaConectado) {
                            listaComunidades.append("✅ Miembro Activo");
                        } else {
                            listaComunidades.append("👤 Miembro");
                        }
                    } else {
                        listaComunidades.append("❌ No Miembro");
                    }
                    listaComunidades.append("\n\n");
                }
            }
            
            listaComunidades.append("💡 Tip: Use el número de la comunidad para conectarse o desconectarse");

            txtComunidades.setText(listaComunidades.toString());
            
        } catch (Exception e) {
            txtComunidades.setText("Error al cargar lista de comunidades: " + e.getMessage());
        }
    }
    
    private void mostrarMensajeExito(String mensaje) {
        txtInformacion.setStyle("-fx-text-fill: green; -fx-background-color: #f0fff0; -fx-border-color: #90ee90;");
        txtInformacion.setText("✅ " + mensaje + "\n\n" + txtInformacion.getText());
        
        // Restaurar estilo después de 3 segundos
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.seconds(3), e -> {
                txtInformacion.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #cccccc;");
            })
        );
        timeline.play();
    }
    
    private void mostrarMensajeError(String mensaje) {
        txtInformacion.setStyle("-fx-text-fill: red; -fx-background-color: #fff0f0; -fx-border-color: #ffcccc;");
        txtInformacion.setText("❌ " + mensaje);
        
        // Restaurar estilo después de 4 segundos
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.seconds(4), e -> {
                txtInformacion.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #cccccc;");
                actualizarInformacionSistema();
            })
        );
        timeline.play();
    }
}
