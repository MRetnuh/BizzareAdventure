package io.github.some.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.some.Principal; // Tu clase principal en core

public class DesktopLauncher {
    public static void main(String[] args) {
        // Ejecutar en pantalla completa
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        config.useVsync(true);
        config.setForegroundFPS(60);
        new Lwjgl3Application(new Principal(), config);

        config.setTitle("Mi Juego");

        new Lwjgl3Application(new Principal(), config);
    }
}
