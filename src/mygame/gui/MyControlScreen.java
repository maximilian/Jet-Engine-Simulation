package mygame.gui;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.CheckBoxStateChangedEvent;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.RadioButton;
import de.lessvoid.nifty.controls.Slider;
import de.lessvoid.nifty.controls.SliderChangedEvent;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.controls.TextFieldChangedEvent;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.screen.Screen;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import mygame.Project;
import mygame.states.GuiAppState;

/**
 * Communicates with the GUI app state. This is the main GUI panel.
 * 
 * @author Maximilian Morell
 */
public class MyControlScreen implements ScreenController {
    Nifty nifty;
    Screen screen;
    private GuiAppState gui;
    private Project app;    
    
    private Element submitAircraftDetailsButton;   
    private Element collisionWindowLayer;
    private Slider visualisationSlider;
    
    // flag for visualisation slider
    private boolean visualisationSet;
    
    // Either live, ISA or custom
    private String weatherType;
    
    /**
     * Constructor for the main GUI screen
     * 
     * @param gui Gui app state to communicate with
     * @param weatherType Type of weather (live, ISA or custom)
     */
    public MyControlScreen(GuiAppState gui, String weatherType){
        visualisationSet = false;
        this.weatherType = weatherType;
        
        this.gui = gui;
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
        
        submitAircraftDetailsButton = screen.findElementById("submitButton");
 
        // Hide the collision window layer by default
        collisionWindowLayer = screen.findElementById("windows");
        collisionWindowLayer.hide();

        visualisationSlider = screen.findNiftyControl("simulationTimeControlSlider", Slider.class);
        visualisationSlider.disable();
    }

    /**
     * Upon starting the screen only show the first tab.
     */
    @Override
    public void onStartScreen() {
        screen.findElementById("tab2_panel").hide();
        screen.findElementById("tab3_panel").hide();
    }

    @Override
    public void onEndScreen() {
        
    }
  
    /**
     * Method executed for take-off visualisation feature button
     */
    public void startTOVisualisation(){
        screen.findElementById("tab1_panel").hide();
        gui.hideForwardArea();
        gui.setEngineSetting(100);
        screen.findElementById("tab2_panel").show();
    }
    
    /**
     * Method executed for exit take-off visualisation feature button
     */
    public void exitTOVisualisation(){
        screen.findElementById("tab2_panel").hide();
        screen.findElementById("tab1_panel").show();
        gui.frontView();
        gui.setSpeed(160);
        gui.submitAircraftVariables();
    }
    
    /**
     * Method executed for campaign mode button
     */
    public void startCampaign(){
        screen.findElementById("tab1_panel").hide();
        screen.findElementById("tab3_panel").show();
    }
    
    /**
     * Method executed for exiting campaign mode
     */
    public void exitCampaign(){
        screen.findElementById("tab3_panel").hide();
        screen.findElementById("tab1_panel").show();
    }
    
    /**
     * Camera to front view of the aircraft
     */
    public void frontView() {
        gui.frontView();
     }

    /**
     * Camera to above view of the aircraft
     */
    public void aboveView() {
        gui.aboveView();
     }
    
    /**
     * Camera to right engine view of the aircraft
     */
    public void rightEngineView() {
        gui.rightEngineView();
     }
    
    /**
     * Camera to left engine view of the aircraft
     */
    public void leftEngineView() {  
        gui.leftEngineView();
     }
    
    /**
     * Submit button for the aircraft variables submitted by the user
     */
    public void submit(){
        submitAircraftDetailsButton.disable();
        
        TextField altitudeField = screen.findNiftyControl("altitudeField", TextField.class);  
        String altitudeString = altitudeField.getRealText();
        int fieldAltitude = Integer.parseInt(altitudeString);
        
        TextField speedField = screen.findNiftyControl("speedField", TextField.class);  
        String speedString = speedField.getRealText();
        int fieldSpeed = Integer.parseInt(speedString);
        
        Slider sliderField = screen.findNiftyControl("engineSettingSlider", Slider.class);
        float fieldEngineSetting = sliderField.getValue();
        
        gui.setAltitude(fieldAltitude);
        gui.setSpeed(fieldSpeed);
        gui.setEngineSetting(fieldEngineSetting);

        gui.submitAircraftVariables();   
        
        updateAircraftLabels(fieldAltitude, fieldSpeed);
    }
    
