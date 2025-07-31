package juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
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
import estilos.EstiloTexto;


import io.github.some.Principal;

public class Menu implements Screen {
	private Musica musicaMenu;
	private final Principal JUEGO;
    private Stage stage;
    private Skin skin;
    private Texture fondoTextura;
    private Image fondoImagen;

    public Menu(Principal juego) {
        this.JUEGO = juego;
        this.musicaMenu = juego.getMusica();
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

    
        fondoTextura = new Texture(Gdx.files.internal("imagenes/fondos/portada.png"));
        fondoImagen = new Image(fondoTextura);
        fondoImagen.setFillParent(true);
        stage.addActor(fondoImagen);

        skin = new Skin(Gdx.files.internal("uiskin.json"));
  

        
        Label titulo = new Label("Akame Bizzare Adventure", EstiloTexto.ponerEstiloLabel(60, Color.PURPLE));
        titulo.setAlignment(Align.center);

        TextButton jugarBtn = new TextButton("Jugar", EstiloTexto.ponerEstiloBoton(skin, 48, Color.PURPLE));
        TextButton opcionesBtn = new TextButton("Opciones", EstiloTexto.ponerEstiloBoton(skin, 48, Color.PURPLE));
        TextButton salirBtn = new TextButton("Salir", EstiloTexto.ponerEstiloBoton(skin, 48, Color.PURPLE));

        jugarBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                musicaMenu.detenerMusica();
            	musicaMenu.cambiarMusica("Balatro");
                JUEGO.setScreen(new Partida(JUEGO));
            }
        });
        

        opcionesBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                JUEGO.setScreen(new Opciones(JUEGO));
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
        table.defaults().center();
        table.add(titulo).padBottom(30).row();
        table.add(jugarBtn).size(200, 50).padBottom(20).row();
        table.add(opcionesBtn).size(200, 50).padBottom(20).row();
        table.add(salirBtn).size(200, 50);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {          
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
        fondoTextura.dispose();
    }
}
