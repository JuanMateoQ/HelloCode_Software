package Modulo_Usuario.Clases;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Usuario extends UsuarioBase {
    private String nombre;
    private String email;
    private int xp;
    private int vidas;
    private Roles rol;
    private Object cursoActual; // Para almacenar el curso actual (usar Object para evitar dependencias)

    // Ruta del archivo de usuarios
    private static final String ARCHIVO_USUARIOS = "src/main/java/Modulo_Usuario/Usuarios/usuarios.txt";


    // Constructor por defecto
    public Usuario() {
        super();
        this.xp = 0;
        this.vidas = 3; // Vidas iniciales
        this.rol = Roles.USUARIO;
        this.cursoActual = null;
    }

    // Constructor con parámetros
    public Usuario(String username, String password) {
        super(username, password);
        this.xp = 0;
        this.vidas = 3; // Vidas iniciales
        this.rol = Roles.USUARIO;
        this.cursoActual = null;
    }

    // Constructor completo
    public Usuario(String username, String password, String nombre, String email) {
        super(username, password);
        this.nombre = nombre;
        this.email = email;
        this.xp = 0;
        this.vidas = 3; // Vidas iniciales
        this.rol = Roles.USUARIO;
        this.cursoActual = null;
    }

    // Constructor con XP
    public Usuario(String username, String password, String nombre, String email, int xp) {
        super(username, password);
        this.nombre = nombre;
        this.email = email;
        this.xp = xp;
        this.vidas = 3; // Vidas iniciales
        this.rol = Roles.USUARIO;
        this.cursoActual = null;
    }

    public Usuario(String username, String password, String nombre, String email, int xp, Roles rol) {
        super(username, password);
        this.nombre = nombre;
        this.email = email;
        this.xp = xp;
        this.vidas = 3; // Vidas iniciales
        this.rol = rol;
        this.cursoActual = null;
    }


    // ... otros getters y setters existentes ...


    public int getXp() {
        return xp;
    }

    /**
     * Obtiene el XP sincronizado desde el archivo
     * Usar cuando necesites los datos más actualizados
     */
    public int getXpSincronizado() {
        System.out.println("🔄 getXpSincronizado() llamado - Usuario: " + this.getUsername());
        sincronizarXpDesdeArchivo();
        System.out.println("🔄 Después de sincronizar XP - XP: " + xp);
        return xp;
    }

    public int getVidas() {
        return vidas;
    }

    /**
     * Obtiene las vidas sincronizadas desde el archivo
     * Usar cuando necesites los datos más actualizados
     */
    public int getVidasSincronizadas() {
        System.out.println("🔄 getVidasSincronizadas() llamado - Usuario: " + this.getUsername());
        sincronizarVidasDesdeArchivo();
        System.out.println("🔄 Después de sincronizar - Vidas: " + vidas);
        return vidas;
    }

    public void setVidas(int vidas) {
        this.vidas = Math.max(0, vidas); // No permitir vidas negativas
        // NO guardar cambios automáticamente para evitar bucles infinitos
    }

    /**
     * Establece las vidas Y guarda los cambios en archivo
     * Usar solo cuando se quiera persistir el cambio
     */
    public void setVidasYGuardar(int vidas) {
        setVidas(vidas);
        guardarCambiosEnArchivo();
    }

    public Roles getRol() {
        return rol;
    }

    public void setRol(Roles rol) {
        this.rol = rol;
    }

    public void setXp(int xp) {
        if (xp > 10000) {
            this.xp = 10000;
        } else {
            this.xp = Math.max(0, xp); // No permitir XP negativo
        }
    }

    /**
     * Establece el XP Y guarda los cambios en archivo
     * Usar solo cuando se quiera persistir el cambio
     */
    public void setXpYGuardar(int xp) {
        setXp(xp);
        guardarCambiosEnArchivo();
    }


    private void guardarCambiosEnArchivo() {
        try {
            File file = new File(ARCHIVO_USUARIOS);
            if (!file.exists()) {
                System.err.println("⚠️ Archivo usuarios.txt no encontrado, no se pueden guardar cambios");
                return;
            }

            // Leer todos los usuarios del archivo
            List<Usuario> todosLosUsuarios = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    linea = linea.trim();
                    if (!linea.isEmpty()) {
                        Usuario usuario = Usuario.fromString(linea);
                        if (usuario != null) {
                            // Si es el usuario actual, usar la instancia actualizada
                            if (usuario.getUsername().equals(this.getUsername())) {
                                todosLosUsuarios.add(this);
                            } else {
                                todosLosUsuarios.add(usuario);
                            }
                        }
                    }
                }
            }

            // Escribir todos los usuarios de vuelta al archivo
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
                for (Usuario usuario : todosLosUsuarios) {
                    bw.write(usuario.toString() + "\n");
                }
            }

            System.out.println("💾 Cambios guardados en archivo para usuario: " + this.getUsername());

        } catch (IOException e) {
            System.err.println("❌ Error al guardar cambios en archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void agregarXP(int puntos) {
        if (puntos > 0) {
            setXp(this.xp + puntos);
            System.out.println("✅ +" + puntos + " XP | Total: " + this.xp);
            guardarCambiosEnArchivo(); // Guardar cambios en archivo
        }
    }

    /**
     * Quita XP al usuario (por respuesta incorrecta)
     * @param puntos Puntos a quitar
     */
    public void quitarXP(int puntos) {
        if (puntos > 0) {
            setXp(this.xp - puntos);
            System.out.println("❌ -" + puntos + " XP | Total: " + this.xp);
            guardarCambiosEnArchivo(); // Guardar cambios en archivo
        }
    }


    public void agregarVida() {
        this.vidas++;
        System.out.println("💚 +1 Vida | Total: " + this.vidas);
        guardarCambiosEnArchivo(); // Guardar cambios en archivo
    }


    public boolean quitarVida() {
        if (this.vidas > 0) {
            this.vidas--;
            System.out.println("💔 -1 Vida | Total: " + this.vidas);
            guardarCambiosEnArchivo(); // Guardar cambios en archivo
            return this.vidas > 0;
        }
        return false;
    }

    public boolean tieneVidas() {
        System.out.println("❓ tieneVidas() llamado - Usuario: " + this.getUsername() + ", Vidas: " + this.vidas + ", Resultado: " + (this.vidas > 0));
        return this.vidas > 0;
    }

    public void resetearVidas() {
        this.vidas = 3;
        System.out.println("🔄 Vidas reseteadas a " + this.vidas);
        guardarCambiosEnArchivo(); // Guardar cambios en archivo
    }


    public void resetearVidas(int vidasNuevas) {
        this.vidas = Math.max(0, vidasNuevas);
        System.out.println("🔄 Vidas reseteadas a " + this.vidas);
        guardarCambiosEnArchivo(); // Guardar cambios en archivo también aquí
    }

    /**
     * Sincroniza las vidas del usuario con el archivo
     * Útil para actualizar las vidas cuando otro proceso las haya modificado
     */
    public void sincronizarVidasDesdeArchivo() {
        try {
            File file = new File(ARCHIVO_USUARIOS);
            if (!file.exists()) {
                System.err.println("⚠️ Archivo usuarios.txt no encontrado para sincronizar vidas");
                return;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    linea = linea.trim();
                    if (!linea.isEmpty()) {
                        String[] datos = linea.split(";");
                        // Verificar si es el usuario actual sin crear objeto completo
                        if (datos.length >= 6 && datos[0].equals(this.getUsername())) {
                            try {
                                int vidasArchivo = Integer.parseInt(datos[5]);
                                if (this.vidas != vidasArchivo) {
                                    System.out.println("🔄 Sincronizando vidas: " + this.vidas + " → " + vidasArchivo);
                                    this.vidas = vidasArchivo; // Asignación directa, sin setter
                                }
                                return;
                            } catch (NumberFormatException e) {
                                // Si hay error parseando, mantener valor actual
                                return;
                            }
                        }
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("❌ Error al sincronizar vidas desde archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sincroniza el XP del usuario con el archivo
     * Útil para actualizar el XP cuando otro proceso lo haya modificado
     */
    public void sincronizarXpDesdeArchivo() {
        try {
            File file = new File(ARCHIVO_USUARIOS);
            if (!file.exists()) {
                System.err.println("⚠️ Archivo usuarios.txt no encontrado para sincronizar XP");
                return;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    linea = linea.trim();
                    if (!linea.isEmpty()) {
                        String[] datos = linea.split(";");
                        // Verificar si es el usuario actual sin crear objeto completo
                        if (datos.length >= 5 && datos[0].equals(this.getUsername())) {
                            try {
                                int xpArchivo = Integer.parseInt(datos[4]);
                                if (this.xp != xpArchivo) {
                                    System.out.println("🔄 Sincronizando XP: " + this.xp + " → " + xpArchivo);
                                    this.xp = xpArchivo; // Asignación directa, sin setter
                                }
                                return;
                            } catch (NumberFormatException e) {
                                // Si hay error parseando, mantener valor actual
                                return;
                            }
                        }
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("❌ Error al sincronizar XP desde archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sincroniza tanto vidas como XP del usuario con el archivo
     * Método conveniente para sincronizar ambos valores a la vez
     */
    public void sincronizarDatosDesdeArchivo() {
        try {
            File file = new File(ARCHIVO_USUARIOS);
            if (!file.exists()) {
                System.err.println("⚠️ Archivo usuarios.txt no encontrado para sincronizar datos");
                return;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    linea = linea.trim();
                    if (!linea.isEmpty()) {
                        String[] datos = linea.split(";");
                        // Verificar si es el usuario actual sin crear objeto completo
                        if (datos.length >= 6 && datos[0].equals(this.getUsername())) {
                            try {
                                // Sincronizar XP
                                int xpArchivo = Integer.parseInt(datos[4]);
                                if (this.xp != xpArchivo) {
                                    System.out.println("🔄 Sincronizando XP: " + this.xp + " → " + xpArchivo);
                                    this.xp = xpArchivo;
                                }
                                
                                // Sincronizar vidas
                                int vidasArchivo = Integer.parseInt(datos[5]);
                                if (this.vidas != vidasArchivo) {
                                    System.out.println("🔄 Sincronizando vidas: " + this.vidas + " → " + vidasArchivo);
                                    this.vidas = vidasArchivo;
                                }
                                
                                return;
                            } catch (NumberFormatException e) {
                                // Si hay error parseando, mantener valores actuales
                                return;
                            }
                        }
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("❌ Error al sincronizar datos desde archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Recarga los datos del usuario actual desde el archivo
     * Útil para sincronizar cambios realizados por otros procesos
     */
    public void recargarDatosDesdeArchivo() {
        try {
            File file = new File(ARCHIVO_USUARIOS);
            if (!file.exists()) {
                System.err.println("⚠️ Archivo usuarios.txt no encontrado, no se pueden recargar datos");
                return;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    linea = linea.trim();
                    if (!linea.isEmpty()) {
                        Usuario usuarioTemp = Usuario.fromString(linea);
                        if (usuarioTemp != null && usuarioTemp.getUsername().equals(this.getUsername())) {
                            // Actualizar solo los datos que pueden cambiar usando asignación directa
                            // para evitar bucles infinitos
                            this.xp = usuarioTemp.getXp();
                            this.vidas = usuarioTemp.getVidas();
                            this.nombre = usuarioTemp.getNombre();
                            this.email = usuarioTemp.getEmail();
                            this.rol = usuarioTemp.getRol();
                            System.out.println("🔄 Datos recargados desde archivo para: " + this.getUsername());
                            return;
                        }
                    }
                }
            }
            
            System.err.println("⚠️ Usuario no encontrado en archivo: " + this.getUsername());
            
        } catch (IOException e) {
            System.err.println("❌ Error al recargar datos desde archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public int getNivel() {
        if (xp < 100) return 1;
        if (xp < 300) return 2;
        if (xp < 600) return 3;
        if (xp < 1000) return 4;
        if (xp < 1500) return 5;
        if (xp < 2500) return 6;
        if (xp < 4000) return 7;
        if (xp < 6000) return 8;
        if (xp < 8500) return 9;
        return 10; // Nivel máximo
    }


    public String getProgresoInfo() {
        return String.format("👤 %s | Nivel %d | XP: %d | Vidas: %d",
                nombre, getNivel(), xp, vidas);
    }


    public void setCurso(Object curso) {
        this.cursoActual = curso;
    }


    public Object getCurso() {
        return cursoActual;
    }


    public boolean tieneCurso() {
        return cursoActual != null;
    }


    public void removerCurso() {
        this.cursoActual = null;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        // Usar getters para username y password heredados de UsuarioBase
        return getUsername() + ";" + getPassword() + ";" + nombre + ";" + email + ";" + xp + ";" + vidas + ";" + rol;
    }

    // Método para crear usuario desde línea de archivo
    public static Usuario fromString(String linea) {
        String[] datos = linea.split(";");
        if (datos.length >= 2) {
            Usuario usuario = new Usuario(datos[0], datos[1]);
            if (datos.length >= 3) usuario.setNombre(datos[2]);
            if (datos.length >= 4) usuario.setEmail(datos[3]);
            if (datos.length >= 5) {
                try {
                    usuario.setXp(Integer.parseInt(datos[4]));
                } catch (NumberFormatException e) {
                    usuario.setXp(0);
                }
            }
            if (datos.length >= 6) {
                try {
                    usuario.setVidas(Integer.parseInt(datos[5]));
                } catch (NumberFormatException e) {
                    usuario.setVidas(3);
                }
            }
            if (datos.length >= 7) {
                try {
                    usuario.setRol(Roles.valueOf(datos[6]));
                    System.out.println("Usuario cargado: " + datos[0] + " con rol: " + datos[6]);
                } catch (Exception e) {
                    usuario.setRol(Roles.USUARIO);
                    System.out.println("Error parseando rol para " + datos[0] + ", asignando USUARIO por defecto");
                }
            }
            return usuario;
        }
        return null;
    }
} 