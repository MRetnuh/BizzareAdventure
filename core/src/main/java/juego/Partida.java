package juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

    
    public Partida(Principal juego) {
        this.juego = juego;
        this.musicaPartida = juego.getMusica();
    }

    @Override
    public void show() {
    	
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

      
        mapa = new TmxMapLoader().load("mapacorregido.tmx");
    mapRenderer = new OrthogonalTiledMapRenderer(mapa);
    anchoMapa = mapa.getProperties().get("width", Integer.class) * mapa.getProperties().get("tilewidth", Integer.class);
    alturaMapa = mapa.getProperties().get("height", Integer.class) * mapa.getProperties().get("tileheight", Integer.class);
    
    
    camara = new OrthographicCamera();
    camara.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

   
    batch = new SpriteBatch();
    personajesDisponibles = jugador.getListaPersonajes();
    if(!jugador.getPartidaEmpezada()) {
    jugador.generarPersonajeAleatorio();
    }
    personajeElegido = jugador.getPersonajeElegido();
    personajeElegido.setStage(stage);

    skin = new Skin(Gdx.files.internal("uiskin.json"));

    nombrePersonajeLabel = new Label("Nombre: " + personajeElegido.getNombre(), EstiloTexto.ponerEstiloLabel(40, Color.RED));
    nombrePersonajeLabel.setAlignment(Align.left);
    
    vidaPersonajeLabel = new Label("Vida: " + personajeElegido.getVida(), EstiloTexto.ponerEstiloLabel(40, Color.RED));
    vidaPersonajeLabel.setAlignment(Align.left);
    
    Table table = new Table();
    table.left().top();
    table.add(nombrePersonajeLabel).size(350, 50).padBottom(10).row();
    table.add(vidaPersonajeLabel).size(350, 50);

    
    Container<Table> contenedor = new Container<>(table);
    contenedor.setSize(400, 130);
    contenedor.setBackground(skin.getDrawable("default-round"));
    contenedor.setPosition(0, Gdx.graphics.getHeight() - contenedor.getHeight()); 

    if (!musicaPartida.estaReproduciendo()) {
        musicaPartida.show(); 
    }

    stage.addActor(contenedor);
    EmisorEventos.obtenerInstancia().en("cambiarPersonaje", this::cambiarPersonajeYSumarVida);

}
    private void cambiarPersonajeYSumarVida() {
        indicePersonajeActual = (indicePersonajeActual + 1) % personajesDisponibles.length;
        
        personajeElegido = personajesDisponibles[indicePersonajeActual];
        
        personajeElegido.setVida(personajeElegido.getVida() + 1);
        
        personajeElegido.setPosicion(200, 930);
        
        actualizarUI();
    }
    
    private void actualizarUI() {
        nombrePersonajeLabel.setText("Nombre: " + personajeElegido.getNombre());
        vidaPersonajeLabel.setText("Vida: " + personajeElegido.getVida());
    }


@Override
public void render(float delta) {
	
    if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.P)) {
    	juego.setScreen(new Opciones(juego, Partida.this));
        return;
    }

  
    Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

   
    if(personajeElegido.getVida() != 0) {
        boolean estaSobreElSuelo = false;
        Rectangle hitboxPersonaje = personajeElegido.getHitbox();

        personajeElegido.guardarPosicionAnterior();
        Rectangle hitboxSuelo = new Rectangle(personajeElegido.getHitbox());
        hitboxSuelo.setY(hitboxSuelo.getY() - 1);

        if (hayColision(hitboxSuelo)) {
            estaSobreElSuelo = true;
        }

        personajeElegido.actualizarGravedad(delta, estaSobreElSuelo, alturaMapa);
        
        float nuevaX = personajeElegido.getNuevaX(delta);
        float nuevaY = personajeElegido.getNuevaY(delta);

        Rectangle hitboxTentativaX = new Rectangle(hitboxPersonaje);
        hitboxTentativaX.setPosition(nuevaX, personajeElegido.getY());
        boolean colisionX = hayColision(hitboxTentativaX);

        Rectangle hitboxTentativaY = new Rectangle(hitboxPersonaje);
        hitboxTentativaY.setPosition(personajeElegido.getX(), nuevaY);
        boolean colisionY = hayColision(hitboxTentativaY);
        if (colisionY) {
            personajeElegido.frenarCaida();
            personajeElegido.setY(personajeElegido.getPrevY());
        }
        if (!colisionX || !colisionY) {
            float nuevoX = !colisionX ? nuevaX : personajeElegido.getX();
            float nuevoY = !colisionY ? nuevaY : personajeElegido.getY();
            personajeElegido.aplicarMovimiento(nuevoX, nuevoY, delta, anchoMapa, alturaMapa);
        }
        
        detectarYEliminarTile(personajeElegido.getHitbox());
        personajeElegido.actualizarCamara(camara, anchoMapa, alturaMapa);
    }
    else {
    	musicaPartida.detenerMusica();
    }



    
    mapRenderer.setView(camara);
    mapRenderer.render();

    
    batch.setProjectionMatrix(camara.combined);
    batch.begin();
    personajeElegido.dibujar(batch, delta); 
    batch.end();
    stage.act(delta);
    stage.draw();
}
	
private void detectarYEliminarTile(Rectangle hitbox) {
	    TiledMapTileLayer tileLayer = (TiledMapTileLayer) mapa.getLayers().get("cajasInteractivas");

	    if (tileLayer == null) {
	        return;
	    }

	    boolean cajaCercana = false;

	    // Definimos un área de interacción más grande (por ejemplo 1 tile alrededor del personaje)
	    int tileX = (int) (hitbox.x / tileLayer.getTileWidth());
	    int tileY = (int) (hitbox.y / tileLayer.getTileHeight());

	    // Recorremos las celdas alrededor del personaje (un radio de 1 tile)
	    for (int x = tileX - 1; x <= tileX + 1; x++) {
	        for (int y = tileY - 1; y <= tileY + 1; y++) {
	            TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
	            if (cell != null && cell.getTile() != null) {
	                int tileId = cell.getTile().getId();
	                if (tileId != ID_TILE_TRANSPARENTE) {
	                    cajaCercana = true;
	                    break;
	                }
	            }
	        }
	        if (cajaCercana) break;
	    }

	    // Si hay una caja cerca y presiona E → elimina todas las cajas
	    if (cajaCercana && personajeElegido.getEstaAtacando()) {
	        int width = tileLayer.getWidth();
	        int height = tileLayer.getHeight();

	        for (int x = 0; x < width; x++) {
	            for (int y = 0; y < height; y++) {
	                TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
	                if (cell != null && cell.getTile() != null) {
	                    int tileId = cell.getTile().getId();
	                    if (tileId != ID_TILE_TRANSPARENTE) {
	                        cell.setTile(mapa.getTileSets().getTile(ID_TILE_TRANSPARENTE));
	                    }
	                }
	            }
	        }

	        EmisorEventos.obtenerInstancia().emitir("cambiarPersonaje");
	    }
	}
private Polygon rectToPolygon(Rectangle rect) {
    Polygon poly = new Polygon(new float[]{
        0, 0,
        rect.width, 0,
        rect.width, rect.height,
        0, rect.height
    });
    poly.setPosition(rect.x, rect.y);
    return poly;
}

private boolean hayColision(Rectangle hitbox) {
	Polygon hitboxPoligono = rectToPolygon(hitbox);
    for (MapObject object : mapa.getLayers().get("colisiones").getObjects()) {
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
    for (MapObject object : mapa.getLayers().get("interactivos").getObjects()) {
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
    return false;
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
    stage.dispose();
}
}