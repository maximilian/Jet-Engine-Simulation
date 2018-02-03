/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.states;

import Weather.WeatherData;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import mygame.gui.MyOptionsScreen;

/**
 *
 * @author max
 */
public class OptionsAppState extends AbstractAppState {
    private NiftyJmeDisplay niftyDisplay;
    private Nifty nifty;
    private MyOptionsScreen optionsScreen;
    
    private WeatherData weather;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(app.getAssetManager(), app.getInputManager(), app.getAudioRenderer(), app.getGuiViewPort());
        nifty = niftyDisplay.getNifty();
        
        /** Read your XML and initialize your custom ScreenController */
        optionsScreen = new MyOptionsScreen(this);

        nifty.fromXml("Interface/options.xml", "options", optionsScreen);
        // attach the Nifty display to the gui view port as a processor
        app.getGuiViewPort().addProcessor(niftyDisplay);
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
    
    public void setWeather(String identifier){
        this.weather = new WeatherData(identifier);
    }
    
    public WeatherData getWeather(){
        return this.weather;
    }
    
    
    
}
