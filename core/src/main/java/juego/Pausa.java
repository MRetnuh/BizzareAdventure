package juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import audios.Musica;
import estilos.EstiloTexto;
import io.github.some.Principal;

public class Pausa implements Screen {
    private final Principal JUEGO;
    private final Partida partida;
    private Stage stage;
    private Skin skin;
    private Musica musicaPausa;

    public Pausa(Principal juego, Partida partida) {
        this.JUEGO = juego;
        this.partida = partida;
        this.musicaPausa = juego.getMusica();
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Label titulo = new Label("PAUSA", EstiloTexto.ponerEstiloLabel(60, Color.WHITE));

        TextButton reanudarBtn = new TextButton("Reanudar", EstiloTexto.ponerEstiloBoton(skin, 48, Color.YELLOW));
        TextButton configuracionBtn = new TextButton("Configuracion", EstiloTexto.ponerEstiloBoton(skin, 48, Color.YELLOW));
        TextButton salirBtn = new TextButton("Salir al Menu", EstiloTexto.ponerEstiloBoton(skin, 48, Color.YELLOW));

        reanudarBtn.addListener(event -> {
            if (Gdx.input.isTouched()) {
                JUEGO.setScreen(partida);
            }
            return true;
        });

        configuracionBtn.addListener(event -> {
            if (Gdx.input.isTouched()) {
                JUEGO.setScreen(new Configuracion(JUEGO) {
                    @Override
                    public void hide() {
                        super.hide();
                        JUEGO.setScreen(new Pausa(JUEGO, partida)); // Volver a pausa.
                    }
                });
            }
            return true;
        });

        salirBtn.addListener(event -> {
            if (Gdx.input.isTouched()) {
                musicaPausa.detenerMusica();
                JUEGO.setScreen(new Menu(JUEGO));
            }
            return true;
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(titulo).padBottom(30).row();
        table.add(reanudarBtn).size(300, 60).padBottom(20).row();
        table.add(configuracionBtn).size(300, 60).padBottom(20).row();
        table.add(salirBtn).size(300, 60);

        stage.addActor(table);
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
    @Override public void dispose() { stage.dispose(); skin.dispose(); }
}
