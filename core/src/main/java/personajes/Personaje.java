package personajes;

import com.badlogic.gdx.Gdx;
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

public abstract class Personaje {
	    private float velocidad;
	    private String nombre;
	    private int vida;
	    private Texture texturaDerrota;
	    private Image imagenDerrota;
	    private Stage stage;
	    private int habilidadEspecial = 1;
	    private String nombreAtaque;
	    private boolean estaAtacando = false;
	    private boolean mirandoDerecha = true;
	    private boolean estaMoviendose = false;
	    private boolean estaSaltando = false;
	    private boolean moviendoDerecha = false;
	    private boolean moviendoIzquierda = false;
	    private final float GRAVEDAD = -500;
	    private float x, y;
	    private float prevX, prevY;
        private float estadoTiempo = 0f;
        private float velocidadCaida = 0;
	    
	    protected Animation<TextureRegion> animDerecha;
	    protected Animation<TextureRegion> animIzquierda;
	    protected Animation<TextureRegion> animAtaqueDerecha;
	    protected Animation<TextureRegion> animAtaqueIzquierda;
	    protected TextureRegion quietaDerecha;
	    protected TextureRegion quietaIzquierda;
		private float tiempoAtaque = 0f;
	    
	    
    public Personaje(String nombre, int velocidad, String nombreAtaque, int vida) {
        this.nombre = nombre;
        this.velocidad = velocidad;
        this.nombreAtaque = nombreAtaque;
        this.vida = vida;
        cargarTexturas(); 
        x = 200;
        y = 930;
    }

    protected abstract void cargarTexturas();

    public void cargarUbicaciones(float x, float y) {
    	this.x = x;
    	this.y = y;
    }
    
