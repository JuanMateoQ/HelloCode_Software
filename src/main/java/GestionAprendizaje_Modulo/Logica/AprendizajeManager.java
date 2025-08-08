package GestionAprendizaje_Modulo.Logica;

import GestionAprendizaje_Modulo.Logica.Curso;
import GestionAprendizaje_Modulo.Logica.NodoRuta;
import GestionAprendizaje_Modulo.Logica.Ruta;
import MetodosGlobales.LeccionesCompletadas;
import Modulo_Usuario.Clases.Usuario;
import Nuevo_Modulo_Leccion.dataBase.LeccionRepository;
import Nuevo_Modulo_Leccion.logic.Leccion;

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

/**
 * =================================================================================
 * Gestor de Aprendizaje - Versión Final (Integrado con Gamificación)
 * =================================================================================
 * Esta clase es el cerebro operacional del módulo. Sus responsabilidades son:
 * 1. Construir la estructura de "plantilla" de Cursos y Rutas de forma segura.
 * 2. Cargar y Guardar de forma permanente el progreso de CADA usuario en un archivo.
 * 3. Proveer métodos para que la UI pueda consultar y actualizar dicho progreso.
 * 4. Notificar al módulo de Gamificación cada vez que el progreso de un usuario se actualiza.
 */
public class AprendizajeManager {

    // --- Implementación del Patrón Singleton ---
    private static AprendizajeManager instancia;

    // --- Estructuras de Datos Principales ---
    private final List<Curso> cursosPlantilla; // La "plantilla" de cursos, igual para todos
    private final Map<String, Map<String, Boolean>> progresoPorUsuario; // El progreso individual de cada usuario
    private final Path RUTA_ARCHIVO_PROGRESO = Paths.get("progreso_usuarios.txt"); // "Tarjeta de memoria"

    /**
     * El constructor es PRIVADO. Al crearse la instancia, intenta cargar el progreso guardado.
     */
    private AprendizajeManager() {
        this.cursosPlantilla = new ArrayList<>();
        this.progresoPorUsuario = new HashMap<>();
        cargarProgresoDesdeArchivo();
    }

    /**
     * Punto de acceso único y global a la instancia del manager.
     */
    public static synchronized AprendizajeManager getInstancia() {
        if (instancia == null) {
            instancia = new AprendizajeManager();
        }
        return instancia;
    }

    /**
     * Construye toda la estructura de datos. Se ejecuta una sola vez y es "a prueba de fallos".
     */
    public void construirDatosDePrueba() {
        if (!cursosPlantilla.isEmpty()) {
            System.out.println("[AprendizajeManager] La estructura ya fue construida.");
            return;
        }
        System.out.println("[AprendizajeManager] Construyendo estructura de aprendizaje...");
        List<Leccion> pozoDeLecciones;
        try {
            pozoDeLecciones = LeccionRepository.getLecciones();
            if (pozoDeLecciones.isEmpty()) {
                throw new IllegalStateException("Lista de lecciones vacía, activando plan de contingencia.");
            }
            System.out.println("[AprendizajeManager] ÉXITO: Se jalaron " + pozoDeLecciones.size() + " lecciones.");
        } catch (Exception e) {
            System.err.println("[AprendizajeManager] ERROR al jalar lecciones: " + e.getMessage());
            System.err.println("[AprendizajeManager] PROCEDIENDO A CREAR LECCIONES DE EMERGENCIA.");
            pozoDeLecciones = List.of(new Leccion(), new Leccion(), new Leccion());
        }

        Curso cursoDemo = new Curso("curso-java-01", "Curso de Java", "Aprende Java desde cero");
        Ruta rutaBasica = new Ruta("ruta-java-basico", "Conceptos Básicos", "BASICO");
        int ordenNodo = 1;
        for (Leccion leccion : pozoDeLecciones) {
            String nodoId = rutaBasica.getId() + "-nodo-" + ordenNodo;
            NodoRuta nodo = new NodoRuta(ordenNodo++, nodoId, leccion);
            rutaBasica.agregarNodo(nodo);
        }
        cursoDemo.agregarRuta(rutaBasica);
        this.cursosPlantilla.add(cursoDemo);
        System.out.println("[AprendizajeManager] Estructura creada con " + rutaBasica.getNodos().size() + " nodos.");
    }

