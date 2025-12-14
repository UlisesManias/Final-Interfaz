package batalla.controlador;

import batalla.Conexion.BatallaDAO;
import batalla.Conexion.PersonajeDAO;
import batalla.modelo.*;
import batalla.vista.PantallaResultado;
import batalla.vista.PantallaPrincipal;

import java.util.List;

/**
 * Controlador para la pantalla de resultados
 * Muestra el resultado de la batalla y el historial
 */
public class ControladorResultado {
    private PantallaResultado vista;
    private List<Personaje> personajes;
    private int totalBatallas;
    private Heroe heroe;
    private Villano villano;
    private String ganador;
    private int turnos;
    private List<Integer> turnosPorBatalla;
    private List<String> ganadoresPorBatalla;

    // Constructor para múltiples batallas con estadísticas completas
    public ControladorResultado(PantallaResultado vista, List<Personaje> personajes, int totalBatallas,
            PartidaGuardada partida, int mayorDanio, String personajeMayorDanio,
            int batallaMasLarga, String ganadorBatallaMasLarga, List<Integer> turnosPorBatalla,
            List<String> ganadoresPorBatalla) {
        this.vista = vista;
        this.personajes = personajes;
        this.totalBatallas = totalBatallas;
        this.turnosPorBatalla = turnosPorBatalla;
        this.ganadoresPorBatalla = ganadoresPorBatalla;

        // Inicializar campos para guardar partida
        this.heroe = (Heroe) personajes.stream().filter(p -> p instanceof Heroe).findFirst().orElse(null);
        this.villano = (Villano) personajes.stream().filter(p -> p instanceof Villano).findFirst().orElse(null);

        if (this.heroe != null && this.villano != null) {
            this.ganador = this.heroe.getVictorias() > this.villano.getVictorias() ? this.heroe.getNombre()
                    : this.villano.getNombre();
        }

        // Calcular total de turnos
        if (turnosPorBatalla != null) {
            this.turnos = turnosPorBatalla.stream().mapToInt(Integer::intValue).sum();
        } else {
            this.turnos = 0;
        }

        configurarEventos();
        mostrarReporteCompleto(partida, mayorDanio, personajeMayorDanio, batallaMasLarga, ganadorBatallaMasLarga,
                turnosPorBatalla, ganadoresPorBatalla);
    }

    // Constructor para múltiples batallas (compatibilidad - sin turnos por batalla)
    public ControladorResultado(PantallaResultado vista, List<Personaje> personajes, int totalBatallas,
            PartidaGuardada partida, int mayorDanio, String personajeMayorDanio,
            int batallaMasLarga, String ganadorBatallaMasLarga) {
        this.vista = vista;
        this.personajes = personajes;
        this.totalBatallas = totalBatallas;
        configurarEventos();
        mostrarReporteCompleto(partida, mayorDanio, personajeMayorDanio, batallaMasLarga, ganadorBatallaMasLarga, null,
                null);
    }

    // Constructor para múltiples batallas (compatibilidad)
    public ControladorResultado(PantallaResultado vista, List<Personaje> personajes, int totalBatallas) {
        this.vista = vista;
        this.personajes = personajes;
        this.totalBatallas = totalBatallas;
        configurarEventos();
        mostrarReporteCompleto(null, 0, "", 0, "", null, null);
    }

    // Constructor de compatibilidad para una sola batalla
    public ControladorResultado(PantallaResultado vista, Heroe heroe, Villano villano, String ganador, int turnos) {
        this.vista = vista;
        this.heroe = heroe;
        this.villano = villano;
        this.ganador = ganador;
        this.turnos = turnos;
        configurarEventos();
        mostrarResultados();
    }

    private PartidaGuardada partidaGuardada;
    // Estadísticas y metadatos usados en el reporte y al guardar partidas
    private int mayorDanio;
    private String personajeMayorDanio;
    private int batallaMasLarga;
    private String ganadorBatallaMasLarga;

