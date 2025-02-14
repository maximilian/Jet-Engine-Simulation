package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import java.util.Random;
import mygame.gui.MyControlScreen;

/**
 * Simulates the collision between an aircraft and a drone
 * 
 * @author Maximilian Morell
 */
public class Simulation extends AbstractAppState{
    
 
    private final MyControlScreen controlScreen;
    private final ResourceLoader loader;
    private final Aircraft aircraft;
    private final Drone drone;
    
    private final Application app;
    private final Camera flyCam;

    private boolean runSimulation;
    private boolean timeNotStarted;
    
    private boolean aircraftView;
    
    private long startTime = 0;
    private long endTime = 0;
    
    private int distanceTravelled;
    
    private final float diameter;
    private boolean positiveDirection;
    private Random rand;
    
    /**
     * Constructor for creating the simulation
     * 
     * @param aircraft the aircraft object
     * @param drone the drone object
     * @param app the main app with core system methods
     * @param controlScreen the GUI control screen
     * @param loader the resource loader
     */
    public Simulation(Aircraft aircraft, Drone drone, Application app, MyControlScreen controlScreen, ResourceLoader loader){
        this.loader = loader;
        this.aircraft = aircraft;
        this.drone = drone;
        
        this.app = (Project) app;
        this.flyCam = app.getCamera();
        
        this.controlScreen = controlScreen;
        
        // simulation is not running by default
        runSimulation = false;
        timeNotStarted = true;
        distanceTravelled = 0;
        
        /* drone hover variables */
        rand = new Random();
        diameter = (float) 30.0;
        positiveDirection = true;
    }

    private float displacement = 0;
    private float distanceMove = 0;

    /**
     * The simulation update loop. This method is run every game "tick".
     * 
     * @param tpf Unique for the machine it's being run on.
     */
    @Override
    public void update(float tpf) {
        
        Spatial aircraftSpatial = aircraft.getSpatial();
        Spatial leftEngineArea = loader.getLeftEngineArea();
        Spatial rightEngineArea = loader.getRightEngineArea();
        
        Spatial rightForwardArea = loader.getRightForwardArea();
        Spatial leftForwardArea = loader.getLeftForwardArea();
        
        
        /* hover drone around area */
        
        if(positiveDirection) {
            if (displacement > diameter/2){
                positiveDirection = false;
            } else {
                distanceMove = rand.nextFloat() * (diameter);
                displacement += distanceMove;
                drone.getSpatial().move(distanceMove*tpf,distanceMove*tpf,0);   
            }

        } else {
            if (displacement < diameter/2){
                positiveDirection = true;
            } else {
                distanceMove = 0 + rand.nextFloat() * (diameter - 0);
                displacement -= distanceMove;
                drone.getSpatial().move(-distanceMove*tpf,-distanceMove*tpf,0);            
            }
        }


        if (runSimulation){
            Vector3f a = new Vector3f(0,aircraft.getAltitude(),0);
            Vector3f b = new Vector3f(0,aircraft.getAltitude(),drone.getConvertedDistanceFromAircraft());

            float distanceVectors = a.distance(b);
            
            if (timeNotStarted) {
                startTime = System.currentTimeMillis();
                timeNotStarted = false;
            }
            
            if ( distanceVectors >= distanceTravelled) {
                aircraftSpatial.move(0,0,aircraft.getConvertedSpeed()*tpf);
                if(aircraftView){
                    flyCam.setLocation(aircraftSpatial.getLocalTranslation().add(0.13625361f, 37.367325f, 159.01654f));

                }
                leftEngineArea.move(0,0, aircraft.getConvertedSpeed()*tpf);
                rightEngineArea.move(0,0,aircraft.getConvertedSpeed()*tpf);
                
                rightForwardArea.move(0,0,aircraft.getConvertedSpeed()*tpf);
                leftForwardArea.move(0,0,aircraft.getConvertedSpeed()*tpf);
                
                distanceTravelled += (aircraft.getConvertedSpeed()*tpf);
                endTime = System.currentTimeMillis();
            } else {
                runSimulation = false;

                aircraftSpatial.setLocalTranslation( new Vector3f(0,aircraft.getAltitude(),drone.getConvertedDistanceFromAircraft()));
                
                rightForwardArea.setLocalTranslation(new Vector3f(-49f,15f+aircraft.getAltitude(),2550+aircraftSpatial.getLocalTranslation().getZ()));
                leftForwardArea.setLocalTranslation(new Vector3f(49f,15f+aircraft.getAltitude(),2550+aircraftSpatial.getLocalTranslation().getZ()));
                
                if(aircraftView){
                    flyCam.setLocation(aircraftSpatial.getLocalTranslation().add(0.13625361f, 37.367325f, 159.01654f));

                }
                leftEngineArea.setLocalTranslation(new Vector3f(0,0,drone.getConvertedDistanceFromAircraft()));
                rightEngineArea.setLocalTranslation(new Vector3f(0,0,drone.getConvertedDistanceFromAircraft()));
                
                distanceTravelled = 0;
                timeNotStarted = true;
                
                float totalTimeTaken = (float) (endTime - startTime) / 1000;
                controlScreen.showCollisionWindow(totalTimeTaken, aircraft.getSpeed(), drone.getDistanceFromAircraft(), aircraftView);
                startTime = endTime = 0;
            }
            System.out.println("time taken: " + (endTime - startTime));
        }
    }
    
