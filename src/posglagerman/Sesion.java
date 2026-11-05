package posglagerman;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Sesion {
    public static int idUsuario = -1;
    public static String username = null;
    public static String rol = null;

    // Crea la tabla si no existe e inserta el admin por defecto
    public static void inicializarBD() {
        try (Connection conex = ConexionDB.getConexion();
             Statement st = conex.createStatement()) {
            
            st.execute("CREATE TABLE IF NOT EXISTS Usuario (" +
                       "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                       "username TEXT UNIQUE NOT NULL, " +
                       "password TEXT NOT NULL, " +
                       "rol TEXT NOT NULL)");
            
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Usuario");
            if (rs.next() && rs.getInt(1) == 0) {
                st.execute("INSERT INTO Usuario (username, password, rol) VALUES ('admin', 'admin123', 'ADMIN')");
            }
        } catch (Exception e) {
            System.err.println("Error al inicializar tabla Usuario: " + e.getMessage());
        }
    }

    public static boolean login(String user, String pass) {
        String sql = "SELECT id_usuario, rol FROM Usuario WHERE username = ? AND password = ?";
        try (Connection conex = ConexionDB.getConexion();
             PreparedStatement ps = conex.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.setString(2, pass);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idUsuario = rs.getInt("id_usuario");
                    username = user;
                    rol = rs.getString("rol");
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Error en login: " + e.getMessage());
        }
        return false;
    }

    public static void cerrarSesion() {
        idUsuario = -1;
        username = null;
        rol = null;
    }

    public static boolean haySesionActiva() {
        return username != null;
    }
}