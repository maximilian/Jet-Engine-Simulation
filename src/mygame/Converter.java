package mygame;

/**
 * Contains different methods for converting between units
 * 
 * @author Maximilian Morell
 */
public class Converter {
    
    /**
     * Converts from knots to meters per second
     * 
     * @param knots Speed in knots
     * @return metersPerSecond speed in meters per second
     */
    public float convertKnotsToMetersPerSecond(int knots){
        float metersPerSecond = (float) (knots * 0.51444444);
        
        return metersPerSecond;
    }
    
    /**
     * Converts from meters to system units
     * 
     * @param meters distance in meters
     * @return distance in system units
     */
    public float convertMetersToSystemUnits(float meters){
        return (float) (meters * 12.1943);
    }
    
    /**
     * Converts from system units to meters
     * 
     * @param systemUnits distance in system units
     * @return distance in meters
     */
    public float convertSystemUnitsToMeters(float systemUnits){
        return (float) (systemUnits / 12.1943);
    }
    
    /**
     * Convert inches of mercury to millibars
     * 
     * @param hg pressure in inches of mercury
     * @return pressure in millibars
     */
    public int convertHgToMillibars(float hg){      
        float millibars = (float) (hg / 0.029529980164712);
        return Math.round(millibars);
    }
    
    /**
     * Convert millibars to inches of mercury
     * 
     * @param millibars pressure in millibars
     * @return pressure in inches of mercury
     */
    public float convertMillibarsToHg(int millibars){      
        float Hg = (float) (millibars * 0.029529980164712);
        return Hg;
    }
    
    /**
     * Convert inches of mercury to pascals
     * @param hg pressure in inches of mercury
     * @return pressure in pascals
     */
    public float convertHgToPascals(float hg){
        return (float) (hg / 0.00029529980164712);
    }
    
    /**
     * Convert degrees Celsius to degrees Fahrenheit
     * 
     * @param celsius degrees in Celsius
     * @return degrees in Fahrenheit
     */
    public float convertCelsiusToKelvin(float celsius){
            return (float) (celsius + 273.15);
    }

   
    /**
     * Convert pressure and temperature to density
     * 
     * @param pressure atmospheric pressure
     * @param temperature atmopsheric temperature
     * @return correctedDensity atmopsheric density
     */
    public float getDensity(float pressure, float temperature){
        float correctedDensity = (pressure/(287*temperature));

        return correctedDensity;
    }
    
    /**
     * Correct mass flow rate for altitude.
     * 
     * @param temperature atmospheric temperature, degrees Celsius
     * @param pressure atmospheric pressure, Pascals
     * @param massFlow sea level mass flow rate, kg/s
     * @return correctedFlow corrected mass flow rate, kg/s
     */
    public float getCorrectedMassFlow(float temperature, float pressure, float massFlow){        
        float theta = (float) (temperature/288.15);
        float delta = (float) (pressure/101325);
        
        float correctedFlow = (float) (massFlow / ((Math.sqrt(theta)) / delta));
        
        System.out.println("corrected flow:" + correctedFlow);
        return correctedFlow;
    }
}
