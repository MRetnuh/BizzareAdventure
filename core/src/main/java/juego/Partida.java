package juego;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import audios.Musica;
import enemigos.EnemigoBase;
import input.InputController;
import jugadores.Jugador;
import mecanicas.*;
import niveles.Nivel1;
import niveles.Nivel2;
import niveles.NivelBase;
import pantallas.NivelSuperado;
import personajes.Personaje;

public class Partida implements Screen {
    private GestorDerrota gestorDerrota = new GestorDerrota();
    private Musica musicaPartida;
    private Stage stage;
    private Stage stageHUD;
    private GestorHUD gestorHUD;
    private final int JUGADOR1 = 0, JUGADOR2 = 1;
    private final Jugador[] JUGADORES = new Jugador[2];
    private Skin skin;
    private OrthographicCamera camara;
    private SpriteBatch batch;
    private InputController inputController;
    private NivelBase[] niveles = {new Nivel1(), new Nivel2()};
    private NivelBase nivelActual;
    private int indiceNivelActual = 0;
    private final Game JUEGO;
    private boolean nivelIniciado  = false;

    public Partida(Game juego, Musica musica) {
        this.JUEGO = juego;
        this.musicaPartida = musica;
        this.camara = new OrthographicCamera();
        this.camara.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport(), this.batch);
        this.stageHUD = new Stage(new ScreenViewport(), this.batch);
        this.nivelActual = this.niveles[this.indiceNivelActual];
        inicializarJugadores();
    }

    private void inicializarNivel() {
        if (this.nivelActual == null) return;

        this.nivelActual.restaurarEstadoCajas();
        this.nivelActual.crearEnemigos();

        if (this.JUGADORES[this.JUGADOR1].getPersonajeElegido() != null) {
        	this.JUGADORES[this.JUGADOR1].getPersonajeElegido().setPosicion(this.nivelActual.getInicioX1(), this.nivelActual.getInicioY1());
        	this.JUGADORES[this.JUGADOR1].generarPersonajeAleatorio();
        }
        if (this.JUGADORES[this.JUGADOR2].getPersonajeElegido() != null) {
        	this.JUGADORES[this.JUGADOR2].getPersonajeElegido().setPosicion(this.nivelActual.getInicioX2(), this.nivelActual.getInicioY2());
        	this.JUGADORES[this.JUGADOR2].generarPersonajeAleatorio();
        }

        this.stage.clear();
        if (this.JUGADORES[this.JUGADOR1].getPersonajeElegido() != null) this.stage.addActor(this.JUGADORES[this.JUGADOR1].getPersonajeElegido());
        if (this.JUGADORES[this.JUGADOR2].getPersonajeElegido() != null) this.stage.addActor(this.JUGADORES[this.JUGADOR2].getPersonajeElegido());
        for (EnemigoBase enemigo : this.nivelActual.getEnemigos()) {
            this.stage.addActor(enemigo);
        }

        this.gestorDerrota.resetear();
    }

    @Override
    public void show() {
        if (!this.nivelIniciado) {
            if (!this.JUGADORES[this.JUGADOR1].getPartidaEmpezada()) this.JUGADORES[this.JUGADOR1].generarPersonajeAleatorio();
            if (!this.JUGADORES[this.JUGADOR2].getPartidaEmpezada()) this.JUGADORES[this.JUGADOR2].generarPersonajeAleatorio();

            this.inputController = new InputController();
            this.nivelIniciado = true;

            inicializarNivel();
        }
        this.gestorHUD = new GestorHUD(this.stageHUD,
        	    this.JUGADORES[this.JUGADOR1],
        	    this.JUGADORES[this.JUGADOR2]);

        Gdx.input.setInputProcessor(this.inputController);
    }

    @Override
    public void render(float delta) {

        GestorInputs.procesarInputs(this.JUGADORES[this.JUGADOR1].getPersonajeElegido(),
        this.JUGADORES[this.JUGADOR2].getPersonajeElegido(), this.inputController,
        this.musicaPartida, this.nivelActual, delta, this.JUEGO, this);

        actualizarPersonaje(this.JUGADORES[this.JUGADOR1], this.JUGADORES[this.JUGADOR1].getPersonajeElegido(), delta, true);

        actualizarPersonaje(this.JUGADORES[this.JUGADOR2], this.JUGADORES[this.JUGADOR2].getPersonajeElegido(), delta, false);

        GestorCamara.actualizar(this.camara, this.JUGADORES[this.JUGADOR1].getPersonajeElegido(),
        this.JUGADORES[this.JUGADOR2].getPersonajeElegido(), this.nivelActual.getAnchoMapa(), this.nivelActual.getAlturaMapa());


        System.out.println(this.JUGADORES[this.JUGADOR1].getPersonajeElegido().getX());
        System.out.println(this.JUGADORES[this.JUGADOR1].getPersonajeElegido().getY());
        System.out.println(this.JUGADORES[this.JUGADOR2].getPersonajeElegido().getX());
        System.out.println(this.JUGADORES[this.JUGADOR2].getPersonajeElegido().getY());


        if (this.nivelActual.comprobarVictoria(this.JUGADORES[this.JUGADOR1].getPersonajeElegido().getX(),
                this.JUGADORES[this.JUGADOR1].getPersonajeElegido().getY(),
                this.JUGADORES[this.JUGADOR2].getPersonajeElegido().getX(),
                this.JUGADORES[this.JUGADOR2].getPersonajeElegido().getY())) {
            this.indiceNivelActual++;
            NivelSuperado nivelSuperado = new NivelSuperado(this.nivelActual.getNombreNivel(),this.JUEGO,
            this.niveles[this.indiceNivelActual].getNombreNivel(),this);
            if (this.indiceNivelActual < this.niveles.length) {
                JUEGO.setScreen(nivelSuperado);
            }
        }

        this.gestorHUD.actualizar();
        this.nivelActual.limpiarEnemigosMuertos();

        this.nivelActual.getMapRenderer().setView(this.camara);
        this.nivelActual.getMapRenderer().render();

        OrthographicCamera stageCam = (OrthographicCamera) this.stage.getCamera();
        stageCam.position.set(this.camara.position.x, this.camara.position.y, this.camara.position.z);
        stageCam.zoom = this.camara.zoom;
        stageCam.update();

        this.batch.setProjectionMatrix(this.camara.combined);

        GestorEnemigos.actualizar(delta, nivelActual, JUGADORES, stage, musicaPartida);

        this.stage.act(delta);
        this.stage.draw();
        this.stageHUD.act(delta);
        this.stageHUD.draw();
    }


    public void inicializarSiguienteNivel() {
        if (this.indiceNivelActual < this.niveles.length) {
            this.nivelActual = this.niveles[this.indiceNivelActual];
            inicializarNivel();

            if (this.inputController != null) this.inputController.resetearInputs();
            if (this.JUGADORES[this.JUGADOR1].getPersonajeElegido() != null) this.JUGADORES[this.JUGADOR1].getPersonajeElegido().detenerMovimiento();
            if (this.JUGADORES[this.JUGADOR2].getPersonajeElegido() != null) this.JUGADORES[this.JUGADOR2].getPersonajeElegido().detenerMovimiento();

            Gdx.input.setInputProcessor(this.inputController);
        }
    }

    private void actualizarPersonaje(Jugador jugador, Personaje personaje, float delta, boolean esJugador1) {
        gestorDerrota.manejarMuerteJugador(personaje, esJugador1, musicaPartida, stageHUD);
        if (gestorDerrota.partidaTerminada()) return;

        GestorCombate.procesarCombate(personaje, nivelActual, musicaPartida, delta);

        GestorGravedad.aplicarGravedad(personaje, delta, nivelActual);

        GestorMovimiento.aplicarMovimiento(personaje, delta, nivelActual, this.JUGADORES, this.JUGADOR1, this.JUGADOR2,
	    esJugador1);

        GestorInteracciones.procesarGolpeCaja(personaje, jugador, esJugador1,
        this.nivelActual, this.stage, this.gestorHUD, this.JUGADORES);
    }

    @Override
    public void dispose() {
        for (NivelBase nivel : this.niveles) nivel.dispose();
        if (this.gestorHUD != null) this.gestorHUD.dispose();
        this.batch.dispose();
        this.stage.dispose();
        if (this.skin != null) this.skin.dispose();
    }

    private void inicializarJugadores() {
    	for (int i = 0; i < this.JUGADORES.length; i++) {
            this.JUGADORES[i] = new Jugador();
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}