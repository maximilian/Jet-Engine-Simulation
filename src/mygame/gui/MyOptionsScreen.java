/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.gui;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.RadioButtonGroupStateChangedEvent;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import mygame.states.GuiAppState;
import mygame.states.OptionsAppState;

/**
 *
 * @author max
 */
public class MyOptionsScreen extends AbstractAppState implements ScreenController {
    private OptionsAppState gui;
    private Nifty nifty;
    private Screen screen;
        
    public MyOptionsScreen(OptionsAppState gui){
        this.gui = gui;
    }
    

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

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
    
    @NiftyEventSubscriber(id="RadioGroup-1")
    public void onRadioGroup1Changed(final String id, final RadioButtonGroupStateChangedEvent event) {
        Label fanDiam = screen.findNiftyControl("fan_diameter", Label.class); 
        Label speedLabel = screen.findNiftyControl("mass_flow", Label.class); 

        if(event.getSelectedId().equals("PW2037")){
            fanDiam.setText("ayy");
        } 
        
        System.out.println("RadioButton [" + event.getSelectedId() + "] is now selected. The old selection was [" + event.getPreviousSelectedId() + "]");
    }
    
}
