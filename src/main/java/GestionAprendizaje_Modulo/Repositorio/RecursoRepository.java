package GestionAprendizaje_Modulo.Repositorio;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import GestionAprendizaje_Modulo.Modelo.Articulo;
import GestionAprendizaje_Modulo.Modelo.DocumentoPDF;
import GestionAprendizaje_Modulo.Modelo.RecursoAprendizaje;
import GestionAprendizaje_Modulo.Modelo.Video;

public class RecursoRepository {

    private static RecursoRepository instancia;
    private final List<RecursoAprendizaje> bancoDeRecursos;

    private RecursoRepository() {
        this.bancoDeRecursos = new ArrayList<>();
    }

    public static synchronized RecursoRepository getInstancia() {
        if (instancia == null) {
            instancia = new RecursoRepository();
        }
        return instancia;
    }

    public void cargarRecursosDesdeTXT() {
        if (!bancoDeRecursos.isEmpty()) return;
        System.out.println("[RecursoRepository] Cargando recursos desde recursos.txt...");
        try (InputStream is = getClass().getResourceAsStream("/GestionAprendizaje_Modulo/data/recursos.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is), StandardCharsets.UTF_8))) {
            reader.lines().forEach(linea -> {
                if (linea.isBlank()) return;
                String[] partes = linea.split("\\|", 6);
                if (partes.length >= 5) {
                    RecursoAprendizaje recurso = crearRecurso(partes);
                    if (recurso != null) bancoDeRecursos.add(recurso);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("[RecursoRepository] Carga completada. Total: " + bancoDeRecursos.size() + " recursos.");
    }

    private RecursoAprendizaje crearRecurso(String[] partes) {
        try {
            String titulo = partes[0].trim();
            String lenguaje = partes[1].trim();
            String tema = partes[2].trim();
            String tipo = partes[3].trim().toLowerCase();
            String url = partes[4].trim();
            String datoExtra = (partes.length == 6) ? partes[5].trim() : "0";

            if (tipo.equals("video")) return new Video(titulo, lenguaje, tema, url, Integer.parseInt(datoExtra));
            if (tipo.equals("pdf")) return new DocumentoPDF(titulo, lenguaje, tema, url, Integer.parseInt(datoExtra));
            if (tipo.equals("articulo")) return new Articulo(titulo, lenguaje, tema, url);
        } catch (Exception e) {
            System.err.println("Error al parsear línea de recurso: " + String.join("|", partes));
        }
        return null;
    }

    /**
     * BÚSQUEDA CON DEPURACIÓN AVANZADA
     */
    public List<RecursoAprendizaje> buscarRecursosPorLenguajeYTema(String lenguaje, String tema) {
        if (lenguaje == null || tema == null) return Collections.emptyList();

        String langBuscado = lenguaje.trim().toLowerCase();
        String temaBuscado = tema.trim().toLowerCase();

        System.out.println("\n--- INICIANDO BÚSQUEDA DETALLADA ---");
        System.out.println("Buscando para: Lenguaje='" + langBuscado + "', Tema='" + temaBuscado + "'");
        System.out.println("Total de recursos en el banco: " + bancoDeRecursos.size());

        List<RecursoAprendizaje> encontrados = new ArrayList<>();

        // Hacemos el bucle manualmente para poder imprimir cada comparación.
        for (RecursoAprendizaje recurso : bancoDeRecursos) {
            String langRecurso = recurso.getLenguaje().trim().toLowerCase();
            String temaRecurso = recurso.getTema().trim().toLowerCase();

            // Comparamos el lenguaje
            boolean coincidenciaLenguaje = langRecurso.equals(langBuscado);
            // Comparamos el tema
            boolean coincidenciaTema = temaRecurso.equals(temaBuscado);

            // Imprimimos el resultado de la comparación para este recurso
            System.out.println("  - Comparando con recurso: '" + recurso.getTitulo() + "'");
            System.out.println("    > Lenguaje: '" + langRecurso + "' vs '" + langBuscado + "' -> Coincide: " + coincidenciaLenguaje);
            System.out.println("    > Tema:     '" + temaRecurso + "' vs '" + temaBuscado + "' -> Coincide: " + coincidenciaTema);

            if (coincidenciaLenguaje && coincidenciaTema) {
                encontrados.add(recurso);
                System.out.println("    --> ¡COINCIDENCIA ENCONTRADA!");
            }
        }

        System.out.println("--- BÚSQUEDA FINALIZADA ---");
        System.out.println("Se encontraron " + encontrados.size() + " recursos en total.\n");
        return encontrados;
    }
}