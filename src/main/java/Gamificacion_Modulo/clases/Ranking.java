package Gamificacion_Modulo.clases;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;
//se puso final para mantener al ranking como singleton
public final class Ranking {

    private static Ranking INSTANCE;
    private List<ProgresoEstudiante> rankingGeneral;


    private Ranking() {
        this.rankingGeneral = new ArrayList<>();
    }

    public static Ranking getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Ranking();
        }

        return INSTANCE;
    }

    public static List<ProgresoEstudiante> getProgresos() {
        return new ArrayList<>(obtenerRankingGlobal());
    }

    public static int getTotalProgresos() {
        return obtenerRankingGlobal().size();
    }
    public void actualizarRanking(ProgresoEstudiante estudiante) {
        // Agregar estudiante si no existe
        if (!rankingGeneral.contains(estudiante)) {
            rankingGeneral.add(estudiante);
        }

        // Ordenar por puntos totales (descendente)
        Collections.sort(rankingGeneral, new Comparator<ProgresoEstudiante>() {
            @Override
            public int compare(ProgresoEstudiante e1, ProgresoEstudiante e2) {
                return e2.getPuntosTotal().compareTo(e1.getPuntosTotal());
            }
        });
    }

    public List<ProgresoEstudiante> obtenerRankingGeneral() {
        return this.rankingGeneral;
    }

    // Métodos estáticos para acceso directo al singleton
    public static Ranking getRanking() {
        return getInstance();
    }

    public static List<ProgresoEstudiante> obtenerRankingGlobal() {
        return getInstance().obtenerRankingGeneral();
    }

}