package Modulo_Ejercicios.logic;

import Nuevo_Modulo_Leccion.logic.TemaLeccion;

import java.util.ArrayList;

public class EjercicioEmparejar extends EjercicioBase {
    private ArrayList<String> columnaIzquierda;
    private ArrayList<String> columnaDerecha;

    /**
     * Constructor privado para usar con Builder
     */
    private EjercicioEmparejar(String instruccion,
                               ArrayList<String> columnaIzquierda,
                               ArrayList<String> columnaDerecha,
                               ArrayList<String> respuestasCorrectas,
                               NivelDificultad nivelDificultad,
                               Lenguaje lenguaje,
                               TemaLeccion temaLeccion) {
        super(instruccion, respuestasCorrectas, nivelDificultad, lenguaje, temaLeccion);
        this.columnaIzquierda = columnaIzquierda;
        this.columnaDerecha = columnaDerecha;
    }

    /**
     * Obtiene la columna izquierda del ejercicio
     */
    public ArrayList<String> obtenerColumnaIzquierda() {
        return columnaIzquierda;
    }

    /**
     * Obtiene la columna derecha del ejercicio
     */
    public ArrayList<String> obtenerColumnaDerecha() {
        return columnaDerecha;
    }

    /**
     * Obtiene el número de parejas en el ejercicio
     */
    public int obtenerNumeroParejas() {
        return respuestasCorrectas != null ? respuestasCorrectas.size() : 0;
    }

    /**
     * Obtiene las respuestas correctas del ejercicio
     */
    public ArrayList<String> obtenerRespuestasCorrectas() {
        return respuestasCorrectas;
    }

    /**
     * Obtiene el String del Lenguaje del Ejercicio
     */

    public String getLenguajeEjercicio() {
        return lenguaje.name();
    }


    @Override
    public ResultadoDeEvaluacion evaluarRespuestas(ArrayList<Respuesta> respuestasUsuario) {
        if (respuestasUsuario == null || respuestasUsuario.isEmpty()) {
            return new ResultadoDeEvaluacion(0);
        }
        // Permitir que el usuario proporcione menos o más; se evalúa hasta el mínimo común
        int total = respuestasCorrectas.size();
        int comparables = Math.min(total, respuestasUsuario.size());
        int aciertos = 0;
        for (int i = 0; i < comparables; i++) {
            //valor verdadero o falso, float
            Object ru = respuestasUsuario.get(i).getRespuesta();
            String esperado = respuestasCorrectas.get(i);
            if (ru != null && esperado.equals(ru.toString())) {
                aciertos++;
            }
        }
        double porcentaje = total == 0 ? 0 : (aciertos * 100.0 / total);
        return new ResultadoDeEvaluacion(porcentaje);
    }

    /**
     * Clase Builder para EjercicioEmparejar
     * Permite construir ejercicios de emparejar de forma fluida
     */
    public static class Builder {
        private String instruccion;
        private ArrayList<String> columnaIzquierda = new ArrayList<>();
        private ArrayList<String> columnaDerecha = new ArrayList<>();
        private ArrayList<String> respuestasCorrectas = new ArrayList<>();
        private NivelDificultad nivelDificultad = NivelDificultad.BASICO;
        private Lenguaje lenguaje = Lenguaje.JAVA;
        private TemaLeccion temaLeccion = TemaLeccion.OTRO;

        /**
         * Establece la instrucción del ejercicio
         */
        public Builder conInstruccion(String instruccion) {
            this.instruccion = instruccion;
            return this;
        }

        /**
         * Agrega una opción a la columna izquierda
         */
        public Builder conOpcionIzquierda(String opcion) {
            this.columnaIzquierda.add(opcion);
            return this;
        }

        /**
         * Agrega múltiples opciones a la columna izquierda
         */
        public Builder conOpcionesIzquierda(ArrayList<String> opciones) {
            this.columnaIzquierda.addAll(opciones);
            return this;
        }

        /**
         * Agrega una opción a la columna derecha
         */
        public Builder conOpcionDerecha(String opcion) {
            this.columnaDerecha.add(opcion);
            return this;
        }

        /**
         * Agrega múltiples opciones a la columna derecha
         */
        public Builder conOpcionesDerecha(ArrayList<String> opciones) {
            this.columnaDerecha.addAll(opciones);
            return this;
        }

        /**
         * Agrega una respuesta correcta
         */
        public Builder conRespuestaCorrecta(String respuestaCorrecta) {
            this.respuestasCorrectas.add(respuestaCorrecta);
            return this;
        }

        /**
         * Agrega múltiples respuestas correctas
         */
        public Builder conRespuestasCorrectas(ArrayList<String> respuestasCorrectas) {
            this.respuestasCorrectas.addAll(respuestasCorrectas);
            return this;
        }

        /**
         * Establece el nivel de dificultad
         */
        public Builder conNivel(NivelDificultad nivelDificultad) {
            this.nivelDificultad = nivelDificultad;
            return this;
        }

        /**
         * Establece el lenguaje de programación
         */
        public Builder conLenguaje(Lenguaje lenguaje) {
            this.lenguaje = lenguaje;
            return this;
        }

        /**
         * Establece el tema de la lección
         */
        public Builder conTema(TemaLeccion temaLeccion) {
            this.temaLeccion = temaLeccion;
            return this;
        }

        /**
         * Construye y valida el EjercicioEmparejar
         * @return EjercicioEmparejar construido
         * @throws IllegalArgumentException si faltan parámetros requeridos
         */
        public EjercicioEmparejar construir() {
            // Validaciones
            if (instruccion == null || instruccion.trim().isEmpty()) {
                throw new IllegalArgumentException("La instrucción es requerida");
            }
            if (columnaIzquierda.isEmpty()) {
                throw new IllegalArgumentException("Debe haber al menos una opción en la columna izquierda");
            }
            if (columnaDerecha.isEmpty()) {
                throw new IllegalArgumentException("Debe haber al menos una opción en la columna derecha");
            }
            if (respuestasCorrectas.isEmpty()) {
                throw new IllegalArgumentException("Debe haber al menos una respuesta correcta");
            }
            if (nivelDificultad == null) {
                throw new IllegalArgumentException("El nivel de dificultad es requerido");
            }
            if (lenguaje == null) {
                throw new IllegalArgumentException("El lenguaje es requerido");
            }

            return new EjercicioEmparejar(instruccion, columnaIzquierda, columnaDerecha, respuestasCorrectas,
                    nivelDificultad, lenguaje, temaLeccion);
        }
    }
}
