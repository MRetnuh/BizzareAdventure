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
import juego.Partida;
import proyectiles.Proyectil;

import java.util.ArrayList;
import java.util.Iterator;

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
    protected boolean mirandoDerecha = true;
    protected boolean estaMoviendose = false;
    private boolean estaSaltando = false;
    protected boolean moviendoDerecha = false;
    private boolean moviendoIzquierda = false;
    private final float GRAVEDAD = -500;
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
    protected TextureRegion frame;
    private TipoAtaque tipoAtaque;
    protected ArrayList<Proyectil> balas = new ArrayList<>();
    private float tiempoDisparo = 0f;
    private final float cooldownDisparo = 0.5f;
    private Partida partida;
	private boolean disparoRealizado = false;
    
    public Personaje(String nombre, int velocidad, String nombreAtaque, int vida, TipoAtaque tipoAtaque) {//-> pasale el stage aca
        this.nombre = nombre;
        this.velocidad = velocidad;
        this.nombreAtaque = nombreAtaque;
        this.vida = vida;
        this.cargarTexturas();
        this.tipoAtaque = tipoAtaque;
        super.setX(200);
        super.setY(930);
        setSize(this.quietaDerecha.getRegionWidth(), this.quietaDerecha.getRegionHeight());
    }

    protected abstract void cargarTexturas();

    public void cargarUbicaciones(float x, float y) {
        super.setX(x);
        super.setY(y);
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
        this.imagenDerrota = new Image(this.texturaDerrota);
        this.imagenDerrota.setSize(200, 50);
        this.imagenDerrota.setOrigin(this.imagenDerrota.getWidth() / 2f, this.imagenDerrota.getHeight() / 2f);
        this.imagenDerrota.setPosition(
                (Gdx.graphics.getWidth() - this.imagenDerrota.getWidth()) / 2f,
                (Gdx.graphics.getHeight() - this.imagenDerrota.getHeight()) / 2f
        );
        this.imagenDerrota.setScale(0.1f);
        this.imagenDerrota.getColor().a = 0;

        this.stage.addActor(this.imagenDerrota);

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
        // 🔹 Elegir frame según el estado
        if (this.estaAtacando) {
            frame = this.mirandoDerecha
                    ? this.animAtaqueDerecha.getKeyFrame(this.tiempoAtaque, false)
                    : this.animAtaqueIzquierda.getKeyFrame(this.tiempoAtaque, false);
        } else if (this.estaMoviendose) {
            frame = this.mirandoDerecha
                    ? this.animDerecha.getKeyFrame(this.estadoTiempo, true)
                    : this.animIzquierda.getKeyFrame(this.estadoTiempo, true);
        } else {
            frame = this.mirandoDerecha ? this.quietaDerecha : this.quietaIzquierda;
        }

        // 🔹 Dibujar al personaje
        batch.draw(frame, getX(), getY());

        // 🔹 Dibujar las balas SIEMPRE
        for (Proyectil p : balas) {
            if (p.isActivo())
                p.draw(batch, parentAlpha);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // 🔹 Actualizar balas
        Iterator<Proyectil> it = balas.iterator();
        while (it.hasNext()) {
            Proyectil p = it.next();
            p.mover(delta, partida);
            if (!p.isActivo()) it.remove();
        }

        // 🔹 Controlar animaciones de ataque o movimiento
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
            super.setY(getY() + this.velocidadCaida * delta); // 👈 Usar setY()
        } else {
            this.velocidadCaida = 0;
        }
    }


    public void atacar(float delta, Partida partida) {
        if (this.tipoAtaque.getTipo().equals("Melee")) {
            if (this.estaAtacando) {
                this.tiempoAtaque += delta;
                if (this.tiempoAtaque >= this.animAtaqueDerecha.getAnimationDuration()) {
                    this.estaAtacando = false;
                    this.tiempoAtaque = 0f;
                }
            }
        } 
        else { // Ataque a distancia
            if (this.estaAtacando && !disparoRealizado) {
                // 🔹 Crear bala una sola vez por ataque
                String ruta = mirandoDerecha
                    ? "imagenes/personajes/enemigo/ataque/Bala_Derecha.png"
                    : "imagenes/personajes/enemigo/ataque/Bala_Izquierda.png";

                Proyectil nuevaBala = new Proyectil(getX(), getY() + 16, this.mirandoDerecha, ruta);
                this.balas.add(nuevaBala);

                disparoRealizado = true;
            }

            // 🔹 Cuando termina la animación, permitimos volver a disparar
            if (this.estaAtacando) {
                this.tiempoAtaque += delta;
                if ((this.mirandoDerecha && this.animAtaqueDerecha.isAnimationFinished(this.tiempoAtaque)) ||
                    (!this.mirandoDerecha && this.animAtaqueIzquierda.isAnimationFinished(this.tiempoAtaque))) {
                    this.estaAtacando = false;
                    this.tiempoAtaque = 0f;
                    disparoRealizado = false; // 🔹 Rehabilitamos el siguiente disparo
                }
            }

            // 🔹 Mover las balas y limpiar las que colisionan
            Iterator<Proyectil> it = this.balas.iterator();
            while (it.hasNext()) {
                Proyectil b = it.next();
                b.mover(delta, partida);
                if (!b.isActivo()) it.remove();
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
        this.estaMoviendose = nuevoX != super.getX() || nuevoY != super.getY();
        this.mirandoDerecha = nuevoX > super.getX() || nuevoX == super.getX() && this.mirandoDerecha;

        float anchoSprite = getWidth();
        float altoSprite = getHeight();

        nuevoX = Math.max(0, Math.min(nuevoX, mapWidth - anchoSprite));

        nuevoY = Math.min(nuevoY, mapHeight - altoSprite);

        setX(nuevoX);
        setY(nuevoY);
    }

    public void guardarPosicionAnterior() {
        this.prevX = super.getX();
        this.prevY = super.getY();
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
        return new Rectangle(super.getX(), super.getY(), 32, 32);
    }

    public float getNuevaX(float delta) {
        float tempX = getX();
        if (this.moviendoDerecha) tempX += this.velocidad * delta;
        if (this.moviendoIzquierda) tempX -= this.velocidad * delta;
        return tempX;
    }

    public float getNuevaY(float delta) {
        float tempY = super.getY();
        if (this.estaSaltando) tempY += this.velocidad * delta;
        return tempY;
    }

    public float getPrevY() {
        return this.prevY;
    }
    public void setY(float prevY) {
        super.setY(prevY); // Usar el método de Actor
    }
    public void setPosicion(float x, float y) {
        super.setX(x);
        super.setY(y);
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
    
    public TipoAtaque getTipoAtaque() {
    	return this.tipoAtaque;
    }
    
    public ArrayList<Proyectil> getBalas() {
        return this.balas;
    }

}