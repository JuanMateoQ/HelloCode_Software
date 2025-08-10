package Nuevo_Modulo_Leccion.logic;

public abstract class FiltroEjercicios {
    protected FiltroEjercicios filtroSiguiente;

    public void cambiarSiguiente(FiltroEjercicios filtroSiguiente) {
        this.filtroSiguiente = filtroSiguiente;
    }

    public abstract boolean FiltrarEjercicios();


}
