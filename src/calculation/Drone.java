/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculation;

import com.jme3.scene.Spatial;

/**
 *
 * @author max
 */
public class Drone {
    
    private int altitude;
    private int distanceFromAircraft;
    
    private Spatial drone;
    
    public Drone(Spatial drone){
        this.drone = drone;
    }
    
    public int getAltitude(){
        return this.altitude;
    }
    
    public int getDistanceFromAircraft(){
        return this.distanceFromAircraft;
    }
    
    public void setAltitude(int altitude){
        this.altitude = altitude;
    }
    
    public void setDistanceFromAircraft(int distance){
        this.distanceFromAircraft = distance;
    }
    
    public Spatial getSpatial(){
        return this.drone;   
    }
}
