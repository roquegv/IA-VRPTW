/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package moacofinal;

//*********** Clase Problema ************

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class Problem
{
	protected double[][] matrizAdy;
	protected int size;
	public Problem(String file)
	{
            int i;
            BufferedReader reader  =null; 
            try {
                reader = new BufferedReader(new FileReader(file));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            String linea = null;
            boolean numerico = false;
            while (!numerico) {
                try{
                    linea = reader.readLine();
                    size = Integer.parseInt(linea);
                    numerico = true;
                }catch(IOException e){
                    numerico = true;
                }catch(NullPointerException e){
                    numerico = true;
                }catch(NumberFormatException ex){
                    numerico = false;
                }
            }
	matrizAdy = new double[size][size];
        try{
            reader.close();
        }catch(Exception e){
            ;
        }
        
	}

        public int getSize()
	{
		return size;
	}

        public double getDistancia(int i,int j)
	{
		return matrizAdy[i][j];
	}
        protected abstract void cargar_estado(String file);
	public abstract double funcion_obj_1(Solucion sol);
	public abstract double funcion_obj_2(Solucion so);
	public abstract double heuristica_1(int estOrigen,int estDest);
	public abstract double heuristica_2(int estOrigen,int estDest);
	
}

