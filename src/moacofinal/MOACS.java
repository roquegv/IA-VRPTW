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
 * @author jmferreira
 */
//************MOACS***********
public class MOACS extends MOACO {

    private TablaFeromona tabla1; //Matrices de feromonas
    private double alfa; // exponente para las feromonas
    private double beta; // exponente para la visibilidad
    private double rho; // Learning step (coeficiente de evaporacion)
    private double taoInicial; // valor inicial para las tablas de feromonas
    private double tao;
    private double q0;
    private double F1MAX; // utilizados para normalizacion
    private double F2MAX;
    private double NORM1;
    private double NORM2;
    private int hormigaActual; // utilizado para calcular los pesos lambda
    private int noLambdas;

    private void inicializar_tabla() {
        tabla1 = new TablaFeromona(prob.getSize());
        tabla1.reiniciar(taoInicial);
    }

    public void online_update(int origen, int destino) {
        double tau;
        tau = (1 - rho) * tabla1.obtenerValor(origen, destino) + rho * taoInicial;
        tabla1.actualizar(origen, destino, tau);
    }

    private void inicializar_parametros(String file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        // El archivo file posee las dos matrices de adyacencia separadas por '\n'
        String linea;
        try {
            reader.readLine(); //Leer la 1ra linea de comentario
            String[] subCadena;
            while ((linea = reader.readLine()) != null) {
                subCadena = linea.split("=");
                double VALOR = Double.valueOf(subCadena[1]).doubleValue();
                if (subCadena[0].compareTo("criterio") == 0) {
                    criterio = (int) VALOR;
                } else if (subCadena[0].compareTo("valor") == 0) {
                    if (criterio == 1) {
                        tiempoTotal = (int) VALOR;
                    } else {
                        maxIteraciones = (int) VALOR;
                    }
                // leer parametros especificos del MAS
                } else if (subCadena[0].compareTo("hormigas") == 0) {
                    this.hormigas = (int) VALOR;
                } else if (subCadena[0].compareTo("alfa") == 0) {
                    this.alfa = VALOR;
                } else if (subCadena[0].compareTo("beta") == 0) {
                    this.beta = VALOR;
                } else if (subCadena[0].compareTo("rho") == 0) {
                    this.rho = VALOR;
                } else if (subCadena[0].compareTo("tau0") == 0) {
                    this.taoInicial = VALOR;
                } else if (subCadena[0].compareTo("q0") == 0) {
                    this.q0 = VALOR;
                } else if (subCadena[0].compareTo("f1max") == 0) {
                    this.F1MAX = VALOR;
                } else if (subCadena[0].compareTo("f2max") == 0) {
                    this.F2MAX = VALOR;
                } else if (subCadena[0].compareTo("d1max") == 0) {
                    this.NORM1 = VALOR;
                } else if (subCadena[0].compareTo("d2max") == 0) {
                    this.NORM2 = VALOR;
                }

            }

        } catch (Exception e) {
            System.err.println("Error de conversion");
            e.printStackTrace();
        }

    }

    private void actualizar_feromonas(Solucion sol, int solSize, double deltaTau) {
        int j;
        int k;
        for (int i = 0; i < solSize - 1; i++) { // actualizar ambas tablas en una cantidad 1/cantNoDominados
            j = sol.get(i);
            k = sol.get(i + 1);
            tabla1.actualizar(j, k, tabla1.obtenerValor(j, k) + deltaTau);
        }
    }

    private int seleccionar_mayor(int estOrigen, int[] visitados) {
        int sgteEstado=0;
        double mayorValor = -1; // inicializar a un valor peque�o
        double heuristica1;
        double heuristica2;
        double valorActual;
        double tau1;
        double tau2;
        double lambda1;
        double lambda2;
        double random;
        int[] sinPorcion = new int[prob.getSize()];
        int cantSinPorcion = 0;
        if (noLambdas != 0) {
            lambda1 = lambda2 = 1;
        } else {
            lambda1 = hormigaActual; // peso de la hormiga actual para el objetivo 1
            lambda2 = hormigas - lambda1 + 1; // peso para el objetivo 2
        }
        for (int i = 0; i < prob.getSize(); i++) {
            if (visitados[i] == 0) // estado i no visitado
            {
                heuristica1 = prob.heuristica_1(estOrigen, i) * NORM1; // normalizado
                heuristica2 = prob.heuristica_2(estOrigen, i) * NORM2; // normalizado
                valorActual = Math.pow(tabla1.obtenerValor(estOrigen, i), alfa) * Math.pow(heuristica1, lambda1 * beta) * Math.pow(heuristica2, lambda2 * beta);
                if (valorActual > mayorValor) {
                    mayorValor = valorActual;
                    sgteEstado = i;
                }
                sinPorcion[cantSinPorcion++] = i;
            }
        }
        if (mayorValor == -1) {
            random = rand() % cantSinPorcion;
            sgteEstado = sinPorcion[(int) random];
        }
        return sgteEstado;
    }

