package batalla.modelo;

/**
 * Clase que representa un villano en el juego
 */
public class Villano extends Personaje {
    private int turnosLeviatan = 0;
    private boolean leviatanInvocado = false;

    public Villano(String nombre, String apodo, int vida, int fuerza, int defensa, int bendiciones) {
        super(nombre, apodo, "Villano", vida, fuerza, defensa, bendiciones);
    }

    @Override
    public void invocarArma() {
        if (this.arma == null) {
            BendicionDelVacio bendicion = new BendicionDelVacio();
            this.arma = bendicion.invocarArma(this.bendiciones);

            if (this.arma != null) {
                System.out.println(this.nombre + " invoca: " + this.arma.getNombre() + "!");
                incrementarArmaInvocada();
            } else {
                System.out.println(this.nombre + " no tiene suficientes maldiciones para invocar un arma.");
            }
        } else {
            System.out.println(this.nombre + " ya tiene un arma: " + this.arma.getNombre());
        }
    }

    @Override
    public ResultadoCombate decidirAccion(Personaje enemigo) {
        if (leviatanInvocado && turnosLeviatan < 3) {
            return continuarInvocacionLeviatan();
        }

        if (leviatanInvocado && turnosLeviatan >= 3) {
            return ejecutarLeviatan(enemigo);
        }

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
     * Ataque supremo del villano: "Leviatán del Vacío"
     */
    public ResultadoCombate invocarLeviatan() {
        ResultadoCombate resultado = new ResultadoCombate(this.nombre, "Invocación Leviatán");
        resultado.setEsAtaqueSupremo(true);

        try {
            if (this.bendiciones >= 100 && !leviatanInvocado) {
                leviatanInvocado = true;
                turnosLeviatan = 1;

                resultado.agregarLog("INVOCACIÓN DEL LEVIATÁN DEL VACÍO!!!");
                resultado.agregarLog(this.nombre + " comienza a canalizar las fuerzas del abismo!");
                resultado.agregarLog("El Leviatán se está materializando... (Turno 1/3)");
                resultado.agregarLog("Las aguas se vuelven turbulentas y la oscuridad se intensifica...");

                this.bendiciones = 0;
                incrementarAtaqueSupremo();
                resultado.agregarLog(this.nombre + " ha agotado todas sus maldiciones del vacío!");
                resultado.setTotalBendiciones(0);

                return resultado;
            } else if (leviatanInvocado) {
                resultado.agregarLog(this.nombre + " ya está invocando al Leviatán del Vacío!");
                return resultado;
            } else {
                resultado.agregarLog(this.nombre + " no tiene suficientes maldiciones para invocar al Leviatán.");
                resultado.agregarLog("Maldiciones necesarias: 100% | Maldiciones actuales: " + this.bendiciones + "%");
                return resultado;
            }
        } catch (Exception e) {
            resultado.agregarLog("Error durante la invocación del Leviatán: " + e.getMessage());
            return resultado;
        }
    }

    private ResultadoCombate continuarInvocacionLeviatan() {
        turnosLeviatan++;
        ResultadoCombate resultado = new ResultadoCombate(this.nombre, "Carga Leviatán");

        if (turnosLeviatan == 2) {
            resultado.agregarLog("El Leviatán se acerca! (Turno 2/3)");
            resultado.agregarLog("Las sombras se alargan y el viento aúlla con furia...");
        } else if (turnosLeviatan == 3) {
            resultado.agregarLog("EL LEVIATÁN ESTÁ AQUÍ!!! (Turno 3/3)");
            resultado.agregarLog("Una criatura gigantesca emerge de las profundidades del vacío!");
            resultado.agregarLog("Está listo para atacar en el próximo turno...");
        }
        return resultado;
    }

    private ResultadoCombate ejecutarLeviatan(Personaje enemigo) {
        ResultadoCombate resultado = new ResultadoCombate(this.nombre, "Ataque Leviatán");

        try {
            if (enemigo == null) {
                resultado.agregarLog("Error: El Leviatán no puede atacar a un enemigo inexistente.");
                return resultado;
            }

            resultado.agregarLog("EL LEVIATÁN DEL VACÍO ATACA!!!");
            resultado.agregarLog("La criatura gigantesca desata su furia sobre " + enemigo.getNombre() + "!");
            resultado.agregarLog("Olas gigantescas y tentáculos se abalanzan!");

            int danio = enemigo.getVida();
            resultado.setDanioRealizado(danio);
            resultado.agregarLog("Daño infligido: " + danio + " puntos!");

            try {
                enemigo.setVida(0);
            } catch (Exception e) {
                resultado.agregarLog("Error al aplicar daño del Leviatán: " + e.getMessage());
            }

            resultado.agregarLog(enemigo.getNombre() + " ha sido derrotado por el poder del Leviatán!");
            resultado.agregarLog("El Leviatán regresa a las profundidades del vacío...");

            leviatanInvocado = false;
            turnosLeviatan = 0;
            return resultado;
        } catch (Exception e) {
            resultado.agregarLog("Error durante la ejecución del Leviatán: " + e.getMessage());
            leviatanInvocado = false;
            turnosLeviatan = 0;
            return resultado;
        }
    }

    public boolean isLeviatanInvocado() {
        return leviatanInvocado;
    }

    public int getTurnosLeviatan() {
        return turnosLeviatan;
    }
}
