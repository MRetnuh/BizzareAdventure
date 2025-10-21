package juego;

import java.util.Iterator;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import audios.Musica;
import estilos.EstiloTexto;
import input.InputController;
import jugadores.Jugador;
import niveles.Nivel1;
import niveles.Nivel2;
import niveles.NivelBase;
import personajes.Enemigo;
import personajes.Personaje;
import proyectiles.Proyectil;

public class Partida implements Screen {

    private Musica musicaPartida;
    private Stage stage;
    private Stage stageHUD;
    private final Jugador jugador1 = new Jugador();
    private final Jugador jugador2 = new Jugador();
    private final float MAX_DISTANCIA_X = Gdx.graphics.getWidth() * 0.95f;
    private final float MAX_DISTANCIA_Y = Gdx.graphics.getHeight() * 0.95f;
    private Skin skin;
    private OrthographicCamera camara;
    private SpriteBatch batch;
    private InputController inputController;
    private Personaje personaje1;
    private Personaje personaje2;
    private NivelBase[] niveles = {new Nivel1(), new Nivel2()};
    private NivelBase nivelActual;
    private int indiceNivelActual = 0;

    private Label nombrePersonaje1Label, vidaPersonaje1Label;
    private Label nombrePersonaje2Label, vidaPersonaje2Label;
    private final Game juego;
    private boolean gameOver1 = false;
    private boolean gameOver2 = false;
    private float nuevaX1, nuevaY1;
    private float nuevaX2, nuevaY2;
    private boolean victoria = false;
    private boolean nivelIniciado  = false;

    public Partida(Game juego, Musica musica) {
        this.juego = juego;
        this.musicaPartida = musica;
        this.camara = new OrthographicCamera();
        this.camara.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport(), this.batch);
        this.stageHUD = new Stage(new ScreenViewport(), this.batch);

