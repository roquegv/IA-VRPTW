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
//*********** Clase VRPTW *************
public class VRPTW extends Problem {

    private int capacity;
    private int ciudades;
    private customerVRP customers[];

    public void cargar_estado(String file) {
        // El archivo file posee: la cantidad de customers, la capacidad de los camiones
        // y los datos de cada customer: coordenadas, demanda, ventana y tiempo de servicio
        double x = 0.0;
        double y = 0.0;
        double dem = 0.0;
        double begin = 0.0;
        double end = 0.0;
        double servTime = 0.0;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        String linea;
        try {
            //Se lee las primeras 5 lineas del archivo de entrada, donde estan # de ciudades y capacidad
            String nada = reader.readLine();
            ciudades = Integer.parseInt(reader.readLine());
            nada = reader.readLine();
            capacity = Integer.parseInt(reader.readLine());
            nada = reader.readLine();
            int count = 0;
            int count_i;
            String[] subCadena;
            while ((linea = reader.readLine()) != null) {
                count = count + 1;
                if (count <= size) {
                    // hacer token
                    subCadena = linea.split("\\s+");
                    for (int i = 0; i < subCadena.length; i++) {
                        switch (i) {
                            case 0:
                                x = Double.valueOf(subCadena[i]).doubleValue();
                                break;
                            case 1:
                                x = Double.valueOf(subCadena[i]).doubleValue();
                                break;
                            case 2:
                                y = Double.valueOf(subCadena[i]).doubleValue();
                                break;
                            case 3:
                                dem = Double.valueOf(subCadena[i]).doubleValue();
                                break;
                            case 4:
                                begin = Double.valueOf(subCadena[i]).doubleValue();
                                break;
                            case 5:
                                end = Double.valueOf(subCadena[i]).doubleValue();
                                break;
                            case 6:
                                servTime = Double.valueOf(subCadena[i]).doubleValue();
                                break;
                            default:
                                ;
                        }

                    }
                    customers[count - 1].setCoord(x, y);
                    customers[count - 1].setDemanda(dem);
                    customers[count - 1].setWindow(begin, end);
                    customers[count - 1].setServiceTime(servTime);
                }
            }
        } catch (Exception e) {
            System.err.println("Error de conversion" + e);
        }
        try {
            reader.close();
        } catch (Exception e) {
            ;
        }

        generar_matriz_ady();
    }

    private void generar_matriz_ady() {
        // a partir de las coordenadas de los customers se genera la matriz simetrica
        // de adyacencia con las dinstancias euclideas entre cada par de customers
        double aux;

        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                aux = Math.pow(customers[i].getCoordX() - customers[j].getCoordX(), 2);
                aux += Math.pow(customers[i].getCoordY() - customers[j].getCoordY(), 2);
                matrizAdy[i][j] = Math.sqrt(aux);
                matrizAdy[j][i] = matrizAdy[i][j];
            }
            matrizAdy[i][i] = 0;
        }
    }

    public VRPTW(String file) {
        super(file);
        customers = new customerVRP[size];
        for (int i = 0; i < size; i++) {
            customers[i] = new customerVRP();
        }
        matrizAdy = new double[size][size];
        cargar_estado(file);
//        printCustomers(customers);
//        printMatrix(matrizAdy);
    }

    public double funcion_obj_1(Solucion sol) {
        return ((SolucionVRP) sol).getCamiones(); // devuelve la cantidad camiones

    }

    public double funcion_obj_2(Solucion sol) {
        int i;
        double suma = 0;
        SolucionVRP s = (SolucionVRP) sol;
        for (i = 0; i < s.getSizeActual() - 1; i++) {
            suma += matrizAdy[s.get(i)][s.get(i + 1)];
        }
        suma += matrizAdy[s.get(s.getSizeActual() - 1)][0];

        return suma; // devolver el "Total Travel Distance"

    }

    public double heuristica_1(int estOrigen, int estDest) {
        return 1;
    }

    public double heuristica_2(int estOrigen, int estDest) {
        return 1.0 / matrizAdy[estOrigen][estDest];
    }

    public int getCapacity() {
        return capacity;
    }

    public double getDemanda(int customer) {
        return customers[customer].getDemanda();
    }

    public double getTimeStart(int customer) {
        return customers[customer].getTimeStart();
    }

    public double getTimeEnd(int customer) {
        return customers[customer].getTimeEnd();
    }

    public double getServiceTime(int customer) {
        return customers[customer].getServiceTime();
    }

    public void imprimir() {
        System.out.print("Matriz Adyacencia:\n");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(matrizAdy[i][j] + " ");
            }
            System.out.print("\n");
        }
        System.out.println("Size: " + size);
        System.out.println("capacity: " + capacity);
        for (int i = 0; i < 20; i++) {
            System.out.print("Customer: " + i + " ");
            System.out.print("Demanda: " + customers[i].getDemanda() + " ");
            System.out.print("Service Time: " + customers[i].getServiceTime() + " ");
            System.out.print("Begin: " + customers[i].getTimeStart() + " ");
            System.out.println("End: " + customers[i].getTimeEnd() + " ");
        }
    }
    
    public static void printCustomers(customerVRP[] customers){
    	for (int i=0;i<customers.length;i++){
    		System.out.println(customers[i]);
    	}
    }
    public static void printMatrix(double [][] matrizAdy){
    	System.out.println("Printing matrix...");
    	for (int i=0;i<matrizAdy.length;i++){
    		for (int j=0;j<matrizAdy[0].length;j++){
    			System.out.print((int)(matrizAdy[i][j])+" ");
    		}
    		System.out.println();
    	}
    }
}



