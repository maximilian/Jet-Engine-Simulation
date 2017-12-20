/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Dome;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;

/**
 * Class used to manage al the different resources/models.
 * 
 * @author max
 */
public class ResourceLoader {
    private TerrainQuad terrain;
    private Spatial aircraft;
    private Spatial leftEngineArea;
    private Spatial rightEngineArea;
    
    private AssetManager assetManager;
    private Camera terrainLodCamera;
    
    public ResourceLoader(AssetManager assetManager, Camera terrainLodCamera) {
        this.assetManager = assetManager;
        this.terrainLodCamera = terrainLodCamera;
    }
    
    public Spatial getAircraft(){
        if(aircraft == null) {
            initAircraft();
        }
        
        return aircraft;
    
    }
    
    public TerrainQuad getTerrain(){
        if(terrain == null){
            initTerrain();
        }
        return terrain;
    
    }
    
    public Spatial getLeftEngineArea(float engineRadius, boolean receivingLittle, boolean submitButton){
        if (leftEngineArea == null || submitButton){
            initLeftEngineArea(engineRadius, receivingLittle);       
        }
        
        return leftEngineArea;
    
    }
    
    public Spatial getRightEngineArea(float engineRadius, boolean receivingLittle, boolean submitButton){
        if (rightEngineArea == null || submitButton){
            initRightEngineArea(engineRadius, receivingLittle);    
        }
        
        return rightEngineArea;
    
    }
    
    public void initAircraft(){
         // Load a model from test_data (OgreXML + material + texture)
        aircraft = assetManager.loadModel("Models/3d-model.j3o");

        aircraft.scale(0.3f, 0.3f, 0.3f); 
    }
    
    public void initRightEngineArea(float engineRadius, boolean receivingLittle){
        Dome rightEngine = new Dome(new Vector3f(-49f, 50f,-16.5f), 100, 30, engineRadius, false);
        
        rightEngineArea = new Geometry("Right Engine", rightEngine);
        
        Material rightEngineAreaMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        rightEngineAreaMat.setColor("Color", new ColorRGBA(0,0,255,0.5f));
        
        // transparent hemisphere
        rightEngineAreaMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        rightEngineArea.setMaterial(rightEngineAreaMat);
        rightEngineArea.rotate(1.6f, 0, 0);
    }
    
    public void initLeftEngineArea(float engineRadius, boolean receivingLittle){
        Dome leftEngine;
        if(receivingLittle){
            leftEngine = new Dome(new Vector3f(49f, 50f,-16.5f), 100, 30, engineRadius, false);
        } else{
            leftEngine = new Dome(new Vector3f(49f, 50f,-16.5f), 2, 30, engineRadius, false);
        }
        leftEngineArea = new Geometry("Left Engine", leftEngine);
        
        Material area_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        area_mat.setColor("Color", new ColorRGBA(255,0,0,0.5f));
        
        //transparent hemisphere
        area_mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        leftEngineArea.setMaterial(area_mat);
        leftEngineArea.rotate(1.6f, 0, 0);
    }
    
    public void initTerrain(){
    
    /** 1. Create terrain material and load four textures into it. */
        Material mat_terrain = new Material(assetManager,
            "Common/MatDefs/Terrain/Terrain.j3md");

        /** 1.1) Add ALPHA map (for red-blue-green coded splat textures) */
        mat_terrain.setTexture("Alpha", assetManager.loadTexture(
                "Textures/Terrain/splat/alphamap.png"));

        /** 1.2) Add GRASS texture into the red layer (Tex1). */
        Texture grass = assetManager.loadTexture(
                "Textures/Terrain/splat/grass.jpg");
        grass.setWrap(Texture.WrapMode.Repeat);
        mat_terrain.setTexture("Tex1", grass);
        mat_terrain.setFloat("Tex1Scale", 64f);

        /** 1.3) Add DIRT texture into the green layer (Tex2) */
        Texture dirt = assetManager.loadTexture(
                "Textures/Terrain/splat/dirt.jpg");
        dirt.setWrap(Texture.WrapMode.Repeat);
        mat_terrain.setTexture("Tex2", dirt);
        mat_terrain.setFloat("Tex2Scale", 32f);

        /** 1.4) Add ROAD texture into the blue layer (Tex3) */
        Texture rock = assetManager.loadTexture(
                "Textures/Terrain/splat/road.jpg");
        rock.setWrap(Texture.WrapMode.Repeat);
        mat_terrain.setTexture("Tex3", rock);
        mat_terrain.setFloat("Tex3Scale", 128f);
          
            /** 2. Create the height map */
        /*     
       AbstractHeightMap heightmap = null;
        Texture heightMapImage = assetManager.loadTexture(
                "Textures/Terrain/splat/mountains512.png");
        heightmap = new ImageBasedHeightMap(heightMapImage.getImage());
        heightmap.load();
        */

        /** 3. We have prepared material and heightmap.
         * Now we create the actual terrain:
         * 3.1) Create a TerrainQuad and name it "my terrain".
         * 3.2) A good value for terrain tiles is 64x64 -- so we supply 64+1=65.
         * 3.3) We prepared a heightmap of size 512x512 -- so we supply 512+1=513.
         * 3.4) As LOD step scale we supply Vector3f(1,1,1).
         * 3.5) We supply the prepared heightmap itself.
         */
        int patchSize = 65;
        terrain = new TerrainQuad("my terrain", patchSize, 513, null);

        /** 4. We give the terrain its material, position & scale it, and attach it. */
        terrain.setMaterial(mat_terrain);
        terrain.setLocalTranslation(0, -100, 0);
        terrain.setLocalScale(2f, 1f, 2f);
        

        /** 5. The LOD (level of detail) depends on were the camera is: */
        TerrainLodControl control = new TerrainLodControl(terrain, terrainLodCamera);
        terrain.addControl(control);
        
       
    
    }
    
    
}
