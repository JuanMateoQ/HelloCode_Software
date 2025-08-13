package Modulo_Ejercicios.logic;

import Nuevo_Modulo_Leccion.logic.TemaLeccion;

import java.util.ArrayList;


public class EjercicioSeleccion extends EjercicioBase {
    private ArrayList<String> opcionesDeSeleccion;

    /**
     * Constructor privado para usar con Builder
     */
    private EjercicioSeleccion(String instruccion, ArrayList<String> opcionesDeSeleccion, ArrayList<String> respuestasCorrectas,
                               NivelDificultad nivelDeDificultad, Lenguaje lenguaje, TemaLeccion temaLeccion) {
        super(instruccion, respuestasCorrectas, nivelDeDificultad, lenguaje, temaLeccion);
        this.opcionesDeSeleccion = opcionesDeSeleccion;
    }

    public ArrayList<String> getListOpciones() {
        return opcionesDeSeleccion;
    }

    public String getOpcion(int numOpcion) {
        return opcionesDeSeleccion.get(numOpcion);
    }

    /**
     * Obtiene las respuestas correctas del ejercicio
     * @return Lista de respuestas correctas
     */
    public ArrayList<String> obtenerRespuestasCorrectas() {
        return respuestasCorrectas;
    }

    public String getLenguajeEjercicio() {
        return lenguaje.name();
    }

    @Override
    public ResultadoDeEvaluacion evaluarRespuestas(ArrayList<Respuesta> respuestasUsuario) {
        // Validación básica
        if (respuestasUsuario == null || respuestasUsuario.isEmpty()) {
            return new ResultadoDeEvaluacion(0);
        }

        // Convertir a strings para comparar
        ArrayList<String> respuestasUsuarioStrings = new ArrayList<>();

        for (Respuesta respuesta : respuestasUsuario) {
            respuestasUsuarioStrings.add(respuesta.getRespuesta().toString());
        }

        if (respuestasUsuarioStrings.containsAll(respuestasCorrectas)
                && respuestasCorrectas.containsAll(respuestasUsuarioStrings)) {
            return new ResultadoDeEvaluacion(100.0);
        }

        // Conteo de aciertos: cuántas correctas fueron seleccionadas por el usuario
        int totalCorrectas = respuestasCorrectas.size();
        int aciertos = 0;

        ArrayList<String> restantes = new ArrayList<>(respuestasCorrectas);
        for (String u : respuestasUsuarioStrings) {
            int idx = restantes.indexOf(u);
            if (idx >= 0) {
                aciertos++;
                restantes.remove(idx);
            }
        }
    

        // Porcentaje basado en el máximo entre correctas y seleccionadas
        int seleccionadas = respuestasUsuarioStrings.size();
        int denominador = Math.max(totalCorrectas, seleccionadas);
        double porcentaje = denominador == 0 ? 0.0 : (aciertos * 100.0 / denominador);

        return new ResultadoDeEvaluacion(porcentaje);
    }

    /**
     * Clase Builder para EjercicioSeleccion
     * Permite construir ejercicios de selección múltiple de forma fluida
     */
    public static class Builder {
        private String instruccion;
        private ArrayList<String> opcionesDeSeleccion = new ArrayList<>();
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
         * Agrega una opción a la lista de opciones
         */
        public Builder conOpcion(String opcion) {
            this.opcionesDeSeleccion.add(opcion);
            return this;
        }

        /**
         * Agrega múltiples opciones a la lista
         */
        public Builder conOpciones(ArrayList<String> opciones) {
            this.opcionesDeSeleccion.addAll(opciones);
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
         * Construye y valida el EjercicioSeleccion
         * @return EjercicioSeleccion construido
         * @throws IllegalArgumentException si faltan parámetros requeridos
         */
        public EjercicioSeleccion construir() {
            //Validaciones
            if (instruccion == null || instruccion.trim().isEmpty()) {
                throw new IllegalArgumentException("La instrucción es requerida");
            }
            if (opcionesDeSeleccion.isEmpty()) {
                throw new IllegalArgumentException("Debe haber al menos una opción");
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

            return new EjercicioSeleccion(instruccion, opcionesDeSeleccion, respuestasCorrectas, nivelDificultad, lenguaje, temaLeccion);
        }
    }
}