    private int seleccionar_probabilistico(int estOrigen, int[] visitados) {
        int sgteEstado = 0;
        double heuristica1;
        double heuristica2;

        double[] productos = new double[prob.getSize()];
        double random;
        double suma = 0;
        double acum = 0;
        double lambda1;
        double lambda2;
        int[] sinPorcion = new int[prob.getSize()];
        int cantSinPorcion = 0;
        if (noLambdas != 0) {
            lambda1 = lambda2 = 1;
        } else {
            lambda1 = hormigaActual; // peso de la hormiga actual para el objetivo 1
            lambda2 = hormigas - lambda1 + 1; // peso para el objetivo 2
        }
        random = rand() / (double) RAND_MAX; // escoger un valor entre 0 y 1
        // hallar la suma y los productos
        for (int i = 0; i < prob.getSize(); i++) {
            if (visitados[i] == 0) {
                heuristica1 = prob.heuristica_1(estOrigen, i) * NORM1; // normalizado
                heuristica2 = prob.heuristica_2(estOrigen, i) * NORM2; // normalizado
                productos[i] = Math.pow(tabla1.obtenerValor(estOrigen, i), alfa) * Math.pow(heuristica1, lambda1 * beta) * Math.pow(heuristica2, lambda2 * beta);
                suma += productos[i];
                sinPorcion[cantSinPorcion++] = i;
            }
        }
        if (suma == 0) {
            random = rand() % cantSinPorcion;
            sgteEstado = sinPorcion[(int) random];
        } else {
            // aplicar ruleta
            for (int i = 0; i < prob.getSize(); i++) {
                if (visitados[i] == 0) // estado i no visitado
                {
                    acum += productos[i] / suma;
                    if (acum >= random) {
                        sgteEstado = i;
                        break;
                    }
                }
            }
        }
        return sgteEstado;
    }

    private double calcular_delta_tao(Solucion sol) {
        double delta;

        delta = 1.0 / (prob.funcion_obj_1(sol) / F1MAX + prob.funcion_obj_2(sol) / F2MAX); //normalizados
        return delta;
    }

    private double calcular_delta_tao(SolucionVRP sol) {
        double delta;

        delta = 1.0 / (prob.funcion_obj_1(sol) / F1MAX + prob.funcion_obj_2(sol) / F2MAX); //normalizados
        return delta;
    }

    private double calcular_tao_prima(double avr1, double avr2) {
        return (1.0 / (avr1 * avr2));
    }

    private double calcular_average(int obj) {
        double avr = 0;
        for (int i = 0; i < pareto.getSize(); i++) {
            if (obj == 1) {
                avr += prob.funcion_obj_1(pareto.getSolucion(i));
            } else {
                avr += prob.funcion_obj_2(pareto.getSolucion(i));
            }
        }

        return (avr / (double) pareto.getSize());
    }

    private double calcular_averageVRP(int obj) {
        double avr = 0;
        for (int i = 0; i < pareto.getSize(); i++) {
            if (obj == 1) {
                avr += prob.funcion_obj_1(pareto.getSolucionVRP(i));
            } else {
                avr += prob.funcion_obj_2(pareto.getSolucionVRP(i));
            }
        }

        return (avr / (double) pareto.getSize());
    }

    public MOACS(Problem p, String file) {
        super(p);
        NORM1 = 1; //por defecto
        NORM2 = 1; //por defecto
        F1MAX = 1; //por defecto
        F2MAX = 1; //por defecto
        inicializar_parametros(file);
        inicializar_tabla();
        noLambdas = 0;
    }

    public int seleccionar_siguiente_estadoTSP(int estOrigen, Solucion sol) {
        int sgteEstado;

        int[] visitados = new int[prob.getSize()];
        double q;
        // marcar estados ya visitados, hallar el vecindario
        for (int i = 0; sol.get(i) != -1; i++) {
            visitados[sol.get(i)] = 1;        //Pseudo-random proportional rule
        }
        q = rand() / (double) RAND_MAX;
        if (q <= q0) {
            sgteEstado = seleccionar_mayor(estOrigen, visitados);
        } else {
            sgteEstado = seleccionar_probabilistico(estOrigen, visitados);//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
        }

        return sgteEstado;
    }

    public int seleccionar_siguiente_estadoQAP(int estOrigen, Solucion sol) {
        int sgte = seleccionar_siguiente_estadoTSP(estOrigen, sol);
        return sgte;
    }

