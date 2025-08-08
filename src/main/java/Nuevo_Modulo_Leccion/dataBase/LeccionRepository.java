package Nuevo_Modulo_Leccion.dataBase;

import Modulo_Ejercicios.DataBase.EjercicioRepository;
import Modulo_Ejercicios.logic.EjercicioBase;
import Modulo_Ejercicios.logic.Lenguaje;
import Modulo_Ejercicios.logic.NivelDificultad;
import Nuevo_Modulo_Leccion.logic.Leccion;
import Nuevo_Modulo_Leccion.logic.TemaLeccion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeccionRepository {
    static List<EjercicioBase> ejercicios = EjercicioRepository.cargarTodosLosEjercicios();



    public static List<Leccion> getListaLecciones(Lenguaje lenguaje, NivelDificultad nivelDificultad, TemaLeccion temaLeccion, int numEjercicioPorLeccion) {
        // Copia y mezcla los ejercicios
        List<EjercicioBase> copiaEjercicios = new ArrayList<>(ejercicios);
        Collections.shuffle(copiaEjercicios);

        // Filtrar los que coinciden con los parámetros
        List<EjercicioBase> auxEjercicios = new ArrayList<>();
        //System.out.println("lenguaje: " + lenguaje + "Dificultad " + nivelDificultad + "Tema " + temaLeccion);
        for (EjercicioBase ejercicio : copiaEjercicios) {
            if (ejercicio.getLenguaje() == lenguaje &&
                    ejercicio.getNivel() == nivelDificultad &&
                    ejercicio.getTema() == temaLeccion) {
                //System.out.println("ejercicio: entré aquí ");

                auxEjercicios.add(ejercicio);
            }
        }

        List<Leccion> lecciones = new ArrayList<>();
        List<EjercicioBase> ejerciciosActuales = new ArrayList<>();
        int contador = 0;
        for (int i = 0; i < auxEjercicios.size(); i++) {
            ejerciciosActuales.add(auxEjercicios.get(i));

            if (ejerciciosActuales.size() == numEjercicioPorLeccion) {
                // Crear lección con este grupo
                Leccion nuevaLeccion = new Leccion();
                lecciones.add(nuevaLeccion);
                lecciones.get(contador).agregarEjerciciosLista(ejerciciosActuales);
                contador++;
                ejerciciosActuales.clear();
            }
        }

        return lecciones;
    }







//    public static List<Leccion> getListaLecciones(NivelDificultad nivelDificultad, Lenguaje lenguaje, TemaLeccion temaLeccion, int numEjercicioPorLeccion) {
//        Collections.shuffle(ejerciciosRandom);
//
//        return null;
//    }




    // Borrar esto cuando el resto esté completo _______________________________________________________________________________________
    // Esto es momentáneo:
    public static List<Leccion> getLecciones() {
        // Lista de lecciones que voy a devolver:
        List<Leccion> lecciones = new ArrayList<>();

        //Ocupo agregar ejercicios a una lección, llamo a los ejercicios del modulo_ejercicios
        List<EjercicioBase> listEjerciciosAux = EjercicioRepository.cargarTodosLosEjercicios();
        //Creo lecciones y a ellas les agrego ejercicios_______________________________________
        Leccion leccionAux = new Leccion();
        leccionAux.agregarEjercicio(listEjerciciosAux.get(0));
        leccionAux.agregarEjercicio(listEjerciciosAux.get(103));
        leccionAux.agregarEjercicio(listEjerciciosAux.get(1));

        Leccion leccionAux2 = new Leccion();
        leccionAux2.agregarEjercicio(listEjerciciosAux.get(2));
        leccionAux2.agregarEjercicio(listEjerciciosAux.get(104));
        leccionAux2.agregarEjercicio(listEjerciciosAux.get(3));

        Leccion leccionAux3 = new Leccion();
        leccionAux3.agregarEjercicio(listEjerciciosAux.get(4));
        leccionAux3.agregarEjercicio(listEjerciciosAux.get(5));
        leccionAux3.agregarEjercicio(listEjerciciosAux.get(105));
        //_____________________________________________________________________________________
        //Agrego las lecciones a la lista:
        lecciones.add(leccionAux);
        lecciones.add(leccionAux2);
        lecciones.add(leccionAux3);

        //todas las lecciones creadas las devuelvo pa que otros la ocupen:
        return lecciones;

    }
}