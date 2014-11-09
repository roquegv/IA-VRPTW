/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package moacofinal;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian Gomez
 */
//*********** Clase TSP *************
public class TSP extends Problem {

    private double[][] matrizAdy2;
    private int ciudades;
    private int objetivos;

    public void cargar_estado(String file) {
        // en la primera linea posee el tamaño del problema

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        // El archivo file posee las dos matrices de adyacencia separadas por '\n'
        String linea;
        try {
            ciudades = Integer.parseInt(reader.readLine());
            objetivos = Integer.parseInt(reader.readLine());
            int count = 0;
            String[] subCadena;
            while ((linea = reader.readLine()) != null) {
                count = count + 1;
                if (count <= ciudades) {
                    // hacer token
                    subCadena = linea.split("\\s");
                    for (int i = 0; i < subCadena.length; i++) {
                        matrizAdy[count - 1][i] = Double.valueOf(subCadena[i]).doubleValue();
                    }
                } else if (count > ciudades + 1) {
                    subCadena = linea.split("\\s");
                    for (int i = 0; i < subCadena.length; i++) {
                        matrizAdy2[count - (ciudades + 2)][i] = Double.valueOf(subCadena[i]).doubleValue();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error de conversion");
        }
    }

    public TSP(String file) {
        super(file);
        matrizAdy2 = new double[size][size];
        cargar_estado(file);
    }

    public double funcion_obj_1(Solucion sol) {
        int i;
        double suma = 0;
        for (i = 0; i < sol.getSize() - 1; i++) {
            suma += matrizAdy[sol.get(i)][sol.get(i + 1)];
        }
        suma += matrizAdy[sol.get(sol.getSize() - 1)][0];
        return suma;
    }

    public double funcion_obj_2(Solucion sol) {
        int i;
        double suma = 0;
        for (i = 0; i < sol.getSize() - 1; i++) {
            suma += matrizAdy2[sol.get(i)][sol.get(i + 1)];
        }
        suma += matrizAdy2[sol.get(sol.getSize() - 1)][0];
        return suma;
    }

    public double heuristica_1(int estOrigen, int estDest) {
        return 1.0 / matrizAdy[estOrigen][estDest];
    }

    public double heuristica_2(int estOrigen, int estDest) {
        return 1.0 / matrizAdy2[estOrigen][estDest];
    }

    public void imprimir_matrices() {
        int i;
        int j;
        System.out.print("Matriz Adyacencia 1:\n");
        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {
                System.out.print(matrizAdy[i][j]+" ");
            }
            System.out.print("\n");
        }
        System.out.print("Matriz Adyacencia 2:\n");
        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {
                System.out.print(matrizAdy2[i][j]+" ");
            }
            System.out.print("\n");
        }
    }
}

