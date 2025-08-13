package Nuevo_Modulo_Leccion.logic;

import Modulo_Ejercicios.logic.EjercicioBase;
import Modulo_Ejercicios.logic.Lenguaje;

// Filtro concreto para filtrar por lenguaje
public class FiltroLenguaje extends FiltroEjercicios {
    private Lenguaje lenguaje;

    public FiltroLenguaje(Lenguaje lenguaje) {
        this.lenguaje = lenguaje;
    }

    @Override
    public boolean filtrarEjercicio(EjercicioBase ejercicio) {
        return ejercicio.getLenguaje() == lenguaje;
    }
}
