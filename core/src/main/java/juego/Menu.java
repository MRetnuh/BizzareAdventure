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
import com.badlogic.gdx.audio.Music;

import io.github.some.Principal;

public class Menu implements Screen {
    private final Principal game;
    private Stage stage;
    private Skin skin;
    private Music musicaFondo;
    private float volumen = 0.5f;

    // Fondo
    private Texture fondoTexture;
    private Image fondoImage;

    public Menu(Principal game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        // Música de fondo
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("musica/primeraisla.mp3"));
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(volumen);
        musicaFondo.play();

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
                musicaFondo.stop(); // Detener música al entrar al juego
                game.setScreen(new Partida(game, musicaFondo, volumen));
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
        // Controles de volumen
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.UP)) {
            volumen = Math.min(1f, volumen + 0.1f);
            musicaFondo.setVolume(volumen);
            System.out.println("subir");
        }
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.DOWN)) {
            volumen = Math.max(0f, volumen - 0.1f);
            musicaFondo.setVolume(volumen);
            System.out.println("bajar");
        }
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.M)) {
            musicaFondo.setVolume(0f);
            System.out.println("Volumen en 0 (mute)");
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
        musicaFondo.dispose();
        fondoTexture.dispose();
    }
}
