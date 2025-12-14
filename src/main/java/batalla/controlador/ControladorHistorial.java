package batalla.controlador;

import batalla.Conexion.BatallaDAO;

import batalla.vista.PantallaHistorial;
import batalla.vista.PantallaPrincipal;
import batalla.vista.formHistorial;
import java.util.List;
import javax.swing.JOptionPane;

public class ControladorHistorial {

    private final PantallaHistorial vista;
    private final BatallaDAO batallaDAO;

    public ControladorHistorial(PantallaHistorial vista) {
        this.vista = vista;
        this.batallaDAO = new BatallaDAO();
        inicializar();
    }

    private void inicializar() {
        cargarTabla();
        configurarEventos();
    }

    private void cargarTabla() {
        List<String[]> historial = batallaDAO.listarTodasRows();
        // Columnas: "N° Batalla", "Heroe", "Villano", "Ganador", "N° Turnos"
        // lista trae: { id, fecha, heroe, villano, ganador, turnos }
        // Adaptamos para que coincida con lo que espera la vista

        Object[][] datos = new Object[historial.size()][5];

        for (int i = 0; i < historial.size(); i++) {
            String[] fila = historial.get(i);
            datos[i][0] = fila[0]; // ID
            datos[i][1] = fila[2]; // Heroe
            datos[i][2] = fila[3]; // Villano
            datos[i][3] = fila[4]; // Ganador
            datos[i][4] = fila[5]; // Turnos
        }

        String[] columnas = { "N° Batalla", "Heroe", "Villano", "Ganador", "N° Turnos" };
        vista.actualizarTabla(datos, columnas);
    }

    private void configurarEventos() {
        vista.getBtnVolver().addActionListener(e -> volver());
        vista.getBtnCargarPartida().addActionListener(e -> cargarDetallePartida());
        vista.getBtnBorrarPartida().addActionListener(e -> borrarPartida());
    }

    private void volver() {
        PantallaPrincipal p = new PantallaPrincipal();
        ControladorPrincipal ctrl = new ControladorPrincipal(p);
        ctrl.iniciar();
        vista.dispose();
    }

    private void cargarDetallePartida() {
        int filaSeleccionada = vista.getFilaSeleccionada();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione una batalla para ver detalles.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener ID de la tabla (asumiendo que está en la columna 0)
        String idStr = (String) vista.getTable().getValueAt(filaSeleccionada, 0);
        int idBatalla = Integer.parseInt(idStr);

        BatallaDAO.BatallaInfo info = batallaDAO.obtenerBatallaPorId(idBatalla);
        if (info != null) {
            formHistorial form = new formHistorial();
            ControladorFormHistorial ctrl = new ControladorFormHistorial(form, info);
            ctrl.iniciar();
            // No cerramos la pantalla de historial, solo abrimos el detalle encima
        } else {
            JOptionPane.showMessageDialog(vista, "No se pudo cargar la información de la batalla.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void borrarPartida() {
        JOptionPane.showMessageDialog(vista, "Funcionalidad de borrar partida individual no implementada en DAO aún.",
                "Info", JOptionPane.INFORMATION_MESSAGE);
        // Podríamos implementar batallaDAO.borrarBatalla(id) luego
    }

    public void iniciar() {
        vista.setVisible(true);
    }
}
