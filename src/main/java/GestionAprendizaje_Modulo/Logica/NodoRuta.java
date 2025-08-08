//package GestionAprendizaje_Modulo.Logica;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * =================================================================================
// * Clase NodoRuta
// * =================================================================================
// * "El corazón del modelo. Es un paso individual en una Ruta. Combina una
// * Lección con materiales de apoyo." - según UML.
// */
//public class NodoRuta {
//
//    private int orden;
//    private boolean completado;
//    private Leccion leccion;
//    private List<RecursoAprendizaje> materialDeApoyo;
//
//    /**
//     * Constructor para la clase NodoRuta.
//     * @param orden El número de paso de este nodo dentro de la ruta.
//     * @param leccion La lección principal asociada a este nodo.
//     */
//    public NodoRuta(int orden, Leccion leccion) {
//        this.orden = orden;
//        this.leccion = leccion;
//        this.completado = false;
//        this.materialDeApoyo = new ArrayList<>(); // Inicializa la lista vacía
//    }
//
//    /**
//     * Añade un nuevo recurso de aprendizaje a la lista de materiales de este nodo.
//     * @param recurso El objeto RecursoAprendizaje que se va a añadir.
//     */
//    public void agregarMaterialDeApoyo(RecursoAprendizaje recurso) {
//        this.materialDeApoyo.add(recurso);
//    }
//
//    // Getters y otros métodos
//    public int getOrden() {
//        return orden;
//    }
//
//    public boolean isCompletado() {
//        return completado;
//    }
//
//    public void marcarComoCompletado() {
//        this.completado = true;
//    }
//
//    public Leccion getLeccion() {
//        return leccion;
//    }
//
//    public List<RecursoAprendizaje> getMaterialDeApoyo() {
//        return materialDeApoyo;
//    }
//}

package GestionAprendizaje_Modulo.Logica;

import java.util.ArrayList;
import java.util.List;

import GestionAprendizaje_Modulo.Modelo.RecursoAprendizaje;
import Nuevo_Modulo_Leccion.logic.Leccion;

/**
 * Representa un único paso en una Ruta. Vincula una Leccion
 * con una lista de materiales de apoyo.
 */
public class NodoRuta {

    private final int orden;
    private boolean completado;
    private final String id;
    private final Leccion leccion; // <-- Utiliza la clase Leccion correcta
    private List<RecursoAprendizaje> materialDeApoyo;

    public NodoRuta(int orden, String id, Leccion leccion) {
        this.orden = orden;
        this.id = id; // <-- Se asigna el ID
        this.leccion = leccion;
        this.materialDeApoyo = new ArrayList<>();
    }

    public void agregarMaterialDeApoyo(RecursoAprendizaje recurso) {
        this.materialDeApoyo.add(recurso);
    }

    // --- Getters y otros métodos ---
    public String getId() { return id; } // <-- GETTER AÑADIDO

    public int getOrden() { return orden; }
    public boolean isCompletado() { return completado; }
    public void marcarComoCompletado() { this.completado = true; }
    public Leccion getLeccion() { return leccion; }
    public List<RecursoAprendizaje> getMaterialDeApoyo() { return materialDeApoyo; }
}
