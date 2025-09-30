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
    private Stage stage;        // Stage para el MUNDO (Personajes, Enemigos - usa cámara móvil)
    private Stage stageHUD;     // Stage para el HUD (Labels/Tablas - usa cámara fija)
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

    public Partida(Game juego, Musica musica) {
        this.juego = juego;
        this.musicaPartida = musica;
    }

    private boolean enemigosCreados = false;

    @Override
    public void show() {
        this.camara = new OrthographicCamera();
        this.camara.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        this.batch = new SpriteBatch();
        
        // 👈 STAGE DEL MUNDO: Para los personajes. Usa ScreenViewport que se adaptará al tamaño
        // El Stage.draw() usará la ProjectionMatrix del batch (establecida con this.camara.combined)
        this.stage = new Stage(new ScreenViewport(this.camara), this.batch); 

        // 👈 STAGE DEL HUD: Para las tablas. Usa ScreenViewport para mantenerse fijo en la pantalla.
        this.stageHUD = new Stage(new ScreenViewport(), this.batch);

        this.mapa = new TmxMapLoader().load("mapacorregido.tmx");
        this.mapRenderer = new OrthogonalTiledMapRenderer(this.mapa);
        this.anchoMapa = mapa.getProperties().get("width", Integer.class) * this.mapa.getProperties().get("tilewidth", Integer.class);
        this.alturaMapa = mapa.getProperties().get("height", Integer.class) * this.mapa.getProperties().get("tileheight", Integer.class);
        restaurarEstadoCajas();

        // 👇 Solo crear enemigos una vez
        if (!enemigosCreados) {
            String[] idsEnemigos = {"enemigo1", "enemigo2", "enemigo3"};
            float[][] posiciones = {
                    {400, 928},
                    {800, 928},
                    {1000, 928}
            };

            for (int i = 0; i < idsEnemigos.length; i++) {
                String id = idsEnemigos[i];
                if (!enemigosMuertos.contains(id)) {
                    enemigos.add(new Enemigo(id, posiciones[i][0], posiciones[i][1]));
                }
            }
            enemigosCreados = true;
        }

        // Generar personajes para ambos jugadores
        if (!this.jugador1.getPartidaEmpezada()) {
            this.jugador1.generarPersonajeAleatorio();
        }
        if (!this.jugador2.getPartidaEmpezada()) {
            this.jugador2.generarPersonajeAleatorio();
        }

        this.personaje1 = this.jugador1.getPersonajeElegido();
        this.personaje2 = this.jugador2.getPersonajeElegido();

        // 👈 AÑADIR PERSONAJES AL STAGE DEL MUNDO
        this.stage.addActor(this.personaje1);
        this.stage.addActor(this.personaje2);
        for (Enemigo enemigo : enemigos) {
            this.stage.addActor(enemigo);
        }
        this.inputController = new InputController();
        Gdx.input.setInputProcessor(inputController);

        this.skin = new Skin(Gdx.files.internal("uiskin.json"));

        // HUD Jugador 1
        nombrePersonaje1Label = new Label("Nombre: " + personaje1.getNombre(), EstiloTexto.ponerEstiloLabel(40, Color.RED));
        vidaPersonaje1Label = new Label("Vida: " + personaje1.getVida(), EstiloTexto.ponerEstiloLabel(40, Color.RED));

        // HUD Jugador 2
        nombrePersonaje2Label = new Label("Nombre: " + personaje2.getNombre(), EstiloTexto.ponerEstiloLabel(40, Color.BLUE));
        vidaPersonaje2Label = new Label("Vida: " + personaje2.getVida(), EstiloTexto.ponerEstiloLabel(40, Color.BLUE));

        Table table1 = new Table();
        Table table2 = new Table();
        table1.left().top();
        table2.right().top();
        table1.add(nombrePersonaje1Label).size(350, 50).padBottom(5).row();
        table1.add(vidaPersonaje1Label).size(350, 50);
        table2.add(nombrePersonaje2Label).size(350, 50).padBottom(5).row();
        table2.add(vidaPersonaje2Label).size(350, 50);

        Container<Table> contenedor1 = new Container<>(table1);
        Container<Table> contenedor2 = new Container<>(table2);
        contenedor1.setSize(400, 130);
        contenedor2.setSize(400, 130);
        contenedor1.setBackground(skin.getDrawable("default-round"));
        contenedor2.setBackground(skin.getDrawable("default-round"));
        contenedor1.setPosition(0, Gdx.graphics.getHeight() - contenedor1.getHeight());
        contenedor2.setPosition(Gdx.graphics.getWidth() - contenedor2.getWidth(), Gdx.graphics.getHeight() - contenedor2.getHeight());

        // 👈 Los contenedores del HUD son Actores y se añaden al STAGE DEL HUD
        this.stageHUD.addActor(contenedor1);
        this.stageHUD.addActor(contenedor2);
    }


    @Override
    public void render(float delta) {
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
        actualizarPersonaje(jugador1, personaje1, delta, true, nuevaX1, nuevaY1);
        actualizarPersonaje(jugador2, personaje2, delta, false, nuevaX2, nuevaY2);
        actualizarCamara();
        actualizarHUD();

        limpiarEnemigosMuertos();

        // 1. DIBUJAR EL MAPA (Usa this.camara)
        this.mapRenderer.setView(this.camara);
        this.mapRenderer.render();

        // 2. DIBUJAR ENTIDADES DEL MUNDO NO ACTORES (Proyectiles, Enemigos, etc.)
        // Sincronizar la matriz del batch con la cámara del mundo
        this.batch.setProjectionMatrix(this.camara.combined);
        this.batch.begin();
        
        if(!gameOver1 || !gameOver2) {
            for (Enemigo enemigo : enemigos) {
                if (enemigo.getVida() > 0) {

                    for (Proyectil b : enemigo.getBalas()) {
                        this.stage.addActor(b);
                    }

                    enemigo.actualizarIA(delta, (personaje1.getX() + personaje2.getX()) / 2f, this.musicaPartida.getVolumen());
                }
            }
        }
        this.batch.end();
        
        // 3. DIBUJAR PERSONAJES (Stage del Mundo)
        // El stage ya usa el batch que tiene la matriz de la cámara del mundo
        this.stage.act(delta);
        this.stage.draw();

        // 4. DIBUJAR HUD (Stage del HUD)
        // El stageHUD usa su propio Viewport/Cámara fija, por lo que no se mueve con el mundo.
        this.stageHUD.act(delta);
        this.stageHUD.draw();

    }

    private void actualizarPersonaje(Jugador jugador, Personaje personaje, float delta, boolean esJugador1, float x, float y) {
        if (personaje.getVida() <= 0) {
            if ((esJugador1 && !gameOver1) || (!esJugador1 && !gameOver2)) {
                if (esJugador1) gameOver1 = true;
                else gameOver2 = true;
                if(this.gameOver1 == true && this.gameOver2 == true) {
                    musicaPartida.cambiarMusica("derrota");
                    // Usar el Stage del MUNDO para la animación de morir/GameOver
                    personaje.morir(this.stageHUD); 
                }
            }
            return;
        }
        if (personaje.getEstaAtacando()) {
            Iterator<Enemigo> iter = enemigos.iterator();
            while(iter.hasNext()) {
                Enemigo e = iter.next();
                if (personaje.getHitbox().overlaps(e.getHitbox()) && e.getVida() > 0) {
                    e.reducirVida();
                    if (e.getVida() <= 0) {
                        enemigosMuertos.add(e.getNombre());
                    }
                }
            }
        }


        boolean estaSobreElSuelo = detectarColision(new Rectangle(personaje.getX(), personaje.getY() - 1, 16, 16));

        personaje.guardarPosicionAnterior();
        personaje.actualizarGravedad(delta, estaSobreElSuelo, alturaMapa);

        float nuevaX = personaje.getNuevaX(delta);
        float nuevaY = personaje.getNuevaY(delta);
        float minX = camara.position.x - camara.viewportWidth / 2f;
        float maxX = camara.position.x + camara.viewportWidth / 2f - personaje.getHitbox().getWidth();

        if (nuevaX < minX) nuevaX = minX;
        if (nuevaX > maxX) nuevaX = maxX;

        if (esJugador1) {
            nuevaX1 = nuevaX;
            nuevaY1 = nuevaY;
        } else {
            nuevaX2 = nuevaX;
            nuevaY2 = nuevaY;
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
            personaje.aplicarMovimiento(finalX, finalY, delta, anchoMapa, alturaMapa);
        }
        personaje.atacar(delta); 
        detectarYEliminarTile(personaje, personaje.getHitbox(), jugador, esJugador1);

        for (Enemigo e : enemigos) {
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
        enemigos.removeIf(e -> e.getVida() <= 0);
    }

    private void actualizarHUD() {
        nombrePersonaje1Label.setText("Nombre: " + personaje1.getNombre());
        vidaPersonaje1Label.setText("Vida: " + personaje1.getVida());

        nombrePersonaje2Label.setText("Nombre: " + personaje2.getNombre());
        vidaPersonaje2Label.setText("Vida: " + personaje2.getVida());
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

 // Partida.java - Reemplazar el método actualizarCamara
    private void actualizarCamara() {
        float centroX;
        float centroY;

        boolean vivo1 = personaje1.getVida() > 0;
        boolean vivo2 = personaje2.getVida() > 0;

        // Si ambos vivos → calcular el centro
        if (vivo1 && vivo2) {
            // Calcula el centro entre los dos
            centroX = (personaje1.getX() + personaje2.getX()) / 2f + personaje1.getWidth() / 2f; 
            centroY = (personaje1.getY() + personaje2.getY()) / 2f + personaje1.getHeight() / 2f; 
        }
        // Si solo vive jugador 1 → seguirlo
        else if (vivo1) {
            // Centrar la cámara en personaje 1
            centroX = personaje1.getX() + personaje1.getWidth() / 2f;
            centroY = personaje1.getY() + personaje1.getHeight()/ 2f;
        }
        // Si solo vive jugador 2 → seguirlo
        else if (vivo2) {
            // Centrar la cámara en personaje 2
            centroX = personaje2.getX() + personaje2.getWidth()/ 2f;
            centroY = personaje2.getY() + personaje2.getHeight() / 2f;
        }
        // Si ambos muertos → no mover la cámara
        else {
            return;
        }

        // Mitad del tamaño visible de la cámara
        float halfWidth = camara.viewportWidth / 2f;
        float halfHeight = camara.viewportHeight / 2f;

        // Limitar cámara dentro de los bordes del mapa
        // 🛑 IMPORTANTE: Asegúrate de que anchoMapa y alturaMapa están correctos
        centroX = Math.max(halfWidth, Math.min(centroX, anchoMapa - halfWidth));
        centroY = Math.max(halfHeight, Math.min(centroY, alturaMapa - halfHeight));

        // Si la altura del mapa es menor que la altura de la cámara, fíjate en el centro del mapa.
        if (alturaMapa < camara.viewportHeight) {
            centroY = alturaMapa / 2f;
        }

        camara.position.set(centroX, centroY, 0);
        camara.update();
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
            if (personaje1.getVida() <= 0 && personaje2.getVida() > 0) {
                personaje1.setPosicion(personaje2.getX(), personaje2.getY());
                personaje1.aumentarVida();
                gameOver1 = false;
            } else if (personaje2.getVida() <= 0 && personaje1.getVida() > 0) {
                personaje2.setPosicion(personaje1.getX(), personaje1.getY());
                personaje2.aumentarVida();
                gameOver2 = false;
            }
            else {
                personaje = jugador.cambiarPersonaje(
                        esJugador1 ? nuevaX1 : nuevaX2,
                        esJugador1 ? nuevaY1 : nuevaY2
                );
                if (esJugador1) {
                    // Reemplazar el actor en el Stage del MUNDO si cambia de personaje
                    this.stage.getActors().removeValue(personaje1, true);
                    personaje1 = personaje;
                    this.stage.addActor(personaje1);
                } else {
                    // Reemplazar el actor en el Stage del MUNDO si cambia de personaje
                    this.stage.getActors().removeValue(personaje2, true);
                    personaje2 = personaje;
                    this.stage.addActor(personaje2);
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
        juego.setScreen(new Opciones(juego, this, musicaPartida));
    }


    @Override 
    public void resize(int width, int height) {
        // Asegúrate de actualizar el viewport de AMBOS Stages
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
