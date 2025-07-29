package Nuevo_Modulo_Leccion.controllers;

import MetodosGlobales.MetodosFrecuentes;
import Modulo_Ejercicios.Controladores.EjercicioCompletarController;
import Modulo_Ejercicios.Controladores.EjercicioSeleccionController;
import Modulo_Ejercicios.exercises.*;
import Nuevo_Modulo_Leccion.logic.Leccion;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class LeccionUIController {
    
    // Variables estáticas para manejar la secuencia de ejercicios
    private static Leccion leccionActual;
    private static int indiceEjercicioActual = 0;

    /**
     * Método principal para mostrar una lección con ejercicios mixtos
     * Detecta automáticamente los tipos de ejercicios y carga la vista apropiada
     */
    public static void mostrarUnaLeccion(Leccion leccionAMostrar, Stage ventanaActual, String rutaFXML) {

        try {
            // Cerrar la ventana actual
            if (ventanaActual != null) {
                ventanaActual.close();
            }
            
            // Crear ejercicios de prueba y agregarlos a la lección (solo para test)
            crearEjerciciosDePrueba(leccionAMostrar);
            //----------------------------------------------------
            
            // Inicializar la secuencia de ejercicios
            leccionActual = leccionAMostrar;
            indiceEjercicioActual = 0;
            
            // Mostrar el primer ejercicio
            mostrarSiguienteEjercicio();
            
        } catch (Exception e) {
            e.printStackTrace();
            MetodosFrecuentes.mostrarAlerta("Error", "Error al cargar la lección: " + e.getMessage());
        }
    }

    /**
     * Muestra el siguiente ejercicio en la secuencia
     */
    public static void mostrarSiguienteEjercicio() {
        try {
            // Verificar si hay más ejercicios
            if (leccionActual == null || indiceEjercicioActual >= leccionActual.getListEjercicios().size()) {
                mostrarLeccionCompletada();
                return;
            }
            
            // Obtener el ejercicio actual
            EjercicioBase ejercicioActual = leccionActual.getListEjercicios().get(indiceEjercicioActual);
            
            // Mostrar el ejercicio según su tipo
            if (ejercicioActual instanceof EjercicioSeleccion) {
                mostrarEjercicioSeleccion((EjercicioSeleccion) ejercicioActual);
            } else if (ejercicioActual instanceof EjercicioCompletarCodigo) {
                mostrarEjercicioCompletar((EjercicioCompletarCodigo) ejercicioActual);
            } else {
                // Ejercicio no reconocido, saltar al siguiente
                indiceEjercicioActual++;
                mostrarSiguienteEjercicio();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            MetodosFrecuentes.mostrarAlerta("Error", "Error al cargar el ejercicio: " + e.getMessage());
        }
    }

    public static void avanzarAlSiguienteEjercicio() {
        indiceEjercicioActual++;
        mostrarSiguienteEjercicio();
    }
    

    private static void mostrarEjercicioSeleccion(EjercicioSeleccion ejercicio) {
        try {
            FXMLLoader loader = new FXMLLoader(LeccionUIController.class.getResource("/Modulo_Ejercicios/views/SeleccionMultiple-view.fxml"));
            Parent root = loader.load();
            
            // Obtener el controlador y configurar el ejercicio individual
            EjercicioSeleccionController controller = loader.getController();
            controller.setEjercicio(ejercicio);
            
            // Mostrar la ventana
            Stage stage = new Stage();
            stage.setTitle("Ejercicio de Selección Múltiple " + (indiceEjercicioActual + 1));
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            MetodosFrecuentes.mostrarAlerta("Error", "Error al cargar ejercicio de selección: " + e.getMessage());
        }
    }

    /**
     * Muestra un ejercicio de completar código individual
     */
    private static void mostrarEjercicioCompletar(EjercicioCompletarCodigo ejercicio) {
        try {
            FXMLLoader loader = new FXMLLoader(LeccionUIController.class.getResource("/Modulo_Ejercicios/views/CompletarCodigo.fxml"));
            Parent root = loader.load();
            
            // Obtener el controlador y configurar el ejercicio individual
            EjercicioCompletarController controller = loader.getController();
            controller.setEjercicio(ejercicio);
            
            // Mostrar la ventana
            Stage stage = new Stage();
            stage.setTitle("Ejercicio de Completar Código " + (indiceEjercicioActual + 1));
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            MetodosFrecuentes.mostrarAlerta("Error", "Error al cargar ejercicio de completar código: " + e.getMessage());
        }
    }

    /**
     * Muestra la pantalla de lección completada
     */
    private static void mostrarLeccionCompletada() {
        try {
            MetodosFrecuentes.mostrarAlerta("¡Felicidades!", "Has completado todos los ejercicios de la lección.");
            // Aquí puedes agregar lógica adicional para mostrar estadísticas, XP ganado, etc.
            
        } catch (Exception e) {
            e.printStackTrace();
            MetodosFrecuentes.mostrarAlerta("Error", "Error al mostrar lección completada: " + e.getMessage());
        }
    }

    /**
     * Crea ejercicios de prueba mixtos (selección y completar) para testear el sistema
     */
    private static void crearEjerciciosDePrueba(Leccion leccion) {
        try {
            List<EjercicioBase> ejercicios = new ArrayList<>();
            
            // Ejercicio 1: Selección Múltiple - Conceptos básicos de Java
            EjercicioSeleccion ejercicio1 = new EjercicioSeleccion.Builder()
                .conInstruccion("¿Cuál es la palabra clave para declarar una variable constante en Java?")
                .conNivel(NivelDificultad.PRINCIPIANTE)
                .conLenguaje(Lenguaje.JAVA)
                .conRespuestaCorrecta("final")
                .conOpcion("final")
                .conOpcion("const")
                .conOpcion("static")
                .conOpcion("immutable")
                .construir();
            ejercicios.add(ejercicio1);
            
            // Ejercicio 2: Completar Código - Método simple (UNA sola respuesta)
            EjercicioCompletarCodigo ejercicio2 = new EjercicioCompletarCodigo.Builder()
                .conInstruccion("Completa el método para calcular el área de un círculo")
                .conNivel(NivelDificultad.PRINCIPIANTE)
                .conLenguaje(Lenguaje.JAVA)
                .conCodigoIncompleto("public double calcularArea(double radio) {\n    return Math.PI * _____ * _____;\n}")
                .conParteFaltante("radio")
                .conRespuestaEsperada("radio")  // Solo UNA respuesta esperada
                .construir();
            ejercicios.add(ejercicio2);
            
            // Ejercicio 3: Selección Múltiple - Estructuras de control
            EjercicioSeleccion ejercicio3 = new EjercicioSeleccion.Builder()
                .conInstruccion("¿Qué estructura de control se usa para repetir un bloque de código mientras una condición sea verdadera?")
                .conNivel(NivelDificultad.INTERMEDIO)
                .conLenguaje(Lenguaje.JAVA)
                .conRespuestaCorrecta("while")
                .conOpcion("while")
                .conOpcion("if")
                .conOpcion("switch")
                .conOpcion("try")
                .construir();
            ejercicios.add(ejercicio3);
            
            // Ejercicio 4: Completar Código - Bucle for
            EjercicioCompletarCodigo ejercicio4 = new EjercicioCompletarCodigo.Builder()
                .conInstruccion("Completa el bucle for para imprimir números del 1 al 10 (escribe solo el primer número)")
                .conNivel(NivelDificultad.INTERMEDIO)
                .conLenguaje(Lenguaje.JAVA)
                .conCodigoIncompleto("for (int i = _____; i <= 10; i++) {\n    System.out.println(i);\n}")
                .conParteFaltante("1")
                .conRespuestaEsperada("1")  // Solo UNA respuesta esperada
                .construir();
            ejercicios.add(ejercicio4);
            
            // Ejercicio 5: Selección Múltiple - Orientación a objetos
            EjercicioSeleccion ejercicio5 = new EjercicioSeleccion.Builder()
                .conInstruccion("¿Cuál es el principio de OOP que permite que una clase herede propiedades de otra?")
                .conNivel(NivelDificultad.AVANZADO)
                .conLenguaje(Lenguaje.JAVA)
                .conRespuestaCorrecta("Herencia")
                .conOpcion("Herencia")
                .conOpcion("Encapsulación")
                .conOpcion("Polimorfismo")
                .conOpcion("Abstracción")
                .construir();
            ejercicios.add(ejercicio5);
            
            // Agregar todos los ejercicios a la lección
            for (EjercicioBase ejercicio : ejercicios) {
                leccion.agregarEjercicio(ejercicio);
            }
            
            System.out.println("✅ Se crearon " + ejercicios.size() + " ejercicios de prueba para la lección");
            
        } catch (Exception e) {
            e.printStackTrace();
            MetodosFrecuentes.mostrarAlerta("Error", "Error al crear ejercicios de prueba: " + e.getMessage());
        }
    }
}
