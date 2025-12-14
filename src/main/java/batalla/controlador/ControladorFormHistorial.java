package batalla.controlador;

import batalla.Conexion.BatallaDAO;
import batalla.vista.formHistorial;

public class ControladorFormHistorial {

    private final formHistorial vista;
    private final BatallaDAO.BatallaInfo info;

    public ControladorFormHistorial(formHistorial vista, BatallaDAO.BatallaInfo info) {
        this.vista = vista;
        this.info = info;

        configurarEventos();
        cargarDatos();
    }

    private void configurarEventos() {
        vista.getBtnCerrar().addActionListener(e -> vista.dispose());
    }

    private void cargarDatos() {

        // -------- ENCABEZADOS --------
        // Ajuste a los nuevos métodos de la clase interna BatallaInfo
        vista.setHeroeNombre(info.getHeroe());
        vista.setVillanoNombre(info.getVillano());
        vista.setGanador(info.getGanador());
        vista.setTurnos(String.valueOf(info.getTurnos()));

        // -------- COMBAT LOG --------
        if (info.getCombatLog() != null && !info.getCombatLog().isEmpty()) {
            vista.setCombatLog(info.getCombatLog());
        } else {
            vista.setCombatLog("Sin registro detallado.");
        }

        // -------- ESTADÍSTICAS --------
        vista.setMayorDanio(String.valueOf(info.getMayorDanio()));
        // vista.setBatallaMasLarga(info.getTurnos() + " turnos"); // "batalla mas
        // larga" es un record global, info.turnos es de esta batalla
        vista.setBatallaMasLarga(String.valueOf(info.getTurnos())); // Reutilizamos el label para mostrar turnos de esta
                                                                    // batalla si se desea, o lo dejamos así.

        vista.setArmasHeroe(String.valueOf(info.getArmasHeroe()));
        vista.setArmasVillano(String.valueOf(info.getArmasVillano()));
        vista.setSupremosHeroe(String.valueOf(info.getSupremosHeroe()));
        vista.setSupremosVillano(String.valueOf(info.getSupremosVillano()));

        vista.setWinrateHeroe(info.getWinrateHeroe());
        vista.setWinrateVillano(info.getWinrateVillano());
    }

    public void iniciar() {
        vista.setVisible(true);
    }
}
