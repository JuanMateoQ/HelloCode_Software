package GestionAprendizaje_Modulo.Modelo;

/**
 * Representa un recurso de aprendizaje de tipo Documento PDF.
 * Hereda de RecursoAprendizaje y añade una propiedad específica: el número de páginas.
 */
public class DocumentoPDF extends RecursoAprendizaje {

    private final int numeroDePaginas;

    /**
     * Constructor para la clase DocumentoPDF.
     * @param titulo El título del documento.
     * @param lenguaje El lenguaje al que pertenece.
     * @param tema El tema que trata.
     * @param url El enlace de descarga del archivo PDF.
     * @param numeroDePaginas El número total de páginas.
     */
    public DocumentoPDF(String titulo, String lenguaje, String tema, String url, int numeroDePaginas) {
        super(titulo, lenguaje, tema, url);
        this.numeroDePaginas = numeroDePaginas;
    }

    public int getNumeroDePaginas() {
        return numeroDePaginas;
    }

    /**
     * Implementación del método abstracto. Devuelve los detalles específicos de este PDF.
     */
    @Override
    public String obtenerDetalle() {
        return String.format("PDF de %d páginas", numeroDePaginas);
    }
}