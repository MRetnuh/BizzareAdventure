package juego;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import audios.Musica;
import estilos.EstiloTexto;
import input.InputController;
import io.github.some.Principal;
import jugadores.Jugador;
import personajes.Enemigo;
import personajes.Personaje;

public class Partida implements Screen {

    private Musica musicaPartida;
    private Stage stage;
    private final Jugador jugador = new Jugador();
    private TiledMap mapa;
    private Skin skin;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camara;
    private SpriteBatch batch;
    private InputController inputController;
    private Set<String> cajasDestruidas = new HashSet<>();
    private int anchoMapa;
    private int alturaMapa;
    private final int ID_TILE_TRANSPARENTE = 0;
    private Enemigo enemigo;
    private Label nombrePersonaje1Label, nombrePersonaje2Label;
    private Label vidaPersonaje1Label, vidaPersonaje2Label;
    private final Principal juego;
    private boolean gameOver = false;

    public Partida(Principal juego) {
        this.juego = juego;
        this.musicaPartida = juego.getMusica();
    }

    @Override
    public void show() {
        this.stage = new Stage();
        Gdx.input.setInputProcessor(this.stage);

        this.mapa = new TmxMapLoader().load("mapacorregido.tmx");
        this.mapRenderer = new OrthogonalTiledMapRenderer(this.mapa);
        this.anchoMapa = mapa.getProperties().get("width", Integer.class) * this.mapa.getProperties().get("tilewidth", Integer.class);
        this.alturaMapa = mapa.getProperties().get("height", Integer.class) * this.mapa.getProperties().get("tileheight", Integer.class);
        restaurarEstadoCajas();

        this.camara = new OrthographicCamera();
        this.camara.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        enemigo = new Enemigo(400, 928);

        this.batch = new SpriteBatch();

        
        jugador.getPersonaje1().setStage(stage);
        jugador.getPersonaje2().setStage(stage);   
        inputController = new InputController(this, jugador.getPersonaje1(), jugador.getPersonaje2());
        Gdx.input.setInputProcessor(inputController);
        
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));
        
        nombrePersonaje1Label = new Label("Nombre: " + jugador.getPersonaje1().getNombre(), EstiloTexto.ponerEstiloLabel(40, Color.RED));
        vidaPersonaje1Label = new Label("Vida: " + jugador.getPersonaje1().getVida(), EstiloTexto.ponerEstiloLabel(40, Color.RED));
        nombrePersonaje2Label = new Label("Nombre: " + jugador.getPersonaje2().getNombre(), EstiloTexto.ponerEstiloLabel(40, Color.BLUE));
        vidaPersonaje2Label = new Label("Vida: " + jugador.getPersonaje2().getVida(), EstiloTexto.ponerEstiloLabel(40, Color.BLUE));

        Table table = new Table();
        table.left().top();
        table.add(nombrePersonaje1Label).size(350, 50).padBottom(5).row();
        table.add(vidaPersonaje1Label).size(350, 50).padBottom(10).row();
        table.add(nombrePersonaje2Label).size(350, 50).padBottom(5).row();
        table.add(vidaPersonaje2Label).size(350, 50);

        Container<Table> contenedor = new Container<>(table);
        contenedor.setSize(400, 200);
        contenedor.setBackground(skin.getDrawable("default-round"));
        contenedor.setPosition(0, Gdx.graphics.getHeight() - contenedor.getHeight());

        this.stage.addActor(contenedor);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        actualizarPersonaje(jugador.getPersonaje1(), delta);
        actualizarPersonaje(jugador.getPersonaje2(), delta);

        actualizarHUD();

        this.mapRenderer.setView(this.camara);
        this.mapRenderer.render();

        this.batch.setProjectionMatrix(this.camara.combined);
        this.batch.begin();
        jugador.getPersonaje1().dibujar(batch, delta);
        jugador.getPersonaje2().dibujar(batch, delta);
        enemigo.dibujar(batch, delta);
        this.batch.end();

        this.stage.act(delta);
        this.stage.draw();

        enemigo.actualizarIA(delta, (jugador.getPersonaje1().getX() + jugador.getPersonaje2().getX()) / 2f);
    }

    private void actualizarPersonaje(Personaje p, float delta) {
        if (p.getVida() <= 0) {
            if (!gameOver) {
                gameOver = true;
                musicaPartida.cambiarMusica("derrota");
                p.morir();
            }
            return;
        }

        boolean estaSobreElSuelo = detectarColision(new Rectangle(p.getX(), p.getY() - 1, 16, 16));

        p.guardarPosicionAnterior();
        p.actualizarGravedad(delta, estaSobreElSuelo, alturaMapa);

        float nuevaX = p.getNuevaX(delta);
        float nuevaY = p.getNuevaY(delta);

        if (nuevaY < -190) {
            p.reducirVida();
        }

        Rectangle hitboxTentativaX = new Rectangle(p.getHitbox());
        hitboxTentativaX.setPosition(nuevaX, p.getY());
        boolean colisionX = detectarColision(hitboxTentativaX);

        Rectangle hitboxTentativaY = new Rectangle(p.getHitbox());
        hitboxTentativaY.setPosition(p.getX(), nuevaY);
        boolean colisionY = detectarColision(hitboxTentativaY);
        if (colisionY) {
            p.frenarCaida();
            p.setY(p.getPrevY());
        }

        if (!colisionX || !colisionY) {
            float finalX = !colisionX ? nuevaX : p.getX();
            float finalY = !colisionY ? nuevaY : p.getY();
            p.aplicarMovimiento(finalX, finalY, delta, anchoMapa, alturaMapa);
        }

        p.atacar(delta, musicaPartida.getVolumen());
        detectarYEliminarTile(p.getHitbox());
        p.actualizarCamara(camara, anchoMapa, alturaMapa);
    }

    private void actualizarHUD() {
        nombrePersonaje1Label.setText("Nombre: " + jugador.getPersonaje1().getNombre());
        vidaPersonaje1Label.setText("Vida: " + jugador.getPersonaje1().getVida());

        nombrePersonaje2Label.setText("Nombre: " + jugador.getPersonaje2().getNombre());
        vidaPersonaje2Label.setText("Vida: " + jugador.getPersonaje2().getVida());
    }

    private void restaurarEstadoCajas() {
        TiledMapTileLayer tileLayer = (TiledMapTileLayer) this.mapa.getLayers().get("cajasInteractivas");
        for (String key : cajasDestruidas) {
            String[] partes = key.split("_");
            int x = Integer.parseInt(partes[0]);
            int y = Integer.parseInt(partes[1]);
            tileLayer.setCell(x, y, null);
        }
    }

    private void detectarYEliminarTile(Rectangle hitbox) {
        TiledMapTileLayer tileLayer = (TiledMapTileLayer) this.mapa.getLayers().get("cajasInteractivas");
        if (tileLayer == null) return;

        boolean cajaCercana = false;

        int tileX = (int) (hitbox.x / tileLayer.getTileWidth());
        int tileY = (int) (hitbox.y / tileLayer.getTileHeight());
        int checkX = tileX - 1;
        while (checkX <= tileX + 1 && !cajaCercana) {
            int checkY = tileY - 1;
            while (checkY <= tileY + 1 && !cajaCercana) {
                TiledMapTileLayer.Cell cell = tileLayer.getCell(checkX, checkY);
                if (cell != null && cell.getTile() != null) {
                    int tileId = cell.getTile().getId();
                    if (tileId != this.ID_TILE_TRANSPARENTE) {
                        cajaCercana = true;
                    }
                }
                checkY++;
            }
            checkX++;
        }

        if (cajaCercana) {
            int ancho = tileLayer.getWidth();
            int altura = tileLayer.getHeight();
            for (int mapX = 0; mapX < ancho; mapX++) {
                for (int mapY = 0; mapY < altura; mapY++) {
                    TiledMapTileLayer.Cell cell = tileLayer.getCell(mapX, mapY);
                    if (cell != null && cell.getTile() != null) {
                        int tileId = cell.getTile().getId();
                        if (tileId != this.ID_TILE_TRANSPARENTE) {
                            cajasDestruidas.add(mapX + "_" + mapY);
                            cell.setTile(this.mapa.getTileSets().getTile(this.ID_TILE_TRANSPARENTE));
                        }
                    }
                }
            }
        }
    }

    private Polygon convertirEnPoligono(Rectangle rect) {
        Polygon poly = new Polygon(new float[]{
            0, 0,
            rect.width, 0,
            rect.width, rect.height,
            0, rect.height
        });
        poly.setPosition(rect.x, rect.y);
        return poly;
    }

    private boolean detectarColision(Rectangle hitbox) {
        Polygon hitboxPoligono = convertirEnPoligono(hitbox);

        for (MapObject object : this.mapa.getLayers().get("colisiones").getObjects()) {
            String clase = object.getProperties().get("type", String.class);
            if (clase == null || !clase.equals("Tierra")) continue;

            if (object instanceof RectangleMapObject) {
                Rectangle rectMapa = ((RectangleMapObject) object).getRectangle();
                if (hitbox.overlaps(rectMapa)) return true;
            } else if (object instanceof PolygonMapObject) {
                PolygonMapObject polygonObject = (PolygonMapObject) object;
                Polygon polygon = polygonObject.getPolygon();
                float x = polygonObject.getProperties().get("x", Float.class);
                float y = polygonObject.getProperties().get("y", Float.class);
                Polygon poligonoTransformado = new Polygon(polygon.getVertices());
                poligonoTransformado.setPosition(x, y);
                if (Intersector.overlapConvexPolygons(hitboxPoligono, poligonoTransformado)) return true;
            }
        }
        return false;
    }

    public void abrirOpciones() {
        juego.setScreen(new Opciones(juego, this));
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        this.mapa.dispose();
        this.mapRenderer.dispose();
        this.batch.dispose();
        this.stage.dispose();
    }
}
