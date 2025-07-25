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

import io.github.some.Principal;
import jugadores.Jugador;
import personajes.Akame;
import personajes.Personaje;


public class Partida implements Screen {
	final int puto = 1;
	final int dasdsd = 34;
	private Stage stage;
	private final Jugador jugador = new Jugador();
    private final Principal game;
    private TiledMap map;
    private Skin skin;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private SpriteBatch batch;

    private Personaje personajeElegido;
    
    private Music musicaFondo;
    private float volumen;


    public Partida(Principal game, Music musicaFondo, float volumen) {
        this.game = game;
        this.musicaFondo = musicaFondo;
        this.volumen = volumen;
        musicaFondo.setVolume(volumen); // asegurar que esté en el volumen actual

    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        // Cargar mapa
        map = new TmxMapLoader().load("mapacorregido.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);
       
        // Configurar cámara
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Inicializar batch y personaje
        batch = new SpriteBatch();
        jugador.generarPersonajeAleatorio();
        personajeElegido = jugador.getPersonajeElegido();

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

        // Música de fondo
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("musica/Balatro.mp3"));
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(volumen);
        musicaFondo.play();
    }



    @Override
    public void render(float delta) {
    	if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.UP)) {
        	volumen = Math.min(1f, volumen + 0.1f);
        	musicaFondo.setVolume(volumen);
        }
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.DOWN)) {
        	volumen = Math.max(0f, volumen - 0.1f);
        	musicaFondo.setVolume(volumen);
        }
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.M)) {
            musicaFondo.setVolume(0f);
        }

        // Limpiar pantalla
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Actualizar movimiento
        personajeElegido.mover(delta);
        personajeElegido.actualizarCamara(camera);
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
            personajeElegido.aplicarMovimiento(nuevaX, nuevaY, delta);
        } else {
            personajeElegido.aplicarMovimiento(personajeElegido.getX(), personajeElegido.getY(), delta);
        }

        personajeElegido.actualizarCamara(camera);


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
