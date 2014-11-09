/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmetal.qualityIndicator.util;

import jmetal.base.Solution;
import jmetal.base.SolutionSet;

/**
 *
 * @author Christian Gomez
 */
public class ConjuntoPareto {

    private int cantSoluciones; //cantidad actual de soluciones

    protected SolutionSet solutionSet;
    
    public ConjuntoPareto() {
        cantSoluciones = 0;
        solutionSet = new SolutionSet(500);
    }

    public int agregarNoDominado(double solObjetivo1, double solObjetivo2) {
        double solfuncion1 = solObjetivo1; // Evaluacion de la solucion respecto

        double solfuncion2 = solObjetivo2; // a las funciones obetivo del problema

        double solauxfuncion1; // Evaluacion de los objetivos de alguna solucion del conjunto

        double solauxfuncion2;

        for (int i = 0; i < solutionSet.size(); i++) {
            solauxfuncion1 = solutionSet.get(i).getObjective(0);
            solauxfuncion2 = solutionSet.get(i).getObjective(1);
            // ambas funciones objetivo siempre se minimizan
            if (solauxfuncion1 <= solfuncion1 && solauxfuncion2 <= solfuncion2) {
                return 0; //sol es dominada por una solucion del conjunto

            }
        }

        Solution solution = new Solution(2);
        solution.setObjective(0, solObjetivo1);
        solution.setObjective(1, solObjetivo2);

        solutionSet.add(solution);

        cantSoluciones++;
        return 1;
    }

    public void eliminarDominados(double solObjetivo1, double solObjetivo2) {
        double solfuncion1 = solObjetivo1; // Evaluacion de la solucion respecto

        double solfuncion2 = solObjetivo2; // a las funciones obetivo del problema

        double solauxfuncion1; // Evaluacion de los objetivos de alguna solucion del conjunto

        double solauxfuncion2;
        //Solucion *elim;

        for (int i = 0; i < cantSoluciones-1; i++) {
            solauxfuncion1 = ((Solution) solutionSet.get(i)).getObjective(0);
            solauxfuncion2 = ((Solution) solutionSet.get(i)).getObjective(1);
            // ambas funciones objetivo siempre se minimizan
            if ((solauxfuncion1 > solfuncion1 && solauxfuncion2 >= solfuncion2) || (solauxfuncion1 >= solfuncion1 && solauxfuncion2 > solfuncion2)) {
                //elim=lista[i];
                solutionSet.remove(i);
                cantSoluciones--;
                i--;
            //elim->destruir();
            }
        }
    }

    public SolutionSet getSolutionSet() {
        return solutionSet;
    }

    public void setSolutionSet(SolutionSet solutionSet) {
        this.solutionSet = solutionSet;
    }

    

    public void enviar(int tid) {
    }

    public void recibir(int tid) {
    }
}

