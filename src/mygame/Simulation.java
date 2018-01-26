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
    
    private final MyControlScreen controlScreen;
    
    private final Aircraft aircraft;
    private final Drone drone;
    
    private final Application app;
    private final Camera flyCam;

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
                flyCam.setLocation((0,aircraft.getAltitude(),(aircraft.getConvertedSpeed()*tpf));
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
                controlScreen.showCollisionWindow(totalTimeTaken, aircraft.getSpeed(), drone.getDistanceFromAircraft());
                startTime = endTime = 0;
            }
            System.out.println("time taken: " + (endTime - startTime));
        }
    }
    
    public void setup(int distance, int altitude, int speed){
        drone.setDistanceFromAircraft(distance);
        
        aircraft.setAltitude(altitude);
        aircraft.setSpeed(speed);
        
        Spatial droneSpatial = drone.getSpatial();
        droneSpatial.setLocalTranslation(49f, altitude, drone.getConvertedDistanceFromAircraft());
        
        Spatial aircraftSpatial = aircraft.getSpatial();
        aircraftSpatial.setLocalTranslation(0,altitude,0);
        
        Quaternion cameraRotation = new Quaternion();
        cameraRotation.fromAngleAxis((float) (FastMath.PI * 2), new Vector3f(0,1,0) );
        flyCam.setLocation(new Vector3f(0.13625361f, 37.367325f+altitude, 159.01654f));
        flyCam.setRotation(cameraRotation);
        
        //setCameraPosition(altitude);
    }
    
    public void run(){
        runSimulation = true;   
    }
    
    public void reset(){
        this.aircraft.getSpatial().setLocalTranslation(0, aircraft.getAltitude(), 0);
        //this.loader.getLeftEngineArea().setLocalTranslation(0, aircraft.getAltitude(),0);
        //this.loader.getRightEngineArea().setLocalTranslation(0, aircraft.getAltitude(),0);
    }
    
    public void setCameraPosition(int altitude){
        Quaternion droneGroundView = new Quaternion();
        droneGroundView.fromAngleAxis((float) (FastMath.PI * 1.33333), new Vector3f(0,1,0) );
        
        float angleTowardsDrone = (float) Math.atan(altitude/500);
        Quaternion droneAngleView = new Quaternion();

        droneAngleView.fromAngleAxis((float) (-angleTowardsDrone), new Vector3f(1,0,0) );
        
        Quaternion combo = droneGroundView.mult(droneAngleView);
        flyCam.setRotation(combo);
        
        flyCam.setLocation( new Vector3f(500f, 20, drone.getConvertedDistanceFromAircraft()));
    
    }
}
