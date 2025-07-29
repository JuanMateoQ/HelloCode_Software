package Gamificacion_Modulo.clases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Modulo_Usuario.Clases.Usuario;

public class ProgresoEstudiante {
    private Usuario usuario;
    private List<Logro> logrosDesbloqueados;
    private Integer puntosTotal;
    public void setPuntosTotal(Integer puntosTotal) {
        this.puntosTotal = puntosTotal;
    }
    private HashMap<String, Double> listaDesafios; // Hash<id, porcentaje>
    private Integer desafiosCompletados;
    private List<Desafio> desafiosActivos = new ArrayList<>();

    public ProgresoEstudiante(Usuario usuario) {
        this.usuario = usuario;
        this.logrosDesbloqueados = new ArrayList<>();
        this.puntosTotal = 0;
        this.listaDesafios = new HashMap<>();
        this.desafiosCompletados = 0;
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
    }

    // Getters
    public List<Logro> getLogros() { return logrosDesbloqueados; }
    public Integer getPuntosTotal() { return puntosTotal; }
    public Integer getDesafiosCompletados() { return desafiosCompletados; }
    public Usuario getUsuario() { return usuario; }
    public HashMap<String, Double> getListaDesafios() { return listaDesafios; }
}