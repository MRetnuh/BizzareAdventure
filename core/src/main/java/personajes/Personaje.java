package personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Timer;

import audios.Musica;

public abstract class Personaje {
	    private float velocidad;
	    private String nombre;
	    private int vida = 1;
	    private Texture textureDerrota;
	    private Image imageDerrota;
	    private Stage stage;
	    private Musica musicaDerrota = new Musica("derrota");
	    private boolean estaMuerto = false;
	    
	    protected float x, y;
	    protected float prevX, prevY;
	    protected float estadoTiempo = 0f;
	    protected float velocidadCaida = 0;
	    protected final float GRAVEDAD = -500;
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

    private void morir() {
        if (estaMuerto) return;

        this.vida = 0;
        estaMuerto = true;

        musicaDerrota.show();

        // FONDO NEGRO
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        Texture blackTexture = new Texture(pixmap);
        pixmap.dispose();

        Image fondoNegro = new Image(blackTexture);
        fondoNegro.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        fondoNegro.getColor().a = 0; // transparente al inicio
        stage.addActor(fondoNegro);

        fondoNegro.addAction(Actions.fadeIn(0.5f)); // oscurecer pantalla en 0.5s

        // CARTEL DE DERROTA
        textureDerrota = new Texture(Gdx.files.internal("imagenes/fondos/GameOver.png"));
        imageDerrota = new Image(textureDerrota);
        imageDerrota.setSize(200, 50); // escala inicial pequeña
        imageDerrota.setOrigin(imageDerrota.getWidth() / 2f, imageDerrota.getHeight() / 2f);
        imageDerrota.setPosition(
            (Gdx.graphics.getWidth() - imageDerrota.getWidth()) / 2f,
            (Gdx.graphics.getHeight() - imageDerrota.getHeight()) / 2f
        );
        imageDerrota.setScale(0.1f); // muy chico al principio
        imageDerrota.getColor().a = 0; // invisible al principio

        stage.addActor(imageDerrota);

        imageDerrota.addAction(Actions.sequence(
            Actions.delay(0.3f),
            Actions.parallel(
                Actions.fadeIn(0.5f),
                Actions.scaleTo(2.5f, 2.5f, 2f, Interpolation.pow2Out)
            )
        ));

        // Salir después de 8 segundos
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Gdx.app.exit();
            }
        }, 8);
    }

    public void retroceder() {
        x = prevX;
        y = prevY;
    }
    
    public void dibujar(SpriteBatch batch) {
        TextureRegion frameActual = estaMoviendose
            ? (mirandoDerecha ? animDerecha.getKeyFrame(estadoTiempo) : animIzquierda.getKeyFrame(estadoTiempo))
            : (mirandoDerecha ? quietaDerecha : quietaIzquierda);

        batch.draw(frameActual, x, y);
    }
 
    public void actualizarGravedad(float delta, boolean estaEnElSuelo, int mapHeight) {
        if (!estaEnElSuelo) {
            velocidadCaida += GRAVEDAD * delta;
            y += velocidadCaida * delta;
        } else {
            velocidadCaida = 0;
        }

        // Si cae debajo del mapa, muere
        if (y < -190) {
            morir();
        }
    }

    
    public void actualizarCamara(OrthographicCamera camara, int mapWidth, int mapHeight) {
        float camX = x + 16;
        float camY = y + 16;

        float halfWidth = camara.viewportWidth / 2f;
        float halfHeight = camara.viewportHeight / 2f;

        camX = Math.max(halfWidth, camX);
        camX = Math.min(mapWidth - halfWidth, camX);

        camY = Math.max(halfHeight, camY);
        camY = Math.min(mapHeight - halfHeight, camY);

        camara.position.set(camX, camY, 0);
        camara.update();
    }

    public void aplicarMovimiento(float nuevoX, float nuevoY, float delta, int mapWidth, int mapHeight) {
        estadoTiempo += delta;
        estaMoviendose = nuevoX != x || nuevoY != y;
        mirandoDerecha = nuevoX > x || (nuevoX == x && mirandoDerecha);

        float anchoSprite = 63;
        float altoSprite = 64;

        // Limitar horizontalmente
        nuevoX = Math.max(0, Math.min(nuevoX, mapWidth - anchoSprite));

        // Limitar solo hacia arriba, pero permitir que caiga por debajo del mapa
        nuevoY = Math.min(nuevoY, mapHeight - altoSprite);

        x = nuevoX;
        y = nuevoY;
    }

    public void guardarPosicionAnterior() {
        prevX = x;
        prevY = y;
    }
    
    public String getNombre() {
    	return this.nombre;
    }
    
    public int getVida() {
    	return this.vida;
    }
    public Rectangle getHitbox() {
        return new Rectangle(x, y, 16, 16); // O ajustalo según el tamaño de sprite real
    }
    
    public float getNuevaX(float delta) {
        float tempX = x;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) tempX += velocidad * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) tempX -= velocidad * delta;
        return tempX;
    }

    public float getNuevaY(float delta) {
        float tempY = y;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) tempY += velocidad * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) tempY -= velocidad * delta;
        return tempY;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}