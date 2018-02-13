/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculation;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import mygame.Converter;
import mygame.states.GuiAppState;
import org.xml.sax.SAXException;

/**
 *
 * @author max
 */
public class EngineArea {

    /*
     * Engine information 
     */

    private boolean receivingLittle;
    private boolean receivingMuch;
    private boolean receivingCorrect;

    private float engineRadiusReal;

    private final GuiAppState gui;
    private final Aircraft aircraft;
    private final Converter converter;
    private final ISA isa;

    private float realPressure;
    private float realTemperature;

    public EngineArea(Aircraft aircraft, GuiAppState gui) {
        this.gui = gui;
        this.aircraft = aircraft;

        this.converter = new Converter();
        this.isa = new ISA();
    }

    public void getWeather() {
        /* Get weather information
         * 
         * If successful, use it. Else, use ISA values.
         */
        try {
            this.realPressure = converter.convertHgToPascals(gui.getWeather().getPressure());
            this.realTemperature = converter.convertCelsiusToKelvin(gui.getWeather().getTemperature());
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
    public float calculateArea() {
        getWeather();
        
        int aircraftAlt = aircraft.getAltitude();
        float engineRadius = aircraft.getEngineDiameter() / 2;
        
        float engineArea = (float) (Math.PI * (Math.pow(engineRadius, 2)));
        float speedMetres = converter.convertKnotsToMetersPerSecond(aircraft.getSpeed());

        float airDensity;

        float correctedEngineFlowRate;
        if (aircraftAlt == 0) {
            airDensity = converter.getDensity(realPressure, realTemperature);
            correctedEngineFlowRate = converter.getCorrectedMassFlow(realTemperature, realPressure, aircraft.getEngineMassFlowRate());
        } else {
            airDensity = isa.getCorrectedDensity(aircraftAlt);
            correctedEngineFlowRate = isa.getCorrectedMassFlow(aircraft.getAltitude(), aircraft.getEngineMassFlowRate());
        }

        float engineNeeds = correctedEngineFlowRate / airDensity;
        float engineReceives = engineArea * speedMetres;
        
        setEngineState(engineReceives, engineNeeds);

        float engineAreaRequired = engineNeeds / speedMetres;

        engineRadiusReal = (float) Math.sqrt((engineAreaRequired / Math.PI));

        float correctScale = converter.convertMetersToSystemUnits(engineRadiusReal);

        System.out.println("Radius = " + engineRadiusReal);
        return correctScale;
    }

    public boolean getReceivingLittle() {
        return receivingLittle;
    }

    public boolean getReceivingMuch() {
        return receivingMuch;
    }

    public float getEngineRadiusReal() {
        return engineRadiusReal;
    }
    
    public void setEngineState(float engineReceives, float engineNeeds){
        
        if (engineReceives < engineNeeds) {
            receivingLittle = true;
            receivingMuch = false;

        } else if (engineReceives > engineNeeds) {
            receivingMuch = true;
            receivingLittle = false;
           
        } else {
            receivingCorrect = true;
        }
    }

}
