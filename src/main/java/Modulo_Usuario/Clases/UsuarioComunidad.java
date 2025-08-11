package Modulo_Usuario.Clases;

import java.util.ArrayList;
import java.util.List;

public class UsuarioComunidad extends Usuario {
    private String idUsuario;
    private NivelAprendizaje nivelAprendizaje;
    private Integer reputacion;
    private List<Usuario> amigos;

    // Constructor por defecto
    public UsuarioComunidad() {
        super();
        this.amigos = new ArrayList<>();
        this.reputacion = 0;
        this.nivelAprendizaje = NivelAprendizaje.PRINCIPIANTE;
    }

    // Constructor con parámetros básicos
    public UsuarioComunidad(String username, String password, String nombre, String email) {
        super(username, password, nombre, email);
        this.amigos = new ArrayList<>();
        this.reputacion = 0;
        this.nivelAprendizaje = NivelAprendizaje.PRINCIPIANTE;
        this.idUsuario = username; // Usamos username como idUsuario por defecto
    }

    // Constructor completo
    public UsuarioComunidad(String username, String password, String nombre, String email,
                            String idUsuario, NivelAprendizaje nivelAprendizaje, Integer reputacion) {
        super(username, password, nombre, email);
        this.idUsuario = idUsuario;
        this.nivelAprendizaje = nivelAprendizaje;
        this.reputacion = reputacion;
        this.amigos = new ArrayList<>();
    }

    // Getters y Setters
    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public NivelAprendizaje getNivelJava() {
        // Calcular nivel basado en XP automáticamente
        return calcularNivelPorXP();
    }

    public void setNivelJava(NivelAprendizaje nivelAprendizaje) {
        this.nivelAprendizaje = nivelAprendizaje;
    }
    
    /**
     * Calcula el nivel Java basado en el XP actual del usuario
     * Rangos de XP para cada nivel:
     * - PRINCIPIANTE: 0-499 XP
     * - INTERMEDIO: 500-1499 XP  
     * - AVANZADO: 1500-3999 XP
     * - EXPERTO: 4000+ XP
     */
    private NivelAprendizaje calcularNivelPorXP() {
        int xp = getXp(); // Heredado de Usuario
        
        if (xp < 500) {
            return NivelAprendizaje.PRINCIPIANTE;
        } else if (xp < 1500) {
            return NivelAprendizaje.INTERMEDIO;
        } else if (xp < 4000) {
            return NivelAprendizaje.AVANZADO;
        } else {
            return NivelAprendizaje.EXPERTO;
        }
    }
    
    /**
     * Obtiene el nivel fijo almacenado (sin calcularlo por XP)
     * Útil para casos de compatibilidad o cuando se necesite el valor original
     */
    public NivelAprendizaje getNivelJavaFijo() {
        return nivelAprendizaje;
    }

    public Integer getReputacion() {
        return reputacion;
    }

    public void setReputacion(Integer reputacion) {
        this.reputacion = reputacion;
    }

    public List<Usuario> getAmigos() {
        return amigos;
    }

    public void setAmigos(List<Usuario> amigos) {
        this.amigos = amigos;
    }


    // Métodos para gestionar amigos
    public void agregarAmigo(Usuario amigo) {
        if (amigo != null && !amigos.contains(amigo)) {
            amigos.add(amigo);
        }
    }

    public void eliminarAmigo(Usuario amigo) {
        amigos.remove(amigo);
    }

    public void incrementarReputacion(int puntos) {
        this.reputacion += puntos;
    }

    public void decrementarReputacion(int puntos) {
        this.reputacion = Math.max(0, this.reputacion - puntos);
    }

    @Override
    public String toString() {
        return "UsuarioComunidad{" +
                "idUsuario='" + idUsuario + '\'' +
                ", nombre='" + getNombre() + '\'' +
                ", xp=" + getXp() +
                ", nivelJava=" + getNivelJava() + " (por XP)" +
                ", reputacion=" + reputacion +
                ", amigos=" + amigos.size() +
                '}';
    }
}