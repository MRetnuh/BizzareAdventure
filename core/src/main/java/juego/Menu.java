package juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import audios.Musica;

import com.badlogic.gdx.audio.Music;

import io.github.some.Principal;

public class Menu implements Screen {
    private Musica musicaMenu = new Musica("primeraisla");
    private final Principal GAME;
    private Stage stage;
    private Skin skin;
    
    // Fondo
    private Texture fondoTexture;
    private Image fondoImage;

    public Menu(Principal game) {
        this.GAME = game;
    }

    @Override
    public void show() {
    	musicaMenu.show();
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        // Cargar fondo
        fondoTexture = new Texture(Gdx.files.internal("imagenes/fondos/portada.png"));
        fondoImage = new Image(fondoTexture);
        fondoImage.setFillParent(true); // Ocupa toda la pantalla
        stage.addActor(fondoImage); // Agregado primero (fondo)

        // UI
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Label title = new Label("Mi Juego", skin);
        title.setFontScale(2);
        title.setAlignment(Align.center);

        TextButton jugarBtn = new TextButton("Jugar", skin);
        TextButton salirBtn = new TextButton("Salir", skin);

        jugarBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
            	musicaMenu.detenerMusica();
                GAME.setScreen(new Partida(GAME));
            }
        });

        salirBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(title).padBottom(30).row();
        table.add(jugarBtn).size(200, 50).padBottom(20).row();
        table.add(salirBtn).size(200, 50);

        stage.addActor(table); // Agregado después (encima del fondo)
    }

    @Override
    public void render(float delta) {
       if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.UP)) {
             musicaMenu.subirVolumen();
        }
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.DOWN)) {
            musicaMenu.bajarVolumen();
        }
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.M)) {
            musicaMenu.silenciar();
        } 

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
       // musicaFondo.dispose();
        fondoTexture.dispose();
    }
}
