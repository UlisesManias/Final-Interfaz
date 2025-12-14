package batalla.modelo;

import batalla.Conexion.PersonajeDAO;
import batalla.Conexion.BatallaDAO;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que gestiona la persistencia de datos usando Base de Datos SQLite
 * Reemplaza la versión anterior que usaba archivos .txt
 */
public class GestorPersistencia {

    // -------------------------------------------------------
    // PERSONAJES - Métodos de compatibilidad
    // -------------------------------------------------------

    /**
     * Elimina un personaje de la base de datos por su apodo
     */
    public static void eliminarPersonaje(String apodo) {
        PersonajeDAO dao = new PersonajeDAO();
        dao.eliminar(apodo);
    }

    /**
     * Guarda un personaje en la base de datos
     * Si ya existe (mismo apodo), lo actualiza
     */
    public static void guardarPersonaje(Personaje p) {
        PersonajeDAO dao = new PersonajeDAO();

        // Verificar si existe
        Personaje existente = dao.obtenerPorApodo(p.getApodo());

        if (existente != null) {
            // Si existe, actualizar
            dao.actualizarEstadisticas(p);
        } else {
            // Si no existe, insertar
            dao.insertar(p);
        }
    }

    /**
     * Guarda una lista de personajes (sobrescribe/actualiza)
     */
    public static void guardarPersonajes(List<Personaje> personajes) {
        PersonajeDAO dao = new PersonajeDAO();

        for (Personaje p : personajes) {
            Personaje existente = dao.obtenerPorApodo(p.getApodo());

            if (existente != null) {
                dao.actualizarEstadisticas(p);
            } else {
                dao.insertar(p);
            }
        }
    }

    /**
     * Carga todos los personajes desde la base de datos
     */
    public static List<Personaje> cargarPersonajes() {
        PersonajeDAO dao = new PersonajeDAO();
        return dao.listarTodos();
    }

    /**
     * Carga solo héroes desde la base de datos
     */
    public static List<Heroe> cargarHeroes() {
        List<Heroe> heroes = new ArrayList<>();
        PersonajeDAO dao = new PersonajeDAO();
        List<Personaje> personajes = dao.listarTodos();

        for (Personaje p : personajes) {
            if (p instanceof Heroe) {
                heroes.add((Heroe) p);
            }
        }

        return heroes;
    }

    /**
     * Carga solo villanos desde la base de datos
     */
    public static List<Villano> cargarVillanos() {
        List<Villano> villanos = new ArrayList<>();
        PersonajeDAO dao = new PersonajeDAO();
        List<Personaje> personajes = dao.listarTodos();

        for (Personaje p : personajes) {
            if (p instanceof Villano) {
                villanos.add((Villano) p);
            }
        }

        return villanos;
    }

    /**
     * Carga el historial de batallas (últimas 5)
     */
    public static List<String> cargarHistorial() {
        BatallaDAO dao = new BatallaDAO();
        List<String[]> rows = dao.listarTodasRows();

        List<String> historial = new ArrayList<>();

        // Tomar solo las primeras 5
        int limite = Math.min(5, rows.size());
        for (int i = 0; i < limite; i++) {
            String[] row = rows.get(i);
            // row[0]=id, row[1]=fecha, row[2]=heroe, row[3]=villano, row[4]=ganador,
            // row[5]=turnos
            historial.add(String.format("BATALLA - Heroe: %s | Villano: %s | Ganador: %s | Turnos: %s",
                    row[2], row[3], row[4], row[5]));
        }

        return historial;
    }

    /**
     * Carga el historial completo de partidas
     */
    public static List<String> cargarHistorialPartidas() {
        BatallaDAO dao = new BatallaDAO();
        List<String[]> rows = dao.listarTodasRows();

        List<String> historial = new ArrayList<>();

        for (String[] row : rows) {
            historial.add(String.format("BATALLA - Heroe: %s | Villano: %s | Ganador: %s | Turnos: %s",
                    row[2], row[3], row[4], row[5]));
        }

        return historial;
    }

    /**
     * Clase auxiliar para almacenar el estado de la batalla
     */
    public static class EstadoBatalla implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private ConfiguracionPartida config;
        private int batallaActual;
        private int turnoActual;

        public EstadoBatalla(ConfiguracionPartida config, int batallaActual, int turnoActual) {
            this.config = config;
            this.batallaActual = batallaActual;
            this.turnoActual = turnoActual;
        }

        public ConfiguracionPartida getConfig() {
            return config;
        }

        public int getBatallaActual() {
            return batallaActual;
        }

        public int getTurnoActual() {
            return turnoActual;
        }
    }
}
