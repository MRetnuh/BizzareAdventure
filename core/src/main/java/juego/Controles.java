package juego;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;


public class Controles implements InputProcessor {
	
    // Jugador 1
    private boolean w, a, s, d, m, p;
    private boolean arriba, izquierda, abajo, derecha, atacar, pausa;

    // Jugador 2
    private boolean up, left, down, right, espacio, enter;
    private boolean arriba2, izquierda2, abajo2, derecha2, atacar2, pausa2;

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            // Jugador 1
            case Keys.W: w = true; arriba = true; break;
            case Keys.A: a = true; izquierda = true; break;
            case Keys.S: s = true; abajo = true; break;
            case Keys.D: d = true; derecha = true; break;
            case Keys.M: m = true; atacar = true; break;
            case Keys.P: p = true; pausa = true; break;

            // Jugador 2
            case Keys.UP:    up = true; arriba2 = true; break;
            case Keys.LEFT:  left = true; izquierda2 = true; break;
            case Keys.DOWN:  down = true; abajo2 = true; break;
            case Keys.RIGHT: right = true; derecha2 = true; break;
            case Keys.SPACE: espacio = true; atacar2 = true; break;
            case Keys.ENTER: enter = true; pausa2 = true; break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            // Jugador 1
            case Keys.W: w = false; break;
            case Keys.A: a = false; break;
            case Keys.S: s = false; break;
            case Keys.D: d = false; break;
            case Keys.M: m = false; break;
            case Keys.P: p = false; break;

            // Jugador 2
            case Keys.UP:    arriba = false; break;
            case Keys.LEFT:  izquierda = false; break;
            case Keys.DOWN:  abajo = false; break;
            case Keys.RIGHT: derecha = false; break;
            case Keys.SPACE: espacio = false; break;
            case Keys.ENTER: enter = false; break;
        }
        return true;
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
    
    @Override public boolean touchCancelled(int x, int y, int pointer, int button) { 
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

    public boolean W() { 
    	return w; 
    }
    public boolean A() { 
    	return a; 
    }
    public boolean S() { 
    	return s; 
    }
    public boolean D() { 
    	return d; 
    }
    public boolean M() { 
    	return m; 
    }
    public boolean P() { 
    	return p; 
    }

  
    public boolean arriba() { 
    	return up; 
    }
    
    public boolean izquierda() { 
    	return left; 
    }
    public boolean abajo() { 
    	return down; 
    }
    
    public boolean derecha() { 
    	return right; 
    }
    
    public boolean espacio() { 
    	return space; 
    }
    
    public boolean enter() { 
    	return enter; 
    }
   
}
