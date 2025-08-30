package juego;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;

public class Controles implements InputProcessor {
	
	
    // Jugador 1
    private boolean saltar1, izquierda1, derecha1, atacar1, pausa1;

    // Jugador 2
    private boolean saltar2, izquierda2, derecha2, atacar2, pausa2;

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.W: saltar1 = true; break;
            case Keys.A: izquierda1 = true; break;
            case Keys.D: derecha1 = true; break;
            case Keys.M: atacar1 = true; break;
            case Keys.P: pausa1 = true; break;

            case Keys.UP: saltar2 = true; break;
            case Keys.LEFT: izquierda2 = true; break;
            case Keys.RIGHT: derecha2 = true; break;
            case Keys.SPACE: atacar2 = true; break;
            case Keys.ENTER: pausa2 = true; break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Keys.W: saltar1 = false; break;
            case Keys.A: izquierda1 = false; break;
            case Keys.D: derecha1 = false; break;
            case Keys.M: atacar1 = false; break;
            case Keys.P: pausa1 = false; break;

            case Keys.UP: saltar2 = false; break;
            case Keys.LEFT: izquierda2 = false; break;
            case Keys.RIGHT: derecha2 = false; break;
            case Keys.SPACE: atacar2 = false; break;
            case Keys.ENTER: pausa2 = false; break;
        }
        return true;
    }

    // Jugador 1
    public boolean isSaltar1() { 
    	return saltar1; 
    }
    
    public boolean isIzquierda1() { 
    	return izquierda1; 
    }
    
    public boolean isDerecha1() { 
    	return derecha1; 
    }
    
    public boolean isAtacar1() { 
    	return atacar1; 
    }
    
    public boolean isPausa1() { 
    	return pausa1; 
    }

    // Jugador 2
    public boolean isSaltar2() { 
    	return saltar2; 
    }
    
    public boolean isIzquierda2() { 
    	return izquierda2; 
    }
    
    public boolean isDerecha2() { 
    	return derecha2; 
    }
    
    public boolean isAtacar2() { 
    	return atacar2; 
    }
    
    public boolean isPausa2() { 
    	return pausa2; 
    }
    @Override public boolean keyTyped(char character) { 
    	return false; 
    }
    
    @Override public boolean touchDown(int x, int y, int pointer, int button) { 
    	return false; 
    }
    
    @Override public boolean touchUp(int x, int y, int pointer, int button) { 
    	return false; 
    }
    
    @Override public boolean touchDragged(int x, int y, int pointer) { 
    	return false; 
    }
    
    @Override public boolean mouseMoved(int x, int y) { 
    	return false; 
    }
    
    @Override public boolean scrolled(float amountX, float amountY) { 
    	return false; 
    }
    
    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { 
        return false; 
    }
    
}
