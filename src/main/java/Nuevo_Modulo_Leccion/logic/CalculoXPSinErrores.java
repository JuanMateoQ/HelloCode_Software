package Nuevo_Modulo_Leccion.logic;

public class CalculoXPSinErrores extends CalculoXPBase {

    public CalculoXPSinErrores(long tiempoTranscurridoLeccion) {
        super(tiempoTranscurridoLeccion, 400, 800, 90_000, 300_000);
    }

    @Override
    public int calcularXP() {
        if (tiempoDeLeccion <= 0) {
            return XP_MINIMA;
        }

        if (tiempoDeLeccion <= TIEMPO_OPTIMO) {
            // Rápido → más XP
            double factor = 1 - ((double) tiempoDeLeccion / TIEMPO_OPTIMO);
            return (int) (XP_MINIMA + (XP_MAXIMA - XP_MINIMA) * factor);
        } else if (tiempoDeLeccion <= TIEMPO_MAXIMO) {
            // Tiempo aceptable → decae poco a poco
            double factor = 1 - ((double) (tiempoDeLeccion - TIEMPO_OPTIMO) / (TIEMPO_MAXIMO - TIEMPO_OPTIMO));
            return (int) (XP_MINIMA + (XP_MAXIMA - XP_MINIMA) * Math.max(factor, 0.2));
        } else {
            // Muy lento → mínima XP
            return XP_MINIMA;
        }
    }
}