    /**
     * Updates the labels for altitude and speed of the aircraft used in campaign mode
     * @param altitude altitude of the aircraft
     * @param speed speed of the aircraft
     */
    public void updateAircraftLabels(int altitude, int speed){
        Label altitudeLabel = screen.findNiftyControl("altitudeLabel", Label.class); 
        Label speedLabel = screen.findNiftyControl("speedLabel", Label.class); 

        altitudeLabel.setText(Integer.toString(altitude));
        speedLabel.setText(Integer.toString(speed));
    }

    
    /**
     * Sets the simulation gui information for the user
     */
    public void setSimulation(){
        TextField droneDistanceField = screen.findNiftyControl("droneDistanceInput", TextField.class);  
        String droneDistanceString = droneDistanceField.getRealText();
        int droneDistance = Integer.parseInt(droneDistanceString);
        
        TextField altitudeField = screen.findNiftyControl("altitudeField", TextField.class);  
        String altitudeString = altitudeField.getRealText();
        int fieldAltitude = Integer.parseInt(altitudeString);
        
        TextField speedField = screen.findNiftyControl("speedField", TextField.class);  
        String speedString = speedField.getRealText();
        int fieldSpeed = Integer.parseInt(speedString);
        
                
        RadioButton aircraftRadioButton = screen.findNiftyControl("radio-option-1", RadioButton.class);
        Boolean aircraftView = aircraftRadioButton.isActivated();
        
        // Sets the simulation environment by calling main app state
        gui.setSimulation(droneDistance, fieldAltitude, fieldSpeed, aircraftView);
    
    }
    
    /**
     * Executed when run simulation button is pressed.
     */
    public void runSimulation(){
        gui.runSimulation();

    }
    
    /**
     * Executed when reset simulationb button is pressed.
     */
    public void resetSimulation(){
        gui.resetSimulation();
    }
    
    /**
     * Collision window showed when the aircraft and drone collide.
     * @param time time taken to collide
     * @param speed speed at which the aircraft travelled
     * @param distance distance of the drone from the aircraft
     * @param aircraftView whether the view was the aircraft pilot's
     */
    public void showCollisionWindow(float time, int speed, int distance, boolean aircraftView){   
        String initial = ""
                + " \n - Endangered the safety of an aircraft, putting over 200 lives at risk."
                + " \n - Could face up to 5 years in prison."
                + "\n\n Be Drone Safe by following the Drone Code"
                + "\n http://dronesafe.uk";
        
        Label windowText = screen.findNiftyControl("time", Label.class);
        
        if(aircraftView){
            windowText.setText("The aircraft pilot spotted a drone " +
                distance +" meters away and was left with\nonly " + 
                time +" seconds to react\n\n "+ 
                "With little time to react, the drone pilot:"+initial);
        } else {
            windowText.setText("Spotting the aircraft " +
                distance +" meters away flying at " + 
                speed + " knots" +
                " left you with\nonly " + 
                time +" seconds to react\n\n "+ 
                "With little time to react, you:"+initial);
        }

        collisionWindowLayer.show();
        
    }

    /**
     * Hides the conflict window when the user presses close
     */
    public void closeConflictWindow(){

        collisionWindowLayer.hide();
        gui.getRootNode().detachChildNamed("Cockpit");
        
        gui.resetSimulation();
    }

    /**
     * Detects a change in any textfield on the main screen
     * @param id id of the textfield
     * @param event event that occurred
     */    
    @NiftyEventSubscriber(pattern=".*Field")
    public void onTextfieldChange(final String id, final TextFieldChangedEvent event) {
               submitAircraftDetailsButton.enable();
    }
    
    /**
     * Detects whether the user clicked on the forward engine area toggle
     * @param id id of the toggle
     * @param event event that occurred
     */
    @NiftyEventSubscriber(id="forwardEngineAreaToggle")
    public void CheckBoxStateChangedEvent(final String id, final CheckBoxStateChangedEvent event){
        if(event.getCheckBox().isChecked()) {
            gui.setShowForwardArea(true);
            gui.showForwardArea();
        } else {
            gui.hideForwardArea();
            gui.setShowForwardArea(false);
        }
    }
    
