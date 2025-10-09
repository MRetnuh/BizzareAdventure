package juego;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import audios.Musica;
import estilos.EstiloTexto;
import input.InputController;
// import io.github.some.Principal; // Importación no usada, eliminada o comentada si no es necesaria
import jugadores.Jugador;
import personajes.Enemigo;
import personajes.Personaje;
import proyectiles.Proyectil;


public class Partida implements Screen {

    private Musica musicaPartida;
    private Stage stage;
    private Stage stageHUD;
    private final Jugador jugador1 = new Jugador();
    private final Jugador jugador2 = new Jugador();
    private TiledMap mapa;
    private Skin skin;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camara;
    private SpriteBatch batch;
    private InputController inputController;
    private Personaje personaje1;
    private Personaje personaje2;
    private Set<String> cajasDestruidas = new HashSet<>();
    private static Set<String> enemigosMuertos = new HashSet<>();
    private int anchoMapa;
    private int alturaMapa;
    private final int ID_TILE_TRANSPARENTE = 0;
    private List<Enemigo> enemigos = new ArrayList<>();
    private Label nombrePersonaje1Label, vidaPersonaje1Label;
    private Label nombrePersonaje2Label, vidaPersonaje2Label;
    private final Game juego;
    private boolean gameOver1 = false;
    private boolean gameOver2 = false;
    private float nuevaX1, nuevaY1;
    private float nuevaX2, nuevaY2;
    private boolean victoria = false;
    
