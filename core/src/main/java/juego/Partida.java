package juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import audios.Musica;
import estilos.EstiloTexto;
import io.github.some.Principal;
import jugadores.Jugador;
import personajes.Akame;
import personajes.Personaje;


public class Partida implements Screen {
	private Musica musicaPartida = new Musica("Balatro");
	private Stage stage;
	private final Jugador jugador = new Jugador();
    private final Principal JUEGO;
    private TiledMap mapa;
    private Skin skin;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camara;
    private SpriteBatch batch;
    private Personaje personajeElegido;
    int anchoMapa; 
    int alturaMapa;
    
    public Partida(Principal juego) {
        this.JUEGO = juego; 

    }

    @Override
    public void show() {
    	
    	musicaPartida.show();
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

      
        mapa = new TmxMapLoader().load("mapacorregido.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(mapa);
        anchoMapa = mapa.getProperties().get("width", Integer.class) * mapa.getProperties().get("tilewidth", Integer.class);
        alturaMapa = mapa.getProperties().get("height", Integer.class) * mapa.getProperties().get("tileheight", Integer.class);
        
        
        camara = new OrthographicCamera();
        camara.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

       
        batch = new SpriteBatch();
        jugador.generarPersonajeAleatorio();
        personajeElegido = jugador.getPersonajeElegido();
        personajeElegido.setStage(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        
        Label nombrePersonaje = new Label("Nombre: " + personajeElegido.getNombre(), EstiloTexto.ponerEstiloLabel(40, Color.RED));
        nombrePersonaje.setAlignment(Align.left);
        
        Label vidaPersonaje = new Label("Vida: " + personajeElegido.getVida(), EstiloTexto.ponerEstiloLabel(40, Color.RED));
        vidaPersonaje.setAlignment(Align.left);

        
        Table table = new Table();
        table.left().top();
        table.add(nombrePersonaje).size(350, 50).padBottom(10).row();
        table.add(vidaPersonaje).size(350, 50);

        
        Container<Table> contenedor = new Container<>(table);
        contenedor.setSize(400, 130);
        contenedor.setBackground(skin.getDrawable("default-round"));
        contenedor.setPosition(0, Gdx.graphics.getHeight() - contenedor.getHeight()); 

        
        stage.addActor(contenedor);

    }



    @Override
    public void render(float delta) {
    	 if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.UP)) {
    		 musicaPartida.subirVolumen();
        }
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.DOWN)) {
        	musicaPartida.bajarVolumen();
        }
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.M)) {
        	musicaPartida.silenciar();
        } 
      
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

       
        if(personajeElegido.getVida() != 0) {
        personajeElegido.mover(delta);
        personajeElegido.actualizarCamara(camara, anchoMapa, alturaMapa);
        
        
        boolean estaSobreElSuelo = false;
        Rectangle hitboxPersonaje = personajeElegido.getHitbox();
        hitboxPersonaje.setY(hitboxPersonaje.getY() - 1); 

        for (MapObject object : mapa.getLayers().get("colisiones").getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rectMapa = ((RectangleMapObject) object).getRectangle();
                if (hitboxPersonaje.overlaps(rectMapa)) {
                    estaSobreElSuelo = true;
                    break;
                }
            }
        }
        
        personajeElegido.actualizarGravedad(delta, estaSobreElSuelo, alturaMapa);
        float nuevaX = personajeElegido.getNuevaX(delta);
        float nuevaY = personajeElegido.getNuevaY(delta);
        Rectangle hitboxTentativa = new Rectangle(nuevaX, nuevaY, 63, 64); 

        boolean colision = false;
        for (MapObject object : mapa.getLayers().get("colisiones").getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rectMapa = ((RectangleMapObject) object).getRectangle();
                if (hitboxTentativa.overlaps(rectMapa)) {
                    colision = true;
                    break;
                }
            }
        }

     
        if (!colision) {
        	personajeElegido.aplicarMovimiento(nuevaX, nuevaY, delta, anchoMapa, alturaMapa);

        } else {
        	personajeElegido.aplicarMovimiento(nuevaX, nuevaY, delta, anchoMapa, alturaMapa);

        }

        personajeElegido.actualizarCamara(camara, anchoMapa, alturaMapa);
        }
        else {
        	musicaPartida.detenerMusica();
        }


        
        mapRenderer.setView(camara);
        mapRenderer.render();

        
        batch.setProjectionMatrix(camara.combined);
        batch.begin();
        personajeElegido.dibujar(batch);     
        batch.end();
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
    	mapa.dispose();
        mapRenderer.dispose();
        batch.dispose();
    }
}