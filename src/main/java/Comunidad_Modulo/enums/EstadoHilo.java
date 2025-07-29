package Comunidad_Modulo.enums;

public enum EstadoHilo {
    ABIERTO("Abierto"),
    RESUELTO("Resuelto"),
    CERRADO("Cerrado");
    
    private final String descripcion;
    
    EstadoHilo(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    @Override
    public String toString() {
        return descripcion;
    }
    
    /**
     * Convierte una descripción a su enum correspondiente
     */
    public static EstadoHilo fromDescripcion(String descripcion) {
        for (EstadoHilo estado : EstadoHilo.values()) {
            if (estado.getDescripcion().equals(descripcion)) {
                return estado;
            }
        }
        return ABIERTO; // Valor por defecto
    }
}