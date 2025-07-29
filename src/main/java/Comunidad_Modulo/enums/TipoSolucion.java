package Comunidad_Modulo.enums;

public enum TipoSolucion {
    TEXTO("Texto"),
    IMAGEN("Imagen"),
    CODIGO("Código");
    
    private final String descripcion;
    
    TipoSolucion(String descripcion) {
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
    public static TipoSolucion fromDescripcion(String descripcion) {
        for (TipoSolucion tipo : TipoSolucion.values()) {
            if (tipo.getDescripcion().equals(descripcion)) {
                return tipo;
            }
        }
        return CODIGO; // Valor por defecto
    }
}
