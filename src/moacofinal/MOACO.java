/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package moacofinal;

import java.util.Random;

/**
 *
 * @author Christian Gomez
 */
public abstract class MOACO {

    public static final int PARETO = 50;
    public static final int RAND_MAX = 2147483647;
    protected Problem prob;
    protected int criterio;
    protected int tiempoTotal;
    protected int maxIteraciones;
    protected int hormigas;
    public ConjuntoPareto pareto;

    public MOACO(Problem p) {
        prob = p;
        pareto = new ConjuntoPareto(500);
    }

    public abstract int seleccionar_siguiente_estadoTSP(int estOrigen, Solucion sol);

    public abstract int seleccionar_siguiente_estadoQAP(int estOrigen, Solucion sol);

    public abstract int seleccionar_siguiente_estadoVRP(int estOrigen, Solucion sol, double currentTime, double cargaActual);

    public abstract void ejecutarTSP();

    public abstract void ejecutarQAP();

    public abstract void ejecutarVRP();

    public int condicion_parada(int generacion, long start, long end) {
        if (criterio == 1) {
            long elapsedTimeMillis = end - start;
            float elapsedTimeSec = elapsedTimeMillis / 1000F;

            if (elapsedTimeSec < tiempoTotal) {
                return 0;
            }
        } else if (generacion < maxIteraciones) {
            return 0;
        }
        return 1;
    }

    public Problem getProblema() {
        return prob;
    }

    public void online_update(int orig, int dest) {
    }

    //Genera un numero entero aleatorio comprendido entre 0 y RAN_MAX
    public int rand() {
        double aleat = Math.random() * RAND_MAX;
        aleat = Math.floor(aleat);
        return (int) aleat;
    }
}