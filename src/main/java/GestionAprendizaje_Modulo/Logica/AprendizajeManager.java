//package GestionAprendizaje_Modulo.Logica;
//
//import GestionAprendizaje_Modulo.Logica.Curso;
//import GestionAprendizaje_Modulo.Logica.NodoRuta;
//import GestionAprendizaje_Modulo.Logica.Ruta;
//import MetodosGlobales.LeccionesCompletadas;
//import Modulo_Usuario.Clases.Usuario;
//import Nuevo_Modulo_Leccion.dataBase.LeccionRepository;
//import Nuevo_Modulo_Leccion.logic.Leccion;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
///**
// * =================================================================================
// * Gestor de Aprendizaje - Versión Final (Integrado con Gamificación)
// * =================================================================================
// * Esta clase es el cerebro operacional del módulo. Sus responsabilidades son:
// * 1. Construir la estructura de "plantilla" de Cursos y Rutas de forma segura.
// * 2. Cargar y Guardar de forma permanente el progreso de CADA usuario en un archivo.
// * 3. Proveer métodos para que la UI pueda consultar y actualizar dicho progreso.
// * 4. Notificar al módulo de Gamificación cada vez que el progreso de un usuario se actualiza.
// */
//public class AprendizajeManager {
//
//    // --- Implementación del Patrón Singleton ---
//    private static AprendizajeManager instancia;
//
//    // --- Estructuras de Datos Principales ---
//    private final List<Curso> cursosPlantilla; // La "plantilla" de cursos, igual para todos
//    private final Map<String, Map<String, Boolean>> progresoPorUsuario; // El progreso individual de cada usuario
//    private final Path RUTA_ARCHIVO_PROGRESO = Paths.get("progreso_usuarios.txt"); // "Tarjeta de memoria"
//
//    /**
//     * El constructor es PRIVADO. Al crearse la instancia, intenta cargar el progreso guardado.
//     */
//    private AprendizajeManager() {
//        this.cursosPlantilla = new ArrayList<>();
//        this.progresoPorUsuario = new HashMap<>();
//        cargarProgresoDesdeArchivo();
//    }
//
//    /**
//     * Punto de acceso único y global a la instancia del manager.
//     */
//    public static synchronized AprendizajeManager getInstancia() {
//        if (instancia == null) {
//            instancia = new AprendizajeManager();
//        }
//        return instancia;
//    }
//
//    /**
//     * Construye toda la estructura de datos. Se ejecuta una sola vez y es "a prueba de fallos".
//     */
//    public void construirDatosDePrueba() {
//        if (!cursosPlantilla.isEmpty()) {
//            System.out.println("[AprendizajeManager] La estructura ya fue construida.");
//            return;
//        }
//        System.out.println("[AprendizajeManager] Construyendo estructura de aprendizaje...");
//        List<Leccion> pozoDeLecciones;
//        try {
//            pozoDeLecciones = LeccionRepository.getLecciones();
//            if (pozoDeLecciones.isEmpty()) {
//                throw new IllegalStateException("Lista de lecciones vacía, activando plan de contingencia.");
//            }
//            System.out.println("[AprendizajeManager] ÉXITO: Se jalaron " + pozoDeLecciones.size() + " lecciones.");
//        } catch (Exception e) {
//            System.err.println("[AprendizajeManager] ERROR al jalar lecciones: " + e.getMessage());
//            System.err.println("[AprendizajeManager] PROCEDIENDO A CREAR LECCIONES DE EMERGENCIA.");
//            pozoDeLecciones = List.of(new Leccion(), new Leccion(), new Leccion());
//        }
//
//        Curso cursoDemo = new Curso("curso-java-01", "Curso de Java", "Aprende Java desde cero");
//        Ruta rutaBasica = new Ruta("ruta-java-basico", "Conceptos Básicos", "BASICO");
//        int ordenNodo = 1;
//        for (Leccion leccion : pozoDeLecciones) {
//            String nodoId = rutaBasica.getId() + "-nodo-" + ordenNodo;
//            NodoRuta nodo = new NodoRuta(ordenNodo++, nodoId, leccion);
//            rutaBasica.agregarNodo(nodo);
//        }
//        cursoDemo.agregarRuta(rutaBasica);
//        this.cursosPlantilla.add(cursoDemo);
//        System.out.println("[AprendizajeManager] Estructura creada con " + rutaBasica.getNodos().size() + " nodos.");
//    }
//
//    /**
//     * Guarda el progreso de un usuario y notifica al sistema de gamificación.
//     * @param usuario El usuario que ha completado el nodo.
//     * @param nodo El NodoRuta que ha sido completado.
//     */
//    public void marcarNodoComoCompletado(Usuario usuario, NodoRuta nodo) {
//        if (usuario == null || nodo == null) return;
//
//        String username = usuario.getUsername();
//        Map<String, Boolean> progresoDelUsuario = progresoPorUsuario.computeIfAbsent(username, k -> new HashMap<>());
//
//        if (progresoDelUsuario.getOrDefault(nodo.getId(), false)) return; // No hacer nada si ya está completado
//
//        progresoDelUsuario.put(nodo.getId(), true);
//        System.out.println("[AprendizajeManager] Progreso guardado en memoria para '" + username + "'.");
//        guardarProgresoEnArchivo();
//
//        // --- ¡INTEGRACIÓN CON GAMIFICACIÓN! ---
//        int totalCompletadas = getLeccionesCompletadasParaUsuario(usuario);
//        System.out.println("[AprendizajeManager] Notificando a Gamificación. Total completadas: " + totalCompletadas);
//        LeccionesCompletadas.set(totalCompletadas);
//    }
//
//    /**
//     * Verifica si un usuario ha completado un nodo específico (lee desde la memoria).
//     */
//    public boolean isNodoCompletadoParaUsuario(Usuario usuario, NodoRuta nodo) {
//        if (usuario == null || nodo == null) return false;
//        Map<String, Boolean> progresoDelUsuario = progresoPorUsuario.get(usuario.getUsername());
//        if (progresoDelUsuario == null) return false;
//        return progresoDelUsuario.getOrDefault(nodo.getId(), false);
//    }
//
//    /**
//     * Devuelve la plantilla de cursos para que la UI la pueda dibujar.
//     */
//    public List<Curso> getCursos() {
//        return cursosPlantilla;
//    }
//
//    /**
//     * Calcula y devuelve la cantidad total de lecciones completadas para un usuario.
//     * Es la fuente de verdad para la clase ProgresoLecciones.
//     */
//    public int getLeccionesCompletadasParaUsuario(Usuario usuario) {
//        if (usuario == null) return 0;
//        Map<String, Boolean> progresoDelUsuario = progresoPorUsuario.get(usuario.getUsername());
//        return (progresoDelUsuario == null) ? 0 : progresoDelUsuario.size();
//    }
//
//    // --- MÉTODOS DE LECTURA/ESCRITURA A ARCHIVO ---
//
//    /**
//     * Guarda el mapa de progreso completo en el archivo progreso_usuarios.txt.
//     */
//    private void guardarProgresoEnArchivo() {
//        List<String> lineas = new ArrayList<>();
//        // Formato de línea: username;id_nodo1,id_nodo2,id_nodo3
//        for (Map.Entry<String, Map<String, Boolean>> entry : progresoPorUsuario.entrySet()) {
//            String username = entry.getKey();
//            String nodosCompletados = String.join(",", entry.getValue().keySet());
//            if (!nodosCompletados.isEmpty()) {
//                lineas.add(username + ";" + nodosCompletados);
//            }
//        }
//        try {
//            Files.write(RUTA_ARCHIVO_PROGRESO, lineas);
//            System.out.println("[AprendizajeManager] Progreso guardado permanentemente en " + RUTA_ARCHIVO_PROGRESO.toAbsolutePath());
//        } catch (IOException e) {
//            System.err.println("[AprendizajeManager] ERROR: No se pudo guardar el progreso en el archivo.");
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Lee el archivo progreso_usuarios.txt y llena el mapa en memoria.
//     */
//    private void cargarProgresoDesdeArchivo() {
//        if (!Files.exists(RUTA_ARCHIVO_PROGRESO)) {
//            System.out.println("[AprendizajeManager] No se encontró archivo de progreso. Se empezará de cero.");
//            return;
//        }
//        try {
//            List<String> lineas = Files.readAllLines(RUTA_ARCHIVO_PROGRESO);
//            for (String linea : lineas) {
//                String[] partes = linea.split(";", 2);
//                if (partes.length == 2 && !partes[1].isEmpty()) {
//                    String username = partes[0];
//                    List<String> nodosCompletados = Arrays.asList(partes[1].split(","));
//                    Map<String, Boolean> progreso = nodosCompletados.stream()
//                            .collect(Collectors.toMap(id -> id, id -> true));
//                    progresoPorUsuario.put(username, progreso);
//                }
//            }
//            System.out.println("[AprendizajeManager] Se ha cargado el progreso para " + progresoPorUsuario.size() + " usuarios desde el archivo.");
//        } catch (IOException e) {
//            System.err.println("[AprendizajeManager] ERROR: No se pudo cargar el progreso desde el archivo.");
//            e.printStackTrace();
//        }
//    }
//}

