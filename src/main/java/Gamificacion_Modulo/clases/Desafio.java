package Gamificacion_Modulo.clases;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Desafio {

    protected Integer puntosRecompensa;
    protected LocalDateTime fechaInicio;
    protected LocalDateTime fechaFin;
    protected Boolean estaActivo;
    protected List<Logro> logrosDisponibles;
    protected int leccionesCompletadas;
    protected int meta;

    // Lista estática de desafíos disponibles
    private static final List<Desafio> desafiosDisponibles = new ArrayList<>();

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

    public void actualizarAvance(Integer cantidad) {
        this.leccionesCompletadas += cantidad;
    }

    public List<Logro> getLogrosDisponibles() {
        return logrosDisponibles;
    }

    // Métodos estáticos para gestionar desafíos disponibles
    public static List<Desafio> getDesafiosDisponibles() {
        return new ArrayList<>(desafiosDisponibles);
    }

    public static void agregarDesafio(Desafio desafio) {
        desafiosDisponibles.add(desafio);
    }

    public static void removerDesafio(Desafio desafio) {
        desafiosDisponibles.remove(desafio);
    }

    public static List<Desafio> getDesafiosActivos() {
        return desafiosDisponibles.stream()
                .filter(Desafio::getEstaActivo)
                .collect(Collectors.toList());
    }

    // Getters
    public Boolean getEstaActivo() { return estaActivo; }

    public abstract Boolean estaCompletado();


} 