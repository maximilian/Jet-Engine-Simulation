package Camera;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/** Represents the different aircraft camera views
 *
 * @author Maximilian Morell
 */
public class AircraftCamera {

    private Camera flyCam;
    
    
    /**
     * Create an AircraftCamera object based on jMonkeyEngine's flyCam
     * 
     * @param flyCam jME Camera instance
     */
    
    public AircraftCamera(Camera flyCam){
        this.flyCam = flyCam;
        
    }
    
    /**
     * Set the view to the front of the aircraft
     * 
     * @param altitude altitude of the aircraft, feet
     */
    
     public void frontView(int altitude) {
         Quaternion rotation = new Quaternion();
        // rotate 170 degrees around y axis
        rotation.fromAngleAxis( FastMath.PI , new Vector3f(0,1,0) );
        flyCam.setRotation(rotation);
        flyCam.setLocation( new Vector3f( 0.08276296f, 15.758865f+altitude, 337.568f ) );
    }
    
     /**
      * Set the view to be above the aircraft
      * 
      * @param altitude altitude of the aircraft, feet
      */
    public void aboveView(int altitude) {
        Quaternion rotation = new Quaternion();
        // rotate 90 degrees around x axis
        rotation.fromAngleAxis( FastMath.PI/2 , new Vector3f(1,0,0) );
        flyCam.setRotation(rotation);
        flyCam.setLocation( new Vector3f( -0.42916974f, 356.08267f+altitude, 79.266045f ) ); 
    }
    
    /**
     * Set the view to be of the right engine of the aircraft
     * 
     * @param altitude altitude of the aircraft, feet
     */
    public void rightEngineView(int altitude) {
        Quaternion rotation = new Quaternion();
        // rotate 5/4*pi around y axis
        rotation.fromAngleAxis((float) (FastMath.PI * 0.75), new Vector3f(0,1,0) );
        flyCam.setRotation(rotation);
        flyCam.setLocation( new Vector3f(-233.71786f, 29.250921f+altitude, 249.49205f));
     }
    
    /**
     * Set the view to be of the left engine of the aircraft
     * 
     * @param altitude altitude of the aircraft, feet
     */
    public void leftEngineView(int altitude) {  
        Quaternion rotation = new Quaternion();
        // rotate 5/4*pi around y axis
        rotation.fromAngleAxis((float) (FastMath.PI * 1.25), new Vector3f(0,1,0) );
        flyCam.setRotation(rotation);
        flyCam.setLocation( new Vector3f(233.71786f, 29.250921f+altitude, 249.49205f));
     }
    
    /**
     * Used exclusively for the take-off visualisation feature.
     * 
     * Moves camera based on displacement of the aircraft.
     * 
     * @param zDisplacement displacement in the z-axis
     * @param xDisplacement displacement in the x-axis
     */
     public void leftEngineView(float zDisplacement, float xDisplacement) { 
        Quaternion rotation = new Quaternion();
        // rotate 5/4*pi around y axis
        rotation.fromAngleAxis((float) (FastMath.PI * 1.25), new Vector3f(0,1,0) );
        flyCam.setRotation(rotation);
        flyCam.setLocation( new Vector3f(233.71786f + xDisplacement, 29.250921f, 249.49205f + zDisplacement));
     }
}
