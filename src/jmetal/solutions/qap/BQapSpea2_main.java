/**
 * BTspSpea2_main.java
 *
 * @author Juan J. Durillo
 * @version 1.0
 */
package jmetal.solutions.qap;

import jmetal.solutions.tsp.*;
import jmetal.metaheuristics.spea2.*;
import java.io.IOException;
import jmetal.base.*;
import jmetal.base.operator.crossover.*;
import jmetal.base.operator.mutation.*;
import jmetal.base.operator.selection.*;
import jmetal.problems.*;

import jmetal.util.JMException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import jmetal.problems.singleObjective.TSP;

public class BQapSpea2_main {

    public static Logger logger_;      // Logger object
    public static FileHandler fileHandler_; // FileHandler object


    /**
     * @param args Command line arguments. The first (optional) argument specifies
     *             the problem to solve.
     * @throws JMException
     */
    public static void main(String[] args) throws JMException, IOException {

        for (int i = 0; i < 10; i++) {



            Problem problem;         // The problem to solve
            Algorithm algorithm;         // The algorithm to use
            Operator crossover;         // Crossover operator
            Operator mutation;         // Mutation operator
            Operator selection;         // Selection operator

            //String name = "qapUni.75.0.1.qap.txt";
            String name= "qapUni.75.p75.1.qap.txt";
            String problemName = "src/files/qap/";
            // Logger object and file to store log messages
            logger_ = Configuration.logger_;
            fileHandler_ = new FileHandler("SPEA2.log");
            logger_.addHandler(fileHandler_);

            if (args.length == 1) {
                Object[] params = {"Permutation"};
                problem = (new ProblemFactory()).getProblem(args[0], params);
            } // if
            else { // Default problem

                problem = new QAP2(problemName + name);

            } // else

            algorithm = new SPEA2(problem);

            // Algorithm params
            algorithm.setInputParameter("populationSize", 100);
            algorithm.setInputParameter("archiveSize", 100);
            algorithm.setInputParameter("maxEvaluations", 25000);

            // Mutation and Crossover for Real codification
            crossover = CrossoverFactory.getCrossoverOperator("TwoPointsCrossover");
            crossover.setParameter("probability", 0.9);
            crossover.setParameter("distribuitionIndex", 20.0);
            mutation = MutationFactory.getMutationOperator("SwapMutation");
            mutation.setParameter("probability", 1.0 / problem.getNumberOfVariables());
            mutation.setParameter("distributionIndex", 20.0);

            /* Mutation and Crossover Binary codification */
            /*
            crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover");
            crossover.setParameter("probability",0.9);
            mutation = MutationFactory.getMutationOperator("BitFlipMutation");
            mutation.setParameter("probability",1.0/80);
             */

            /* Selection Operator */
            selection = SelectionFactory.getSelectionOperator("BinaryTournament");

            // Add the operators to the algorithm
            algorithm.addOperator("crossover", crossover);
            algorithm.addOperator("mutation", mutation);
            algorithm.addOperator("selection", selection);

            // Execute the Algorithm
            long initTime = System.currentTimeMillis();
            SolutionSet population = algorithm.execute();
            long estimatedTime = System.currentTimeMillis() - initTime;

            // Result messages
            logger_.info("Total execution time: " + estimatedTime);
            logger_.info("Objectives values have been writen to file FUN");
            population.printObjectivesToFile("FUN");
            logger_.info("Variables values have been writen to file VAR");
            population.printVariablesToFile("VAR");
            population.printObjectivesToFile("c:\\instancias-parametros\\generado\\"+ name + "-SPEA.txt");
        }//main

    }
} // BTspSpea2_main.java
