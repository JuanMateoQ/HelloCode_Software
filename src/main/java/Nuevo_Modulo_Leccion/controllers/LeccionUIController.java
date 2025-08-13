package Nuevo_Modulo_Leccion.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import Conexion.MetodosFrecuentes;
import Conexion.SesionManager;
import GestionAprendizaje_Modulo.Logica.AprendizajeManager;
import Modulo_Ejercicios.Controladores.EjercicioCompletarController;
import Modulo_Ejercicios.Controladores.EjercicioSeleccionController;
import Modulo_Ejercicios.logic.EjercicioBase;
import Modulo_Ejercicios.logic.EjercicioCompletarCodigo;
import Modulo_Ejercicios.logic.EjercicioEmparejar;
import Modulo_Ejercicios.logic.EjercicioSeleccion;
import Modulo_Ejercicios.logic.ResultadoDeEvaluacion;
import Nuevo_Modulo_Leccion.logic.CalculoXPBasico;
import Nuevo_Modulo_Leccion.logic.CalculoXPQuedasteSinVidas;
import Nuevo_Modulo_Leccion.logic.CalculoXPSinErrores;
import Nuevo_Modulo_Leccion.logic.Leccion;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
//Para restaurar las vidas del usuario actual:
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;



public class LeccionUIController {
    //porcentajeParaCompletarUnaLeccion
    private static int porcentajeParaCompletarUnaLeccion = 50;
    // Variables estáticas para manejar la secuencia de ejercicios
    private static Leccion leccionActual;
    private static int indiceEjercicioActual = 0;
    private static String rutaFXMLVentanaFinal; // nueva variable
    // Registro de ventanas de ejercicios abiertas para poder cerrarlas al final
    private static final List<Stage> ventanasEjercicio = new ArrayList<>();
    //Para calcular el tiempo en que se demora hacer una lección:
    private static long tiempoInicio;
    private static long tiempoFin;
    private static long tiempoTranscurridoLeccion;
    //calcular el porcentaje de acierto en la lección
    private static double  porcentajeAciertosLeccion;


