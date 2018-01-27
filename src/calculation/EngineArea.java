/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculation;

import Weather.WeatherData;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import mygame.Converter;
import org.xml.sax.SAXException;

/**
 *
 * @author max
 */
public class EngineArea {
    /*
     * Engine information 
    */
    
    // Diameter of the engine fan blades, meters
    private final float engineDiameter;
    // Air mass flow rate through the engine, kg/s
    private final float engineFlowRate;
    
    private boolean receivingLittle;
    private boolean receivingMuch;
    private boolean receivingCorrect;
    
    private float engineRadiusReal;
        
    private final Aircraft aircraft;
    private final Converter converter;
    private final ISA isa;
    private final WeatherData weather;
    
    private float realPressure;
    private float realTemperature;
    
    public EngineArea(Aircraft aircraft){
        this.aircraft = aircraft;
        this.engineDiameter = aircraft.getEngineDiameter();
        this.engineFlowRate = (float) 548.85;
        
        this.converter = new Converter();
        this.isa = new ISA();
        this.weather = new WeatherData();
        
        /* Try and download the real weather
         * 
         * If successful, use it. Else, use ISA values.
        */
        try {
            this.realPressure = converter.convertHgToPascals(weather.getPressure());
            this.realTemperature = converter.convertCelsiusToKelvin(weather.getTemperature());
        } catch (IOException ex) {
            realPressure = 101325;
            realTemperature = (float) 288.15;
        } catch (SAXException ex) {
            realPressure = 101325;
            realTemperature = (float) 288.15;
        } catch (ParserConfigurationException ex) {
            realPressure = 101325;
            realTemperature = (float) 288.15;
        }
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
        int aircraftAlt = aircraft.getAltitude();

        float engineRadius = engineDiameter / 2;
        float engineArea = (float) (Math.PI * (Math.pow(engineRadius, 2)));
        
        float speedMetres = converter.convertKnotsToMetersPerSecond(aircraft.getSpeed());
           
        /*
         * TEMPERATURE MAY NEED TO BE IN KELVIN!
        */
        
        float airDensity;
        float airTemperature;
        float airPressure;
                
        float correctedEngineFlowRate;
        if (aircraftAlt == 0){
            airDensity = converter.getDensity(realPressure, realTemperature);
            correctedEngineFlowRate = converter.getCorrectedMassFlow(realTemperature, realPressure, engineFlowRate);
            System.out.println("REAL DENSITY IS:"+airDensity);
             System.out.println("REAL FLOW RATE IS:"+correctedEngineFlowRate);
        }else {
            airDensity = isa.getCorrectedDensity(aircraftAlt);
            System.out.println("ISA DENSITY IS:"+airDensity);
            correctedEngineFlowRate =  isa.getCorrectedMassFlow(aircraft.getAltitude(), engineFlowRate);
            System.out.println("ISA FLOW RATE IS:"+correctedEngineFlowRate);
        }
        
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
        
        float correctScale = converter.convertMetersToSystemUnits(engineRadiusReal);
        
        System.out.println("Radius = " + engineRadiusReal );
        return correctScale;   
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