    /**
     * Sets up the simulation
     * @param distance horizontal distance of the drone from the aircraft
     * @param altitude altitude of the aircraft (and drone)
     * @param speed speed of the aircraft
     * @param aircraftView boolean - whether the view is of the aircraft or drone
     */
    public void setup(int distance, int altitude, int speed, boolean aircraftView){
        
        drone.setDistanceFromAircraft(distance);
        
        aircraft.setAltitude(altitude);
        aircraft.setSpeed(speed);
        
        this.aircraftView = aircraftView;
        
        Spatial droneSpatial = drone.getSpatial();
        droneSpatial.setLocalTranslation(49f, altitude, drone.getConvertedDistanceFromAircraft());
        
        Spatial aircraftSpatial = aircraft.getSpatial();
        aircraftSpatial.setLocalTranslation(0,altitude,0);
        
        if (aircraftView){
            setCameraCockpitPosition(altitude);
        } else {
           setCameraDronePosition(altitude); 
        }

    }
    
    /**
     * Run the simulation
     */
    public void run(){
        runSimulation = true;   
    }
    
    /**
     * Reset the simulation
     */
    public void reset(){
        this.aircraft.getSpatial().setLocalTranslation(0, aircraft.getAltitude(), 0);
        Spatial rightForwardArea = loader.getRightForwardArea();
        Spatial leftForwardArea = loader.getLeftForwardArea();
        
        Spatial leftEngineArea = loader.getLeftEngineArea();
        Spatial rightEngineArea = loader.getRightEngineArea();
        
        rightForwardArea.setLocalTranslation(new Vector3f(-49f,15f+aircraft.getAltitude(),2550));
        leftForwardArea.setLocalTranslation(new Vector3f(49f,15f+aircraft.getAltitude(),2550));
       
        leftEngineArea.setLocalTranslation(new Vector3f(0,0,0));
        rightEngineArea.setLocalTranslation(new Vector3f(0,0,0));
    }
    
    /**
     * Set the camera to be in the view of the aircraft cockpit
     * 
     * @param altitude altitude of the aircraft
     */
    public void setCameraCockpitPosition(int altitude){
            Quaternion cameraRotation = new Quaternion();
            cameraRotation.fromAngleAxis((float) (FastMath.PI * 2), new Vector3f(0,1,0) );
            flyCam.setLocation(new Vector3f(0.13625361f, 37.367325f+altitude, 159.01654f));
            flyCam.setRotation(cameraRotation);
    }
    
    /**
     * Set the camera to be in the view of the drone operator
     * 
     * @param altitude altitude of the drone
     */
    public void setCameraDronePosition(int altitude){
        Quaternion droneGroundView = new Quaternion();
        droneGroundView.fromAngleAxis((float) (FastMath.PI * 1.33333), new Vector3f(0,1,0) );
        
        float angleTowardsDrone = (float) Math.atan((float) altitude/500);

        Quaternion droneAngleView = new Quaternion();
        droneAngleView.fromAngleAxis((float) (-angleTowardsDrone), new Vector3f(1,0,0) );
        
        Quaternion combQuaternion = droneGroundView.mult(droneAngleView);
        flyCam.setRotation(combQuaternion);
        
        flyCam.setLocation( new Vector3f(500f, 20, drone.getConvertedDistanceFromAircraft()));
    }
}
