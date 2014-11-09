/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package moacofinal;

/**
 *
 * @author Christian Gomez
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        String parametros = new String(new char[100]); // indica el archivo de parametros a utilizar
        MOACO alg; // instancia del algoritmo
        Problem prob; // instancia del problema a resolver
        String cad = new String(new char[300]);
        String pr = new String(new char[60]);

        //String[][] arrayArchivoProblema = {{"KROAB100.TSP.TXT", "kroac100.tsp.txt"}, {"qapUni.75.0.1.qap.txt", "qapUni.75.p75.1.qap.txt"}, {"rc101.txt", "c101.txt"}};
        String[][] arrayArchivoProblema = {{"c101.txt", "rc101.txt"}};
        String ruta = "instancias-parametros/";
        String subRuta = "generado/";
        String archivoParametros = "parametros_vrptw.txt";
        String[] arrayAlgoritmoEjecucion = {"M3AS", "MOACS"}; //Valores: MOACS, M3AS
        String problemaEjecucion = "VRP"; //Valores: TSP, QAP, VRP
        String algoritmoEjecucion; //Valores: MOACS, M3AS
        String archivoProblema;
        for (int k = 0; k < 2; k++) { //recorrer array de Algoritmos
            algoritmoEjecucion = arrayAlgoritmoEjecucion[k];
            for (int m = 0; m < 2; m++) { //recorrer array de ArchivoProblema

                parametros = ruta + archivoParametros;
                pr = arrayArchivoProblema[0][m]; // 0=TSP, 1=QAP, 2=VRP
                archivoProblema = pr;
                System.out.println();
                System.out.println("*********Ejecutandose con los sgtes parametros*********");
                System.out.println();
                System.out.println("archivoProblema = " + archivoProblema);
                System.out.println("ruta = " + ruta);
                System.out.println("archivoParametros = " + archivoParametros);
                System.out.println("algoritmoEjecucion = " + algoritmoEjecucion);
                System.out.println("problemaEjecucion = " + problemaEjecucion);
                System.out.println();

                cad = ruta + subRuta + pr + "-" + algoritmoEjecucion + ".txt";
                System.out.println(cad);
                String cadenaYtrue = ruta + subRuta + "YTRUE-" + pr + ".txt";
                // Ejecutar el algoritmo indicado
                System.out.println("Ejecutando... ");

                for (int i = 0; i < 10; i++) {
//                    System.out.print(" " + i);

                    if (problemaEjecucion.compareTo("TSP") == 0) {
                        prob = new TSP(ruta+archivoProblema);
                        if (algoritmoEjecucion.compareTo("MOACS") == 0) {
                            alg = new MOACS(prob, parametros);
                        } else {
                            alg = new M3AS(prob, parametros);
                        }
                        alg.ejecutarTSP();
                        alg.pareto.agregarSoluciones(prob, cad); //Generar los frentes del MOACS
                        alg.pareto.agregarSoluciones(prob, cadenaYtrue); //Generar los frentes de todos los algoritmos

                    } else if (problemaEjecucion.compareTo("QAP") == 0) {
                        prob = new QAP(ruta + archivoProblema);
                        if (algoritmoEjecucion.compareTo("MOACS") == 0) {
                            alg = new MOACS(prob, parametros);
                        } else {
                            alg = new M3AS(prob, parametros);
                        }
                        alg.ejecutarQAP();
                        alg.pareto.agregarSoluciones(prob, cad); //Generar los frentes del MOACS
                        alg.pareto.agregarSoluciones(prob, cadenaYtrue); //Generar los frentes de todos los algoritmos

                    } else if (problemaEjecucion.compareTo("VRP") == 0) {
                    	
                        prob = new VRPTW(ruta + archivoProblema);
                        if (algoritmoEjecucion.compareTo("MOACS") == 0) {
                            alg = new MOACS(prob, parametros);
                        } else {
                            alg = new M3AS(prob, parametros);
                        }
                        alg.ejecutarVRP();
                        alg.pareto.agregarSolucionesVRP(prob, cad);
                        alg.pareto.agregarSolucionesVRP(prob, cadenaYtrue);

                    }
                }
                System.out.println("\n+++++++++++RESULTS++++++++++++");
                System.out.println("Pareto Generado: " + cad);
                System.out.println("Pareto Ytrue Generado: " + cadenaYtrue);
                System.out.println("Ejecución Finalizada...");
                
            }
            break;
        }
    }
}

