package Gamificacion_Modulo.clases;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;

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
        return new ArrayList<>(rankingGeneral);
    }

    public Integer calcularPosicion(ProgresoEstudiante estudiante) {
        return rankingGeneral.indexOf(estudiante) + 1;
    }
}