package input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

import juego.Partida;
import personajes.Personaje;

public class InputController implements InputProcessor {

    private final Partida partida;
    private final Personaje personaje;

    public InputController(Partida partida, Personaje personaje) {
        this.partida = partida;
        this.personaje = personaje;
    }

    @Override
    public boolean keyDown(int keycode) {
    	if(this.personaje.getVida() > 0) {
        switch (keycode) {
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                this.personaje.setMoviendoDerecha(true);
                break;

            case Input.Keys.A:
            case Input.Keys.LEFT:
                this.personaje.setMoviendoIzquierda(true);
                break;

            case Input.Keys.W:
            case Input.Keys.UP:
            	this.personaje.setEstaSaltando(true);
                break;

           /* case Input.Keys.M: 
            	this.personaje.iniciarAtaque();
                break;*/
                
            case Input.Keys.P:
                this.partida.abrirOpciones();
                this.personaje.setMoviendoIzquierda(false);
                this.personaje.setMoviendoDerecha(false);
                break;
            	
        }
    	}
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.D:
            case Input.Keys.RIGHT:
            	 this.personaje.setMoviendoDerecha(false);
            	 break;
            	 
            case Input.Keys.A:
            case Input.Keys.LEFT:
            	 this.personaje.setMoviendoIzquierda(false);
                break;

            case Input.Keys.W:
            case Input.Keys.UP:
        		this.personaje.setEstaSaltando(false);
            		
        }
        return true;
    }

    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }

	@Override
	public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}
}
