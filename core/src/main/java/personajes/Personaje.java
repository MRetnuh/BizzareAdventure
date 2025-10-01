package personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
// import com.badlogic.gdx.graphics.OrthographicCamera; // Ya no se necesita aquí
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor; // 👈 Importar Actor
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Timer;

import audios.EfectoSonido;

public abstract class Personaje extends Actor {
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
    private float prevX, prevY;
    private float estadoTiempo = 0f;
    private float velocidadCaida = 0;
    private Texture texturaPrincipal; 
    protected Animation<TextureRegion> animDerecha;
    protected Animation<TextureRegion> animIzquierda;
    protected Animation<TextureRegion> animAtaqueDerecha;
    protected Animation<TextureRegion> animAtaqueIzquierda;
    protected TextureRegion quietaDerecha;
    protected TextureRegion quietaIzquierda;
    private float tiempoAtaque = 0f;


    public Personaje(String nombre, int velocidad, String nombreAtaque, int vida) {//-> pasale el stage aca
        this.nombre = nombre;
        this.velocidad = velocidad;
        this.nombreAtaque = nombreAtaque;
        this.vida = vida;
        this.cargarTexturas();
        super.setX(200);
        super.setY(930);
        setSize(quietaDerecha.getRegionWidth(), quietaDerecha.getRegionHeight());
    }

    protected abstract void cargarTexturas();

    public void cargarUbicaciones(float x, float y) {
        setX(x);
        setY(y);
    }

    public void morir(Stage stage) {//-> Aca no le pases el stage
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

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion frame;
        // elegir frame según estado
        if (estaAtacando) {
            // tu lógica de ataque
            frame = mirandoDerecha ? animAtaqueDerecha.getKeyFrame(tiempoAtaque, false)
                    : animAtaqueIzquierda.getKeyFrame(tiempoAtaque, false);
        } else if (estaMoviendose) {
            frame = mirandoDerecha ? animDerecha.getKeyFrame(estadoTiempo, true)
                    : animIzquierda.getKeyFrame(estadoTiempo, true);
        } else {
            frame = mirandoDerecha ? quietaDerecha : quietaIzquierda;
        }

        batch.draw(frame, getX(), getY());//->Capaz esta bien
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (this.estaAtacando) {
            this.tiempoAtaque += delta;

            if ((this.mirandoDerecha && this.animAtaqueDerecha.isAnimationFinished(this.tiempoAtaque)) ||
                    (!this.mirandoDerecha && this.animAtaqueIzquierda.isAnimationFinished(this.tiempoAtaque))) {
                this.estaAtacando = false;
                this.tiempoAtaque = 0f;
            }

        } else {
            this.estadoTiempo += delta;
        }
    }

    public void actualizarGravedad(float delta, boolean estaEnElSuelo, int mapHeight) {
        if (!estaEnElSuelo) {
            this.velocidadCaida += this.GRAVEDAD * delta;
            setY(getY() + this.velocidadCaida * delta); // 👈 Usar setY()
        } else {
            this.velocidadCaida = 0;
        }
    }

    public void atacar(float delta) {
	    if (this.estaAtacando) {
	        this.tiempoAtaque += delta;

	        if (this.tiempoAtaque >= this.animAtaqueDerecha.getAnimationDuration()) {
	            this.estaAtacando = false; // El ataque ha terminado
	            this.tiempoAtaque = 0f; // Reinicia el tiempo para el próximo ataque
	        }
	    }
	}


    public void iniciarAtaque(float volumen) {
        if (!this.estaAtacando) {
            this.estaAtacando = true;
            this.tiempoAtaque  = 0f;
            EfectoSonido.reproducir(this.nombreAtaque, volumen);

        }

    }

    public void aplicarMovimiento(float nuevoX, float nuevoY, float delta, int mapWidth, int mapHeight) {
        this.estaMoviendose = nuevoX != getX() || nuevoY != getY();
        this.mirandoDerecha = nuevoX > getX() || (nuevoX == getX() && this.mirandoDerecha);

        float anchoSprite = getWidth();
        float altoSprite = getHeight();

        nuevoX = Math.max(0, Math.min(nuevoX, mapWidth - anchoSprite));

        nuevoY = Math.min(nuevoY, mapHeight - altoSprite);

        setX(nuevoX);
        setY(nuevoY);
    }

    public void guardarPosicionAnterior() {
        this.prevX = getX();
        this.prevY = getY();
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
        return new Rectangle(getX(), getY(), 32, 32);
    }

    public float getNuevaX(float delta) {
        float tempX = getX();
        if (this.moviendoDerecha) tempX += this.velocidad * delta;
        if (this.moviendoIzquierda) tempX -= this.velocidad * delta;
        return tempX;
    }

    public float getNuevaY(float delta) {
        float tempY = getY();
        if (this.estaSaltando) tempY += this.velocidad * delta;
        return tempY;
    }

    // ELIMINADO: public float getX() / public float getY() (Usar super.getX(), super.getY())

    public float getPrevY() {
        return this.prevY;
    }
    public void setY(float prevY) {
        super.setY(prevY); // 👈 Usar el método de Actor
    }
    public void setPosicion(float x, float y) {
        setX(x);
        setY(y);
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