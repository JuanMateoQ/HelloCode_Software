package Gamificacion_Modulo.clases;


import Conexion.SesionManager;
import Modulo_Usuario.Clases.Usuario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Desafio {

    protected Integer puntosRecompensa;
    protected LocalDateTime fechaInicio;
    protected LocalDateTime fechaFin;
    protected Boolean estaActivo;
    protected List<Logro> logrosDisponibles;
    protected int leccionesCompletadas;
    public int meta;
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
                estudiante.actualizarLogro(logro);
            }
            return true;
        }
        return false;
    }

    public void completarDesafio(ProgresoEstudiante estudiante) {
        Usuario usr = SesionManager.getInstancia().getUsuarioAutenticado();
        usr.agregarXP(puntosRecompensa);
        estudiante.aumentarDesafiosCompletados();
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

    public Double getAvanceDesafio() {
        if (this.meta == 0) return 0.0;
        double progreso = (leccionesCompletadas * 100.0) / this.meta;
        return Math.min(progreso, 100.0); // Máximo 100%
    }
    // Getters
    public Boolean getEstaActivo() { return estaActivo; }

    public Boolean estaCompletado(){
        return this.leccionesCompletadas >= this.meta;
    };


} 