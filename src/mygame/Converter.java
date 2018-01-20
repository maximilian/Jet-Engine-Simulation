/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

/**
 * Contains different methods for converting between units
 * 
 * @author max
 */
public class Converter {
    
    public float convertKnotsToMetersPerSecond(int knots){
        float metersPerSecond = (float) (knots * 0.51444444);
        
        return metersPerSecond;
    }
    
    public float convertMetersToSystemUnits(float meters){
        return (float) (meters * 12.1943);
    }
    
    public int convertHgToMillibars(float hg){
        
        float millibars = (float) (hg / 0.029529980164712);
        return Math.round(millibars);
    
    }
}
