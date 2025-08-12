package Modulo_Ejercicios.otrosModulos;

public class Usuario {
    private static int vidas = 2; // Número de vidas del usuario

    public static int getVidas() {
        return vidas;
    }

    public static void restarVida() {
        if (vidas > 0) {
            vidas--;
        }
    }

}
