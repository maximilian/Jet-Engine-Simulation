/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.states;

import calculation.Aircraft;
import calculation.EngineArea;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import de.lessvoid.nifty.Nifty;
import mygame.Project;
import mygame.ResourceLoader;
import mygame.gui.MyControlScreen;

/**
 * Handles general GUI of the app.
 * 
 * @author max
 */
public class GuiAppState extends AbstractAppState {
    
    private Project app;
    private EngineArea engineArea;
    private Aircraft aircraft;
    
    private NiftyJmeDisplay niftyDisplay;
    private Nifty nifty;
    
    private MyControlScreen controlScreen;
    private Camera flyCam;
    private ResourceLoader loader;
    private Node rootNode;
    
    private int altitude;
    private int altitudeDisplacement;
    
    Spatial rightForwardArea;
    
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        this.app = (Project) app;
        this.flyCam = this.app.getCamera();
        this.loader = this.app.getResourceLoader();
        this.rootNode = this.app.getRootNode();
        this.aircraft = this.app.getAircraft();
        
        // Camera view on load
        frontView();
        
        
        
        niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(app.getAssetManager(), app.getInputManager(), app.getAudioRenderer(), app.getGuiViewPort());
        nifty = niftyDisplay.getNifty();
        
        /** Read your XML and initialize your custom ScreenController */
        controlScreen = new MyControlScreen(this);
        
        
        nifty.fromXml("Interface/screen.xml", "start", controlScreen);
        // attach the Nifty display to the gui view port as a processor
        app.getGuiViewPort().addProcessor(niftyDisplay);
        
        
    }
    
    @Override
    public void update(float tpf) {
        //TODO: implement behavior during runtime
        
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }

    public void frontView() {
        
         Quaternion rotation = new Quaternion();
        // rotate 170 degrees around y axis
        rotation.fromAngleAxis( FastMath.PI , new Vector3f(0,1,0) );
        flyCam.setRotation(rotation);
        flyCam.setLocation( new Vector3f( 0.08276296f, 15.758865f+altitude, 337.568f ) );

     }

    
    public void aboveView() {
        Quaternion rotation = new Quaternion();
        // rotate 90 degrees around x axis
        rotation.fromAngleAxis( FastMath.PI/2 , new Vector3f(1,0,0) );
        flyCam.setRotation(rotation);
        flyCam.setLocation( new Vector3f( -0.42916974f, 356.08267f+altitude, 79.266045f ) ); 
     }
    
    public void rightEngineView() {
        Quaternion rotation = new Quaternion();
        // rotate 5/4*pi around y axis
        rotation.fromAngleAxis((float) (FastMath.PI * 0.75), new Vector3f(0,1,0) );
        flyCam.setRotation(rotation);
        flyCam.setLocation( new Vector3f(-233.71786f, 29.250921f+altitude, 249.49205f));
     }
    
    public void leftEngineView() {  
        Quaternion rotation = new Quaternion();
        // rotate 5/4*pi around y axis
        rotation.fromAngleAxis((float) (FastMath.PI * 1.25), new Vector3f(0,1,0) );
        flyCam.setRotation(rotation);
        flyCam.setLocation( new Vector3f(233.71786f, 29.250921f+altitude, 249.49205f));
     }
    
    public void submitAircraftVariables(int speed){
        
        aircraft.setAltitude(altitude);
        aircraft.setSpeed(speed);
        aircraft.setEngineSetting(100);
        
        Spatial aircraftSpatial = aircraft.getSpatial();
        aircraftSpatial.setLocalTranslation(0, altitude, 0);
        
        updateEngineArea();
        updateForwardArea();
        // updates the flycams altitude. Todo: disable submit if nothing was changed
        flyCam.setLocation(flyCam.getLocation().add(new Vector3f(0,altitudeDisplacement,0)));
    }

    public void hideForwardArea(){
        rootNode.detachChildNamed("Forward Engine Area");
    }
    
    public void showForwardArea(){
        rootNode.attachChild(rightForwardArea);
    }
    public void updateEngineArea(){
        this.engineArea = new EngineArea(aircraft);
        
        float engineRadius = engineArea.calculateArea();
        Spatial leftEngine = loader.getLeftEngineArea(engineRadius, engineArea.getReceivingLittle(), true, altitude);
        Spatial rightEngine = loader.getRightEngineArea(engineRadius, engineArea.getReceivingLittle(), true, altitude);
        
        rootNode.detachChildNamed("Right Engine");
        rootNode.detachChildNamed("Left Engine");
        
        rootNode.attachChild(rightEngine);
        rootNode.attachChild(leftEngine);
    }
    
    public void updateForwardArea(){
         this.engineArea = new EngineArea(aircraft);
         
         float engineRadius = engineArea.calculateArea();
         
         rightForwardArea = loader.getRightForwardArea(engineRadius, altitude, true);
         
        rootNode.detachChildNamed("Forward Engine Area");
        rootNode.attachChild(rightForwardArea);
    }
    
        
    public void setAltitude(int fieldAltitude){
        this.altitudeDisplacement = fieldAltitude - altitude;
        this.altitude = fieldAltitude;
    }
    
    public EngineArea getEngineArea(){
        return engineArea;
    }
    
    public Aircraft getAircraft(){
        return aircraft;
    }
    
}
