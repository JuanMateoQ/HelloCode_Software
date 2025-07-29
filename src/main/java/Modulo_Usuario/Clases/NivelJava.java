package Modulo_Usuario.Clases;

public enum NivelJava {
    PRINCIPIANTE("Principiante"),
    INTERMEDIO("Intermedio"),
    AVANZADO("Avanzado"),
    EXPERTO("Experto");

    private final String descripcion;

    NivelJava(String descripcion) {
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
     * Convierte una descripción de vuelta al enum
     */
    public static NivelJava fromDescripcion(String descripcion) {
        for (NivelJava nivel : NivelJava.values()) {
            if (nivel.getDescripcion().equals(descripcion)) {
                return nivel;
            }
        }
        throw new IllegalArgumentException("Descripción de nivel no válida: " + descripcion);
    }
} 