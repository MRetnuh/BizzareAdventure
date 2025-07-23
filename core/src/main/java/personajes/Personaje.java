package personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.Array;

public class Personaje {
    private float velocidad = 100f;

    private float x, y;
    private float estadoTiempo = 0f;
    private boolean mirandoDerecha = true;
    private boolean estaMoviendose = false;

    private Animation<TextureRegion> animDerecha;
    private Animation<TextureRegion> animIzquierda;
    private TextureRegion quietaDerecha;
    private TextureRegion quietaIzquierda;
    private Texture spriteSheet;

    public Personaje() {
        
    	spriteSheet = new Texture(Gdx.files.internal("cacas/2-sheet.png"));
    	int frameWidth = 63;
        int frameHeight = 64;
        TextureRegion[][] frames = TextureRegion.split(spriteSheet, frameWidth, frameHeight);
        // Cargar animaciones para izquierda y derecha
        TextureRegion[] framesDerecha = frames[0];
        TextureRegion[] framesIzquierda = frames[1];

        //Crear animaciones
        animDerecha = new Animation<>(0.1f, framesDerecha);
        animIzquierda = new Animation<>(0.1f, framesIzquierda);
        // Cargar imagen quieta
        quietaDerecha = new TextureRegion(new Texture(Gdx.files.internal("cacas/akame_derecha_(detenida).png")));
        quietaIzquierda = new TextureRegion(new Texture(Gdx.files.internal("cacas/akame_izquierda_(detenida).png")));

        // Posición inicial
        x = 1050;
        y = 930;
    }

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
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            y -= velocidad * delta;
            estaMoviendose = true;
        }
    }

    public void dibujar(SpriteBatch batch) {
        TextureRegion frameActual;

        if (estaMoviendose) {
        	frameActual = mirandoDerecha ? animDerecha.getKeyFrame(estadoTiempo, true) : animIzquierda.getKeyFrame(estadoTiempo, true);
        } else {
            frameActual = mirandoDerecha ? quietaDerecha : quietaIzquierda;
        }

        batch.draw(frameActual, x, y);
    }

    public void actualizarCamara(OrthographicCamera camara) {
        camara.position.set(x + 32, y + 32, 0); // centramos la cámara en el personaje
        camara.update();
    }
}
