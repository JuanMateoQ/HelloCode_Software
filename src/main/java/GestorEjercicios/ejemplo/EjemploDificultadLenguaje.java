package GestorEjercicios.ejemplo;

import GestorEjercicios.GestorEjerciciosEntry;
import GestorEjercicios.model.Leccion;
import GestorEjercicios.enums.TipoLeccion;
import GestorEjercicios.enums.NivelDificultad;
import GestorEjercicios.enums.LenguajeProgramacion;
import Modulo_Ejercicios.exercises.EjercicioSeleccion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ejemplo específico que demuestra las nuevas funcionalidades
 * de dificultad y lenguaje de programación en las lecciones
 */
public class EjemploDificultadLenguaje {
    
    public static void main(String[] args) {
        // Inicializar el módulo
        GestorEjerciciosEntry.inicializar();
        
        System.out.println("=== DEMOSTRACIÓN: Dificultad y Lenguaje de Programación ===\n");
        
        // Crear ejercicios de ejemplo con diferentes dificultades y lenguajes
        List<Object> ejerciciosJavaBasico = crearEjerciciosJavaBasico();
        List<Object> ejerciciosPythonIntermedio = crearEjerciciosPythonIntermedio();
        List<Object> ejerciciosJavaScriptAvanzado = crearEjerciciosJavaScriptAvanzado();
        List<Object> ejerciciosMixtos = crearEjerciciosMixtos();
        
        // 1. Crear lecciones con dificultad y lenguaje calculados automáticamente
        System.out.println("1. LECCIONES CON CÁLCULO AUTOMÁTICO:");
        System.out.println("=====================================");
        
        Leccion leccionAutoJava = GestorEjerciciosEntry.crearLeccionNormal(
            "Java Básico - Variables",
            ejerciciosJavaBasico
        );
        
        Leccion leccionAutoPython = GestorEjerciciosEntry.crearLeccionNormal(
            "Python Intermedio - Funciones",
            ejerciciosPythonIntermedio
        );
        
        Leccion leccionAutoJavaScript = GestorEjerciciosEntry.crearLeccionNormal(
            "JavaScript Avanzado - Clases",
            ejerciciosJavaScriptAvanzado
        );
        
        Leccion leccionAutoMixta = GestorEjerciciosEntry.crearLeccionNormal(
            "Lección Mixta - Varios Lenguajes",
            ejerciciosMixtos
        );
        
        // Mostrar información calculada automáticamente
        mostrarInformacionLeccion("Java Básico (Auto)", leccionAutoJava);
        mostrarInformacionLeccion("Python Intermedio (Auto)", leccionAutoPython);
        mostrarInformacionLeccion("JavaScript Avanzado (Auto)", leccionAutoJavaScript);
        mostrarInformacionLeccion("Lección Mixta (Auto)", leccionAutoMixta);
        
        // 2. Crear lecciones con dificultad y lenguaje específicos
        System.out.println("\n2. LECCIONES CON DIFICULTAD Y LENGUAJE ESPECÍFICOS:");
        System.out.println("==================================================");
        
        Leccion leccionJavaAvanzado = GestorEjerciciosEntry.crearLeccionNormal(
            "Java Avanzado - POO",
            ejerciciosJavaBasico, // Usamos ejercicios básicos pero especificamos avanzado
            NivelDificultad.AVANZADO,
            LenguajeProgramacion.JAVA
        );
        
        Leccion leccionPythonBasico = GestorEjerciciosEntry.crearLeccionNormal(
            "Python Básico - Variables",
            ejerciciosPythonIntermedio, // Usamos ejercicios intermedios pero especificamos básico
            NivelDificultad.BASICO,
            LenguajeProgramacion.PYTHON
        );
        
        Leccion leccionJavaScriptIntermedio = GestorEjerciciosEntry.crearLeccionPrueba(
            "JavaScript Intermedio - Funciones",
            ejerciciosJavaScriptAvanzado, // Usamos ejercicios avanzados pero especificamos intermedio
            NivelDificultad.INTERMEDIO,
            LenguajeProgramacion.JAVASCRIPT
        );
        
        // Mostrar información especificada manualmente
        mostrarInformacionLeccion("Java Avanzado (Específico)", leccionJavaAvanzado);
        mostrarInformacionLeccion("Python Básico (Específico)", leccionPythonBasico);
        mostrarInformacionLeccion("JavaScript Intermedio (Específico)", leccionJavaScriptIntermedio);
        
        // 3. Filtrar lecciones por criterios
        System.out.println("\n3. FILTRADO DE LECCIONES POR CRITERIOS:");
        System.out.println("=======================================");
        
        List<Leccion> todasLasLecciones = GestorEjerciciosEntry.obtenerTodasLasLecciones();
        
        // Filtrar por lenguaje
        List<Leccion> leccionesJava = todasLasLecciones.stream()
            .filter(l -> l.getLenguaje() == LenguajeProgramacion.JAVA)
            .collect(Collectors.toList());
        
        List<Leccion> leccionesPython = todasLasLecciones.stream()
            .filter(l -> l.getLenguaje() == LenguajeProgramacion.PYTHON)
            .collect(Collectors.toList());
        
        List<Leccion> leccionesJavaScript = todasLasLecciones.stream()
            .filter(l -> l.getLenguaje() == LenguajeProgramacion.JAVASCRIPT)
            .collect(Collectors.toList());
        
        // Filtrar por dificultad
        List<Leccion> leccionesBasicas = todasLasLecciones.stream()
            .filter(l -> l.getDificultad() == NivelDificultad.BASICO)
            .collect(Collectors.toList());
        
        List<Leccion> leccionesIntermedias = todasLasLecciones.stream()
            .filter(l -> l.getDificultad() == NivelDificultad.INTERMEDIO)
            .collect(Collectors.toList());
        
        List<Leccion> leccionesAvanzadas = todasLasLecciones.stream()
            .filter(l -> l.getDificultad() == NivelDificultad.AVANZADO)
            .collect(Collectors.toList());
        
        // Mostrar resultados del filtrado
        System.out.println("Lecciones por lenguaje:");
        System.out.println("  - Java: " + leccionesJava.size() + " lecciones");
        System.out.println("  - Python: " + leccionesPython.size() + " lecciones");
        System.out.println("  - JavaScript: " + leccionesJavaScript.size() + " lecciones");
        
        System.out.println("\nLecciones por dificultad:");
        System.out.println("  - Básico: " + leccionesBasicas.size() + " lecciones");
        System.out.println("  - Intermedio: " + leccionesIntermedias.size() + " lecciones");
        System.out.println("  - Avanzado: " + leccionesAvanzadas.size() + " lecciones");
        
        // 4. Filtros combinados
        System.out.println("\n4. FILTROS COMBINADOS:");
        System.out.println("======================");
        
        List<Leccion> leccionesJavaAvanzadas = todasLasLecciones.stream()
            .filter(l -> l.getLenguaje() == LenguajeProgramacion.JAVA)
            .filter(l -> l.getDificultad() == NivelDificultad.AVANZADO)
            .collect(Collectors.toList());
        
        List<Leccion> leccionesPythonBasicas = todasLasLecciones.stream()
            .filter(l -> l.getLenguaje() == LenguajeProgramacion.PYTHON)
            .filter(l -> l.getDificultad() == NivelDificultad.BASICO)
            .collect(Collectors.toList());
        
        System.out.println("Lecciones Java Avanzadas:");
        leccionesJavaAvanzadas.forEach(l -> System.out.println("  - " + l.obtenerResumen()));
        
        System.out.println("\nLecciones Python Básicas:");
        leccionesPythonBasicas.forEach(l -> System.out.println("  - " + l.obtenerResumen()));
        
        // 5. Estadísticas generales
        System.out.println("\n5. ESTADÍSTICAS GENERALES:");
        System.out.println("==========================");
        
        System.out.println("Total de lecciones: " + todasLasLecciones.size());
        System.out.println("Lecciones normales: " + todasLasLecciones.stream()
            .filter(l -> l.getTipo() == TipoLeccion.NORMAL).count());
        System.out.println("Lecciones de prueba: " + todasLasLecciones.stream()
            .filter(l -> l.getTipo() == TipoLeccion.PRUEBA).count());
        
        System.out.println("\nDistribución por lenguaje:");
        System.out.println("  - Java: " + todasLasLecciones.stream()
            .filter(l -> l.getLenguaje() == LenguajeProgramacion.JAVA).count());
        System.out.println("  - Python: " + todasLasLecciones.stream()
            .filter(l -> l.getLenguaje() == LenguajeProgramacion.PYTHON).count());
        System.out.println("  - JavaScript: " + todasLasLecciones.stream()
            .filter(l -> l.getLenguaje() == LenguajeProgramacion.JAVASCRIPT).count());
        
        System.out.println("\nDistribución por dificultad:");
        System.out.println("  - Básico: " + todasLasLecciones.stream()
            .filter(l -> l.getDificultad() == NivelDificultad.BASICO).count());
        System.out.println("  - Intermedio: " + todasLasLecciones.stream()
            .filter(l -> l.getDificultad() == NivelDificultad.INTERMEDIO).count());
        System.out.println("  - Avanzado: " + todasLasLecciones.stream()
            .filter(l -> l.getDificultad() == NivelDificultad.AVANZADO).count());
    }
    
