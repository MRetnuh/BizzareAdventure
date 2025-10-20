package proyectiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import juego.Partida;

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

        try {
            FileHandle archivo = Gdx.files.internal(ruta);

            if (!archivo.exists()) {
                Gdx.app.error("DEBUG_PROY", "Archivo no encontrado: " + ruta + " -> usando textura roja por defecto");
                this.textura = crearTexturaError();
            } else {
                Gdx.app.log("DEBUG_PROY", "Cargando textura: " + ruta);
                this.textura = new Texture(archivo);
                Gdx.app.log("DEBUG_PROY", "Textura cargada OK: " + ruta);
            }
        } catch (Exception e) {
            Gdx.app.error("DEBUG_PROY", "Error cargando textura: " + ruta + " -> usando textura de error", e);
            this.textura = crearTexturaError();
        }
    }

    private Texture crearTexturaError() {
        Pixmap pixmap = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 0, 0, 1); // Rojo intenso
        pixmap.fill();
        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return tex;
    }

    @Override
    public void act(float delta) {
        if (!this.activa) return;

        float movimiento = this.velocidad * delta;
        if (this.haciaDerecha) {
            super.setX(getX() + movimiento);
        } else {
            super.setX(getX() - movimiento);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (this.activa && this.textura != null) {
            batch.draw(this.textura, super.getX(), super.getY(), super.getWidth(), super.getHeight());
        } else if (this.textura == null) {
            Gdx.app.error("DEBUG_PROY", "Intento de dibujar proyectil con textura nula");
        }
    }

    public Rectangle getHitbox() {
        return new Rectangle(super.getX(), super.getY(), super.getWidth(), super.getHeight());
    }

    public void desactivar() {
        this.activa = false;
        this.remove();
    }

    public void mover(float delta, Partida partida) {
        if (!activa) return;
    }

    public boolean isActivo() {
        return activa;
    }

    public void dispose() {
        if (this.textura != null) {
            this.textura.dispose();
            this.textura = null;
        }
    }
}
