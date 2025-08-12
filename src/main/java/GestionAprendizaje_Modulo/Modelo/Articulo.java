package GestionAprendizaje_Modulo.Modelo;

/**
 * Representa un recurso de aprendizaje de tipo Artículo web.
 * Hereda de RecursoAprendizaje y no añade propiedades adicionales,
 * pero proporciona su propia implementación del detalle.
 */
public class Articulo extends RecursoAprendizaje {

    /**
     * Constructor para la clase Articulo.
     * @param titulo El título del artículo.
     * @param lenguaje El lenguaje al que pertenece.
     * @param tema El tema que trata.
     * @param url El enlace a la página web del artículo.
     */
    public Articulo(String titulo, String lenguaje, String tema, String url) {
        super(titulo, lenguaje, tema, url);
    }

    /**
     * Implementación del método abstracto.
     * Para un artículo, el detalle puede ser simplemente un texto descriptivo.
     */
    @Override
    public String obtenerDetalle() {
        return "Artículo en línea";
    }
}