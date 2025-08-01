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

import audios.EfectoSonido;
import audios.Musica;

public abstract class Personaje {
	    private float velocidad;
	    private String nombre;
	    private int vida = 1;
	    private Texture texturaDerrota;
	    private Image imagenDerrota;
	    private Stage stage;
	    private Musica musicaDerrota = new Musica("derrota");
	    private boolean estaMuerto = false;
	    private int habilidadEspecial = 1;
	    private String nombreAtaque;
	    
	    protected float x, y;
	    protected float prevX, prevY;
	    protected float estadoTiempo = 0f;
	    protected float velocidadCaida = 0;
	    protected final float GRAVEDAD = -500;
	    protected boolean mirandoDerecha = true;
	    protected boolean estaMoviendose = false;
	    protected Animation<TextureRegion> animDerecha;
	    protected Animation<TextureRegion> animIzquierda;
	    protected Animation<TextureRegion> animAtaqueDerecha;
	    protected Animation<TextureRegion> animAtaqueIzquierda;
	    protected TextureRegion quietaDerecha;
	    protected TextureRegion quietaIzquierda;
	    protected boolean estaAtacando = false;
	    
    public Personaje(String nombre, int velocidad, String nombreAtaque) {
        this.nombre = nombre;
        this.velocidad = velocidad;
        this.nombreAtaque = nombreAtaque;
        cargarTexturas(); 
        x = 200;
        y = 930;
    }

    protected abstract void cargarTexturas();

    private void morir() {
        if (estaMuerto) return;

        this.vida = 0;
        estaMuerto = true;

        musicaDerrota.show();

   
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        Texture blackTexture = new Texture(pixmap);
        pixmap.dispose();

        Image fondoNegro = new Image(blackTexture);
        fondoNegro.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        fondoNegro.getColor().a = 0;
        stage.addActor(fondoNegro);

        fondoNegro.addAction(Actions.fadeIn(0.5f)); 

      
        texturaDerrota = new Texture(Gdx.files.internal("imagenes/fondos/GameOver.png"));
        imagenDerrota = new Image(texturaDerrota);
        imagenDerrota.setSize(200, 50); 
        imagenDerrota.setOrigin(imagenDerrota.getWidth() / 2f, imagenDerrota.getHeight() / 2f);
        imagenDerrota.setPosition(
            (Gdx.graphics.getWidth() - imagenDerrota.getWidth()) / 2f,
            (Gdx.graphics.getHeight() - imagenDerrota.getHeight()) / 2f
        );
        imagenDerrota.setScale(0.1f); 
        imagenDerrota.getColor().a = 0; 

        stage.addActor(imagenDerrota);

        imagenDerrota.addAction(Actions.sequence(
            Actions.delay(0.3f),
            Actions.parallel(
                Actions.fadeIn(0.5f),
                Actions.scaleTo(2.5f, 2.5f, 2f, Interpolation.pow2Out)
            )
        ));

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
    
    public void dibujar(SpriteBatch batch, float delta) {
        estadoTiempo += delta;

        TextureRegion frame;

        if (estaAtacando) {
            frame = mirandoDerecha ? animAtaqueDerecha.getKeyFrame(estadoTiempo, false)
                                   : animAtaqueIzquierda.getKeyFrame(estadoTiempo, false);

            if ((mirandoDerecha && animAtaqueDerecha.isAnimationFinished(estadoTiempo)) ||
                (!mirandoDerecha && animAtaqueIzquierda.isAnimationFinished(estadoTiempo))) {
                estaAtacando = false;
                estadoTiempo = 0; // reiniciar tiempo
            }

        } else if (estaMoviendose) {
            frame = mirandoDerecha ? animDerecha.getKeyFrame(estadoTiempo, true)
                                   : animIzquierda.getKeyFrame(estadoTiempo, true);
        } else {
            frame = mirandoDerecha ? quietaDerecha : quietaIzquierda;
        }

        batch.draw(frame, x, y);
    }

 
    public void actualizarGravedad(float delta, boolean estaEnElSuelo, int mapHeight) {
        if (!estaEnElSuelo) {
            velocidadCaida += GRAVEDAD * delta;
            y += velocidadCaida * delta;
        } else {
            velocidadCaida = 0;
        }

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
    
    private void atacar(float delta) {
        float tiempoAtaque = 0f;
		if (Gdx.input.isKeyPressed(Input.Keys.M) && !estaAtacando) {
            estaAtacando = true;
            tiempoAtaque = 0;
            EfectoSonido efectoAtaque = new EfectoSonido(this.nombreAtaque);
            efectoAtaque.reproducir(); // ⬅️ Solo una vez al iniciar el ataque
        }

        if (estaAtacando) {
            tiempoAtaque += delta;

            if (tiempoAtaque > animAtaqueDerecha.getAnimationDuration()) {
                estaAtacando = false;
                tiempoAtaque = 0f;
            }
        }
    }


    public void aplicarMovimiento(float nuevoX, float nuevoY, float delta, int mapWidth, int mapHeight) {
        estadoTiempo += delta;
        estaMoviendose = nuevoX != x || nuevoY != y;
        mirandoDerecha = nuevoX > x || (nuevoX == x && mirandoDerecha);

        float anchoSprite = 63;
        float altoSprite = 64;

        nuevoX = Math.max(0, Math.min(nuevoX, mapWidth - anchoSprite));

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
        return new Rectangle(x, y, 16, 16); 
    }
    
    public float getNuevaX(float delta) {
        float tempX = x;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) tempX += velocidad * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) tempX -= velocidad * delta;
        atacar(delta);
        return tempX;
    }

    public float getNuevaY(float delta) {
        float tempY = y;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) tempY += velocidad * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) tempY -= velocidad * delta;
        atacar(delta);
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
    public void frenarCaida() {
        this.velocidadCaida = 0;
    }
   
    public float getPrevY() {
        return prevY;
    }
    public void setY(float prevY) {
    	y = prevY;
    }
}