package Gamificacion_Modulo.clases;

import java.util.List;

public class DesafioSemanal extends Desafio {

    public DesafioSemanal(Integer metaSemanal, int puntos, List<Logro> logros) {
        super(logros, puntos, metaSemanal);
        this.meta = metaSemanal;
        this.leccionesCompletadas = 0;
    }


    @Override
    public String definirCriterios() {
        return "";
    }

    @Override
    public Boolean estaCompletado() {
        return this.leccionesCompletadas >= super.meta;
    }

    public void actualizarAvance(Integer cantidad) {
        this.leccionesCompletadas += cantidad;
    }


    public Double getProgreso() {
        if (super.meta == 0) return 0.0;
        double progreso = (leccionesCompletadas * 100.0) / super.meta;
        return Math.min(progreso, 100.0); // Máximo 100%
    }

    // Getters
    public Integer getMetaSemanal() { return super.meta; }
    public Integer getLeccionesCompletadas() { return leccionesCompletadas; }
} 