package Gamificacion_Modulo.clases;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Logro {
    private String nombre;
    private String descripcion;
    private int puntajeUmbral;
    private final LocalDate fechaObtencion;

    // Lista estática de logros disponibles
    private static final List<Logro> logrosDisponibles = new ArrayList<>();
    private static boolean inicializado = false;

    public Logro(String nombre, String descripcion, int umbral) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.puntajeUmbral = umbral;
        this.fechaObtencion = LocalDate.now();
    }

    public Boolean verifarComplecion(ProgresoEstudiante progreso) {
        return progreso.getPuntosTotal() >= this.puntajeUmbral;
    }

    // Método estático para obtener logros disponibles
    public static List<Logro> getLogrosDisponibles() {
        if (!inicializado) {
            inicializarLogros();
        }
        return new ArrayList<>(logrosDisponibles);
    }

    // Método para inicializar logros predeterminados
    private static void inicializarLogros() {
        if (!inicializado) {
            logrosDisponibles.clear();
            logrosDisponibles.add(new Logro("Principiante", "Completar tu primer desafio", 100));
            logrosDisponibles.add(new Logro("Dedicado", "Completar 3 desafios", 250));
            logrosDisponibles.add(new Logro("Acumulador", "Obtener 500 puntos", 150));
            logrosDisponibles.add(new Logro("Coleccionista", "Obtener 5 logros", 300));
            inicializado = true;
            System.out.println(">>> Logros predeterminados cargados: " + logrosDisponibles.size());
        }
    }

    // Método para agregar un nuevo logro
    public static void agregarLogro(Logro logro) {
        if (!inicializado) {
            inicializarLogros();
        }
        logrosDisponibles.add(logro);
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public int getPuntajeUmbral() { return puntajeUmbral; }

    public int getPuntos(){
        return 300;
    }
}