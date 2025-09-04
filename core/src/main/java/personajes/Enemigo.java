package personajes;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import proyectiles.Proyectil;

public class Enemigo extends Personaje {

    private float rangoMovimiento = 200; 
    private float puntoInicialX;
    private boolean moviendoDerecha = true;
    private ArrayList<Proyectil> balas = new ArrayList<>();
    private float tiempoDisparo = 0;
    private final float cooldownDisparo = 1.0f; // segundos entre disparos

    public Enemigo(float x, float y) {
        super("Enemigo", 100, "ataqueEnemigo");
        cargarUbicaciones(x, y);
        this.puntoInicialX = x;
    }

    @Override
    protected void cargarTexturas() {
        // Movimiento derecha
        Array<TextureRegion> framesDerecha = new Array<>();
        for (int i = 1; i <= 4; i++) {
            framesDerecha.add(new TextureRegion(new Texture(Gdx.files.internal(
                    "imagenes/personajes/enemigo/Enemigo_Moviendose_Derecha_" + i + ".png"))));
        }
        super.animDerecha = new Animation<>(0.1f, framesDerecha, Animation.PlayMode.LOOP);

        // Movimiento izquierda
        Array<TextureRegion> framesIzquierda = new Array<>();
        for (int i = 1; i <= 4; i++) {
            framesIzquierda.add(new TextureRegion(new Texture(Gdx.files.internal(
                    "imagenes/personajes/enemigo/Enemigo_Moviendose_Izquierda_" + i + ".png"))));
        }
        super.animIzquierda = new Animation<>(0.1f, framesIzquierda, Animation.PlayMode.LOOP);

        // Quieto
        super.quietaDerecha = new TextureRegion(new Texture(Gdx.files.internal(
                "imagenes/personajes/enemigo/enemigo_quieto.png")));
        super.quietaIzquierda = new TextureRegion(new Texture(Gdx.files.internal(
                "imagenes/personajes/enemigo/enemigo_quieto.png")));
    }

    public void actualizarIA(float delta, float jugadorX) {
        // Patrulla
        float nuevaX = getX();
        if (moviendoDerecha) {
            nuevaX += getVelocidad() * delta;
            if (nuevaX > puntoInicialX + rangoMovimiento) moviendoDerecha = false;
        } else {
            nuevaX -= getVelocidad() * delta;
            if (nuevaX < puntoInicialX - rangoMovimiento) moviendoDerecha = true;
        }
        aplicarMovimiento(nuevaX, getY(), delta, 10000, 1000);

        // Actualizar balas
        Iterator<Proyectil> it = balas.iterator();
        while (it.hasNext()) {
        	Proyectil b = it.next();
            b.actualizar(delta);
            // Si sale de rango, se elimina
            if (b.getX() < 0 || b.getX() > 2000) {
                b.desactivar();
                it.remove();
            }
        }

        // Control de disparo
        tiempoDisparo += delta;
        if (tiempoDisparo >= cooldownDisparo) {
            float distancia = Math.abs(jugadorX - getX());
            if (distancia < 2 * 32) { // 2 tiles (32 px por tile)
                if ((moviendoDerecha && jugadorX > getX())) {
                    disparar("imagenes/personajes/enemigo/ataque/Bala_Derecha.png");
                    tiempoDisparo = 0;
                }
                else if ((!moviendoDerecha && jugadorX < getX())) {
                    disparar("imagenes/personajes/enemigo/ataque/Bala_Izquierda.png");
                    tiempoDisparo = 0;
                }
            }
        }
    }

    private void disparar(String ruta) {
        balas.add(new Proyectil(getX(), getY() + 16, moviendoDerecha, ruta));
    }
    
    public ArrayList<Proyectil> getBalas() {
        return balas;
    }

    private float getVelocidad() {
        return 80;
    }
}
