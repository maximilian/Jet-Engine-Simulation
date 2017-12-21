/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculation;

/**
 *
 * @author max
 */
public class EngineArea {
    // International Standard Atmosphere variables
    private final float temperature;
    private final float pressure;
    
    /*
     * Engine information 
    */
    
    // Diameter of the engine fan blades, meters
    private final float engineDiameter;
    // Air mass flow rate through the engine, kg/s
    private final float engineFlowRate;
    
    private float correctedDensity;
    private float correctedPressure;
    private float correctedTemperature;
    
    private float altitude;
    private float speed;
    private float engineSetting;
    
    private boolean receivingLittle;
    private boolean receivingMuch;
    private boolean receivingCorrect;
    
    
    public EngineArea(){
        this.temperature = (float) 288.15;
        this.pressure = (float) 101325;
        
        this.engineDiameter = (float) 2.154;
        this.engineFlowRate = (float) 548.85;

    }
    
    /*
     * @param altitude, in feet, of the aircraft
     * @return 
    */
    
    public void setAircraftAltitude(float altitude){
        this.altitude = altitude;
    }
    
    /*
     * @param speed, in knots, of the aircraft
     * @return 
    */
    
    public void setAircraftSpeed(float speed){
        this.speed = speed;
    }
    
    /*
     * @param engine setting, in percentage, of the aircraft engine
     * @return 
    */
    
    public void setAircraftEngineSetting(float engineSetting){
        this.engineSetting = engineSetting;
    }
    
    /*
     * Returns the radius of the area around the engine
     *
     * @param altitude, in feet, of the aircraft
     * @param speed, in knots, of the aircraft
     * @param engine setting, in percentage, of the aircraft engine
     * @return the radius, in correct jME scale, of the area around the engine
    */
    public float calculateArea(){
        float correctedEngineFlowRate = getCorrectedMassFlow(altitude, engineFlowRate); 
        
        float engineRadius = engineDiameter / 2;
        float engineArea = (float) (Math.PI * (Math.pow(engineRadius, 2)));
        
        float speedMetres = (float) (0.514444444 * speed); // convert from knots to metres
   
        float airDensity = getCorrectedDensity(altitude);
        
        float engineNeeds = correctedEngineFlowRate / airDensity;
        float engineReceives = engineArea * speedMetres;
        
        if (engineReceives < engineNeeds){
            receivingLittle = true;
            System.out.println("too little");
        } else if(engineReceives > engineNeeds){
            receivingMuch = true;
            System.out.println("too much");
        } else {
            receivingCorrect = true;
            System.out.println("perfect");
        }

        float engineAreaRequired = engineNeeds / speedMetres;
        
        float engineRadiusRequired = (float) Math.sqrt((engineAreaRequired / Math.PI));
        
        float correctScale = (float) (engineRadiusRequired * 12.1943);
        
        System.out.println("Radius = " + engineRadiusRequired );
       return correctScale;   
    }
    
     /*
     * Returns the corrected temperature which can then be used to calculate
     * the corrected pressure and corrected density
     *
     * @param altitude, in feet, of the aircraft
     * @return the corrected temperature, in Kelvin
    */
    
    public float getCorrectedTemperature(float altitude){
        float meters = (float) (altitude / 3.2808);
        System.out.println("meters:" + meters);
        float correctedKelvin = (float) (288.15 - 0.0065*(meters));
        
        return correctedKelvin;
    }
    
    /*
     * Returns the corrected pressure which can then be used to calculate
     * the corrected density, and therefore the corrected air mass flow
     *
     * @param the correct temperature, in Kelvin
     * @return the corrected pressure, in Pascals
    */
    
    public float getCorrectedPressure(float correctedTemperature){
       
        correctedPressure = (float) (101325 * Math.pow((correctedTemperature/288.15),((9.80665/(287*0.0065)))));
        
        return correctedPressure;
    }
    
    /*
     * Returns the corrected density which can then be used to calculate
     *
     * @param the altitude, in feet
     * @return the corrected density, in kg/m^3
    */
    
    public float getCorrectedDensity(float altitude){
        
        correctedTemperature = getCorrectedTemperature(altitude);
        correctedPressure = getCorrectedPressure(correctedTemperature);
        
        
        correctedDensity = (correctedPressure/(287*correctedTemperature));
        
        return correctedDensity;
    }
    
    /*
     * Returns the corrected engine mass flow
     *
     * @param the altitude, in feet
     * @param the mass flow, in feet
     * @return the corrected density, in kg/m^3
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
