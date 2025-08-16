package io.github.some;

import com.badlogic.gdx.Game;

import audios.Musica;
import juego.Menu;

public class Principal extends Game {
    private Musica musicaMenu;

    @Override
    public void create() {
    	this.musicaMenu = new Musica("primeraisla");
    	this.musicaMenu.show();
        setScreen(new Menu(this));
    }

    public Musica getMusica() {
        return this.musicaMenu;
    }
}
