package mygame;

import Camera.AircraftCamera;
import calculation.Aircraft;
import calculation.Drone;
import mygame.states.GuiAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;

/**
 * Manages 3D scene graph - core component of the system
 * 
 * @author Maximilian Morell
 */
public class Project extends SimpleApplication {
    private ResourceLoader loader;
    private GuiAppState gui;
    
    private Aircraft aircraftObject;
    private Drone droneObject;
    
    private AircraftCamera airCam;
     
    boolean receivingLittle = false;
    boolean receivingMuch = false;
    boolean receivingCorrect = false;

    @Override
    public void simpleInitApp() {
        
        setDisplayFps(false);
        setDisplayStatView(false);
        
        airCam = new AircraftCamera(getCamera());
        
        flyCam.setMoveSpeed(250);
        flyCam.setDragToRotate(true);
        cam.setFrustumFar(5000);
        
        loader = new ResourceLoader(assetManager, cam, settings);
        rootNode.attachChild(loader.getTerrain());
        
        gui = new GuiAppState();
        stateManager.attach(gui);
        
        //stateManager.attach(new OptionsAppState());

        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));

        // need this in any game involving physics
        BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
           
        Node aircraftNode = new Node("aircraft");
        aircraftNode.attachChild(loader.getAircraft());
        
        rootNode.attachChild(aircraftNode);
   
        aircraftObject = new Aircraft(rootNode.getChild("3d-model-objnode"));
        
        aircraftObject.setSpeed(160);
        aircraftObject.setAltitude(0);
        
        droneObject = new Drone(loader.getDrone());


        aircraftNode.attachChild(loader.getLeftEngineArea(0, receivingLittle, false, 0));       
        aircraftNode.attachChild(loader.getRightEngineArea(0, receivingLittle, false, 0));

        // add a light to make the model visible 
        rootNode.addLight(loader.getSun());   
    }
     
    /**
     * Gets the resource loader
     * 
     * @return loader 
     */
    public ResourceLoader getResourceLoader(){
        return loader;
    }
    
    /**
     * Gets the aircraft object
     * 
     * @return aircraftObject
     */
    public Aircraft getAircraft(){
        return aircraftObject;
    }
    
    /**
     * Gets the drone object
     * 
     * @return droneObject
     */
    public Drone getDrone(){
        return droneObject;
    }
    
    /**
     * Gets the aircraft camera object
     * 
     * @return airCam 
     */
    public AircraftCamera getAircraftCamera(){
        return airCam;
    }
}