    private void configurarEventos() {
        vista.getBtnVolver().addActionListener(e -> volverPrincipal());
        vista.getBtnAgain().addActionListener(e -> nuevaBatalla());
        vista.getBtnGuardarPartida().addActionListener(e -> guardarPartida());
    }

    private void mostrarResultados() {
        vista.limpiar();

        // Mostrar resultado de la batalla
        vista.agregarResultado("=== RESULTADO DE LA BATALLA ===");
        vista.agregarResultado("");
        vista.agregarResultado("Ganador: " + ganador);
        vista.agregarResultado("Turnos totales: " + turnos);
        vista.agregarResultado("");
        vista.agregarResultado("Estadísticas finales:");
        vista.agregarResultado(heroe.getNombre() + ": " + heroe.getVida() + " vida restante");
        vista.agregarResultado(villano.getNombre() + ": " + villano.getVida() + " vida restante");
        vista.agregarResultado("");

        // Mostrar armas utilizadas
        vista.agregarResultado("Armas utilizadas:");
        if (heroe.getArma() != null) {
            vista.agregarResultado(heroe.getNombre() + ": " + heroe.getArma().getNombre());
        } else {
            vista.agregarResultado(heroe.getNombre() + ": Ninguna");
        }

        if (villano.getArma() != null) {
            vista.agregarResultado(villano.getNombre() + ": " + villano.getArma().getNombre());
        } else {
            vista.agregarResultado(villano.getNombre() + ": Ninguna");
        }

        // Mostrar historial (simplificado)
        vista.agregarHistorial("Batalla: " + heroe.getNombre() + " vs " + villano.getNombre());
        vista.agregarHistorial("Ganador: " + ganador);
        vista.agregarHistorial("Turnos: " + turnos);

        // Construir objeto PartidaGuardada básico para permitir guardar la partida
        // individual
        partidaGuardada = new PartidaGuardada();
        partidaGuardada.setHeroeNombre(heroe.getNombre());
        partidaGuardada.setVillanoNombre(villano.getNombre());
        partidaGuardada.setCantidadBatallas(1);
        // Agregar un combat log mínimo con información esencial (si el juego tuviera un
        // log real, usarlo)
        partidaGuardada.getCombatLog().add("Resultado: " + ganador + " en " + turnos + " turnos");
        partidaGuardada.getCombatLog().add(heroe.getNombre() + " vida final: " + heroe.getVida());
        partidaGuardada.getCombatLog().add(villano.getNombre() + " vida final: " + villano.getVida());
    }

