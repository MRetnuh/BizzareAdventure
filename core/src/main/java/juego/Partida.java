package juego;

import java.util.Iterator;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import audios.EfectoSonido;
import audios.Musica;
import estilos.EstiloTexto;
import input.InputController;
import jugadores.Jugador;
import niveles.Nivel1;
import niveles.Nivel2;
import niveles.NivelBase;
import pantallas.Opciones;
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
    private final Game JUEGO;
    private boolean gameOver1 = false;
    private boolean gameOver2 = false;
    private float nuevaX1, nuevaY1;
    private float nuevaX2, nuevaY2;
    private boolean victoria = false;
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
    }

    private boolean detectarColisionNivel(Rectangle hitbox) {
        if (this.nivelActual != null) {
            return this.nivelActual.detectarColision(hitbox);
        }
        return false;
    }

    private void inicializarNivel() {
        if (this.nivelActual == null) return;

        this.nivelActual.restaurarEstadoCajas();
        this.nivelActual.crearEnemigos();

        if (this.personaje1 != null) {
        	this.personaje1.setPosicion(this.nivelActual.getInicioX1(), this.nivelActual.getInicioY1());
        	 this.jugador1.generarPersonajeAleatorio();
        	 this.personaje1 = this.jugador1.getPersonajeElegido();
        }
        if (this.personaje2 != null) {
        	this.personaje2.setPosicion(this.nivelActual.getInicioX2(), this.nivelActual.getInicioY2());
        	this.jugador2.generarPersonajeAleatorio();
       	 	this.personaje2 = this.jugador2.getPersonajeElegido();
        }

        this.stage.clear();
        if (this.personaje1 != null) this.stage.addActor(this.personaje1);
        if (this.personaje2 != null) this.stage.addActor(this.personaje2);
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
            if (this.indiceNivelActual < this.niveles.length) {
                this.nivelActual = this.niveles[indiceNivelActual];
                
                inicializarNivel();
            }
        }

        this.nivelActual.actualizarCamara(this.camara, this.personaje1, this.personaje2);
        actualizarHUD();
        this.nivelActual.limpiarEnemigosMuertos();

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
                    for (Proyectil b : enemigo.getBalas()) {
                        if (!this.stage.getActors().contains(b, true)) {
                            this.stage.addActor(b);
                        }
                    }
                    enemigo.actualizarIA(delta, this.personaje1, this.personaje2, this.musicaPartida.getVolumen(), this.nivelActual);
                }
            }
        }

        this.stage.act(delta);
        this.stage.draw();
        this.stageHUD.act(delta);
        this.stageHUD.draw();
    }

    private void actualizarInputs(float delta) {
        if (this.personaje1.getVida() > 0) {
            this.personaje1.setMoviendoDerecha(this.inputController.getDerecha1());
            this.personaje1.setMoviendoIzquierda(this.inputController.getIzquierda1());
            this.personaje1.setEstaSaltando(this.inputController.getSaltar1());
            if (this.inputController.getAtacar1()) {
                this.personaje1.iniciarAtaque(this.musicaPartida.getVolumen(), delta, this.nivelActual);
                this.inputController.setAtacarFalso1();
            }
            if (this.inputController.getOpciones1()) {
                abrirOpciones();
                this.inputController.setOpcionesFalso1();
            }
        }

        if (this.personaje2.getVida() > 0) {
            this.personaje2.setMoviendoDerecha(this.inputController.getDerecha2());
            this.personaje2.setMoviendoIzquierda(this.inputController.getIzquierda2());
            this.personaje2.setEstaSaltando(this.inputController.getSaltar2());
            if (this.inputController.getAtacar2()) {
                this.personaje2.iniciarAtaque(this.musicaPartida.getVolumen(), delta, this.nivelActual);
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
            if ((esJugador1 && !this.gameOver1) || (!esJugador1 && !this.gameOver2)) {
                if (esJugador1) this.gameOver1 = true;
                else this.gameOver2 = true;
                if (this.gameOver1 && this.gameOver2) {
                    this.musicaPartida.cambiarMusica("Derrota");
                    personaje.morir(this.stageHUD);
                }
            }
            return;
        }

            Iterator<Enemigo> iter = this.nivelActual.getEnemigos().iterator();
            while(iter.hasNext()) {
                Enemigo e = iter.next();
                if(personaje.getTipoAtaque().getTipo().equals("Melee") && personaje.getEstaAtacando()) {
                    if (personaje.getHitbox().overlaps(e.getHitbox()) && e.getVida() > 0) {
                        e.reducirVida();
                        e.remove();
                        if (e.getVida() <= 0) {
                            this.nivelActual.agregarEnemigosMuertos(e);
                        }

                    }
                }
                 if (personaje.getTipoAtaque().getTipo().equals("Distancia")) {
                    Iterator<Proyectil> it = personaje.getBalas().iterator(); 
                    while (it.hasNext()) {
                        Proyectil b = it.next();
                        if (b.getHitbox().overlaps(e.getHitbox()) && e.getVida() > 0) {
                            e.reducirVida();
                            b.desactivar(); 
                            if (e.getVida() <= 0) {
                            	this.nivelActual.agregarEnemigosMuertos(e);
                                e.remove();
                            }
                        }
                    }
                }

        }


        boolean estaSobreElSuelo = detectarColisionNivel(new Rectangle(personaje.getX(), personaje.getY() - 1, 16, 16));
        personaje.guardarPosicionAnterior();
        personaje.actualizarGravedad(delta, estaSobreElSuelo, this.nivelActual.getAlturaMapa());

        float nuevaX = personaje.getNuevaX(delta);
        float nuevaY = personaje.getNuevaY(delta);

        Personaje otroPersonaje = esJugador1 ? this.personaje2 : this.personaje1;
        if (otroPersonaje != null && otroPersonaje.getVida() > 0) {
            float centroPersonajeX = nuevaX + personaje.getWidth() / 2f;
            float centroOtroX = otroPersonaje.getX() + otroPersonaje.getWidth() / 2f;
            float distanciaX = Math.abs(centroPersonajeX - centroOtroX);

            float centroPersonajeY = nuevaY + personaje.getHeight() / 2f;
            float centroOtroY = otroPersonaje.getY() + otroPersonaje.getHeight() / 2f;
            float distanciaY = Math.abs(centroPersonajeY - centroOtroY);

            if (distanciaX > this.MAX_DISTANCIA_X) nuevaX = personaje.getX();
            if (distanciaY > this.MAX_DISTANCIA_Y) nuevaY = personaje.getY();
        }

        if (esJugador1) {
            this.nuevaX1 = nuevaX;
            this.nuevaY1 = nuevaY;
        } else {
            this.nuevaX2 = nuevaX;
            this.nuevaY2 = nuevaY;
        }

        if (nuevaY < -190) personaje.reducirVida();

        Rectangle hitboxTentativaX = new Rectangle(personaje.getHitbox());
        hitboxTentativaX.setPosition(nuevaX, personaje.getY());
        boolean colisionX =  this.nivelActual.detectarColision(hitboxTentativaX);

        Rectangle hitboxTentativaY = new Rectangle(personaje.getHitbox());
        hitboxTentativaY.setPosition(personaje.getX(), nuevaY);
        boolean colisionY = this.nivelActual.detectarColision(hitboxTentativaY);

        if (colisionY) {
            personaje.frenarCaida();
            personaje.setY(personaje.getPrevY());
        }

        if (!colisionX || !colisionY) {
            float finalX = !colisionX ? nuevaX : personaje.getX();
            float finalY = !colisionY ? nuevaY : personaje.getY();
            personaje.aplicarMovimiento(finalX, finalY, delta, this.nivelActual.getAnchoMapa(), this.nivelActual.getAlturaMapa());
        }

        personaje.atacar(delta);

        for (Enemigo e : this.nivelActual.getEnemigos()) {
            Iterator<Proyectil> it = e.getBalas().iterator();
            while (it.hasNext()) {
                Proyectil b = it.next();
                if (b.getHitbox().overlaps(personaje.getHitbox())) {
                		if(personaje.getEstaAtacando() && personaje.getTipoAtaque().getTipo().equals("Melee")) {
                			EfectoSonido.reproducir("Parry", this.musicaPartida.getVolumen());
                			b.desactivar();
                		}
                		else {
                    personaje.reducirVida();
                    b.desactivar();
                		}
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
        table1.add(this.nombrePersonaje1Label).size(350, 50).padBottom(5).row();
        table1.add(this.vidaPersonaje1Label).size(350, 50);
        table2.add(this.nombrePersonaje2Label).size(350, 50).padBottom(5).row();
        table2.add(this.vidaPersonaje2Label).size(350, 50);

        Container<Table> cont1 = new Container<>(table1);
        Container<Table> cont2 = new Container<>(table2);
        cont1.setSize(400, 130); cont2.setSize(400, 130);
        cont1.setBackground(skin.getDrawable("default-round")); cont2.setBackground(skin.getDrawable("default-round"));
        cont1.setPosition(0, Gdx.graphics.getHeight() - cont1.getHeight());
        cont2.setPosition(Gdx.graphics.getWidth() - cont2.getWidth(), Gdx.graphics.getHeight() - cont2.getHeight());
        this.stageHUD.addActor(cont1); this.stageHUD.addActor(cont2);
    }

    private void actualizarHUD() {
        this.nombrePersonaje1Label.setText("Nombre: " + this.personaje1.getNombre());
        this.vidaPersonaje1Label.setText("Vida: " + this.personaje1.getVida());
        this.nombrePersonaje2Label.setText("Nombre: " + this.personaje2.getNombre());
        this.vidaPersonaje2Label.setText("Vida: " + this.personaje2.getVida());
    }

    private void detectarYEliminarTile(Personaje personaje, Jugador jugador, boolean esJugador1) {
        if (!personaje.getEstaAtacando()) {
            return;
        }
        Rectangle hitboxOriginal = personaje.getHitbox(); 
        boolean cajaRota;
        
        if(personaje.getEstaSaltando()) {
        	 Rectangle hitboxAumentada = new Rectangle(
        	            hitboxOriginal.x, 
        	            hitboxOriginal.y - (hitboxOriginal.height - 12.0f), 
        	            hitboxOriginal.width, 
        	            20.0f);
        	 cajaRota = this.nivelActual.destruirCajaEnHitbox(hitboxAumentada);
        }
        
        final float ALTURA_REDUCIDA = 10.0f;
        Rectangle hitboxReducida = new Rectangle(
            hitboxOriginal.x, 
            hitboxOriginal.y + (hitboxOriginal.height - ALTURA_REDUCIDA), 
            hitboxOriginal.width, 
            ALTURA_REDUCIDA
        );
        
        cajaRota = this.nivelActual.destruirCajaEnHitbox(hitboxReducida);
        

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
    public void abrirOpciones() {
        this.JUEGO.setScreen(new Opciones(this.JUEGO, this, this.musicaPartida));
    }

    @Override
    public void dispose() {
        for (NivelBase nivel : this.niveles) nivel.dispose();
        this.batch.dispose();
        this.stage.dispose();
        this.stageHUD.dispose();
        if (this.skin != null) this.skin.dispose();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
