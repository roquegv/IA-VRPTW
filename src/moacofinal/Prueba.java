/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package moacofinal;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian Gomez
 */
public class Prueba {

    protected static double[][] matrizAdy;
    private static double[][] matrizAdy2;
    private static double[][] matrizFlujo1;
    private static double[][] matrizFlujo2;
    private static customerVRP customers[];
    protected static int size;
    static int ciudades;
    static int objetivos;
    static int capacity;

    public Prueba(String file) {
        int i;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        String linea = null;
        boolean numerico = false;
        while (!numerico) {
            try {
                linea = reader.readLine();
                size = Integer.parseInt(linea);
                numerico = true;
            } catch (IOException e) {
                numerico = true;
            } catch (NullPointerException e) {
                numerico = true;
            } catch (NumberFormatException ex) {
                numerico = false;
            }
        }
    }

    private static int getSize() {
        return size;
    }

    public void cargar_estadoVRPTW(String file) {
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
        customers = new customerVRP[size];
        for (int i = 0; i < size; i++) {
            customers[i] = new customerVRP();
        }

        try {
            String nada = reader.readLine();
            ciudades = Integer.parseInt(reader.readLine());
            nada = reader.readLine();
            capacity = Integer.parseInt(reader.readLine());
            nada = reader.readLine();
            //matrizAdy = new double[ciudades][ciudades];
            //matrizFlujo1 = new double[ciudades][ciudades];
            //matrizFlujo2 = new double[ciudades][ciudades];

            int count = 0;
            int count_i;
            String[] subCadena;
            while ((linea = reader.readLine()) != null) {
                count = count + 1;
                if (count <= size) {
                    // hacer token
                    subCadena = linea.split("\\s+");
                    //count_i = -1;
                    for (int i = 0; i < subCadena.length; i++) {
                        //count_i += 1;
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
        matrizAdy = new double[size][size];

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

    public void cargar_estadoQAP(String file) {
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
            matrizAdy = new double[ciudades][ciudades];
            matrizFlujo1 = new double[ciudades][ciudades];
            matrizFlujo2 = new double[ciudades][ciudades];
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
            matrizAdy = new double[ciudades][ciudades];
            matrizAdy2 = new double[ciudades][ciudades];
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

    public static void imprimirMatriz(double matriz[][]) {
        for (int i = 0; i < ciudades; i++) {
            for (int j = 0; j < ciudades; j++) {
                System.out.print(matriz[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void imprimirVector(customerVRP customer[]) {
        for (int i = 0; i < size; i++) {
            System.out.print("Customer: " + i + " ");
            System.out.print("Demanda: " + customers[i].getDemanda() + " ");
            System.out.print("Service Time: " + customers[i].getServiceTime() + " ");
            System.out.print("Begin: " + customers[i].getTimeStart() + " ");
            System.out.println("End: " + customers[i].getTimeEnd() + " ");
        }
    }

        public static void imprimir() {
        System.out.print("Matriz Adyacencia:\n");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(matrizAdy[i][j] + " ");
            }
            System.out.print("\n");
        }
        System.out.println("Size: "+ size);
        System.out.println("capacity: " + capacity);
        for (int i = 0; i < 20; i++) {
            System.out.print("Customer: " + i + " ");
            System.out.print("Demanda: " + customers[i].getDemanda() + " ");
            System.out.print("Service Time: " + customers[i].getServiceTime() + " ");
            System.out.print("Begin: " + customers[i].getTimeStart() + " ");
            System.out.println("End: " + customers[i].getTimeEnd() + " ");
        }
    }
        
    public static void listarSoluciones(String prob, String file) {
        try {
            PrintStream output = new PrintStream(
                    new FileOutputStream(file));
            output.println(prob);
            for (int i = 0; i < size; i++) {
                output.println(i + "\t" + customers[i].getDemanda());
            }
            output.close();
        } catch (java.io.IOException e) {
            System.out.println("Error al leer archivo");
        }
    }        
    public static void main(String[] args) {
        Prueba proft = new Prueba("E:\\Inteligencia Artificial\\TrabajoPracticoFinal\\Implementacion\\moacos\\instancias-parametros\\rc101.txt");
        System.out.println(getSize());
        //proft.cargar_estado("E:\\Inteligencia Artificial\\TrabajoPracticoFinal\\Implementacion\\moacos\\instancias-parametros\\KROAB100.TSP.TXT");
        //imprimirMatriz(matrizAdy);
        //imprimirMatriz(matrizAdy2);
        //proft.cargar_estadoQAP("E:\\Inteligencia Artificial\\TrabajoPracticoFinal\\Implementacion\\moacos\\instancias-parametros\\qapUni.75.0.1.qap.txt");
        //imprimirMatriz(matrizAdy);
        proft.cargar_estadoVRPTW("E:\\Inteligencia Artificial\\TrabajoPracticoFinal\\Implementacion\\moacos\\instancias-parametros\\rc101.txt");
        proft.imprimirVector(customers);
        //imprimir();
        listarSoluciones("Esto es una prueba", "E:\\Inteligencia Artificial\\TrabajoPracticoFinal\\Implementacion\\moacos\\instancias-parametros\\prueba.frente.txt");
    }
}
