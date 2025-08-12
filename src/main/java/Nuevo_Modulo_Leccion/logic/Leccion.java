package Nuevo_Modulo_Leccion.logic;

import Modulo_Ejercicios.logic.EjercicioBase;

import java.util.ArrayList;
import java.util.List;

public class Leccion {
    //Una lección tiene o está compuesta por varios ejercicios:
    private List<EjercicioBase> listEjercicios;
    private double XPGeneral;

    public Leccion() {
        listEjercicios = new ArrayList<EjercicioBase>();
        XPGeneral = 330;
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

    public void agregarEjerciciosLista(List<EjercicioBase> listaEjercicios) {
        for (EjercicioBase ejercicio : listaEjercicios) {
            agregarEjercicio(ejercicio);
        }
    }

    public double calcularXP() {
        return XPGeneral;
    }

    public double calcularXP_fallida(double porcertajeAciertoTotal) {
            return 0;
    }
}
