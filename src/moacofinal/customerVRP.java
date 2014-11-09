/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package moacofinal;

/**
 *
 * @author Christian Gomez
 */
//*********Clase Auxiliar para el VRPTW***********
public class customerVRP
{
	private double x;
	private double y;
	private double serviceTime;
	private double demanda;
	private double timeStart;
	private double timeEnd;
	public customerVRP()
	{
		}
	public void setCoord(double x,double y)
	{
		this.x =x;
		this.y =y;
	}
	public double getCoordX()
	{
		return x;
	}
	public double getCoordY()
	{
		return y;
	}
	public void setServiceTime(double servTime)
	{
		serviceTime =servTime;
	}
	public double getServiceTime()
	{
		return serviceTime;
	}
	public void setDemanda(double dem)
	{
		demanda =dem;
	}
	public double getDemanda()
	{
		return demanda;
	}
	public void setWindow(double begin,double end)
	{
		timeStart =begin;
		timeEnd =end;
	}
	public double getTimeStart()
	{
		return timeStart;
	}
	public double getTimeEnd()
	{
		return timeEnd;
	}
	
	@Override
	public String toString(){
		return "X:"+x+" Y:"+y+" D:"+demanda+" TW:["+timeStart+","+timeEnd+"]";
	}
}
