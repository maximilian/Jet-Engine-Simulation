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
public class Aircraft {
    private int altitude;
    private int speed;
    private int engineSetting;
    
    private Spatial aircraftObject;
    
    
    public int getSpeed(){
        return this.speed;
    }
    
    public int getAltitude(){
        return this.altitude;
    }
    
    public int getEngineSetting(){
        return this.engineSetting;
    }
    
    public void setSpeed(int speed){
        this.speed = speed;
    }
    
    public void setAltitude(int altitude){
        this.altitude = altitude;
    }
    
    public void setEngineSetting(int engineSetting){
        this.engineSetting = engineSetting;
    }
}
