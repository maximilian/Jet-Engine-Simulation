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
        Label fanDiameter = screen.findNiftyControl("fan_diameter", Label.class); 
        Label massFlow = screen.findNiftyControl("mass_flow", Label.class); 
        
        String selected = event.getSelectedId();
        
        if(selected.equals("PW2037")){
            fanDiameter.setText("2.154");
            massFlow.setText("548.85");
        } else if (selected.equals("PW2040")){
            fanDiameter.setText("2.146");
            massFlow.setText("573.34");
        } else if (selected.equals("RB211")){
            fanDiameter.setText("1.");
            massFlow.setText("522.08");
        }
        
    }
    
}
