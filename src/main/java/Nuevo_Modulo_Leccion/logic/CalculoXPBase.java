package Nuevo_Modulo_Leccion.logic;

public abstract class CalculoXPBase implements CalculoXP{
    protected long tiempoDeLeccion;
    protected int XP_MINIMA;
    protected int XP_MAXIMA;
    protected long TIEMPO_OPTIMO;
    protected long TIEMPO_MAXIMO;

    public CalculoXPBase(long tiempoDeLeccion, int XP_MINIMA, int XP_MAXIMA, long TIEMPO_OPTIMO, long TIEMPO_MAXIMO) {
        this.tiempoDeLeccion = tiempoDeLeccion;
        this.XP_MINIMA = XP_MINIMA;
        this.XP_MAXIMA = XP_MAXIMA;
        this.TIEMPO_OPTIMO = TIEMPO_OPTIMO;
        this.TIEMPO_MAXIMO = TIEMPO_MAXIMO;
    }

}
