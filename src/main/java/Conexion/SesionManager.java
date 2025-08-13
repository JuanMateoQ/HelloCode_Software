package Conexion;

import Modulo_Usuario.Clases.Usuario;
import Modulo_Usuario.Clases.UsuarioComunidad;
import Modulo_Usuario.Clases.NivelJava;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestor de sesión global para mantener el usuario autenticado
 * a través de todos los módulos del sistema.
 */
public class SesionManager {
    private static SesionManager instancia;
    private Usuario usuarioAutenticado;
    private UsuarioComunidad usuarioComunidad;
    private List<Usuario> usuarios;
    
    // Ruta del archivo de usuarios
    private static final String ARCHIVO_USUARIOS = "src/main/java/Modulo_Usuario/Usuarios/usuarios.txt";
    
    private SesionManager() {
        // Cargar usuarios al inicializar la instancia
        cargarUsuariosDesdeArchivo();
    }
    
    public static SesionManager getInstancia() {
        if (instancia == null) {
            instancia = new SesionManager();
        }
        return instancia;
    }

    public void guardarUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    /**
     * Carga todos los usuarios desde el archivo usuarios.txt
     */
    public void cargarUsuariosDesdeArchivo() {
        this.usuarios = new ArrayList<>();
        
        try {
            File file = new File(ARCHIVO_USUARIOS);
            if (!file.exists()) {
                System.err.println("⚠️ Archivo usuarios.txt no encontrado: " + ARCHIVO_USUARIOS);
                return;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    linea = linea.trim();
                    if (!linea.isEmpty()) {
                        Usuario usuario = Usuario.fromString(linea);
                        if (usuario != null) {
                            this.usuarios.add(usuario);
                        }
                    }
                }
            }
            
            System.out.println("✅ Cargados " + this.usuarios.size() + " usuarios desde archivo");
            
        } catch (IOException e) {
            System.err.println("❌ Error al cargar usuarios desde archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Actualiza la lista de usuarios recargando desde el archivo
     */
    public void actualizarUsuariosDesdeArchivo() {
        System.out.println("🔄 Actualizando lista de usuarios...");
        cargarUsuariosDesdeArchivo();
    }

    /**
     * Agrega un nuevo usuario a la lista y lo sincroniza con el archivo
     */
    public void agregarNuevoUsuario(Usuario nuevoUsuario) {
        if (nuevoUsuario == null) {
            System.err.println("⚠️ No se puede agregar un usuario nulo");
            return;
        }
        
        // Verificar si ya existe
        boolean yaExiste = usuarios.stream()
                .anyMatch(u -> u.getUsername().equals(nuevoUsuario.getUsername()));
        
        if (!yaExiste) {
            usuarios.add(nuevoUsuario);
            System.out.println("✅ Usuario agregado a la lista: " + nuevoUsuario.getUsername());
        } else {
            System.out.println("⚠️ Usuario ya existe en la lista: " + nuevoUsuario.getUsername());
        }
        
        // Recargar desde archivo para asegurar sincronización completa
        actualizarUsuariosDesdeArchivo();
    }

    /**
     * Fuerza una recarga completa de todos los usuarios
     */
    public void forzarRecarga() {
        System.out.println("🔄 Forzando recarga completa de usuarios...");
        cargarUsuariosDesdeArchivo();
    }

    /**
     * Busca un usuario por username en la lista cargada
     * Actualiza la lista antes de buscar para asegurar datos actualizados
     */
    public Usuario buscarUsuarioPorUsername(String username) {
        if (username == null) {
            return null;
        }
        
        // Actualizar lista antes de buscar
        actualizarUsuariosDesdeArchivo();
        
        if (usuarios == null) {
            return null;
        }
        
        Usuario encontrado = usuarios.stream()
                .filter(u -> username.equals(u.getUsername()))
                .findFirst()
                .orElse(null);
                
        if (encontrado != null) {
            System.out.println("👤 Usuario encontrado: " + encontrado.getUsername());
        } else {
            System.out.println("❌ Usuario no encontrado: " + username);
        }
        
        return encontrado;
    }

    /**
     * Obtiene todos los usuarios cargados
     */
    public List<Usuario> getTodosLosUsuarios() {
        if (usuarios == null) {
            System.out.println("⚠️ Lista de usuarios es null, cargando desde archivo...");
            cargarUsuariosDesdeArchivo();
        } else {
            // Siempre actualizar para asegurar datos más recientes
            actualizarUsuariosDesdeArchivo();
        }
        
        System.out.println("📊 Total de usuarios cargados: " + (usuarios != null ? usuarios.size() : 0));
        
        return usuarios != null ? new ArrayList<>(usuarios) : new ArrayList<>(); // Retornar copia para evitar modificaciones externas
    }

    /**
     * Inicia sesión con un usuario
     */
    public void iniciarSesion(Usuario usuario) {
        // Actualizar lista de usuarios antes de iniciar sesión
        actualizarUsuariosDesdeArchivo();
        
        this.usuarioAutenticado = usuario;
        // Convertir Usuario a UsuarioComunidad para uso en el módulo de comunidad
        this.usuarioComunidad = convertirAUsuarioComunidad(usuario);
        System.out.println("Sesión iniciada para: " + usuario.getUsername());
        System.out.println("📊 Datos del usuario en sesión: XP=" + usuario.getXp() + ", Vidas=" + usuario.getVidas() + ", Nombre=" + usuario.getNombre());
    }
    
    /**
     * Cierra la sesión actual
     */
    public void cerrarSesion() {
        this.usuarioAutenticado = null;
        this.usuarioComunidad = null;
        System.out.println("Sesión cerrada");
    }
    /**
     * Obtiene la lista de usuarios actualizada
     */
    public List<Usuario> getUsuarios() {
        // Siempre actualizar desde archivo antes de devolver
        actualizarUsuariosDesdeArchivo();
        return usuarios;
    }
    /**
     * Verifica si hay un usuario autenticado
     */
    public boolean hayUsuarioAutenticado() {
        return usuarioAutenticado != null;
    }
    
    /**
     * Obtiene el usuario autenticado (formato original)
     */
    public Usuario getUsuarioAutenticado() {
        // Sincronizar datos del usuario autenticado con el archivo
        if (usuarioAutenticado != null) {
            usuarioAutenticado.recargarDatosDesdeArchivo();
        }
        return usuarioAutenticado;
    }
    
    /**
     * Obtiene el usuario autenticado en formato de comunidad
     */
    public UsuarioComunidad getUsuarioComunidad() {
        return usuarioComunidad;
    }
    
    /**
     * Obtiene el nombre del usuario actual
     */
    public String getNombreUsuarioActual() {
        if (usuarioAutenticado != null) {
            return usuarioAutenticado.getNombre() != null && !usuarioAutenticado.getNombre().equals("null") 
                   ? usuarioAutenticado.getNombre() 
                   : usuarioAutenticado.getUsername();
        }
        return "Usuario no autenticado";
    }
    
    /**
     * Obtiene el username del usuario actual
     */
    public String getUsernameActual() {
        return usuarioAutenticado != null ? usuarioAutenticado.getUsername() : null;
    }
    
    /**
     * Convierte un Usuario a UsuarioComunidad
     */
    private UsuarioComunidad convertirAUsuarioComunidad(Usuario usuario) {
        // Determinar nombre para mostrar
        String nombreDisplay = usuario.getNombre() != null && !usuario.getNombre().equals("null") 
                              ? usuario.getNombre() 
                              : usuario.getUsername();
        
        // Determinar email
        String emailDisplay = usuario.getEmail() != null && !usuario.getEmail().equals("null") 
                             ? usuario.getEmail() 
                             : "";
        
        // Crear UsuarioComunidad con datos del usuario original
        UsuarioComunidad usuarioComunidad = new UsuarioComunidad(
            usuario.getUsername(),
            usuario.getPassword(),
            nombreDisplay,
            emailDisplay,
            usuario.getUsername(), // usar username como ID
            NivelJava.PRINCIPIANTE, // nivel por defecto
            0 // reputación inicial
        );
        
        return usuarioComunidad;
    }
    
    /**
     * Actualiza el nivel Java del usuario en la sesión
     */
    public void actualizarNivelJava(NivelJava nuevoNivel) {
        if (usuarioComunidad != null) {
            usuarioComunidad.setNivelJava(nuevoNivel);
        }
    }
    
    /**
     * Actualiza la reputación del usuario en la sesión
     */
    public void actualizarReputacion(int nuevaReputacion) {
        if (usuarioComunidad != null) {
            usuarioComunidad.setReputacion(nuevaReputacion);
        }
    }

    /**
     * Notifica que se ha creado un nuevo usuario
     * Debe llamarse después de crear un usuario nuevo
     */
    public void notificarNuevoUsuario() {
        System.out.println("🔔 Notificación: Se ha creado un nuevo usuario, recargando lista...");
        forzarRecarga();
    }

    /**
     * Obtiene el conteo actual de usuarios
     */
    public int getConteoUsuarios() {
        actualizarUsuariosDesdeArchivo();
        return usuarios != null ? usuarios.size() : 0;
    }

    /**
     * Verifica si un usuario existe por username
     */
    public boolean existeUsuario(String username) {
        if (username == null) return false;
        
        Usuario usuario = buscarUsuarioPorUsername(username);
        return usuario != null;
    }
}
