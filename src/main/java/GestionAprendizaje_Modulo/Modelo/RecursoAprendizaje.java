package GestionAprendizaje_Modulo.Modelo;

public class RecursoAprendizaje {
    private String titulo;
    private String url;
    private String tipo;

    public RecursoAprendizaje(String titulo, String url, String tipo) {
        this.titulo = titulo;
        this.url = url;
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getUrl() {
        return url;
    }

    public String getTipo() {
        return tipo;
    }
}
