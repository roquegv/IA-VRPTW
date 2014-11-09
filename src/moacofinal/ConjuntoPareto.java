/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package moacofinal;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;

/**
 *
 * @author Christian Gomez
 */
public class ConjuntoPareto {

    public static final int PARETO = 50;
    private int cantSoluciones; //cantidad actual de soluciones
    private int tamano; //tamano del array de soluciones

    // array que contiene las soluciones del frente pareto
    protected Solucion[] lista;
    protected SolucionVRP[] listaVRP;

    public ConjuntoPareto(int numSoluciones) {
        cantSoluciones = 0;
        tamano = numSoluciones;
        lista = new Solucion[numSoluciones];
        listaVRP = new SolucionVRP[numSoluciones];
    }

    public int agregarNoDominado(Solucion sol, Problem prob) {
        double solfuncion1 = prob.funcion_obj_1(sol); // Evaluacion de la solucion respecto

        double solfuncion2 = prob.funcion_obj_2(sol); // a las funciones obetivo del problema

        double solauxfuncion1; // Evaluacion de los objetivos de alguna solucion del conjunto

        double solauxfuncion2;

        for (int i = 0; i < cantSoluciones; i++) {
            solauxfuncion1 = prob.funcion_obj_1(lista[i]);
            solauxfuncion2 = prob.funcion_obj_2(lista[i]);
            // ambas funciones objetivo siempre se minimizan
            if (solauxfuncion1 <= solfuncion1 && solauxfuncion2 <= solfuncion2) {
                return 0; //sol es dominada por una solucion del conjunto

            }
        }
        //Aumentar el tama�o del conjunto Pareto si �ste est� lleno
        if (cantSoluciones == tamano) {
            Solucion[] listaAux = lista;
            tamano = tamano * 2;
            lista = new Solucion[tamano];
            for (int i = 0; i < cantSoluciones; i++) {
                lista[i] = listaAux[i];
            }
        }
        if (lista[cantSoluciones] == null) {
            lista[cantSoluciones] = new Solucion(sol.getSize());
        }
        lista[cantSoluciones].solcpy(sol);
        cantSoluciones++;
        return 1;
    }

    public void eliminarDominados(Solucion sol, Problem prob) {
        double solfuncion1 = prob.funcion_obj_1(sol); // Evaluacion de la solucion respecto

        double solfuncion2 = prob.funcion_obj_2(sol); // a las funciones obetivo del problema

        double solauxfuncion1; // Evaluacion de los objetivos de alguna solucion del conjunto

        double solauxfuncion2;
        //Solucion *elim;

        for (int i = 0; i < cantSoluciones; i++) {
            solauxfuncion1 = prob.funcion_obj_1(lista[i]);
            solauxfuncion2 = prob.funcion_obj_2(lista[i]);
            // ambas funciones objetivo siempre se minimizan
            if ((solauxfuncion1 > solfuncion1 && solauxfuncion2 >= solfuncion2) || (solauxfuncion1 >= solfuncion1 && solauxfuncion2 > solfuncion2)) {
                //elim=lista[i];
                lista[i] = lista[cantSoluciones - 1];
                lista[cantSoluciones - 1] = null; //liberar puntero
                cantSoluciones--;
                i--;
            //elim->destruir();
            }
        }
    }

    public int agregarNoDominado(SolucionVRP sol, Problem prob) {
        double solfuncion1 = prob.funcion_obj_1(sol); // Evaluacion de la solucion respecto

        double solfuncion2 = prob.funcion_obj_2(sol); // a las funciones obetivo del problema

        double solauxfuncion1; // Evaluacion de los objetivos de alguna solucion del conjunto

        double solauxfuncion2;

        for (int i = 0; i < cantSoluciones; i++) {
            solauxfuncion1 = prob.funcion_obj_1(listaVRP[i]);
            solauxfuncion2 = prob.funcion_obj_2(listaVRP[i]);
            // ambas funciones objetivo siempre se minimizan
            if (solauxfuncion1 <= solfuncion1 && solauxfuncion2 <= solfuncion2) {
                return 0; //sol es dominada por una solucion del conjunto

            }
        }
        //Aumentar el tama�o del conjunto Pareto si �ste est� lleno
        if (cantSoluciones == tamano) {
            SolucionVRP[] listaAux = listaVRP;
            tamano = tamano * 2;
            listaVRP = new SolucionVRP[tamano];
            for (int i = 0; i < cantSoluciones; i++) {
                listaVRP[i] = listaAux[i];
            }
        }
        if (listaVRP[cantSoluciones] == null) {
            listaVRP[cantSoluciones] = new SolucionVRP(sol.getSize());
        }
        listaVRP[cantSoluciones].solcpy(sol);
        cantSoluciones++;
        return 1;
    }