package GestionAprendizaje_Modulo.Logica;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import Conexion.LeccionesCompletadas;
import Modulo_Ejercicios.logic.Lenguaje;
import Modulo_Ejercicios.logic.NivelDificultad;
import Modulo_Usuario.Clases.Usuario;
import Nuevo_Modulo_Leccion.dataBase.LeccionRepository;
import Nuevo_Modulo_Leccion.logic.Leccion;
import Nuevo_Modulo_Leccion.logic.TemaLeccion;

/**
 * =================================================================================
 * Gestor de Aprendizaje - Versión Final Unificada
 * =================================================================================
 * Esta clase es el cerebro operacional del módulo. Sus responsabilidades son:
 * 1. Construir la estructura completa de Rutas, pidiendo lecciones específicas
 *    al otro módulo para cada tema y nivel.
 * 2. Cargar y Guardar de forma permanente el progreso de CADA usuario en un archivo.
 * 3. Proveer métodos para que la UI pueda consultar y actualizar dicho progreso.
 * 4. Notificar al módulo de Gamificación cada vez que el progreso de un usuario se actualiza.
 */
public class AprendizajeManager {

    private static AprendizajeManager instancia;
    private final List<Ruta> rutasPlantilla;
    private final Map<String, Map<String, Boolean>> progresoPorUsuario;
    private final Path RUTA_ARCHIVO_PROGRESO = Paths.get("src/main/resources/GestionAprendizaje_Modulo/data/progreso_usuarios.txt");

