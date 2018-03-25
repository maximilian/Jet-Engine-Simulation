package mygame.states;

import Camera.AircraftCamera;
import Weather.WeatherData;
import mygame.Aircraft;
import mygame.Drone;
import calculation.EngineArea;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import de.lessvoid.nifty.Nifty;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import mygame.Converter;
import mygame.Project;
import mygame.ResourceLoader;
import mygame.Simulation;
import mygame.gui.MyControlScreen;
import mygame.gui.MyOptionsScreen;
import org.xml.sax.SAXException;

/**
 * Intermediary between the the GUI control screens and the actual game engine
 * 
 * @author Maximilian Morell
 */
public class GuiAppState extends AbstractAppState {
    AppStateManager stateManager;
    
    private Project app;
    private EngineArea engineArea;
    private Aircraft aircraft;
    private Drone drone;
    
    private NiftyJmeDisplay niftyDisplay;
    private Nifty nifty;
    
    private MyOptionsScreen optionScreen;
    private MyControlScreen controlScreen;
    private Camera flyCam;
    private AircraftCamera aircraftView;
    private ResourceLoader loader;
    private Node rootNode;

    private int altitudeDisplacement;

    private boolean showForwardArea;
    
    Spatial rightForwardArea;
    Spatial leftForwardArea;   
    
    private Simulation simulation;
    
    private WeatherData weather;
    private Converter converter;
    
    private String weatherType;
    
