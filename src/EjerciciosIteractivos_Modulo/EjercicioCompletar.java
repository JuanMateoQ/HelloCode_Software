package EjerciciosIteractivos_Modulo;

import java.util.ArrayList;

public class EjercicioCompletar extends Ejercicio {
    private final ArrayList<String> partesACompletar;

    public EjercicioCompletar(String enunciado, ArrayList<String> partesACompletar, ArrayList<String> respuestas) {
        super(enunciado, respuestas);

        this.partesACompletar = partesACompletar;
    }

    public ArrayList<String> getPartesACompletar() {
        return this.partesACompletar;
    }


}
