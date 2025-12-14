package batalla.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla 'historial_batallas'
 */
public class BatallaDAO {

    public BatallaDAO() {
        crearTablaSiNoExiste();
    }

    private void crearTablaSiNoExiste() {
        String sql = "CREATE TABLE IF NOT EXISTS batallas ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "heroe_id INTEGER, "
                + "villano_id INTEGER, "
                + "ganador_id INTEGER, "
                + "turnos INTEGER, "
                + "combat_log TEXT, "
                + "mayor_danio INTEGER, "
                + "armas_heroe INTEGER, "
                + "armas_villano INTEGER, "
                + "supremos_heroe INTEGER, "
                + "supremos_villano INTEGER, "
                + "winrate_heroe TEXT, "
                + "winrate_villano TEXT, "
                + "FOREIGN KEY(heroe_id) REFERENCES personajes(id), "
                + "FOREIGN KEY(villano_id) REFERENCES personajes(id), "
                + "FOREIGN KEY(ganador_id) REFERENCES personajes(id))";

        try (Connection conn = ConexionDB.conectar();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error al crear tabla batallas: " + e.getMessage());
        }
    }

    public boolean insertarBatalla(int heroeId, int villanoId, int ganadorId, int turnos,
            String combatLog, int mayorDanio,
            int armasHeroe, int armasVillano,
            int supremosHeroe, int supremosVillano,
            String winrateHeroe, String winrateVillano) {

        String sql = "INSERT INTO batallas ("
                + "heroe_id, villano_id, ganador_id, turnos, combat_log, "
                + "mayor_danio, armas_heroe, armas_villano, "
                + "supremos_heroe, supremos_villano, "
                + "winrate_heroe, winrate_villano"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, heroeId);
            ps.setInt(2, villanoId);
            ps.setInt(3, ganadorId);
            ps.setInt(4, turnos);
            ps.setString(5, combatLog);
            ps.setInt(6, mayorDanio);
            ps.setInt(7, armasHeroe);
            ps.setInt(8, armasVillano);
            ps.setInt(9, supremosHeroe);
            ps.setInt(10, supremosVillano);
            ps.setString(11, winrateHeroe);
            ps.setString(12, winrateVillano);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar batalla: " + e.getMessage());
            javax.swing.JOptionPane.showMessageDialog(null, "Error al guardar en BD: " + e.getMessage(), "Error SQL",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public List<String[]> listarTodasRows() {
        List<String[]> lista = new ArrayList<>();

        String sql = "SELECT b.id, b.fecha, "
                + "h.nombre as heroe_nombre, "
                + "v.nombre as villano_nombre, "
                + "g.nombre as ganador_nombre, "
                + "b.turnos "
                + "FROM batallas b "
                + "JOIN personajes h ON b.heroe_id = h.id "
                + "JOIN personajes v ON b.villano_id = v.id "
                + "JOIN personajes g ON b.ganador_id = g.id "
                + "ORDER BY b.id DESC LIMIT 10";

        try (Connection conn = ConexionDB.conectar();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String[] fila = new String[6];
                fila[0] = String.valueOf(rs.getInt("id"));
                fila[1] = rs.getString("fecha");
                fila[2] = rs.getString("heroe_nombre");
                fila[3] = rs.getString("villano_nombre");
                fila[4] = rs.getString("ganador_nombre");
                fila[5] = String.valueOf(rs.getInt("turnos"));

                lista.add(fila);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar todas las batallas: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Devuelve toda la información de una batalla por su id.
     */
    public BatallaInfo obtenerBatallaPorId(int idBatalla) {
        String sql = "SELECT b.*, "
                + "h.nombre as heroe_nombre, "
                + "v.nombre as villano_nombre, "
                + "g.nombre as ganador_nombre "
                + "FROM batallas b "
                + "JOIN personajes h ON b.heroe_id = h.id "
                + "JOIN personajes v ON b.villano_id = v.id "
                + "JOIN personajes g ON b.ganador_id = g.id "
                + "WHERE b.id = ?";

        try (Connection conn = ConexionDB.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idBatalla);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BatallaInfo info = new BatallaInfo();
                    info.setId(rs.getInt("id"));
                    info.setFecha(rs.getString("fecha"));
                    info.setHeroe(rs.getString("heroe_nombre")); // Usar alias del JOIN
                    info.setVillano(rs.getString("villano_nombre"));
                    info.setGanador(rs.getString("ganador_nombre"));
                    info.setTurnos(rs.getInt("turnos"));

                    info.setCombatLog(rs.getString("combat_log"));
                    info.setMayorDanio(rs.getInt("mayor_danio"));

                    info.setArmasHeroe(rs.getInt("armas_heroe"));
                    info.setArmasVillano(rs.getInt("armas_villano"));
                    info.setSupremosHeroe(rs.getInt("supremos_heroe"));
                    info.setSupremosVillano(rs.getInt("supremos_villano"));

                    info.setWinrateHeroe(rs.getString("winrate_heroe"));
                    info.setWinrateVillano(rs.getString("winrate_villano"));

                    return info;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener batalla por id: " + e.getMessage());
        }

        return null;
    }

    public void borrarHistorial() {
        String sql = "DELETE FROM historial_batallas";

        try (Connection conn = ConexionDB.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al borrar historial: " + e.getMessage());
        }
    }

    // ---------------- Clase auxiliar para devolver detalles de batalla
    public static class BatallaInfo {
        private int id;
        private String fecha;

        private String heroe;
        private String villano;
        private String ganador;
        private int turnos;

        private String combatLog;
        private int mayorDanio;
        private int armasHeroe;
        private int armasVillano;
        private int supremosHeroe;
        private int supremosVillano;
        private String winrateHeroe;
        private String winrateVillano;

        // Getters y Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getFecha() {
            return fecha;
        }

        public void setFecha(String fecha) {
            this.fecha = fecha;
        }

        public String getHeroe() {
            return heroe;
        }

        public void setHeroe(String heroe) {
            this.heroe = heroe;
        }

        public String getVillano() {
            return villano;
        }

        public void setVillano(String villano) {
            this.villano = villano;
        }

        public String getGanador() {
            return ganador;
        }

        public void setGanador(String ganador) {
            this.ganador = ganador;
        }

        public int getTurnos() {
            return turnos;
        }

        public void setTurnos(int turnos) {
            this.turnos = turnos;
        }

        public String getCombatLog() {
            return combatLog;
        }

        public void setCombatLog(String combatLog) {
            this.combatLog = combatLog;
        }

        public int getMayorDanio() {
            return mayorDanio;
        }

        public void setMayorDanio(int mayorDanio) {
            this.mayorDanio = mayorDanio;
        }

        public int getArmasHeroe() {
            return armasHeroe;
        }

        public void setArmasHeroe(int armasHeroe) {
            this.armasHeroe = armasHeroe;
        }

        public int getArmasVillano() {
            return armasVillano;
        }

        public void setArmasVillano(int armasVillano) {
            this.armasVillano = armasVillano;
        }

        public int getSupremosHeroe() {
            return supremosHeroe;
        }

        public void setSupremosHeroe(int supremosHeroe) {
            this.supremosHeroe = supremosHeroe;
        }

        public int getSupremosVillano() {
            return supremosVillano;
        }

        public void setSupremosVillano(int supremosVillano) {
            this.supremosVillano = supremosVillano;
        }

        public String getWinrateHeroe() {
            return winrateHeroe;
        }

        public void setWinrateHeroe(String winrateHeroe) {
            this.winrateHeroe = winrateHeroe;
        }

        public String getWinrateVillano() {
            return winrateVillano;
        }

        public void setWinrateVillano(String winrateVillano) {
            this.winrateVillano = winrateVillano;
        }
    }
}
