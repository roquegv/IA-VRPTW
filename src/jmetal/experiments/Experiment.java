/*
 * Experiment.java
 *
 * @author Antonio J. Nebro
 * @version 1.0
 * 
 * This is the base class to define experiments to be carried out with jMetal
 */
package jmetal.experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import jmetal.base.Algorithm;
import jmetal.base.Problem;
import jmetal.base.SolutionSet;
import jmetal.problems.ProblemFactory;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.JMException;

/**
 *
 * @author antonio
 */
public abstract class Experiment {

  String experimentName_;
  String[] algorithmNameList_; // List of the names of the algorithms to be executed
  String[] problemList_; // List of problems to be solved
  String[] paretoFrontFile_; // List of the files containing the pareto fronts
  // corresponding to the problems in problemList_
  String[] indicatorList_; // List of the quality indicators to be applied
  String experimentBaseDirectory_; // Directory to store the results
  String latexDirectory_ ; // Directory to store the latex files
  String paretoFrontDirectory_; // Directory containing the Pareto front files
  String outputParetoFrontFile_; // Name of the file containing the output 
  // Pareto front
  String outputParetoSetFile_; // Name of the file containing the output 
  // Pareto set
  int independentRuns_; // Number of independent runs per algorithm
  Settings[] algorithmSettings_; // Paremeter settings of each algorithm
  Algorithm[] algorithm_; // jMetal algorithms to be executed

  /**
   * Constructor
   * 
   * Contains default settings
   */
  public Experiment() {
    experimentName_ = "noName";

    algorithmNameList_ = null;
    problemList_ = null;
    paretoFrontFile_ = null;
    indicatorList_ = null;

    experimentBaseDirectory_ = "";
    paretoFrontDirectory_ = "";
    latexDirectory_ = "latex" ;
    
    outputParetoFrontFile_ = "FUN";
    outputParetoSetFile_ = "VAR";

    algorithmSettings_ = null;
    algorithm_ = null;

    independentRuns_ = 0;
  } // Constructor

  /**
   * Runs the experiment
   */
  public void runExperiment() throws JMException, IOException {
    int numberOfAlgorithms = algorithmNameList_.length;
    SolutionSet[] resultFront = new SolutionSet[numberOfAlgorithms];

    // Step 1: check experiment base directory
    checkExperimentDirectory();

    for (int problemId = 0; problemId < problemList_.length; problemId++) {
      Problem problem;   // The problem to solve

      // STEP 2: get the problem from the list
      Object[] params = {"Real"}; // Parameters of the problem
      problem = (new ProblemFactory()).getProblem(problemList_[problemId], params);

      // STEP 3: check the file containing the Pareto front of the problem
      if (indicatorList_.length > 0) {
        File pfFile = new File(paretoFrontDirectory_ + "/" + paretoFrontFile_[problemId]);
        if (pfFile.exists()) {
          paretoFrontFile_[problemId] = paretoFrontDirectory_ + "/" + paretoFrontFile_[problemId];
        } else {
          paretoFrontFile_[problemId] = "";
        }
      } // if

      for (int runs = 0; runs < independentRuns_; runs++) {
        // STEP 4: configure the algorithms
        algorithmSettings(problem, problemId);

        // STEP 5: run the algorithms
        for (int i = 0; i < numberOfAlgorithms; i++) {
          // STEP 6: create output directories
          File experimentDirectory;
          String directory;

          directory = experimentBaseDirectory_ + "/" + algorithmNameList_[i] + "/" +
                  problemList_[problemId];

          experimentDirectory = new File(directory);
          if (!experimentDirectory.exists()) {
            boolean result = new File(directory).mkdirs();
            System.out.println("Creating " + directory);
          }

          // STEP 7: run the algorithm
          System.out.println("Running algorithm: " + algorithmNameList_[i] +
                  ", problem: " + problemList_[problemId] +
                  ", run: " + runs);

          resultFront[i] = algorithm_[i].execute();

          // STEP 8: put the results in the output directory
          resultFront[i].printObjectivesToFile(directory + "/" + outputParetoFrontFile_ + "." + runs);
          resultFront[i].printVariablesToFile(directory + "/" + outputParetoSetFile_ + "." + runs);

          // STEP 9: calculate quality indicators 
          if (indicatorList_.length > 0) {
            QualityIndicator indicators;
            //System.out.println("PF file: " + paretoFrontFile_[problemId]);
            indicators = new QualityIndicator(problem, paretoFrontFile_[problemId]);

            for (int j = 0; j < indicatorList_.length; j++) {
              if (indicatorList_[j].equals("HV")) {
                double value = indicators.getHypervolume(resultFront[i]);
                FileWriter os = new FileWriter(experimentDirectory + "/HV", true);
                os.write("" + value + "\n");
                os.close();
              }
              if (indicatorList_[j].equals("SPREAD")) {
                double value = indicators.getSpread(resultFront[i]);
                FileWriter os = new FileWriter(experimentDirectory + "/SPREAD", true);
                os.write("" + value + "\n");
                os.close();
              }
              if (indicatorList_[j].equals("IGD")) {
                double value = indicators.getIGD(resultFront[i]);
                FileWriter os = new FileWriter(experimentDirectory + "/IGD", true);
                os.write("" + value + "\n");
                os.close();
              }
              if (indicatorList_[j].equals("EPSILON")) {
                double value = indicators.getEpsilon(resultFront[i]);
                FileWriter os = new FileWriter(experimentDirectory + "/EPSILON", true);
                os.write("" + value + "\n");
                os.close();
              }
            } // for
          } // if
        } // for
      } // for
    } //for
  } // runExperiment

