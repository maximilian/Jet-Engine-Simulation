/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.system.AppSettings;

/**
 *
 * @author max
 */
public class Main {
    public static void main(String[] args){
        Project app = new Project();
        app.setShowSettings(false);
        
        AppSettings settings = new AppSettings(true);

        settings.setRenderer(AppSettings.LWJGL_OPENGL2);
        settings.setWidth(848);
        settings.setHeight(480);
        
        app.setSettings(settings);

        app.start(); // start the game
        
    }
}
