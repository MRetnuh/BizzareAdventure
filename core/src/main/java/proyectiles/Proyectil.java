package proyectiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch; // Usar Batch en el método draw()
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Proyectil extends Actor {
    // Ya no necesitas 'private float x, y;' porque Actor lo maneja

    private float velocidad;
    private boolean haciaDerecha;
    private Texture textura;
    private boolean activa = true;
    private final int WIDTH = 16;
    private final int HEIGHT = 16;

    public Proyectil(float inicialX, float inicialY, boolean haciaDerecha, String ruta) {
        // 1. Establece la posición inicial usando los métodos de Actor
        this.setX(inicialX);
        this.setY(inicialY);

        // 2. Establece el tamaño del Actor para que la hitbox sea precisa
        this.setWidth(WIDTH);
        this.setHeight(HEIGHT);

        this.haciaDerecha = haciaDerecha;
        this.velocidad = 400;
        this.textura = new Texture(Gdx.files.internal(ruta));
        
    }
    @Override
    public void act(float delta) {
        if (!activa) return; // Si no está activo, no se mueve

        float movimiento = velocidad * delta;

        if (haciaDerecha) {
            // Mueve el Actor y actualiza su X automáticamente
            this.setX(getX() + movimiento);
        } else {
            this.setX(getX() - movimiento);
        }
    }

    /**
     * Sobreescribe el método draw() de Actor para renderizar el proyectil.
     * Batch es el tipo correcto para usar en Actor.draw().
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (activa) {
            // Usa getX() y getY() del Actor
            batch.draw(textura, getX(), getY(), getWidth(), getHeight());
        }
    }

    public Rectangle getHitbox() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    public void desactivar() {
        this.activa = false;
        this.remove(); 
    }
}