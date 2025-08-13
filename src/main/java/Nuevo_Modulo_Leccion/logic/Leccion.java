package Nuevo_Modulo_Leccion.logic;

import Modulo_Ejercicios.logic.EjercicioBase;

import java.util.ArrayList;
import java.util.List;

public class Leccion {
    //Una lección tiene o está compuesta por varios ejercicios:
    private List<EjercicioBase> listEjercicios;
    private double XPQueOtorga;
    private boolean completada;
    private TemaLeccion temaLeccion;
    private CalculoXP calculoXP;


    public Leccion() {
        listEjercicios = new ArrayList<EjercicioBase>();
        XPQueOtorga = 0; // por si no se pasa a la strategy no salte excepción
        completada = false;
        this.temaLeccion = TemaLeccion.OTRO;//inicialmente luego se cambia al correspondiente

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

    public int getXPcalculada(CalculoXP estrategiaUsada) {
        return estrategiaUsada.calcularXP();
    }



    public void setCompletada(boolean completada) {
        this.completada = completada;
    }
    public boolean getCompletada() {
        return completada;
    }
}
