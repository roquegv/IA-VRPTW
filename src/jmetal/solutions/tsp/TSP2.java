/**
 * TSP.java
 *
 * @author Antonio J. Nebro
 * @version 1.0
 */
package jmetal.solutions.tsp;

import jmetal.base.*;
import jmetal.base.Configuration.SolutionType_;
import jmetal.base.Configuration.VariableType_;
import jmetal.base.variable.Permutation;

import java.io.*;

/**
 * Class representing a TSP (Traveling Salesman Problem) problem.
 */
public class TSP2 extends Problem {

    public int numberOfCities_;
    public double[][] distanceMatrix_;
    public double[][] timeMatrix_;

    /**
     * Creates a new TSP problem instance. It accepts data files from TSPLIB
     * @param filename The file containing the definition of the problem
     */
    public TSP2(String filename) throws FileNotFoundException, IOException {
        numberOfVariables_ = 1;
        numberOfObjectives_ = 2;
        numberOfConstraints_ = 0;
        problemName_ = "TSP";

        solutionType_ = SolutionType_.Permutation;

        variableType_ = new VariableType_[numberOfVariables_];
        length_ = new int[numberOfVariables_];

        variableType_[0] = Enum.valueOf(VariableType_.class, "Permutation");

        readProblem(filename);
        System.out.println(numberOfCities_);
        length_[0] = numberOfCities_;

        /*System.out.println("Matriz de distancias");
        for (int k = 0; k < numberOfCities_; k++) {
            for (int j = 0; j < numberOfCities_; j++) {
                //System.out.print(distanceMatrix_[k][j] + "\t");
            } // for
            System.out.println();
        } // for
        System.out.println();
        //System.out.println("Matriz de tiempo");
        for (int k = 0; k < numberOfCities_; k++) {
            for (int j = 0; j < numberOfCities_; j++) {
                //System.out.print(timeMatrix_[k][j] + "\t");
            } // for
            System.out.println();
        } // for */
    } // TSP

    /** 
     * Constructor.
     * Creates a new instance of the Kursawe problem.
     * @param numberOfVariables Number of variables of the problem 
     * @param solutionType The solution type must "Real" or "BinaryReal". 
     */
    public TSP2(Integer numberOfVariables, String solutionType, String fileName) throws FileNotFoundException, IOException {
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

        fitness = 0.0;
        fitness2 = 0.0;

        for (int i = 0; i < (numberOfCities_ - 1); i++) {

            //1° Objetivo
            int x;
            int y;

            x = ((Permutation) solution.getDecisionVariables().variables_[0]).vector_[i];
            y = ((Permutation) solution.getDecisionVariables().variables_[0]).vector_[i + 1];
//  cout << "I : " << i << ", x = " << x << ", y = " << y << endl ;    
            fitness += distanceMatrix_[x][y];

            //2° Objetivo
            int x2;
            int y2;

            x2 = ((Permutation) solution.getDecisionVariables().variables_[0]).vector_[i];
            y2 = ((Permutation) solution.getDecisionVariables().variables_[0]).vector_[i + 1];
//  cout << "I : " << i << ", x = " << x << ", y = " << y << endl ;
            fitness2 += timeMatrix_[x2][y2];
        } // for

        int firstCity;
        int lastCity;

        firstCity = ((Permutation) solution.getDecisionVariables().variables_[0]).vector_[0];
        lastCity = ((Permutation) solution.getDecisionVariables().variables_[0]).vector_[numberOfCities_ - 1];
        fitness += distanceMatrix_[firstCity][lastCity];
        fitness2 += timeMatrix_[firstCity][lastCity];


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
            token.nextToken();

            distanceMatrix_ = new double[numberOfCities_][numberOfCities_];
            timeMatrix_ = new double[numberOfCities_][numberOfCities_];

            // Cargar objetivo 1
            System.out.println("Cargar Objetivo 1");
            for (int k = 0; k < numberOfCities_; k++) {
                for (int j = 0; j < numberOfCities_; j++) {
                    token.nextToken();
                    distanceMatrix_[k][j] = token.nval;

                    //System.out.println("X: " + k + ", Y: " + j + ", Valor: " + distanceMatrix_[k][j]);
                } // for
            } // for

            // Cargar objetivo 2
            for (int k = 0; k < numberOfCities_; k++) {
                for (int j = 0; j < numberOfCities_; j++) {
                    token.nextToken();
                    timeMatrix_[k][j] = token.nval;
                } // for
            } // for



        } // try
        catch (Exception e) {
            System.err.println("TSP.readProblem(): error when reading data file " + e);
            System.exit(1);
        } // catch

    } // readProblem
} // TSP
