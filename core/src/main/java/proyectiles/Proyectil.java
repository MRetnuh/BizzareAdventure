package proyectiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch; // Usar Batch en el método draw()
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Proyectil extends Actor {
    private float velocidad;
    private boolean haciaDerecha;
    private Texture textura;
    private boolean activa = true;
    private final int WIDTH = 16;
    private final int HEIGHT = 16;

    public Proyectil(float inicialX, float inicialY, boolean haciaDerecha, String ruta) {
        super.setX(inicialX);
        super.setY(inicialY);

        this.setWidth(this.WIDTH);
        this.setHeight(this.HEIGHT);

        this.haciaDerecha = haciaDerecha;
        this.velocidad = 400;
        this.textura = new Texture(Gdx.files.internal(ruta));
        
    }
    @Override
    public void act(float delta) {
        if (!this.activa) return; // Si no está activo, no se mueve

        float movimiento = this.velocidad * delta;

        if (this.haciaDerecha) {
            super.setX(getX() + movimiento);
        } else {
            super.setX(getX() - movimiento);
        }
    }
    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (this.activa) {
            // Usa getX() y getY() del Actor
            batch.draw(this.textura, super.getX(), super.getY(), super.getWidth(), super.getHeight());
        }
    }

    public Rectangle getHitbox() {
        return new Rectangle(super.getX(), super.getY(), super.getWidth(), super.getHeight());
    }

    public void desactivar() {
        this.activa = false;
        this.remove(); 
    }
}