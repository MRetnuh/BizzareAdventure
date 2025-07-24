package personajes;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.Array;
public class Akame extends Personaje{

	public Akame() {
		super("Akame", 170);
	}
	
	  @Override
	    protected void cargarTexturas() {
	        Array<TextureRegion> framesDerecha = new Array<>();
	        for (int i = 1; i <= 4; i++) {
	            framesDerecha.add(new TextureRegion(new Texture(Gdx.files.internal("imagenes/personajes/akame/akame_derecha_moviendose_" + i + ".png"))));
	        }
	        animDerecha = new Animation<>(0.1f, framesDerecha, Animation.PlayMode.LOOP);

	        Array<TextureRegion> framesIzquierda = new Array<>();
	        for (int i = 1; i <= 4; i++) {
	            framesIzquierda.add(new TextureRegion(new Texture(Gdx.files.internal("imagenes/personajes/akame/akame_izquierda_moviendose_" + i + ".png"))));
	        }
	        animIzquierda = new Animation<>(0.1f, framesIzquierda, Animation.PlayMode.LOOP);

	        quietaDerecha = new TextureRegion(new Texture(Gdx.files.internal("imagenes/personajes/akame/akame_derecha_(detenida).png")));
	        quietaIzquierda = new TextureRegion(new Texture(Gdx.files.internal("imagenes/personajes/akame/akame_izquierda_(detenida).png")));
	    }
}
