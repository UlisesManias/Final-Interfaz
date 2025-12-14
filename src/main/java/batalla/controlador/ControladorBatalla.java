package batalla.controlador;

import batalla.modelo.*;
import batalla.vista.PantallaBatalla;
import batalla.vista.PantallaResultado;

import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ControladorBatalla {

    private PantallaBatalla vista;
    private ConfiguracionPartida config;
    private Heroe heroe;
    private Villano villano;

    private int turno = 1;
    private int batallaActual = 1;
    private boolean turnoHeroe;
    private boolean pausado = false;

    private Random random = new Random();

    // Estadísticas
    private int mayorDanio = 0;
    private String personajeMayorDanio = "";
    private int batallaMasLarga = 0;
    private String ganadorBatallaMasLarga = "";
    private List<Integer> turnosPorBatalla = new ArrayList<>(); // Guardar turnos de cada batalla
    private List<String> ganadoresPorBatalla = new ArrayList<>(); // Guardar ganador de cada batalla

    private List<String> combatLog = new ArrayList<>();

    // =========================
    // CONSTRUCTORES
    // =========================

    public ControladorBatalla(PantallaBatalla vista, ConfiguracionPartida config) {
        this.vista = vista;
        this.config = config;
        this.heroe = (Heroe) config.getHeroe();
        this.villano = (Villano) config.getVillano();
        this.turnoHeroe = random.nextBoolean();
        configurarEventos();
    }

    public ControladorBatalla(PantallaBatalla vista, Heroe heroe, Villano villano) {
        this.vista = vista;
        this.heroe = heroe;
        this.villano = villano;
        this.turnoHeroe = random.nextBoolean();
        this.config = new ConfiguracionPartida();
        config.agregarPersonaje(heroe);
        config.agregarPersonaje(villano);
        configurarEventos();
    }

    // =========================
    // EVENTOS
    // =========================

    private void configurarEventos() {
        vista.getBtnIniciar().addActionListener(e -> iniciarBatallas());
        vista.getBtnPausa().addActionListener(e -> togglePausa());
    }

    private void togglePausa() {
        pausado = !pausado;
        vista.setPausado(pausado);
        vista.agregarLog(pausado ? "⏸ COMBATE PAUSADO" : "▶ COMBATE REANUDADO");

        if (!pausado && heroe.estaVivo() && villano.estaVivo()) {
            siguienteTurno();
        }
    }

    // =========================
    // INICIO
    // =========================

    public void iniciar() {
        vista.setVisible(true);
        vista.limpiarLog();
        vista.setInfoPartida("Batalla: 0/0");
        vista.setTurno("Turno: 0");
    }

    private void iniciarBatallas() {
        int cantidadBatallas = (int) vista.getSpnNumBatallas().getValue();
        boolean ataquesSupremos = vista.getChkAtkSupremos().isSelected();

        config.setCantidadBatallas(cantidadBatallas);
        config.setAtaquesSupremosActivados(ataquesSupremos);

        vista.getBtnIniciar().setEnabled(false);
        vista.getBtnPausa().setEnabled(true);

        batallaActual = 1;
        iniciarBatalla();
    }

    private void iniciarBatalla() {
        heroe.restaurarEstadisticasIniciales();
        villano.restaurarEstadisticasIniciales();

        if (heroe.getVida() <= 0)
            heroe.setVida(100);
        if (villano.getVida() <= 0)
            villano.setVida(100);

        vista.limpiarLog();
        combatLog.clear();

        turno = 1;
        turnoHeroe = random.nextBoolean();
        mayorDanio = 0;
        personajeMayorDanio = "";

        imprimirEncabezadoTabla();
        actualizarUI();

        siguienteTurno();
    }

    // =========================
    // COMBATE
    // =========================

    private void siguienteTurno() {
        if (pausado)
            return;

        if (!heroe.estaVivo() || !villano.estaVivo()) {
            finalizarBatalla();
            return;
        }

        ResultadoCombate resultado;

        if (turnoHeroe) {
            resultado = (config.isAtaquesSupremosActivados() && heroe.getBendiciones() >= 100)
                    ? heroe.usarAtaqueSupremo(villano)
                    : heroe.decidirAccion(villano);
        } else {
            resultado = (config.isAtaquesSupremosActivados()
                    && villano.getBendiciones() >= 100
                    && !villano.isLeviatanInvocado())
                            ? villano.invocarLeviatan()
                            : villano.decidirAccion(heroe);
        }

        if (resultado.getDanioRealizado() > mayorDanio) {
            mayorDanio = resultado.getDanioRealizado();
            personajeMayorDanio = resultado.getAtacanteNombre();
        }

        imprimirFilaTurno(resultado);

        turnoHeroe = !turnoHeroe;
        actualizarUI();
        turno++;

        Timer timer = new Timer(1000, e -> {
            ((Timer) e.getSource()).stop();
            siguienteTurno();
        });
        timer.setRepeats(false);
        timer.start();
    }

    // =========================
    // LOG EN TABLA
    // =========================

    private void imprimirEncabezadoTabla() {
        vista.agregarLog("================================================================================");
        vista.agregarLog("                               COMBAT LOG");
        vista.agregarLog("================================================================================");
        vista.agregarLog(String.format(
                "Batalla: %d / %d | Ataques Supremos: %s",
                batallaActual,
                config.getCantidadBatallas(),
                config.isAtaquesSupremosActivados() ? "ON" : "OFF"));
        vista.agregarLog("--------------------------------------------------------------------------------");
        vista.agregarLog(String.format(
                "%-5s | %-12s | %-15s | %-4s | %-4s | %-5s | %-6s | %-6s",
                "Turno", "Atacante", "Acción", "Daño", "Crit", "Parry", "VidaH", "VidaV"));
        vista.agregarLog("--------------------------------------------------------------------------------");
    }

    private void imprimirFilaTurno(ResultadoCombate r) {
        String fila = String.format(
                "%-5d | %-12s | %-15s | %-4d | %-4s | %-5s | %-6d | %-6d",
                turno,
                r.getAtacanteNombre(),
                r.getAccionNombre(),
                r.getDanioRealizado(),
                r.isEsCritico() ? "SI" : "NO",
                r.isEsParry() ? "SI" : "NO",
                heroe.getVida(),
                villano.getVida());

        vista.agregarLog(fila);
        combatLog.add(fila);
    }

    // =========================
    // FINALIZACIÓN
    // =========================

    private void finalizarBatalla() {
        Personaje ganador = heroe.estaVivo() ? heroe : villano;
        Personaje perdedor = heroe.estaVivo() ? villano : heroe;

        ganador.incrementarVictoria();
        perdedor.incrementarDerrota();

        vista.agregarLog("--------------------------------------------------------------------------------");
        vista.agregarLog("GANADOR: " + ganador.getNombre());
        vista.agregarLog("TURNOS TOTALES: " + (turno - 1));
        vista.agregarLog("MAYOR DAÑO: " + mayorDanio + " (" + personajeMayorDanio + ")");
        vista.agregarLog("================================================================================");

        // Guardar turnos de esta batalla
        turnosPorBatalla.add(turno - 1);
        // Guardar ganador de esta batalla
        ganadoresPorBatalla.add(ganador.getNombre());

        if (turno > batallaMasLarga) {
            batallaMasLarga = turno;
            ganadorBatallaMasLarga = ganador.getNombre();
        }

        batallaActual++;

        if (batallaActual > config.getCantidadBatallas()) {
            finalizarTodasLasBatallas();
        } else {
            Timer timer = new Timer(2000, e -> {
                ((Timer) e.getSource()).stop();
                iniciarBatalla();
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    private void finalizarTodasLasBatallas() {
        // Guardar directamente el héroe y villano que participaron en las batallas
        List<Personaje> personajesAGuardar = new ArrayList<>();
        personajesAGuardar.add(heroe);
        personajesAGuardar.add(villano);
        GestorPersistencia.guardarPersonajes(personajesAGuardar);

        PartidaGuardada partida = new PartidaGuardada();
        partida.setHeroeNombre(heroe.getNombre());
        partida.setHeroeApodo(heroe.getApodo());
        partida.setVillanoNombre(villano.getNombre());
        partida.setVillanoApodo(villano.getApodo());
        partida.setCantidadBatallas(config.getCantidadBatallas());
        partida.setAtaquesSupremosActivados(config.isAtaquesSupremosActivados());
        partida.setCombatLog(combatLog);

        PantallaResultado pantallaResultado = new PantallaResultado();
        ControladorResultado controladorResultado = new ControladorResultado(
                pantallaResultado,
                config.getPersonajes(),
                config.getCantidadBatallas(),
                partida,
                mayorDanio,
                personajeMayorDanio,
                batallaMasLarga,
                ganadorBatallaMasLarga,
                turnosPorBatalla,
                ganadoresPorBatalla);

        controladorResultado.iniciar();
        vista.dispose();
    }

    private void actualizarUI() {
        vista.setInfoPartida("Batalla: " + batallaActual + "/" + config.getCantidadBatallas());
        vista.setTurno("Turno: " + turno);
        vista.actualizarEstadoPersonaje(heroe);
        vista.actualizarEstadoPersonaje(villano);
    }
}
