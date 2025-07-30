package io.github.some;

import com.badlogic.gdx.Game;

import audios.Musica;
import juego.Menu;

public class Principal extends Game {
    private Musica musicaMenu;

    @Override
    public void create() {
        musicaMenu = new Musica("primeraisla");
        setScreen(new Menu(this));
    }

    public Musica getMusica() {
        return musicaMenu;
    }
}
