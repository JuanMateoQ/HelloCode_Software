package GestionAprendizaje_Modulo.Modelo;

/**
 * =================================================================================
 * Clase Base Abstracta: RecursoAprendizaje
 * =================================================================================
 * Define las propiedades y comportamientos comunes a todos los tipos de recursos
 * de aprendizaje. Incluye lenguaje y tema para permitir una clasificación precisa.
 */
public abstract class RecursoAprendizaje {

    // --- Atributos Comunes ---
    // 'final' indica que una vez asignados en el constructor, no pueden cambiar.
    protected final String titulo;
    protected final String lenguaje;
    protected final String tema;
    protected final String url;

    /**
     * Constructor para la clase base. Las clases hijas llamarán a este constructor
     * usando super() para inicializar las propiedades comunes.
     * @param titulo El título descriptivo del recurso.
     * @param lenguaje El lenguaje de programación al que pertenece (ej. "JAVA").
     * @param tema El tema específico que trata (ej. "Bucles").
     * @param url La URL o enlace principal asociado al recurso.
     */
    public RecursoAprendizaje(String titulo, String lenguaje, String tema, String url) {
        this.titulo = titulo;
        this.lenguaje = lenguaje;
        this.tema = tema;
        this.url = url;
    }

    // --- Getters Comunes ---
    public String getTitulo() { return titulo; }
    public String getLenguaje() { return lenguaje; }
    public String getTema() { return tema; }
    public String getUrl() { return url; }

    /**
     * Método abstracto que obliga a cada clase hija a implementar su propia
     * forma de devolver los detalles específicos que la diferencian.
     * @return Un String con los detalles únicos del recurso (ej. duración, número de páginas).
     */
    public abstract String obtenerDetalle();

    /**
     * Devuelve una representación en texto del objeto, útil para depuración y logs.
     */
    @Override
    public String toString() {
        return String.format("[%s] %s - Tema: %s - Detalle: %s",
                this.getClass().getSimpleName(), // Obtiene el nombre de la clase hija ("Video", etc.)
                this.titulo,
                this.tema,
                this.obtenerDetalle());
    }
}