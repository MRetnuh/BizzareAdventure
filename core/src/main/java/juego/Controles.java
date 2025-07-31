package juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import audios.Musica;
import estilos.EstiloTexto;
import io.github.some.Principal;

public class Controles implements Screen {
    private final Principal JUEGO;
    private Stage stage;
    private Skin skin;
    private Musica musicaControles;
    
    public Controles(Principal juego) {
        this.JUEGO = juego;
        this.musicaControles = juego.getMusica(); 
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Label titulo = new Label("CONTROLES", EstiloTexto.ponerEstiloLabel(70, Color.WHITE));
        titulo.setAlignment(Align.center);

        Label movimientoTitulo = new Label("Movimiento:", EstiloTexto.ponerEstiloLabel(60, Color.ORANGE));
        Label combateTitulo = new Label("Combate:", EstiloTexto.ponerEstiloLabel(60, Color.ORANGE));
        Label opcionesTitulo = new Label("Opciones:", EstiloTexto.ponerEstiloLabel(60, Color.ORANGE));

        Label w = new Label("W: Saltar", EstiloTexto.ponerEstiloLabel(48, Color.LIGHT_GRAY));
        Label a = new Label("A: Moverse a la izquierda", EstiloTexto.ponerEstiloLabel(48, Color.LIGHT_GRAY));
        Label d = new Label("D: Moverse a la derecha", EstiloTexto.ponerEstiloLabel(48, Color.LIGHT_GRAY));
        Label s = new Label("S: Agacharse", EstiloTexto.ponerEstiloLabel(48, Color.LIGHT_GRAY));

        Label m = new Label("M: Atacar", EstiloTexto.ponerEstiloLabel(48, Color.LIGHT_GRAY));

        Label p = new Label("P: Pausar / Opciones", EstiloTexto.ponerEstiloLabel(48, Color.LIGHT_GRAY));

        TextButton volverBtn = new TextButton("Volver", EstiloTexto.ponerEstiloBoton(skin, 48, Color.RED));
        volverBtn.addListener(event -> {
            if (Gdx.input.isTouched()) {
                JUEGO.setScreen(new Opciones(JUEGO));
            }
            return true;
        });

        Table tabla = new Table();
        tabla.setFillParent(true);
        tabla.top().padTop(30);

        tabla.add(titulo).colspan(1).padBottom(40).row();

        tabla.add(movimientoTitulo).left().padBottom(10).row();
        tabla.add(w).left().padBottom(5).row();
        tabla.add(a).left().padBottom(5).row();
        tabla.add(d).left().padBottom(5).row();
        tabla.add(s).left().padBottom(25).row();

        tabla.add(combateTitulo).left().padBottom(10).row();
        tabla.add(m).left().padBottom(25).row();

        tabla.add(opcionesTitulo).left().padBottom(10).row();
        tabla.add(p).left().padBottom(40).row();

        tabla.add(volverBtn).center().size(200, 60);

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