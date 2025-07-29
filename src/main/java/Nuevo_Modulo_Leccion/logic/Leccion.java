package Nuevo_Modulo_Leccion.logic;

import Modulo_Ejercicios.exercises.EjercicioBase;

import java.util.ArrayList;
import java.util.List;

public class Leccion {
    //Una lección tiene o está compuesta por varios ejercicios:
    private List<EjercicioBase> listEjercicios;

    public Leccion() {
        listEjercicios = new ArrayList<EjercicioBase>();
    }

    public void agregarEjercicio(EjercicioBase nuevoEjercicio) {
        listEjercicios.add(nuevoEjercicio);
    }

    public int getNumEjercicios() {
        return listEjercicios.size();
    }

    /**
     * Obtiene la lista de ejercicios de la lección
     * @return Lista de ejercicios
     */
    public List<EjercicioBase> getListEjercicios() {
        return listEjercicios;
    }
}
