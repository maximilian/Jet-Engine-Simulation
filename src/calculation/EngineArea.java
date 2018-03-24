package calculation;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import mygame.Converter;
import mygame.states.GuiAppState;
import org.xml.sax.SAXException;

/** Represents the area around the engine
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
   
    private boolean isaEnabled;
    
    /** Create the engine area object
     * 
     * @param aircraft the aircraft which the area belongs to
     * @param weatherType the source of the weather (live, ISA or custom)
     * @param gui the app state for the gui
     */
    public EngineArea(Aircraft aircraft, String weatherType, GuiAppState gui) {
        this.gui = gui;
        this.aircraft = aircraft;

        this.converter = new Converter();
        this.isa = new ISA();
        
        if(weatherType.equals("ISA")){
            isaEnabled = true;
        } else {
            isaEnabled = false;
        }
    }

    /**
     * Try and get the weather information.
     * 
     * If it's successful, use it. If not, use the ISA model
     */
    public void getWeather() {
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

    /**
     * Returns the radius of the area around the engine. Note: maximum altitude 36,089 ft.
     *
     */
    public float calculateArea() {
	// Fetches the weather - this could be live weather or custom weather
        getWeather();
        
        int aircraftAlt = aircraft.getAltitude();
        float engineRadius = aircraft.getEngineDiameter() / 2;
        
        // The area of the fan diameter
        float engineArea = (float) (Math.PI * (Math.pow(engineRadius, 2)));
        float speedMetres = converter.convertKnotsToMetersPerSecond(aircraft.getSpeed());

        float airDensity;
        float correctedEngineFlowRate;
        
        // if the engine is above sea level then use the ISA model for predicting high altitude conditions
        if (aircraftAlt == 0 && !isaEnabled) {
            airDensity = converter.getDensity(realPressure, realTemperature);
            correctedEngineFlowRate = converter.getCorrectedMassFlow(realTemperature, realPressure, aircraft.getEngineMassFlowRate());
        } else {
            airDensity = isa.getCorrectedDensity(aircraftAlt);
            correctedEngineFlowRate = isa.getCorrectedMassFlow(aircraft.getAltitude(), aircraft.getEngineMassFlowRate());
        }
        
        // volumetric flow rate of air that the engine needs to generate thrust
        float engineNeeds = correctedEngineFlowRate / airDensity;
        
        // volumetric flow rate of air that the engine actually receives
        float engineReceives = engineArea * speedMetres;
        
        // set the engine state by comparing what the engine receives vs what it needs
        setEngineState(engineReceives, engineNeeds);

        // area around the engine required to get the air it needs
        float engineAreaRequired = engineNeeds / speedMetres;

        engineRadiusReal = (float) Math.sqrt((engineAreaRequired / Math.PI));
        float correctScale = converter.convertMetersToSystemUnits(engineRadiusReal);
        
        return correctScale;
    }

    /**
     * Check whether the engine is receiving too little air
     * 
     * @return boolean
     */
    
    public boolean getReceivingLittle() {
        return receivingLittle;
    }
    
    /**
     * Check whether the engine is receiving too much air
     * 
     * @return boolean
     */
    public boolean getReceivingMuch() {
        return receivingMuch;
    }

    /**
     * Get the radius of the area around the engine, meters
     * 
     * @return radius of area around the engine, meters
     */
    public float getEngineRadiusReal() {
        return engineRadiusReal;
    }
    
    /**
     * Set the state of the engine. It can be in one of three states:
     * receiving too little, receiving too much or receiving the correct amount
     * 
     * @param engineReceives amount of air the engine is receiving
     * @param engineNeeds amount of air the engine actually needs
     */
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
