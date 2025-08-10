package Conexion;

import Gamificacion_Modulo.clases.ProgresoEstudiante;
import Gamificacion_Modulo.clases.Ranking;
import Modulo_Usuario.Clases.Usuario;

public class LeccionesCompletadas {
    public static void set(int cantidad){
        Ranking ran = Ranking.getInstance();
        String usr = SesionManager.getInstancia().getUsernameActual();
        for(ProgresoEstudiante progreso: ran.obtenerRankingGeneral()){
           if (compare(progreso.getUsuario(), usr)){
               progreso.getDesafiosActivos().getFirst().actualizarAvance(cantidad);
           }
        }
    }

    private static boolean compare(Usuario u1, String u2){
        return u1.getUsername().equals(u2);
    }
}
