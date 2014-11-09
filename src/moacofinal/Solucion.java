/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package moacofinal;

/**
 *
 * @author Christian Gomez
 */
import java.io.PrintStream;

public class Solucion
{
	protected int array[];
	protected int size;

        public Solucion(int tam)
	{
		array = new int[tam];
		for(int i=0;i<tam;i++)
			array[i]=-1;
		size =tam;
	}

	public void resetear()
	{
		for(int i =0;i<size;i++)
			array[i]=-1;
	}

	public void set(int pos,int valor)
	{
		array[pos]=valor;
	}
        
	public int get(int pos)
	{
		return array[pos];
	}
        
	public int getSize()
	{
		return size;
	}
        
	public void imprimir(PrintStream f)
	{
		for (int i =0;i<size-1;i++)
			f.print(array[i]+"-");
                f.print(array[size-1]);
	}
	public void imprimir()
	{
		for (int i =0;i<size-1;i++)
			System.out.print(array[i]+"-");

		System.out.println(array[size-1]);
	}

        public void solcpy(Solucion sol)
	{
		for(int i =0;i<sol.getSize();i++)
			array[i]=sol.get(i);
	}
}