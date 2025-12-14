package batalla.modelo;

/**
 * Clase que representa un héroe en el juego
 */
public class Heroe extends Personaje {
    public Heroe(String nombre, String apodo, int vida, int fuerza, int defensa, int bendiciones) {
        super(nombre, apodo, "Heroe", vida, fuerza, defensa, bendiciones);
    }

    @Override
    public void invocarArma() {
        if (this.arma == null) {
            BendicionCelestial bendicion = new BendicionCelestial();
            this.arma = bendicion.invocarArma(this.bendiciones);

            if (this.arma != null) {
                System.out.println(this.nombre + " invoca: " + this.arma.getNombre() + "!");
                incrementarArmaInvocada();
            } else {
                System.out.println(this.nombre + " no tiene suficientes bendiciones para invocar un arma.");
            }
        } else {
            System.out.println(this.nombre + " ya tiene un arma: " + this.arma.getNombre());
        }
    }

    @Override
    public ResultadoCombate decidirAccion(Personaje enemigo) {
        if (this.arma == null && this.bendiciones >= 30) {
            invocarArma();
            ResultadoCombate res = new ResultadoCombate(this.nombre, "Invocación");
            res.agregarLog(this.nombre + " ha invocado un arma!");
            return res;
        } else {
            return atacar(enemigo);
        }
    }

    /**
     * Ataque supremo del héroe: "Castigo Bendito"
     */
    public ResultadoCombate usarAtaqueSupremo(Personaje enemigo) {
        ResultadoCombate resultado = new ResultadoCombate(this.nombre, "Ataque Supremo");
        resultado.setEsAtaqueSupremo(true);

        try {
            if (enemigo == null) {
                resultado.agregarLog("Error: No se puede usar ataque supremo contra un enemigo inexistente.");
                return resultado;
            }

            if (this.bendiciones >= 100) {
                int danioSupremo = (this.vida > 0) ? this.vida / 2 : 0;

                try {
                    enemigo.setVida(enemigo.getVida() - danioSupremo);
                } catch (Exception e) {
                    resultado.agregarLog("Error al aplicar daño: " + e.getMessage());
                    return resultado;
                }

                resultado.setDanioRealizado(danioSupremo);
                resultado.agregarLog("CASTIGO BENDITO!!!");
                resultado.agregarLog(this.nombre + " canaliza toda su energía divina!");
                resultado.agregarLog("El poder celestial desciende sobre " + enemigo.getNombre() + "!");
                resultado.agregarLog("Daño infligido: " + danioSupremo + " puntos!");
                resultado.agregarLog(enemigo.getNombre() + " ahora tiene " + enemigo.getVida() + " puntos de vida.");

                this.bendiciones = 0;
                incrementarAtaqueSupremo();
                resultado.agregarLog(this.nombre + " ha agotado todas sus bendiciones divinas!");
                resultado.setTotalBendiciones(0);

                return resultado;
            } else {
                resultado.agregarLog(this.nombre + " no tiene suficientes bendiciones para usar el ataque supremo.");
                resultado.agregarLog("Bendiciones necesarias: 100% | Bendiciones actuales: " + this.bendiciones + "%");
                return resultado;
            }
        } catch (Exception e) {
            resultado.agregarLog("Error durante el ataque supremo: " + e.getMessage());
            return resultado;
        }
    }
}
