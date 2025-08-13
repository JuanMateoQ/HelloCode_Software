package Nuevo_Modulo_Leccion.logic;

public class CalculoXPQuedasteSinVidas extends CalculoXPBase {

    public CalculoXPQuedasteSinVidas(long tiempoTranscurridoLeccion) {
        super(tiempoTranscurridoLeccion,50,75,60_000,300_000 );
    }

    @Override
    public int calcularXP() {
        if (tiempoDeLeccion <= 0) {
            return XP_MINIMA;
        }

        if (tiempoDeLeccion <= TIEMPO_OPTIMO) {
            // Fue rápido pero perdió vidas
            double factor = 1 - ((double) tiempoDeLeccion / TIEMPO_OPTIMO);
            return (int) (XP_MINIMA + (XP_MAXIMA - XP_MINIMA) * factor);
        } else if (tiempoDeLeccion <= TIEMPO_MAXIMO) {
            // Tardó más, pero igual recibe algo
            double factor = 1 - ((double) (tiempoDeLeccion - TIEMPO_OPTIMO) / (TIEMPO_MAXIMO - TIEMPO_OPTIMO));
            return (int) (XP_MINIMA + (XP_MAXIMA - XP_MINIMA) * Math.max(factor, 0.1));
        } else {
            // Muy lento
            return XP_MINIMA;
        }
    }
}
