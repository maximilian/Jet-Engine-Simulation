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
    
    private float engineDiameter;
    private float engineMassFlowRate;
    
    private final Spatial aircraft;
    
    public Aircraft(Spatial aircraft){
        this.aircraft = aircraft;
        
        this.engineDiameter = (float) 2.154;
        this.engineMassFlowRate = (float) 548.85;
        this.engineSetting = 100;
        
        this.speed = 160;
        this.altitude = 0;
    }
    
    
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
    
    public Spatial getSpatial(){
        return this.aircraft;
    }

    public float getEngineDiameter() {
        return this.engineDiameter;
    }
    
    /**
     * Returns the engine mass flow rate based on the current
     * engine setting (using relationship between mass flow and net thrust).
     * 
     * @return Mass flow rate, kg/s
     */
    public float getEngineMassFlowRate(){
        int correspondingMassFlow = 0;
        switch(engineSetting){
            // max engine setting
            case 100:
                correspondingMassFlow = 100;
                break;
            case 75:
                correspondingMassFlow = 87;
                break;
            case 50:
                correspondingMassFlow = 71;
                break;
            case 25:
                correspondingMassFlow = 51;
                break;
            // idle setting
            case 0: 
                correspondingMassFlow = 30;
                break;
            default: break;
            
        }

        return this.engineMassFlowRate * ((float) correspondingMassFlow/100);
    }
    
    /**
     * Sets the diameter of the engine fan, meters
     * 
     * @param engineDiameter fan diameter, m
     */
    
    public void setEngineDiameter(float engineDiameter){
        this.engineDiameter = engineDiameter;
    }
    
    /**
     * Sets the mass flow rate of the engine, kg/s
     * 
     * @param engineMassFlow mass flow rate of the engine, kg/s
     */
    
    public void setEngineMassFlow(float engineMassFlow){
        this.engineMassFlowRate = engineMassFlow;
    }
    
    /**
     * Returns the aircraft speed converted to the system's units
     * 
     * @return aircraft speed in system's units
     */
    public float getConvertedSpeed(){
        return (float) ((this.speed  * 0.514444) * 12.193);
    
    }
}
