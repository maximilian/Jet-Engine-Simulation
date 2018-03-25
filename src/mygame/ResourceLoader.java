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
 * Boeing 757-200 3D model credits to "2001kraft" from Cgtrader:
 * https://www.cgtrader.com/free-3d-models/aircraft/commercial/template-boeing-757-200
 * 
 * Drone 3D model credits to "ortho13" from Cgtrader: 
 * https://www.cgtrader.com/free-3d-models/aircraft/private/drone-355ed96d-7f05-4c34-8e55-ea53e905a9b4
 * 
 * Boeing 757 cockpit panel credits to Mario Coelho from AVSIM
 * 
 * @author Maximilian Morell
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
    
    private final AssetManager assetManager;
    private final Camera terrainLodCamera;
    private final AppSettings settings;
    
    /**
     * Resource loader constructor
     * @param assetManager app asset manager
     * @param terrainLodCamera app camera
     * @param settings store of configuration
     */
    public ResourceLoader(AssetManager assetManager, Camera terrainLodCamera, AppSettings settings) {
        this.assetManager = assetManager;
        this.terrainLodCamera = terrainLodCamera;
        this.settings = settings;
    }
    
    /**
     * Get the light for the visualisation
     * @return sun the light source
     */
    public DirectionalLight getSun(){
        if(sun == null){
            initSun();
        }
        return sun;
    }
    
    /**
     * Get the visualisation terrain
     * @return terrain
     */
    public TerrainQuad getTerrain(){
        if(terrain == null){
            initTerrain();
        }
        return terrain;
    }
    
    /**
     * Get the aircraft spatial
     * @return aircraft
     */
    public Spatial getAircraft(){
        if(aircraft == null) {
            initAircraft();
        } 
        return aircraft;
    }
    
    /**
     * Get the aircraft cockpit panel (for campaign mode)
     * @return cockpit cockpit panel picture
     */
    public Picture getCockpit(){
        if(cockpit == null){
            initCockpit();
        }
        
        return cockpit;
    }
    
    /**
     * Get the left area around the engine
     * @param engineRadius the radius of the area around the engine
     * @param receivingLittle state of the engine
     * @param submitButton whether the method was accessed via submit button
     * @param altitude altitude of the aircraft
     * @return the left engine area spatial
     */
    public Spatial getLeftEngineArea(float engineRadius, boolean receivingLittle, boolean submitButton, int altitude){
        if (leftEngineArea == null || submitButton){
            initLeftEngineArea(engineRadius, receivingLittle, altitude);       
        }
        return leftEngineArea;
    }
    
    /**
     * Get the right area around the engine
     * @param engineRadius the radius of the area around the engine
     * @param receivingLittle state of the engine
     * @param submitButton whether the method was accessed via submit button
     * @param altitude altitude of the aircraft
     * @return the right engine area spatial
     */
    public Spatial getRightEngineArea(float engineRadius, boolean receivingLittle, boolean submitButton, int altitude){
        if (rightEngineArea == null || submitButton){
            initRightEngineArea(engineRadius, receivingLittle, altitude);    
        }
        return rightEngineArea;
    }
    
    /**
     * Get the area around the engine for the left engine.
     * 
     * @return leftEngineArea
     */
    public Spatial getLeftEngineArea(){
        return leftEngineArea;
    }
    
    /**
     * Get the area around the engine for the right engine.
     * 
     * @return rightEngineArea
     */
    public Spatial getRightEngineArea(){
        return rightEngineArea;
    }
    /**
     * Get the right engine's future forward area
     * 
     * @param engineRadius the radius of the area around the engine
     * @param altitude the altitude of the aircraft
     * @param submitButton whether the method was accessed via submit button
     * @return rightForwardArea
     */
    public Spatial getRightForwardArea(float engineRadius, int altitude, boolean submitButton){
        if (rightForwardArea == null || submitButton){
            initRightForwardArea(engineRadius, altitude);
        }
        
        return rightForwardArea;
    }
        
    /**
     * Get the left engine's future forward area
     * 
     * @param engineRadius the radius of the area around the engine
     * @param altitude the altitude of the aircraft
     * @param submitButton whether the method was accessed via submit button
     * @return leftForwardArea
     */
    public Spatial getLeftForwardArea(float engineRadius, int altitude, boolean submitButton){
        if (leftForwardArea == null || submitButton){
            initLeftForwardArea(engineRadius, altitude);
        }
        return leftForwardArea;
    }
        
    
    /**
     * Used by the simulation (campaign mode) for getting the right forward area
     * 
     * @return rightForwardArea
     */
    
    public Spatial getRightForwardArea(){
        return rightForwardArea;
    }
    
    /**
     * Used by the simulation (campaign mode) for getting the left forward area
     * 
     * @return leftForwardArea
     */
    public Spatial getLeftForwardArea(){
        return leftForwardArea;
    }

    /**
     * Initiate the aircraft using the 3D model
     */
    public void initAircraft(){
         // Load a model from test_data (OgreXML + material + texture)
        aircraft = assetManager.loadModel("Models/3d-model.j3o");
        aircraft.scale(0.3f, 0.3f, 0.3f); 
    }
    
    /**
     * Initiate the right engine area
     * @param engineRadius the radius of the area around the engine
     * @param receivingLittle the state of the engine
     * @param altitude the altitude of the aircraft
     */
    public void initRightEngineArea(float engineRadius, boolean receivingLittle, int altitude){
        Dome rightEngine;
        
        // choose geometric shape based on the ennie state
         if(receivingLittle){
            rightEngine = new Dome(new Vector3f(-49f, 51f,-15.5f-altitude), 100, 30, engineRadius, false);
        } else{
            rightEngine = new Dome(new Vector3f(-49f, 51f,-15.5f-altitude), 2, 30, engineRadius, false);
        }
        rightEngineArea = new Geometry("Right Engine", rightEngine);
        
        // provide colour to the material
        Material rightEngineAreaMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        rightEngineAreaMat.setColor("Color", new ColorRGBA(0,0,255,0.5f));
        
        // make the hemisphere transparent
        rightEngineAreaMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        rightEngineArea.setQueueBucket(Bucket.Transparent);
        rightEngineArea.setMaterial(rightEngineAreaMat);

        // rotate the area around the engine
        Quaternion rotation = new Quaternion();
        rotation.fromAngleAxis((float) (FastMath.PI/2), new Vector3f(1,0,0) );        
        rightEngineArea.rotate(rotation);
        
    }
    
    /**
     * Initiate the left engine area
     * @param engineRadius the radius of the area around the engine
     * @param receivingLittle the state of the engine
     * @param altitude the altitude of the aircraft
     */
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
        
        // make the hemisphere transparent
        area_mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        leftEngineArea.setQueueBucket(Bucket.Transparent);
        leftEngineArea.setMaterial(area_mat);
        
        // rotate the area around the engine
        Quaternion rotation = new Quaternion();
        rotation.fromAngleAxis((float) (FastMath.PI/2), new Vector3f(1,0,0) );
        leftEngineArea.rotate(rotation);
    }
    
    /**
     * Initiate the future forward area of the right engine
     * @param engineRadius the radius of the area around the engine
     * @param altitude the altitude of the aircraft
     */
    public void initRightForwardArea(float engineRadius, int altitude){
        Cylinder cylinder = new Cylinder(100, 100, engineRadius,5000);
        rightForwardArea = new Geometry("Forward Right Engine Area", cylinder);
        
        rightForwardArea.setLocalTranslation(new Vector3f(-49f,15f+altitude,2550));
        
        // make the forward area transparent
        Material area_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        area_mat.setColor("Color", new ColorRGBA(255,0,0,0.4f));
        area_mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        rightForwardArea.setMaterial(area_mat);      
    }
    
    /**
     * Initiate the future forward area of the left engine
     * @param engineRadius the radius of the area around the engine
     * @param altitude the altitude of the aircraft
     */
     public void initLeftForwardArea(float engineRadius, int altitude){
        Cylinder cylinder = new Cylinder(100, 100, engineRadius,5000);
        leftForwardArea = new Geometry("Forward Left Engine Area", cylinder);
        
        leftForwardArea.setLocalTranslation(new Vector3f(49f,15f+altitude,2550));
        
        // make the forward area transparent
        Material area_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        area_mat.setColor("Color", new ColorRGBA(255,0,0,0.4f));
        area_mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        leftForwardArea.setMaterial(area_mat);      
    }
     /**
      * Initiate the drone based on the 3D model
      */
     public void initDrone(){
        // load the 3D model
        drone = assetManager.loadModel("Models/AR_Drone.j3o");
        drone.scale(2f, 2f, 2f);
        
        Material area_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

        drone.move(new Vector3f(49f,500f,1219.3f));
     }
     
     /**
      * Get the drone spatial
      * @return this.drone
      */
     public Spatial getDrone(){
         if (drone == null){
             initDrone();
         }
         
        return this.drone;
     }
    
     /**
      * Initiate the terrain of the visualisation
      */
    public void initTerrain(){
    
    /** 1. Create terrain material and load four textures into it. */
        Material mat_terrain = new Material(assetManager,
            "Common/MatDefs/Misc/Unshaded.j3md");

        // the static Google Maps texture
        Texture airport = assetManager.loadTexture("Textures/staticmap.png");
        
        mat_terrain.setTexture("ColorMap", airport);

        int patchSize = 64;
        terrain = new TerrainQuad("my terrain", patchSize, 513, null);
        
        // give the terrain its material, position & scale it, and attach it.
        terrain.setMaterial(mat_terrain);
        terrain.setLocalTranslation(-50, 0, 2400);
        
        // rotate the terrain
        Quaternion rotation = new Quaternion();
        rotation.fromAngleAxis( ((FastMath.PI/4)) , new Vector3f(0,1,0) );
        
        // apply the rotation
        terrain.setLocalRotation( rotation );
        terrain.setLocalScale(7f, 7f, 7f);
        

        /** 5. The LOD (level of detail) depends on were the camera is: */
        TerrainLodControl control = new TerrainLodControl(terrain, terrainLodCamera);
        terrain.addControl(control);
    }
    
    /**
     * Initiate the light source for the visualisation
     */
    public void initSun(){
        sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
    }
    
    /**
     * Initiate the cockpit panel which is used in the campaign mode simulation
     */
    public void initCockpit(){    
        cockpit = new Picture("Cockpit");
        cockpit.setImage(assetManager, "Textures/757VFR.png", true);
        cockpit.setWidth(settings.getWidth());
        cockpit.setHeight(settings.getHeight());
        cockpit.setPosition(0, 80);

    }
    
     
   
    
}
