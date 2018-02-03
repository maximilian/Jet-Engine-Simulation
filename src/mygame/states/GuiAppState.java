/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.states;

import Camera.AircraftCamera;
import Weather.WeatherData;
import calculation.Aircraft;
import calculation.Drone;
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
import org.xml.sax.SAXException;

/**
 * Handles general GUI of the app.
 * 
 * @author max
 */
public class GuiAppState extends AbstractAppState {
    AppStateManager stateManager;
    
    private Project app;
    private EngineArea engineArea;
    private Aircraft aircraft;
    private Drone drone;
    
    private NiftyJmeDisplay niftyDisplay;
    private Nifty nifty;
    
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
                
        this.weather = new WeatherData("EGPF");
        this.engineArea = new EngineArea(aircraft, weather);

        this.converter = new Converter();
        
        
        // Camera view on load
        frontView();
        
        niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(app.getAssetManager(), app.getInputManager(), app.getAudioRenderer(), app.getGuiViewPort());
        nifty = niftyDisplay.getNifty();
        
        /** Read your XML and initialize your custom ScreenController */
        controlScreen = new MyControlScreen(this);

        nifty.fromXml("Interface/screen.xml", "start", controlScreen);
        // attach the Nifty display to the gui view port as a processor
        app.getGuiViewPort().addProcessor(niftyDisplay);
        
        
        submitAircraftVariables();  
        
