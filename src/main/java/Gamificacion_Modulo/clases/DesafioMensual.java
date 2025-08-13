package Gamificacion_Modulo.clases;

import java.time.LocalDateTime;
import java.util.List;

public class DesafioMensual extends Desafio {

    public DesafioMensual(Integer metaSemanal, int puntos, List<Logro> logros) {
        super(logros, puntos, metaSemanal);
        this.meta = metaSemanal;
        this.leccionesCompletadas = 0;
        this.activar();
    }

    public DesafioMensual(Desafio desafio) {
        super(desafio.logrosDisponibles, desafio.puntosRecompensa, desafio.meta);
        this.meta = desafio.meta;
        this.leccionesCompletadas = 0;
        this.activar();
    }
    @Override
    public void activar() {
        this.estaActivo = true;
        this.fechaInicio = LocalDateTime.now();
        this.fechaFin = this.fechaInicio.plusMonths(1);
    }

    // Getters
    public Integer getMetaMensual() { return this.meta; }
    public Integer getLeccionesCompletadas() {
        return leccionesCompletadas; }
}