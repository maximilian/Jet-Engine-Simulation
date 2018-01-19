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
    
    private Aircraft aircraft;
    
    private boolean receivingLittle;
    private boolean receivingMuch;
    private boolean receivingCorrect;
    
    private float engineRadiusReal;
    private float engineRadiusScaled;
    
    
    
    public EngineArea(Aircraft aircraft){
        this.aircraft = aircraft;
        
        this.temperature = (float) 288.15;
        this.pressure = (float) 101325;
        
        this.engineDiameter = aircraft.getEngineDiameter();
        
        this.engineFlowRate = (float) 548.85;

    }
    
    
    /*
     * Returns the radius of the area around the engine. Note: maximum altitude 36,089 ft.
     *
     * @param altitude, in feet, of the aircraft
     * @param speed, in knots, of the aircraft
     * @param engine setting, in percentage, of the aircraft engine
     * @return the radius, in correct jME scale, of the area around the engine
    */
    public float calculateArea(){
        float correctedEngineFlowRate = getCorrectedMassFlow(aircraft.getAltitude(), engineFlowRate); 
        
        float engineRadius = engineDiameter / 2;
        float engineArea = (float) (Math.PI * (Math.pow(engineRadius, 2)));
        
        float speedMetres = (float) (0.514444444 * aircraft.getSpeed()); // convert from knots to metres
   
        float airDensity = getCorrectedDensity(aircraft.getAltitude());
        
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
        
        engineRadiusReal = (float) Math.sqrt((engineAreaRequired / Math.PI));
        
        
        float correctScale = (float) (engineRadiusReal * 12.1943);
        
        System.out.println("Radius = " + engineRadiusReal );
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
        
        return 101600;
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
        System.out.println("corrected density:"+correctedDensity);
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
        
        System.out.println("corrected flow:" + correctedFlow);
        return correctedFlow;
    }
    
    public boolean getReceivingLittle(){
        return receivingLittle;
    }
    
    public boolean getReceivingMuch(){
        return receivingMuch;
    }
    
    public float getEngineRadiusReal(){
     return engineRadiusReal;
    }
    
}
