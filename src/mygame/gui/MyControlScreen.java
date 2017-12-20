/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.gui;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.screen.Screen;
import mygame.Project;
import mygame.ResourceLoader;
import mygame.states.GuiAppState;

/**
 * Manages the main GUI
 * 
 * @author max
 */
public class MyControlScreen extends AbstractAppState implements ScreenController {
    Nifty nifty;
    Screen screen;
    private GuiAppState gui;
    private Project app;      
    private float altitude;
    
    public MyControlScreen(GuiAppState gui){
        this.gui = gui;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        this.app=(Project) app;
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
        gui.frontView();
     }

    
    public void aboveView() {
        gui.aboveView();
     }
    
    public void rightEngineView() {
        gui.rightEngineView();
     }
    
    public void leftEngineView() {  
        gui.leftEngineView();
     }
    
    public void submit(){
        screen = nifty.getCurrentScreen();

        TextField altitudeField = screen.findNiftyControl("altitudeField", TextField.class);        
        String altitudeString = altitudeField.getRealText();
        altitude = Integer.parseInt(altitudeString);
        
         Spatial aircraft = this.app.getRootNode().getChild("3d-model-objnode");
        
        aircraft.setLocalTranslation(0, altitude, 0);
        
        gui.setVariables((int) altitude);
    }
    

    
    
    public void quitGame() {
        System.out.println("quit pls");
        app.stop();
    }
}