  public void checkExperimentDirectory() {
    File experimentDirectory;

    experimentDirectory = new File(experimentBaseDirectory_);
    if (experimentDirectory.exists()) {
      System.out.println("Experiment directory exists");
      if (experimentDirectory.isDirectory()) {
        System.out.println("Experiment directory is a directory");
      } else {
        System.out.println("Experiment directory is not a directory. Deleting file and creating directory");
      }
      experimentDirectory.delete();
      boolean result = new File(experimentBaseDirectory_).mkdirs();
    } // if
    else {
      System.out.println("Experiment directory does NOT exist. Creating");
      boolean result = new File(experimentBaseDirectory_).mkdirs();
    } // else
  } // checkDirectories

  /**
   * Especifies the settings of each algorith. This method is checked in each
   * experiment run
   * @param problem Problem to solve
   * @param problemId Index of the problem in problemList_
   */
  public abstract void algorithmSettings(Problem problem, int problemId); // algorithmSettings

  public void generateLatexTables() throws FileNotFoundException, IOException {
    Vector[][][] data = new Vector[indicatorList_.length][][];
    for (int indicator = 0; indicator < indicatorList_.length; indicator++) {
      // A data vector per problem 
      data[indicator] = new Vector[problemList_.length][];

      for (int problem = 0; problem < problemList_.length; problem++) {
        data[indicator][problem] = new Vector[algorithmNameList_.length];

        for (int algorithm = 0; algorithm < algorithmNameList_.length; algorithm++) {
          data[indicator][problem][algorithm] = new Vector();

          String directory = experimentBaseDirectory_;
          directory += "/" + algorithmNameList_[algorithm];
          directory += "/" + problemList_[problem];
          directory += "/" + indicatorList_[indicator];
          // Read values from data files
          FileInputStream fis = new FileInputStream(directory);
          InputStreamReader isr = new InputStreamReader(fis);
          BufferedReader br = new BufferedReader(isr);
          System.out.println(directory);
          String aux = br.readLine();
          while (aux != null) {
            data[indicator][problem][algorithm].add(Double.parseDouble(aux));
            System.out.println(Double.parseDouble(aux));
            aux = br.readLine();
          } // while
        } // for    
      } // for
    } // for

    double[][][] mean;
    double[][][] median;
    double[][][] stdDeviation;
    double[][][] iqr;
    double[][][] max ;
    double[][][] min ;
    int   [][][] numberOfValues ;
    
    Map<String, Double> statValues = new HashMap<String, Double>();
    
    statValues.put("mean", 0.0);
    statValues.put("median", 0.0);
    statValues.put("stdDeviation", 0.0);
    statValues.put("iqr", 0.0);
    statValues.put("max", 0.0);
    statValues.put("min", 0.0);

    mean = new double[indicatorList_.length][][];
    median = new double[indicatorList_.length][][];
    stdDeviation = new double[indicatorList_.length][][];
    iqr = new double[indicatorList_.length][][];
    min = new double[indicatorList_.length][][];
    max = new double[indicatorList_.length][][];
    numberOfValues = new int[indicatorList_.length][][];
/*
          calculateStatistics(v, statValues);
          
          System.out.println("Mean: " + statValues.get("mean"));
          System.out.println("Median : " + statValues.get("median"));
          System.out.println("Std : " + statValues.get("stdDeviation"));
          System.out.println("IQR : " + statValues.get("iqr"));
          System.out.println("Min : " + statValues.get("min"));
          System.out.println("Max : " + statValues.get("max"));
 */

   
    for (int indicator = 0; indicator < indicatorList_.length; indicator++) {
      // A data vector per problem 
      mean[indicator] = new double[problemList_.length][];
      median[indicator] = new double[problemList_.length][];
      stdDeviation[indicator] = new double[problemList_.length][];
      iqr[indicator] = new double[problemList_.length][];
      min[indicator] = new double[problemList_.length][];
      max[indicator] = new double[problemList_.length][];
      numberOfValues[indicator] = new int[problemList_.length][];

      for (int problem = 0; problem < problemList_.length; problem++) {
        mean[indicator][problem] = new double[algorithmNameList_.length];
        median[indicator][problem] = new double[algorithmNameList_.length];
        stdDeviation[indicator][problem] = new double[algorithmNameList_.length];
        iqr[indicator][problem] = new double[algorithmNameList_.length];
        min[indicator][problem] = new double[algorithmNameList_.length];
        max[indicator][problem] = new double[algorithmNameList_.length];
        numberOfValues[indicator][problem] = new int[algorithmNameList_.length];

        for (int algorithm = 0; algorithm < algorithmNameList_.length; algorithm++) {
          Collections.sort(data[indicator][problem][algorithm]);

          String directory = experimentBaseDirectory_;
          directory += "/" + algorithmNameList_[algorithm];
          directory += "/" + problemList_[problem];
          directory += "/" + indicatorList_[indicator];

          //System.out.println("----" + directory + "-----");
          //calculateStatistics(data[indicator][problem][algorithm], meanV, medianV, minV, maxV, stdDeviationV, iqrV) ;
          calculateStatistics(data[indicator][problem][algorithm], statValues);
          /*
          System.out.println("Mean: " + statValues.get("mean"));
          System.out.println("Median : " + statValues.get("median"));
          System.out.println("Std : " + statValues.get("stdDeviation"));
          System.out.println("IQR : " + statValues.get("iqr"));
          System.out.println("Min : " + statValues.get("min"));
          System.out.println("Max : " + statValues.get("max"));
          System.out.println("N_values: " + data[indicator][problem][algorithm].size()) ;
          */
          mean[indicator][problem][algorithm] = statValues.get("mean");
          median[indicator][problem][algorithm] = statValues.get("median");
          stdDeviation[indicator][problem][algorithm] = statValues.get("stdDeviation");
          iqr[indicator][problem][algorithm] = statValues.get("iqr");
          min[indicator][problem][algorithm] = statValues.get("min");
          max[indicator][problem][algorithm] = statValues.get("max");
          numberOfValues[indicator][problem][algorithm] = data[indicator][problem][algorithm].size() ;
        }
      }
    }
    
    File latexOutput ;
    latexOutput = new File(latexDirectory_);
    if (!latexOutput.exists()) {
      boolean result = new File(latexDirectory_).mkdirs();
      System.out.println("Creating " + latexDirectory_ + " directory");
    }
    System.out.println("Experiment name: " + experimentName_) ;
    String latexFile = latexDirectory_ +"/" + experimentName_ + ".tex" ;
    printHeaderLatexCommands(latexFile) ;
    for (int i = 0; i < indicatorList_.length; i++) {
      printMeanStdDev(latexFile, i, mean, stdDeviation) ; 
      printMedianIQR(latexFile, i, median, iqr) ; 
    } // for
    printEndLatexCommands(latexFile) ;
  } // generateLatexTables

