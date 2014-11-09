/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jmetal.solutions.vrptw;

/**
 *
 * @author Alida
 */
public class CustomerVRP {
 
    /*privat*/ double x,y, serviceTime,demanda;
    /*private*/ double timeStart,timeEnd;
    
    public void CustomerVRP (){
        x = 0; y = 0; 
        serviceTime = 0; demanda = 0;
        timeStart =0 ; timeEnd = 0;
    }
    public double getDemanda() {
        return demanda;
    }

    public void setDemanda(double demanda) {
        this.demanda = demanda;
    }

    public double getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(double serviceTime) {
        this.serviceTime = serviceTime;
    }

    public double getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(double timeEnd) {
        this.timeEnd = timeEnd;
    }

    public double getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(double timeStart) {
        this.timeStart = timeStart;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
    
    

}
