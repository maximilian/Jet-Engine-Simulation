/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.gui;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.TextField;
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
    private float altitude;
    
    public MyControlScreen(GuiAppState gui){
        this.gui = gui;
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
        
        TextField altitudeField = screen.findNiftyControl("altitudeField", TextField.class);  
        String altitudeString = altitudeField.getRealText();
        int fieldAltitude = Integer.parseInt(altitudeString);
        
        gui.setAltitude(fieldAltitude);
        
        gui.submitVariables();
        
        
    }
    
   

    
    
    public void quitGame() {
        System.out.println("quit pls");
        app.stop();
    }


}