    /**
     * Muestra la información completa de una lección
     */
    private static void mostrarInformacionLeccion(String titulo, Leccion leccion) {
        System.out.println(titulo + ":");
        System.out.println("  - Nombre: " + leccion.obtenerResumen());
        System.out.println("  - Tipo: " + leccion.getTipo());
        System.out.println("  - Dificultad: " + leccion.getDificultad());
        System.out.println("  - Lenguaje: " + leccion.getLenguaje());
        System.out.println("  - Ejercicios: " + leccion.getNumeroEjercicios());
        System.out.println("  - Resumen: " + leccion.obtenerResumen());
        System.out.println();
    }
    
    /**
     * Crea ejercicios de Java básico
     */
    private static List<Object> crearEjerciciosJavaBasico() {
        List<Object> ejercicios = new ArrayList<>();
        
        // Ejercicio 1: Variables en Java
        EjercicioSeleccion ej1 = new EjercicioSeleccion.Builder()
            .conInstruccion("¿Cuál es la forma correcta de declarar una variable en Java?")
            .conOpcion("var x = 5;")
            .conOpcion("int x = 5;")
            .conOpcion("variable x = 5;")
            .conOpcion("x = 5;")
            .conRespuestaCorrecta("int x = 5;")
            .conNivel(Modulo_Ejercicios.exercises.NivelDificultad.BASICO)
            .conLenguaje(Modulo_Ejercicios.exercises.Lenguaje.JAVA)
            .construir();
        ejercicios.add(ej1);
        
        // Ejercicio 2: Tipos de datos
        EjercicioSeleccion ej2 = new EjercicioSeleccion.Builder()
            .conInstruccion("¿Qué tipo de dato se usa para números enteros en Java?")
            .conOpcion("float")
            .conOpcion("int")
            .conOpcion("String")
            .conOpcion("boolean")
            .conRespuestaCorrecta("int")
            .conNivel(Modulo_Ejercicios.exercises.NivelDificultad.BASICO)
            .conLenguaje(Modulo_Ejercicios.exercises.Lenguaje.JAVA)
            .construir();
        ejercicios.add(ej2);
        
        return ejercicios;
    }
    
