/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Weather;

/** Represents the ISA model
 * 
 * 
 * @author Maximilian Morell
 */
public class ISA {
    
    private float correctedPressure;
    private float correctedTemperature;
    private float correctedDensity;
    
    /**
     * Returns the corrected temperature which can then be used to calculate
     * the corrected pressure and corrected density
     *
     * @param altitude altitude of aircraft, feet
     * @return corrected temperature, Kelvin
    */
    
    public float getCorrectedTemperature(float altitude){
        float meters = (float) (altitude / 3.2808);

        float correctedKelvin = (float) (288.15 - 0.0065*(meters));
                System.out.println("temperature is"+correctedKelvin);
        return correctedKelvin;
    }
    
    /**
     * Returns the corrected pressure which can then be used to calculate
     * the corrected density, and therefore the corrected air mass flow
     *
     * @param correctedTemperature the corrected temperature, Kelvin
     * @return correctedPressure the corrected pressure, Pascals
    */
    
    public float getCorrectedPressure(float correctedTemperature){
        correctedPressure = (float) (101325 * Math.pow((correctedTemperature/288.15),((9.80665/(287*0.0065)))));
        
        return correctedPressure;
    }
    
    /**
     * Returns the corrected density based on the temperature
     * and pressure
     *
     * @param altitude the altitude which the model is focused on, feet
     * @return correctedDensity the corrected density, kg/m^3
    */
    
    public float getCorrectedDensity(float altitude){
        
        correctedTemperature = getCorrectedTemperature(altitude);
        correctedPressure = getCorrectedPressure(correctedTemperature);
            
        correctedDensity = (correctedPressure/(287*correctedTemperature));
        
        return correctedDensity;
    }
    
    /**
     * Returns the corrected engine mass flow
     *
     * @param altitude the specified altitude, feet
     * @param massFlow mass flow rate at sea level, kg/s
     * @return correctedFlow corrected mass flow rate due to altitude
    */
    
    public float getCorrectedMassFlow(float altitude, float massFlow){
        correctedTemperature = getCorrectedTemperature(altitude);
        correctedPressure = getCorrectedPressure(correctedTemperature);
        
        float theta = (float) (correctedTemperature/288.15);
        float delta = (float) (correctedPressure/101325);
        
        float correctedFlow = (float) (massFlow / ((Math.sqrt(theta)) / delta));

        return correctedFlow;
    }
}
