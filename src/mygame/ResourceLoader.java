/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Dome;
import com.jme3.system.AppSettings;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;
import com.jme3.ui.Picture;

/**
 * Class used to manage al the different resources/models.
 * 
 * @author max
 */
public class ResourceLoader {
    private DirectionalLight sun;
    private TerrainQuad terrain;
    private Spatial aircraft;
    private Spatial leftEngineArea;
    private Spatial rightEngineArea;
    
    private Spatial leftForwardArea;
    private Spatial rightForwardArea;
    
    private Spatial drone;
    
    private Picture cockpit;
    
    private AssetManager assetManager;
    private Camera terrainLodCamera;
    private AppSettings settings;
    
    public ResourceLoader(AssetManager assetManager, Camera terrainLodCamera, AppSettings settings) {
        this.assetManager = assetManager;
        this.terrainLodCamera = terrainLodCamera;
        this.settings = settings;
    }
    
    public DirectionalLight getSun(){
        if(sun == null){
            initSun();
        }
        return sun;
    }
    
        
    public TerrainQuad getTerrain(){
        if(terrain == null){
            initTerrain();
        }
        return terrain;
    }
    
    
    public Spatial getAircraft(){
        if(aircraft == null) {
            initAircraft();
        } 
        return aircraft;
    }
    
    public Picture getCockpit(){
        if(cockpit == null){
            initCockpit();
        }
        
        return cockpit;
    }
    
    public Spatial getLeftEngineArea(float engineRadius, boolean receivingLittle, boolean submitButton, int altitude){
        if (leftEngineArea == null || submitButton){
            initLeftEngineArea(engineRadius, receivingLittle, altitude);       
        }
        return leftEngineArea;
    }
    
    public Spatial getRightEngineArea(float engineRadius, boolean receivingLittle, boolean submitButton, int altitude){
        if (rightEngineArea == null || submitButton){
            initRightEngineArea(engineRadius, receivingLittle, altitude);    
        }
        return rightEngineArea;
    }
    
    public Spatial getRightForwardArea(float engineRadius, int altitude, boolean submitButton){
        if (rightForwardArea == null || submitButton){
            initRightForwardArea(engineRadius, altitude);
        }
        
        return rightForwardArea;
    }
    
    /*
     * Used by simulation
    */
    
    public Spatial getRightForwardArea(){
        return rightForwardArea;
    }
    
    public Spatial getLeftForwardArea(){
        return leftForwardArea;
    }
    
    public Spatial getLeftForwardArea(float engineRadius, int altitude, boolean submitButton){
        if (leftForwardArea == null || submitButton){
            initLeftForwardArea(engineRadius, altitude);
        }
        
        return leftForwardArea;
    }
    
    public Spatial getLeftEngineArea(){
        return leftEngineArea;
    }
    
    public Spatial getRightEngineArea(){
        return rightEngineArea;
    }
    
    public void initAircraft(){
         // Load a model from test_data (OgreXML + material + texture)
        aircraft = assetManager.loadModel("Models/3d-model.j3o");
        aircraft.scale(0.3f, 0.3f, 0.3f); 
    }
    
    public void initRightEngineArea(float engineRadius, boolean receivingLittle, int altitude){
        Dome rightEngine;
        
         if(receivingLittle){
            rightEngine = new Dome(new Vector3f(-49f, 51f,-15.5f-altitude), 100, 30, engineRadius, false);
        } else{
            rightEngine = new Dome(new Vector3f(-49f, 51f,-15.5f-altitude), 2, 30, engineRadius, false);
        }
        rightEngineArea = new Geometry("Right Engine", rightEngine);
        
        
        Material rightEngineAreaMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        rightEngineAreaMat.setColor("Color", new ColorRGBA(0,0,255,0.5f));
        
        // transparent hemisphere
        rightEngineAreaMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        rightEngineArea.setQueueBucket(Bucket.Transparent);
        rightEngineArea.setMaterial(rightEngineAreaMat);

        Quaternion rotation = new Quaternion();
        rotation.fromAngleAxis((float) (FastMath.PI/2), new Vector3f(1,0,0) );
        
        rightEngineArea.rotate(rotation);
        
    }
    
