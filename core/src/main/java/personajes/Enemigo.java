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
    private Personaje objetivoActual = null;
    private float tiempoSinVerJugador = 0f;
    private final float TIEMPOPARAOLVIDAR = 1.0f; 
    private final float TOLERANCIAVERTICAL = 100f; 

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
            super.estaMoviendose = false;
            super.tiempoDisparo += delta;
            this.rangoVision = 500;

            this.mirandoDerecha = this.objetivoActual.getX() > super.getX();
            super.frame = this.mirandoDerecha ? super.quietaDerecha : super.quietaIzquierda;

            if (super.tiempoDisparo >= super.COOLDOWNDISPARO) {
                dispararHaciaObjetivo(volumen);
                super.tiempoDisparo = 0;
            }
        } else {
            super.estaMoviendose = true;
            this.rangoVision = 250;
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

        if (this.objetivoActual != null) {
            if (this.objetivoActual.getVida() > 0 && detectarRangoConTolerancia(this.objetivoActual)) {
                this.tiempoSinVerJugador = 0;
                return;
            } else {
            	
                this.tiempoSinVerJugador += Gdx.graphics.getDeltaTime();
                if (this.tiempoSinVerJugador >= this.TIEMPOPARAOLVIDAR) {
                    this.objetivoActual = null;
                    this.tiempoSinVerJugador = 0;
                }
                return;
            }
        }
        
        if (j1Vivo && detectarRangoConTolerancia(j1)) nuevoObjetivo = j1;
        else if (j2Vivo && detectarRangoConTolerancia(j2)) nuevoObjetivo = j2;

        this.objetivoActual = nuevoObjetivo;
    }


    private boolean detectarRangoConTolerancia(Personaje jugador) {
        float distanciaX = Math.abs(jugador.getX() - super.getX());
        float distanciaY = Math.abs(jugador.getY() - super.getY());

        return distanciaX <= this.rangoVision && distanciaY <= this.TOLERANCIAVERTICAL;
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
        if (this.objetivoActual.getVida() <= 0) {
            this.objetivoActual = null;
            return;
        }
        boolean objetivoALaDerecha = this.objetivoActual.getX() > super.getX();
        this.moviendoDerecha = objetivoALaDerecha;

        String ruta = objetivoALaDerecha ?
                "imagenes/personajes/enemigo/ataque/Bala_Derecha.png" :
                "imagenes/personajes/enemigo/ataque/Bala_Izquierda.png";

        disparar(ruta, volumen);
    }
    
    private void disparar(String ruta, float volumen) {
        this.balas.add(new Proyectil(getX(), getY() + 16, this.moviendoDerecha, ruta));
        EfectoSonido.reproducir("Disparo", volumen);
    }

    private float getVelocidad() {
        return 80;
    }
}
