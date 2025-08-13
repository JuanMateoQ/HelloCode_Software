package Conexion;

import Gamificacion_Modulo.clases.Desafio;
import Gamificacion_Modulo.clases.ProgresoEstudiante;
import Gamificacion_Modulo.clases.Ranking;
import Modulo_Usuario.Clases.Usuario;

public class LeccionesCompletadas {

    private static Ranking ran = Ranking.getInstance();
    private static Usuario usr = SesionManager.getInstancia().getUsuarioAutenticado();
    private static ProgresoEstudiante progresoActual = encontrarProgresoActual();

    public static void set(int cantidad){
        usr = SesionManager.getInstancia().getUsuarioAutenticado();
        progresoActual = encontrarProgresoActual();
        progresoActual.verificarDesafios();
        if(!progresoActual.getDesafiosActivos().isEmpty()){
            for(Desafio desafio: progresoActual.getDesafiosActivos()){
                desafio.actualizarAvance(1);
                if(desafio.estaCompletado())
                    desafio.completarDesafio(progresoActual);
            }
        }
        progresoActual.verificarDesafios();
    }

    private static boolean compare(Usuario u1, Usuario u2){
        return u1.getUsername().equals(u2.getUsername());
    }

    public static void aumentarXP(int cantidad){
        usr = SesionManager.getInstancia().getUsuarioAutenticado();;
        progresoActual = encontrarProgresoActual();
        progresoActual.sumarPuntos(cantidad);
    }
    private static ProgresoEstudiante encontrarProgresoActual(){
        ran = Ranking.getInstance();
        for(ProgresoEstudiante progreso: ran.obtenerRankingGeneral()){
            if(compare(progreso.getUsuario(), usr))
                return progreso;
        }
        return new ProgresoEstudiante(usr);
    }
}