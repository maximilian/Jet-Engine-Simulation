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
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.RadioButton;
import de.lessvoid.nifty.controls.RadioButtonGroup;
import de.lessvoid.nifty.controls.RadioButtonGroupStateChangedEvent;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.controls.TextFieldChangedEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import mygame.Converter;
import mygame.states.GuiAppState;
import org.xml.sax.SAXException;

/**
 *
 * @author max
 */
public class MyOptionsScreen extends AbstractAppState implements ScreenController {
    private GuiAppState gui;
    private Nifty nifty;
    private Screen screen;
    private Converter converter;
    
    private TextField customFanDiameterField;
    private TextField customMassFlowField;
    
    public MyOptionsScreen(GuiAppState gui){
        this.gui = gui;
        this.converter = new Converter();
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
        
        customFanDiameterField = screen.findNiftyControl("engine-customDiameter", TextField.class); 
        customMassFlowField = screen.findNiftyControl("engine-customMass", TextField.class); 
        
        customFanDiameterField.disable();
        customMassFlowField.disable();

    }

    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {
        
    }
    
    public void getAirportData(){

        Label temp_wx = screen.findNiftyControl("temp_wx", Label.class); 
        Label pressure_wx = screen.findNiftyControl("pressure_wx", Label.class);
        Label time_wx = screen.findNiftyControl("time_wx", Label.class);
        
        TextField airportIdentifierField = screen.findNiftyControl("airportIdentifier", TextField.class);
        String airportIdentifier = airportIdentifierField.getRealText();
        
        gui.setWeather(airportIdentifier);
        
        
        try {
            temp_wx.setText(Float.toString(gui.getWeather().getTemperature()));
            
            int convertedPressure = converter.convertHgToMillibars(gui.getWeather().getPressure());
            pressure_wx.setText(Float.toString(convertedPressure));
            
            LocalDateTime datetime = gui.getWeather().getDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyy HH:mm");
            String formattedDate = datetime.format(formatter);
       
            time_wx.setText(formattedDate);

        } catch (IOException ex) {
            Logger.getLogger(MyOptionsScreen.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(MyOptionsScreen.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(MyOptionsScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @NiftyEventSubscriber(id="RadioGroup-1")
    public void onRadioGroup1Changed(final String id, final RadioButtonGroupStateChangedEvent event) {
        Label fanDiameter = screen.findNiftyControl("fan_diameter", Label.class); 
        Label massFlow = screen.findNiftyControl("mass_flow", Label.class); 
        
        String selected = event.getSelectedId();
        customFanDiameterField.disable();
        customMassFlowField.disable();
            
        if(selected.equals("PW2037")){
            fanDiameter.setText("2.154");
            massFlow.setText("548.85");
        } else if (selected.equals("PW2040")){
            fanDiameter.setText("2.146");
            massFlow.setText("573.34");
        } else if (selected.equals("RB211")){
            fanDiameter.setText("1.882");
            massFlow.setText("522.08");
        } else if (selected.equals("custom")){
            customFanDiameterField.enable();
            customMassFlowField.enable();
        }
        
    }
    
    @NiftyEventSubscriber(pattern="engine-.*")
    public void onTextFieldChange(final String id, final TextFieldChangedEvent event){
        Label fanDiameter = screen.findNiftyControl("fan_diameter", Label.class); 
        Label massFlow = screen.findNiftyControl("mass_flow", Label.class); 

        if(event.getTextFieldControl().getId().equals("engine-customDiameter")){
            fanDiameter.setText(event.getText());
        }

        if(event.getTextFieldControl().getId().equals("engine-customMass")){
            massFlow.setText(event.getText());
        }
    }
    
    @NiftyEventSubscriber(id="RadioGroup-2")
    public void onRadioGroup2Changed(final String id, final RadioButtonGroupStateChangedEvent event) {
        Label temp_wx = screen.findNiftyControl("temp_wx", Label.class); 
        Label pressure_wx = screen.findNiftyControl("pressure_wx", Label.class); 
        
        TextField airportIdentifierField = screen.findNiftyControl("airportIdentifier", TextField.class);
        Button airportIdentifierButton = screen.findNiftyControl("airportCheck", Button.class);
        
        String selected = event.getSelectedId();
        
        airportIdentifierField.disable();
        airportIdentifierButton.disable();
        
        if(selected.equals("live_wx")){     
            temp_wx.setText("TEMP");
            pressure_wx.setText("TEMP");
            
            airportIdentifierField.enable();
            airportIdentifierButton.enable();
        } else if (selected.equals("ISA_wx")){
            temp_wx.setText("15");
            pressure_wx.setText("1013.25");
        } else if (selected.equals("custom_wx")){
            temp_wx.setText("TEMP");
            pressure_wx.setText("TEMP");
        } else if (selected.equals("custom")){
            customFanDiameterField.enable();
            customMassFlowField.enable();
        }
        
    }

    public void submitSettings(){
        TextField airportIdentifierField = screen.findNiftyControl("airportIdentifier", TextField.class);

        String ident = airportIdentifierField.getRealText();

        
        Label fanDiameter = screen.findNiftyControl("fan_diameter", Label.class); 
        Label massFlow = screen.findNiftyControl("mass_flow", Label.class); 
        
        RadioButton PW2037Radio = screen.findNiftyControl("PW2037", RadioButton.class);
        RadioButton PW2040Radio = screen.findNiftyControl("PW2040", RadioButton.class);
        RadioButton RB211Radio = screen.findNiftyControl("RB211", RadioButton.class);
        RadioButton customRadio = screen.findNiftyControl("custom", RadioButton.class);

        gui.submitSettings(ident, Float.parseFloat(fanDiameter.getText()), Float.parseFloat(massFlow.getText()));
        customFanDiameterField.disable();
        customMassFlowField.disable();
            

    }
}
