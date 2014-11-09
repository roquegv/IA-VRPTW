/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package moacofinal;

import java.io.PrintStream;

/**
 *
 * @author Christian Gomez
 */

//********** Clase SolucionVRP ***********
public class SolucionVRP extends Solucion
{
	private int camiones;
	private int sizeActual;
	private void duplicar_size()
	{
		int arrayAnterior[] = array;
		array = new int[size*2];
		for(int i =0;i<size;i++)
			array[i]=arrayAnterior[i];
		for(int i =size;i<size*2;i++)
			array[i]=-1;
		size*=2;
	}
        
	public SolucionVRP(int tam)
	{
		super(tam);
		camiones =1;
		sizeActual =0;
	}
        
	public void setCamiones(int nro)
	{
		camiones =nro;
	}
	public void incCamiones()
	{
		camiones++;
	}
	public int getCamiones()
	{
		return camiones;
	}
	public void add(int valor)
	{
		if(sizeActual+1 >= size)
			duplicar_size();
		array[sizeActual]=valor;
		sizeActual++;
	}
	public int getSizeActual()
	{
		return sizeActual;
	}
	public void imprimir(PrintStream f)
	{
		for (int i =0;i<sizeActual-1;i++)
                    f.print(array[i]+"-");
                f.print(array[sizeActual-1]+"-");
	}
	public void resetear()
	{
		for(int i =0;i<size;i++)
			array[i]=-1;
		sizeActual =0;
		camiones =0;
	}
	public void solcpy(SolucionVRP sol)
	{
		for(int i =0;i<sol.getSizeActual();i++)
			array[i]=sol.get(i);
		for(int i =sol.getSizeActual();i<size;i++)
			array[i]=-1;
		sizeActual =sol.getSizeActual();
		camiones =sol.getCamiones();
	}
}

