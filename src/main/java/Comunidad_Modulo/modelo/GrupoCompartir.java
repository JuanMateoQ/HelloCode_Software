package Comunidad_Modulo.modelo;

import Modulo_Usuario.Clases.UsuarioComunidad;
import Modulo_Usuario.Clases.NivelJava;
import Comunidad_Modulo.enums.TipoTema;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GrupoCompartir {
    private String idGrupo;
    private String titulo;
    private NivelJava nivelJava;
    private TipoTema tipoTema;
    private List<UsuarioComunidad> miembros;
    private List<Solucion> soluciones;

    public GrupoCompartir(String titulo, NivelJava nivelJava, TipoTema tipoTema) {
        this.idGrupo = UUID.randomUUID().toString();
        this.titulo = titulo;
        this.nivelJava = nivelJava;
        this.tipoTema = tipoTema;
        this.miembros = new ArrayList<>();
        this.soluciones = new ArrayList<>();
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

    public NivelJava getNivelJava() {
        return nivelJava;
    }

    public void setNivelJava(NivelJava nivelJava) {
        this.nivelJava = nivelJava;
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

    public List<Solucion> getSoluciones() {
        return new ArrayList<>(soluciones);
    }

    // Métodos de negocio
    public void compartirSolucion(Solucion solucion) {
        if (!miembros.contains(solucion.getAutor())) {
            throw new IllegalArgumentException("Solo los miembros pueden compartir soluciones");
        }

        soluciones.add(solucion);
        // Otorgar puntos de reputación por compartir
        solucion.getAutor().incrementarReputacion(3);
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
        return usuario.getNivelJava() == nivelJava;
    }


    @Override
    public String toString() {
        return String.format("Grupo: %s [%s - %s] (%d miembros, %d soluciones)",
                titulo, nivelJava, tipoTema, miembros.size(), soluciones.size());
    }
}
