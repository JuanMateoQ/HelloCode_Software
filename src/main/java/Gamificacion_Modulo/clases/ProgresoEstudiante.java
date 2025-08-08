package Gamificacion_Modulo.clases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Modulo_Usuario.Clases.Usuario;

public class ProgresoEstudiante {
    private Usuario usuario;
    private List<Logro> logrosDesbloqueados;
    private Integer puntosTotal;
    private HashMap<String, Double> listaDesafios; // Hash<id, porcentaje>
    private Integer desafiosCompletados;
    private List<Desafio> desafiosActivos = new ArrayList<>();

    public List<Logro> getLogrosDesbloqueados() {
        return logrosDesbloqueados;
    }

    // Lista estática de todos los progresos
    private static final List<ProgresoEstudiante> progresos = new ArrayList<>();

    public ProgresoEstudiante(Usuario usuario) {
        this.usuario = usuario;
        this.logrosDesbloqueados = new ArrayList<>();
        this.puntosTotal = 0;
        this.listaDesafios = new HashMap<>();
        this.desafiosCompletados = 0;

        // Agregar automáticamente a la lista estática
        progresos.add(this);
    }

    public void setPuntosTotal(Integer puntosTotal) {
        this.puntosTotal = puntosTotal;
        // Actualizar automáticamente el ranking cuando cambian los puntos
        Ranking.actualizarRankingGlobal(this);
    }

    public void actualizarProgreso(Desafio desafio) {
        // 1. Verificar si el desafío está completado
        if (desafio.estaCompletado()) {
            // 2. Actualizar porcentaje en lista de desafíos
            listaDesafios.put("Xd", 100.0);

            // 3. Verificar si desbloquea logros
            if (desafio.verificarComplecion(this)) {
                // 4. Completar el desafío (suma puntos y desactiva)
                desafio.completarDesafio(this);
                desafiosCompletados++;
                System.out.println("Desafio completado! Puntos totales: " + puntosTotal);
            }
        } else {
            // Actualizar progreso parcial
            Double progreso = 0.0;
            if (desafio instanceof DesafioSemanal) {
                progreso = ((DesafioSemanal) desafio).getProgreso();
            } else if (desafio instanceof DesafioMensual) {
                progreso = ((DesafioMensual) desafio).getProgreso();
            }
            listaDesafios.put("xd", progreso);
        }
    }

    public void agregarLogro(Logro logro) {
        if (!logrosDesbloqueados.contains(logro)) {
            logrosDesbloqueados.add(logro);
            puntosTotal += logro.getPuntos();
            System.out.println("*** Logro desbloqueado: " + logro.getNombre() + "! (+" + logro.getPuntos() + " puntos)");
        }
    }

    public List<Desafio> getDesafiosActivos() {
        return desafiosActivos;
    }
    public void agregarDesafio(Desafio desafio) {
        desafiosActivos.add(desafio);
    }

    public void sumarPuntos(Integer puntos) {
        this.puntosTotal += puntos;
        // Actualizar automáticamente el ranking cuando cambian los puntos
        Ranking.actualizarRankingGlobal(this);
    }

    // Getters
    public List<Logro> getLogros() { return logrosDesbloqueados; }
    public Integer getPuntosTotal() { return puntosTotal; }
    public Integer getDesafiosCompletados() { return desafiosCompletados; }
    public Usuario getUsuario() { return usuario; }
    public HashMap<String, Double> getListaDesafios() { return listaDesafios; }

    // Métodos estáticos para gestionar la lista de progresos
    public static List<ProgresoEstudiante> getProgresos() {
        return new ArrayList<>(progresos);
    }

    public static ProgresoEstudiante buscarProgresoPorUsuario(String username) {
        return progresos.stream()
                .filter(p -> p.getUsuario().getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public static ProgresoEstudiante getProgresoUsuarioLogueado() {
        try {
            MetodosGlobales.SesionManager sesionManager = MetodosGlobales.SesionManager.getInstancia();
            Modulo_Usuario.Clases.Usuario usuarioLogueado = sesionManager.getUsuarioAutenticado();

            if (usuarioLogueado == null) {
                System.out.println(">>> No hay usuario logueado en el sistema");
                return null;
            }

            // Buscar el progreso del usuario logueado
            for (ProgresoEstudiante progreso : progresos) {
                if (progreso.getUsuario().getUsername().equals(usuarioLogueado.getUsername())) {
                    System.out.println(">>> Progreso encontrado para usuario logueado: " + usuarioLogueado.getNombre());
                    return progreso;
                }
            }

            System.out.println(">>> No se encontró progreso para el usuario logueado: " + usuarioLogueado.getNombre());
            return null;

        } catch (Exception e) {
            System.err.println(">>> Error al obtener progreso del usuario logueado: " + e.getMessage());
            return null;
        }
    }

    public static boolean existeProgreso(String username) {
        return progresos.stream()
                .anyMatch(p -> p.getUsuario().getUsername().equals(username));
    }

    public static void limpiarProgresos() {
        progresos.clear();
    }

    public static int getTotalProgresos() {
        return progresos.size();
    }
}