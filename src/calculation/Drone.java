/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculation;

import com.jme3.scene.Spatial;

/** Represents a drone object
 *
 * @author max
 */

public class Drone {
    
    private int altitude;
    private int distanceFromAircraft;
    
    private Spatial drone;
    
    /**
     * Create a drone object with the drone spatial
     * 
     * @param drone the drone spatial used in the scene graph
     */
    public Drone(Spatial drone){
        this.drone = drone;
    }
    
    /**
     * Gets the altitude of the drone, in feet.
     *
     * @return altitude of drone, feet
     */
    
    public int getAltitude(){
        return this.altitude;
    }
    
    /**
     * Gets the distance, in meters, that the drone is from the aircraft
     * 
     * @return the distance, meters
     */
    public int getDistanceFromAircraft(){
        return this.distanceFromAircraft;
    }
    
    /**
     * Gets the horizontal distance, in system units, of the drone from the aircraft
     *
     * @return the distance in the system's units
     */
    public float getConvertedDistanceFromAircraft(){
        return (float) (this.distanceFromAircraft * 12.193);
    }
    
    /**
     * Sets the altitude of the aircraft
     * 
     * @param altitude 
     */
    public void setAltitude(int altitude){
        this.altitude = altitude;
    }
    
    /**
     * Sets the horizontal distance of the drone from the aircraft
     *
     * @param distance
    */
    
    public void setDistanceFromAircraft(int distance){
        this.distanceFromAircraft = distance;
    }
    
    /**
     * Return the spatial for the Drone object
     * 
     * @return 
     */
    public Spatial getSpatial(){
        return this.drone;   
    }
    
    
}
