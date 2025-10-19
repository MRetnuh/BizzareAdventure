
package juego;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import proyectiles.Proyectil; // Importamos Nivel

public class Partida implements Screen {

    private Musica musicaPartida;
    private Stage stage;
    private Stage stageHUD;
    private final Jugador jugador1 = new Jugador();
    private final Jugador jugador2 = new Jugador();
    
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
    private boolean partidaIniciada = false; 
    
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
    
    public boolean detectarColision(Rectangle hitbox) {
        if (this.nivelActual != null) {
            return this.nivelActual.detectarColision(hitbox);
        }
        return false;
    }

    private void inicializarNivel() {
        this.nivelActual.restaurarEstadoCajas();
        this.nivelActual.crearEnemigos(); 
        
        this.personaje1.setPosicion(this.nivelActual.getInicioX1(), this.nivelActual.getInicioY1());
        this.personaje2.setPosicion(this.nivelActual.getInicioX2(), this.nivelActual.getInicioY2());
        
        this.stage.clear();
        this.stage.addActor(this.personaje1);
        this.stage.addActor(this.personaje2);
        for (Enemigo enemigo : this.nivelActual.getEnemigos()) {
            this.stage.addActor(enemigo);
        }
        
        this.victoria = false;
        this.gameOver1 = false;
        this.gameOver2 = false;
        Gdx.input.setInputProcessor(this.inputController);
    }

    @Override
    public void show() {
        if (!this.partidaIniciada) {
            if (!this.jugador1.getPartidaEmpezada()) {
                this.jugador1.generarPersonajeAleatorio();
            }
            if (!this.jugador2.getPartidaEmpezada()) {
                this.jugador2.generarPersonajeAleatorio();
            }
            this.inputController = new InputController();
            this.partidaIniciada = true;
        }

        this.personaje1 = this.jugador1.getPersonajeElegido();
        this.personaje2 = this.jugador2.getPersonajeElegido();
        
        inicializarHUD(); 
        inicializarNivel(); 
    }