        this.nivelActual = this.niveles[this.indiceNivelActual];
    }

    public boolean detectarColisionNivel(Rectangle hitbox) {
        if (this.nivelActual != null) {
            return this.nivelActual.detectarColision(hitbox);
        }
        return false;
    }

    private void inicializarNivel() {
        if (this.nivelActual == null) return;

        this.nivelActual.restaurarEstadoCajas();
        this.nivelActual.crearEnemigos();

        if (personaje1 != null) {
        	personaje1.setPosicion(this.nivelActual.getInicioX1(), this.nivelActual.getInicioY1());
        	 this.jugador1.generarPersonajeAleatorio();
        	 this.personaje1 = this.jugador1.getPersonajeElegido();
        }
        if (personaje2 != null) {
        	personaje2.setPosicion(this.nivelActual.getInicioX2(), this.nivelActual.getInicioY2());
        	this.jugador2.generarPersonajeAleatorio();
       	 	this.personaje2 = this.jugador2.getPersonajeElegido();
        }

        this.stage.clear();
        if (personaje1 != null) this.stage.addActor(this.personaje1);
        if (personaje2 != null) this.stage.addActor(this.personaje2);
        for (Enemigo enemigo : this.nivelActual.getEnemigos()) {
            this.stage.addActor(enemigo);
        }

        this.victoria = false;
        this.gameOver1 = false;
        this.gameOver2 = false;
    }

    @Override
    public void show() {
        if (!this.nivelIniciado) {
            if (!this.jugador1.getPartidaEmpezada()) this.jugador1.generarPersonajeAleatorio();
            if (!this.jugador2.getPartidaEmpezada()) this.jugador2.generarPersonajeAleatorio();

            this.inputController = new InputController();
            this.nivelIniciado = true;

            this.personaje1 = this.jugador1.getPersonajeElegido();
            this.personaje2 = this.jugador2.getPersonajeElegido();

            inicializarNivel();
        }

        inicializarHUD();
        Gdx.input.setInputProcessor(this.inputController);
    }

    @Override
    public void render(float delta) {

        actualizarInputs(delta);

        actualizarPersonaje(this.jugador1, this.personaje1, delta, true);
        actualizarPersonaje(this.jugador2, this.personaje2, delta, false);

        if (this.nivelActual.comprobarVictoria(this.nuevaX1, this.nuevaY1, this.nuevaX2, this.nuevaY2)) {
            this.victoria = true;
            this.indiceNivelActual++;
            if (indiceNivelActual < niveles.length) {
                this.nivelActual = this.niveles[indiceNivelActual];
                
                inicializarNivel();
            }
        }

        this.nivelActual.actualizarCamara(this.camara, this.personaje1, this.personaje2);
        actualizarHUD();
        nivelActual.limpiarEnemigosMuertos();

        this.nivelActual.getMapRenderer().setView(this.camara);
        this.nivelActual.getMapRenderer().render();

        OrthographicCamera stageCam = (OrthographicCamera) this.stage.getCamera();
        stageCam.position.set(this.camara.position.x, this.camara.position.y, this.camara.position.z);
        stageCam.zoom = this.camara.zoom;
        stageCam.update();

        this.batch.setProjectionMatrix(this.camara.combined);

        if (!this.gameOver1 || !this.gameOver2) {
            for (Enemigo enemigo : this.nivelActual.getEnemigos()) {
                if (enemigo.getVida() > 0) {
                    // Añadir proyectiles de manera segura
                    for (Proyectil b : enemigo.getBalas()) {
                        if (!stage.getActors().contains(b, true)) {
                            stage.addActor(b);
                        }
                    }
                    enemigo.actualizarIA(delta, this.personaje1, this.personaje2, this.musicaPartida.getVolumen(), this);
                }
            }
        }

        this.stage.act(delta);
        this.stage.draw();
        this.stageHUD.act(delta);
        this.stageHUD.draw();
    }

    private void actualizarInputs(float delta) {
        if (personaje1.getVida() > 0) {
            personaje1.setMoviendoDerecha(this.inputController.getDerecha1());
            personaje1.setMoviendoIzquierda(this.inputController.getIzquierda1());
            personaje1.setEstaSaltando(this.inputController.getSaltar1());
            if (this.inputController.getAtacar1()) {
                personaje1.iniciarAtaque(this.musicaPartida.getVolumen());
                this.inputController.setAtacarFalso1();
            }
            if (this.inputController.getOpciones1()) {
                abrirOpciones();
                this.inputController.setOpcionesFalso1();
            }
        }

        if (personaje2.getVida() > 0) {
            personaje2.setMoviendoDerecha(this.inputController.getDerecha2());
            personaje2.setMoviendoIzquierda(this.inputController.getIzquierda2());
            personaje2.setEstaSaltando(this.inputController.getSaltar2());
            if (this.inputController.getAtacar2()) {
                personaje2.iniciarAtaque(this.musicaPartida.getVolumen());
                this.inputController.setAtacarFalso2();
            }
            if (this.inputController.getOpciones2()) {
                abrirOpciones();
                this.inputController.setOpcionesFalso2();
            }
        }
    }

    private void actualizarPersonaje(Jugador jugador, Personaje personaje, float delta, boolean esJugador1) {
        if (personaje.getVida() <= 0) {
            if ((esJugador1 && !gameOver1) || (!esJugador1 && !gameOver2)) {
                if (esJugador1) gameOver1 = true;
                else gameOver2 = true;
                if (gameOver1 && gameOver2) {
                    this.musicaPartida.cambiarMusica("derrota");
                    personaje.morir(this.stageHUD);
                }
            }
            return;
        }

        boolean estaSobreElSuelo = detectarColisionNivel(new Rectangle(personaje.getX(), personaje.getY() - 1, personaje.getWidth(), personaje.getHeight()));
        personaje.guardarPosicionAnterior();
        personaje.actualizarGravedad(delta, estaSobreElSuelo, this.nivelActual.getAlturaMapa());

        float nuevaX = personaje.getNuevaX(delta);
        float nuevaY = personaje.getNuevaY(delta);

        Personaje otroPersonaje = esJugador1 ? personaje2 : personaje1;
        if (otroPersonaje != null && otroPersonaje.getVida() > 0) {
            float centroPersonajeX = nuevaX + personaje.getWidth() / 2f;
            float centroOtroX = otroPersonaje.getX() + otroPersonaje.getWidth() / 2f;
            float distanciaX = Math.abs(centroPersonajeX - centroOtroX);

            float centroPersonajeY = nuevaY + personaje.getHeight() / 2f;
            float centroOtroY = otroPersonaje.getY() + otroPersonaje.getHeight() / 2f;
            float distanciaY = Math.abs(centroPersonajeY - centroOtroY);

            if (distanciaX > MAX_DISTANCIA_X) nuevaX = personaje.getX();
            if (distanciaY > MAX_DISTANCIA_Y) nuevaY = personaje.getY();
        }

        if (esJugador1) {
            nuevaX1 = nuevaX;
            nuevaY1 = nuevaY;
        } else {
            nuevaX2 = nuevaX;
            nuevaY2 = nuevaY;
        }

        if (nuevaY < -190) personaje.reducirVida();

        // Colisiones con nivel
        Rectangle hitboxX = new Rectangle(personaje.getHitbox());
        hitboxX.setPosition(nuevaX, personaje.getY());
        boolean colX = detectarColisionNivel(hitboxX);

        Rectangle hitboxY = new Rectangle(personaje.getHitbox());
        hitboxY.setPosition(personaje.getX(), nuevaY);
        boolean colY = detectarColisionNivel(hitboxY);

        if (colY) {
            personaje.frenarCaida();
            personaje.setY(personaje.getPrevY());
        }

        float finalX = !colX ? nuevaX : personaje.getX();
        float finalY = !colY ? nuevaY : personaje.getY();
        personaje.aplicarMovimiento(finalX, finalY, delta, nivelActual.getAnchoMapa(), nivelActual.getAlturaMapa());

        // Ataques
        personaje.atacar(delta, this);

        // Colisiones con proyectiles enemigos
        for (Enemigo e : nivelActual.getEnemigos()) {
            Iterator<Proyectil> it = e.getBalas().iterator();
            while (it.hasNext()) {
                Proyectil b = it.next();
                if (b.getHitbox().overlaps(personaje.getHitbox())) {
                    personaje.reducirVida();
                    b.desactivar();
                    it.remove();
                }
            }
        }

        detectarYEliminarTile(personaje, jugador, esJugador1);
    }
    
    private void inicializarHUD() {
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));
        this.nombrePersonaje1Label = new Label("Nombre: " + this.personaje1.getNombre(), EstiloTexto.ponerEstiloLabel(40, Color.RED));
        this.vidaPersonaje1Label = new Label("Vida: " + this.personaje1.getVida(), EstiloTexto.ponerEstiloLabel(40, Color.RED));
        this.nombrePersonaje2Label = new Label("Nombre: " + this.personaje2.getNombre(), EstiloTexto.ponerEstiloLabel(40, Color.BLUE));
        this.vidaPersonaje2Label = new Label("Vida: " + this.personaje2.getVida(), EstiloTexto.ponerEstiloLabel(40, Color.BLUE));

        Table table1 = new Table(); Table table2 = new Table();
        table1.left().top(); table2.right().top();
        table1.add(nombrePersonaje1Label).size(350, 50).padBottom(5).row();
        table1.add(vidaPersonaje1Label).size(350, 50);
        table2.add(nombrePersonaje2Label).size(350, 50).padBottom(5).row();
        table2.add(vidaPersonaje2Label).size(350, 50);

        Container<Table> cont1 = new Container<>(table1);
        Container<Table> cont2 = new Container<>(table2);
        cont1.setSize(400, 130); cont2.setSize(400, 130);
        cont1.setBackground(skin.getDrawable("default-round")); cont2.setBackground(skin.getDrawable("default-round"));
        cont1.setPosition(0, Gdx.graphics.getHeight() - cont1.getHeight());
        cont2.setPosition(Gdx.graphics.getWidth() - cont2.getWidth(), Gdx.graphics.getHeight() - cont2.getHeight());
        stageHUD.addActor(cont1); stageHUD.addActor(cont2);
    }

    private void actualizarHUD() {
        nombrePersonaje1Label.setText("Nombre: " + personaje1.getNombre());
        vidaPersonaje1Label.setText("Vida: " + personaje1.getVida());
        nombrePersonaje2Label.setText("Nombre: " + personaje2.getNombre());
        vidaPersonaje2Label.setText("Vida: " + personaje2.getVida());
    }

    private void detectarYEliminarTile(Personaje personaje, Jugador jugador, boolean esJugador1) {
        boolean cajaRota = nivelActual.detectarColision(personaje.getHitbox());
        if (!cajaRota) return;

        if (personaje1.getVida() <= 0 && personaje2.getVida() > 0) {
            personaje1.setPosicion(personaje2.getX(), personaje2.getY());
            personaje1.aumentarVida();
            gameOver1 = false;
        } else if (personaje2.getVida() <= 0 && personaje1.getVida() > 0) {
            personaje2.setPosicion(personaje1.getX(), personaje1.getY());
            personaje2.aumentarVida();
            gameOver2 = false;
        } else {
            Personaje nuevo = jugador.cambiarPersonaje(
                    esJugador1 ? nuevaX1 : nuevaX2,
                    esJugador1 ? nuevaY1 : nuevaY2
            );
            if (esJugador1) {
                stage.getActors().removeValue(personaje1, true);
                personaje1 = nuevo;
                stage.addActor(personaje1);
            } else {
                stage.getActors().removeValue(personaje2, true);
                personaje2 = nuevo;
                stage.addActor(personaje2);
            }
        }
        actualizarHUD();
    }

    public void abrirOpciones() {
        this.juego.setScreen(new Opciones(this.juego, this, this.musicaPartida));
    }

    @Override
    public void dispose() {
        for (NivelBase nivel : this.niveles) nivel.dispose();
        this.batch.dispose();
        this.stage.dispose();
        this.stageHUD.dispose();
        if (skin != null) skin.dispose();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
