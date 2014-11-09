/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package moacofinal;

/**
 *
 * @author Christian Gomez
 */

//***********Clase Tabla Feromonas***************
public class TablaFeromona
{
	private int size;
	private double[][] tabla;
	public TablaFeromona(int tam)
	{
		size =tam;
		tabla = new double[tam][tam];
	}
	public double obtenerValor(int estOrigen, int estDestino)
	{
		return tabla[estOrigen][estDestino];
	}
	public void actualizar(int estOrigen, int estDestino, double tau)
	{
		tabla[estOrigen][estDestino] = tau;
	}
	public void reiniciar(double tau0)
	{
		for (int i =0; i<size; i++)
		{
			for (int j =0; j<size; j++)
			{
				tabla[i][j] = tau0;
			}
		}
	}
	public void imprimir()
	{
		for (int i =0; i<size; i++)
		{
			for (int j =0; j<size; j++)
			{
				System.out.printf("%lf ",tabla[i][j]);
			}
			System.out.print("\n");
		}
	}
}
