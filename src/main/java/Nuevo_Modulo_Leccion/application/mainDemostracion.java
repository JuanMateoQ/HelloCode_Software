package Nuevo_Modulo_Leccion.application;

import Nuevo_Modulo_Leccion.controllers.LeccionUIController;
import Nuevo_Modulo_Leccion.logic.Leccion;
import Nuevo_Modulo_Leccion.dataBase.LeccionRepository;
import javafx.stage.Stage;

import java.util.List;

public class mainDemostracion {
    public static void main(String[] args) {

        // Simulación de como otro módulo puede acoplarse con el módulo de lecciones

        // 1. Pides a lista de lecciones existentes:
        List<Leccion> listLecciones = LeccionRepository.getLecciones();
        // Por el momento:
        // 1. Todas las lecciones serán de JAVA NivelDeDificultad BÁSICA.
        // 2. Todas las lecciones contienen 3 ejercicios dentro de ellas.
        // 3. Todas las lecciones tratan sobre bucles (for, while, do-while, etc.)
        System.out.println("Cuantas lecciones te pasé : " + listLecciones.size());
        System.out.println("Cuantos ejercicios tiene cada lección: " + listLecciones.get(0).getNumEjercicios());


        //Listo ya estás acoplado, ahora queda mostrar una lección para ello les proveemos lo siguiente:

        //Llamas a una clase que tiene un metodo estático que te permite mostrar cualquier lección, en este caso mostraré
        // la lección 0 del arreglo que te mandé, pero puedes mostar cualquier lección
        LeccionUIController.mostrarUnaLeccion(listLecciones.get(0),new Stage(), "/Nuevo_Modulo_Leccion/views/ejemploDeUso.fxml");
        // ESTE MÉTODO SE TIENE QUE USAR DESDE UNA VENTANA YA ABIERTA SI NO VA A DAR ERRORES PUES JAVAFX NECESITA ESO
        // USEN EL MainGUIDemo ahi si vale correctamente.
    }

}