    public void eliminarDominados(SolucionVRP sol, Problem prob) {
        double solfuncion1 = prob.funcion_obj_1(sol); // Evaluacion de la solucion respecto

        double solfuncion2 = prob.funcion_obj_2(sol); // a las funciones obetivo del problema

        double solauxfuncion1; // Evaluacion de los objetivos de alguna solucion del conjunto

        double solauxfuncion2;
        //SolucionVRP *elim;

        for (int i = 0; i < cantSoluciones; i++) {
            solauxfuncion1 = prob.funcion_obj_1(listaVRP[i]);
            solauxfuncion2 = prob.funcion_obj_2(listaVRP[i]);
            // ambas funciones objetivo siempre se minimizan
            if ((solauxfuncion1 > solfuncion1 && solauxfuncion2 >= solfuncion2) || (solauxfuncion1 >= solfuncion1 && solauxfuncion2 > solfuncion2)) {
                //elim=listaVRP[i];
                listaVRP[i] = listaVRP[cantSoluciones - 1];
                listaVRP[cantSoluciones - 1] = null; //liberar puntero

                cantSoluciones--;
                i--;
            //elim->destruir();
            }
        }
    }

    public void listarSoluciones(Problem prob, String file) {
        try {
            PrintStream output = new PrintStream(
                    new FileOutputStream(file));
            output.println(cantSoluciones);
            for (int i = 0; i < cantSoluciones; i++) {
                output.println(prob.funcion_obj_1(lista[i]) + "\t" + prob.funcion_obj_2(lista[i]));
            }
            output.close();
        } catch (java.io.IOException e) {
            System.out.println("Error al leer archivo");
        }
    }

    public void agregarSoluciones(Problem prob, String file) {
        try {
            FileWriter fstream = new FileWriter(file, true);
            BufferedWriter output = new BufferedWriter(fstream);
//            System.out.println(cantSoluciones);
            for (int i = 0; i < cantSoluciones; i++) {
//            	System.out.println(prob.funcion_obj_1(lista[i]) + "\t" + prob.funcion_obj_2(lista[i]));
                output.write(prob.funcion_obj_1(lista[i]) + "\t" + prob.funcion_obj_2(lista[i]));
                output.newLine();
            }
            output.close();
            fstream.close();
        } catch (java.io.IOException e) {
            System.out.println("Error al leer archivo");
            e.printStackTrace();
        }
    }

    public void listarSolucionesVRP(Problem prob, String file) {
        try {
            PrintStream output = new PrintStream(
                    new FileOutputStream(file));
            output.println(cantSoluciones);
            for (int i = 0; i < cantSoluciones; i++) {
                output.println(prob.funcion_obj_1(listaVRP[i]) + "\t" + prob.funcion_obj_2(listaVRP[i]));
            }
            output.close();
        } catch (java.io.IOException e) {
            System.out.println("Error al leer archivo");
        }
    }

    public void agregarSolucionesVRP(Problem prob, String file) {
        try {
            FileWriter fstream = new FileWriter(file, true);
            BufferedWriter output = new BufferedWriter(fstream);
//            System.out.println(cantSoluciones);
            for (int i = 0; i < cantSoluciones; i++) {
                output.write(prob.funcion_obj_2(listaVRP[i]) + "\t" + prob.funcion_obj_1(listaVRP[i]));
                output.newLine();
            }
            output.close();
        } catch (java.io.IOException e) {
            System.out.println("Error al leer archivo");
        }
    }

    public int getSize() {
        return cantSoluciones;
    }

    public Solucion getSolucion(int i) {
        return lista[i];
    }

    public SolucionVRP getSolucionVRP(int i) {
        return listaVRP[i];
    }

    public void enviar(int tid) {
    }

    public void recibir(int tid) {
    }
}