    private AprendizajeManager() {
        this.rutasPlantilla = new ArrayList<>();
        this.progresoPorUsuario = new HashMap<>();
        cargarProgresoDesdeArchivo();
    }

    public static synchronized AprendizajeManager getInstancia() {
        if (instancia == null) {
            instancia = new AprendizajeManager();
        }
        return instancia;
    }

    /**
     * El método central que construye toda la estructura de datos.
     * Se ejecuta una sola vez al inicio de la aplicación y es "a prueba de fallos".
     */
    public void construirDatosDePrueba() {
        if (!rutasPlantilla.isEmpty()) {
            System.out.println("[AprendizajeManager] La estructura ya fue construida.");
            return;
        }
        System.out.println("[AprendizajeManager] Construyendo todas las rutas de aprendizaje...");

        try {
            // Construimos una por una las rutas que queremos en nuestra aplicación.
            construirRutaJava();
            construirRutaPython();
            construirRutaC(); // Descomenta para añadir más rutas
            construirRutaPhp();

            System.out.println("[AprendizajeManager] Construcción de " + rutasPlantilla.size() + " rutas completada.");

        } catch (Exception e) {
            System.err.println("!!! ERROR FATAL DURANTE LA CONSTRUCCIÓN DE RUTAS !!!");
            System.err.println("Posiblemente el módulo de Lecciones o Ejercicios no está funcionando correctamente.");
            e.printStackTrace();
        }
    }

