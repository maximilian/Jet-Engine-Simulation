/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculation;

import Weather.WeatherData;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import mygame.Converter;
import org.xml.sax.SAXException;

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
    
    private boolean receivingLittle;
    private boolean receivingMuch;
    private boolean receivingCorrect;
    
    private float engineRadiusReal;
        
    private final Aircraft aircraft;
    private final Converter converter;
    private final ISA isa;
    private final WeatherData weather;
    
    public EngineArea(Aircraft aircraft){
        this.aircraft = aircraft;
        
        this.temperature = (float) 288.15;
        this.pressure = (float) 101325;
        
        this.engineDiameter = aircraft.getEngineDiameter();
        
        this.engineFlowRate = (float) 548.85;
        
        this.converter = new Converter();
        this.isa = new ISA();
        this.weather = new WeatherData();
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
        float airDensity;
        
        float correctedEngineFlowRate = isa.getCorrectedMassFlow(aircraft.getAltitude(), engineFlowRate); 
        
        float engineRadius = engineDiameter / 2;
        float engineArea = (float) (Math.PI * (Math.pow(engineRadius, 2)));
        
        float speedMetres = converter.convertKnotsToMetersPerSecond(aircraft.getSpeed());
           
        /*
         * TEMPERATURE MAY NEED TO BE IN KELVIN!
        */
        
        if (aircraftAlt == 0){
            try {
                airDensity = converter.getDensity(weather.getPressure(), (float) 1.0);
            } catch (IOException ex) {
                airDensity = isa.getCorrectedDensity(aircraftAlt);
            } catch (SAXException ex) {
                airDensity = isa.getCorrectedDensity(aircraftAlt);
            } catch (ParserConfigurationException ex) {
                airDensity = isa.getCorrectedDensity(aircraftAlt);
            }
        }else {
            airDensity = isa.getCorrectedDensity(aircraftAlt);
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
