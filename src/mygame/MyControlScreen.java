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
import com.jme3.input.FlyByCamera;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import de.lessvoid.nifty.Nifty;
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
    //throw new UnsupportedOperationException("Not supporteda yet.");
        this.nifty = nifty;
        this.screen = screen;
    }

    @Override
    public void onStartScreen() {
        
    }

    @Override
    public void onEndScreen() {
        
    }
    
    public void startGame() {
        System.out.println("front view pls");
        flyCam.setLocation( new Vector3f( 0.08276296f, 15.758865f, 337.568f ) );
       
     }
    public void quitGame() {
        System.out.println("quit pls");
        app.stop();
    }
}
