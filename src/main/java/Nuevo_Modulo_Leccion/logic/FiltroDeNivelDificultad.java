package Nuevo_Modulo_Leccion.logic;

import Modulo_Ejercicios.logic.EjercicioBase;
import Modulo_Ejercicios.logic.NivelDificultad;

// Filtro concreto para filtrar por nivel de dificultad
public class FiltroDeNivelDificultad extends FiltroEjercicios {
    private NivelDificultad nivel;

    public FiltroDeNivelDificultad(NivelDificultad nivel) {
        this.nivel = nivel;
    }

    @Override
    public boolean filtrarEjercicio(EjercicioBase ejercicio) {
        return ejercicio.getNivel() == nivel;
    }
}
