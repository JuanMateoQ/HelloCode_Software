package Gamificacion_Modulo.clases;

import java.time.LocalDateTime;
import java.util.List;

public abstract class Desafio {

    protected Integer puntosRecompensa;
    protected LocalDateTime fechaInicio;
    protected LocalDateTime fechaFin;
    protected Boolean estaActivo;
    protected List<Logro> logrosDisponibles;
    protected int leccionesCompletadas;
    protected int meta;


    public Desafio( List<Logro> logros, int recompensa, int meta) {
        this.logrosDisponibles = logros;
        this.puntosRecompensa = recompensa; // Puntos base por completar desafío
        this.activar();
        this.meta = meta;
    }

    public void activar() {
        this.estaActivo = true;
        this.fechaInicio = LocalDateTime.now();
    }

    public void desactivar() {
        this.estaActivo = false;
        this.fechaFin = LocalDateTime.now();
    }

    public Boolean verificarComplecion(ProgresoEstudiante estudiante) {
        return leccionesCompletadas >= meta;
    }

    public Boolean desbloquearLogro(ProgresoEstudiante estudiante) {

        if (!estudiante.getLogros().contains(this.logrosDisponibles)) {
            for(Logro logro : this.logrosDisponibles) {
                estudiante.agregarLogro(logro);
            }
            return true;
        }
        return false;
    }

    public void completarDesafio(ProgresoEstudiante estudiante) {
        // Sumar puntos de recompensa
        estudiante.sumarPuntos(puntosRecompensa);
        // Desactivar el desafío
        desactivar();
        desbloquearLogro(estudiante);
    }

    public List<Logro> getLogrosDisponibles() {
        return logrosDisponibles;
    }

    // Getters
    public Boolean getEstaActivo() { return estaActivo; }

    public abstract String definirCriterios();
    public abstract Boolean estaCompletado();


} 