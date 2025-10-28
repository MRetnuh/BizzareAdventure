package juego;

import java.util.Iterator;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import audios.EfectoSonido;
import audios.Musica;
import enemigos.TipoEnemigo;
import enemigos.EnemigoBase;
import input.InputController;
import jugadores.Jugador;
import mecanicas.GestorCamara;
import mecanicas.GestorHUD;
import niveles.Nivel1;
import niveles.Nivel2;
import niveles.NivelBase;
import pantallas.NivelSuperado;
import pantallas.Opciones;
import personajes.Personaje;
import personajes.TipoAtaque;
import proyectiles.Proyectil;

public class Partida implements Screen {

    private Musica musicaPartida;
    private Stage stage;
    private Stage stageHUD;
    private GestorHUD gestorHUD;
    private final int JUGADOR1 = 0, JUGADOR2 = 1;
    private final Jugador[] JUGADORES = new Jugador[2];
    private final float MAX_DISTANCIA_X = Gdx.graphics.getWidth() * 0.95f;
    private final float MAX_DISTANCIA_Y = Gdx.graphics.getHeight() * 0.95f;
    private Skin skin;
    private OrthographicCamera camara;
    private SpriteBatch batch;
    private InputController inputController;
    private NivelBase[] niveles = {new Nivel1(), new Nivel2()};
    private NivelBase nivelActual;
    private int indiceNivelActual = 0;
    private final Game JUEGO;
    private boolean gameOver1 = false;
    private boolean gameOver2 = false;
    private float nuevaX1, nuevaY1;
    private float nuevaX2, nuevaY2;
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

        this.gameOver1 = false;
        this.gameOver2 = false;
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

        actualizarInputs(delta);

        actualizarPersonaje(this.JUGADORES[this.JUGADOR1], this.JUGADORES[this.JUGADOR1].getPersonajeElegido(), delta, true);
        
        actualizarPersonaje(this.JUGADORES[this.JUGADOR2], this.JUGADORES[this.JUGADOR2].getPersonajeElegido(), delta, false);
        
