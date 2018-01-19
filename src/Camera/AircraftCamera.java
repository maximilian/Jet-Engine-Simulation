/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Camera;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 *
 * @author max
 */
public class AircraftCamera {

    private Camera flyCam;
    
    
    public AircraftCamera(Camera flyCam){
        this.flyCam = flyCam;
        
    }
    
     public void frontView(int altitude) {
         Quaternion rotation = new Quaternion();
        // rotate 170 degrees around y axis
        rotation.fromAngleAxis( FastMath.PI , new Vector3f(0,1,0) );
        flyCam.setRotation(rotation);
        flyCam.setLocation( new Vector3f( 0.08276296f, 15.758865f+altitude, 337.568f ) );

     }
    
    public void aboveView(int altitude) {
        Quaternion rotation = new Quaternion();
        // rotate 90 degrees around x axis
        rotation.fromAngleAxis( FastMath.PI/2 , new Vector3f(1,0,0) );
        flyCam.setRotation(rotation);
        flyCam.setLocation( new Vector3f( -0.42916974f, 356.08267f+altitude, 79.266045f ) ); 
     }
}
