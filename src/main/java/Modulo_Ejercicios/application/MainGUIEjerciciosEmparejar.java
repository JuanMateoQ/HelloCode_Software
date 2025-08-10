package Modulo_Ejercicios.application;

import javafx.application.Application;
import javafx.stage.Stage;
import Modulo_Ejercicios.DataBase.EjercicioRepository;
import Modulo_Ejercicios.logic.EjercicioEmparejar;
import Modulo_Ejercicios.logic.Respuesta;
import Modulo_Ejercicios.logic.RespuestaString;
import Modulo_Ejercicios.logic.ResultadoDeEvaluacion;
import Nuevo_Modulo_Leccion.logic.Leccion;
import Nuevo_Modulo_Leccion.controllers.LeccionUIController;

import java.util.*;

public class MainGUIEjerciciosEmparejar extends Application {

    public static void main(String[] args) {
        //ejecutarDemoConsola();
        launch(args);

    }

    private static void ejecutarDemoConsola() {
        System.out.println("=== DEMO CONSOLA: Ejercicio Emparejar ===");
        List<EjercicioEmparejar> ejercicios = EjercicioRepository.cargarEjerciciosEmparejar();
        if (ejercicios.isEmpty()) {
            System.out.println("No se encontraron ejercicios de emparejar.");
            return;
        }
        EjercicioEmparejar ejercicio = ejercicios.get(0);
        System.out.println("Instrucción: " + ejercicio.getInstruccion());

        List<String> izquierda = ejercicio.obtenerColumnaIzquierda();
        List<String> derecha = new ArrayList<>(ejercicio.obtenerColumnaDerecha());

        // Mezclamos la derecha para simular desorden si no lo está
        Collections.shuffle(derecha, new Random());

        System.out.println("Columna Izquierda:");
        for (int i = 0; i < izquierda.size(); i++) {
            System.out.println((i + 1) + ") " + izquierda.get(i));
        }

        System.out.println("Columna Derecha (desordenada):");
        for (int i = 0; i < derecha.size(); i++) {
            System.out.println((char)('A' + i) + ") " + derecha.get(i));
        }

        System.out.println();
        System.out.println("Ingresa las respuestas indicando para cada elemento de la izquierda el elemento de la derecha que corresponde.");
        System.out.println("Formato: escribe las letras de la columna derecha en el orden correcto separado por comas (ej: B,A,D,C)");

        String linea;
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Tu orden: ");
            linea = sc.nextLine().trim();
        }
        String[] tokens = linea.split(",");

        if (tokens.length != izquierda.size()) {
            System.out.println("Cantidad de respuestas distinta al número de elementos. Abortando.");
            return;
        }

        // Map letra -> valor derecha
        // Se usa este HashMap para convertir rápidamente cada letra (A, B, C, ...) digitada por el
        // usuario a la opción real de la columna derecha que se mostró desordenada. Al barajar la
        // lista 'derecha', las posiciones cambian; el mapa conserva la asociación letra-opción en
        // O(1) para validar el orden ingresado sin tener que buscar linealmente cada vez.
        Map<String, String> mapaDerecha = new HashMap<>();
        for (int i = 0; i < derecha.size(); i++) {
            mapaDerecha.put(String.valueOf((char)('A' + i)).toUpperCase(), derecha.get(i));
        }

        ArrayList<Respuesta> respuestasUsuario = new ArrayList<>();
        for (String t : tokens) {
            String clave = t.trim().toUpperCase();
            String valor = mapaDerecha.get(clave);
            if (valor == null) {
                System.out.println("Entrada inválida: " + clave);
                return;
            }
            respuestasUsuario.add(new RespuestaString(valor));
        }

        ResultadoDeEvaluacion resultado = ejercicio.evaluarRespuestas(respuestasUsuario);
        System.out.println("Resultado: " + resultado.getPorcentajeDeAcerto() + "% de acierto");
        System.out.println("Respuestas correctas (orden esperado de la derecha): " + ejercicio.obtenerRespuestasCorrectas());
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        List<EjercicioEmparejar> ejercicios = EjercicioRepository.cargarEjerciciosEmparejar();
        if (ejercicios.isEmpty()) {
            System.out.println("No se encontraron ejercicios de emparejar.");
            return;
        }
        EjercicioEmparejar ejercicio = ejercicios.get(0);
        Leccion leccion = new Leccion();
        leccion.agregarEjercicio(ejercicio);

        // Tercer parámetro es la ruta FXML final a mostrar tras terminar la lección; se deja vacío por ahora
        LeccionUIController.mostrarUnaLeccion(leccion, primaryStage, "Modulo_Usuario/views/homeUsuario.fxml");
    }
}