    // Para calcular el porcentaje de aciertos dentro de una lección:
    /**
     * Método principal para mostrar una lección con ejercicios mixtos
     * Detecta automáticamente los tipos de ejercicios y carga la vista apropiada
     */
    public static void mostrarUnaLeccion(Leccion leccionAMostrar, Stage ventanaActual, String rutaFXML) {
        try {
            //Inicial a tomar el tiempo :
            tiempoInicio = 0; //primero lo regreso a cero
            tiempoInicio = System.currentTimeMillis(); // tome el tiempo actual
            porcentajeAciertosLeccion = 0 ;

            // Inicializar la secuencia de ejercicios
            leccionActual = leccionAMostrar;
            indiceEjercicioActual = 0;
            rutaFXMLVentanaFinal = rutaFXML;

            // Verifica si el usuario tiene al menos una vida
            if (SesionManager.getInstancia().getUsuarioAutenticado().getVidas() > 0) {
                ventanaActual.close();
                mostrarSiguienteEjercicio();
            } else {
                MetodosFrecuentes.mostrarAlerta("No tienes suficientes vidas", "Debes tener más de una vida para acceder a los ejercicios.");


            }

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

    // Permite a los controladores de ejercicios conocer la ruta final configurada por Lección
    public static String getRutaFXMLVentanaFinal() {
        return rutaFXMLVentanaFinal;
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
            // Crear la ventana del ejercicio
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            // Inyectar callback opcional: el controller puede declarar setOnResultado(Consumer<ResultadoDeEvaluacion>)
            try {
                var metodo = ctrl.getClass().getMethod("setOnResultado", Consumer.class);
                Consumer<ResultadoDeEvaluacion> onResultado = (res) -> {
                    if (res == null) return;
                    boolean fallo = res.getPorcentajeDeAcerto() < 100.0;
                    //Ingresar hasta aqui
                    porcentajeAciertosLeccion += res.getPorcentajeDeAcerto();
                    System.out.println(" EL POCENTAJE SUMA +=  ES DE : " + porcentajeAciertosLeccion);
                    if (fallo) {
                        SesionManager.getInstancia().getUsuarioAutenticado().quitarVida();
                    }
                };
                metodo.invoke(ctrl, onResultado);
            } catch (NoSuchMethodException nsme) {
            }

            // Registrar la ventana y limpiar cuando se cierre
            ventanasEjercicio.add(stage);
            stage.setOnHidden(e -> ventanasEjercicio.remove(stage));

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            MetodosFrecuentes.mostrarAlerta("Error", "Error al cargar ventana: " + e.getMessage());
        }
    }


    private static void mostrarLeccionCompletada() {
        try {
            // xp que se le asignará :
            int xp_ganada;
            //Porcentaje de aciertos totales, es decir de cada ejercicio la suma y el total
            double porcertajeAciertoTotal = porcentajeAciertosLeccion/ leccionActual.getNumEjercicios();
            //tiempo final para calcular el transcurrido:
            tiempoFin = System.currentTimeMillis();
            tiempoTranscurridoLeccion = tiempoFin - tiempoInicio;
            //Calculo en minutos y segundos:
            long segundos = (tiempoTranscurridoLeccion / 1000) % 60;
            long minutos = (tiempoTranscurridoLeccion / (1000 * 60)) % 60;

            //Definir la XP que ha ganado según algún criterio, es decir, cuál es la estrategia de cálculo:
            if (porcertajeAciertoTotal == 100){
                //Quiere decir que no tuvo errores: wow xd
                 xp_ganada = leccionActual.getXPcalculada(new CalculoXPSinErrores(tiempoTranscurridoLeccion));
            }else{
                //Fer estuvo aquí 3:)
                 xp_ganada = leccionActual.getXPcalculada(new CalculoXPBasico(tiempoTranscurridoLeccion));
            }
            // Dar al usuario actual su xp correspondiente:
            SesionManager.getInstancia().getUsuarioAutenticado().agregarXP(xp_ganada);


            // Cerrar cualquier ventana de ejercicio que quede abierta
            cerrarVentanasEjercicioAbiertas();

            FXMLLoader loader = new FXMLLoader(LeccionUIController.class.getResource("/Nuevo_Modulo_Leccion/views/ResumenLeccionCompletada.fxml"));
            Parent root = loader.load();

            //Pasar como parámetros los recursos para que se muestre en el resumen de la leccion, le paso al controlador
            ResumenLeccionController controller = loader.getController(); // obtengo el controlador
            //Marcar leccion como completada si el valor del porcentaje de aciertos total es mayor o igual al 50%
            if(porcertajeAciertoTotal >= porcentajeParaCompletarUnaLeccion){
                leccionActual.setCompletada(true);
                AprendizajeManager.getInstancia().onLeccionCompletada(SesionManager.getInstancia().getUsuarioAutenticado(), leccionActual);
            }
            //Le paso los datos jeje
            controller.inicializarDatos(minutos, segundos, xp_ganada,
                    porcertajeAciertoTotal,
                    SesionManager.getInstancia().getNombreUsuarioActual(),
                    leccionActual.getCompletada());



            Stage resumenStage = new Stage();
            resumenStage.setTitle("Resumen de la Lección");
            resumenStage.setScene(new Scene(root));
            resumenStage.setResizable(false);

            resumenStage.showAndWait();

            if (rutaFXMLVentanaFinal != null && !rutaFXMLVentanaFinal.isEmpty()) {
                String ruta = rutaFXMLVentanaFinal;
                MetodosFrecuentes.mostrarVentana(ruta, "Menú de Lecciones");
            }

        } catch (Exception e) {
            e.printStackTrace();
            MetodosFrecuentes.mostrarAlerta("Error", "Error al mostrar lección completada: " + e.getMessage());
        }
    }

    /**
     * Reemplaza la escena del ejercicio por la vista "se acabaron vidas".
     * La navegación a la ruta final se realiza desde el botón OK de esa vista.
     */
    public static void mostrarSeAcabaronVidasYVolver(Stage currentStage) {
        try {
            // se quedó sin vidas démosle 2 minutos de regeneración:
            if (SesionManager.getInstancia().getUsuarioAutenticado().getVidas() <= 0) {
                System.out.println("Iniciando regeneracion de vidas en dos minutos");
                iniciarRecuperacionVidas();
            }

            //Porcentaje de acierto total en lección:
            System.out.println(" EL PORCENTAJE DE ACIERTOS LA SUMA TOTAL ES : " + porcentajeAciertosLeccion + " EL NUMERO DE JERCICIOS EN LA LECCIONES ES:  " + leccionActual.getNumEjercicios());
            double porcertajeAciertoTotal = porcentajeAciertosLeccion/ leccionActual.getNumEjercicios();
            //tiempo final para calcular el transcurrido:
            tiempoFin = System.currentTimeMillis();
            tiempoTranscurridoLeccion = tiempoFin - tiempoInicio;
            //Calculo en minutos y segundos:
            long segundos = (tiempoTranscurridoLeccion / 1000) % 60;
            long minutos = (tiempoTranscurridoLeccion / (1000 * 60)) % 60;

            int xpCasoPerdida = leccionActual.getXPcalculada(new CalculoXPQuedasteSinVidas(tiempoTranscurridoLeccion));
            //Dar al usuario esa xp:
            SesionManager.getInstancia().getUsuarioAutenticado().agregarXP(xpCasoPerdida);

            FXMLLoader loader = new FXMLLoader(LeccionUIController.class.getResource("/Nuevo_Modulo_Leccion/views/seAcabaronVidasLeccion.fxml"));
            Parent root = loader.load();
            //Pasar como parámetros los recursos para que se muestre en el resumen de la leccion, le paso al controlador
            seAcabaronVidasLeccionController controller = loader.getController(); // obtengo el controlador
            //Marcar leccion como completada si el valor del porcentaje de aciertos total es mayor o igual al 50%
            if(porcertajeAciertoTotal >= porcentajeParaCompletarUnaLeccion){
                leccionActual.setCompletada(true);
                AprendizajeManager.getInstancia().onLeccionCompletada(SesionManager.getInstancia().getUsuarioAutenticado(), leccionActual);

            }
            //Le paso los datos jeje
            controller.inicializarDatos(minutos, segundos,
                    xpCasoPerdida,porcertajeAciertoTotal,
                    SesionManager.getInstancia().getNombreUsuarioActual(),
                    leccionActual.getCompletada());


            if (currentStage != null) {
                currentStage.setScene(new Scene(root));
                currentStage.setTitle("Sin vidas");
            }
        } catch (Exception e) {
            e.printStackTrace();
            MetodosFrecuentes.mostrarAlerta("Error", "No se pudo abrir la pantalla de fin de vidas: " + e.getMessage());
        }
    }

    private static void cerrarVentanasEjercicioAbiertas() {
        // Cierra todas las ventanas de ejercicios aún abiertas para evitar que queden detrás del resumen
        for (Stage s : new ArrayList<>(ventanasEjercicio)) {
            try {
                if (s != null && s.isShowing()) {
                    s.close();
                }
            } catch (Exception ignored) { }
        }
        ventanasEjercicio.clear();
    }


    private static void iniciarRecuperacionVidas() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.minutes(2), e -> {
                    SesionManager.getInstancia().getUsuarioAutenticado().setVidas(3);
                    System.out.println("¡Vidas restauradas a 3!");
                })
        );
        timeline.setCycleCount(1);
        timeline.play();
    }

}