    public int seleccionar_siguiente_estadoVRP(int estOrigen, Solucion sol, double currentTime, double cargaActual) {
        int sgteEstado = 0;
        int[] visitados = new int[prob.getSize()];
        SolucionVRP soluc = (SolucionVRP) sol;
        VRPTW problem = (VRPTW) prob;
        int totalVisitados = 1; // necesariamente se visito el deposito 1 vez
        double distancia;
        double q;
        // hallar el vecindario
        for (int i = 0; i < soluc.getSizeActual(); i++) {
            visitados[soluc.get(i)] = 1;
            if (soluc.get(i) != 0) // estado 0 ya se contabilizo
            {
                totalVisitados++;
            }
        }
        for (int i = 0; i < problem.getSize(); i++) {
            if (visitados[i] == 0) { // controlar si se cumplira la ventana, la capacidad
                // y si se podra volver a tiempo al deposito si fuera necesario
                distancia = Math.max(currentTime + problem.getDistancia(estOrigen, i), problem.getTimeStart(i));
                if (cargaActual + problem.getDemanda(i) > problem.getCapacity() || currentTime + problem.getDistancia(estOrigen, i) > problem.getTimeEnd(i) || distancia + problem.getDistancia(i, 0) > problem.getTimeEnd(0)) {
                    visitados[i] = 1; // marcar como no vecino
                    totalVisitados++;
                }
            }
        }

        if (totalVisitados >= problem.getSize()) {
            sgteEstado = 0; // ir al deposito
        } else {
            //Pseudo-random proportional rule
            q = rand() / (double) RAND_MAX;
            if (q <= q0) {
                sgteEstado = seleccionar_mayor(estOrigen, visitados);
            } else {
                sgteEstado = seleccionar_probabilistico(estOrigen, visitados);
            }
        }


        return sgteEstado;
    }

    public void ejecutarTSP() {
        int generacion = 0;
        int estOrigen;
        double deltaTao;
        double taoPrima;
        long start;
        long end;
        Solucion[] sols = new Solucion[hormigas];
        for (int i = 0; i < hormigas; i++) {
            sols[i] = new Solucion(prob.getSize() + 1);
        }
        start = System.currentTimeMillis();
        end = start;
        tao = -1;
        while (condicion_parada(generacion, start, end) == 0) {
            generacion++;
            for (int i = 0; i < hormigas; i++) {
                estOrigen = rand() % (prob.getSize()); // colocar a la hormiga en un estado inicial aleatorio
                hormigaActual = i + 1; // utilizado en seleccionar_sgte_estado
                construir_solucionTSP(estOrigen, this, 1, sols[i]);
            }

            for (int i = 0; i < hormigas; i++) {
                if (pareto.agregarNoDominado(sols[i], prob) == 1) {
                    pareto.eliminarDominados(sols[i], prob);
                }
                //else
                sols[i].resetear();
            }

            taoPrima = calcular_tao_prima(calcular_average(1), calcular_average(2));

            if (taoPrima > tao) {
                // reiniciar tabla de feromonas
                tao = taoPrima;
                tabla1.reiniciar(tao);
            } else {
                // actualizan la tabla las soluciones del frente Pareto
                for (int i = 0; i < pareto.getSize(); i++) {
                    deltaTao = calcular_delta_tao(pareto.getSolucion(i));
                    actualizar_feromonas(pareto.getSolucion(i), pareto.getSolucion(i).getSize(), deltaTao);
                }
            }
            end = System.currentTimeMillis();
        }
    //	pareto->listarSoluciones(prob,"/home/fuentes/tsp.moacs.pareto");
    }

    public void ejecutarQAP() {
        int generacion = 0;
        int estOrigen;
        double deltaTao;
        double taoPrima;
        long start;
        long end;
        Solucion[] sols = new Solucion[hormigas];
        for (int i = 0; i < hormigas; i++) {
            sols[i] = new Solucion(prob.getSize());
        }

        start = System.currentTimeMillis();
        end = start;
        tao = -1;
        while (condicion_parada(generacion, start, end)==0) {
            generacion++;
            for (int i = 0; i < hormigas; i++) {
                estOrigen = rand() % (prob.getSize()); // colocar a la hormiga en un estado inicial aleatorio
                hormigaActual = i + 1; // utilizado en seleccionar_sgte_estado
                construir_solucionQAP(estOrigen, this, 1, sols[i]);
            }

            for (int i = 0; i < hormigas; i++) {
                if (pareto.agregarNoDominado(sols[i], prob)==1) {
                    pareto.eliminarDominados(sols[i], prob);
                }
                //else
                sols[i].resetear();
            }

            taoPrima = calcular_tao_prima(calcular_average(1), calcular_average(2));

            if (taoPrima > tao) {
                // reiniciar tabla de feromonas
                tao = taoPrima;
                tabla1.reiniciar(tao);
            } else {
                // actualizan la tabla las soluciones del frente Pareto
                for (int i = 0; i < pareto.getSize(); i++) {
                    deltaTao = calcular_delta_tao(pareto.getSolucion(i));
                    actualizar_feromonas(pareto.getSolucion(i), pareto.getSolucion(i).getSize(), deltaTao);
                }
            }
            end = System.currentTimeMillis();
        }
    //	pareto->listarSoluciones(prob,"/home/fuentes/qap.moacs.pareto");
    }