    /**
     * Guarda el progreso de un usuario y notifica al sistema de gamificación.
     * @param usuario El usuario que ha completado el nodo.
     * @param nodo El NodoRuta que ha sido completado.
     */
    public void marcarNodoComoCompletado(Usuario usuario, NodoRuta nodo) {
        if (usuario == null || nodo == null) return;

        String username = usuario.getUsername();
        Map<String, Boolean> progresoDelUsuario = progresoPorUsuario.computeIfAbsent(username, k -> new HashMap<>());

        if (progresoDelUsuario.getOrDefault(nodo.getId(), false)) return; // No hacer nada si ya está completado

        progresoDelUsuario.put(nodo.getId(), true);
        System.out.println("[AprendizajeManager] Progreso guardado en memoria para '" + username + "'.");
        guardarProgresoEnArchivo();

        // --- ¡INTEGRACIÓN CON GAMIFICACIÓN! ---
        int totalCompletadas = getLeccionesCompletadasParaUsuario(usuario);
        System.out.println("[AprendizajeManager] Notificando a Gamificación. Total completadas: " + totalCompletadas);
        LeccionesCompletadas.set(totalCompletadas);
    }

    /**
     * Verifica si un usuario ha completado un nodo específico (lee desde la memoria).
     */
    public boolean isNodoCompletadoParaUsuario(Usuario usuario, NodoRuta nodo) {
        if (usuario == null || nodo == null) return false;
        Map<String, Boolean> progresoDelUsuario = progresoPorUsuario.get(usuario.getUsername());
        if (progresoDelUsuario == null) return false;
        return progresoDelUsuario.getOrDefault(nodo.getId(), false);
    }

    /**
     * Devuelve la plantilla de cursos para que la UI la pueda dibujar.
     */
    public List<Curso> getCursos() {
        return cursosPlantilla;
    }

    /**
     * Calcula y devuelve la cantidad total de lecciones completadas para un usuario.
     * Es la fuente de verdad para la clase ProgresoLecciones.
     */
    public int getLeccionesCompletadasParaUsuario(Usuario usuario) {
        if (usuario == null) return 0;
        Map<String, Boolean> progresoDelUsuario = progresoPorUsuario.get(usuario.getUsername());
        return (progresoDelUsuario == null) ? 0 : progresoDelUsuario.size();
    }

    // --- MÉTODOS DE LECTURA/ESCRITURA A ARCHIVO ---

    /**
     * Guarda el mapa de progreso completo en el archivo progreso_usuarios.txt.
     */
    private void guardarProgresoEnArchivo() {
        List<String> lineas = new ArrayList<>();
        // Formato de línea: username;id_nodo1,id_nodo2,id_nodo3
        for (Map.Entry<String, Map<String, Boolean>> entry : progresoPorUsuario.entrySet()) {
            String username = entry.getKey();
            String nodosCompletados = String.join(",", entry.getValue().keySet());
            if (!nodosCompletados.isEmpty()) {
                lineas.add(username + ";" + nodosCompletados);
            }
        }
        try {
            Files.write(RUTA_ARCHIVO_PROGRESO, lineas);
            System.out.println("[AprendizajeManager] Progreso guardado permanentemente en " + RUTA_ARCHIVO_PROGRESO.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("[AprendizajeManager] ERROR: No se pudo guardar el progreso en el archivo.");
            e.printStackTrace();
        }
    }

    /**
     * Lee el archivo progreso_usuarios.txt y llena el mapa en memoria.
     */
    private void cargarProgresoDesdeArchivo() {
        if (!Files.exists(RUTA_ARCHIVO_PROGRESO)) {
            System.out.println("[AprendizajeManager] No se encontró archivo de progreso. Se empezará de cero.");
            return;
        }
        try {
            List<String> lineas = Files.readAllLines(RUTA_ARCHIVO_PROGRESO);
            for (String linea : lineas) {
                String[] partes = linea.split(";", 2);
                if (partes.length == 2 && !partes[1].isEmpty()) {
                    String username = partes[0];
                    List<String> nodosCompletados = Arrays.asList(partes[1].split(","));
                    Map<String, Boolean> progreso = nodosCompletados.stream()
                            .collect(Collectors.toMap(id -> id, id -> true));
                    progresoPorUsuario.put(username, progreso);
                }
            }
            System.out.println("[AprendizajeManager] Se ha cargado el progreso para " + progresoPorUsuario.size() + " usuarios desde el archivo.");
        } catch (IOException e) {
            System.err.println("[AprendizajeManager] ERROR: No se pudo cargar el progreso desde el archivo.");
            e.printStackTrace();
        }
    }
}