    private void mostrarReporteCompleto(PartidaGuardada partida, int mayorDanio, String personajeMayorDanio,
            int batallaMasLarga, String ganadorBatallaMasLarga, List<Integer> turnosPorBatalla,
            List<String> ganadoresPorBatalla) {
        this.partidaGuardada = partida;
        this.mayorDanio = mayorDanio;
        this.personajeMayorDanio = personajeMayorDanio;
        this.batallaMasLarga = batallaMasLarga;
        this.ganadorBatallaMasLarga = ganadorBatallaMasLarga;
        vista.limpiar();

        // Mostrar datos en tabla - una fila por batalla
        Object[][] datos = new Object[totalBatallas][5];
        String[] columnas = { "N° Batalla", "Héroe", "Villano", "Ganador", "Turnos" };

        // Por ahora, mostrar datos básicos - se puede mejorar con datos reales de cada
        // batalla
        for (int i = 0; i < totalBatallas && i < datos.length; i++) {
            Personaje h = personajes.stream().filter(p -> p instanceof Heroe).findFirst().orElse(null);
            Personaje v = personajes.stream().filter(p -> p instanceof Villano).findFirst().orElse(null);

            if (h != null && v != null) {
                datos[i][0] = i + 1;
                datos[i][1] = h.getNombre();
                datos[i][2] = v.getNombre();

                // Usar ganador real de cada batalla si está disponible
                if (ganadoresPorBatalla != null && i < ganadoresPorBatalla.size()) {
                    datos[i][3] = ganadoresPorBatalla.get(i);
                } else {
                    // Fallback a lógica anterior (menos precisa para batallas individuales)
                    datos[i][3] = h.getVictorias() > v.getVictorias() ? h.getNombre() : v.getNombre();
                }
                // Usar turnos reales de cada batalla si están disponibles
                if (turnosPorBatalla != null && i < turnosPorBatalla.size()) {
                    datos[i][4] = turnosPorBatalla.get(i);
                } else {
                    datos[i][4] = "N/A";
                }
            }
        }

        vista.actualizarTabla(datos, columnas);

        // Mostrar estadísticas
        vista.agregarResultado("=== ESTADÍSTICAS GENERALES ===");
        vista.agregarResultado("Total de batallas: " + totalBatallas);
        if (mayorDanio > 0) {
            vista.agregarResultado("Mayor daño en un solo ataque: " + mayorDanio + " (" + personajeMayorDanio + ")");
        }
        if (batallaMasLarga > 0) {
            vista.agregarResultado(
                    "Batalla más larga: " + batallaMasLarga + " turnos (Ganador: " + ganadorBatallaMasLarga + ")");
        }

        // Calcular totales
        int totalArmasHeroe = 0;
        int totalArmasVillano = 0;
        int totalSupremosHeroe = 0;
        int totalSupremosVillano = 0;

        for (Personaje p : personajes) {
            if (p instanceof Heroe) {
                totalArmasHeroe += p.getArmasInvocadas();
                totalSupremosHeroe += p.getAtaquesSupremosUsados();
            } else {
                totalArmasVillano += p.getArmasInvocadas();
                totalSupremosVillano += p.getAtaquesSupremosUsados();
            }
        }

        vista.agregarResultado("Total armas invocadas héroe: " + totalArmasHeroe);
        vista.agregarResultado("Total armas invocadas villano: " + totalArmasVillano);
        vista.agregarResultado("Ataques supremos ejecutados héroe: " + totalSupremosHeroe);
        vista.agregarResultado("Ataques supremos ejecutados villano: " + totalSupremosVillano);

        // Cargar historial
        List<String> historial = GestorPersistencia.cargarHistorial();
        vista.agregarHistorial("=== HISTORIAL DE BATALLAS ===");
        for (String batalla : historial) {
            vista.agregarHistorial(batalla);
        }
    }

