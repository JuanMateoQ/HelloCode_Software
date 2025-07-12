package EjerciciosIteractivos_Modulo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Estructura {

    private final String ejercicioSinCompletar;
    private final String bandera;

    public Estructura(String ejercicioSinCompletar, String bandera) {
        this.ejercicioSinCompletar = ejercicioSinCompletar;
        this. bandera = asignarBandera(bandera);
    }

    private String asignarBandera(String bandera) {
        List<String> especiales = Arrays.asList(
                ".", "^", "$", "*", "+", "?", "(", ")", "[", "]", "{", "}", "|", "\\"
        );
        if (especiales.contains(bandera)) {
            return "\\" + bandera;
        } else {
            return bandera;
        }
    }

    public ArrayList<String> convertirEjercicioEnPartes() {
        String [] aux =ejercicioSinCompletar.split(this.bandera);
        ArrayList<String> listaPartes = new ArrayList<>();
        for (String parte : aux) {
            listaPartes.add(parte.trim());
        }
        System.out.println(listaPartes);
        System.out.println(listaPartes.size());
        return listaPartes;
    }
}