    /**
     * Crea ejercicios de Python intermedio
     */
    private static List<Object> crearEjerciciosPythonIntermedio() {
        List<Object> ejercicios = new ArrayList<>();
        
        // Ejercicio 1: Funciones en Python
        EjercicioSeleccion ej1 = new EjercicioSeleccion.Builder()
            .conInstruccion("¿Cuál es la sintaxis correcta para definir una función en Python?")
            .conOpcion("function nombre():")
            .conOpcion("def nombre():")
            .conOpcion("func nombre():")
            .conOpcion("define nombre():")
            .conRespuestaCorrecta("def nombre():")
            .conNivel(Modulo_Ejercicios.exercises.NivelDificultad.INTERMEDIO)
            .conLenguaje(Modulo_Ejercicios.exercises.Lenguaje.PYTHON)
            .construir();
        ejercicios.add(ej1);
        
        // Ejercicio 2: List comprehensions
        EjercicioSeleccion ej2 = new EjercicioSeleccion.Builder()
            .conInstruccion("¿Cuál es la forma correcta de crear una lista de números pares del 0 al 10?")
            .conOpcion("[x for x in range(11) if x % 2 == 0]")
            .conOpcion("[x if x % 2 == 0 for x in range(11)]")
            .conOpcion("for x in range(11): if x % 2 == 0")
            .conOpcion("list(range(0, 11, 2))")
            .conRespuestaCorrecta("[x for x in range(11) if x % 2 == 0]")
            .conNivel(Modulo_Ejercicios.exercises.NivelDificultad.INTERMEDIO)
            .conLenguaje(Modulo_Ejercicios.exercises.Lenguaje.PYTHON)
            .construir();
        ejercicios.add(ej2);
        
        return ejercicios;
    }
    
