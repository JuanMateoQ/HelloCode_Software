package Gamificacion_Modulo.clases;

import java.time.LocalDateTime;
import java.util.List;

public class DesafioMensual extends Desafio {

    public DesafioMensual(Integer metaSemanal, int puntos, List<Logro> logros) {
        super(logros, puntos, metaSemanal);
        this.meta = metaSemanal;
        this.leccionesCompletadas = 0;
        activar();
    }


    @Override
    public void activar() {
        this.estaActivo = true;
        this.fechaInicio = LocalDateTime.now();
        this.fechaFin = this.fechaInicio.plusMonths(1);
    }

    @Override
    public Boolean estaCompletado() {
        return this.leccionesCompletadas >= this.meta;
    }

    public void actualizarAvance(Integer cantidad) {
        this.leccionesCompletadas += cantidad;
    }


    public Double getProgreso() {
        if (super.meta == 0) return 0.0;
        double progreso = (leccionesCompletadas * 100.0) / this.meta;
        return Math.min(progreso, 100.0); // Máximo 100%
    }

    // Getters
    public Integer getMetaMensual() { return this.meta; }
    public Integer getLeccionesCompletadas() {
        return leccionesCompletadas; }
}