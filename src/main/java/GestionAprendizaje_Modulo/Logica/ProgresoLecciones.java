package GestionAprendizaje_Modulo.Logica;

import MetodosGlobales.SesionManager;
import Modulo_Usuario.Clases.Usuario;

/**
 * =================================================================================
 * Clase de Utilidad para el Progreso - Versión Final
 * =================================================================================
 * Esta clase actúa como un "atajo" o una fachada simple para consultar el progreso
 * del usuario actualmente logueado.
 *
 * Ya no almacena su propio estado (el contador estático ha sido eliminado).
 * En su lugar, delega toda la lógica al AprendizajeManager, asegurando que los
 * datos de progreso sean siempre consistentes y específicos para el usuario
 * de la sesión actual.
 */
public class ProgresoLecciones {

    // Esta clase ya no necesita ninguna variable. Es puramente funcional.

    /**
     * Devuelve el número de lecciones completadas POR EL USUARIO ACTUAL.
     * Es un método estático, por lo que se puede llamar desde cualquier lugar
     * sin necesidad de crear una instancia de la clase.
     *
     * @return La cantidad de lecciones que el usuario logueado ha completado.
     */
    public static int getLeccionesCompletadas() {
        // 1. Obtener el usuario que tiene la sesión iniciada desde el gestor global.
        Usuario usuarioActual = SesionManager.getInstancia().getUsuarioAutenticado();

        // 2. Comprobación de seguridad: si no hay ningún usuario en la sesión,
        //    el progreso es, por definición, cero.
        if (usuarioActual == null) {
            System.err.println("[ProgresoLecciones] Se intentó obtener el progreso, pero no hay usuario en la sesión.");
            return 0;
        }

        // 3. Pedirle al manager, que es el cerebro de la lógica, que nos dé el
        //    progreso para ESE usuario específico.
        return AprendizajeManager.getInstancia().getLeccionesCompletadasParaUsuario(usuarioActual);
    }

    // El método `incrementarLeccionesCompletadas()` se ha vuelto obsoleto y ha sido eliminado.
    // La lógica de marcar un nodo como completado ahora es responsabilidad del
    // RutaController, que llama directamente a `AprendizajeManager.marcarNodoComoCompletado()`.
    // Esto es más preciso, ya que se registra el progreso de un NODO específico,
    // no solo se incrementa un contador genérico.
}