        GestorCamara.actualizar(this.camara, this.JUGADORES[this.JUGADOR1].getPersonajeElegido(), 
        this.JUGADORES[this.JUGADOR2].getPersonajeElegido(), this.nivelActual.getAnchoMapa(), this.nivelActual.getAlturaMapa());
        
        
        System.out.println(this.JUGADORES[this.JUGADOR1].getPersonajeElegido().getX());
        System.out.println(this.JUGADORES[this.JUGADOR1].getPersonajeElegido().getY());
        System.out.println(this.JUGADORES[this.JUGADOR2].getPersonajeElegido().getX());
        System.out.println(this.JUGADORES[this.JUGADOR2].getPersonajeElegido().getY());
        
        
        if (this.nivelActual.comprobarVictoria(this.nuevaX1, this.nuevaY1, this.nuevaX2, this.nuevaY2)) {
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

        if (!this.gameOver1 || !this.gameOver2) {
            for (EnemigoBase enemigo : this.nivelActual.getEnemigos()) {
                if (enemigo.getVida() > 0) {
                    for (Proyectil b : enemigo.getBalas()) {
                        if (!this.stage.getActors().contains(b, true)) {
                            this.stage.addActor(b);
                        }
                    }
                    enemigo.actualizarIA(delta, this.JUGADORES[this.JUGADOR1].getPersonajeElegido(), 
                    this.JUGADORES[this.JUGADOR2].getPersonajeElegido(), this.musicaPartida.getVolumen(), this.nivelActual);
                }
            }
        }

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


    private void actualizarInputs(float delta) {
        if (this.JUGADORES[this.JUGADOR1].getPersonajeElegido().getVida() > 0) {
        	this.JUGADORES[this.JUGADOR1].getPersonajeElegido().setMoviendoDerecha(this.inputController.getDerecha1());
        	this.JUGADORES[this.JUGADOR1].getPersonajeElegido().setMoviendoIzquierda(this.inputController.getIzquierda1());
        	this.JUGADORES[this.JUGADOR1].getPersonajeElegido().setEstaSaltando(this.inputController.getSaltar1());
            if (this.inputController.getAtacar1()) {
            	this.JUGADORES[this.JUGADOR1].getPersonajeElegido().iniciarAtaque(this.musicaPartida.getVolumen(), delta, this.nivelActual);
                this.inputController.setAtacarFalso1();
            }
            if (this.inputController.getOpciones1()) {
                abrirOpciones();
                this.inputController.setOpcionesFalso1();
            }
        }

        if (this.JUGADORES[this.JUGADOR2].getPersonajeElegido().getVida() > 0) {
        	this.JUGADORES[this.JUGADOR2].getPersonajeElegido().setMoviendoDerecha(this.inputController.getDerecha2());
        	this.JUGADORES[this.JUGADOR2].getPersonajeElegido().setMoviendoIzquierda(this.inputController.getIzquierda2());
        	this.JUGADORES[this.JUGADOR2].getPersonajeElegido().setEstaSaltando(this.inputController.getSaltar2());
            if (this.inputController.getAtacar2()) {
            	this.JUGADORES[this.JUGADOR2].getPersonajeElegido().iniciarAtaque(this.musicaPartida.getVolumen(), delta, this.nivelActual);
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

            Iterator<EnemigoBase> iter = this.nivelActual.getEnemigos().iterator();
            while(iter.hasNext()) {
                EnemigoBase e = iter.next();
                if(personaje.getTipoAtaque() == TipoAtaque.MELEE && personaje.getEstaAtacando()) {
                    if (personaje.getHitbox().overlaps(e.getHitbox()) && e.getVida() > 0) {
                        e.reducirVida();
                        if (e.getVida() <= 0) {
                            this.nivelActual.agregarEnemigosMuertos(e);
                            e.remove();
                        }

                    }
                }
                 if (personaje.getTipoAtaque() == TipoAtaque.DISTANCIA) {
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


        boolean estaSobreElSuelo = this.nivelActual.detectarColision(new Rectangle(personaje.getX(), personaje.getY() - 1, 16, 16));
        personaje.guardarPosicionAnterior();
        personaje.actualizarGravedad(delta, estaSobreElSuelo, this.nivelActual.getAlturaMapa());

        float nuevaX = personaje.getNuevaX(delta);
        float nuevaY = personaje.getNuevaY(delta);

        Personaje otroPersonaje = esJugador1 ? this.JUGADORES[this.JUGADOR2].getPersonajeElegido() : this.JUGADORES[this.JUGADOR1].getPersonajeElegido();
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
            personaje.setVelocidadCaida(0);
            personaje.setY(personaje.getPrevY());
        }

        if (!colisionX || !colisionY) {
            float finalX = !colisionX ? nuevaX : personaje.getX();
            float finalY = !colisionY ? nuevaY : personaje.getY();
            personaje.aplicarMovimiento(finalX, finalY, delta, this.nivelActual.getAnchoMapa(), this.nivelActual.getAlturaMapa());
        }

        personaje.atacar(delta);

        for (EnemigoBase e : this.nivelActual.getEnemigos()) {
        	if(e.getTipoEnemigo() == TipoEnemigo.PERSEGUIDOR && e.getHitbox().overlaps(personaje.getHitbox())) {
        		personaje.reducirVida();
        	}
        	
            Iterator<Proyectil> it = e.getBalas().iterator();
            while (it.hasNext()) {
                Proyectil b = it.next();
                if (b.getHitbox().overlaps(personaje.getHitbox())) {
                		if(personaje.getEstaAtacando() && personaje.getTipoAtaque() == TipoAtaque.MELEE) {
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
            if (this.JUGADORES[this.JUGADOR1].getPersonajeElegido().getVida() <= 0 && this.JUGADORES[this.JUGADOR2].getPersonajeElegido().getVida() > 0) {
            	this.JUGADORES[this.JUGADOR1].getPersonajeElegido().setPosicion(this.JUGADORES[this.JUGADOR2].getPersonajeElegido().getX(), this.JUGADORES[this.JUGADOR2].getPersonajeElegido().getY());
            	this.JUGADORES[this.JUGADOR1].getPersonajeElegido().aumentarVida();
                this.gameOver1 = false;
            } else if (this.JUGADORES[this.JUGADOR2].getPersonajeElegido().getVida() <= 0 && this.JUGADORES[this.JUGADOR1].getPersonajeElegido().getVida() > 0) {
            	this.JUGADORES[this.JUGADOR2].getPersonajeElegido().setPosicion(this.JUGADORES[this.JUGADOR1].getPersonajeElegido().getX(), this.JUGADORES[this.JUGADOR1].getPersonajeElegido().getY());
            	this.JUGADORES[this.JUGADOR2].getPersonajeElegido().aumentarVida();
                this.gameOver2 = false;
            } else {
                  this.stage.getActors().removeValue(jugador.getPersonajeElegido(), true);
                  this.stage.addActor(jugador.cambiarPersonaje(esJugador1 ? this.nuevaX1 : this.nuevaX2,
                  esJugador1 ? this.nuevaY1 : this.nuevaY2));
              }
            this.gestorHUD.actualizar();
        }
    }
    public void abrirOpciones() {
        this.JUEGO.setScreen(new Opciones(this.JUEGO, this, this.musicaPartida));
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
    	for (int i = 0; i < JUGADORES.length; i++) {
            JUGADORES[i] = new Jugador(); 
        }
    }
    
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
