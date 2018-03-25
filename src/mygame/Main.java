package mygame;

import com.jme3.system.AppSettings;

/** Main class for the system
 *
 * @author Maximilian Morell
 */
public class Main {
    public static void main(String[] args){
        Project app = new Project();
        app.setShowSettings(false);
        
        AppSettings settings = new AppSettings(true);
        
        // Window dimensions
        settings.setRenderer(AppSettings.LWJGL_OPENGL2);
        settings.setWidth(848);
        settings.setHeight(480);
        
        app.setSettings(settings);
        
        // start the visualisation
        app.start();
        
    }
}