    /**
     * Detects whether there was a change in a slider
     * @param id id of the slider
     * @param event event that occurred
     */
    @NiftyEventSubscriber(pattern=".*Slider")
    public void SliderChangedEvent(final String id, final SliderChangedEvent event){
        // if the slider was for the take-off visualisation
        if (id.equals("simulationTimeControlSlider")){
            if (visualisationSet){
                float percentage = event.getSlider().getValue();

                // distance travelled (real life units)
                float currDistance = (percentage/100) * 1850;
                // speed based on real life units, using v = u + at
                float speed = (float) Math.sqrt(2 * 1.605 * currDistance);

                // distance that aircraft will move. Based on 0 - 3188 scale, where 3188 is 68% of total runway at Glasgow airport
                float distanceToMove = (percentage/100) * 3188;
                
                // aircraft speed in knots
                float speedKnots = (float) (speed * 1.94384);

                gui.runVisualisation(speedKnots, distanceToMove);

                Label speedVisLabel = screen.findNiftyControl("speedVisualisation", Label.class); 
                Label radiusVisLabel = screen.findNiftyControl("radiusVisualisation", Label.class); 
                
                // updates speed label
                speedVisLabel.setText(Integer.toString(Math.round(speedKnots)) + " knots");
                
                // updates engine radius label
                DecimalFormat df = new DecimalFormat("##.##");
                String roundedRadius = df.format(gui.getEngineRadius());
                radiusVisLabel.setText(roundedRadius + " meters");
                
                // if take-off speed reached - rotate aircraft
                if (percentage > 100){
                 gui.rotateVisualisation(percentage);
                }
            }
        // if the slider was for the aircraft engine setting on the main screen
        } else if (id.equals("engineSettingSlider")){
            submitAircraftDetailsButton.enable();
        }

    }
    
    /**
     * Sets the take-off visualisation 
     */
    public void setVisualisation(){
        Element resetVis = screen.findElementById("resetVisualisationField");
        Element setVis = screen.findElementById("setVisualisationField");

        resetVis.enable();
        setVis.disable();

        visualisationSlider.enable();
        visualisationSet = true;
        gui.setVisualisation();
        

    }
    
    /**
     * Resets the take-off visualisation
     */
    public void resetVisualisation(){
        Element resetVis = screen.findElementById("resetVisualisationField");
        Element setVis = screen.findElementById("setVisualisationField");
        
        resetVis.disable();
        setVis.enable();
        
        visualisationSlider.setValue(0);
        visualisationSlider.disable();
        visualisationSet = false;
        
        gui.resetVisualisation();
    }
    
    /**
     * Sets the weather information display in the GUI
     * 
     * @param fieldName the airport identifier
     * @param pressure the airport pressure, Millibars
     * @param temperature the airport temperature, Celsius
     * @param datetime the time and date the weather report was issued
     */
    public void setWeatherInformation(String fieldName, int pressure, float temperature, LocalDateTime datetime){
        Label weatherHeader = screen.findNiftyControl("weather-data-header", Label.class);
        Label airportIdLabel = screen.findNiftyControl("airportId", Label.class); 

        Label airportPressureLabel = screen.findNiftyControl("airportPressure", Label.class); 
        Label airportTempLabel = screen.findNiftyControl("airportTemp", Label.class); 
        Label airportLastUpdateLabel = screen.findNiftyControl("lastUpdate", Label.class);
        
        if(weatherType.equals("live")){
            weatherHeader.setText("Live Weather");
            airportIdLabel.setText(fieldName);
                    
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyy HH:mm");
            String formattedDate = datetime.format(formatter);

            airportLastUpdateLabel.setText(formattedDate);
        } else if(weatherType.equals("ISA")){
            weatherHeader.setText("ISA Weather");
            airportIdLabel.setText("n/a");
            airportLastUpdateLabel.setText("n/a");

        } else {
            weatherHeader.setText("Custom Weather");
            airportIdLabel.setText("n/a");
            airportLastUpdateLabel.setText("n/a");
        }

        airportPressureLabel.setText(Integer.toString(pressure)+" mbar");
        airportTempLabel.setText(Float.toString(temperature)+" C");

    }
    
    /**
     * Open the options menu to change system settings
     */
    public void openSettings(){
        gui.openSettings();
    }
    
 


}
