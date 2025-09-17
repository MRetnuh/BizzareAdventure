package juego;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import juego.Controles;

import audios.Musica;
import estilos.EstiloTexto;
import io.github.some.Principal;

public class Opciones implements Screen {
	private Screen screenAnterior;
    private final Game JUEGO;
    private Stage stage;
    private Skin skin;
    private Musica musicaOpciones;
    
    public Opciones(Game juego, Screen screenAnterior, Musica musica) {
        this.JUEGO = juego;
        this.musicaOpciones = musica;
        this.screenAnterior = screenAnterior;
    }
    
    @Override
    public void show() {
    	this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(this.stage);

        this.skin = new Skin(Gdx.files.internal("uiskin.json"));

        Label titulo = new Label("Opciones", EstiloTexto.ponerEstiloLabel(60, Color.WHITE));
        titulo.setAlignment(Align.center);

        TextButton controlesBtn = new TextButton("Controles", EstiloTexto.ponerEstiloBoton(skin, 48, Color.PURPLE));
        TextButton sonidoBtn = new TextButton("Sonido", EstiloTexto.ponerEstiloBoton(skin, 48, Color.PURPLE));
        TextButton volverBtn = new TextButton("Volver", EstiloTexto.ponerEstiloBoton(skin,48, Color.PURPLE));
        
        sonidoBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                JUEGO.setScreen(new Configuracion(JUEGO, screenAnterior, musicaOpciones));
            }
        });
        
        controlesBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                JUEGO.setScreen(new Controles(JUEGO, screenAnterior, musicaOpciones));
            }
        });
        
        volverBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                JUEGO.setScreen(screenAnterior);
            }
        });
        
        
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.defaults().center();
        table.add(titulo).padBottom(30).row();
        table.add(sonidoBtn).size(215, 50).padBottom(20).row();
        table.add(controlesBtn).size(215, 50).padBottom(20).row();
        table.add(volverBtn).size(215, 50);

        this.stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.stage.act(delta);
        this.stage.draw();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
    	this.stage.dispose();
    	this.skin.dispose();
    }
}