    /**
     * Initialises the app state
     * 
     * @param stateManager game engine's state manager
     * @param app contains fundamental methods from the Application class used across the system
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        this.app = (Project) app;
        this.stateManager = stateManager;
        this.flyCam = this.app.getCamera();
        this.aircraftView = this.app.getAircraftCamera();
        this.loader = this.app.getResourceLoader();
        this.rootNode = this.app.getRootNode();
        this.aircraft = this.app.getAircraft();
        this.drone = this.app.getDrone();
        this.weatherType = "live";
        this.weather = new WeatherData("EGPF");
        this.engineArea = new EngineArea(aircraft, weatherType, this);

        this.converter = new Converter();
        
        
        // Camera view on load
        frontView();
        submitAircraftVariables();
        
        niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(app.getAssetManager(), app.getInputManager(), app.getAudioRenderer(), app.getGuiViewPort());
        nifty = niftyDisplay.getNifty();
        
        // Read the XML and initialize the custom ScreenController
        controlScreen = new MyControlScreen(this, "live");
        
        nifty.fromXml("Interface/screen.xml", "start", controlScreen);
        // attach the Nifty display to the gui view port as a processor
        app.getGuiViewPort().addProcessor(niftyDisplay);

        updateWeatherScreen();
    } 
    
    /**
     * Updates the weather information provided on the main screen.
     */
    public void updateWeatherScreen(){
        try {
            int convertedPressure = converter.convertHgToMillibars(weather.getPressure());


            controlScreen.setWeatherInformation(weather.getFieldName(), convertedPressure, weather.getTemperature(), weather.getDateTime());
        } catch (IOException ex) {
            Logger.getLogger(GuiAppState.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(GuiAppState.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(GuiAppState.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void update(float tpf) {

    }
    
    @Override
    public void cleanup() {
        super.cleanup();
    }

    /**
     * Sets the view to the front of the aircraft.
     */
    public void frontView() {
        aircraftView.frontView(aircraft.getAltitude());
     }
    
    /**
     * Sets the view to above of the aircraft.
     */
    public void aboveView() {
       aircraftView.aboveView(aircraft.getAltitude());
     }
    
    /**
     * Sets the view to the right engine of the aircraft.
     */
    public void rightEngineView() {
        aircraftView.rightEngineView(aircraft.getAltitude());
     }
    
    /**
     * Sets the view to the left engien of the aircraft.
     */
    public void leftEngineView() {  
        aircraftView.leftEngineView(aircraft.getAltitude());
     }
    
    /**
     * Initial method for submitting user input aircraft parameters.
     */
    public void submitAircraftVariables(){       
        Spatial aircraftSpatial = aircraft.getSpatial();
        aircraftSpatial.setLocalTranslation(0, aircraft.getAltitude(), 0);
        
        // updates the area around the engine
        updateEngineArea();
        // updates the forward area around the engine (i.e. the future area of the engine)
        updateForwardArea();
        
        // updates the flycam's altitude
        flyCam.setLocation(flyCam.getLocation().add(new Vector3f(0,altitudeDisplacement,0)));
    }
    
    /**
     * Initial call to starting the simulation (campaign mode).
     * 
     * @param distance the horizontal distance of the drone from the aircraft
     * @param altitude the altitude of the aircraft (and drone)
     * @param speed the speed of the aircraft
     * @param aircraftView the aircraft's view
     */
    public void setSimulation(int distance, int altitude, int speed, boolean aircraftView){
        if (simulation == null){
            simulation = new Simulation(aircraft, drone, app,controlScreen, loader);
            stateManager.attach(simulation);
        }
        // sets the drone to the same altitude as the aircraft
        drone.setAltitude(aircraft.getAltitude());
        // sets up the simulation environment
        simulation.setup(distance, altitude, speed, aircraftView);
        updateEngineArea();
        
        // if the view is of the cockpit then load the cockpit 757 panel
        if(aircraftView){
            rootNode.attachChild(loader.getCockpit());
        } else {
            rootNode.detachChildNamed("Cockpit");
        }
        
        rootNode.attachChild(loader.getDrone());
    }
    
    /**
     * Run the campaign mode simulation
     */
    public void runSimulation(){
        simulation.run();
    }
    
    /**
     * Reset the campaign mode simulation
     */
    public void resetSimulation(){
        rootNode.detachChildNamed("AR_Drone-geom-0");
        simulation.reset();
        frontView();
    }
    
    /**
     * Hide the future forward area of the engine (from the toggle)
     */
    public void hideForwardArea(){
        rootNode.detachChildNamed("Forward Right Engine Area");
        rootNode.detachChildNamed("Forward Left Engine Area");
    }
    
    /**
     * Show the future forward area of the engine (from the toggle)
     */
    public void showForwardArea(){
        if (rightForwardArea == null || leftForwardArea == null){
            updateForwardArea();
        }
        rootNode.attachChild(rightForwardArea);
        rootNode.attachChild(leftForwardArea);
    }
    
    /**
     * Update (and therefore re-calculate) the area around the engine
     */
    public void updateEngineArea(){ 
        float engineRadius = engineArea.calculateArea();
        Spatial leftEngine = loader.getLeftEngineArea(engineRadius, engineArea.getReceivingLittle(), true, aircraft.getAltitude());
        Spatial rightEngine = loader.getRightEngineArea(engineRadius, engineArea.getReceivingLittle(), true, aircraft.getAltitude());
        
        rootNode.detachChildNamed("Right Engine");
        rootNode.detachChildNamed("Left Engine");

        rootNode.attachChild(rightEngine);
        rootNode.attachChild(leftEngine);
    }
    
    /**
     * Update the forward future area of the engine
     */
    public void updateForwardArea(){
        this.engineArea = new EngineArea(aircraft, weatherType, this);
        
        // re-calculate the area around the engine to get radius
        float engineRadius = engineArea.calculateArea();
         
        rightForwardArea = loader.getRightForwardArea(engineRadius, aircraft.getAltitude(), true);
        leftForwardArea = loader.getLeftForwardArea(engineRadius, aircraft.getAltitude(),true);
        
        rootNode.detachChildNamed("Forward Right Engine Area");
        rootNode.detachChildNamed("Forward Left Engine Area");
        if (showForwardArea){
            showForwardArea();
        }
    }
    
    
    /**
     * Set the aircraft altitude based on the difference between what the user inputs and the actual
     * aircraft altitude
     * 
     * @param fieldAltitude altitude entered in the textfield
     */
    public void setAltitude(int fieldAltitude){
        this.altitudeDisplacement = fieldAltitude - aircraft.getAltitude();
        aircraft.setAltitude(fieldAltitude);
    }
    
    /**
     * Set the speed of the aircraft
     * @param speed speed in knots
     */
    public void setSpeed(int speed){
        aircraft.setSpeed(speed);
    }
    
    /**
     * Set the aircraft's engine setting
     * 
     * @param setting engine setting, percentage
     */
    public void setEngineSetting(float setting){
        aircraft.setEngineSetting((int) setting);
    }
    
    /**
     * Get the engine area object
     * @return engine area
     */
    public EngineArea getEngineArea(){
        return engineArea;
    }
    
    /**
     * Enable showing the forward future area of the engine
     * 
     * @param showForwardArea Boolean toggle
     */
    public void setShowForwardArea(Boolean showForwardArea){
        this.showForwardArea = showForwardArea;
    }
    
    /**
     * Set the system for the take-off visualisation
     */
    public void setVisualisation(){
        Spatial ac = aircraft.getSpatial();
        
        // Set the aircraft to be in a take-off configuration
        ac.setLocalTranslation(0,0,0);
        aircraft.setSpeed(5);
        aircraft.setAltitude(0);
        
        updateEngineArea();
        
        // set the view to be of the left engine with a displacement of 0
        aircraftView.leftEngineView(0);
    }
    
    /**
     * Reset the take-off visualisation
     */
    public void resetVisualisation(){
        Spatial ac = aircraft.getSpatial();
        
        // Reset the aircraft's rotation
        Quaternion noRot = new Quaternion();
        noRot.fromAngleAxis( ((0)) , new Vector3f(1,0,0) );
        ac.setLocalRotation(noRot);
        
        // Set the aircraft to the beginning of take-off roll
        ac.setLocalTranslation(0,0,0);
        updateEngineArea();
        
        // set the view to be of the left engine with a displacement of 0
        aircraftView.leftEngineView(0);
    }
    
    /**
     * Execute the take-off visualisation (this is ran as the slider is changed)
     * 
     * @param speed speed of the aircraft
     * @param distance distance travelled down the runway
     */
     
    public void runVisualisation(float speed, float distance){
        aircraft.setSpeed(Math.round(speed));
        
        // update the area around the engine
        updateEngineArea();
        
        Spatial leftEngineArea = loader.getLeftEngineArea();
        Spatial rightEngineArea = loader.getRightEngineArea();
        
        // Maintains aircraft on the runway centerline
        float xDisplacement = - distance/40;
        
        this.aircraft.getSpatial().setLocalTranslation(xDisplacement,0,distance);
        leftEngineArea.setLocalTranslation(xDisplacement,0,distance);
        rightEngineArea.setLocalTranslation(xDisplacement,0,distance);
        aircraftView.leftEngineView(distance, xDisplacement);
        
        // Ensure aircraft is flat on the earth when not at the speed of rotation
        Quaternion noRot = new Quaternion();
        noRot.fromAngleAxis( ((0)) , new Vector3f(1,0,0) );
        
        aircraft.getSpatial().setLocalRotation(noRot);
        
        // update the aircraft's rotation
        leftEngineArea.setLocalRotation(leftEngineArea.getLocalRotation().mult(noRot));
        rightEngineArea.setLocalRotation(rightEngineArea.getLocalRotation().mult(noRot));
    }
    
    /**
     * When the aircraft has reached the point of rotation in the take-off visualisation then
     * rotate the aircraft
     * 
     * @param rate Rate at which the aircraft is being rotated
     */
    public void rotateVisualisation(float rate){
        // Quaternion storing a rotation based on the rate
        float percentage = (rate - 100);
        Quaternion rotation = new Quaternion();
        rotation.fromAngleAxis( ((-FastMath.PI/(82/percentage))) , new Vector3f(1,0,0) );
        
        // The rotation is applied to the actual aircraft
        aircraft.getSpatial().setLocalRotation( rotation );
        
        // rotates the area around the engines
        Quaternion areaRotation = new Quaternion();
        areaRotation.fromAngleAxis( ((-FastMath.PI/(82/percentage))) , new Vector3f(1,0,0) );
        
        Spatial leftEngineArea = loader.getLeftEngineArea();
        Spatial rightEngineArea = loader.getRightEngineArea();
        
        Quaternion currentRot = leftEngineArea.getLocalRotation();
        // concatenate the rotations
        Quaternion combo = currentRot.mult(areaRotation);
        
        // apply the rotation to the engine areas
        leftEngineArea.setLocalRotation(combo);
        rightEngineArea.setLocalRotation(combo);
    
    }
    
    /**
     * Get the radius of the area around the engine
     * @return engine radius in meters
     */
    public float getEngineRadius(){
        return converter.convertSystemUnitsToMeters(engineArea.calculateArea());
    }

    /**
     * Get the root node of the scene graph
     * @return this.rootNode
     */
    public Node getRootNode(){
        return this.rootNode;
    }
    
    /**
     * Open the settings window GUI
     */
    public void openSettings(){
        app.getGuiViewPort().removeProcessor(niftyDisplay);
        
        niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(app.getAssetManager(), app.getInputManager(), app.getAudioRenderer(), app.getGuiViewPort());
        nifty = niftyDisplay.getNifty();
        
        // read the XML and initialize the custom ScreenController
        optionScreen = new MyOptionsScreen(this);
        
        // get the GUI XML for the settings menu
        nifty.fromXml("Interface/options.xml", "options", optionScreen);
        // attach the Nifty display to the gui view port as a processor
        app.getGuiViewPort().addProcessor(niftyDisplay);
    } 
    
    /**
     * Submit the settings menu parameters
     * @param ident airport ICAO identifer
     * @param engineDiameter diameter of the engine fan, meters
     * @param engineFlow mass flow rate of the engine, kg/s
     * @param temperature temperature at the airport, degrees Celsius
     * @param pressure pressure at the airport, Millibars
     * @param weatherType type of weather: live, ISA or custom
     */
    public void submitSettings(String ident, float engineDiameter, float engineFlow, float temperature, float pressure, String weatherType){

        this.weather = new WeatherData(ident, temperature, converter.convertMillibarsToHg((int) pressure));
        this.weatherType = weatherType;
        aircraft.setEngineDiameter(engineDiameter);
        aircraft.setEngineMassFlow(engineFlow);

        app.getGuiViewPort().removeProcessor(niftyDisplay);
        
        niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(app.getAssetManager(), app.getInputManager(), app.getAudioRenderer(), app.getGuiViewPort());
        nifty = niftyDisplay.getNifty();
        
        controlScreen = new MyControlScreen(this, weatherType);
        
        nifty.fromXml("Interface/screen.xml", "start", controlScreen);
        // attach the Nifty display to the gui view port as a processor
        app.getGuiViewPort().addProcessor(niftyDisplay);
        
        updateWeatherScreen();
        
    }
 
    
    /**
     * Set the airport weather with the null values for pressure and temperature
     * @param identifier airport ICAO identifier
     */
    public void setWeather(String identifier){
        this.weather = new WeatherData(identifier, 0.0f, 0.0f);
    }
    
    /**
     * Get the weather data object
     * @return this.weather
     */
    public WeatherData getWeather(){
        return this.weather;
    }
    

}