    private void guardarPartida() {

        if (heroe == null || villano == null) {
            javax.swing.JOptionPane.showMessageDialog(vista,
                    "No hay datos de batalla para guardar.",
                    "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ================================
        // 1) Asegurar que los Personajes existen en la BD y actualizar sus estadísticas
        // ================================
        PersonajeDAO pdao = new PersonajeDAO();
        pdao.asegurarPersonajeEnBD(heroe);
        pdao.asegurarPersonajeEnBD(villano);

        // Actualizar estadísticas acumuladas (victorias, derrotas, etc.)
        pdao.actualizarEstadisticas(heroe);
        pdao.actualizarEstadisticas(villano);

        // ================================
        // 2) Preparar datos para historial_batallas
        // ================================
        BatallaDAO batallaDAO = new BatallaDAO();
        int guardadas = 0;

        // Winrates calculados "al momento"
        int victoriasH = heroe.getVictorias();
        int batallasH = heroe.getVictorias() + heroe.getDerrotas();
        double winrateH = batallasH > 0 ? (double) victoriasH / batallasH * 100 : 0;

        int victoriasV = villano.getVictorias();
        int batallasV = villano.getVictorias() + villano.getDerrotas();
        double winrateV = batallasV > 0 ? (double) victoriasV / batallasV * 100 : 0;

        String winrateHeroeStr = String.format("%.2f%%", winrateH);
        String winrateVillanoStr = String.format("%.2f%%", winrateV);

        // Si tenemos datos detallados por batalla (modo varias batallas)
        if (ganadoresPorBatalla != null && turnosPorBatalla != null) {
            for (int i = 0; i < totalBatallas; i++) {
                if (i < ganadoresPorBatalla.size() && i < turnosPorBatalla.size()) {
                    String nombreGanador = ganadoresPorBatalla.get(i);
                    int turnosBatalla = turnosPorBatalla.get(i);

                    // Nota: En modo multi-batalla, estos contadores deberían ser por batalla,
                    // pero aquí tenemos los acumulados en el objeto Personaje.
                    // Para simplificar, dividimos o usamos el total (la UI mostrará total sesión).
                    // Para ser más precisos, guardamos el total acumulado hasta ese punto.

                    // Simples estimaciones de "armas por batalla" si no las trackeamos
                    // individualmente
                    int armasH = heroe.getArmasInvocadas() / totalBatallas;
                    int armasV = villano.getArmasInvocadas() / totalBatallas;
                    int supremosH = heroe.getAtaquesSupremosUsados() / totalBatallas;
                    int supremosV = villano.getAtaquesSupremosUsados() / totalBatallas;

                    // Combat log: concatenamos el resumen si existe
                    String logBatalla = "";
                    if (partidaGuardada != null && partidaGuardada.getCombatLog() != null) {
                        logBatalla = String.join("\n", partidaGuardada.getCombatLog());
                    }

                    // Obtener IDs
                    int heroeId = heroe.getId();
                    int villanoId = villano.getId();
                    int ganadorId = (nombreGanador.equals(heroe.getNombre())) ? heroeId : villanoId;

                    if (batallaDAO.insertarBatalla(heroeId, villanoId, ganadorId, turnosBatalla,
                            logBatalla, mayorDanio, armasH, armasV, supremosH, supremosV, winrateHeroeStr,
                            winrateVillanoStr)) {
                        guardadas++;
                    }
                }
            }
        } else {
            // Caso batalla individual
            String logBatalla = "";
            if (partidaGuardada != null && partidaGuardada.getCombatLog() != null) {
                logBatalla = String.join("\n", partidaGuardada.getCombatLog());
            } else {
                // Generar log básico si no hay
                logBatalla = "Batalla: " + heroe.getNombre() + " vs " + villano.getNombre() + "\n" +
                        "Ganador: " + ganador + "\n" +
                        "Turnos: " + turnos;
            }

            // Obtener IDs
            int heroeId = heroe.getId();
            int villanoId = villano.getId();
            int ganadorId = (ganador.equals(heroe.getNombre())) ? heroeId : villanoId;

            if (batallaDAO.insertarBatalla(heroeId, villanoId, ganadorId, turnos,
                    logBatalla, mayorDanio, heroe.getArmasInvocadas(), villano.getArmasInvocadas(),
                    heroe.getAtaquesSupremosUsados(), villano.getAtaquesSupremosUsados(),
                    winrateHeroeStr, winrateVillanoStr)) {
                guardadas = 1;
            }
        }

        if (guardadas > 0) {
            javax.swing.JOptionPane.showMessageDialog(vista,
                    "Se han guardado " + guardadas + " batallas en el historial.",
                    "Éxito",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
        } else {
            javax.swing.JOptionPane.showMessageDialog(vista,
                    "Error al guardar las batallas.",
                    "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void volverPrincipal() {
        PantallaPrincipal pantallaPrincipal = new PantallaPrincipal();
        ControladorPrincipal controladorPrincipal = new ControladorPrincipal(pantallaPrincipal);
        controladorPrincipal.iniciar();
        vista.dispose();
    }

    private void nuevaBatalla() {
        batalla.vista.PantallaCreacion pantallaCreacion = new batalla.vista.PantallaCreacion();
        ControladorCreacion controladorCreacion = new ControladorCreacion(pantallaCreacion);
        controladorCreacion.iniciar();
        vista.dispose();
    }

    public void iniciar() {
        vista.setVisible(true);
    }
}
