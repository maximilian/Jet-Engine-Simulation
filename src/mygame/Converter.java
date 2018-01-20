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
    
    public float convertHgToPascals(float hg){
        return (float) (hg / 0.00029529980164712);
    }
    
    public float convertCelsiusToKelvin(float celsius){
            return (float) (celsius + 273.15);
    }

    /*
     * Given pressure and temperature, get density
    */
    
    public float getDensity(float pressure, float temperature){
        float correctedDensity = (pressure/(287*temperature));

        return correctedDensity;
    }
        
    public float getCorrectedMassFlow(float temperature, float pressure, float massFlow){        
        float theta = (float) (temperature/288.15);
        float delta = (float) (pressure/101325);
        
        float correctedFlow = (float) (massFlow / ((Math.sqrt(theta)) / delta));
        
        System.out.println("corrected flow:" + correctedFlow);
        return correctedFlow;
    }
}
