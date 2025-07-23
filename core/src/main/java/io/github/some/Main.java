package io.github.some;

import com.badlogic.gdx.Game;

import juego.Menu;

public class Main extends Game {
    @Override
    public void create() {
        setScreen(new Menu(this));
    }
}
