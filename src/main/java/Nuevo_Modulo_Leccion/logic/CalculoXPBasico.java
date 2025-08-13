package Nuevo_Modulo_Leccion.logic;

public class CalculoXPBasico extends CalculoXPBase {

    public CalculoXPBasico(long tiempoTranscurridoLeccion) {
        super(tiempoTranscurridoLeccion, 200, 500, 120_000, 600_000);
    }

    @Override
    public int calcularXP() {
        if (tiempoDeLeccion <= 0) {
            return XP_MINIMA;
        }

        if (tiempoDeLeccion <= TIEMPO_OPTIMO) {
            // Un poco más simple que el avanzado
            double factor = 1 - ((double) tiempoDeLeccion / TIEMPO_OPTIMO);
            return (int) (XP_MINIMA + (XP_MAXIMA - XP_MINIMA) * (0.5 + factor / 2));
            // Esto asegura que incluso rápido, no da el tope máximo tan fácil
        } else if (tiempoDeLeccion <= TIEMPO_MAXIMO) {
            // Caída más suave
            double factor = 1 - ((double) (tiempoDeLeccion - TIEMPO_OPTIMO) / (TIEMPO_MAXIMO - TIEMPO_OPTIMO));
            return (int) (XP_MINIMA + (XP_MAXIMA - XP_MINIMA) * Math.max(factor, 0.4));
        } else {
            // Muy lento
            return XP_MINIMA;
        }
    }
}
