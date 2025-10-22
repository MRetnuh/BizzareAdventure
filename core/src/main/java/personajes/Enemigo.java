package personajes;

import java.util.Iterator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import audios.EfectoSonido;
import proyectiles.Proyectil;
import niveles.NivelBase;

public class Enemigo extends Personaje {

    private float rangoMovimiento = 200;
    private float rangoVision = 250;
    private float puntoInicialX;
    private float tiempoDisparo = 0;
    private final float cooldownDisparo = 1.0f;
    private Personaje objetivoActual = null;

    public Enemigo(String nombre, float x, float y) {
        super(nombre, 100, "ataqueEnemigo", 1, TipoAtaque.DISTANCIA);
        setPosition(x, y);
        this.puntoInicialX = x;
    }

    @Override
    protected void cargarTexturas() {
        Array<TextureRegion> framesDerecha = new Array<>();
        for (int i = 1; i <= 4; i++) {
            framesDerecha.add(new TextureRegion(new Texture(Gdx.files.internal(
                    "imagenes/personajes/enemigo/Enemigo_Moviendose_Derecha_" + i + ".png"))));
        }
        super.animDerecha = new Animation<>(0.1f, framesDerecha, Animation.PlayMode.LOOP);

        Array<TextureRegion> framesIzquierda = new Array<>();
        for (int i = 1; i <= 4; i++) {
            framesIzquierda.add(new TextureRegion(new Texture(Gdx.files.internal(
                    "imagenes/personajes/enemigo/Enemigo_Moviendose_Izquierda_" + i + ".png"))));
        }
        super.animIzquierda = new Animation<>(0.1f, framesIzquierda, Animation.PlayMode.LOOP);

        super.quietaDerecha = new TextureRegion(new Texture(Gdx.files.internal(
                "imagenes/personajes/enemigo/enemigo_quieto_derecha.png")));
        super.quietaIzquierda = new TextureRegion(new Texture(Gdx.files.internal(
                "imagenes/personajes/enemigo/enemigo_quieto_izquierda.png")));
    }

    public void actualizarIA(float delta, Personaje jugador1, Personaje jugador2, float volumen, NivelBase nivel)
    {
        seleccionarObjetivo(jugador1, jugador2);

        if (this.objetivoActual != null) {
            this.estaMoviendose = false;
            this.tiempoDisparo += delta;

            this.mirandoDerecha = this.objetivoActual.getX() > super.getX();
            super.frame = this.mirandoDerecha ? super.quietaDerecha : super.quietaIzquierda;

            if (this.tiempoDisparo >= this.cooldownDisparo) {
                dispararHaciaObjetivo(volumen);
                this.tiempoDisparo = 0;
            }
        } else {
            this.estaMoviendose = true;
            patrullar(delta, nivel);
        }

        Iterator<Proyectil> it = this.balas.iterator();
        while (it.hasNext()) {
            Proyectil b = it.next();
            b.mover(delta, nivel, this);
            if (!b.isActivo()) it.remove();
        }
    }

    private void seleccionarObjetivo(Personaje j1, Personaje j2) {
        Personaje nuevoObjetivo = null;

        boolean j1Vivo = j1 != null && j1.getVida() > 0;
        boolean j2Vivo = j2 != null && j2.getVida() > 0;

        if (this.objetivoActual != null && this.objetivoActual.getVida() > 0) return;

        if (j1Vivo && detectarRango(j1)) nuevoObjetivo = j1;
        else if (j2Vivo && detectarRango(j2)) nuevoObjetivo = j2;

        this.objetivoActual = nuevoObjetivo;
    }

    private boolean detectarRango(Personaje jugador) {
        float distanciaX = jugador.getX() - super.getX();
        if (this.moviendoDerecha)
            return distanciaX > 0 && distanciaX <= this.rangoVision;
        else
            return distanciaX < 0 && Math.abs(distanciaX) <= this.rangoVision;
    }

    private void patrullar(float delta, NivelBase nivel) {
        float nuevaX = super.getX() + (this.moviendoDerecha ? getVelocidad() : -getVelocidad()) * delta;
        Rectangle hitbox = new Rectangle(nuevaX, super.getY(), getWidth(), getHeight());

        if (!nivel.detectarColision(hitbox)) {
            super.aplicarMovimiento(nuevaX, super.getY(), delta, 10000, 1000);
        } else {
            this.moviendoDerecha = !this.moviendoDerecha;
        }

        if (super.getX() > this.puntoInicialX + this.rangoMovimiento) this.moviendoDerecha = false;
        if (super.getX() < this.puntoInicialX - this.rangoMovimiento) this.moviendoDerecha = true;
    }

    private void dispararHaciaObjetivo(float volumen) {
        if (this.objetivoActual == null) return;

        boolean objetivoALaDerecha = this.objetivoActual.getX() > super.getX();
        this.moviendoDerecha = objetivoALaDerecha;

        String ruta = objetivoALaDerecha ?
                "imagenes/personajes/enemigo/ataque/Bala_Derecha.png" :
                "imagenes/personajes/enemigo/ataque/Bala_Izquierda.png";

        disparar(ruta, volumen);
    }
    
    private void disparar(String ruta, float volumen) {
        this.balas.add(new Proyectil(getX(), getY() + 16, this.moviendoDerecha, ruta));
        EfectoSonido.reproducir("disparo", volumen);
    }

    private float getVelocidad() {
        return 80;
    }
}