    public void initLeftEngineArea(float engineRadius, boolean receivingLittle, int altitude){
        Dome leftEngine;
        if(receivingLittle){
            leftEngine = new Dome(new Vector3f(49f, 51f,-15.5f-altitude), 100, 30, engineRadius, false);
        } else{
            leftEngine = new Dome(new Vector3f(49f, 51f,-15.5f-altitude), 2, 30, engineRadius, false);
        }
        leftEngineArea = new Geometry("Left Engine", leftEngine);
        
        Material area_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        area_mat.setColor("Color", new ColorRGBA(255,0,0,0.5f));
        
        //transparent hemisphere
        area_mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        leftEngineArea.setQueueBucket(Bucket.Transparent);
        leftEngineArea.setMaterial(area_mat);
        Quaternion rotation = new Quaternion();
        rotation.fromAngleAxis((float) (FastMath.PI/2), new Vector3f(1,0,0) );
        
        leftEngineArea.rotate(rotation);
    }
    
    public void initRightForwardArea(float engineRadius, int altitude){
        Cylinder cylinder = new Cylinder(100, 100, engineRadius,5000);
        rightForwardArea = new Geometry("Forward Right Engine Area", cylinder);
        
        rightForwardArea.setLocalTranslation(new Vector3f(-49f,15f+altitude,2550));
        
        Material area_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        area_mat.setColor("Color", new ColorRGBA(255,0,0,0.4f));
        area_mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        rightForwardArea.setMaterial(area_mat);      
    }
    
     public void initLeftForwardArea(float engineRadius, int altitude){
        Cylinder cylinder = new Cylinder(100, 100, engineRadius,5000);
        leftForwardArea = new Geometry("Forward Left Engine Area", cylinder);
        
        leftForwardArea.setLocalTranslation(new Vector3f(49f,15f+altitude,2550));
        
        Material area_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        area_mat.setColor("Color", new ColorRGBA(255,0,0,0.4f));
        area_mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        leftForwardArea.setMaterial(area_mat);      
    }
     
     public void initDrone(){
      // Load a model from test_data (OgreXML + material + texture)
        drone = assetManager.loadModel("Models/AR_Drone.j3o");
        drone.scale(2f, 2f, 2f);
        
        Material area_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        
        //area_mat.setColor("Color", ColorRGBA.White);
        //drone.setMaterial(area_mat);
        drone.move(new Vector3f(49f,500f,1219.3f));
     }
     
     public Spatial getDrone(){
         if (drone == null){
             initDrone();
         }
         
        return this.drone;
     }
    
    public void initTerrain(){
    
    /** 1. Create terrain material and load four textures into it. */
        Material mat_terrain = new Material(assetManager,
            "Common/MatDefs/Misc/Unshaded.j3md");

        Texture airport = assetManager.loadTexture("Textures/staticmap.png");
        
        mat_terrain.setTexture("ColorMap", airport);

        int patchSize = 64;
        terrain = new TerrainQuad("my terrain", patchSize, 513, null);
        
        /** 4. We give the terrain its material, position & scale it, and attach it. */
        terrain.setMaterial(mat_terrain);
        terrain.setLocalTranslation(-50, 0, 2400);
        
        /* This quaternion stores a 45 degree rotation */
        Quaternion rotation = new Quaternion();
        rotation.fromAngleAxis( ((FastMath.PI/4)) , new Vector3f(0,1,0) );
        /* The rotation is applied: The object rolls by 180 degrees. */
        terrain.setLocalRotation( rotation );
        terrain.setLocalScale(7f, 7f, 7f);
        

        /** 5. The LOD (level of detail) depends on were the camera is: */
        TerrainLodControl control = new TerrainLodControl(terrain, terrainLodCamera);
        terrain.addControl(control);
    }
    
    public void initSun(){
        sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
    }
    
    public void initCockpit(){    
        cockpit = new Picture("Cockpit");
        cockpit.setImage(assetManager, "Textures/757VFR.png", true);
        cockpit.setWidth(settings.getWidth());
        cockpit.setHeight(settings.getHeight());
        cockpit.setPosition(0, 80);

    }
    
     
   
    
}
