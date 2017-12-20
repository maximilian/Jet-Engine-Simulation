/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.screen.Screen;

/**
 *
 * @author max
 */
public class MyControlScreen extends AbstractAppState implements ScreenController {
    Nifty nifty;
    Screen screen;
    
    private SimpleApplication app;
    private Camera flyCam;
   
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        this.app=(SimpleApplication) app;
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
    //throw new UnsupportedOperationException("Not supported yet.");
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
        flyCam.setLocation( new Vector3f( 0.08276296f, 15.758865f, 337.568f ) );
        axisLines();
     }
    
    public void axisLines(){
        
        Node testy = this.app.getRootNode();
        
        AssetManager assetManager = app.getAssetManager();
             Line xaxis = new Line(Vector3f.ZERO, new Vector3f(400f, 0, 0));
        Geometry xaxisline = new Geometry("BOOM!", xaxis);
        
        Line yaxis = new Line(Vector3f.ZERO, new Vector3f(0, 400f, 0));
        Geometry yaxisline = new Geometry("BOOM!", yaxis);
        
        Line zaxis = new Line(Vector3f.ZERO, new Vector3f(0, 0, 400f));
        Geometry zaxisline = new Geometry("BOOM!", zaxis);
                
        Material area_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        area_mat.setColor("Color", ColorRGBA.Green);
        
         Material yarea_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        yarea_mat.setColor("Color", ColorRGBA.Green);
        
        Material zarea_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        zarea_mat.setColor("Color", ColorRGBA.Green);
              
        xaxisline.setMaterial(area_mat);
        yaxisline.setMaterial(yarea_mat);
        zaxisline.setMaterial(zarea_mat);
        
        testy.attachChild(xaxisline);
        testy.attachChild(yaxisline);
        testy.attachChild(zaxisline);
        
        
    }
    
    public void aboveView() {
        Quaternion rotation = new Quaternion();
        // rotate 90 degrees around x axis
        rotation.fromAngleAxis( FastMath.PI/2 , new Vector3f(1,0,0) );
        flyCam.setRotation(rotation);
        flyCam.setLocation( new Vector3f( -0.42916974f, 356.08267f, 79.266045f ) );
       
     }
    
    public void rightEngineView() {
        Quaternion rotation = new Quaternion();
        // rotate 5/4*pi around y axis
        rotation.fromAngleAxis((float) (FastMath.PI * 0.75), new Vector3f(0,1,0) );
        
        flyCam.setRotation(rotation);
        
        flyCam.setLocation( new Vector3f(-233.71786f, 29.250921f, 249.49205f));
        System.out.println(flyCam.getLocation());
     }
    
    public void leftEngineView() {
        
        Quaternion rotation = new Quaternion();
        // rotate 5/4*pi around y axis
        rotation.fromAngleAxis((float) (FastMath.PI * 1.25), new Vector3f(0,1,0) );
        
        flyCam.setRotation(rotation);
        
        flyCam.setLocation( new Vector3f(233.71786f, 29.250921f, 249.49205f));
     }
    
    public void submit(){
    
        System.out.println("working");
    }
    
    
    public void moveAircraft(){
        Screen screen = nifty.getCurrentScreen();
        TextField altitudeField = screen.findNiftyControl("altitudeField", TextField.class);
        
        String altitude = altitudeField.getRealText();
        int alt = Integer.parseInt(altitude);
        System.out.println(altitude);
        
        Spatial aircraft = this.app.getRootNode().getChild("3d-model-objnode");
        
        aircraft.setLocalTranslation(0, alt, 0);

        flyCam.setLocation( new Vector3f( 0.08276296f, 15.758865f + alt, 337.568f ) );
    
    }
    
    
    public void quitGame() {
        System.out.println("quit pls");
        app.stop();
    }
}
