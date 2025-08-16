package juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
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
import eventos.EmisorEventos;
import io.github.some.Principal;
import jugadores.Jugador;
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
    private Personaje personajeElegido;
    int anchoMapa; 
    int alturaMapa;
    private MapObject objetoInteractivoActual = null;
    private final int ID_TILE_TRANSPARENTE = 0;
    private Personaje[] personajesDisponibles;
    private int indicePersonajeActual = 0;
    private Label nombrePersonajeLabel;
    private Label vidaPersonajeLabel;
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
    
    
        this.camara = new OrthographicCamera();
        this.camara.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

   
        this.batch = new SpriteBatch();
        this.personajesDisponibles = jugador.getListaPersonajes();
    if(!this.jugador.getPartidaEmpezada()) {
    	this.jugador.generarPersonajeAleatorio();
    }
    this.personajeElegido = this.jugador.getPersonajeElegido();
    this.personajeElegido.setStage(this.stage);

    this.skin = new Skin(Gdx.files.internal("uiskin.json"));

    this.nombrePersonajeLabel = new Label("Nombre: " + this.personajeElegido.getNombre(), EstiloTexto.ponerEstiloLabel(40, Color.RED));
    this.nombrePersonajeLabel.setAlignment(Align.left);
    
    this.vidaPersonajeLabel = new Label("Vida: " + this.personajeElegido.getVida(), EstiloTexto.ponerEstiloLabel(40, Color.RED));
    this.vidaPersonajeLabel.setAlignment(Align.left);
    
    Table table = new Table();
    table.left().top();
    table.add(this.nombrePersonajeLabel).size(350, 50).padBottom(10).row();
    table.add(this.vidaPersonajeLabel).size(350, 50);

    
    Container<Table> contenedor = new Container<>(table);
    contenedor.setSize(400, 130);
    contenedor.setBackground(skin.getDrawable("default-round"));
    contenedor.setPosition(0, Gdx.graphics.getHeight() - contenedor.getHeight()); 

    this.stage.addActor(contenedor);

}
@Override
public void render(float delta) {
	
    Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

   
    if(this.personajeElegido.getVida() != 0) {
    	if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.P)) {
    		this.juego.setScreen(new Opciones(this.juego, Partida.this));
        return;
    }
        boolean estaSobreElSuelo = false;
        Rectangle hitboxPersonaje = this.personajeElegido.getHitbox();

        this.personajeElegido.guardarPosicionAnterior();
        Rectangle hitboxSuelo = new Rectangle(this.personajeElegido.getHitbox());
        hitboxSuelo.setY(hitboxSuelo.getY() - 1);

        if (detectarColision(hitboxSuelo)) {
            estaSobreElSuelo = true;
        }

        this.personajeElegido.actualizarGravedad(delta, estaSobreElSuelo, this.alturaMapa);
        
        float nuevaX = this.personajeElegido.getNuevaX(delta);
        float nuevaY = this.personajeElegido.getNuevaY(delta);

        if (nuevaY < -190) {
        	this.personajeElegido.reducirVida();
        }
        Rectangle hitboxTentativaX = new Rectangle(hitboxPersonaje);
        hitboxTentativaX.setPosition(nuevaX, this.personajeElegido.getY());
        boolean colisionX = detectarColision(hitboxTentativaX);

        Rectangle hitboxTentativaY = new Rectangle(hitboxPersonaje);
        hitboxTentativaY.setPosition(this.personajeElegido.getX(), nuevaY);
        boolean colisionY = detectarColision(hitboxTentativaY);
        if (colisionY) {
        	this.personajeElegido.frenarCaida();
        	this.personajeElegido.setY(this.personajeElegido.getPrevY());
        }
        if (!colisionX || !colisionY) {
            float nuevoX = !colisionX ? nuevaX : this.personajeElegido.getX();
            float nuevoY = !colisionY ? nuevaY : this.personajeElegido.getY();
            this.personajeElegido.aplicarMovimiento(nuevoX, nuevoY, delta, this.anchoMapa, this.alturaMapa);
        }
        this.personajeElegido.atacar(delta, this.musicaPartida.getVolumen());
        detectarYEliminarTile(this.personajeElegido.getHitbox());
        this.personajeElegido.actualizarCamara(this.camara, this.anchoMapa, this.alturaMapa);
    }
    else {
    	if(!this.gameOver) {
    		this.gameOver = true;
    		this.musicaPartida.cambiarMusica("derrota");
    		this.personajeElegido.morir();
    	}
    }



    
    this.mapRenderer.setView(this.camara);
    this.mapRenderer.render();

    
    this.batch.setProjectionMatrix(this.camara.combined);
    this.batch.begin();
    this.personajeElegido.dibujar(this.batch, delta); 
    this.batch.end();
    this.stage.act(delta);
    this.stage.draw();
}
	
