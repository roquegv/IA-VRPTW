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
//*********** Clase QAP *************
public class QAP extends Problem {

    private double[][] matrizFlujo1;
    private double[][] matrizFlujo2;
    private int ciudades;

    public void cargar_estado(String file) {
        // El archivo file posee las tres matrices: adyacencia,flujo1,flujo2 separadas por '\n'
        // en la primera linea posee informacion adicional
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        String linea;
        try {
            ciudades = Integer.parseInt(reader.readLine());
            //matrizAdy = new double[ciudades][ciudades];
            //matrizFlujo1 = new double[ciudades][ciudades];
            //matrizFlujo2 = new double[ciudades][ciudades];
            int count = 0;
            int count_i;
            String[] subCadena;
            while ((linea = reader.readLine()) != null) {
                count = count + 1;
                if (count <= ciudades) {
                    // hacer token
                    subCadena = linea.split("\\s+");
                    count_i = -1;
                    for (int i = 0; i < subCadena.length; i++) {
                        count_i += 1;
                        if (subCadena[i].equals("")) {
                            count_i--;
                        } else {
                            matrizAdy[count - 1][count_i] = Double.valueOf(subCadena[i]).doubleValue();
                        }
                    }
                } else if ((count > ciudades + 1) && (count < ciudades * 2 + 2)) {
                    subCadena = linea.split("\\s+");
                    count_i = -1;
                    for (int i = 0; i < subCadena.length; i++) {
                        count_i += 1;
                        if (subCadena[i].equals("")) {
                            count_i--;
                        } else {
                            matrizFlujo1[count - (ciudades + 2)][count_i] = Double.valueOf(subCadena[i]).doubleValue();
                        }
                    }

                } else if (count > ciudades * 2 + 2) {
                    subCadena = linea.split("\\s+");
                    count_i = -1;
                    for (int i = 0; i < subCadena.length; i++) {
                        count_i += 1;
                        if (subCadena[i].equals("")) {
                            count_i--;
                        } else {
                            matrizFlujo2[count - (ciudades * 2 + 3)][count_i] = Double.valueOf(subCadena[i]).doubleValue();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error de conversion" + e);
        }

    }

    public QAP(String file) {
        super(file);
        matrizFlujo1 = new double[size][size];
        matrizFlujo2 = new double[size][size];
        cargar_estado(file);
    }

    public double funcion_obj_1(Solucion sol) {
        int i;
        int j;
        double suma = 0;
        for (i = 0; i < sol.getSize(); i++) {
            for (j = 0; j < sol.getSize(); j++) {
                suma += matrizAdy[i][j] * matrizFlujo1[sol.get(i)][sol.get(j)];
            }
        }
        return suma;
    }

    public double funcion_obj_2(Solucion sol) {
        int i;
        int j;
        double suma = 0;
        for (i = 0; i < sol.getSize(); i++) {
            for (j = 0; j < sol.getSize(); j++) {
                suma += matrizAdy[i][j]*matrizFlujo2[sol.get(i)][sol.get(j)];
            }
        }
        

        return suma;
    }

    public double heuristica_1(int estOrigen, int estDest) // No se utilizan
    {
        return 1;
    }

    public double heuristica_2(int estOrigen, int estDest) // heurísticas
    {
        return 1;
    }

    public void imprimir_matrices() {
        int i;
        int j;

        System.out.print("Matriz Adyacencia:\n");
        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {
                System.out.print(matrizAdy[i][j]+" ");
            }
            System.out.print("\n");
        }
        System.out.print("Matriz flujo 1:\n");
        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {
                System.out.print(matrizFlujo1[i][j]+" ");
            }
            System.out.print("\n");
        }
        System.out.print("Matriz flujo 2:\n");
        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {
                System.out.print(matrizFlujo2[i][j]+" ");
            }
            System.out.print("\n");
        }
    }
}

