
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapText;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Line;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import de.lessvoid.nifty.Nifty;
import mygame.MyControlScreen;




/** Sample 1 - how to get started with the most simple JME 3 application.
 * Display a blue 3D cube and view from all sides by
 * moving the mouse and pressing the WASD keys. */
public class Project extends SimpleApplication {
    private TerrainQuad terrain;
        
    boolean receivingLittle = false;
    boolean receivingMuch = false;
    boolean receivingCorrect = false;
    
    
    public static void main(String[] args){
        Project app = new Project();
        app.setShowSettings(false);

        app.start(); // start the game
    }

    @Override
    public void simpleInitApp() {
        NiftyJmeDisplay niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        Nifty nifty = niftyDisplay.getNifty();
        /** Read your XML and initialize your custom ScreenController */
        
        MyControlScreen startScreen = new MyControlScreen();
        stateManager.attach(startScreen);
        
        nifty.fromXml("Interface/screen.xml", "start", startScreen);

        // attach the Nifty display to the gui view port as a processor
        guiViewPort.addProcessor(niftyDisplay);
        flyCam.setMoveSpeed(250);
        flyCam.setDragToRotate(true);

        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));

        // need this in any game involving physics
        BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
    
        // wall and teapot for testing purposes
        /*Spatial teapot = assetManager.loadModel("Models/Teapot/Teapot.obj");
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
        */
        
         // Load a model from test_data (OgreXML + material + texture)
        Spatial aircraft = assetManager.loadModel("Models/3d-model.j3o");

        System.out.println(aircraft.getWorldBound());
        
        //aircraft.setMaterial(mat_aircraft);
        //aircraft.scale(0.5f, 0.5f, 0.5f);
        aircraft.rotate(0f, 0f, 0.0f);
        aircraft.setLocalTranslation(0f, 0f, 0f);
        aircraft.scale(0.3f, 0.3f, 0.3f);
     
        rootNode.attachChild(aircraft);
        
        axisLines();
   
        float engineRadius = calculateArea(2000, 160, 100);
        
        Dome leftEngine;
        if(receivingLittle){
            leftEngine = new Dome(new Vector3f(49f, 50f,-16.5f), 100, 30, engineRadius, false);
        } else{
            leftEngine = new Dome(new Vector3f(49f, 50f,-16.5f), 2, 30, engineRadius, false);
        }
        Geometry rightEngineArea = new Geometry("Right Engine", leftEngine);
        
        Material area_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        area_mat.setColor("Color", new ColorRGBA(255,0,0,0.5f));
        
        //transparent hemisphere
        area_mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        
        rightEngineArea.setMaterial(area_mat);
        rightEngineArea.rotate(1.6f, 0, 0);
        
        Dome rightEngine = new Dome(new Vector3f(-49f, 50f,-16.5f), 100, 30, engineRadius, false);
        Geometry leftEngineArea = new Geometry("Left Engine", rightEngine);
        
        Material leftAreaMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        leftAreaMat.setColor("Color", new ColorRGBA(0,0,255,0.5f));
        
        // transparent hemisphere
        leftAreaMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        
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
}