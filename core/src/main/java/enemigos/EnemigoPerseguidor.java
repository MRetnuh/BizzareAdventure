package enemigos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import niveles.NivelBase;
import personajes.Personaje;
import personajes.TipoAtaque;

public class EnemigoPerseguidor extends EnemigoBase {
	

    public EnemigoPerseguidor(String nombre, float x, float y) {
        super(nombre, 90, "EspadaCorte", 1, TipoAtaque.MELEE);
        setPosition(x, y);
    }

    @Override
    protected void cargarTexturas() {
        Array<TextureRegion> framesDerecha = new Array<>();
        for (int i = 1; i <= 4; i++) {
            framesDerecha.add(new TextureRegion(new Texture(Gdx.files.internal(
                    "imagenes/personajes/leone/leone_derecha_moviendose_" + i + ".png"))));
        }
        super.animDerecha = new Animation<>(0.1f, framesDerecha, Animation.PlayMode.LOOP);

        Array<TextureRegion> framesIzquierda = new Array<>();
        for (int i = 1; i <= 4; i++) {
            framesIzquierda.add(new TextureRegion(new Texture(Gdx.files.internal(
                    "imagenes/personajes/leone/leone_izquierda_moviendose_" + i + ".png"))));
        }
        super.animIzquierda = new Animation<>(0.1f, framesIzquierda, Animation.PlayMode.LOOP);

        super.quietaDerecha = new TextureRegion(new Texture(Gdx.files.internal(
                "imagenes/personajes/leone/leone_derecha_(detenida).png")));
        super.quietaIzquierda = new TextureRegion(new Texture(Gdx.files.internal(
                "imagenes/personajes/leone/leone_izquierda_(detenida).png")));
    }
    @Override
    public void actualizarIA(float delta, Personaje jugador1, Personaje jugador2, float volumen, NivelBase nivel){
        seleccionarObjetivo(jugador1, jugador2);
        if (objetivoActual == null) super.patrullar(delta, nivel);
        else {
        perseguir(delta, nivel);
        }
    }

    private void perseguir(float delta, NivelBase nivel) {
        if (objetivoActual == null) return;

        float direccion = objetivoActual.getX() > getX() ? 1 : -1;
        float nuevaX = getX() + direccion * getVelocidad() * delta;

        Rectangle hitbox = new Rectangle(nuevaX, getY(), getWidth(), getHeight());

        if (!nivel.detectarColision(hitbox)) {
            aplicarMovimiento(nuevaX, getY(), delta, 10000, 1000);
        }

        mirandoDerecha = direccion > 0;
        estaMoviendose = true;
    }

   

	@Override
	public float getVelocidad() {
		return 400;
	}
}
