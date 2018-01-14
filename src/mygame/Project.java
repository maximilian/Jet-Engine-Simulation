package mygame;

import calculation.Aircraft;
import calculation.Drone;
import mygame.states.GuiAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Line;

public class Project extends SimpleApplication {
    private ResourceLoader loader;
    private GuiAppState gui;
    
    private Aircraft aircraftObject;
    private Drone droneObject;
     
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
        cam.setFrustumFar(5000);
        
        
        loader = new ResourceLoader(assetManager, cam);
        rootNode.attachChild(loader.getTerrain());
         
        gui = new GuiAppState();
        stateManager.attach(gui);
        
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));

        // need this in any game involving physics
        BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
      
        rootNode.attachChild(loader.getAircraft());
        aircraftObject = new Aircraft(rootNode.getChild("3d-model-objnode"));
        
        
        
        rootNode.attachChild(loader.getDrone());
        droneObject = new Drone(rootNode.getChild("AR_Drone-geom-0"));


        rootNode.attachChild(loader.getLeftEngineArea(0, receivingLittle, false, 0));       
        rootNode.attachChild(loader.getRightEngineArea(0, receivingLittle, false, 0));
       
        // add a light to make the model visible 
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

   
    
    public ResourceLoader getResourceLoader(){
        return loader;
    }
    
    public Aircraft getAircraft(){
        return aircraftObject;
    }
    
    public Drone getDrone(){
        return droneObject;
    }
}