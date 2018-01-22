/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import calculation.Aircraft;
import calculation.Drone;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import mygame.gui.MyControlScreen;

/**
 * Simulates collision between aircraft and drone
 * 
 * @author max
 */
public class Simulation extends AbstractAppState{
    
    private MyControlScreen controlScreen;
    
    private Aircraft aircraft;
    private Drone drone;
    
    private Application app;
    private Camera flyCam;

    private boolean runSimulation;
    private boolean timeNotStarted;
    
    private long startTime = 0;
    private long endTime = 0;
    
    private int distanceTravelled;
    
    public Simulation(Aircraft aircraft, Drone drone, Application app, MyControlScreen controlScreen){
        
        this.aircraft = aircraft;
        this.drone = drone;
        
        this.app = (Project) app;
        this.flyCam = app.getCamera();
        
        this.controlScreen = controlScreen;
        
        // simulation is not running by default
        runSimulation = false;
        timeNotStarted = true;
        distanceTravelled = 0;
    }
    
    @Override
    public void update(float tpf) {
        Spatial aircraftSpatial = aircraft.getSpatial();
        //Spatial leftEngineArea = loader.getLeftEngineArea();
        //Spatial rightEngineArea = loader.getRightEngineArea();

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
                //leftEngineArea.move(0,0, aircraft.getConvertedSpeed()*tpf);
                //rightEngineArea.move(0,0,aircraft.getConvertedSpeed()*tpf);
                
                distanceTravelled += (aircraft.getConvertedSpeed()*tpf);
                System.out.println(aircraft.getSpeed());
                endTime = System.currentTimeMillis();
            } else {
                runSimulation = false;
                aircraftSpatial.setLocalTranslation( new Vector3f(0,aircraft.getAltitude(),drone.getConvertedDistanceFromAircraft()));
                //leftEngineArea.setLocalTranslation(new Vector3f(0,0,drone.getConvertedDistanceFromAircraft()));
                //rightEngineArea.setLocalTranslation(new Vector3f(0,0,drone.getConvertedDistanceFromAircraft()));
                
                distanceTravelled = 0;
                timeNotStarted = true;
                
                float totalTimeTaken = (float) (endTime - startTime) / 1000;
                System.out.println(totalTimeTaken + " seconds");
                controlScreen.showCollisionWindow(totalTimeTaken, aircraft.getSpeed(), drone.getDistanceFromAircraft());
                startTime = endTime = 0;
            }
            System.out.println("time taken: " + (endTime - startTime));
        }
    }
    
    public void setup(int distance){
        drone.setDistanceFromAircraft(distance);
        
        Spatial droneSpatial = drone.getSpatial();
        droneSpatial.setLocalTranslation(49f, aircraft.getAltitude(), drone.getConvertedDistanceFromAircraft());
        
        setCameraPosition();
    }
    
    public void run(){
        runSimulation = true;   
    }
    
    public void reset(){
        this.aircraft.getSpatial().setLocalTranslation(0, aircraft.getAltitude(), 0);
        //this.loader.getLeftEngineArea().setLocalTranslation(0, aircraft.getAltitude(),0);
        //this.loader.getRightEngineArea().setLocalTranslation(0, aircraft.getAltitude(),0);
    }
    
    public void setCameraPosition(){
        Quaternion droneGroundView = new Quaternion();
        droneGroundView.fromAngleAxis((float) (FastMath.PI * 1.5), new Vector3f(0,1,0) );
        
        float angleTowardsDrone = (float) Math.atan(aircraft.getAltitude()/500);
        Quaternion droneAngleView = new Quaternion();

        droneAngleView.fromAngleAxis((float) (-angleTowardsDrone), new Vector3f(1,0,0) );
        
        Quaternion combo = droneGroundView.mult(droneAngleView);
        flyCam.setRotation(combo);
        
        flyCam.setLocation( new Vector3f(500f, 20, drone.getConvertedDistanceFromAircraft()));
    
    }
}