  /**
   * Calculates statistical values from a vector of Double objects
   * @param vector
   * @param values
   */
  void calculateStatistics(Vector vector,
          Map<String, Double> values) {

    double sum, minimum, maximum, sqsum, min, max, median, mean, iqr, stdDeviation;

    sqsum = 0.0;
    sum = 0.0;
    min = 1E300;
    max = -1E300;
    median = 0;

    for (int i = 0; i < vector.size(); i++) {
      double val = (Double) vector.elementAt(i);

      sqsum += val * val;
      sum += val;
      if (val < min) {
        min = val;
      }
      if (val > max) {
        max = val;
      }
    } // for

    // Mean
    mean = sum / vector.size();

    // Standard deviation
    if (sqsum / vector.size() - mean * mean < 0.0) {
      stdDeviation = 0.0;
    } else {
      stdDeviation = Math.sqrt(sqsum / vector.size() - mean * mean);
    }

    // Median
    if (vector.size() % 2 != 0) {
      median = (Double) vector.elementAt(vector.size() / 2);
    } else {
      median = ((Double) vector.elementAt(vector.size() / 2 - 1) +
              (Double) vector.elementAt(vector.size() / 2)) / 2.0;
    }

    values.put("mean", (Double) mean);
    values.put("median", calculateMedian(vector, 0, vector.size()-1));
    values.put("iqr", calculateIQR(vector)) ;
    values.put("stdDeviation", (Double) stdDeviation);
    values.put("min", (Double) min);
    values.put("max", (Double) max);

  } // calculateStatistics

