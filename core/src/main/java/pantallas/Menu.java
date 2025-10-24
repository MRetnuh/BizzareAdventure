package pantallas;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
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
import juego.Partida;

public class Menu implements Screen {
	private Musica musicaMenu;
	private final Game JUEGO;
    private Stage stage;
    private Skin skin;
    private Texture fondoTextura;
    private Image fondoImagen;

    public Menu(Game juego) {
        this.stage = new Stage();
        this.JUEGO = juego;
        this.musicaMenu = new Musica("Menu");
        this.musicaMenu.show(this.stage);
    }

    @Override
    public void show() {

        Gdx.input.setInputProcessor(this.stage);
    
        this.fondoTextura = new Texture(Gdx.files.internal("imagenes/fondos/portada.png"));
        this.fondoImagen = new Image(this.fondoTextura);
        this.fondoImagen.setFillParent(true);
        this.stage.addActor(fondoImagen);

        this.skin = new Skin(Gdx.files.internal("uiskin.json")); //-> Hace falta? //kevin (el follador de hornet) Investiga para que mierda sirve el skin
        
        Label titulo = new Label("Akame Bizzare Adventure", EstiloTexto.ponerEstiloLabel(60, Color.PURPLE));
        titulo.setAlignment(Align.center);
   
        TextButton jugarBtn = new TextButton("Jugar", EstiloTexto.ponerEstiloBoton(skin, 48, Color.PURPLE));
        TextButton opcionesBtn = new TextButton("Opciones", EstiloTexto.ponerEstiloBoton(skin, 48, Color.PURPLE));
        TextButton salirBtn = new TextButton("Salir", EstiloTexto.ponerEstiloBoton(skin, 48, Color.PURPLE));

        jugarBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
            	musicaMenu.cambiarMusica("PrimerNivel");
                JUEGO.setScreen(new Partida(JUEGO, musicaMenu));
            }
        });
        

        opcionesBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                JUEGO.setScreen(new Opciones(JUEGO, Menu.this, musicaMenu));
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

        this.stage.addActor(table);
    }

    @Override
    public void render(float delta) {          
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
