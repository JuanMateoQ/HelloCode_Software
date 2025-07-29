package GestorEjercicios.ejemplo;

import GestorEjercicios.GestorEjerciciosEntry;
import GestorEjercicios.model.Leccion;
import GestorEjercicios.enums.TipoLeccion;
import GestorEjercicios.enums.NivelDificultad;
import GestorEjercicios.enums.LenguajeProgramacion;
import Modulo_Usuario.Clases.Usuario;
import Modulo_Usuario.Clases.UsuarioComunidad;
import Modulo_Ejercicios.exercises.EjercicioSeleccion;
import Modulo_Ejercicios.exercises.EjercicioCompletarCodigo;
import Modulo_Ejercicios.exercises.Lenguaje;

import java.util.ArrayList;
import java.util.List;

/**
 * Ejemplo de uso del módulo GestorEjercicios
 * Demuestra cómo otros módulos pueden integrar y usar el gestor de ejercicios
 */
public class EjemploUsoGestorEjercicios {
    
    public static void main(String[] args) {
        // Inicializar el módulo GestorEjercicios
        GestorEjerciciosEntry.inicializar();
        
        // Crear un usuario de ejemplo
        Usuario usuario = new UsuarioComunidad("usuario_ejemplo", "password123", "Usuario Ejemplo", "ejemplo@email.com");
        
        // Crear ejercicios de ejemplo
        List<Object> ejerciciosSeleccion = crearEjerciciosSeleccion();
        List<Object> ejerciciosCompletar = crearEjerciciosCompletarCodigo();
        
        // Crear lecciones usando el gestor
        System.out.println("\n=== Creando lecciones ===");
        
        Leccion leccionNormal = GestorEjerciciosEntry.crearLeccionNormal(
            "Fundamentos de Java", 
            ejerciciosSeleccion
        );
        
        Leccion leccionPrueba = GestorEjerciciosEntry.crearLeccionPrueba(
            "Evaluación de Sintaxis", 
            ejerciciosCompletar
        );
        
        Leccion leccionPersonalizada = GestorEjerciciosEntry.crearLeccion(
            "Lección Avanzada",
            ejerciciosSeleccion,
            TipoLeccion.NORMAL,
            25, // 25 XP
            10  // 10 puntos de conocimiento
        );

        // Crear lecciones con dificultad y lenguaje específicos
        Leccion leccionJavaAvanzado = GestorEjerciciosEntry.crearLeccionNormal(
            "Java Avanzado - POO",
            ejerciciosSeleccion,
            NivelDificultad.AVANZADO,
            LenguajeProgramacion.JAVA
        );

        Leccion leccionPythonBasico = GestorEjerciciosEntry.crearLeccionNormal(
            "Python Básico - Variables",
            ejerciciosCompletar,
            NivelDificultad.BASICO,
            LenguajeProgramacion.PYTHON
        );

        Leccion leccionJavaScriptIntermedio = GestorEjerciciosEntry.crearLeccionPrueba(
            "JavaScript Intermedio - Funciones",
            ejerciciosSeleccion,
            NivelDificultad.INTERMEDIO,
            LenguajeProgramacion.JAVASCRIPT
        );
        
        // Mostrar las lecciones creadas
        System.out.println("\n=== Lecciones creadas ===");
        List<Leccion> todasLasLecciones = GestorEjerciciosEntry.obtenerTodasLasLecciones();
        for (Leccion leccion : todasLasLecciones) {
            System.out.println(leccion.obtenerResumen());
        }

        // Mostrar información detallada de dificultad y lenguaje
        System.out.println("\n=== Información detallada de lecciones ===");
        System.out.println("Lección Java Avanzado:");
        System.out.println("  - Dificultad: " + leccionJavaAvanzado.getDificultad());
        System.out.println("  - Lenguaje: " + leccionJavaAvanzado.getLenguaje());
        System.out.println("  - Tipo: " + leccionJavaAvanzado.getTipo());
        
        System.out.println("\nLección Python Básico:");
        System.out.println("  - Dificultad: " + leccionPythonBasico.getDificultad());
        System.out.println("  - Lenguaje: " + leccionPythonBasico.getLenguaje());
        System.out.println("  - Tipo: " + leccionPythonBasico.getTipo());
        
        System.out.println("\nLección JavaScript Intermedio:");
        System.out.println("  - Dificultad: " + leccionJavaScriptIntermedio.getDificultad());
        System.out.println("  - Lenguaje: " + leccionJavaScriptIntermedio.getLenguaje());
        System.out.println("  - Tipo: " + leccionJavaScriptIntermedio.getTipo());
        
        // Simular progreso del usuario
        System.out.println("\n=== Simulando progreso del usuario ===");
        
        // Usuario completa la lección normal con 4 de 5 ejercicios correctos
        GestorEjerciciosEntry.marcarLeccionCompletada(leccionNormal, usuario, 4);
        
        // Usuario completa la lección de prueba con 3 de 3 ejercicios correctos
        GestorEjerciciosEntry.marcarLeccionCompletada(leccionPrueba, usuario, 3);
        
        // Mostrar estadísticas del usuario
        System.out.println("\n=== Estadísticas del usuario ===");
        // TODO: Implementar estadísticas de usuario
        System.out.println("Usuario: " + usuario.getUsername());
        System.out.println("Funcionalidad de estadísticas en desarrollo...");
        /*
        GestorEjercicios.IGestorEjercicios.EstadisticasUsuario estadisticas = 
            GestorEjerciciosEntry.obtenerEstadisticasUsuario(usuario);
        
        System.out.println("Experiencia total: " + estadisticas.getExperienciaTotal());
        System.out.println("Conocimiento total: " + estadisticas.getConocimientoTotal());
        System.out.println("Lecciones completadas: " + estadisticas.getLeccionesCompletadas());
        System.out.println("Ejercicios correctos: " + estadisticas.getEjerciciosCorrectos() + "/" + estadisticas.getEjerciciosTotales());
        System.out.println("Porcentaje de acierto: " + String.format("%.1f%%", estadisticas.getPorcentajeAcierto() * 100));
        */
        
        // Mostrar progreso en lecciones específicas
        System.out.println("\n=== Progreso en lecciones específicas ===");
        System.out.println("Progreso en lección normal: " + 
            String.format("%.1f%%", GestorEjerciciosEntry.obtenerProgresoUsuario(leccionNormal, usuario) * 100));
        System.out.println("Progreso en lección de prueba: " + 
            String.format("%.1f%%", GestorEjerciciosEntry.obtenerProgresoUsuario(leccionPrueba, usuario) * 100));
        System.out.println("Progreso en lección personalizada: " + 
            String.format("%.1f%%", GestorEjerciciosEntry.obtenerProgresoUsuario(leccionPersonalizada, usuario) * 100));
        
        // Mostrar estado del módulo
        System.out.println("\n=== Estado del módulo ===");
        System.out.println(GestorEjerciciosEntry.obtenerEstadoModulo());
    }
    
