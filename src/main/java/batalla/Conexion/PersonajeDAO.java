package batalla.Conexion;

import batalla.modelo.Personaje;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonajeDAO {

    public PersonajeDAO() {
        crearTablaSiNoExiste();
    }

    private void crearTablaSiNoExiste() {
        String sql = "CREATE TABLE IF NOT EXISTS personajes ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nombre TEXT, "
                + "apodo TEXT UNIQUE, "
                + "tipo TEXT, "
                + "vida INTEGER, "
                + "fuerza INTEGER, "
                + "defensa INTEGER, "
                + "bendiciones INTEGER, "
                + "victorias INTEGER, "
                + "derrotas INTEGER, "
                + "supremos_usados INTEGER, "
                + "armas_invocadas INTEGER)";

        try (Connection conn = ConexionDB.conectar();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error al crear tabla personajes: " + e.getMessage());
        }
    }

    // INSERT - Agregar un personaje nuevo
    public void insertar(Personaje p) {

        String sql = "INSERT INTO personajes "
                + "(nombre, apodo, tipo, vida, fuerza, defensa, bendiciones, victorias, derrotas, supremos_usados, armas_invocadas) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getApodo());
            ps.setString(3, p.getTipo());
            ps.setInt(4, p.getVidaMaxima()); // vida inicial/máxima
            ps.setInt(5, p.getFuerza());
            ps.setInt(6, p.getDefensa());
            ps.setInt(7, p.getBendiciones());
            ps.setInt(8, p.getVictorias());
            ps.setInt(9, p.getDerrotas());
            ps.setInt(10, p.getAtaquesSupremosUsados());
            ps.setInt(11, p.getArmasInvocadas());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al insertar personaje: " + e.getMessage());
        }
    }

    // UPDATE - Actualizar estadísticas luego de una batalla
    public void actualizarEstadisticas(Personaje p) {

        String sql = "UPDATE personajes SET "
                + "vida = ?, fuerza = ?, defensa = ?, bendiciones = ?, victorias = ?, derrotas = ?, supremos_usados = ?, armas_invocadas = ? "
                + "WHERE apodo = ?";

        try (Connection conn = ConexionDB.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, p.getVidaMaxima());
            ps.setInt(2, p.getFuerza());
            ps.setInt(3, p.getDefensa());
            ps.setInt(4, p.getBendiciones());
            ps.setInt(5, p.getVictorias());
            ps.setInt(6, p.getDerrotas());
            ps.setInt(7, p.getAtaquesSupremosUsados());
            ps.setInt(8, p.getArmasInvocadas());
            ps.setString(9, p.getApodo());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al actualizar estadísticas: " + e.getMessage());
        }
    }

    // DELETE - Eliminar un personaje por apodo
    public void eliminar(String apodo) {
        String sql = "DELETE FROM personajes WHERE apodo = ?";

        try (Connection conn = ConexionDB.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, apodo);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al eliminar personaje: " + e.getMessage());
        }
    }

    // SELECT - Obtener un personaje por apodo (UNIQUE)
    public Personaje obtenerPorApodo(String apodo) {

        String sql = "SELECT * FROM personajes WHERE apodo = ?";
        Personaje p = null;

        try (Connection conn = ConexionDB.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, apodo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p = mapearPersonaje(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar personaje: " + e.getMessage());
        }

        return p;
    }

    // SELECT - Listar todos los personajes
    public List<Personaje> listarTodos() {

        List<Personaje> lista = new ArrayList<>();
        String sql = "SELECT * FROM personajes";

        try (Connection conn = ConexionDB.conectar();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearPersonaje(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar personajes: " + e.getMessage());
        }

        return lista;
    }

    // SELECT - Obtener ranking (ordenado por victorias)
    public List<Personaje> obtenerRanking() {

        List<Personaje> lista = new ArrayList<>();
        String sql = "SELECT * FROM personajes ORDER BY victorias DESC";

        try (Connection conn = ConexionDB.conectar();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearPersonaje(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener ranking: " + e.getMessage());
        }

        return lista;
    }

    // MÉTODO PRIVADO - Convertir fila SQL → Objeto Personaje
    private Personaje mapearPersonaje(ResultSet rs) throws SQLException {

        String tipo = rs.getString("tipo");
        Personaje p;

        // Crear instancia correcta según el tipo
        if ("Heroe".equals(tipo)) {
            p = new batalla.modelo.Heroe(
                    rs.getString("nombre"),
                    rs.getString("apodo"),
                    rs.getInt("vida"),
                    rs.getInt("fuerza"),
                    rs.getInt("defensa"),
                    rs.getInt("bendiciones"));
        } else {
            p = new batalla.modelo.Villano(
                    rs.getString("nombre"),
                    rs.getString("apodo"),
                    rs.getInt("vida"),
                    rs.getInt("fuerza"),
                    rs.getInt("defensa"),
                    rs.getInt("bendiciones"));
        }

        p.setId(rs.getInt("id"));

        // Restaurar estadísticas acumuladas
        int victorias = rs.getInt("victorias");
        int derrotas = rs.getInt("derrotas");
        int supremos = rs.getInt("supremos_usados");
        int armas = rs.getInt("armas_invocadas");

        for (int i = 0; i < victorias; i++)
            p.incrementarVictoria();
        for (int i = 0; i < derrotas; i++)
            p.incrementarDerrota();
        for (int i = 0; i < supremos; i++)
            p.incrementarAtaqueSupremo();
        for (int i = 0; i < armas; i++)
            p.incrementarArmaInvocada();

        return p;
    }

    // ================================================================
    // ASEGURAR PERSONAJE EN BD
    // Si existe → devuelve su ID
    // Si no existe → lo inserta y devuelve el ID nuevo
    // También actualiza el objeto Personaje con p.setId(id)
    // ================================================================
    public int asegurarPersonajeEnBD(Personaje p) {

        if (p == null)
            return -1;

        // 1) BUSCAR PERSONAJE POR APODO (que es UNIQUE)
        String sqlBuscar = "SELECT id FROM personajes WHERE apodo = ? LIMIT 1";

        try (Connection conn = ConexionDB.conectar();
                PreparedStatement ps = conn.prepareStatement(sqlBuscar)) {

            ps.setString(1, p.getApodo());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idExistente = rs.getInt("id");
                    p.setId(idExistente); // ← asignar ID al objeto
                    return idExistente;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar personaje: " + e.getMessage());
        }

        // 2) SI NO EXISTE → INSERTARLO
        String sqlInsert = "INSERT INTO personajes "
                + "(nombre, apodo, tipo, vida, fuerza, defensa, bendiciones, victorias, derrotas, supremos_usados, armas_invocadas) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.conectar();
                PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getApodo());
            ps.setString(3, p.getTipo());
            ps.setInt(4, p.getVidaMaxima());
            ps.setInt(5, p.getFuerza());
            ps.setInt(6, p.getDefensa());
            ps.setInt(7, p.getBendiciones());
            ps.setInt(8, p.getVictorias());
            ps.setInt(9, p.getDerrotas());
            ps.setInt(10, p.getAtaquesSupremosUsados());
            ps.setInt(11, p.getArmasInvocadas());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int nuevoId = rs.getInt(1);
                p.setId(nuevoId); // ← asignar ID al objeto
                return nuevoId;
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar personaje: " + e.getMessage());
        }

        return -1;
    }

}