    public void morir(Stage stage) {
    	this.stage = stage;
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        Texture blackTexture = new Texture(pixmap);
        pixmap.dispose();

        Image fondoNegro = new Image(blackTexture);
        fondoNegro.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        fondoNegro.getColor().a = 0;
        this.stage.addActor(fondoNegro);

        fondoNegro.addAction(Actions.fadeIn(0.5f)); 

      
        this.texturaDerrota = new Texture(Gdx.files.internal("imagenes/fondos/GameOver.png"));
        this.imagenDerrota = new Image(texturaDerrota);
        this.imagenDerrota.setSize(200, 50); 
        this.imagenDerrota.setOrigin(imagenDerrota.getWidth() / 2f, imagenDerrota.getHeight() / 2f);
        this.imagenDerrota.setPosition(
            (Gdx.graphics.getWidth() - imagenDerrota.getWidth()) / 2f,
            (Gdx.graphics.getHeight() - imagenDerrota.getHeight()) / 2f
        );
        this.imagenDerrota.setScale(0.1f); 
        this.imagenDerrota.getColor().a = 0; 

        this.stage.addActor(imagenDerrota);

        this.imagenDerrota.addAction(Actions.sequence(
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

    public void dibujar(SpriteBatch batch, float delta) {
        // Incrementa el tiempo del temporizador de ataque si estás atacando
        if (this.estaAtacando) {
            this.tiempoAtaque += delta;
        } else {
            // Incrementa el tiempo general si no estás atacando
            this.tiempoAtaque += delta;
        }

        TextureRegion frame;

        if (this.estaAtacando) {
            frame = this.mirandoDerecha ? this.animAtaqueDerecha.getKeyFrame(this.tiempoAtaque, false)
                                       : this.animAtaqueIzquierda.getKeyFrame(this.tiempoAtaque, false);

            // Comprueba si la animación de ataque ha terminado usando su propio temporizador
            if ((this.mirandoDerecha && this.animAtaqueDerecha.isAnimationFinished(this.tiempoAtaque)) ||
                (!this.mirandoDerecha && this.animAtaqueIzquierda.isAnimationFinished(this.tiempoAtaque))) {
                this.estaAtacando = false; // El ataque termina
                this.tiempoAtaque = 0f; // Reinicia el temporizador
            }

        } else if (this.estaMoviendose) {
            // Usa el temporizador general para el movimiento
            frame = this.mirandoDerecha ? this.animDerecha.getKeyFrame(this.tiempoAtaque, true)
                                       : this.animIzquierda.getKeyFrame(this.tiempoAtaque, true);
        } else {
            // Usa el temporizador general para el estado quieto
            frame = this.mirandoDerecha ? this.quietaDerecha : this.quietaIzquierda;
        }

        batch.draw(frame, this.x, this.y);
    }

 
    public void actualizarGravedad(float delta, boolean estaEnElSuelo, int mapHeight) {
        if (!estaEnElSuelo) {
        	this.velocidadCaida += this.GRAVEDAD * delta;
        	this.y += this.velocidadCaida * delta;
        } else {
        	this.velocidadCaida = 0;
        }
    }

    public void atacar(float delta) {
    	    if (this.estaAtacando) {
    	        this.tiempoAtaque += delta;

    	        // Comprueba si la animación ha terminado
    	        if (this.tiempoAtaque >= this.animAtaqueDerecha.getAnimationDuration()) {
    	            this.estaAtacando = false; // El ataque ha terminado
    	            this.tiempoAtaque = 0f; // Reinicia el tiempo para el próximo ataque
    	        }
    	    }
    	}


    public void iniciarAtaque(float volumen) {
        // Solo inicia el ataque si no está en curso
        if (!this.estaAtacando) {
            this.estaAtacando = true;
            this.tiempoAtaque  = 0f;  
            EfectoSonido.reproducir(this.nombreAtaque, volumen);
            
        }
        
    }

    public void aplicarMovimiento(float nuevoX, float nuevoY, float delta, int mapWidth, int mapHeight) {
    	this.estadoTiempo += delta;
    	this.estaMoviendose = nuevoX != this.x || nuevoY != this.y;
    	this.mirandoDerecha = nuevoX > this.x || (nuevoX == this.x && this.mirandoDerecha);

        float anchoSprite = 63;
        float altoSprite = 64;

        nuevoX = Math.max(0, Math.min(nuevoX, mapWidth - anchoSprite));

        nuevoY = Math.min(nuevoY, mapHeight - altoSprite);

        this.x = nuevoX;
        this.y = nuevoY;
    }

    public void guardarPosicionAnterior() {
    	this.prevX = this.x;
    	this.prevY = this.y;
    }
    
    public void frenarCaida() {
        this.velocidadCaida = 0;
    }
    public void reducirVida() {
    	this.vida--;
    }
    public String getNombre() {
    	return this.nombre;
    }
    
    public int getVida() {
    	return this.vida;
    }
    public Rectangle getHitbox() {
        return new Rectangle(this.x, this.y, 32, 32); 
    }
    
    public float getNuevaX(float delta) {
        float tempX = this.x;
        if (this.moviendoDerecha) tempX += this.velocidad * delta;
        if (this.moviendoIzquierda) tempX -= this.velocidad * delta;
        return tempX;
    }

    public float getNuevaY(float delta) {
        float tempY = this.y;
        if (this.estaSaltando) tempY += this.velocidad * delta;
        return tempY;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }
    public float getPrevY() {
        return this.prevY;
    }
    public void setY(float prevY) {
    	this.y = prevY;
    }
    public void setPosicion(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public void aumentarVida() {
        this.vida++;
    }
    public boolean getEstaAtacando() {
    	return this.estaAtacando;
    }
    public void setEstaAtacando(boolean atacando) {
        this.estaAtacando = atacando;
    }

    public void setEstaMoviendose(boolean moviendose) {
        this.estaMoviendose = moviendose;
    }

    public boolean getEstaMoviendose() {
        return estaMoviendose;
    }
    public void setMoviendoDerecha(boolean moviendo) {
        this.moviendoDerecha = moviendo;
    }

    public void setMoviendoIzquierda(boolean moviendo) {
        this.moviendoIzquierda = moviendo;
    }

    public void setEstaSaltando(boolean moviendo) {
    	this.estaSaltando = moviendo;
    }


    
   
}
