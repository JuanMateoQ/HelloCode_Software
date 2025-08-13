package Nuevo_Modulo_Leccion.logic;

import Modulo_Ejercicios.logic.EjercicioBase;

// Filtro concreto para filtrar por tema de lección
public class FiltroDeTemaLeccion extends FiltroEjercicios {
    private TemaLeccion tema;

    public FiltroDeTemaLeccion(TemaLeccion tema) {
        this.tema = tema;
    }

    @Override
    public boolean filtrarEjercicio(EjercicioBase ejercicio) {
        return ejercicio.getTema() == tema;
    }
}
