package juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import io.github.some.Principal;
import personajes.Personaje;

public class Partida implements Screen {
    private final Principal game;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private SpriteBatch batch;

    private Personaje akame;

    // 🧪 Imagen de prueba
    private Texture prueba;

    public Partida(Principal game) {
        this.game = game;
    }

    @Override
    public void show() {
        // Cargar mapa
        map = new TmxMapLoader().load("mapacorregido.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        // Configurar cámara
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Inicializar batch y personaje
        batch = new SpriteBatch();
        akame = new Personaje(); // usa los sprites de akame
    }

    @Override
    public void render(float delta) {
        // Limpiar pantalla
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Actualizar movimiento
        akame.mover(delta);
        akame.actualizarCamara(camera);

        // Dibujar mapa
        mapRenderer.setView(camera);
        mapRenderer.render();

        // Dibujar personaje + imagen de prueba
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        akame.dibujar(batch);     // Dibuja el personaje normalmente
        batch.end();
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
        prueba.dispose(); // Libera la imagen de prueba
    }
}
