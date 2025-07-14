package EjerciciosIteractivos_Modulo;

import EjerciciosIteractivos_Modulo.Contenido.*;
import EjerciciosIteractivos_Modulo.Lecciones.Leccion;

import java.util.ArrayList;
import java.util.Scanner;

//Fernando Huilca
public class Main {
    private static Scanner scanner = new Scanner(System.in); //Input
    public static void main(String[] args) {
    //Variables para el main______________________________________
        int opcionMenuGeneral = 1; //Menu While
        //Contenido tiene muchas lecciones:
        Contenido contenidoRutaBasica = new Contenido();
        Leccion leccionTipoDatos = new Leccion("Tipos de Datos");
        Leccion leccionSintaxis = new Leccion("Sintaxis Basica");
        contenidoRutaBasica.addLeccion(leccionSintaxis);
        contenidoRutaBasica.addLeccion(leccionTipoDatos);
        //Lecciones tienen muchos ejercicios:
        String enunciado = "¿Cuál de estos es un tipo de dato entero en Java?";
        ArrayList<String> opciones = new ArrayList<>();
        opciones.add("String");
        opciones.add("boolean");
        opciones.add("double");
        opciones.add("int");
        ArrayList<String> respuestas = new ArrayList<>();
        respuestas.add("int");

        String enunciado2 = "¿Qué tipo se usa para verdadero o falso?";
        ArrayList<String> opciones2 = new ArrayList<>();
        opciones2.add("String");
        opciones2.add("boolean");
        opciones2.add("double");
        opciones2.add("int");
        ArrayList<String> respuestas2 = new ArrayList<>();
        respuestas2.add("boolean");


        Ejercicio ejercicioSeleccion = new EjercicioSeleccion(enunciado,opciones, respuestas);
        Ejercicio ejercicioSeleccion2 = new EjercicioSeleccion(enunciado2,opciones2, respuestas2);
        leccionTipoDatos.addEjercicio(ejercicioSeleccion);
        leccionTipoDatos.addEjercicio(ejercicioSeleccion2);


        //******************* Ejercicios de Completar
        String enunciado3 = "Declara una variable int llamada int con el nombre de 'edad' y un valor de 18...";
        String ejercicioSinCompletar = "int - 18";
        Estructura estructura = new Estructura(ejercicioSinCompletar, "-");

        ArrayList<String> respuesta3 = new ArrayList<>();
        respuesta3.add("edad");

        Ejercicio ejercicioCompletar = new EjercicioCompletar(enunciado3, estructura.convertirEjercicioEnPartes(), respuesta3);
        leccionTipoDatos.addEjercicio(ejercicioCompletar);

        String enunciado4 = "Completa el tipo de variable si se conoce que...";
        //Entre cada parte debe ir una plabra a completar. Tomar en cuenta ese aspecto.
        String  ejercicioSinCompletar2 =
                        "* validador = true;" +
                        "\nif(*) contador++;";
        Estructura estructura2 = new Estructura(ejercicioSinCompletar2, "*");

        ArrayList<String> respuesta4 = new ArrayList<>();
        respuesta4.add("boolean");
        respuesta4.add("validador");

        Ejercicio ejercicioCompletar2 = new EjercicioCompletar(enunciado4, estructura2.convertirEjercicioEnPartes(), respuesta4);
        leccionTipoDatos.addEjercicio(ejercicioCompletar2);
    //____________________________________________________________


        System.out.println("_______________________ Bienvenido a HelloCode _______________________");
        while (opcionMenuGeneral != 111) {
            System.out.println("_________________ Ejemplo Contenido sobre tipo de datos _________________");
            System.out.println("Seleccione la lección que desea realizar: ");
            for (int i = 0; i < contenidoRutaBasica.getLecciones().size(); i++) {
                System.out.println(i + ". Leccion " + contenidoRutaBasica.getLecciones().get(i).getNombre());
            }
            opcionMenuGeneral = scanner.nextInt();
            System.out.println("Ahora estamos dentro de la lección " + contenidoRutaBasica.getLecciones().get(opcionMenuGeneral).getNombre());
            //Metodo para mostrar los ejercicios de una leccion dada:
            for (int i = 0; i < contenidoRutaBasica.getLeccion(opcionMenuGeneral).getNumEjercicios(); i++) {

                Ejercicio ejercicio = contenidoRutaBasica.getLeccion(opcionMenuGeneral).getEjercicio(i);

                /*El problema de mostrar los ejercicios siendo estos diferentes puede usar un patron como el de estados
                 o strategy para evitar los ifs, pero a nivel o capa de back no es necesario */

                if (ejercicio instanceof EjercicioSeleccion ) {
                    logicaMostrarEjercicioSeleccion((EjercicioSeleccion) ejercicio);
                }
                if (ejercicio instanceof EjercicioCompletar ) {
                    logicaMostrarEjercicioCompletar((EjercicioCompletar) ejercicio);
                }
            }
        }
    }

    private static void logicaMostrarEjercicioCompletar(EjercicioCompletar ejercicioCompletar) {
        String espacioVacio = " __________ ";
        System.out.println("" + ejercicioCompletar.getEnunciado());
        System.out.println(" ____Ejercicio____");
        String ejercicioEspacios = String.join(espacioVacio, ejercicioCompletar.getPartesACompletar());
        Scanner scanner = new Scanner(System.in);

        System.out.println(ejercicioEspacios);
        ArrayList<String> respuestas = new ArrayList<>();
        for (int i = 0; i < ejercicioCompletar.getPartesACompletar().size() - 1; i++) {
            System.out.print("Escriba su(s) respuesta(s) No." + (i + 1) + ": ");
            //scanner.nextLine(); // 🔥 limpia el \n del buffer
            respuestas.add(scanner.nextLine());
        }
    }


    private static void logicaMostrarEjercicioSeleccion(EjercicioSeleccion ejercicioSeleccion) {
        System.out.println("" + ejercicioSeleccion.getEnunciado());
        System.out.println(" ____Opciones____");
        for (int i = 0 ; i <  ejercicioSeleccion.getListOpciones().size(); i++){
            System.out.println(i + " " + ejercicioSeleccion.getOpcion(i));
        }
        System.out.print("Escriba el numero de su respuesta: ");
        scanner.nextLine(); // 🔥 limpia el \n del buffer
        scanner.nextInt();
    }
}