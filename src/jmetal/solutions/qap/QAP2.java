/**
 * TSP.java
 *
 * @author Antonio J. Nebro
 * @version 1.0
 */
package jmetal.solutions.qap;

import jmetal.solutions.tsp.*;
import jmetal.base.*;
import jmetal.base.Configuration.SolutionType_;
import jmetal.base.Configuration.VariableType_;
import jmetal.base.variable.Permutation;

import java.io.*;

/**
 * Class representing a TSP (Traveling Salesman Problem) problem.
 */
public class QAP2 extends Problem {

    public int numberOfCities_;
//    public double[][] distanceMatrix_;
//    public double[][] timeMatrix_;
    public double[][] flujo1Matrix_;
    public double[][] flujo2Matrix_;
    public double[][] distanceMatrix_;

    /**
     * Creates a new TSP problem instance. It accepts data files from TSPLIB
     * @param filename The file containing the definition of the problem
     */
    public QAP2(String filename) throws FileNotFoundException, IOException {
        numberOfVariables_ = 1;
        numberOfObjectives_ = 2;
        numberOfConstraints_ = 0;
        problemName_ = "QAP2";

        solutionType_ = SolutionType_.Permutation;

        variableType_ = new VariableType_[numberOfVariables_];
        length_ = new int[numberOfVariables_];

        variableType_[0] = Enum.valueOf(VariableType_.class, "Permutation");

        readProblem(filename);
        System.out.println(numberOfCities_);
        length_[0] = numberOfCities_;

       /* System.out.println("Matriz de distancias");
        for (int k = 0; k < numberOfCities_; k++) {
            for (int j = 0; j < numberOfCities_; j++) {
//                System.out.print(flujo1Matrix_[k][j] + "\t");
            } // for
            System.out.println();
        } // for
        System.out.println();
        System.out.println("Matriz de tiempo");
        for (int k = 0; k < numberOfCities_; k++) {
            for (int j = 0; j < numberOfCities_; j++) {
//                System.out.print(flujo2Matrix_[k][j] + "\t");
            } // for
            System.out.println();
        } // for*/
    } // TSP

    /** 
     * Constructor.
     * Creates a new instance of the Kursawe problem.
     * @param numberOfVariables Number of variables of the problem 
     * @param solutionType The solution type must "Real" or "BinaryReal". 
     */
    public QAP2(Integer numberOfVariables, String solutionType, String fileName) throws FileNotFoundException, IOException {
        numberOfVariables_ = 1;
        numberOfObjectives_ = 2;
        numberOfConstraints_ = 0;
        problemName_ = "TSP";
        numberOfCities_ = 9;

        upperLimit_ = new double[numberOfVariables_];
        lowerLimit_ = new double[numberOfVariables_];

        for (int i = 0; i < numberOfVariables_; i++) {
            lowerLimit_[i] = -5.0;
            upperLimit_[i] = 5.0;
        } // for

        solutionType_ = SolutionType_.Permutation;

        // All the variables are of the same type, so the solutionType name is the
        // same than the variableType name
        variableType_ = new VariableType_[numberOfVariables_];
        for (int var = 0; var < numberOfVariables_; var++) {
            variableType_[var] = Enum.valueOf(VariableType_.class, solutionType);
        } // for


        readProblem(fileName);

        length_ = new int[numberOfVariables_];
        length_[0] = numberOfCities_;
    }

    /** 
     * Evaluates a solution 
     * @param solution The solution to evaluate
     */
    public void evaluate(Solution solution) {
        double fitness;
        double fitness2;
         int x, y;
        fitness = 0.0;
        fitness2 = 0.0;

       for (int i = 0; i < (numberOfCities_ ); i++) {
           for (int j = 0; j < (numberOfCities_ ); j++) {
                x = ((Permutation) solution.getDecisionVariables().variables_[0]).vector_[i];
                y = ((Permutation) solution.getDecisionVariables().variables_[0]).vector_[j];
  
            fitness += distanceMatrix_[i][j] * flujo1Matrix_[x][y];
           }
      }
        
         for (int i = 0; i < (numberOfCities_ ); i++) {
           for (int j = 0; j < (numberOfCities_ ); j++) {
                x = ((Permutation) solution.getDecisionVariables().variables_[0]).vector_[i];
                y = ((Permutation) solution.getDecisionVariables().variables_[0]).vector_[j];
  
            fitness2 += distanceMatrix_[i][j] * flujo2Matrix_[x][y];
           }
      }
            
            solution.setObjective(0, fitness);
            solution.setObjective(1, fitness2);
    } // evaluate

    public void readProblem(String fileName) throws FileNotFoundException,
            IOException {

        Reader inputFile = new BufferedReader(
                new InputStreamReader(
                new FileInputStream(fileName)));

        StreamTokenizer token = new StreamTokenizer(inputFile);
        try {
            token.nextToken();

            numberOfCities_ = (int) token.nval;
            
            flujo1Matrix_ = new double[numberOfCities_][numberOfCities_];
            flujo2Matrix_ = new double[numberOfCities_][numberOfCities_];
            distanceMatrix_ = new double[numberOfCities_][numberOfCities_];

            // Cargar objetivo 1
            for (int k = 0; k < numberOfCities_; k++) {
                for (int j = 0; j < numberOfCities_; j++) {
                    token.nextToken();
                    flujo1Matrix_[k][j] = token.nval;
                } // for
            } // for

            // Cargar objetivo 2
            for (int k = 0; k < numberOfCities_; k++) {
                for (int j = 0; j < numberOfCities_; j++) {
                    token.nextToken();
                    flujo2Matrix_[k][j] = token.nval;
                } // for
            } // for

            for (int k = 0; k < numberOfCities_; k++) {
                for (int j = 0; j < numberOfCities_; j++) {
                    token.nextToken();
                    distanceMatrix_[k][j] = token.nval;
//                    System.out.println("X: " + k + ", Y: " + j + ", Valor: " + distanceMatrix_[k][j]);
                } // for
            } // for

           /* for (int k = 0; k < numberOfCities_; k++) {
                for (int j = 0; j < numberOfCities_; j++) {
                    token.nextToken();
                    flujo1Matrix_[k][j] = flujo1Matrix_[k][j] * distanceMatrix_[k][j];
                } // for
            } // for

            // Cargar objetivo 2
            for (int k = 0; k < numberOfCities_; k++) {
                for (int j = 0; j < numberOfCities_; j++) {
                    token.nextToken();
                    flujo2Matrix_[k][j] = flujo2Matrix_[k][j] * distanceMatrix_[k][j];;
                } // for
            } // for
*/

        } // try
        catch (Exception e) {
            System.err.println("QAP2.readProblem(): error when reading data file " + e);
            System.exit(1);
        } // catch

    } // readProblem



} // TSP
