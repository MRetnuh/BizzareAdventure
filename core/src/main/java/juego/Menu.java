package juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import io.github.some.Principal;
import com.badlogic.gdx.audio.Music;



public class Menu implements Screen {
    private final Principal game;
    private Stage stage;
    private Skin skin;
    private Music musicaFondo;
    private float volumen = 0.5f; // Volumen inicial (entre 0 y 1)

    public Menu(Principal game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
     // Cargar y reproducir música de fondo
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("musica/primeraisla.mp3"));
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(volumen);
        musicaFondo.play();

        skin = new Skin(Gdx.files.internal("uiskin.json")); // Usa una skin básica de LibGDX

        Label title = new Label("Mi Juego", skin);
        title.setFontScale(2);
        title.setAlignment(Align.center);

        TextButton jugarBtn = new TextButton("Jugar", skin);
        TextButton salirBtn = new TextButton("Salir", skin);

        jugarBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                musicaFondo.stop(); // O no, si querés que siga
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

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
    	// Controles de volumen con flechas ↑ ↓
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
            musicaFondo.setVolume(0f); // Silenciar directamente
            System.out.println("Volumen en 0 (mute)");
        }
        Gdx.gl.glClearColor(0, 0, 0, 1); // Fondo negro
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    // Los otros métodos pueden quedar vacíos por ahora
    public void resize(int width, int height) {}
    public void pause() {}
    public void resume() {}
    public void hide() {}
    public void dispose() {
        stage.dispose();
        skin.dispose();
        musicaFondo.dispose();

    }
}