private void detectarYEliminarTile(Rectangle hitbox) {
    TiledMapTileLayer tileLayer = (TiledMapTileLayer) this.mapa.getLayers().get("cajasInteractivas");

    if (tileLayer == null) {
        return;
    }

    boolean cajaCercana = false;

    int tileX = (int) (hitbox.x / tileLayer.getTileWidth());
    int tileY = (int) (hitbox.y / tileLayer.getTileHeight());
    int checkX = tileX - 1;
    while (checkX <= tileX + 1 && !cajaCercana) {
        int checkY = tileY - 1;
        while (checkY <= tileY + 1 &&  !cajaCercana) {
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



    if (cajaCercana && this.personajeElegido.getEstaAtacando()) {
    	this.jugador.cambiarPersonaje();
        int ancho = tileLayer.getWidth();
        int altura = tileLayer.getHeight();
        int mapX = 0;
       while(mapX < ancho) {
    	   int mapY = 0;
            while(mapY < altura) {
                TiledMapTileLayer.Cell cell = tileLayer.getCell(mapX, mapY);
                if (cell != null && cell.getTile() != null) {
                    int tileId = cell.getTile().getId();
                    if (tileId != this.ID_TILE_TRANSPARENTE) {
                        cell.setTile(this.mapa.getTileSets().getTile(this.ID_TILE_TRANSPARENTE));
                    }
                }
                mapY++;
            }
            mapX++;
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
            if (Intersector.overlapConvexPolygons(hitboxPoligono, poligonoTransformado)) {
                return true;
            }
        }
    }
    for (MapObject object : this.mapa.getLayers().get("interactivos").getObjects()) {
        String clase = object.getProperties().get("type", String.class);
        if (clase == null || !clase.equals("Tierra")) continue;

        Rectangle rectMapa = null;

        if (object instanceof RectangleMapObject) {
            rectMapa = ((RectangleMapObject) object).getRectangle();
            if (!hitbox.overlaps(rectMapa)) continue;
        } else if (object instanceof PolygonMapObject) {
            PolygonMapObject polygonObject = (PolygonMapObject) object;
            Polygon polygon = polygonObject.getPolygon();
            float x = polygonObject.getProperties().get("x", Float.class);
            float y = polygonObject.getProperties().get("y", Float.class);
            Polygon poligonoTransformado = new Polygon(polygon.getVertices());
            poligonoTransformado.setPosition(x, y);
            if (!Intersector.overlapConvexPolygons(hitboxPoligono, poligonoTransformado)) continue;

            rectMapa = poligonoTransformado.getBoundingRectangle();
        }

        TiledMapTileLayer tileLayer = (TiledMapTileLayer) this.mapa.getLayers().get("cajasInteractivas");
        if (tileLayer != null) {
            int startX = (int) (rectMapa.x / tileLayer.getTileWidth());
            int endX = (int) ((rectMapa.x + rectMapa.width) / tileLayer.getTileWidth());
            int startY = (int) (rectMapa.y / tileLayer.getTileHeight());
            int endY = (int) ((rectMapa.y + rectMapa.height) / tileLayer.getTileHeight());

            boolean tieneTile = false;
            for (int x = startX; x <= endX; x++) {
                for (int y = startY; y <= endY; y++) {
                    TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
                    if (cell != null && cell.getTile() != null) {
                        int tileId = cell.getTile().getId();
                        if (tileId != this.ID_TILE_TRANSPARENTE) {
                            tieneTile = true;
                            break;
                        }
                    }
                }
                if (tieneTile) break;
            }

            if (!tieneTile) continue;
        }

        return true;
    }



    return false;
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