    public void ejecutarVRP() {
        int generacion = 0;
        int estOrigen;
        double deltaTao;
        double taoPrima;
        long start;
        long end;
        SolucionVRP[] sols = new SolucionVRP[hormigas];
        for (int i = 0; i < hormigas; i++) {
            sols[i] = new SolucionVRP(prob.getSize()*2);
        }
        
        start = System.currentTimeMillis();
        end = start;
        tao = -1;
        noLambdas = 1;
        while (condicion_parada(generacion, start, end)==0) {
            generacion++;
            for (int i = 0; i < hormigas; i++) {
                estOrigen = 0; // colocar a la hormiga en un estado inicial aleatorio
                hormigaActual = i + 1; // utilizado en seleccionar_sgte_estado
                construir_solucionVRP(estOrigen, this, 1, sols[i]);
            }

            for (int i = 0; i < hormigas; i++) {
                if (pareto.agregarNoDominado(sols[i], prob)==1) {
                    pareto.eliminarDominados(sols[i], prob);
                }
                //else
                sols[i].resetear();
            }

            taoPrima = calcular_tao_prima(calcular_averageVRP(1), calcular_averageVRP(2));

            if (taoPrima > tao) {
                // reiniciar tabla de feromonas
                tao = taoPrima;
                tabla1.reiniciar(tao);
            } else {
                // actualizan la tabla las soluciones del frente Pareto
                for (int i = 0; i < pareto.getSize(); i++) {
                    deltaTao = calcular_delta_tao(pareto.getSolucionVRP(i));

                    actualizar_feromonas(pareto.getSolucionVRP(i), pareto.getSolucionVRP(i).getSizeActual(), deltaTao);
                }
            }
            end = System.currentTimeMillis();
        }
    //	pareto->listarSoluciones(prob,"/home/fuentes/qap.moacs.pareto");
    }

    private void construir_solucionTSP(int estOrig, MOACO aco, int onlineUpdate, Solucion sol) {
        int estVisitados = 0;
        int sgteEstado = 0;
        int estActual = estOrig;
        sol.set(estVisitados, estOrig);
        estVisitados++;
        while (estVisitados < (aco.getProblema().getSize())) {
            sgteEstado = aco.seleccionar_siguiente_estadoTSP(estActual, sol);
            if (onlineUpdate != 0) {
                aco.online_update(estActual, sgteEstado);
            }
            estActual = sgteEstado;
            sol.set(estVisitados, sgteEstado);
            estVisitados++;
        }
        sol.set(estVisitados, estOrig);
    }

    private void construir_solucionQAP(int estOrigen, MOACO aco, int onlineUpdate, Solucion sol) {
        int estVisitados = 0;
        int sgteEstado;
        int estActual = estOrigen;

        sol.set(estVisitados, estOrigen);
        estVisitados++;
        while (estVisitados < aco.getProblema().getSize()) {
            sgteEstado = aco.seleccionar_siguiente_estadoQAP(estActual, sol);
            if (onlineUpdate != 0) {
                aco.online_update(estActual, sgteEstado);
            }
            estActual = sgteEstado;
            sol.set(estVisitados, sgteEstado);
            estVisitados++;
        }

    }

    private void construir_solucionVRP(int estOrigen, MOACO aco, int onlineUpdate, SolucionVRP sol) {
        int estVisitados = 0;
        int sgteEstado;
        int estActual = estOrigen;
        double cargaActual;
        double currentTime;

        VRPTW vrp = (VRPTW) aco.getProblema();

        sol.add(estOrigen);
        estVisitados++;
        currentTime = 0;
        cargaActual = 0;
        while (estVisitados < vrp.getSize()) {
            sgteEstado = aco.seleccionar_siguiente_estadoVRP(estActual, sol, currentTime, cargaActual);
            sol.add(sgteEstado);
            if (sgteEstado != 0) //0 representa el deposito, no ir al deposito
            {
                estVisitados++;
                currentTime = Math.max(currentTime + vrp.getDistancia(estActual, sgteEstado), vrp.getTimeStart(sgteEstado));
                cargaActual += vrp.getDemanda(sgteEstado);
                if (onlineUpdate != 0) {
                    aco.online_update(estActual, sgteEstado);
                }
            } else // ir al deposito
            {
                currentTime = 0;
                cargaActual = 0;
                sol.incCamiones();
            }
            estActual = sgteEstado;
        }
        sol.add(estOrigen); // volver al deposito
    }
}
