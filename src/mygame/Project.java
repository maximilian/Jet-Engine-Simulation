package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Line;
import de.lessvoid.nifty.Nifty;

public class Project extends SimpleApplication {
    private ResourceLoader loader;
     
    boolean receivingLittle = false;
    boolean receivingMuch = false;
    boolean receivingCorrect = false;

    @Override
    public void simpleInitApp() {
        
        axisLines();
        
        setDisplayFps(false);
        setDisplayStatView(false);
        
        flyCam.setMoveSpeed(250);
        flyCam.setDragToRotate(true);
        
        loader = new ResourceLoader(assetManager, cam);
        rootNode.attachChild(loader.getTerrain());
         
        NiftyJmeDisplay niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        Nifty nifty = niftyDisplay.getNifty();
        /** Read your XML and initialize your custom ScreenController */
        MyControlScreen startScreen = new MyControlScreen();
        stateManager.attach(startScreen);
        nifty.fromXml("Interface/screen.xml", "start", startScreen);
        // attach the Nifty display to the gui view port as a processor
        guiViewPort.addProcessor(niftyDisplay);
        
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));

        // need this in any game involving physics
        BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
      
        rootNode.attachChild(loader.getAircraft());

        float engineRadius = calculateArea(2000, 140, 100);
        
        rootNode.attachChild(loader.getLeftEngineArea(engineRadius, receivingLittle, false));       
        rootNode.attachChild(loader.getRightEngineArea(engineRadius, receivingLittle, false));
       
        // You must add a light to make the model visible 
        rootNode.addLight(loader.getSun());
    }
    
    public void axisLines(){
             Line xaxis = new Line(Vector3f.ZERO, new Vector3f(400f, 0, 0));
        Geometry xaxisline = new Geometry("BOOM!", xaxis);
        
        Line yaxis = new Line(Vector3f.ZERO, new Vector3f(0, 400f, 0));
        Geometry yaxisline = new Geometry("BOOM!", yaxis);
        
        Line zaxis = new Line(Vector3f.ZERO, new Vector3f(0, 0, 400f));
        Geometry zaxisline = new Geometry("BOOM!", zaxis);
                
        Material area_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        area_mat.setColor("Color", ColorRGBA.Red);
        
         Material yarea_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        yarea_mat.setColor("Color", ColorRGBA.Green);
        
        Material zarea_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        zarea_mat.setColor("Color", ColorRGBA.Blue);
              
        xaxisline.setMaterial(area_mat);
        yaxisline.setMaterial(yarea_mat);
        zaxisline.setMaterial(zarea_mat);
        
        rootNode.attachChild(xaxisline);
        rootNode.attachChild(yaxisline);
        rootNode.attachChild(zaxisline);   
    }

    
     /*
     * Returns the radius of the area around the engine
     *
     * @param altitude, in feet, of the aircraft
     * @param speed, in knots, of the aircraft
     * @param engine setting, in percentage, of the aircraft engine
     * @return the radius, in correct jME scale, of the area around the engine
    */
    
    
    public float calculateArea(float altitude, float speed, float engineSetting){
        float engineFlowRate = getCorrectedMassFlow(altitude, (float) 548.85); // kg/s - needs to be corrected
        
        float engineDiameter = (float) 2.154; // m
        
        
        float engineRadius = engineDiameter / 2;
        float engineArea = (float) (Math.PI * (Math.pow(engineRadius, 2)));
        
        float speedMetres = (float) (0.514444444 * speed); // convert from knots to metres
        
       
        System.out.println("corrected mass flow: " + engineFlowRate);
        
        float airDensity = getCorrectedDensity(altitude);
        
        System.out.println("corrected density: " + airDensity);
        float engineNeeds = engineFlowRate / airDensity;
        float engineReceives = engineArea * speedMetres;
        

        if (engineReceives < engineNeeds){
            receivingLittle = true;
            System.out.println("too little");
        } else if(engineReceives > engineNeeds){
            receivingMuch = true;
            System.out.println("too much");
        } else {
            receivingCorrect = true;
            System.out.println("perfect");
        }
        
        
        float engineAreaRequired = engineNeeds / speedMetres;
        
        float engineRadiusRequired = (float) Math.sqrt((engineAreaRequired / Math.PI));
        
        float correctScale = (float) (engineRadiusRequired * 12.1943);
        
        System.out.println("Radius = " + engineRadiusRequired );
       return correctScale;   
    }
    
    /*
     * Returns the corrected temperature which can then be used to calculate
     * the corrected pressure and corrected density
     *
     * @param altitude, in feet, of the aircraft
     * @return the corrected temperature, in Kelvin
    */
    
    public float getCorrectedTemperature(float altitude){
        float meters = (float) (altitude / 3.2808);
        System.out.println("meters:" + meters);
        float correctedKelvin = (float) (288.15 - 0.0065*(meters));
        
        return correctedKelvin;
    }
    
    /*
     * Returns the corrected pressure which can then be used to calculate
     * the corrected density, and therefore the corrected air mass flow
     *
     * @param the correct temperature, in Kelvin
     * @return the corrected pressure, in Pascals
    */
    
    public float getCorrectedPressure(float correctedTemperature){
       
        float correctedPressure = (float) (101325 * Math.pow((correctedTemperature/288.15),((9.80665/(287*0.0065)))));
        
        return correctedPressure;
    }
    
    /*
     * Returns the corrected density which can then be used to calculate
     *
     * @param the altitude, in feet
     * @return the corrected density, in kg/m^3
    */
    
    public float getCorrectedDensity(float altitude){
        
        float correctedTemperature = getCorrectedTemperature(altitude);
        float correctedPressure = getCorrectedPressure(correctedTemperature);
        
        
        float correctedDensity = (correctedPressure/(287*correctedTemperature));
        
        return correctedDensity;
    }
    
    /*
     * Returns the corrected engine mass flow
     *
     * @param the altitude, in feet
     * @param the mass flow, in feet
     * @return the corrected density, in kg/m^3
    */
    
    
    public float getCorrectedMassFlow(float altitude, float massFlow){
        float correctedTemperature = getCorrectedTemperature(altitude);
        float correctedPressure = getCorrectedPressure(correctedTemperature);
        
        float theta = (float) (correctedTemperature/288.15);
        float delta = (float) (correctedPressure/101325);
        
        float correctedFlow = (float) (massFlow / ((Math.sqrt(theta)) / delta));
            
        return correctedFlow;
    }
    
    public boolean getEngineState(){
        
        return true;
    }
}