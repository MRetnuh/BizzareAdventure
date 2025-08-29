package personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Enemigo extends Personaje {

    private float rangoMovimiento = 200; 
    private float puntoInicialX;
    private boolean moviendoDerecha = true;

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
        // Patrulla simple
        float nuevaX = getX();
        if (moviendoDerecha) {
            nuevaX += getVelocidad() * delta;
            if (nuevaX > puntoInicialX + rangoMovimiento) moviendoDerecha = false;
        } else {
            nuevaX -= getVelocidad() * delta;
            if (nuevaX < puntoInicialX - rangoMovimiento) moviendoDerecha = true;
        }
        aplicarMovimiento(nuevaX, getY(), delta, 10000, 1000);

        // Ataque si jugador está cerca
        if (Math.abs(jugadorX - getX()) < 50 && !getEstaAtacando()) {
            iniciarAtaque();
        }
    }

    private float getVelocidad() {
        return 80;
    }
}
