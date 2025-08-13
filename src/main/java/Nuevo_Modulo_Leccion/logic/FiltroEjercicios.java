package Nuevo_Modulo_Leccion.logic;

import Modulo_Ejercicios.logic.EjercicioBase;

/**
 * Clase base para la cadena de responsabilidad de filtros de ejercicios.
 * Cada filtro puede decidir si un ejercicio pasa el filtro y delegar al siguiente.
 */
public abstract class FiltroEjercicios {
    protected FiltroEjercicios filtroSiguiente;

    public void cambiarSiguiente(FiltroEjercicios filtroSiguiente) {
        this.filtroSiguiente = filtroSiguiente;
    }

    /**
     * Método que implementa la cadena de responsabilidad.
     * Devuelve true si el ejercicio pasa este filtro y todos los siguientes.
     */
    public boolean aplicarCadena(EjercicioBase ejercicio) {
        boolean pasaEsteFiltro = filtrarEjercicio(ejercicio);
        if (!pasaEsteFiltro) return false;
        if (filtroSiguiente != null) {
            return filtroSiguiente.aplicarCadena(ejercicio);
        }
        return true;
    }

    /**
     * Cada filtro concreto debe implementar este método.
     */
    public abstract boolean filtrarEjercicio(EjercicioBase ejercicio);
}
