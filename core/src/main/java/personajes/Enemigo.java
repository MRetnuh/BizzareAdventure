package personajes;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import audios.EfectoSonido;
import proyectiles.Proyectil;

public class Enemigo extends Personaje {

    private float rangoMovimiento = 200; 
    private float rangoVision = 250; // distancia en la que ve al jugador
    private float puntoInicialX;
    private boolean moviendoDerecha = true;
    private boolean atacando = false; // nuevo flag
    private ArrayList<Proyectil> balas = new ArrayList<>();
    private float tiempoDisparo = 0;
    private final float cooldownDisparo = 1.0f; // segundos entre disparos
    private Personaje objetivoActual = null; // jugador que está atacando

    public Enemigo(String nombre, float x, float y) {
        super(nombre, 100, "ataqueEnemigo", 1);
        setPosition(x, y);
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
                "imagenes/personajes/enemigo/enemigo_quieto_derecha.png")));
        super.quietaIzquierda = new TextureRegion(new Texture(Gdx.files.internal(
                "imagenes/personajes/enemigo/enemigo_quieto_izquierda.png")));
    }

    // 🔹 Método actualizado para IA con dos jugadores
    public void actualizarIA(float delta, Personaje jugador1, Personaje jugador2, float volumen) {
        seleccionarObjetivo(jugador1, jugador2);

        if (this.objetivoActual != null) {
            // El jugador está en rango → quedarse quieto y disparar
            this.atacando = true;
            this.estaMoviendose = false; // 👈 CLAVE
            this.tiempoDisparo += delta;
            if (this.objetivoActual.getX() > super.getX()) {
                super.frame = super.quietaDerecha;
            } else {
                super.frame = super.quietaIzquierda;
            }
            // Determinar hacia dónde mira
            this.mirandoDerecha = this.objetivoActual.getX() > super.getX();

            if (this.tiempoDisparo >= this.cooldownDisparo) {
                dispararHaciaObjetivo(volumen);
                this.tiempoDisparo = 0;
            }
        } else {
            // Patrulla normal si no hay jugador vivo a la vista
            this.atacando = false;
            this.estaMoviendose = true; // 👈 Vuelve a animarse al patrullar
            patrullar(delta);
        }

        // Actualizar balas
        Iterator<Proyectil> it = this.balas.iterator();
        while (it.hasNext()) {
            Proyectil b = it.next();
            if (b.getX() < 0 || b.getX() > 5000) {
                b.desactivar();
                it.remove();
            }
        }
    }


    // 🔹 Determina qué jugador atacar
    private void seleccionarObjetivo(Personaje j1, Personaje j2) {
        Personaje nuevoObjetivo = null;

        boolean j1Vivo = j1 != null && j1.getVida() > 0;
        boolean j2Vivo = j2 != null && j2.getVida() > 0;

        // Si ya tiene objetivo y sigue vivo, mantenerlo
        if (this.objetivoActual != null && this.objetivoActual.getVida() > 0) return;

        // Si no tiene, buscar uno nuevo
        if (j1Vivo && detectarRango(j1)) nuevoObjetivo = j1;
        else if (j2Vivo && detectarRango(j2)) nuevoObjetivo = j2;

        this.objetivoActual = nuevoObjetivo;
    }

    private boolean detectarRango(Personaje jugador) {
        float distancia = Math.abs(jugador.getX() - super.getX());
        return distancia <= this.rangoVision;
    }

    // 🔹 Patrullaje normal
    private void patrullar(float delta) {
        float nuevaX = super.getX();
        if (this.moviendoDerecha) {
            nuevaX += getVelocidad() * delta;
            if (nuevaX > this.puntoInicialX + this.rangoMovimiento)
                this.moviendoDerecha = false;
        } else {
            nuevaX -= getVelocidad() * delta;
            if (nuevaX < this.puntoInicialX - this.rangoMovimiento)
                this.moviendoDerecha = true;
        }
        super.aplicarMovimiento(nuevaX, super.getY(), delta, 10000, 1000);
    }

    // 🔹 Disparo hacia el jugador objetivo
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

    public ArrayList<Proyectil> getBalas() {
        return this.balas;
    }

    private float getVelocidad() {
        return 80;
    }
}