    /**
     * Lógica específica para construir la ruta de Java con sus 3 niveles y 3 temas.
     */
    private void construirRutaJava() {
        System.out.println(" -> Construyendo Ruta de Java...");
        Ruta rutaJava = new Ruta("ruta-java", "Java", "JAVA");
        int ordenGlobalNodo = 1;

        // --- Nivel Básico ---
        List<Leccion> leccionesBasicas = LeccionRepository.getListaLecciones(Lenguaje.JAVA, NivelDificultad.BASICO, TemaLeccion.CONTROL_DE_FLUJO, 3);
        for (Leccion leccion : leccionesBasicas) {
            String nodoId = rutaJava.getId() + "-nodo-" + ordenGlobalNodo;
            rutaJava.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesBasicas1 = LeccionRepository.getListaLecciones(Lenguaje.JAVA, NivelDificultad.BASICO, TemaLeccion.CLASES_Y_ESTRUCTURAS, 3);
        for (Leccion leccion : leccionesBasicas1) {
            String nodoId = rutaJava.getId() + "-nodo-" + ordenGlobalNodo;
            rutaJava.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesBasicas2 = LeccionRepository.getListaLecciones(Lenguaje.JAVA, NivelDificultad.BASICO, TemaLeccion.TIPOS_Y_OPERADORES, 3);
        for (Leccion leccion : leccionesBasicas2) {
            String nodoId = rutaJava.getId() + "-nodo-" + ordenGlobalNodo;
            rutaJava.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesBasicas3 = LeccionRepository.getListaLecciones(Lenguaje.JAVA, NivelDificultad.BASICO, TemaLeccion.FUNCIONES_Y_METODOS, 3);
        for (Leccion leccion : leccionesBasicas3) {
            String nodoId = rutaJava.getId() + "-nodo-" + ordenGlobalNodo;
            rutaJava.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesBasicas4 = LeccionRepository.getListaLecciones(Lenguaje.JAVA, NivelDificultad.BASICO, TemaLeccion.OTRO, 3);
        for (Leccion leccion : leccionesBasicas4) {
            String nodoId = rutaJava.getId() + "-nodo-" + ordenGlobalNodo;
            rutaJava.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesBasicas5 = LeccionRepository.getListaLecciones(Lenguaje.JAVA, NivelDificultad.BASICO, TemaLeccion.ENTRADA_Y_SALIDA, 3);
        for (Leccion leccion : leccionesBasicas5) {
            String nodoId = rutaJava.getId() + "-nodo-" + ordenGlobalNodo;
            rutaJava.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }

        // --- Nivel Intermedio ---
        List<Leccion> leccionesIntermedias = LeccionRepository.getListaLecciones(Lenguaje.JAVA, NivelDificultad.INTERMEDIO, TemaLeccion.CONTROL_DE_FLUJO, 3);
        for (Leccion leccion : leccionesIntermedias) {
            String nodoId = rutaJava.getId() + "-nodo-" + ordenGlobalNodo;
            rutaJava.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesIntermedias1 = LeccionRepository.getListaLecciones(Lenguaje.JAVA, NivelDificultad.INTERMEDIO, TemaLeccion.CLASES_Y_ESTRUCTURAS, 3);
        for (Leccion leccion : leccionesIntermedias1) {
            String nodoId = rutaJava.getId() + "-nodo-" + ordenGlobalNodo;
            rutaJava.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesIntermedias2 = LeccionRepository.getListaLecciones(Lenguaje.JAVA, NivelDificultad.INTERMEDIO, TemaLeccion.TIPOS_Y_OPERADORES, 3);
        for (Leccion leccion : leccionesIntermedias2) {
            String nodoId = rutaJava.getId() + "-nodo-" + ordenGlobalNodo;
            rutaJava.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesIntermedias3 = LeccionRepository.getListaLecciones(Lenguaje.JAVA, NivelDificultad.INTERMEDIO, TemaLeccion.FUNCIONES_Y_METODOS, 3);
        for (Leccion leccion : leccionesIntermedias3) {
            String nodoId = rutaJava.getId() + "-nodo-" + ordenGlobalNodo;
            rutaJava.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesIntermedias4 = LeccionRepository.getListaLecciones(Lenguaje.JAVA, NivelDificultad.INTERMEDIO, TemaLeccion.OTRO, 3);
        for (Leccion leccion : leccionesIntermedias4) {
            String nodoId = rutaJava.getId() + "-nodo-" + ordenGlobalNodo;
            rutaJava.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesIntermedias5 = LeccionRepository.getListaLecciones(Lenguaje.JAVA, NivelDificultad.INTERMEDIO, TemaLeccion.ENTRADA_Y_SALIDA, 3);
        for (Leccion leccion : leccionesIntermedias5) {
            String nodoId = rutaJava.getId() + "-nodo-" + ordenGlobalNodo;
            rutaJava.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }

        // --- Nivel Avanzado ---
        List<Leccion> leccionesAvanzadas = LeccionRepository.getListaLecciones(Lenguaje.JAVA, NivelDificultad.AVANZADO, TemaLeccion.CONTROL_DE_FLUJO, 3);
        for (Leccion leccion : leccionesAvanzadas) {
            String nodoId = rutaJava.getId() + "-nodo-" + ordenGlobalNodo;
            rutaJava.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesAvanzadas1 = LeccionRepository.getListaLecciones(Lenguaje.JAVA, NivelDificultad.AVANZADO, TemaLeccion.CLASES_Y_ESTRUCTURAS, 3);
        for (Leccion leccion : leccionesAvanzadas1) {
            String nodoId = rutaJava.getId() + "-nodo-" + ordenGlobalNodo;
            rutaJava.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesAvanzadas2 = LeccionRepository.getListaLecciones(Lenguaje.JAVA, NivelDificultad.AVANZADO, TemaLeccion.TIPOS_Y_OPERADORES, 3);
        for (Leccion leccion : leccionesAvanzadas2) {
            String nodoId = rutaJava.getId() + "-nodo-" + ordenGlobalNodo;
            rutaJava.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesAvanzadas3 = LeccionRepository.getListaLecciones(Lenguaje.JAVA, NivelDificultad.AVANZADO, TemaLeccion.FUNCIONES_Y_METODOS, 3);
        for (Leccion leccion : leccionesAvanzadas3) {
            String nodoId = rutaJava.getId() + "-nodo-" + ordenGlobalNodo;
            rutaJava.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesAvanzadas4 = LeccionRepository.getListaLecciones(Lenguaje.JAVA, NivelDificultad.AVANZADO, TemaLeccion.OTRO, 3);
        for (Leccion leccion : leccionesAvanzadas4) {
            String nodoId = rutaJava.getId() + "-nodo-" + ordenGlobalNodo;
            rutaJava.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesAvanzadas5 = LeccionRepository.getListaLecciones(Lenguaje.JAVA, NivelDificultad.AVANZADO, TemaLeccion.ENTRADA_Y_SALIDA, 3);
        for (Leccion leccion : leccionesAvanzadas5) {
            String nodoId = rutaJava.getId() + "-nodo-" + ordenGlobalNodo;
            rutaJava.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }

        if (!rutaJava.getNodos().isEmpty()) {
            this.rutasPlantilla.add(rutaJava);
            System.out.println("    -> Ruta de Java finalizada con " + rutaJava.getNodos().size() + " nodos.");
        } else {
            System.err.println("    -> No se pudieron cargar lecciones para la Ruta de Java.");
        }
    }

    /**
     * Lógica específica para construir la ruta de Python.
     */
    private void construirRutaPython() {
        System.out.println(" -> Construyendo Ruta de Python...");
        Ruta rutaPython = new Ruta("ruta-python", "Python", "PYTHON");
        int ordenGlobalNodo = 1;
        // --- Nivel Básico ---
        List<Leccion> leccionesBasicas = LeccionRepository.getListaLecciones(Lenguaje.PYTHON, NivelDificultad.BASICO, TemaLeccion.CONTROL_DE_FLUJO, 3);
        for (Leccion leccion : leccionesBasicas) {
            String nodoId = rutaPython.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPython.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesBasicas1 = LeccionRepository.getListaLecciones(Lenguaje.PYTHON, NivelDificultad.BASICO, TemaLeccion.CLASES_Y_ESTRUCTURAS, 3);
        for (Leccion leccion : leccionesBasicas1) {
            String nodoId = rutaPython.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPython.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesBasicas2 = LeccionRepository.getListaLecciones(Lenguaje.PYTHON, NivelDificultad.BASICO, TemaLeccion.TIPOS_Y_OPERADORES, 3);
        for (Leccion leccion : leccionesBasicas2) {
            String nodoId = rutaPython.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPython.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesBasicas3 = LeccionRepository.getListaLecciones(Lenguaje.PYTHON, NivelDificultad.BASICO, TemaLeccion.FUNCIONES_Y_METODOS, 3);
        for (Leccion leccion : leccionesBasicas3) {
            String nodoId = rutaPython.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPython.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesBasicas4 = LeccionRepository.getListaLecciones(Lenguaje.PYTHON, NivelDificultad.BASICO, TemaLeccion.OTRO, 3);
        for (Leccion leccion : leccionesBasicas4) {
            String nodoId = rutaPython.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPython.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesBasicas5 = LeccionRepository.getListaLecciones(Lenguaje.PYTHON, NivelDificultad.BASICO, TemaLeccion.ENTRADA_Y_SALIDA, 3);
        for (Leccion leccion : leccionesBasicas5) {
            String nodoId = rutaPython.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPython.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        // --- Nivel Intermedio ---
        List<Leccion> leccionesIntermedias = LeccionRepository.getListaLecciones(Lenguaje.PYTHON, NivelDificultad.INTERMEDIO, TemaLeccion.CONTROL_DE_FLUJO, 3);
        for (Leccion leccion : leccionesIntermedias) {
            String nodoId = rutaPython.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPython.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesIntermedias1 = LeccionRepository.getListaLecciones(Lenguaje.PYTHON, NivelDificultad.INTERMEDIO, TemaLeccion.CLASES_Y_ESTRUCTURAS, 3);
        for (Leccion leccion : leccionesIntermedias1) {
            String nodoId = rutaPython.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPython.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesIntermedias2 = LeccionRepository.getListaLecciones(Lenguaje.PYTHON, NivelDificultad.INTERMEDIO, TemaLeccion.TIPOS_Y_OPERADORES, 3);
        for (Leccion leccion : leccionesIntermedias2) {
            String nodoId = rutaPython.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPython.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesIntermedias3 = LeccionRepository.getListaLecciones(Lenguaje.PYTHON, NivelDificultad.INTERMEDIO, TemaLeccion.FUNCIONES_Y_METODOS, 3);
        for (Leccion leccion : leccionesIntermedias3) {
            String nodoId = rutaPython.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPython.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesIntermedias4 = LeccionRepository.getListaLecciones(Lenguaje.PYTHON, NivelDificultad.INTERMEDIO, TemaLeccion.OTRO, 3);
        for (Leccion leccion : leccionesIntermedias4) {
            String nodoId = rutaPython.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPython.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesIntermedias5 = LeccionRepository.getListaLecciones(Lenguaje.PYTHON, NivelDificultad.INTERMEDIO, TemaLeccion.ENTRADA_Y_SALIDA, 3);
        for (Leccion leccion : leccionesIntermedias5) {
            String nodoId = rutaPython.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPython.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }

        // --- Nivel Avanzado ---
        List<Leccion> leccionesAvanzadas = LeccionRepository.getListaLecciones(Lenguaje.PYTHON, NivelDificultad.AVANZADO, TemaLeccion.CONTROL_DE_FLUJO, 3);
        for (Leccion leccion : leccionesAvanzadas) {
            String nodoId = rutaPython.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPython.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesAvanzadas1 = LeccionRepository.getListaLecciones(Lenguaje.PYTHON, NivelDificultad.AVANZADO, TemaLeccion.CLASES_Y_ESTRUCTURAS, 3);
        for (Leccion leccion : leccionesAvanzadas1) {
            String nodoId = rutaPython.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPython.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesAvanzadas2 = LeccionRepository.getListaLecciones(Lenguaje.PYTHON, NivelDificultad.AVANZADO, TemaLeccion.TIPOS_Y_OPERADORES, 3);
        for (Leccion leccion : leccionesAvanzadas2) {
            String nodoId = rutaPython.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPython.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesAvanzadas3 = LeccionRepository.getListaLecciones(Lenguaje.PYTHON, NivelDificultad.AVANZADO, TemaLeccion.FUNCIONES_Y_METODOS, 3);
        for (Leccion leccion : leccionesAvanzadas3) {
            String nodoId = rutaPython.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPython.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesAvanzadas4 = LeccionRepository.getListaLecciones(Lenguaje.PYTHON, NivelDificultad.AVANZADO, TemaLeccion.OTRO, 3);
        for (Leccion leccion : leccionesAvanzadas4) {
            String nodoId = rutaPython.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPython.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesAvanzadas5 = LeccionRepository.getListaLecciones(Lenguaje.PYTHON, NivelDificultad.AVANZADO, TemaLeccion.ENTRADA_Y_SALIDA, 3);
        for (Leccion leccion : leccionesAvanzadas5) {
            String nodoId = rutaPython.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPython.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }

        if (!rutaPython.getNodos().isEmpty()) {
            this.rutasPlantilla.add(rutaPython);
            System.out.println("    -> Ruta de Python finalizada con " + rutaPython.getNodos().size() + " nodos.");
        } else {
            System.err.println("    -> No se pudieron cargar lecciones para la Ruta de Python.");
        }
    }

    /**
     * Lógica específica para construir la ruta de php.
     */
    private void construirRutaPhp() {
        System.out.println(" -> Construyendo Ruta de Python...");
        Ruta rutaPhp = new Ruta("ruta-php", "PHP", "PHP");
        int ordenGlobalNodo = 1;
        // --- Nivel Básico ---
        List<Leccion> leccionesBasicas = LeccionRepository.getListaLecciones(Lenguaje.PHP, NivelDificultad.BASICO, TemaLeccion.CONTROL_DE_FLUJO, 3);
        for (Leccion leccion : leccionesBasicas) {
            String nodoId = rutaPhp.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPhp.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesBasicas1 = LeccionRepository.getListaLecciones(Lenguaje.PHP, NivelDificultad.BASICO, TemaLeccion.CLASES_Y_ESTRUCTURAS, 3);
        for (Leccion leccion : leccionesBasicas1) {
            String nodoId = rutaPhp.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPhp.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesBasicas2 = LeccionRepository.getListaLecciones(Lenguaje.PHP, NivelDificultad.BASICO, TemaLeccion.TIPOS_Y_OPERADORES, 3);
        for (Leccion leccion : leccionesBasicas2) {
            String nodoId = rutaPhp.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPhp.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesBasicas3 = LeccionRepository.getListaLecciones(Lenguaje.PHP, NivelDificultad.BASICO, TemaLeccion.FUNCIONES_Y_METODOS, 3);
        for (Leccion leccion : leccionesBasicas3) {
            String nodoId = rutaPhp.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPhp.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesBasicas4 = LeccionRepository.getListaLecciones(Lenguaje.PHP, NivelDificultad.BASICO, TemaLeccion.OTRO, 3);
        for (Leccion leccion : leccionesBasicas4) {
            String nodoId = rutaPhp.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPhp.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesBasicas5 = LeccionRepository.getListaLecciones(Lenguaje.PHP, NivelDificultad.BASICO, TemaLeccion.ENTRADA_Y_SALIDA, 3);
        for (Leccion leccion : leccionesBasicas5) {
            String nodoId = rutaPhp.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPhp.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        // --- Nivel Intermedio ---
        List<Leccion> leccionesIntermedias = LeccionRepository.getListaLecciones(Lenguaje.PHP, NivelDificultad.INTERMEDIO, TemaLeccion.CONTROL_DE_FLUJO, 3);
        for (Leccion leccion : leccionesIntermedias) {
            String nodoId = rutaPhp.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPhp.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesIntermedias1 = LeccionRepository.getListaLecciones(Lenguaje.PHP, NivelDificultad.INTERMEDIO, TemaLeccion.CLASES_Y_ESTRUCTURAS, 3);
        for (Leccion leccion : leccionesIntermedias1) {
            String nodoId = rutaPhp.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPhp.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesIntermedias2 = LeccionRepository.getListaLecciones(Lenguaje.PHP, NivelDificultad.INTERMEDIO, TemaLeccion.TIPOS_Y_OPERADORES, 3);
        for (Leccion leccion : leccionesIntermedias2) {
            String nodoId = rutaPhp.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPhp.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesIntermedias3 = LeccionRepository.getListaLecciones(Lenguaje.PHP, NivelDificultad.INTERMEDIO, TemaLeccion.FUNCIONES_Y_METODOS, 3);
        for (Leccion leccion : leccionesIntermedias3) {
            String nodoId = rutaPhp.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPhp.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesIntermedias4 = LeccionRepository.getListaLecciones(Lenguaje.PHP, NivelDificultad.INTERMEDIO, TemaLeccion.OTRO, 3);
        for (Leccion leccion : leccionesIntermedias4) {
            String nodoId = rutaPhp.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPhp.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesIntermedias5 = LeccionRepository.getListaLecciones(Lenguaje.PHP, NivelDificultad.INTERMEDIO, TemaLeccion.ENTRADA_Y_SALIDA, 3);
        for (Leccion leccion : leccionesIntermedias5) {
            String nodoId = rutaPhp.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPhp.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }

        // --- Nivel Avanzado ---
        List<Leccion> leccionesAvanzadas = LeccionRepository.getListaLecciones(Lenguaje.PHP, NivelDificultad.AVANZADO, TemaLeccion.CONTROL_DE_FLUJO, 3);
        for (Leccion leccion : leccionesAvanzadas) {
            String nodoId = rutaPhp.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPhp.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesAvanzadas1 = LeccionRepository.getListaLecciones(Lenguaje.PHP, NivelDificultad.AVANZADO, TemaLeccion.CLASES_Y_ESTRUCTURAS, 3);
        for (Leccion leccion : leccionesAvanzadas1) {
            String nodoId = rutaPhp.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPhp.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesAvanzadas2 = LeccionRepository.getListaLecciones(Lenguaje.PHP, NivelDificultad.AVANZADO, TemaLeccion.TIPOS_Y_OPERADORES, 3);
        for (Leccion leccion : leccionesAvanzadas2) {
            String nodoId = rutaPhp.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPhp.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesAvanzadas3 = LeccionRepository.getListaLecciones(Lenguaje.PHP, NivelDificultad.AVANZADO, TemaLeccion.FUNCIONES_Y_METODOS, 3);
        for (Leccion leccion : leccionesAvanzadas3) {
            String nodoId = rutaPhp.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPhp.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesAvanzadas4 = LeccionRepository.getListaLecciones(Lenguaje.PHP, NivelDificultad.AVANZADO, TemaLeccion.OTRO, 3);
        for (Leccion leccion : leccionesAvanzadas4) {
            String nodoId = rutaPhp.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPhp.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesAvanzadas5 = LeccionRepository.getListaLecciones(Lenguaje.PHP, NivelDificultad.AVANZADO, TemaLeccion.ENTRADA_Y_SALIDA, 3);
        for (Leccion leccion : leccionesAvanzadas5) {
            String nodoId = rutaPhp.getId() + "-nodo-" + ordenGlobalNodo;
            rutaPhp.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }

        if (!rutaPhp.getNodos().isEmpty()) {
            this.rutasPlantilla.add(rutaPhp);
            System.out.println("    -> Ruta de PHP finalizada con " + rutaPhp.getNodos().size() + " nodos.");
        } else {
            System.err.println("    -> No se pudieron cargar lecciones para la Ruta de PHP.");
        }
    }

    /**
     * Lógica específica para construir la ruta de C.
     */
    private void construirRutaC() {
        System.out.println(" -> Construyendo Ruta de Python...");
        Ruta rutaC = new Ruta("ruta-c", "C", "C");
        int ordenGlobalNodo = 1;
        // --- Nivel Básico ---
        List<Leccion> leccionesBasicas = LeccionRepository.getListaLecciones(Lenguaje.C, NivelDificultad.BASICO, TemaLeccion.CONTROL_DE_FLUJO, 3);
        for (Leccion leccion : leccionesBasicas) {
            String nodoId = rutaC.getId() + "-nodo-" + ordenGlobalNodo;
            rutaC.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesBasicas1 = LeccionRepository.getListaLecciones(Lenguaje.C, NivelDificultad.BASICO, TemaLeccion.CLASES_Y_ESTRUCTURAS, 3);
        for (Leccion leccion : leccionesBasicas1) {
            String nodoId = rutaC.getId() + "-nodo-" + ordenGlobalNodo;
            rutaC.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesBasicas2 = LeccionRepository.getListaLecciones(Lenguaje.C, NivelDificultad.BASICO, TemaLeccion.TIPOS_Y_OPERADORES, 3);
        for (Leccion leccion : leccionesBasicas2) {
            String nodoId = rutaC.getId() + "-nodo-" + ordenGlobalNodo;
            rutaC.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesBasicas3 = LeccionRepository.getListaLecciones(Lenguaje.C, NivelDificultad.BASICO, TemaLeccion.FUNCIONES_Y_METODOS, 3);
        for (Leccion leccion : leccionesBasicas3) {
            String nodoId = rutaC.getId() + "-nodo-" + ordenGlobalNodo;
            rutaC.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesBasicas4 = LeccionRepository.getListaLecciones(Lenguaje.C, NivelDificultad.BASICO, TemaLeccion.OTRO, 3);
        for (Leccion leccion : leccionesBasicas4) {
            String nodoId = rutaC.getId() + "-nodo-" + ordenGlobalNodo;
            rutaC.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesBasicas5 = LeccionRepository.getListaLecciones(Lenguaje.C, NivelDificultad.BASICO, TemaLeccion.ENTRADA_Y_SALIDA, 3);
        for (Leccion leccion : leccionesBasicas5) {
            String nodoId = rutaC.getId() + "-nodo-" + ordenGlobalNodo;
            rutaC.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        // --- Nivel Intermedio ---
        List<Leccion> leccionesIntermedias = LeccionRepository.getListaLecciones(Lenguaje.C, NivelDificultad.INTERMEDIO, TemaLeccion.CONTROL_DE_FLUJO, 3);
        for (Leccion leccion : leccionesIntermedias) {
            String nodoId = rutaC.getId() + "-nodo-" + ordenGlobalNodo;
            rutaC.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesIntermedias1 = LeccionRepository.getListaLecciones(Lenguaje.C, NivelDificultad.INTERMEDIO, TemaLeccion.CLASES_Y_ESTRUCTURAS, 3);
        for (Leccion leccion : leccionesIntermedias1) {
            String nodoId = rutaC.getId() + "-nodo-" + ordenGlobalNodo;
            rutaC.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesIntermedias2 = LeccionRepository.getListaLecciones(Lenguaje.C, NivelDificultad.INTERMEDIO, TemaLeccion.TIPOS_Y_OPERADORES, 3);
        for (Leccion leccion : leccionesIntermedias2) {
            String nodoId = rutaC.getId() + "-nodo-" + ordenGlobalNodo;
            rutaC.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesIntermedias3 = LeccionRepository.getListaLecciones(Lenguaje.C, NivelDificultad.INTERMEDIO, TemaLeccion.FUNCIONES_Y_METODOS, 3);
        for (Leccion leccion : leccionesIntermedias3) {
            String nodoId = rutaC.getId() + "-nodo-" + ordenGlobalNodo;
            rutaC.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesIntermedias4 = LeccionRepository.getListaLecciones(Lenguaje.C, NivelDificultad.INTERMEDIO, TemaLeccion.OTRO, 3);
        for (Leccion leccion : leccionesIntermedias4) {
            String nodoId = rutaC.getId() + "-nodo-" + ordenGlobalNodo;
            rutaC.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesIntermedias5 = LeccionRepository.getListaLecciones(Lenguaje.C, NivelDificultad.INTERMEDIO, TemaLeccion.ENTRADA_Y_SALIDA, 3);
        for (Leccion leccion : leccionesIntermedias5) {
            String nodoId = rutaC.getId() + "-nodo-" + ordenGlobalNodo;
            rutaC.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }

        // --- Nivel Avanzado ---
        List<Leccion> leccionesAvanzadas = LeccionRepository.getListaLecciones(Lenguaje.C, NivelDificultad.AVANZADO, TemaLeccion.CONTROL_DE_FLUJO, 3);
        for (Leccion leccion : leccionesAvanzadas) {
            String nodoId = rutaC.getId() + "-nodo-" + ordenGlobalNodo;
            rutaC.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesAvanzadas1 = LeccionRepository.getListaLecciones(Lenguaje.C, NivelDificultad.AVANZADO, TemaLeccion.CLASES_Y_ESTRUCTURAS, 3);
        for (Leccion leccion : leccionesAvanzadas1) {
            String nodoId = rutaC.getId() + "-nodo-" + ordenGlobalNodo;
            rutaC.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesAvanzadas2 = LeccionRepository.getListaLecciones(Lenguaje.C, NivelDificultad.AVANZADO, TemaLeccion.TIPOS_Y_OPERADORES, 3);
        for (Leccion leccion : leccionesAvanzadas2) {
            String nodoId = rutaC.getId() + "-nodo-" + ordenGlobalNodo;
            rutaC.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesAvanzadas3 = LeccionRepository.getListaLecciones(Lenguaje.C, NivelDificultad.AVANZADO, TemaLeccion.FUNCIONES_Y_METODOS, 3);
        for (Leccion leccion : leccionesAvanzadas3) {
            String nodoId = rutaC.getId() + "-nodo-" + ordenGlobalNodo;
            rutaC.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesAvanzadas4 = LeccionRepository.getListaLecciones(Lenguaje.C, NivelDificultad.AVANZADO, TemaLeccion.OTRO, 3);
        for (Leccion leccion : leccionesAvanzadas4) {
            String nodoId = rutaC.getId() + "-nodo-" + ordenGlobalNodo;
            rutaC.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }
        List<Leccion> leccionesAvanzadas5 = LeccionRepository.getListaLecciones(Lenguaje.C, NivelDificultad.AVANZADO, TemaLeccion.ENTRADA_Y_SALIDA, 3);
        for (Leccion leccion : leccionesAvanzadas5) {
            String nodoId = rutaC.getId() + "-nodo-" + ordenGlobalNodo;
            rutaC.agregarNodo(new NodoRuta(ordenGlobalNodo++, nodoId, leccion));
        }

        if (!rutaC.getNodos().isEmpty()) {
            this.rutasPlantilla.add(rutaC);
            System.out.println("    -> Ruta de C finalizada con " + rutaC.getNodos().size() + " nodos.");
        } else {
            System.err.println("    -> No se pudieron cargar lecciones para la Ruta de C.");
        }
    }


    /**
     * Devuelve una ruta específica por su nombre de lenguaje.
     */
    public Ruta getRutaPorNombre(String nombre) {
        return rutasPlantilla.stream()
                .filter(r -> r.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }

    // --- MÉTODOS DE GESTIÓN DE PROGRESO ---

    public void marcarNodoComoCompletado(Usuario usuario, NodoRuta nodo) {
        if (usuario == null || nodo == null) return;
        String username = usuario.getUsername();
        Map<String, Boolean> progresoDelUsuario = progresoPorUsuario.computeIfAbsent(username, k -> new HashMap<>());
        if (progresoDelUsuario.getOrDefault(nodo.getId(), false)) return;
        progresoDelUsuario.put(nodo.getId(), true);
        guardarProgresoEnArchivo();
        int totalCompletadas = getLeccionesCompletadasParaUsuario(usuario);
        System.out.println("[AprendizajeManager] Notificando a Gamificación. Total completadas: " + totalCompletadas);
        LeccionesCompletadas.set(totalCompletadas);
    }

    public boolean isNodoCompletadoParaUsuario(Usuario usuario, NodoRuta nodo) {
        if (usuario == null || nodo == null) return false;
        Map<String, Boolean> progresoDelUsuario = progresoPorUsuario.get(usuario.getUsername());
        if (progresoDelUsuario == null) return false;
        return progresoDelUsuario.getOrDefault(nodo.getId(), false);
    }

    public int getLeccionesCompletadasParaUsuario(Usuario usuario) {
        if (usuario == null) return 0;
        Map<String, Boolean> progresoDelUsuario = progresoPorUsuario.get(usuario.getUsername());
        return (progresoDelUsuario == null) ? 0 : progresoDelUsuario.size();
    }

    public List<Ruta> getRutas() {
        return rutasPlantilla;
    }

    // --- MÉTODOS DE PERSISTENCIA ---

    private void guardarProgresoEnArchivo() {
        List<String> lineas = new ArrayList<>();
        for (Map.Entry<String, Map<String, Boolean>> entry : progresoPorUsuario.entrySet()) {
            String username = entry.getKey();
            String nodosCompletados = String.join(",", entry.getValue().keySet());
            if (!nodosCompletados.isEmpty()) lineas.add(username + ";" + nodosCompletados);
        }
        try {
            Files.write(RUTA_ARCHIVO_PROGRESO, lineas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarProgresoDesdeArchivo() {
        if (!Files.exists(RUTA_ARCHIVO_PROGRESO)) return;
        try {
            List<String> lineas = Files.readAllLines(RUTA_ARCHIVO_PROGRESO);
            for (String linea : lineas) {
                String[] partes = linea.split(";", 2);
                if (partes.length == 2 && !partes[1].isEmpty()) {
                    String username = partes[0];
                    List<String> nodosCompletados = Arrays.asList(partes[1].split(","));
                    Map<String, Boolean> progreso = nodosCompletados.stream().collect(Collectors.toMap(id -> id, id -> true));
                    progresoPorUsuario.put(username, progreso);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
