package Nuevo_Modulo_Leccion.dataBase;

import Modulo_Ejercicios.DataBase.EjercicioRepository;
import Modulo_Ejercicios.exercises.EjercicioBase;
import Modulo_Ejercicios.exercises.EjercicioSeleccion;
import Nuevo_Modulo_Leccion.logic.Leccion;

import java.util.ArrayList;
import java.util.List;

public class LeccionRepository {



    // Esto es momentáneo:
    public static List<Leccion> getLecciones() {
        // Lista de lecciones que voy a devolver:
        List<Leccion> lecciones = new ArrayList<>();

        //Ocupo agregar ejercicios a una lección, llamo a los ejercicios del modulo_ejercicios
        List<EjercicioBase> listEjerciciosAux = EjercicioRepository.cargarTodosLosEjercicios();
        //Creo lecciones y a ellas les agrego ejercicios_______________________________________
        Leccion leccionAux = new Leccion();
        leccionAux.agregarEjercicio(listEjerciciosAux.get(0));
        leccionAux.agregarEjercicio(listEjerciciosAux.get(1));
        leccionAux.agregarEjercicio(listEjerciciosAux.get(2));

        Leccion leccionAux2 = new Leccion();
        leccionAux2.agregarEjercicio(listEjerciciosAux.get(3));
        leccionAux2.agregarEjercicio(listEjerciciosAux.get(4));
        leccionAux2.agregarEjercicio(listEjerciciosAux.get(5));

        Leccion leccionAux3 = new Leccion();
        leccionAux3.agregarEjercicio(listEjerciciosAux.get(6));
        leccionAux3.agregarEjercicio(listEjerciciosAux.get(7));
        leccionAux3.agregarEjercicio(listEjerciciosAux.get(8));
        //_____________________________________________________________________________________
        //Agrego las lecciones a la lista:
        lecciones.add(leccionAux);
        lecciones.add(leccionAux2);
        lecciones.add(leccionAux3);

        //todas las lecciones creadas las devuelvo pa que otros la ocupen:
        return lecciones;

    }
}
