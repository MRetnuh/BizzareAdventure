package juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import audios.Musica;
import estilos.EstiloTexto;
import io.github.some.Principal;

public class Configuracion implements Screen {
    private final Principal JUEGO;
    private Stage stage;
    private Skin skin;
    private Musica musicaConfig;
    
    public Configuracion(Principal juego) {
        this.JUEGO = juego;
        this.musicaConfig = juego.getMusica(); 
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Label titulo = new Label("Configuracion", EstiloTexto.ponerEstiloLabel(36, Color.WHITE));
        titulo.setAlignment(Align.center);

        Label volumenLabel = new Label("Volumen: " + (int)(musicaConfig.getVolumen() * 100) + "%", EstiloTexto.ponerEstiloLabel(36, Color.WHITE));

        Slider volumenSlider = new Slider(0f, 1f, 0.01f, false, skin);
        volumenSlider.setValue(musicaConfig.getVolumen());
        volumenSlider.addListener(event -> {
            float nuevoVolumen = volumenSlider.getValue();
            musicaConfig.setVolumen(nuevoVolumen);
            volumenLabel.setText("Volumen: " + (int)(nuevoVolumen * 100) + "%");
            return false;
        });

        TextButton volverBtn = new TextButton("Volver", skin);
        volverBtn.addListener(event -> {
            if (Gdx.input.isTouched()) {
                JUEGO.setScreen(new Menu(JUEGO));
            }
            return true;
        });

        Table tabla = new Table();
        tabla.setFillParent(true);
        tabla.center();

        tabla.add(titulo).padBottom(30).row();
        tabla.add(volumenLabel).padBottom(10).row();
        tabla.add(volumenSlider).width(300).padBottom(30).row();
        tabla.add(volverBtn).size(200, 50);

        stage.addActor(tabla);
    }

    @Override
    public void render(float delta) {
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
    }
}
