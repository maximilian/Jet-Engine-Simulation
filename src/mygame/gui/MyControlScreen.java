/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * Communicates with the Gui app state.
 * 
 * @author max
 */
public class MyControlScreen implements ScreenController {
    Nifty nifty;
    Screen screen;
    private GuiAppState gui;
    private Project app;    
    
    private Element submitAircraftDetailsButton;   
    private Element collisionWindowLayer;
    private Slider visualisationSlider;
    
    /* flag for visualisation slider */
    private boolean visualisationSet;
    
    public MyControlScreen(GuiAppState gui){
        visualisationSet = false;
        this.gui = gui;
    }
    
    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        
        submitAircraftDetailsButton = screen.findElementById("submitButton");
 
         // Hide the collision window layer by default
         collisionWindowLayer = screen.findElementById("windows");
         collisionWindowLayer.hide();

         visualisationSlider = screen.findNiftyControl("simulationTimeControl", Slider.class);
         visualisationSlider.disable();
         
        /*Label fanRadiusLabel = screen.findNiftyControl("fanRadiusLabel", Label.class);
        
        float fanRadius = gui.getAircraft().getEngineDiameter() / 2;
        DecimalFormat df = new DecimalFormat("##.##");
        String roundedRadius = df.format(fanRadius);
        fanRadiusLabel.setText(roundedRadius + " metres"); */
        
    }

    @Override
    public void onStartScreen() {
        screen.findElementById("tab2_panel").hide();
        screen.findElementById("tab3_panel").hide();
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
        submitAircraftDetailsButton.disable();
        
        TextField altitudeField = screen.findNiftyControl("altitudeField", TextField.class);  
        String altitudeString = altitudeField.getRealText();
        int fieldAltitude = Integer.parseInt(altitudeString);
        
        TextField speedField = screen.findNiftyControl("speedField", TextField.class);  
        String speedString = speedField.getRealText();
        int fieldSpeed = Integer.parseInt(speedString);
        
        Slider sliderField = screen.findNiftyControl("sliderH", Slider.class);
        float fieldEngineSetting = sliderField.getValue();
        
        gui.setAltitude(fieldAltitude);
        gui.setSpeed(fieldSpeed);
        gui.setEngineSetting(fieldEngineSetting);
        
        
        gui.submitAircraftVariables();   
        
        updateAircraftLabels(fieldAltitude, fieldSpeed);
    }
    
    public void updateAircraftLabels(int altitude, int speed){
        Label altitudeLabel = screen.findNiftyControl("altitudeLabel", Label.class); 
        Label speedLabel = screen.findNiftyControl("speedLabel", Label.class); 

        altitudeLabel.setText(Integer.toString(altitude));
        speedLabel.setText(Integer.toString(speed));
    }
    
    public void takeoffVis(){
        
    }
    
    
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
        
        gui.setSimulation(droneDistance, fieldAltitude, fieldSpeed, aircraftView);
    
    }
    
    public void runSimulation(){
        gui.runSimulation();

    }
    
    public void resetSimulation(){
        gui.resetSimulation();
    }
    
    public void showCollisionWindow(float time, int speed, int distance, boolean aircraftView){   
        String initial = ""
                + " \n - Endangered the safety of an aircraft, putting over 200 lives at risk."
                + " \n - Could face up to 5 years in prison."
                + "\n\n Be Drone Safe by following the Drone Code"
                + "\n http://dronesafe.uk";
        
        Label windowText = screen.findNiftyControl("time", Label.class);
        
        if(aircraftView){
            windowText.setText("The aircraft pilot spotted a drone " +
                distance +" meters away and was left you with\nonly " + 
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
    
        /*
    public void updateRadiusText(){
        Label radiusLabel = screen.findNiftyControl("radiusLabel", Label.class); 
        DecimalFormat df = new DecimalFormat("##.##");
        String roundedRadius = df.format(gui.getEngineArea().getEngineRadiusReal());
        radiusLabel.setText(roundedRadius + " metres"); 
    }*/
    
    /*
     * Hides the conflict window after collision
    */
    public void closeConflictWindow(){

        collisionWindowLayer.hide();
        gui.getRootNode().detachChildNamed("Cockpit");
        
        gui.resetSimulation();
    }
    
    int oldValue = 0;
    
    @NiftyEventSubscriber(pattern=".*Field")
    public void onTextfieldChange(final String id, final TextFieldChangedEvent event) {
               submitAircraftDetailsButton.enable();
        
        // validation, ensure value entered is numeric and valid
        int parsedInt;
        try{
            parsedInt = Integer.parseInt(event.getText());
            
             if(parsedInt < 0){
                event.getTextFieldControl().setText(Integer.toString(oldValue));
             } else{
                 oldValue = parsedInt;
             }
      
        
        } catch(NumberFormatException e){
             
             event.getTextFieldControl().setText(Integer.toString(oldValue));
        } 
    }
    
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
    
    @NiftyEventSubscriber(id="simulationTimeControl")
    public void SliderChangedEvent(final String id, final SliderChangedEvent event){
        if (visualisationSet){
            float percentage = event.getSlider().getValue();


            // distance travelled (real life units)
            float currDistance = (percentage/100) * 1850;
            // speed based on real life units, using v = u + at
            float speed = (float) Math.sqrt(2 * 1.605 * currDistance);

            // distance that aircraft will move. Based on 0 - 3188 scale, where 3188 is 68% of total Gla runway
            float distanceToMove = (percentage/100) * 3188;

            float speedKnots = (float) (speed * 1.94384);

            gui.runVisualisation(speedKnots, distanceToMove);

            System.out.println("aircraft speed=" + speedKnots);

            Label speedVisLabel = screen.findNiftyControl("speedVisualisation", Label.class); 
            Label radiusVisLabel = screen.findNiftyControl("radiusVisualisation", Label.class); 

            speedVisLabel.setText(Integer.toString(Math.round(speedKnots)) + " knots");


            DecimalFormat df = new DecimalFormat("##.##");
            String roundedRadius = df.format(gui.getEngineRadius());
            radiusVisLabel.setText(roundedRadius + " meters");

            if (percentage > 100){
             gui.rotateVisualisation(percentage);
            }
        }
    }
    
    public void setVisualisation(){
        Element resetVis = screen.findElementById("resetVisualisationField");
        Element setVis = screen.findElementById("setVisualisationField");

        resetVis.enable();
        setVis.disable();

        visualisationSlider.enable();
        visualisationSet = true;
        gui.setVisualisation();
        

    }
    
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
    
    public void setWeatherInformation(String fieldName, int pressure, float temperature, LocalDateTime datetime){
        Label airportIdLabel = screen.findNiftyControl("airportId", Label.class); 
        Label airportPressureLabel = screen.findNiftyControl("airportPressure", Label.class); 
        Label airportTempLabel = screen.findNiftyControl("airportTemp", Label.class); 
        Label airportLastUpdateLabel = screen.findNiftyControl("lastUpdate", Label.class);
        
        airportIdLabel.setText(fieldName);
        airportPressureLabel.setText(Integer.toString(pressure)+" mbar");
        airportTempLabel.setText(Float.toString(temperature)+" C");
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyy HH:mm");
        String formattedDate = datetime.format(formatter);
       
        airportLastUpdateLabel.setText(formattedDate);
    }
    
    public void openSettings(){
        gui.openSettings();
    }
    
 


}
