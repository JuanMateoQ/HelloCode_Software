package Nuevo_Modulo_Leccion.controllers;

import MetodosGlobales.MetodosFrecuentes;
import Modulo_Ejercicios.Controladores.EjercicioCompletarController;
import Modulo_Ejercicios.Controladores.EjercicioSeleccionController;
import Modulo_Ejercicios.logic.EjercicioBase;
import Modulo_Ejercicios.logic.EjercicioCompletarCodigo;
import Modulo_Ejercicios.logic.EjercicioSeleccion;
import Modulo_Ejercicios.logic.EjercicioEmparejar;
import Nuevo_Modulo_Leccion.logic.Leccion;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.function.Consumer;

public class LeccionUIController {
    
    // Variables estáticas para manejar la secuencia de ejercicios
    private static Leccion leccionActual;
    private static int indiceEjercicioActual = 0;
    private static String rutaFXMLVentanaFinal; // nueva variable
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

            // Inicializar la secuencia de ejercicios
            leccionActual = leccionAMostrar;
            indiceEjercicioActual = 0;
            rutaFXMLVentanaFinal = rutaFXML;
            
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
            
            EjercicioBase ejercicioActual = leccionActual.getListEjercicios().get(indiceEjercicioActual);
            
            if (ejercicioActual instanceof EjercicioSeleccion) {
                mostrarEjercicioSeleccion((EjercicioSeleccion) ejercicioActual);
            } else if (ejercicioActual instanceof EjercicioCompletarCodigo) {
                mostrarEjercicioCompletar((EjercicioCompletarCodigo) ejercicioActual);
            } else if (ejercicioActual instanceof EjercicioEmparejar) {
                mostrarEjercicioEmparejar((EjercicioEmparejar) ejercicioActual);
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

    public static int getIndiceEjercicioActual() {
        return indiceEjercicioActual;
    }

    public static int getTotalEjercicios() {
        return leccionActual != null ? leccionActual.getListEjercicios().size() : 0;
    }
    

    private static void mostrarEjercicioSeleccion(EjercicioSeleccion ejercicio) {
        mostrarVentanaEjercicio(
                "/Modulo_Ejercicios/views/SeleccionMultiple-view.fxml",
                EjercicioSeleccionController.class,
                c -> c.setEjercicio(ejercicio)
        );
    }

    private static void mostrarEjercicioCompletar(EjercicioCompletarCodigo ejercicio) {
        mostrarVentanaEjercicio(
                "/Modulo_Ejercicios/views/CompletarCodigo.fxml",
                EjercicioCompletarController.class,
                c -> c.setEjercicio(ejercicio)
        );
    }

    private static void mostrarEjercicioEmparejar(EjercicioEmparejar ejercicio) {
        mostrarVentanaEjercicio(
                "/Modulo_Ejercicios/views/Emparejar.fxml",
                Modulo_Ejercicios.Controladores.EmparejarController.class,
                c -> c.setEjercicio(ejercicio)
        );
    }

    private static <T> void mostrarVentanaEjercicio(String rutaFXML, Class<T> tipoControlador, Consumer<T> inicializador) {
        try {
            FXMLLoader loader = new FXMLLoader(LeccionUIController.class.getResource(rutaFXML));
            Parent root = loader.load();
            Object ctrl = loader.getController();
            if (tipoControlador.isInstance(ctrl) && inicializador != null) {
                inicializador.accept(tipoControlador.cast(ctrl));
            }
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            MetodosFrecuentes.mostrarAlerta("Error", "Error al cargar ventana: " + e.getMessage());
        }
    }

    /**
     * Muestra la pantalla de lección completada
     */
    /*private static void mostrarLeccionCompletada() {
        try {
            MetodosFrecuentes.mostrarVentana("/Nuevo_Modulo_Leccion/views/ResumenLeccionCompletada.fxml", "Resumen");
            //MetodosFrecuentes.mostrarAlerta("¡Felicidades!", "Has completado todos los ejercicios de la lección.");
            // Abrir la ventana final que fue pasada por parámetro
            if (rutaFXMLVentanaFinal != null && !rutaFXMLVentanaFinal.isEmpty()) {
                MetodosFrecuentes.mostrarVentana(rutaFXMLVentanaFinal, "Menú de Lecciones");
            }
            // Aquí puedes agregar lógica adicional para mostrar estadísticas, XP ganado, etc.

        } catch (Exception e) {
            e.printStackTrace();
            MetodosFrecuentes.mostrarAlerta("Error", "Error al mostrar lección completada: " + e.getMessage());
        }
    }*/
    private static void mostrarLeccionCompletada() {
        try {
            FXMLLoader loader = new FXMLLoader(LeccionUIController.class.getResource("/Nuevo_Modulo_Leccion/views/ResumenLeccionCompletada.fxml"));
            Parent root = loader.load();

            Stage resumenStage = new Stage();
            resumenStage.setTitle("Resumen de la Lección");
            resumenStage.setScene(new Scene(root));
            resumenStage.setResizable(false);

            resumenStage.showAndWait();

            if (rutaFXMLVentanaFinal != null && !rutaFXMLVentanaFinal.isEmpty()) {
                MetodosFrecuentes.mostrarVentana(rutaFXMLVentanaFinal, "Menú de Lecciones");
            }

        } catch (Exception e) {
            e.printStackTrace();
            MetodosFrecuentes.mostrarAlerta("Error", "Error al mostrar lección completada: " + e.getMessage());
        }
    }



}