    public Partida(Game juego, Musica musica) {
        this.juego = juego;
        this.musicaPartida = musica;
        this.camara = new OrthographicCamera();
        this.camara.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport(), this.batch);
        this.stageHUD = new Stage(new ScreenViewport(), this.batch);
    }

    private boolean enemigosCreados = false;

    @Override
    public void show() {
        this.mapa = new TmxMapLoader().load("mapacorregido.tmx");
        this.mapRenderer = new OrthogonalTiledMapRenderer(this.mapa);
        this.anchoMapa = mapa.getProperties().get("width", Integer.class) * this.mapa.getProperties().get("tilewidth", Integer.class);
        this.alturaMapa = mapa.getProperties().get("height", Integer.class) * this.mapa.getProperties().get("tileheight", Integer.class);
        restaurarEstadoCajas();

        if (!this.enemigosCreados) {
            String[] idsEnemigos = {"enemigo1", "enemigo2", "enemigo3"};
            float[][] posiciones = {
                    {600, 928},
                    {800, 928},
                    {1000, 928}
            };

            for (int i = 0; i < idsEnemigos.length; i++) {
                String id = idsEnemigos[i];
                if (!this.enemigosMuertos.contains(id)) {
                    this.enemigos.add(new Enemigo(id, posiciones[i][0], posiciones[i][1]));
                }
            }
            this.enemigosCreados = true;
        }

        if (!this.jugador1.getPartidaEmpezada()) {
            this.jugador1.generarPersonajeAleatorio();
        }
        if (!this.jugador2.getPartidaEmpezada()) {
            this.jugador2.generarPersonajeAleatorio();
        }

        this.personaje1 = this.jugador1.getPersonajeElegido();
        this.personaje2 = this.jugador2.getPersonajeElegido();

        this.stage.addActor(this.personaje1);
        this.stage.addActor(this.personaje2);
        for (Enemigo enemigo : this.enemigos) {
            this.stage.addActor(enemigo);
        }
        this.inputController = new InputController();
        Gdx.input.setInputProcessor(this.inputController);

        this.skin = new Skin(Gdx.files.internal("uiskin.json")); //kevin (el follador de hornet)
        this.nombrePersonaje1Label = new Label("Nombre: " + this.personaje1.getNombre(), EstiloTexto.ponerEstiloLabel(40, Color.RED));
        this.vidaPersonaje1Label = new Label("Vida: " + this.personaje1.getVida(), EstiloTexto.ponerEstiloLabel(40, Color.RED));

        this.nombrePersonaje2Label = new Label("Nombre: " + this.personaje2.getNombre(), EstiloTexto.ponerEstiloLabel(40, Color.BLUE));
        this.vidaPersonaje2Label = new Label("Vida: " + this.personaje2.getVida(), EstiloTexto.ponerEstiloLabel(40, Color.BLUE));
        Table table1 = new Table();
        Table table2 = new Table();
        table1.left().top();
        table2.right().top();
        table1.add(this.nombrePersonaje1Label).size(350, 50).padBottom(5).row();
        table1.add(this.vidaPersonaje1Label).size(350, 50);
        table2.add(this.nombrePersonaje2Label).size(350, 50).padBottom(5).row();
        table2.add(this.vidaPersonaje2Label).size(350, 50);

        Container<Table> contenedor1 = new Container<>(table1);
        Container<Table> contenedor2 = new Container<>(table2);
        contenedor1.setSize(400, 130);
        contenedor2.setSize(400, 130);
        contenedor1.setBackground(this.skin.getDrawable("default-round"));
        contenedor2.setBackground(this.skin.getDrawable("default-round"));
        contenedor1.setPosition(0, Gdx.graphics.getHeight() - contenedor1.getHeight());
        contenedor2.setPosition(Gdx.graphics.getWidth() - contenedor2.getWidth(), Gdx.graphics.getHeight() - contenedor2.getHeight());

        this.stageHUD.addActor(contenedor1);
        this.stageHUD.addActor(contenedor2);
    }


    @Override
    public void render(float delta) {
    	if(this.victoria == false) {
        if(this.personaje1.getVida() > 0){
            this.personaje1.setMoviendoDerecha(this.inputController.getDerecha1());
            this.personaje1.setMoviendoIzquierda(this.inputController.getIzquierda1());
            this.personaje1.setEstaSaltando(this.inputController.getSaltar1());
            if(this.inputController.getAtacar1()) {
                this.personaje1.iniciarAtaque(this.musicaPartida.getVolumen());
                this.inputController.setAtacarFalso1();
            }
            if(this.inputController.getOpciones1()) abrirOpciones();
        }

        if(this.personaje2.getVida() > 0){
            this.personaje2.setMoviendoDerecha(this.inputController.getDerecha2());
            this.personaje2.setMoviendoIzquierda(this.inputController.getIzquierda2());
            this.personaje2.setEstaSaltando(this.inputController.getSaltar2());
            if(this.inputController.getAtacar2()) {
                this.personaje2.iniciarAtaque(this.musicaPartida.getVolumen());
                this.inputController.setAtacarFalso2();
            }
            if(this.inputController.getOpciones2()) abrirOpciones();
        }
    	}
        actualizarPersonaje(this.jugador1, this.personaje1, delta, true, this.nuevaX1, this.nuevaY1);
        actualizarPersonaje(this.jugador2, this.personaje2, delta, false, this.nuevaX2, this.nuevaY2);
        
        if(this.nuevaX1 >= 3502.00 && this.nuevaX1 <= 3700.00 && this.nuevaY1 >= 1250.00 || this.nuevaX2 >= 3502.00 && this.nuevaX2 <= 3502.00 && this.nuevaY2 >= 1250.00) {
        	this.victoria = true;
        	this.personaje1.setMoviendoDerecha(false);
        	this.musicaPartida.cambiarMusica("primeraisla");
        } 
        
        actualizarCamara();
        actualizarHUD();
        System.out.println(victoria);
        System.out.println(nuevaX1);
        System.out.println(nuevaX2);
        System.out.println(nuevaY1);
        System.out.println(nuevaY2);
        limpiarEnemigosMuertos();

        this.mapRenderer.setView(this.camara);
        this.mapRenderer.render();

        this.batch.setProjectionMatrix(this.camara.combined);

        if(!this.gameOver1 || !this.gameOver2) {
            for (Enemigo enemigo : this.enemigos) {
                if (enemigo.getVida() > 0) {
                	
                    for (Proyectil b : enemigo.getBalas()) {
                        this.stage.addActor(b);
                    }

                    enemigo.actualizarIA(delta, this.personaje1, this.personaje2, this.musicaPartida.getVolumen(), this);
                }
            }
        }
        // sincronizar la cámara del stage con la cámara del mundo
        OrthographicCamera stageCam = (OrthographicCamera) this.stage.getViewport().getCamera();
        stageCam.position.set(this.camara.position.x, this.camara.position.y, this.camara.position.z);
        stageCam.zoom = this.camara.zoom; // opcional si cambias zoom
        this.stage.getViewport().apply();  // aplica tamaño y matrices
        stageCam.update();

        this.stage.act(delta);
        this.stage.draw();

        this.stageHUD.act(delta);
        this.stageHUD.draw();

    }

    private void actualizarPersonaje(Jugador jugador, Personaje personaje, float delta, boolean esJugador1, float x, float y) {
        if (personaje.getVida() <= 0) {
            if ((esJugador1 && ! this.gameOver1) || (!esJugador1 && ! this.gameOver2)) {
                if (esJugador1) this.gameOver1 = true;
                else this.gameOver2 = true;
                if(this.gameOver1 == true && this.gameOver2 == true) {
                    this.musicaPartida.cambiarMusica("derrota");
                    personaje.morir(this.stageHUD); 
                }
            }
            return;
        }
        if (personaje.getEstaAtacando()) {
            Iterator<Enemigo> iter = this.enemigos.iterator();
            while(iter.hasNext()) {
                Enemigo e = iter.next();
                if (personaje.getHitbox().overlaps(e.getHitbox()) && e.getVida() > 0) {
                    e.reducirVida();
                    e.remove();
                    if (e.getVida() <= 0) {
                        this.enemigosMuertos.add(e.getNombre());
                    }
                }
            }
        }


        boolean estaSobreElSuelo = detectarColision(new Rectangle(personaje.getX(), personaje.getY() - 1, 16, 16));

        personaje.guardarPosicionAnterior();
        personaje.actualizarGravedad(delta, estaSobreElSuelo, this.alturaMapa);

        float nuevaX = personaje.getNuevaX(delta);
        float nuevaY = personaje.getNuevaY(delta);
        float minX = this.camara.position.x - this.camara.viewportWidth / 2f;
        float maxX = this.camara.position.x + this.camara.viewportWidth / 2f - personaje.getHitbox().getWidth();

        if (nuevaX < minX) nuevaX = minX;
        if (nuevaX > maxX) nuevaX = maxX;

        if (esJugador1) {
            this.nuevaX1 = nuevaX;
            this.nuevaY1 = nuevaY;
        } else {
            this.nuevaX2 = nuevaX;
            this.nuevaY2 = nuevaY;
        }

        if (nuevaY < -190) {
            personaje.reducirVida();
        }

        Rectangle hitboxTentativaX = new Rectangle(personaje.getHitbox());
        hitboxTentativaX.setPosition(nuevaX, personaje.getY());
        boolean colisionX = detectarColision(hitboxTentativaX);

        Rectangle hitboxTentativaY = new Rectangle(personaje.getHitbox());
        hitboxTentativaY.setPosition(personaje.getX(), nuevaY);
        boolean colisionY = detectarColision(hitboxTentativaY);

        if (colisionY) {
            personaje.frenarCaida();
            personaje.setY(personaje.getPrevY());
        }

        if (!colisionX || !colisionY) {
            float finalX = !colisionX ? nuevaX : personaje.getX();
            float finalY = !colisionY ? nuevaY : personaje.getY();
            personaje.aplicarMovimiento(finalX, finalY, delta, this.anchoMapa, this.alturaMapa);
        }
        personaje.atacar(delta); 
        detectarYEliminarTile(personaje, personaje.getHitbox(), jugador, esJugador1);

        for (Enemigo e : this.enemigos) {
            Iterator<Proyectil> it = e.getBalas().iterator();
            while (it.hasNext()) {
                Proyectil b = it.next();
                if (b.getHitbox().overlaps(personaje.getHitbox())) {
                    personaje.reducirVida();
                    b.desactivar();
                    it.remove();
                }
            }
        }
    }

    private void limpiarEnemigosMuertos() {
        this.enemigos.removeIf(e -> e.getVida() <= 0);
    }

    private void actualizarHUD() {
        this.nombrePersonaje1Label.setText("Nombre: " + this.personaje1.getNombre());
        this.vidaPersonaje1Label.setText("Vida: " + this.personaje1.getVida());

        this.nombrePersonaje2Label.setText("Nombre: " + this.personaje2.getNombre());
        this.vidaPersonaje2Label.setText("Vida: " + this.personaje2.getVida());
    }

    private void restaurarEstadoCajas() {
        TiledMapTileLayer tileLayer = (TiledMapTileLayer) this.mapa.getLayers().get("cajasInteractivas");
        for (String key : this.cajasDestruidas) {
            String[] partes = key.split("_");
            int x = Integer.parseInt(partes[0]);
            int y = Integer.parseInt(partes[1]);
            tileLayer.setCell(x, y, null);
        }
    }
    private void actualizarCamara() {
        float centroX;
        float centroY;

        boolean vivo1 = this.personaje1.getVida() > 0;
        boolean vivo2 = this.personaje2.getVida() > 0;

        if (vivo1 && vivo2) {
            centroX = (this.personaje1.getX() + this.personaje2.getX()) / 2f + this.personaje1.getWidth() / 2f;
            centroY = (this.personaje1.getY() + this.personaje2.getY()) / 2f + this.personaje1.getHeight() / 2f;
        } else if (vivo1) {
            centroX = this.personaje1.getX() + this.personaje1.getWidth() / 2f;
            centroY = this.personaje1.getY() + this.personaje1.getHeight() / 2f;
        } else if (vivo2) {
            centroX = this.personaje2.getX() + this.personaje2.getWidth() / 2f;
            centroY = this.personaje2.getY() + this.personaje2.getHeight() / 2f;
        } else {
            return;
        }

        float halfWidth = this.camara.viewportWidth / 2f;
        float halfHeight = this.camara.viewportHeight / 2f;

        // 🔹 Ajuste más robusto usando MathUtils.clamp (más limpio)
        centroX = MathUtils.clamp(centroX, halfWidth, this.anchoMapa - halfWidth);
        centroY = MathUtils.clamp(centroY, halfHeight, this.alturaMapa - halfHeight);

        // 🔹 Corrección clave: si el mapa es más chico que la cámara
        if (this.anchoMapa < this.camara.viewportWidth) {
            centroX = this.anchoMapa / 2f;
        }
        if (this.alturaMapa < this.camara.viewportHeight) {
            centroY = this.alturaMapa / 2f;
        }

        // 🔹 Corrige la posición final de la cámara
        this.camara.position.set(centroX, centroY, 0);
        this.camara.update();
    }




    private void detectarYEliminarTile(Personaje personaje, Rectangle hitbox, Jugador jugador, boolean esJugador1) {
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
        if (cajaCercana && personaje.getEstaAtacando()) {
            if (this.personaje1.getVida() <= 0 && this.personaje2.getVida() > 0) {
                this.personaje1.setPosicion(this.personaje2.getX(), this.personaje2.getY());
                this.personaje1.aumentarVida();
                this.gameOver1 = false;
            } else if (personaje2.getVida() <= 0 && this.personaje1.getVida() > 0) {
                this.personaje2.setPosicion(this.personaje1.getX(), this.personaje1.getY());
                this.personaje2.aumentarVida();
                this.gameOver2 = false;
            }
            else {
                personaje = jugador.cambiarPersonaje(
                        esJugador1 ? this.nuevaX1 : this.nuevaX2,
                        esJugador1 ? this.nuevaY1 : this.nuevaY2
                );
                if (esJugador1) {
                    this.stage.getActors().removeValue(this.personaje1, true);
                    this.personaje1 = personaje;
                    this.stage.addActor(this.personaje1);
                } else {
                    this.stage.getActors().removeValue(this.personaje2, true);
                    this.personaje2 = personaje;
                    this.stage.addActor(this.personaje2);
                }

            }
            actualizarHUD();
            int ancho = tileLayer.getWidth();
            int altura = tileLayer.getHeight();
            for (int mapX = 0; mapX < ancho; mapX++) {
                for (int mapY = 0; mapY < altura; mapY++) {
                    TiledMapTileLayer.Cell cell = tileLayer.getCell(mapX, mapY);
                    if (cell != null && cell.getTile() != null) {
                        int tileId = cell.getTile().getId();
                        if (tileId != this.ID_TILE_TRANSPARENTE) {
                            this.cajasDestruidas.add(mapX + "_" + mapY);
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

    public void abrirOpciones() {
        this.juego.setScreen(new Opciones(this.juego, this, this.musicaPartida));
    }


    @Override 
    public void resize(int width, int height) {
        if (this.stage != null) {
            this.stage.getViewport().update(width, height, true);
        }
        if (this.stageHUD != null) {
            this.stageHUD.getViewport().update(width, height, true);
        }
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        this.mapa.dispose();
        this.mapRenderer.dispose();
        this.batch.dispose();
        this.stage.dispose();
        this.stageHUD.dispose(); // 👈 Liberar el Stage del HUD
    }
}
