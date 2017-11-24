package jme3test.helloworld;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapText;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;




/** Sample 1 - how to get started with the most simple JME 3 application.
 * Display a blue 3D cube and view from all sides by
 * moving the mouse and pressing the WASD keys. */
public class Project extends SimpleApplication {
    private TerrainQuad terrain;
    
    public static void main(String[] args){
        Project app = new Project();
        app.setShowSettings(false);

        app.start(); // start the game
    }

    @Override
    public void simpleInitApp() {
        
        flyCam.setMoveSpeed(250);
        
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));

        
        // need this in any game involving physics
        BulletAppState bulletAppState = new BulletAppState();
    stateManager.attach(bulletAppState);
    
    
        Spatial teapot = assetManager.loadModel("Models/Teapot/Teapot.obj");
        Material mat_default = new Material(
            assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        teapot.setMaterial(mat_default);

        rootNode.attachChild(teapot);

        // Create a wall with a simple texture from test_data
        Box box = new Box(2.5f,2.5f,1.0f);
        Spatial wall = new Geometry("Box", box );
        
        Material mat_brick = new Material(
            assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_brick.setTexture("ColorMap",
            assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg"));
        wall.setMaterial(mat_brick);
        wall.setLocalTranslation(10f,0f, 0f);
        rootNode.attachChild(wall);

        
         // Load a model from test_data (OgreXML + material + texture)
        Spatial aircraft = assetManager.loadModel("Models/3d-model.j3o");


        //aircraft.setMaterial(mat_aircraft);
        //aircraft.scale(0.5f, 0.5f, 0.5f);
        aircraft.rotate(0f, 0f, 0.0f);
        aircraft.setLocalTranslation(0f, 0f, 0f);
        aircraft.scale(0.3f, 0.3f, 0.3f);
     
        
        
        rootNode.attachChild(aircraft);
        
        
        axisLines();
   
        
        Dome sphere = new Dome(new Vector3f(49f, 50f,-16.5f), 100, 30, 35, false);
        Geometry rightEngineArea = new Geometry("Right Engine", sphere);
        
        Material area_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        area_mat.setColor("Color", ColorRGBA.Red);
        
        rightEngineArea.setMaterial(area_mat);

        rightEngineArea.rotate(1.6f, 0, 0);
        
        
        Dome leftEngine = new Dome(new Vector3f(-49f, 50f,-16.5f), 100, 30, 35, false);
        Geometry leftEngineArea = new Geometry("Left Engine", leftEngine);
        
        Material leftAreaMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        leftAreaMat.setColor("Color", ColorRGBA.Blue);
        
        leftEngineArea.setMaterial(leftAreaMat);

        leftEngineArea.rotate(1.6f, 0, 0);
        
        
        rootNode.attachChild(leftEngineArea);
        rootNode.attachChild(rightEngineArea);
        
        
        
        
        // Display a line of text with a default font
        guiNode.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText helloText = new BitmapText(guiFont, false);
        helloText.setSize(guiFont.getCharSet().getRenderedSize());
        helloText.setText("Hello Max");
        helloText.setLocalTranslation(300, helloText.getLineHeight(), 0);
        guiNode.attachChild(helloText);

        loadTerrain();     
       
        calculateArea(2000, 160, 100);
        
        // You must add a light to make the model visible
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
        rootNode.addLight(sun);
        
        
        /*Node node = (Node) aircraft;
           
        Geometry geo;
        
        geo = (Geometry) node.getChild("3d-model-geom-8");
        
       
        
        geo.rotate(0f, 1f, 0.0f);
        geo.setLocalTranslation(-100f, 0f, 50f);
        geo.scale(0.3f, 0.3f, 0.3f);
  
        */
    }
    
    
    
    /* helper to load terrain */
    public void loadTerrain(){
    
    /** 1. Create terrain material and load four textures into it. */
        Material mat_terrain = new Material(assetManager,
            "Common/MatDefs/Terrain/Terrain.j3md");

        /** 1.1) Add ALPHA map (for red-blue-green coded splat textures) */
        mat_terrain.setTexture("Alpha", assetManager.loadTexture(
                "Textures/Terrain/splat/alphamap.png"));

        /** 1.2) Add GRASS texture into the red layer (Tex1). */
        Texture grass = assetManager.loadTexture(
                "Textures/Terrain/splat/grass.jpg");
        grass.setWrap(WrapMode.Repeat);
        mat_terrain.setTexture("Tex1", grass);
        mat_terrain.setFloat("Tex1Scale", 64f);

        /** 1.3) Add DIRT texture into the green layer (Tex2) */
        Texture dirt = assetManager.loadTexture(
                "Textures/Terrain/splat/dirt.jpg");
        dirt.setWrap(WrapMode.Repeat);
        mat_terrain.setTexture("Tex2", dirt);
        mat_terrain.setFloat("Tex2Scale", 32f);

        /** 1.4) Add ROAD texture into the blue layer (Tex3) */
        Texture rock = assetManager.loadTexture(
                "Textures/Terrain/splat/road.jpg");
        rock.setWrap(WrapMode.Repeat);
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
        rootNode.attachChild(terrain);

        /** 5. The LOD (level of detail) depends on were the camera is: */
        TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
        terrain.addControl(control);
    
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
    
    boolean receivingLittle = false;
    boolean receivingMuch = false;
    boolean receivingCorrect = false;
    public void calculateArea(double altitude, double speed, double engineSetting){
        double engineFlowRate = 332; // kg/s - needs to be corrected
        double engineDiameter = (float) 1.6; // m
        
        double engineArea = Math.PI * (Math.pow(engineDiameter, 2));
        
        double speedMetres = 0.514444444 * speed; // convert from knots to metres
        
        // method here required to calculate corrected density and speed
        
        double airDensity = 1.15;
        
        double engineNeeds = engineFlowRate / airDensity;
        double engineReceives = engineArea * speedMetres;
        
        
        if (engineNeeds > engineReceives){
            receivingLittle = true;
        } else if(engineNeeds < engineReceives){
            receivingMuch = true;
        } else {
            receivingCorrect = true;
        }
        
        double engineAreaRequired = engineNeeds / speedMetres;
        
        double engineDiameterRequired = 2 * Math.sqrt((engineAreaRequired / Math.PI));
        
        
        
        
        System.out.println(engineDiameterRequired);   
    }
}