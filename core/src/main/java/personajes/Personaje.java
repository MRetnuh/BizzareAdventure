package personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.Array;

public abstract class Personaje {
    private float velocidad;
    private String nombre;
    private int vida = 1;
    
    protected float x, y;
    protected float estadoTiempo = 0f;
    protected boolean mirandoDerecha = true;
    protected boolean estaMoviendose = false;
    protected Animation<TextureRegion> animDerecha;
    protected Animation<TextureRegion> animIzquierda;
    protected TextureRegion quietaDerecha;
    protected TextureRegion quietaIzquierda;

    public Personaje(String nombre, int velocidad) {
        this.nombre = nombre;
        this.velocidad = velocidad;
        cargarTexturas(); // ← Cada subclase implementa esto
        x = 1050;
        y = 930;
    }

    // 👇 Método abstracto que cada hijo implementa
    protected abstract void cargarTexturas();

    public void mover(float delta) {
        estadoTiempo += delta;
        estaMoviendose = false;

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += velocidad * delta;
            mirandoDerecha = true;
            estaMoviendose = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= velocidad * delta;
            mirandoDerecha = false;
            estaMoviendose = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            y += velocidad * delta;
            estaMoviendose = true;
        }
    }

    public void dibujar(SpriteBatch batch) {
        TextureRegion frameActual = estaMoviendose
            ? (mirandoDerecha ? animDerecha.getKeyFrame(estadoTiempo) : animIzquierda.getKeyFrame(estadoTiempo))
            : (mirandoDerecha ? quietaDerecha : quietaIzquierda);

        batch.draw(frameActual, x, y);
    }

    public void actualizarCamara(OrthographicCamera camara) {
        camara.position.set(x + 16, y + 16, 0);
        camara.update();
    }
    
    public String getNombre() {
    	return this.nombre;
    }
    
    public int getVida() {
    	return this.vida;
    }
}
