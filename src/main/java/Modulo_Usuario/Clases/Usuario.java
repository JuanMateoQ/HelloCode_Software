package Modulo_Usuario.Clases;

public class Usuario extends UsuarioBase {
    private String nombre;
    private String email;
    private int xp;
    private int vidas;
    private Roles rol;
    private Object cursoActual; // Para almacenar el curso actual (usar Object para evitar dependencias)



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

    public int getVidas() {
        return vidas;
    }

    public void setVidas(int vidas) {
        this.vidas = Math.max(0, vidas); // No permitir vidas negativas
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

    // ===== MÉTODOS PÚBLICOS PARA MÓDULO DE LECCIONES =====

    /**
     * Agrega XP al usuario (por respuesta correcta)
     * @param puntos Puntos a agregar
     */
    public void agregarXP(int puntos) {
        if (puntos > 0) {
            setXp(this.xp + puntos);
            System.out.println("✅ +" + puntos + " XP | Total: " + this.xp);
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
        }
    }

    /**
     * Agrega una vida al usuario
     */
    public void agregarVida() {
        this.vidas++;
        System.out.println("💚 +1 Vida | Total: " + this.vidas);
    }

    /**
     * Quita una vida al usuario
     * @return true si aún tiene vidas, false si se quedó sin vidas
     */
    public boolean quitarVida() {
        if (this.vidas > 0) {
            this.vidas--;
            System.out.println("💔 -1 Vida | Total: " + this.vidas);
            return this.vidas > 0;
        }
        return false;
    }

    /**
     * Verifica si el usuario tiene vidas disponibles
     * @return true si tiene vidas, false si no
     */
    public boolean tieneVidas() {
        return this.vidas > 0;
    }

    /**
     * Resetea las vidas del usuario al máximo
     */
    public void resetearVidas() {
        this.vidas = 3;
        System.out.println("🔄 Vidas reseteadas a " + this.vidas);
    }

    /**
     * Resetea las vidas del usuario a un número específico
     * @param vidasNuevas Número de vidas a establecer
     */
    public void resetearVidas(int vidasNuevas) {
        this.vidas = Math.max(0, vidasNuevas);
        System.out.println("🔄 Vidas reseteadas a " + this.vidas);
    }

    /**
     * Obtiene el nivel del usuario basado en su XP
     * @return Nivel del usuario (1-10)
     */
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

    /**
     * Obtiene información del progreso del usuario
     * @return String con información de XP, nivel y vidas
     */
    public String getProgresoInfo() {
        return String.format("👤 %s | Nivel %d | XP: %d | Vidas: %d",
                nombre, getNivel(), xp, vidas);
    }

    // ===== MÉTODOS PARA GESTIÓN DE CURSOS =====

    /**
     * Establece el curso actual del usuario
     * @param curso El curso a asignar
     */
    public void setCurso(Object curso) {
        this.cursoActual = curso;
    }

    /**
     * Obtiene el curso actual del usuario
     * @return El curso actual o null si no tiene curso asignado
     */
    public Object getCurso() {
        return cursoActual;
    }

    /**
     * Verifica si el usuario tiene un curso asignado
     * @return true si tiene curso, false si no
     */
    public boolean tieneCurso() {
        return cursoActual != null;
    }

    /**
     * Remueve el curso actual del usuario
     */
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