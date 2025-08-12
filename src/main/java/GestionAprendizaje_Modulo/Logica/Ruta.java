package GestionAprendizaje_Modulo.Logica;

import java.util.ArrayList;
import java.util.List;

/**
 * =================================================================================
 * Clase Ruta
 * =================================================================================
 * "Una secuencia ordenada de aprendizaje dentro de un Curso." - según UML.
 */
public class Ruta {

    private String id;
    private String nombre;
    private String nivel;
    private List<NodoRuta> nodos;

    public Ruta(String id, String nombre, String nivel) {
        this.id = id;
        this.nombre = nombre;
        this.nivel = nivel;
        this.nodos = new ArrayList<>(); // Inicializa la lista vacía
    }

    /**
     * Añade un nuevo nodo a la lista de nodos de esta ruta.
     * @param nodo El objeto NodoRuta que se va a añadir.
     */
    public void agregarNodo(NodoRuta nodo) {
        this.nodos.add(nodo);
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNivel() {
        return nivel;
    }

    public List<NodoRuta> getNodos() {
        return nodos;
    }
}