  /**
   * Calculates the median of a vector considering the positions indicated by 
   * the parameters first and last
   * @param vector
   * @param first index of first position to consider in the vector
   * @param last index of last position to consider in the vector
   * @return The median
   */
  Double calculateMedian(Vector vector, int first, int last) {
    double median = 0.0;

    int size = last - first + 1 ;
    //System.out.println("size: " + size) ;
    
    if (size % 2 != 0) {
      median = (Double) vector.elementAt(first + size / 2);
    } else {
      median = ((Double) vector.elementAt(first + size / 2 - 1) +
              (Double) vector.elementAt(first + size / 2)) / 2.0;
    }

    return median;
  } // calculatemedian
  
  /**
   * Calculates the interquartile range (IQR) of a vector of Doubles
   * @param vector
   * @return The IQR
   */
  Double calculateIQR(Vector vector) {
    double q3     = 0.0 ;
    double q1     = 0.0 ;

    if (vector.size() % 2 != 0) {
      q3 = calculateMedian(vector, vector.size()/2+1, vector.size()-1) ;
      q1 = calculateMedian(vector, 0, vector.size()/2-1) ;
      //System.out.println("Q1: [" + 0 + ", " + (vector.size()/2 - 1) + "] = " + q1) ;
      //System.out.println("Q3: [" + (vector.size()/2+1) + ", " + (vector.size()-1) + "]= " + q3) ;
    } else {
      q3 = calculateMedian(vector, vector.size()/2, vector.size()-1) ;
      q1 = calculateMedian(vector, 0, vector.size()/2-1) ;
      //System.out.println("Q1: [" + 0 + ", " + (vector.size()/2 - 1) + "] = " + q1) ;
      //System.out.println("Q3: [" + (vector.size()/2) + ", " + (vector.size()-1) + "]= " + q3) ;
   }


    return q3 - q1;
  } // calculateIQR
  
  void printHeaderLatexCommands(String fileName) throws IOException {
    FileWriter os = new FileWriter(fileName, false);
    os.write("\\documentclass{article}"+ "\n");
    os.write("\\title{"+ experimentName_ +"}"+ "\n") ;
    os.write("\\author{}"+ "\n") ;
    os.write("\\begin{document}"+ "\n") ;
    os.write("\\maketitle"+ "\n") ;
    os.write("\\section{Tables}"+ "\n") ;

    os.close();
  }
  
  void printEndLatexCommands(String fileName) throws IOException {
    FileWriter os = new FileWriter(fileName, true);
    os.write("\\end{document}"+ "\n") ;
    os.close();
  } // printEndLatexCommands
  