    /**
     * Crea ejercicios de selección múltiple de ejemplo
     */
    private static List<Object> crearEjerciciosSeleccion() {
        List<Object> ejercicios = new ArrayList<>();
        
        // Ejercicio 1: Tipos de datos
        EjercicioSeleccion ejercicio1 = new EjercicioSeleccion.Builder()
            .conInstruccion("¿Cuál de los siguientes es un tipo de dato primitivo en Java?")
            .conNivel(Modulo_Ejercicios.exercises.NivelDificultad.BASICO)
            .conLenguaje(Lenguaje.JAVA)
            .conOpcion("String")
            .conOpcion("int")
            .conOpcion("ArrayList")
            .conOpcion("Object")
            .conRespuestaCorrecta("int")
            .construir();
        
        // Ejercicio 2: Variables
        EjercicioSeleccion ejercicio2 = new EjercicioSeleccion.Builder()
            .conInstruccion("¿Cómo se declara una variable constante en Java?")
            .conNivel(Modulo_Ejercicios.exercises.NivelDificultad.BASICO)
            .conLenguaje(Lenguaje.JAVA)
            .conOpcion("var constante = 10;")
            .conOpcion("final int constante = 10;")
            .conOpcion("const int constante = 10;")
            .conOpcion("static int constante = 10;")
            .conRespuestaCorrecta("final int constante = 10;")
            .construir();
        
        ejercicios.add(ejercicio1);
        ejercicios.add(ejercicio2);
        
        return ejercicios;
    }
    
    /**
     * Crea ejercicios de completar código de ejemplo
     */
    private static List<Object> crearEjerciciosCompletarCodigo() {
        List<Object> ejercicios = new ArrayList<>();
        
        // Ejercicio 1: Completar método
        EjercicioCompletarCodigo ejercicio1 = new EjercicioCompletarCodigo.Builder()
            .conInstruccion("Completa el método para calcular la suma de dos números")
            .conCodigoIncompleto("public int sumar(int a, int b) {\n    return ____;\n}")
            .conParteFaltante("a + b")
            .conRespuestaEsperada("a + b")
            .conNivel(Modulo_Ejercicios.exercises.NivelDificultad.BASICO)
            .conLenguaje(Lenguaje.JAVA)
            .construir();
        
        // Ejercicio 2: Completar bucle
        EjercicioCompletarCodigo ejercicio2 = new EjercicioCompletarCodigo.Builder()
            .conInstruccion("Completa el bucle for para imprimir números del 1 al 5")
            .conCodigoIncompleto("for (int i = 1; i ____ 5; i++) {\n    System.out.println(i);\n}")
            .conParteFaltante("<=")
            .conRespuestaEsperada("<=")
            .conNivel(Modulo_Ejercicios.exercises.NivelDificultad.BASICO)
            .conLenguaje(Lenguaje.JAVA)
            .construir();
        
        ejercicios.add(ejercicio1);
        ejercicios.add(ejercicio2);
        
        return ejercicios;
    }
} 