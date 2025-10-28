package niveles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

import enemigos.EnemigoTirador;
import enemigos.EnemigoBase;
import jugadores.Jugador;
import personajes.Personaje;

public abstract class NivelBase {
	private String nombreMapa;
    private String nombreNivel;
    private TiledMap mapa;
    private OrthogonalTiledMapRenderer mapRenderer;
    private int anchoMapa;
    private int alturaMapa;
    private final int ID_TILE_TRANSPARENTE = 0;
    private Set<String> cajasDestruidas = new HashSet<>();
   
    protected Set<String> enemigosMuertos = new HashSet<>(); 
    protected List<EnemigoBase> enemigos = new ArrayList<>();
    protected float inicioX1, inicioY1;
    protected float inicioX2, inicioY2;
    
    
    public NivelBase(String nombreNivel, String nombreMapa) {
        this.nombreMapa = nombreMapa;
        this.nombreNivel = nombreNivel;
        cargarMapa();
        definirPosicionesIniciales(); 
    }
    
    public abstract void definirPosicionesIniciales();
    public abstract void crearEnemigos(); 
    public abstract boolean comprobarVictoria(float nuevaX1, float nuevaY1, float nuevaX2, float nuevaY2);

    
    private void cargarMapa() {
        this.mapa = new TmxMapLoader().load(this.nombreMapa);
        this.mapRenderer = new OrthogonalTiledMapRenderer(this.mapa);
        this.anchoMapa = mapa.getProperties().get("width", Integer.class) * this.mapa.getProperties().get("tilewidth", Integer.class);
        this.alturaMapa = mapa.getProperties().get("height", Integer.class) * this.mapa.getProperties().get("tileheight", Integer.class);
    }

 public boolean destruirCajaEnHitbox(Rectangle hitbox) {
     TiledMapTileLayer tileLayer = (TiledMapTileLayer) this.mapa.getLayers().get("cajasInteractivas");
     if (tileLayer == null) return false;

     float tileWidth = tileLayer.getTileWidth();
     float tileHeight = tileLayer.getTileHeight();

     Set<String> cajasProcesadas = new HashSet<>(); 
     boolean seDestruyoAlgunaCaja = false;

     final float EPSILON = 0.0001f; 
     int startX = (int) Math.floor(hitbox.x / tileWidth);
     int startY = (int) Math.floor(hitbox.y / tileHeight);
     int endX = (int) Math.floor((hitbox.x + hitbox.width - EPSILON) / tileWidth);
     int endY = (int) Math.floor((hitbox.y + hitbox.height - EPSILON) / tileHeight);

     for (int mapX = startX; mapX <= endX; mapX++) {
         for (int mapY = startY; mapY <= endY; mapY++) {
             TiledMapTileLayer.Cell cell = tileLayer.getCell(mapX, mapY);

             if (cell != null && cell.getTile() != null && cell.getTile().getId() != this.ID_TILE_TRANSPARENTE) {

                 int origenX = (mapX / 2) * 2;
                 int origenY = (mapY / 2) * 2;
                 String keyOrigen = origenX + "_" + origenY;

                 if (!cajasProcesadas.contains(keyOrigen)) {

                     cajasProcesadas.add(keyOrigen); 

                     boolean destruccionExitosa = destruirMatrizCaja(tileLayer, origenX, origenY);

                     if (destruccionExitosa) {
                         seDestruyoAlgunaCaja = true;
                     }
                 }
             }
         }
     }
     return seDestruyoAlgunaCaja;
 }

 private boolean destruirMatrizCaja(TiledMapTileLayer tileLayer, int origenX, int origenY) {
     boolean destruida = false;
     for (int dx = 0; dx < 2; dx++) {
         for (int dy = 0; dy < 2; dy++) {
             int tileX = origenX + dx;
             int tileY = origenY + dy;

             if (tileX < tileLayer.getWidth() && tileY < tileLayer.getHeight()) {
                 TiledMapTileLayer.Cell cell = tileLayer.getCell(tileX, tileY);
                 if (cell != null && cell.getTile() != null && cell.getTile().getId() != this.ID_TILE_TRANSPARENTE) {
                     
                     this.cajasDestruidas.add(tileX + "_" + tileY);
                     cell.setTile(this.mapa.getTileSets().getTile(this.ID_TILE_TRANSPARENTE));
                     destruida = true;
                 }
             }
         }
     }
     return destruida;
 }
    
    public boolean detectarColision(Rectangle hitbox) {
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
    
    public void restaurarEstadoCajas() {
        TiledMapTileLayer tileLayer = (TiledMapTileLayer) this.mapa.getLayers().get("cajasInteractivas");
        if (tileLayer == null) return;
        for (String key : this.cajasDestruidas) {
            String[] partes = key.split("_");
            int x = Integer.parseInt(partes[0]);
            int y = Integer.parseInt(partes[1]);
            tileLayer.setCell(x, y, null); 
        }
    }
    
  

    public void dispose() {
        if (this.mapa != null) this.mapa.dispose();
        if (this.mapRenderer != null) this.mapRenderer.dispose();
    }
    public void limpiarEnemigosMuertos() {
        this.enemigos.removeIf(e -> e.getVida() <= 0);
    }
    
    public void agregarEnemigosMuertos(EnemigoBase enemigo) {
    	this.enemigosMuertos.add(enemigo.getNombre());
    }  
    
    public TiledMap getMapa() { return this.mapa; }
    public OrthogonalTiledMapRenderer getMapRenderer() { return this.mapRenderer; }
    public List<EnemigoBase> getEnemigos() { return this.enemigos; }
    public int getAnchoMapa() { return this.anchoMapa; }
    public int getAlturaMapa() { return this.alturaMapa; }
    public float getInicioX1() { return this.inicioX1; }
    public float getInicioY1() { return this.inicioY1; }
    public float getInicioX2() { return this.inicioX2; }
    public float getInicioY2() { return this.inicioY2; }
    public String getNombreNivel()  { return this.nombreNivel; }
}