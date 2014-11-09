/**
 * TSP.java
 *
 * @author Antonio J. Nebro
 * @version 1.0
 */
package jmetal.problems.singleObjective;

import jmetal.base.*;
import jmetal.base.Configuration.SolutionType_;
import jmetal.base.Configuration.VariableType_;
import jmetal.base.variable.Permutation;

import java.io.*;

/**
 * Class representing a TSP (Traveling Salesman Problem) problem.
 */
public class TSP extends Problem {

    public int numberOfCities_;
    public double[][] distanceMatrix_;

    /**
     * Creates a new TSP problem instance. It accepts data files from TSPLIB
     * @param filename The file containing the definition of the problem
     */
    public TSP(String filename) throws FileNotFoundException, IOException {
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
    } // TSP


    /** 
     * Constructor.
     * Creates a new instance of the Kursawe problem.
     * @param numberOfVariables Number of variables of the problem 
     * @param solutionType The solution type must "Real" or "BinaryReal". 
     */
    public TSP(Integer numberOfVariables, String solutionType, String fileName) throws FileNotFoundException, IOException {
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

        fitness = 0.0;

        for (int i = 0; i < (numberOfCities_ - 1); i++) {
            int x;
            int y;

            x = ((Permutation) solution.getDecisionVariables().variables_[0]).vector_[i];
            y = ((Permutation) solution.getDecisionVariables().variables_[0]).vector_[i + 1];
//  cout << "I : " << i << ", x = " << x << ", y = " << y << endl ;    
            fitness += distanceMatrix_[x][y];
        } // for

        int firstCity;
        int lastCity;

        firstCity = ((Permutation) solution.getDecisionVariables().variables_[0]).vector_[0];
        lastCity = ((Permutation) solution.getDecisionVariables().variables_[0]).vector_[numberOfCities_ - 1];
        fitness += distanceMatrix_[firstCity][lastCity];

        solution.setObjective(0, fitness);
    } // evaluate


    public void readProblem(String fileName) throws FileNotFoundException,
            IOException {

        Reader inputFile = new BufferedReader(
                new InputStreamReader(
                new FileInputStream(fileName)));

        StreamTokenizer token = new StreamTokenizer(inputFile);
        try {
            boolean found;
            found = false;

            token.nextToken();
            while (!found) {
                if ((token.sval != null) && ((token.sval.compareTo("DIMENSION") == 0))) {
                    found = true;
                } else {
                    token.nextToken();
                }
            } // while

            token.nextToken();
            token.nextToken();

            numberOfCities_ = (int) token.nval;

            distanceMatrix_ = new double[numberOfCities_][numberOfCities_];

            // Find the string SECTION  
            found = false;
            token.nextToken();
            while (!found) {
                if ((token.sval != null) &&
                        ((token.sval.compareTo("SECTION") == 0))) {
                    found = true;
                } else {
                    token.nextToken();
                }
            } // while

            // Read the data

            double[] c = new double[2 * numberOfCities_];

            for (int i = 0; i < numberOfCities_; i++) {
                token.nextToken();
                int j = (int) token.nval;

                token.nextToken();
                c[2 * (j - 1)] = token.nval;
                token.nextToken();
                c[2 * (j - 1) + 1] = token.nval;
            } // for

            double dist;
            for (int k = 0; k < numberOfCities_; k++) {
                distanceMatrix_[k][k] = 0;
                for (int j = k + 1; j < numberOfCities_; j++) {
                    dist = Math.sqrt(Math.pow((c[k * 2] - c[j * 2]), 2.0) +
                            Math.pow((c[k * 2 + 1] - c[j * 2 + 1]), 2));
                    dist = (int) (dist + .5);
                    distanceMatrix_[k][j] = dist;
                    distanceMatrix_[j][k] = dist;
                } // for

            } // for

        } // try
        catch (Exception e) {
            System.err.println("TSP.readProblem(): error when reading data file " + e);
            System.exit(1);
        } // catch

    } // readProblem

} // TSP
