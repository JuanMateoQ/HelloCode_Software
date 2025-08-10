package GestionAprendizaje_Modulo.Logica;

import java.util.ArrayList;
import java.util.List;

/**
 * =================================================================================
 * Clase Curso
 * =================================================================================
 * "La entidad de más alto nivel. Agrupa un conjunto de Rutas." - según UML.
 */
public class Curso {

    private String id;
    private String nombre;
    private String descripcion;
    private List<Ruta> rutas;

    public Curso(String id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.rutas = new ArrayList<>(); // Inicializa la lista vacía
    }

    /**
     * Añade una nueva ruta a la lista de rutas de este curso.
     * @param ruta La objeto Ruta que se va a añadir.
     */
    public void agregarRuta(Ruta ruta) {
        this.rutas.add(ruta);
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public List<Ruta> getRutas() {
        return rutas;
    }
}