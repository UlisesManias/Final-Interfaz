package batalla.modelo;

import java.util.ArrayList;
import java.util.List;

public class ResultadoCombate {
    private String atacanteNombre;
    private String accionNombre;
    private int danioBase;
    private int danioRealizado;
    private boolean esCritico;
    private boolean esParry;
    private boolean esAtaqueSupremo;
    private int defensaEnemigo;
    private int danioReflejado;
    private int bendicionesGanadas;
    private int totalBendiciones;
    private List<String> logs;

    public ResultadoCombate(String atacanteNombre, String accionNombre) {
        this.atacanteNombre = atacanteNombre;
        this.accionNombre = accionNombre;
        this.logs = new ArrayList<>();
    }

    public void agregarLog(String log) {
        this.logs.add(log);
    }

    // Getters and Setters
    public String getAtacanteNombre() {
        return atacanteNombre;
    }

    public String getAccionNombre() {
        return accionNombre;
    }

    public void setAccionNombre(String accionNombre) {
        this.accionNombre = accionNombre;
    }

    public int getDanioRealizado() {
        return danioRealizado;
    }

    public void setDanioRealizado(int danio) {
        this.danioRealizado = danio;
    }

    public boolean isEsCritico() {
        return esCritico;
    }

    public void setEsCritico(boolean esCritico) {
        this.esCritico = esCritico;
    }

    public boolean isEsParry() {
        return esParry;
    }

    public void setEsParry(boolean esParry) {
        this.esParry = esParry;
    }

    public int getDefensaEnemigo() {
        return defensaEnemigo;
    }

    public void setDefensaEnemigo(int defensa) {
        this.defensaEnemigo = defensa;
    }

    public int getDanioBase() {
        return danioBase;
    }

    public void setDanioBase(int danioBase) {
        this.danioBase = danioBase;
    }

    public int getDanioReflejado() {
        return danioReflejado;
    }

    public void setDanioReflejado(int danioReflejado) {
        this.danioReflejado = danioReflejado;
    }

    public int getBendicionesGanadas() {
        return bendicionesGanadas;
    }

    public void setBendicionesGanadas(int bendiciones) {
        this.bendicionesGanadas = bendiciones;
    }

    public int getTotalBendiciones() {
        return totalBendiciones;
    }

    public void setTotalBendiciones(int total) {
        this.totalBendiciones = total;
    }

    public boolean isEsAtaqueSupremo() {
        return esAtaqueSupremo;
    }

    public void setEsAtaqueSupremo(boolean esAtaqueSupremo) {
        this.esAtaqueSupremo = esAtaqueSupremo;
    }

    public List<String> getLogs() {
        return logs;
    }
}
