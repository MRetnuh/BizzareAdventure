package input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import juego.Partida;
import personajes.Personaje;

public class InputController implements InputProcessor {

    private final Partida partida;
    private Personaje personaje1;
    private Personaje personaje2;

    public InputController(Partida partida, Personaje personaje1, Personaje personaje2) {
        this.partida = partida;
        this.personaje1 = personaje1;
        this.personaje2 = personaje2;
    }

    public void setPersonaje1(Personaje p) { this.personaje1 = p; }
    public void setPersonaje2(Personaje p) { this.personaje2 = p; }

    @Override
    public boolean keyDown(int keycode) {
    	
        if(personaje1.getVida() > 0) {
            switch(keycode) {
                case Input.Keys.W: personaje1.setEstaSaltando(true); break;
                case Input.Keys.A: personaje1.setMoviendoIzquierda(true); break;
                case Input.Keys.D: personaje1.setMoviendoDerecha(true); break;
                case Input.Keys.M: personaje1.iniciarAtaque(); break;
                case Input.Keys.P: 
                    partida.abrirOpciones();
                    personaje1.setMoviendoDerecha(false);
                    personaje1.setMoviendoIzquierda(false);
                    personaje1.setEstaSaltando(false);
                    personaje1.setEstaAtacando(false);
                    break;
            }
        }

        if(personaje2.getVida() > 0) {
            switch(keycode) {
                case Input.Keys.UP: personaje2.setEstaSaltando(true); break;
                case Input.Keys.LEFT: personaje2.setMoviendoIzquierda(true); break;
                case Input.Keys.RIGHT: personaje2.setMoviendoDerecha(true); break;
                case Input.Keys.ENTER: personaje2.iniciarAtaque(); break;
            }
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        // personaje1
        switch(keycode) {
            case Input.Keys.W: personaje1.setEstaSaltando(false); break;
            case Input.Keys.A: personaje1.setMoviendoIzquierda(false); break;
            case Input.Keys.D: personaje1.setMoviendoDerecha(false); break;
        }

        // personaje2
        switch(keycode) {
            case Input.Keys.UP: personaje2.setEstaSaltando(false); break;
            case Input.Keys.LEFT: personaje2.setMoviendoIzquierda(false); break;
            case Input.Keys.RIGHT: personaje2.setMoviendoDerecha(false); break;
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
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
}
