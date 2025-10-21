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

import personajes.Enemigo;
import personajes.Personaje;

public abstract class NivelBase {
    public TiledMap mapa;
    protected OrthogonalTiledMapRenderer mapRenderer;
    protected int anchoMapa;
    protected int alturaMapa;
    public final int ID_TILE_TRANSPARENTE = 0;
    protected List<Enemigo> enemigos = new ArrayList<>();
    public Set<String> cajasDestruidas = new HashSet<>();
    protected static Set<String> enemigosMuertos = new HashSet<>(); 
    
    protected float inicioX1, inicioY1;
    protected float inicioX2, inicioY2;
    protected String nombreMapa;
    
    public NivelBase(String nombreMapa) {
        this.nombreMapa = nombreMapa;
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

    public void actualizarCamara(OrthographicCamera camara, Personaje p1, Personaje p2) {
        float centroX;
        float centroY;

        boolean vivo1 = p1.getVida() > 0;
        boolean vivo2 = p2.getVida() > 0;

        if (vivo1 && vivo2) {
            centroX = (p1.getX() + p2.getX()) / 2f + p1.getWidth() / 2f;
            centroY = (p1.getY() + p2.getY()) / 2f + p1.getHeight() / 2f;
        } else if (vivo1) {
            centroX = p1.getX() + p1.getWidth() / 2f;
            centroY = p1.getY() + p1.getHeight() / 2f;
        } else if (vivo2) {
            centroX = p2.getX() + p2.getWidth() / 2f;
            centroY = p2.getY() + p2.getHeight() / 2f;
        } else {
            return;
        }

        float halfWidth = camara.viewportWidth / 2f;
        float halfHeight = camara.viewportHeight / 2f;

        centroX = MathUtils.clamp(centroX, halfWidth, this.anchoMapa - halfWidth);
        centroY = MathUtils.clamp(centroY, halfHeight, this.alturaMapa - halfHeight);

        if (this.anchoMapa < camara.viewportWidth) {
            centroX = this.anchoMapa / 2f;
        }
        if (this.alturaMapa < camara.viewportHeight) {
            centroY = this.alturaMapa / 2f;
        }

        camara.position.set(centroX, centroY, 0);
        camara.update();
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
    
    public TiledMap getMapa() { return this.mapa; }
    public OrthogonalTiledMapRenderer getMapRenderer() { return this.mapRenderer; }
    public List<Enemigo> getEnemigos() { return this.enemigos; }
    public int getAnchoMapa() { return this.anchoMapa; }
    public int getAlturaMapa() { return this.alturaMapa; }
    public float getInicioX1() { return inicioX1; }
    public float getInicioY1() { return inicioY1; }
    public float getInicioX2() { return inicioX2; }
    public float getInicioY2() { return inicioY2; }

    public void dispose() {
        if (this.mapa != null) this.mapa.dispose();
        if (this.mapRenderer != null) this.mapRenderer.dispose();
    }
    public void limpiarEnemigosMuertos() {
        this.enemigos.removeIf(e -> e.getVida() <= 0);
    }
    
    public void agregarEnemigosMuertos(Enemigo enemigo) {
    	this.enemigosMuertos.add(enemigo.getNombre());
    }
}