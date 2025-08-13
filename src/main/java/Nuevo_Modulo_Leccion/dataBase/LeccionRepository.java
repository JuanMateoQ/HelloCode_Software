package Nuevo_Modulo_Leccion.dataBase;

import Modulo_Ejercicios.DataBase.EjercicioRepository;
import Modulo_Ejercicios.logic.EjercicioBase;
import Modulo_Ejercicios.logic.Lenguaje;
import Modulo_Ejercicios.logic.NivelDificultad;
import Nuevo_Modulo_Leccion.logic.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeccionRepository {
    //Aplicación del patron Cadena de Responsabilidad
    private static FiltroEjercicios filtroEjercicios;


    static List<EjercicioBase> ejercicios = EjercicioRepository.cargarTodosLosEjercicios();



    /**
     * Devuelve una lista de lecciones aplicando el patrón Chain of Responsibility con filtros configurables.
     * Cada filtro recibe los parámetros necesarios en su constructor.
     * Puedes cambiar el orden de los filtros fácilmente.
     */
    public static List<Leccion> getListaLecciones(Lenguaje lenguaje, NivelDificultad nivelDificultad, TemaLeccion temaLeccion, int numEjercicioPorLeccion) {
        // 1. Crear los filtros con los parámetros deseados
        FiltroEjercicios filtroLenguaje = new FiltroLenguaje(lenguaje);
        FiltroEjercicios filtroDificultad = new FiltroDeNivelDificultad(nivelDificultad);
        FiltroEjercicios filtroTema = new FiltroDeTemaLeccion(temaLeccion);

        // 2. Configurar la cadena esto hace que sea facil cambiar el orden si se desea xd
        filtroLenguaje.cambiarSiguiente(filtroDificultad);
        filtroDificultad.cambiarSiguiente(filtroTema);

        // 3. Copiar y mezclar los ejercicios
        List<EjercicioBase> copiaEjercicios = new ArrayList<>(ejercicios);
        Collections.shuffle(copiaEjercicios);

        // 4. Filtrar usando la cadena de responsabilidad
        List<EjercicioBase> ejerciciosFiltrados = new ArrayList<>();
        for (EjercicioBase ejercicio : copiaEjercicios) {
            // aplicarCadena devuelve true si pasa todos los filtros
            if (filtroLenguaje.aplicarCadena(ejercicio)) {
                ejerciciosFiltrados.add(ejercicio);
            }
        }

        // 5. Agrupar en lecciones esto es similar a lo que se hizo en el otro metodo
        List<Leccion> lecciones = new ArrayList<>();
        List<EjercicioBase> ejerciciosActuales = new ArrayList<>();
        int contador = 0;
        for (int i = 0; i < ejerciciosFiltrados.size(); i++) {
            ejerciciosActuales.add(ejerciciosFiltrados.get(i));
            if (ejerciciosActuales.size() == numEjercicioPorLeccion) {
                Leccion nuevaLeccion = new Leccion();
                lecciones.add(nuevaLeccion);
                lecciones.get(contador).agregarEjerciciosLista(ejerciciosActuales);
                contador++;
                ejerciciosActuales.clear();
            }
        }
        return lecciones;
    }






    public static List<Leccion> getListaLecciones2(Lenguaje lenguaje, NivelDificultad nivelDificultad, TemaLeccion temaLeccion, int numEjercicioPorLeccion) {
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

}