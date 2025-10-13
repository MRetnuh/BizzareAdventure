package juego;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import personajes.Enemigo;
import personajes.Personaje;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public abstract class NivelBase {

    protected TiledMap mapa;
    protected List<Enemigo> enemigos;
    protected float startX;
    protected float startY;

    public NivelBase() {
        this.enemigos = new ArrayList<>();
    }

    protected abstract String getNombreMapa();
    protected abstract void inicializarPropiedadesDelNivel();
    public abstract boolean haFinalizado(float nuevaX1, float nuevaY1, float nuevaX2, float nuevaY2);

    public void inicializar() {
        // Llama a la inicialización de propiedades ANTES de cargar el mapa y enemigos
        inicializarPropiedadesDelNivel();

        // 1. Carga del mapa
        String nombreMapa = getNombreMapa();
        if (nombreMapa != null && !nombreMapa.isEmpty()) {
            this.mapa = new TmxMapLoader().load(nombreMapa);
        }
    }

    public void actualizar(float delta, Personaje personaje1, Personaje personaje2, Partida partida) {
        Iterator<Enemigo> iter = this.enemigos.iterator();
        while (iter.hasNext()) {
            Enemigo enemigo = iter.next();
            if (enemigo.getVida() > 0) {
                enemigo.actualizarIA(delta, personaje1, personaje2, partida.getMusicaPartida().getVolumen(), partida);
            } else {
                iter.remove();
            }
        }
    }

    public void render(float delta) {
    }

    public TiledMap getMapa() { return this.mapa; }
    public List<Enemigo> getEnemigos() { return this.enemigos; }
    public float getStartX() { return this.startX; }
    public float getStartY() { return this.startY; }

    public void dispose() {
        if (this.mapa != null) {
            this.mapa.dispose();
        }
    }
}