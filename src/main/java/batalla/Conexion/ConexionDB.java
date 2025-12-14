package batalla.Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class ConexionDB {

    private static final String URL = "jdbc:sqlite:src/main/java/batalla/database/BDjuego.db";

    public static Connection conectar() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(URL);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Driver SQLite no encontrado\n" + e.getMessage(), "Error Drivers",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.out.println("Error al conectar a SQLite: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error al conectar a SQLite: " + e.getMessage(), "Error Conexión",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }

    }
}