    @Override
    public void render(float delta) {
        
        if(this.personaje1.getVida() > 0){
            this.personaje1.setMoviendoDerecha(this.inputController.getDerecha1());
            this.personaje1.setMoviendoIzquierda(this.inputController.getIzquierda1());
            this.personaje1.setEstaSaltando(this.inputController.getSaltar1());
            if(this.inputController.getAtacar1()) {
                this.personaje1.iniciarAtaque(this.musicaPartida.getVolumen());
                this.inputController.setAtacarFalso1();
            }
            if(this.inputController.getOpciones1()) abrirOpciones();
        }

        if(this.personaje2.getVida() > 0){
            this.personaje2.setMoviendoDerecha(this.inputController.getDerecha2());
            this.personaje2.setMoviendoIzquierda(this.inputController.getIzquierda2());
            this.personaje2.setEstaSaltando(this.inputController.getSaltar2());
            if(this.inputController.getAtacar2()) {
                this.personaje2.iniciarAtaque(this.musicaPartida.getVolumen());
                this.inputController.setAtacarFalso2();
            }
            if(this.inputController.getOpciones2()) abrirOpciones();
        }
        
        actualizarPersonaje(this.jugador1, this.personaje1, delta, true);
        actualizarPersonaje(this.jugador2, this.personaje2, delta, false);
        
        if(this.nivelActual.comprobarVictoria(this.nuevaX1, this.nuevaY1, this.nuevaX2, this.nuevaY2)) {
            this.victoria = true;
            this.indiceNivelActual++;
            this.nivelActual = this.niveles[indiceNivelActual];
            inicializarNivel();
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

        if(!this.gameOver1 || !this.gameOver2) {
            for (Enemigo enemigo : this.nivelActual.getEnemigos()) { 
                 if (enemigo.getVida() > 0) {
                     for (Proyectil b : enemigo.getBalas()) {
                          this.stage.addActor(b);
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
    
    private void actualizarPersonaje(Jugador jugador, Personaje personaje, float delta, boolean esJugador1) {
        if (personaje.getVida() <= 0) {
            if ((esJugador1 && ! this.gameOver1) || (!esJugador1 && ! this.gameOver2)) {
                if (esJugador1) this.gameOver1 = true;
                else this.gameOver2 = true;
                if(this.gameOver1 == true && this.gameOver2 == true) {
                    this.musicaPartida.cambiarMusica("derrota");
                    personaje.morir(this.stageHUD);
                }
            }
            return;
        }
        
        if (personaje.getEstaAtacando()) {
            Iterator<Enemigo> iter = this.nivelActual.getEnemigos().iterator(); 
            while(iter.hasNext()) {
                 // ... (Tu lógica de colisión Melee y Distancia) ...
            }
        }
        
        boolean estaSobreElSuelo = detectarColision(new Rectangle(personaje.getX(), personaje.getY() - 1, 16, 16));

        personaje.guardarPosicionAnterior();
        personaje.actualizarGravedad(delta, estaSobreElSuelo, this.nivelActual.getAlturaMapa()); 

        float nuevaX = personaje.getNuevaX(delta);
        float nuevaY = personaje.getNuevaY(delta);
        
        if (esJugador1) {
            this.nuevaX1 = nuevaX;
            this.nuevaY1 = nuevaY;
        } else {
            this.nuevaX2 = nuevaX;
            this.nuevaY2 = nuevaY;
        }

        if (nuevaY < -190) {
            personaje.reducirVida();
        }
        
        Rectangle hitboxTentativaX = new Rectangle(personaje.getHitbox());
        hitboxTentativaX.setPosition(nuevaX, personaje.getY());
        boolean colisionX = detectarColision(hitboxTentativaX);

        Rectangle hitboxTentativaY = new Rectangle(personaje.getHitbox());
        hitboxTentativaY.setPosition(personaje.getX(), nuevaY);
        boolean colisionY = detectarColision(hitboxTentativaY);

        if (colisionY) {
            personaje.frenarCaida();
            personaje.setY(personaje.getPrevY());
        }

        if (!colisionX || !colisionY) {
            float finalX = !colisionX ? nuevaX : personaje.getX();
            float finalY = !colisionY ? nuevaY : personaje.getY();
            personaje.aplicarMovimiento(finalX, finalY, delta, this.nivelActual.getAnchoMapa(), this.nivelActual.getAlturaMapa());
        }
        
        personaje.atacar(delta, this); 
        detectarYEliminarTile(personaje, jugador, esJugador1);

        for (Enemigo e : this.nivelActual.getEnemigos()) {
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
    }

    private void detectarYEliminarTile(Personaje personaje, Jugador jugador, boolean esJugador1) {
        boolean cajaRota = this.nivelActual.detectarColision(personaje.getHitbox());
        
        if (cajaRota) {
            
            if (this.personaje1.getVida() <= 0 && this.personaje2.getVida() > 0) {
                this.personaje1.setPosicion(this.personaje2.getX(), this.personaje2.getY());
                this.personaje1.aumentarVida();
                this.gameOver1 = false;
            } else if (this.personaje2.getVida() <= 0 && this.personaje1.getVida() > 0) {
                this.personaje2.setPosicion(this.personaje1.getX(), this.personaje1.getY());
                this.personaje2.aumentarVida();
                this.gameOver2 = false;
            } else {
                Personaje nuevoPersonaje = jugador.cambiarPersonaje(
                    esJugador1 ? this.nuevaX1 : this.nuevaX2,
                    esJugador1 ? this.nuevaY1 : this.nuevaY2
                );
                
                if (esJugador1) {
                    this.stage.getActors().removeValue(this.personaje1, true);
                    this.personaje1 = nuevoPersonaje;
                    this.stage.addActor(this.personaje1);
                } else {
                    this.stage.getActors().removeValue(this.personaje2, true);
                    this.personaje2 = nuevoPersonaje;
                    this.stage.addActor(this.personaje2);
                }
            }
            actualizarHUD();
        }
    }

        private void actualizarHUD() {
            this.nombrePersonaje1Label.setText("Nombre: " + this.personaje1.getNombre());
            this.vidaPersonaje1Label.setText("Vida: " + this.personaje1.getVida());

            this.nombrePersonaje2Label.setText("Nombre: " + this.personaje2.getNombre());
            this.vidaPersonaje2Label.setText("Vida: " + this.personaje2.getVida());
        
 }
        private void inicializarHUD() {
            this.skin = new Skin(Gdx.files.internal("uiskin.json")); 
            this.nombrePersonaje1Label = new Label("Nombre: " + this.personaje1.getNombre(), EstiloTexto.ponerEstiloLabel(40, Color.RED));
            this.vidaPersonaje1Label = new Label("Vida: " + this.personaje1.getVida(), EstiloTexto.ponerEstiloLabel(40, Color.RED));

            this.nombrePersonaje2Label = new Label("Nombre: " + this.personaje2.getNombre(), EstiloTexto.ponerEstiloLabel(40, Color.BLUE));
            this.vidaPersonaje2Label = new Label("Vida: " + this.personaje2.getVida(), EstiloTexto.ponerEstiloLabel(40, Color.BLUE));
            Table table1 = new Table();
            Table table2 = new Table();
            table1.left().top();
            table2.right().top();
            table1.add(this.nombrePersonaje1Label).size(350, 50).padBottom(5).row();
            table1.add(this.vidaPersonaje1Label).size(350, 50);
            table2.add(this.nombrePersonaje2Label).size(350, 50).padBottom(5).row();
            table2.add(this.vidaPersonaje2Label).size(350, 50);

            Container<Table> contenedor1 = new Container<>(table1);
            Container<Table> contenedor2 = new Container<>(table2);
            contenedor1.setSize(400, 130);
            contenedor2.setSize(400, 130);
            contenedor1.setBackground(this.skin.getDrawable("default-round"));
            contenedor2.setBackground(this.skin.getDrawable("default-round"));
            contenedor1.setPosition(0, Gdx.graphics.getHeight() - contenedor1.getHeight());
            contenedor2.setPosition(Gdx.graphics.getWidth() - contenedor2.getWidth(), Gdx.graphics.getHeight() - contenedor2.getHeight());

            this.stageHUD.addActor(contenedor1);
            this.stageHUD.addActor(contenedor2);
        }


        public void abrirOpciones() {
            this.juego.setScreen(new Opciones(this.juego, this, this.musicaPartida));
        }


    @Override
    public void dispose() {
        for (NivelBase nivel : this.niveles) {
            nivel.dispose();
        }
        this.batch.dispose();
        this.stage.dispose();
        this.stageHUD.dispose();
        if (this.skin != null) this.skin.dispose(); 
    }

	@Override
	public void resize(int width, int height) {
	}
	@Override
	public void pause() {
	}
	@Override
	public void resume() {
	}
	@Override
	public void hide() {
	}
}