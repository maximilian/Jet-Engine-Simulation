/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.gui;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.controls.TextFieldChangedEvent;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.screen.Screen;
import mygame.Project;
import mygame.states.GuiAppState;

/**
 * Communicates with the Gui app state.
 * 
 * @author max
 */
public class MyControlScreen implements ScreenController {
    Nifty nifty;
    Screen screen;
    private GuiAppState gui;
    private Project app;    
    
    private Element submitButton;
    
    public MyControlScreen(GuiAppState gui){
        this.gui = gui;
        
        
    }
    
    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        
        submitButton = screen.findElementById("submitButton");
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
        submitButton.disable();
        
        TextField altitudeField = screen.findNiftyControl("altitudeField", TextField.class);  
        String altitudeString = altitudeField.getRealText();
        int fieldAltitude = Integer.parseInt(altitudeString);
        
        TextField speedField = screen.findNiftyControl("speedField", TextField.class);  
        String speedString = speedField.getRealText();
        int fieldSpeed = Integer.parseInt(speedString);
        
        gui.setAltitude(fieldAltitude);
        gui.submitAircraftVariables(fieldSpeed);      
    }
    
    @NiftyEventSubscriber(pattern=".*Field")
    public void onTextfieldChange(final String id, final TextFieldChangedEvent event) {
        submitButton.enable();
        
    }
  
    public void quitGame() {
        System.out.println("quit pls");
        app.stop();
    }


}
