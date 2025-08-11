package Modulo_Usuario.Clases;

public enum NivelAprendizaje {
    PRINCIPIANTE("Principiante"),
    INTERMEDIO("Intermedio"),
    AVANZADO("Avanzado"),
    EXPERTO("Experto");

    private final String descripcion;

    NivelAprendizaje(String descripcion) {
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
    public static NivelAprendizaje fromDescripcion(String descripcion) {
        for (NivelAprendizaje nivel : NivelAprendizaje.values()) {
            if (nivel.getDescripcion().equals(descripcion)) {
                return nivel;
            }
        }
        throw new IllegalArgumentException("Descripción de nivel no válida: " + descripcion);
    }
} 