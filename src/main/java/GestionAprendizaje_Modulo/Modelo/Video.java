package GestionAprendizaje_Modulo.Modelo;

/**
 * Representa un recurso de aprendizaje de tipo Video.
 * Hereda de RecursoAprendizaje y añade una propiedad específica: la duración.
 */
public class Video extends RecursoAprendizaje {

    private final int duracionEnSegundos;

    /**
     * Constructor para la clase Video.
     * @param titulo El título del video.
     * @param lenguaje El lenguaje al que pertenece (ej. "JAVA").
     * @param tema El tema que trata (ej. "Bucles").
     * @param url El enlace a la plataforma de video.
     * @param duracionEnSegundos La duración total del video en segundos.
     */
    public Video(String titulo, String lenguaje, String tema, String url, int duracionEnSegundos) {
        // Llama al constructor de la clase padre para inicializar los campos comunes.
        super(titulo, lenguaje, tema, url);
        this.duracionEnSegundos = duracionEnSegundos;
    }

    public int getDuracionEnSegundos() {
        return duracionEnSegundos;
    }

    /**
     * Implementación del método abstracto. Devuelve los detalles específicos de este video.
     */
    @Override
    public String obtenerDetalle() {
        int minutos = duracionEnSegundos / 60;
        int segundos = duracionEnSegundos % 60;
        // El formato %02d asegura que siempre haya dos dígitos (ej. "05:09").
        return String.format("Duración: %02d:%02d min", minutos, segundos);
    }
}