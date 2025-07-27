package Modulo_Usuario.Clases;

/**
 * Base class for all users, containing common credentials.
 */
public abstract class UsuarioBase {
    private String username;
    private String password;

    // Constructor por defecto
    public UsuarioBase() {
        this.username = "";
        this.password = "";
    }

    public UsuarioBase(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
