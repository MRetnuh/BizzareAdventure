package proyectiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Proyectil {
    private float x, y;
    private float velocidad;
    private boolean haciaDerecha;
    private Texture textura;
    private boolean activa = true;

    public Proyectil(float x, float y, boolean haciaDerecha) {
        this.x = x;
        this.y = y;
        this.haciaDerecha = haciaDerecha;
        this.velocidad = 300;
            this.textura = new Texture(Gdx.files.internal("imagenes/personajes/enemigo/ataque/Bala_Derecha.png"));
    }

    public void actualizar(float delta) {
        if (haciaDerecha) {
            x += velocidad * delta;
        } else {
            x -= velocidad * delta;
        }
    }

    public void dibujar(SpriteBatch batch) {
        if (activa) {
            batch.draw(textura, x, y, 16, 16);
        }
    }

    public Rectangle getHitbox() {
        return new Rectangle(x, y, 16, 16);
    }

    public boolean isActiva() {
        return activa;
    }

    public void desactivar() {
        activa = false;
    }

    public void dispose() {
        textura.dispose();
    }

	public float getX() {
		return this.x;
	}
}
