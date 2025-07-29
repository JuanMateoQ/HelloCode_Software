package Gamificacion_Modulo.clases;

import java.time.LocalDate;

public class Logro {
    private String nombre;
    private String descripcion;
    private int puntajeUmbral;
    private final LocalDate fechaObtencion;

    public Logro(String nombre, String descripcion, int umbral) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.puntajeUmbral = umbral;
        this.fechaObtencion = LocalDate.now();
    }

    public Boolean verifarComplecion(ProgresoEstudiante progreso) {
        return progreso.getPuntosTotal() >= this.puntajeUmbral;
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public int getPuntajeUmbral() { return puntajeUmbral; }

    public int getPuntos(){

        return 300;
    }
}