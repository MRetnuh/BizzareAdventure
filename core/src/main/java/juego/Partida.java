package juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
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
import io.github.some.Principal;
import jugadores.Jugador;
import personajes.Akame;
import personajes.Personaje;


public class Partida implements Screen {
	private Musica musicaPartida = new Musica("Balatro");
	private Stage stage;
	private final Jugador jugador = new Jugador();
    private final Principal game;
    private TiledMap map;
    private Skin skin;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private SpriteBatch batch;

    private Personaje personajeElegido;

    int mapWidthInPixels; 
    int mapHeightInPixels;
    
    public Partida(Principal game) {
        this.game = game; // asegurar que esté en el volumen actual

    }

    @Override
    public void show() {
    	
    	musicaPartida.show();
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        // Cargar mapa
        map = new TmxMapLoader().load("mapacorregido.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        mapWidthInPixels = map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class);
        mapHeightInPixels = map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class);
        
        // Configurar cámara
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Inicializar batch y personaje
        batch = new SpriteBatch();
        jugador.generarPersonajeAleatorio();
        personajeElegido = jugador.getPersonajeElegido();
        personajeElegido.setStage(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Crear labels
        Label nombrePersonaje = new Label("Nombre: " + personajeElegido.getNombre(), skin);
        nombrePersonaje.setFontScale(2);
        nombrePersonaje.setAlignment(Align.left);

        Label vidaPersonaje = new Label("Vida: " + personajeElegido.getVida(), skin);
        vidaPersonaje.setFontScale(2);
        vidaPersonaje.setAlignment(Align.left);

        // Crear tabla con labels
        Table table = new Table();
        table.left().top();
        table.add(nombrePersonaje).size(350, 50).padBottom(10).row();
        table.add(vidaPersonaje).size(350, 50);

        // Contenedor que envuelve la tabla
        Container<Table> contenedor = new Container<>(table);
        contenedor.setSize(400, 130); // Ajustá el tamaño como prefieras
        contenedor.setBackground(skin.getDrawable("default-round"));
        contenedor.setPosition(0, Gdx.graphics.getHeight() - contenedor.getHeight()); // esquina superior izquierda

        // Agregar contenedor al escenario
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
        // Limpiar pantalla
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Actualizar movimiento
        if(personajeElegido.getVida() != 0) {
        personajeElegido.mover(delta);
        personajeElegido.actualizarCamara(camera, mapWidthInPixels, mapHeightInPixels);
        
        //Gravedad
        boolean estaSobreElSuelo = false;
        Rectangle hitboxPersonaje = personajeElegido.getHitbox();
        hitboxPersonaje.setY(hitboxPersonaje.getY() - 1); // bajamos la hitbox 1 pixel

        for (MapObject object : map.getLayers().get("colisiones").getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rectMapa = ((RectangleMapObject) object).getRectangle();
                if (hitboxPersonaje.overlaps(rectMapa)) {
                    estaSobreElSuelo = true;
                    break;
                }
            }
        }
        
        personajeElegido.actualizarGravedad(delta, estaSobreElSuelo, mapHeightInPixels);
     // Movimiento con detección de colisiones
        float nuevaX = personajeElegido.getNuevaX(delta);
        float nuevaY = personajeElegido.getNuevaY(delta);
        Rectangle hitboxTentativa = new Rectangle(nuevaX, nuevaY, 63, 64); // Ajustá tamaño si hace falta

        boolean colision = false;
        for (MapObject object : map.getLayers().get("colisiones").getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rectMapa = ((RectangleMapObject) object).getRectangle();
                if (hitboxTentativa.overlaps(rectMapa)) {
                    colision = true;
                    break;
                }
            }
        }

        // Aplicar movimiento
        if (!colision) {
        	personajeElegido.aplicarMovimiento(nuevaX, nuevaY, delta, mapWidthInPixels, mapHeightInPixels);

        } else {
        	personajeElegido.aplicarMovimiento(nuevaX, nuevaY, delta, mapWidthInPixels, mapHeightInPixels);

        }

        personajeElegido.actualizarCamara(camera, mapWidthInPixels, mapHeightInPixels);
        }
        else {
        	musicaPartida.detenerMusica();
        }


        // Dibujar mapa
        mapRenderer.setView(camera);
        mapRenderer.render();

        // Dibujar personaje + imagen de prueba
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        personajeElegido.dibujar(batch);     // Dibuja el personaje normalmente
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
        map.dispose();
        mapRenderer.dispose();
        batch.dispose();
    }
}