package batalla.modelo;

import java.util.Random;

/**
 * Clase abstracta que representa un personaje en el juego
 */
public abstract class Personaje {
    protected int id;
    protected String nombre;
    protected String apodo;
    protected String tipo;
    protected int vida;
    protected int vidaMaxima;
    protected int fuerza;
    protected int defensa;
    protected Arma arma;
    protected int bendiciones;
    protected int bendicionesIniciales; // Guardar bendiciones iniciales
    protected int derrotas = 0;
    protected int victorias = 0;
    protected int ataquesSupremosUsados = 0;
    protected int armasInvocadas = 0;
    protected Random random = new Random();

    public Personaje(String nombre, String apodo, String tipo, int vida, int fuerza, int defensa, int bendiciones) {
        this.nombre = nombre;
        this.apodo = apodo;
        this.tipo = tipo;
        this.vida = vida;
        this.vidaMaxima = vida;
        this.fuerza = fuerza;
        this.defensa = defensa;
        this.bendiciones = bendiciones;
        this.bendicionesIniciales = bendiciones; // Guardar valor inicial
        this.arma = null;
    }

    public boolean estaVivo() {
        return this.vida > 0;
    }

    @Override
    public String toString() {
        String armaInfo = (arma != null) ? " | Arma: " + arma.getNombre() : " | Sin arma";
        return "[ " + nombre + " | Vida: " + vida + " | Fuerza: " + fuerza +
                " | Defensa: " + defensa + " | Bendiciones: " + bendiciones + "%" + armaInfo + " ]";
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApodo() {
        return apodo;
    }

    public String getTipo() {
        return tipo;
    }

    public int getVida() {
        return vida;
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }

    public int getDerrotas() {
        return derrotas;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setVida(int vida) {
        this.vida = vida;
    }

    public int getFuerza() {
        return fuerza;
    }

    public int getDefensa() {
        return defensa;
    }

    public int getBendiciones() {
        return bendiciones;
    }

    public void setDerrotas(int derrotas) {
        this.derrotas = derrotas;
    }

    public void setBendiciones(int bendiciones) {
        this.bendiciones = Math.max(0, Math.min(100, bendiciones));
    }

    public int getBendicionesIniciales() {
        return bendicionesIniciales;
    }

    /**
     * Restaura las estadísticas iniciales del personaje
     */
    public void restaurarEstadisticasIniciales() {
        this.vida = this.vidaMaxima;
        // NO resetear bendiciones para que se acumulen entre batallas
        // this.bendiciones = this.bendicionesIniciales;
        this.arma = null;
    }

    public Arma getArma() {
        return arma;
    }

    public void setArma(Arma arma) {
        this.arma = arma;
    }

    public int getVictorias() {
        return victorias;
    }

    public void incrementarVictoria() {
        victorias++;
    }

    public void incrementarDerrota() {
        derrotas++;
    }

    public int getAtaquesSupremosUsados() {
        return ataquesSupremosUsados;
    }

    public void setVictorias(int victorias) {
        this.victorias = victorias;
    }

    public void setAtaquesSupremosUsados(int ataquesSupremosUsados) {
        this.ataquesSupremosUsados = ataquesSupremosUsados;
    }

    public void setArmasInvocadas(int armasInvocadas) {
        this.armasInvocadas = armasInvocadas;
    }

    public void incrementarAtaqueSupremo() {
        ataquesSupremosUsados++;
    }

    public int getArmasInvocadas() {
        return armasInvocadas;
    }

    public void incrementarArmaInvocada() {
        armasInvocadas++;
    }

    public ResultadoCombate atacar(Personaje enemigo) {
        ResultadoCombate resultado = new ResultadoCombate(this.nombre, "Ataque básico");

        try {
            if (enemigo == null) {
                resultado.agregarLog("Error: No se puede atacar a un enemigo inexistente.");
                return resultado;
            }

            boolean critico = random.nextInt(100) < 20;
            boolean parry = random.nextInt(100) < 10;

            resultado.setEsCritico(critico);
            resultado.setEsParry(parry);

            int dmg = this.fuerza;
            resultado.setDanioBase(dmg); // Base damage before crit

            if (critico) {
                dmg *= 2;
                // Update dmg in result if needed/conceptually correct, though we usually track
                // 'final' or 'potential'
                // Let's store potential damage after crit for display
                resultado.setDanioBase(dmg);
            }

            if (parry) {
                resultado.agregarLog(enemigo.getNombre() + " hace parry, evita el ataque!");
                // Implement damage reflection logic if implied by "Daño reflejado" in prompt,
                // but prompt example shows "Atenea bloquea el ataque / Daño reflejado: 10".
                // Assuming simple return for now, controller can handle specific parry logging
                // or we map it here.
                // Let's assume standard parry implies 0 damage taken.
                resultado.setDanioRealizado(0);

                // For demonstration, let's say parry reflects 10 damage or some amount.
                // The prompt example says "Daño reflejado: 10".
                // Let's assume damage reflected is constant or based on defense for now.
                int reflected = 10;
                this.vida -= reflected;
                resultado.setDanioReflejado(reflected);

                return resultado;
            }

            String armaNombre = "Sin arma";
            if (this.arma != null) {
                try {
                    dmg += this.arma.getDanioExtra();
                    armaNombre = this.arma.getNombre();
                    resultado.agregarLog(this.nombre + " ataca con " + armaNombre + "!");
                } catch (Exception e) {
                    resultado.agregarLog("Error al usar el arma: " + e.getMessage());
                }
            }

            resultado.setAccionNombre("Ataque con " + (this.arma != null ? this.arma.getNombre() : "puños"));

            resultado.setDefensaEnemigo(enemigo.defensa);
            int danioFinal = Math.max(0, dmg - enemigo.defensa);
            enemigo.vida -= danioFinal;
            resultado.setDanioRealizado(danioFinal);

            resultado.agregarLog(this.nombre + " causa " + danioFinal + " de daño a " + enemigo.nombre);

            int gananciaBendicion = 10;
            this.bendiciones = Math.min(100, this.bendiciones + gananciaBendicion);
            resultado.setBendicionesGanadas(gananciaBendicion);
            resultado.setTotalBendiciones(this.bendiciones);
            resultado.agregarLog(
                    this.nombre + " ha ganado " + gananciaBendicion + "% de poder! (Total: " + this.bendiciones + "%)");

            if (this.arma != null) {
                try {
                    this.arma.usarEfectoEspecial(enemigo);
                } catch (Exception e) {
                    resultado.agregarLog("Error al usar efecto especial del arma: " + e.getMessage());
                }
            }

            return resultado;
        } catch (Exception e) {
            resultado.agregarLog("Error durante el ataque: " + e.getMessage());
            return resultado;
        }
    }

    public abstract void invocarArma();

    public abstract ResultadoCombate decidirAccion(Personaje enemigo);
}
