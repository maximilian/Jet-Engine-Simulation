/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.screen.Screen;

/**
 * Manages the main GUI
 * 
 * @author max
 */
public class MyControlScreen extends AbstractAppState implements ScreenController {
    Nifty nifty;
    Screen screen;
    
    private Project app;
    private Camera flyCam;

            
    private float altitude;
   
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        this.app=(Project) app;
        this.flyCam = this.app.getCamera();
        
        //TODO: initialize your AppState, e.g. attach spatials to rootNode
        //this is called on the OpenGL thread after the AppState has been attached
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
    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }

    @Override
    public void onStartScreen() {
        
    }

    @Override
    public void onEndScreen() {
        
    }
    
    public void frontView() {
         Quaternion rotation = new Quaternion();
        // rotate 170 degrees around y axis
        rotation.fromAngleAxis( FastMath.PI , new Vector3f(0,1,0) );
        flyCam.setRotation(rotation);
        flyCam.setLocation( new Vector3f( 0.08276296f, 15.758865f+altitude, 337.568f ) );
        
        moveAircraft();
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
    
    public void submit(){
    
        moveAircraft();
    }
    
    
    public void moveAircraft(){
        screen = nifty.getCurrentScreen();
        
        TextField altitudeField = screen.findNiftyControl("altitudeField", TextField.class);
        
        String altitudeString = altitudeField.getRealText();
        altitude = Integer.parseInt(altitudeString);
        
        
        Spatial aircraft = this.app.getRootNode().getChild("3d-model-objnode");
        
        aircraft.setLocalTranslation(0, altitude, 0);
    
    }
    
    
    public void quitGame() {
        System.out.println("quit pls");
        app.stop();
    }
}