    /**
     * Crea ejercicios de JavaScript avanzado
     */
    private static List<Object> crearEjerciciosJavaScriptAvanzado() {
        List<Object> ejercicios = new ArrayList<>();
        
        // Ejercicio 1: Clases en JavaScript (usando C como alternativa)
        EjercicioSeleccion ej1 = new EjercicioSeleccion.Builder()
            .conInstruccion("¿Cuál es la sintaxis correcta para definir una clase en JavaScript ES6?")
            .conOpcion("class MiClase {}")
            .conOpcion("function MiClase() {}")
            .conOpcion("var MiClase = class {}")
            .conOpcion("object MiClase {}")
            .conRespuestaCorrecta("class MiClase {}")
            .conNivel(Modulo_Ejercicios.exercises.NivelDificultad.AVANZADO)
            .conLenguaje(Modulo_Ejercicios.exercises.Lenguaje.C)
            .construir();
        ejercicios.add(ej1);
        
        // Ejercicio 2: Promesas
        EjercicioSeleccion ej2 = new EjercicioSeleccion.Builder()
            .conInstruccion("¿Qué método se usa para manejar múltiples promesas en JavaScript?")
            .conOpcion("Promise.all()")
            .conOpcion("Promise.race()")
            .conOpcion("Promise.resolve()")
            .conOpcion("Promise.reject()")
            .conRespuestaCorrecta("Promise.all()")
            .conNivel(Modulo_Ejercicios.exercises.NivelDificultad.AVANZADO)
            .conLenguaje(Modulo_Ejercicios.exercises.Lenguaje.C)
            .construir();
        ejercicios.add(ej2);
        
        return ejercicios;
    }
    
    /**
     * Crea ejercicios mixtos con diferentes lenguajes y dificultades
     */
    private static List<Object> crearEjerciciosMixtos() {
        List<Object> ejercicios = new ArrayList<>();
        
        // Agregar ejercicios de diferentes tipos
        ejercicios.addAll(crearEjerciciosJavaBasico());
        ejercicios.addAll(crearEjerciciosPythonIntermedio());
        ejercicios.addAll(crearEjerciciosJavaScriptAvanzado());
        
        return ejercicios;
    }
} 