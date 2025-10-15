
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
    private boolean partidaIniciada = false; // Bandera para controlar la inicialización única
    
    public Partida(Game juego, Musica musica) {
        this.juego = juego;
        this.musicaPartida = musica;
        this.camara = new OrthographicCamera();
        this.camara.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport(), this.batch);
        this.stageHUD = new Stage(new ScreenViewport(), this.batch);
        
        // Inicialización de niveles
        this.nivelActual = this.niveles[this.indiceNivelActual];
    }
    
    // 🔹 MÉTODO DELEGADOR (Necesario para Proyectil.java)
    public boolean detectarColision(Rectangle hitbox) {
        if (this.nivelActual != null) {
            return this.nivelActual.detectarColision(hitbox);
        }
        return false;
    }

    private void inicializarNivel() {
        // 1. Delega la inicialización del mapa y enemigos
        this.nivelActual.restaurarEstadoCajas();
        this.nivelActual.crearEnemigos(); 
        
        // 2. Posiciona los personajes (ya deben estar asignados en show())
        this.personaje1.setPosicion(this.nivelActual.getInicioX1(), this.nivelActual.getInicioY1());
        this.personaje2.setPosicion(this.nivelActual.getInicioX2(), this.nivelActual.getInicioY2());
        
        // 3. Configurar Stage
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
            // Lógica de generación de personajes (se mantiene aquí, no en Nivel)
            if (!this.jugador1.getPartidaEmpezada()) {
                this.jugador1.generarPersonajeAleatorio();
            }
            if (!this.jugador2.getPartidaEmpezada()) {
                this.jugador2.generarPersonajeAleatorio();
            }
            this.inputController = new InputController();
            this.partidaIniciada = true;
        }

        // 🚨 CORRECCIÓN 1: ASIGNAR PERSONAJES ANTES DE HUD (Arregla NPE)
        this.personaje1 = this.jugador1.getPersonajeElegido();
        this.personaje2 = this.jugador2.getPersonajeElegido();
        
        inicializarHUD(); // Ahora tiene personajes válidos
        inicializarNivel(); // Ahora configura mapa, enemigos y Stage
    }


    @Override
    public void render(float delta) {
        
        // 1. PROCESAR INPUT (Copia del código del usuario)
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
        
        // 2. ACTUALIZAR PERSONAJES (Delegando la lógica del mundo)
        actualizarPersonaje(this.jugador1, this.personaje1, delta, true);
        actualizarPersonaje(this.jugador2, this.personaje2, delta, false);
        
        // 3. COMPROBAR VICTORIA (Delegando al Nivel)
        if(this.nivelActual.comprobarVictoria(this.nuevaX1, this.nuevaY1, this.nuevaX2, this.nuevaY2)) {
            this.victoria = true;
            this.indiceNivelActual++;
            this.nivelActual = this.niveles[indiceNivelActual];
            inicializarNivel();
        }
        
        // 4. ACTUALIZAR MUNDO Y CÁMARA (Delegando al Nivel)
        this.nivelActual.actualizarCamara(this.camara, this.personaje1, this.personaje2);
        actualizarHUD();
        nivelActual.limpiarEnemigosMuertos(); 
        
        // 5. RENDERIZADO DEL MAPA
        this.nivelActual.getMapRenderer().setView(this.camara);
        this.nivelActual.getMapRenderer().render();

        // 6. RENDERIZADO DE ACTORES (Personajes, Enemigos, Proyectiles)
        
        // 🚨 CORRECCIÓN 2: ARREGLAR DIBUJADO DEL STAGE
        // Sincronizar la cámara del Stage con la cámara del mundo para que los actores se muevan con el mapa
        OrthographicCamera stageCam = (OrthographicCamera) this.stage.getCamera();
        stageCam.position.set(this.camara.position.x, this.camara.position.y, this.camara.position.z);
        stageCam.zoom = this.camara.zoom;
        stageCam.update();
        
        this.batch.setProjectionMatrix(this.camara.combined); // Opcional, pero limpio si el batch es compartido

        // 7. ACTUALIZAR ENEMIGOS (Delegando al Nivel)
        if(!this.gameOver1 || !this.gameOver2) {
            for (Enemigo enemigo : this.nivelActual.getEnemigos()) { 
                 if (enemigo.getVida() > 0) {
                     // Añadir balas al stage aquí
                     for (Proyectil b : enemigo.getBalas()) {
                          this.stage.addActor(b);
                     }
                     // El enemigo recibe los límites del mapa del nivel
                     enemigo.actualizarIA(delta, this.personaje1, this.personaje2, this.musicaPartida.getVolumen(), this);
                 }
            }
        }
        
        this.stage.act(delta);
        this.stage.draw(); // Dibuja los actores con la cámara sincronizada

        this.stageHUD.act(delta);
        this.stageHUD.draw();
    }
    
    // Simplificamos la firma de este método, ya que los valores X/Y se obtienen internamente
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
        
        // Lógica de ataque y colisión con Enemigos (permanece como Lógica de Juego)
        if (personaje.getEstaAtacando()) {
            Iterator<Enemigo> iter = this.nivelActual.getEnemigos().iterator(); 
            while(iter.hasNext()) {
                 // ... (Tu lógica de colisión Melee y Distancia) ...
            }
        }
        
        // Detección de suelo DELEGADA
        boolean estaSobreElSuelo = detectarColision(new Rectangle(personaje.getX(), personaje.getY() - 1, 16, 16));

        personaje.guardarPosicionAnterior();
        // 🔹 Delegación de altura del mapa para la gravedad
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

        if (nuevaY < -190) { // Límite de caída al vacío
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
            // Pasa los límites del mapa del nivel
            personaje.aplicarMovimiento(finalX, finalY, delta, this.nivelActual.getAnchoMapa(), this.nivelActual.getAlturaMapa());
        }
        
        personaje.atacar(delta, this); 
        
        // 🔹 DELEGAR DESTRUCCIÓN DE TILE AL NIVEL, MANTENIENDO EL EFECTO DE JUEGO AQUÍ
        detectarYEliminarTile(personaje, jugador, esJugador1);

        // Colisión con proyectiles enemigos
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

    // 🔹 LÓGICA DEL JUEGO: Efecto de romper la caja (copiado del usuario)
    private void detectarYEliminarTile(Personaje personaje, Jugador jugador, boolean esJugador1) {
        // 1. Delega la detección y DESTRUCCIÓN de la caja al Nivel (Lógica del MUNDO)
        boolean cajaRota = this.nivelActual.detectarColision(personaje.getHitbox());
        
        if (cajaRota) {
            // 2. Si el Nivel confirma que se rompió, Partida aplica el efecto (Lógica del JUEGO)
            
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
            // ... (Copiar la lógica de inicialización del HUD desde Partida.show()) ...
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

    // ... (resize, pause, resume, hide) ...

    @Override
    public void dispose() {
        // Disponer todos los niveles
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}
}