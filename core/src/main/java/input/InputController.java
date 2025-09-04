package input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

import juego.Partida;
import personajes.Personaje;

public class InputController implements InputProcessor {
	 
    private final Partida partida;
    private Personaje personaje;
    private final int teclaIzquierda;
    private final int teclaDerecha;
    private final int teclaSaltar;
    private float volumen;
    private final int teclaAtacar;
    public InputController(Partida partida, Personaje personaje, int izquierda, int derecha, int saltar, int atacar, float volumen) {
    	this.partida = partida;
    	this.personaje = personaje;
    	this.teclaIzquierda = izquierda;
    	this.teclaDerecha = derecha;
    	this.teclaSaltar = saltar;
    	this.volumen = volumen;
    	this.teclaAtacar = atacar;
}
    
    public void setPersonaje(Personaje personaje) {
        this.personaje = personaje;
    }

    @Override
    public boolean keyDown(int keycode) {
    	if(this.personaje.getVida() > 0) {
        	if(keycode == this.teclaDerecha) {
                this.personaje.setMoviendoDerecha(true);
        	}
        	else if(keycode == this.teclaIzquierda) {
                this.personaje.setMoviendoIzquierda(true);
        	}
        	else if(keycode == this.teclaSaltar) {
            	this.personaje.setEstaSaltando(true);
        	}
        	else if(keycode == Input.Keys.P) {
                this.partida.abrirOpciones();
                this.personaje.setMoviendoIzquierda(false);
                this.personaje.setMoviendoDerecha(false);
                this.personaje.setEstaSaltando(false);
                this.personaje.setEstaAtacando(false);
        	}
            	
        	 else if(keycode == this.teclaAtacar) {
                 this.personaje.iniciarAtaque(volumen);
             }
             
        }
    	
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
    	if(keycode == this.teclaDerecha) {
            this.personaje.setMoviendoDerecha(false);
    	}
    	else if(keycode == this.teclaIzquierda) {
            this.personaje.setMoviendoIzquierda(false);
    	}
    	else if(keycode == this.teclaSaltar) {
        	this.personaje.setEstaSaltando(false);
    	}
        
    return false;
}

    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }

	@Override
	public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
		return false;
	}
}