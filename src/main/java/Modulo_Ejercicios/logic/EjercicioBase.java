package Modulo_Ejercicios.logic;

import java.util.ArrayList;

import Nuevo_Modulo_Leccion.logic.TemaLeccion;

public abstract class EjercicioBase implements EvaluadorRespuestas {
    protected String instruccion;
    protected ArrayList<String> respuestasCorrectas;
    protected NivelDificultad nivelDificultad;
    protected Lenguaje lenguaje;
    protected TemaLeccion temaLeccion;

    public EjercicioBase(String instruccion, ArrayList<String> respuestasCorrectas,
                         NivelDificultad nivelDificultad, Lenguaje lenguaje, TemaLeccion temaLeccion) {
        this.instruccion = instruccion;
        this.respuestasCorrectas = respuestasCorrectas;
        this.nivelDificultad = nivelDificultad;
        this.lenguaje = lenguaje;
        this.temaLeccion = temaLeccion;
    }

    public String getInstruccion() {
        return instruccion;
    }
    
    public NivelDificultad getNivel() {
        return nivelDificultad;
    }
    
    public Lenguaje getLenguaje() {
        return lenguaje;
    }
    public TemaLeccion getTema() {
        return temaLeccion;
    }
}
