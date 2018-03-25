package mygame.gui;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.Label;
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
 * Communicates with the GUI app state. This is the settings GUI panel.
 * 
 * @author Maximilian Morell
 */
public class MyOptionsScreen extends AbstractAppState implements ScreenController {
    private GuiAppState gui;
    private Nifty nifty;
    private Screen screen;
    private Converter converter;
    
    private TextField customFanDiameterField;
    private TextField customMassFlowField;
    
    private TextField customTempField;
    private TextField customPressureField;
    
    // used for updating weather panel in ControlScreen
    private String selectedWeatherType;
    
    /**
     * Constructor for the options/settings window GUI screen
     * 
     * @param gui the GUI app state
     */
    public MyOptionsScreen(GuiAppState gui){
        this.gui = gui;
        this.converter = new Converter();
        
        this.selectedWeatherType = "live";
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
    }
    
    @Override
    public void update(float tpf) {
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
    }

    /**
     * ScreenController method - supplies the screen and nifty object.
     * @param nifty
     * @param screen 
     */
    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        
        customFanDiameterField = screen.findNiftyControl("engine-customDiameter", TextField.class); 
        customMassFlowField = screen.findNiftyControl("engine-customMass", TextField.class); 
        
        customTempField = screen.findNiftyControl("engine-wx-customTemp", TextField.class);
        customPressureField = screen.findNiftyControl("engine-wx-customPressure", TextField.class);
        
        customFanDiameterField.disable();
        customMassFlowField.disable();

        customTempField.disable();
        customPressureField.disable();
    }

    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {
        
    }
    
    /**
     * Attempts to get the airport weather data (to be displayed to user) upon being submitted by the user
     */
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
    
    /**
     * Checks for a change in the engine type radio group for the engine information
     * 
     * @param id id of the radio button group
     * @param event event that occurred
     */
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
    
    /**
     * Checks for a change in the custom engine data/weather information text fields
     * and displays the text in the gui
     * 
     * @param id the textfield that changed
     * @param event the event that occurred
     */
    @NiftyEventSubscriber(pattern="engine-.*")
    public void onTextFieldChange(final String id, final TextFieldChangedEvent event){
        Label fanDiameter = screen.findNiftyControl("fan_diameter", Label.class); 
        Label massFlow = screen.findNiftyControl("mass_flow", Label.class); 
        
        Label temp_wx = screen.findNiftyControl("temp_wx", Label.class); 
        Label pressure_wx = screen.findNiftyControl("pressure_wx", Label.class); 
        
        if(event.getTextFieldControl().getId().equals("engine-customDiameter")){
            fanDiameter.setText(event.getText());
        }

        if(event.getTextFieldControl().getId().equals("engine-customMass")){
            massFlow.setText(event.getText());
        }
        
        if(event.getTextFieldControl().getId().equals("engine-wx-customTemp")){
            temp_wx.setText(event.getText());
        }
        
        if(event.getTextFieldControl().getId().equals("engine-wx-customPressure")){
            pressure_wx.setText(event.getText());
        }
    }
    
    /**
     * Checks for a change in the weather radio group
     * 
     * @param id the id of the radio button group
     * @param event the event that occurred
     */
    @NiftyEventSubscriber(id="RadioGroup-2")
    public void onRadioGroup2Changed(final String id, final RadioButtonGroupStateChangedEvent event) {
        Label temp_wx = screen.findNiftyControl("temp_wx", Label.class); 
        Label pressure_wx = screen.findNiftyControl("pressure_wx", Label.class); 
        
        TextField airportIdentifierField = screen.findNiftyControl("airportIdentifier", TextField.class);
        Button airportIdentifierButton = screen.findNiftyControl("airportCheck", Button.class);

        String selected = event.getSelectedId();
        
        airportIdentifierField.disable();
        airportIdentifierButton.disable();
        
        customTempField.disable();
        customPressureField.disable();
        
        /**
         * Checks whether the weather is chosen to be live, ISA or custom
         */
        if(selected.equals("live_wx")){ 
            selectedWeatherType = "live";
            temp_wx.setText("-");
            pressure_wx.setText("-");
            
            airportIdentifierField.enable();
            airportIdentifierButton.enable();
        } else if (selected.equals("ISA_wx")){
            selectedWeatherType = "ISA";
            temp_wx.setText("15");
            pressure_wx.setText("1013.25");
        } else {
            selectedWeatherType = "custom";
            temp_wx.setText("15");
            pressure_wx.setText("1013.25");
            
            customTempField.enable();
            customPressureField.enable();
        } 
        
    }
    
    /**
     * Submit the changes in the settings GUI
     */
    public void submitSettings(){
        TextField airportIdentifierField = screen.findNiftyControl("airportIdentifier", TextField.class);
        
        String ident = airportIdentifierField.getRealText();

        Label fanDiameter = screen.findNiftyControl("fan_diameter", Label.class); 
        Label massFlow = screen.findNiftyControl("mass_flow", Label.class); 

        Label temp_wx = screen.findNiftyControl("temp_wx", Label.class); 
        Label pressure_wx = screen.findNiftyControl("pressure_wx", Label.class); 
        
        // if airport not checked before submitting then get airport data first
        if(temp_wx.getText().equals("-")){
            getAirportData();
        }
        
        // Submits all the data to be used by the gui app state
        gui.submitSettings(ident, Float.parseFloat(fanDiameter.getText()), Float.parseFloat(massFlow.getText()), Float.parseFloat(temp_wx.getText()), Float.parseFloat(pressure_wx.getText()), selectedWeatherType);
        
        customFanDiameterField.disable();
        customMassFlowField.disable();
    }
    
}
