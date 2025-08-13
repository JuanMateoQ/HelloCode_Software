package Gamificacion_Modulo.clases;

import java.time.LocalDateTime;
import java.util.List;

public class DesafioSemanal extends Desafio {

    public DesafioSemanal(Integer metaSemanal, int puntos, List<Logro> logros) {
        super(logros, puntos, metaSemanal);
        this.meta = metaSemanal;
        this.leccionesCompletadas = 0;
        this.activar();
    }
    public DesafioSemanal(Desafio desafio) {
        super(desafio.logrosDisponibles, desafio.puntosRecompensa, desafio.meta);
        this.meta = desafio.meta;
        this.leccionesCompletadas = 0;
        this.activar();
    }
    @Override
    public void activar() {
        this.estaActivo = true;
        this.fechaInicio = LocalDateTime.now();
        this.fechaFin = this.fechaInicio.plusWeeks(1);
    }

    // Getters
    public Integer getMetaSemanal() { return this.meta; }
    public Integer getLeccionesCompletadas() {
        return leccionesCompletadas; }
} 