package Comunidad_Modulo.modelo;

import Modulo_Usuario.Clases.NivelAprendizaje;
import Modulo_Usuario.Clases.UsuarioComunidad;
import Comunidad_Modulo.enums.TipoTema;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GrupoDiscusion {
    private String idGrupo;
    private String titulo;
    private NivelAprendizaje nivelAprendizaje;
    private TipoTema tipoTema;
    private List<UsuarioComunidad> miembros;
    private List<HiloDiscusion> hilos;

    public GrupoDiscusion(String titulo, NivelAprendizaje nivelAprendizaje, TipoTema tipoTema) {
        this.idGrupo = UUID.randomUUID().toString();
        this.titulo = titulo;
        this.nivelAprendizaje = nivelAprendizaje;
        this.tipoTema = tipoTema;
        this.miembros = new ArrayList<>();
        this.hilos = new ArrayList<>();
    }

    // Getters y setters
    public String getIdGrupo() {
        return idGrupo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public NivelAprendizaje getNivelJava() {
        return nivelAprendizaje;
    }

    public void setNivelJava(NivelAprendizaje nivelAprendizaje) {
        this.nivelAprendizaje = nivelAprendizaje;
    }

    public TipoTema getTipoTema() {
        return tipoTema;
    }

    public void setTipoTema(TipoTema tipoTema) {
        this.tipoTema = tipoTema;
    }

    public List<UsuarioComunidad> getMiembros() {
        return new ArrayList<>(miembros);
    }

    public List<HiloDiscusion> getHilos() {
        return new ArrayList<>(hilos);
    }

    // Métodos de negocio
    public void addHilo(HiloDiscusion hilo) {
        if (!miembros.contains(hilo.getAutor())) {
            throw new IllegalArgumentException("Solo los miembros pueden crear hilos");
        }
        hilos.add(hilo);
    }

    public void unirseGrupo(UsuarioComunidad usuario) {
        if (!miembros.contains(usuario)) {
            miembros.add(usuario);
        }
    }

    public void salirGrupo(UsuarioComunidad usuario) {
        miembros.remove(usuario);
    }

    public boolean esApropiado(UsuarioComunidad usuario) {
        return usuario.getNivelJava() == nivelAprendizaje;
    }

    public int getHilosActivos() {
        return (int) hilos.stream()
                .filter(h -> h.getEstado() == Comunidad_Modulo.enums.EstadoHilo.ABIERTO)
                .count();
    }

    public int getHilosResueltos() {
        return (int) hilos.stream()
                .filter(h -> h.getEstado() == Comunidad_Modulo.enums.EstadoHilo.RESUELTO)
                .count();
    }

    @Override
    public String toString() {
        return String.format("Grupo: %s [%s - %s] (%d miembros, %d hilos)",
                titulo, nivelAprendizaje, tipoTema, miembros.size(), hilos.size());
    }
}