        updateWeatherScreen();
        
    } 
    
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
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }

    public void frontView() {
        aircraftView.frontView(aircraft.getAltitude());
     }
    
    public void aboveView() {
       aircraftView.aboveView(aircraft.getAltitude());
     }
    
    public void rightEngineView() {
        aircraftView.rightEngineView(aircraft.getAltitude());
     }
    
    public void leftEngineView() {  
        aircraftView.leftEngineView(aircraft.getAltitude());
     }
    
    public void submitAircraftVariables(){       
        aircraft.setEngineSetting(100);
        
        Spatial aircraftSpatial = aircraft.getSpatial();
        aircraftSpatial.setLocalTranslation(0, aircraft.getAltitude(), 0);

        updateEngineArea();
        updateForwardArea();
        
        // updates the flycams altitude. Todo: disable submit if nothing was changed
        flyCam.setLocation(flyCam.getLocation().add(new Vector3f(0,altitudeDisplacement,0)));
    }
    
    public void setSimulation(int distance, int altitude, int speed, boolean aircraftView){
        if (simulation == null){
            simulation = new Simulation(aircraft, drone, app,controlScreen);
            stateManager.attach(simulation);
        }
        drone.setAltitude(aircraft.getAltitude());
        simulation.setup(distance, altitude, speed, aircraftView);
        updateEngineArea();
        
        if(aircraftView){
            rootNode.attachChild(loader.getCockpit());
        } else {
            rootNode.detachChildNamed("Cockpit");
        }
    }
    
    public void runSimulation(){
        simulation.run();
    }
    
    public void resetSimulation(){
        simulation.reset();
        frontView();
    }

    public void hideForwardArea(){
        rootNode.detachChildNamed("Forward Right Engine Area");
        rootNode.detachChildNamed("Forward Left Engine Area");
    }
    
    public void showForwardArea(){
        if (rightForwardArea == null || leftForwardArea == null){
            updateForwardArea();
        }
        rootNode.attachChild(rightForwardArea);
        rootNode.attachChild(leftForwardArea);
    }
    
    
    public void updateEngineArea(){

        
        float engineRadius = engineArea.calculateArea();
        Spatial leftEngine = loader.getLeftEngineArea(engineRadius, engineArea.getReceivingLittle(), true, aircraft.getAltitude());
        Spatial rightEngine = loader.getRightEngineArea(engineRadius, engineArea.getReceivingLittle(), true, aircraft.getAltitude());
        
        rootNode.detachChildNamed("Right Engine");
        rootNode.detachChildNamed("Left Engine");

        rootNode.attachChild(rightEngine);
        rootNode.attachChild(leftEngine);
    }
    
    public void updateForwardArea(){
         this.engineArea = new EngineArea(aircraft, weather);
         
         float engineRadius = engineArea.calculateArea();
         
        rightForwardArea = loader.getRightForwardArea(engineRadius, aircraft.getAltitude(), true);
        leftForwardArea = loader.getLeftForwardArea(engineRadius, aircraft.getAltitude(),true);
        
        rootNode.detachChildNamed("Forward Right Engine Area");
        rootNode.detachChildNamed("Forward Left Engine Area");
        if (showForwardArea){
            showForwardArea();
        }
    }
    
        
    public void setAltitude(int fieldAltitude){
        this.altitudeDisplacement = fieldAltitude - aircraft.getAltitude();
        aircraft.setAltitude(fieldAltitude);
    }
    
    public void setSpeed(int speed){
        aircraft.setSpeed(speed);
    }
    
    public EngineArea getEngineArea(){
        return engineArea;
    }

    public void setShowForwardArea(Boolean showForwardArea){
        this.showForwardArea = showForwardArea;
    }
    
    public void setVisualisation(){
        Spatial ac = aircraft.getSpatial();
        
        ac.setLocalTranslation(0,0,0);
        aircraft.setSpeed(5);
        aircraft.setAltitude(0);
        
        updateEngineArea();
            
        aircraftView.leftEngineView(0);
    }
    
    public void resetVisualisation(){
        Spatial ac = aircraft.getSpatial();
        
        Quaternion noRot = new Quaternion();
        noRot.fromAngleAxis( ((0)) , new Vector3f(1,0,0) );
        ac.setLocalRotation(noRot);
        
        ac.setLocalTranslation(0,0,0);
        updateEngineArea();
        
        aircraftView.leftEngineView(0);
        
    
    }
    
    public void runVisualisation(float speed, float distance){
        aircraft.setSpeed(Math.round(speed));
        
        updateEngineArea();
        
        System.out.println("move=="+distance);
        
        Spatial leftEngineArea = loader.getLeftEngineArea();
        Spatial rightEngineArea = loader.getRightEngineArea();
        
        // maintain aircraft on runway
        float xDisplacement = - distance/40;
        
        this.aircraft.getSpatial().setLocalTranslation(xDisplacement,0,distance);
        leftEngineArea.setLocalTranslation(xDisplacement,0,distance);
        rightEngineArea.setLocalTranslation(xDisplacement,0,distance);
        aircraftView.leftEngineView(distance, xDisplacement);
        
        // Ensure aircraft is flat on the earth when not at Vr
        Quaternion noRot = new Quaternion();
        noRot.fromAngleAxis( ((0)) , new Vector3f(1,0,0) );
        
        aircraft.getSpatial().setLocalRotation(noRot);
        leftEngineArea.setLocalRotation(leftEngineArea.getLocalRotation().mult(noRot));
        rightEngineArea.setLocalRotation(rightEngineArea.getLocalRotation().mult(noRot));
    }
    
    public void rotateVisualisation(float rate){
         /* This quaternion stores a 45 degree rotation */
         
        float percentage = (rate - 100);
        Quaternion rotation = new Quaternion();
        rotation.fromAngleAxis( ((-FastMath.PI/(82/percentage))) , new Vector3f(1,0,0) );
        /* The rotation is applied: The object rolls by 180 degrees. */
        aircraft.getSpatial().setLocalRotation( rotation );
        
        Quaternion areaRotation = new Quaternion();
        areaRotation.fromAngleAxis( ((-FastMath.PI/(82/percentage))) , new Vector3f(1,0,0) );
        
        Spatial leftEngineArea = loader.getLeftEngineArea();
        Spatial rightEngineArea = loader.getRightEngineArea();
        
        Quaternion currentRot = leftEngineArea.getLocalRotation();
        
        Quaternion combo = currentRot.mult(areaRotation);
                
        leftEngineArea.setLocalRotation(combo);
        rightEngineArea.setLocalRotation(combo);
    
    }
    
    public float getEngineRadius(){
        return converter.convertSystemUnitsToMeters(engineArea.calculateArea());
    }

    public Node getRootNode(){
        return this.rootNode;
    }
    
}