  void printMeanStdDev(String fileName, int indicator,  double[][][] mean, double[][][]stdDev) throws IOException {
    FileWriter os = new FileWriter(fileName, true);
    os.write("\\"+"\n") ;
    os.write("\\begin{table}"+ "\n") ;
    os.write("\\caption{" + indicatorList_[indicator] + ". Mean and standard deviation}"+ "\n") ;
    os.write("\\label{table:mean."+indicatorList_[indicator]+"}"+ "\n") ;
    os.write("\\centering"+ "\n") ;
    os.write("\\begin{tabular}{l") ;

    // calculate the number of columns
    for (int i = 0; i < algorithmNameList_.length; i++)
      os.write("l") ;
    os.write("}\n") ;
 
    os.write("\\hline") ;
    // write table head
    for (int i = -1; i < algorithmNameList_.length; i++)
      if (i == -1)
        os.write(" & ") ;
      else if (i == (algorithmNameList_.length-1))
        os.write(" "+algorithmNameList_[i] + "\\\\"+ "\n") ;
      else
        os.write("" + algorithmNameList_[i] + " & ") ;
    os.write("\\hline"+ "\n") ;
    
    String m, s ;
    // write lines
    for (int i = 0 ; i < problemList_.length; i++) {
      os.write(problemList_[i] + " & ") ;
      for (int j = 0; j < (algorithmNameList_.length-1); j++) {
        
        m = String.format(Locale.ENGLISH,"%10.2e", mean[indicator][i][j]) ;
        s = String.format(Locale.ENGLISH,"%8.1e", stdDev[indicator][i][j]) ;
        os.write("$" + m + "_{" + s+ "}$ & " ) ;
      }
      m = String.format(Locale.ENGLISH,"%10.2e", mean[indicator][i][algorithmNameList_.length-1]) ;
      s = String.format(Locale.ENGLISH,"%8.1e", stdDev[indicator][i][algorithmNameList_.length-1]) ;
      os.write("$" + m + "_{" +s+ "}$ \\\\"+ "\n" ) ;
    } // for
    //os.write("" + mean[0][problemList_.length-1][algorithmNameList_.length-1] + "\\\\"+ "\n" ) ;
    
    os.write("\\hline" + "\n") ;
    os.write("\\end{tabular}"+ "\n") ;
    os.write("\\end{table}"+ "\n") ;
    os.close() ;
  } // printMeanStdDev
  
    void printMedianIQR(String fileName, int indicator,  double[][][] median, double[][][]IQR) throws IOException {
    FileWriter os = new FileWriter(fileName, true);
    os.write("\\"+"\n") ;
    os.write("\\begin{table}"+ "\n") ;
    os.write("\\caption{" + indicatorList_[indicator] + ". Median and IQR}"+ "\n") ;
    os.write("\\label{table:median."+indicatorList_[indicator]+"}"+ "\n") ;
    os.write("\\centering"+ "\n") ;
    os.write("\\begin{tabular}{l") ;

    // calculate the number of columns
    for (int i = 0; i < algorithmNameList_.length; i++)
      os.write("l") ;
    os.write("}\n") ;
 
    os.write("\\hline") ;
    // write table head
    for (int i = -1; i < algorithmNameList_.length; i++)
      if (i == -1)
        os.write(" & ") ;
      else if (i == (algorithmNameList_.length-1))
        os.write(" "+algorithmNameList_[i] + "\\\\"+ "\n") ;
      else
        os.write("" + algorithmNameList_[i] + " & ") ;
    os.write("\\hline"+ "\n") ;
    
    String m, s ;
    // write lines
    for (int i = 0 ; i < problemList_.length; i++) {
      os.write(problemList_[i] + " & ") ;
      for (int j = 0; j < (algorithmNameList_.length-1); j++) {
        
        m = String.format(Locale.ENGLISH,"%10.2e", median[indicator][i][j]) ;
        s = String.format(Locale.ENGLISH,"%8.1e", IQR[indicator][i][j]) ;
        os.write("$" + m + "_{" + s+ "}$ & " ) ;
      }
      m = String.format(Locale.ENGLISH,"%10.2e", median[indicator][i][algorithmNameList_.length-1]) ;
      s = String.format(Locale.ENGLISH,"%8.1e", IQR[indicator][i][algorithmNameList_.length-1]) ;
      os.write("$" + m + "_{" +s+ "}$ \\\\"+ "\n" ) ;
    } // for
    //os.write("" + mean[0][problemList_.length-1][algorithmNameList_.length-1] + "\\\\"+ "\n" ) ;
    
    os.write("\\hline" + "\n") ;
    os.write("\\end{tabular}"+ "\n") ;
    os.write("\\end{table}"+ "\n") ;
    os.close() ;
  } // printMedianIQR
} // Experiment
