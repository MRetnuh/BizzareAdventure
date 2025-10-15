package personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public enum FabricaDePersonajes {

    AKAME("Akame", 250, "EspadaCorte", 1, TipoAtaque.MELEE,
            "imagenes/personajes/akame/akame_derecha_moviendose_",
            "imagenes/personajes/akame/akame_izquierda_moviendose_",
            "imagenes/personajes/akame/ataque/akame_derecha_atacando_",
            "imagenes/personajes/akame/ataque/akame_izquierda_atacando_",
            "imagenes/personajes/akame/akame_derecha_(detenida).png",
            "imagenes/personajes/akame/akame_izquierda_(detenida).png", 4, 6
    ),
    LEONE("Leone", 230, "EspadaCorte", 1, TipoAtaque.DISTANCIA,
            "imagenes/personajes/leone/leone_derecha_moviendose_",
            "imagenes/personajes/leone/leone_izquierda_moviendose_",
            "imagenes/personajes/akame/ataque/akame_derecha_atacando_",
            "imagenes/personajes/akame/ataque/akame_izquierda_atacando_",
            "imagenes/personajes/leone/leone_derecha_(detenida).png",
            "imagenes/personajes/leone/leone_izquierda_(detenida).png", 4, 6
    ),
	
	HORNET("Hornet", 250, "EspadaCorte", 1, TipoAtaque.MELEE, 
			"imagenes/personajes/hornet/hornet_derecha_moviendose_",
            "imagenes/personajes/hornet/hornet_izquierda_moviendose_",
            "imagenes/personajes/akame/ataque/akame_derecha_atacando_",
            "imagenes/personajes/akame/ataque/akame_izquierda_atacando_",
            "imagenes/personajes/hornet/hornet_derecha_(detenida).png",
            "imagenes/personajes/hornet/hornet_izquierda_(detenida).png", 8, 6);

    private final String nombre;
    private final int velocidad;
    private final String nombreAtaque;
    private final int vida;
    private final String rutaMovDerecha, rutaMovIzquierda;
    private final String rutaAtaqueDerecha, rutaAtaqueIzquierda;
    private final String rutaQuietoDerecha, rutaQuietoIzquierda;
    private final TipoAtaque tipoAtaque;
    private int cantSpriteMovimiento;
    private int cantSpriteAtaque;
    FabricaDePersonajes(String nombre, int velocidad, String nombreAtaque, int vida,TipoAtaque tipoAtaque,
                        String rutaMovDerecha, String rutaMovIzquierda,
                        String rutaAtaqueDerecha, String rutaAtaqueIzquierda,
                        String rutaQuietoDerecha, String rutaQuietoIzquierda, int cantSpriteMovimiento, int cantSpriteAtaque) {
        this.nombre = nombre;
        this.velocidad = velocidad;
        this.nombreAtaque = nombreAtaque;
        this.vida = vida;
        this.rutaMovDerecha = rutaMovDerecha;
        this.rutaMovIzquierda = rutaMovIzquierda;
        this.rutaAtaqueDerecha = rutaAtaqueDerecha;
        this.rutaAtaqueIzquierda = rutaAtaqueIzquierda;
        this.rutaQuietoDerecha = rutaQuietoDerecha;
        this.rutaQuietoIzquierda = rutaQuietoIzquierda;
        this.tipoAtaque = tipoAtaque;
        this.cantSpriteAtaque = cantSpriteAtaque;
        this.cantSpriteMovimiento = cantSpriteMovimiento;
    }

    public Personaje crear() {
        Personaje pj = new Personaje(nombre, velocidad, nombreAtaque, vida, tipoAtaque) {
            @Override
            protected void cargarTexturas() {
                Array<TextureRegion> framesDerecha = new Array<>();
                for (int i = 1; i <= cantSpriteMovimiento; i++) {
                    framesDerecha.add(new TextureRegion(new Texture(Gdx.files.internal(rutaMovDerecha + i + ".png"))));
                }
                super.animDerecha = new Animation<>(0.1f, framesDerecha, Animation.PlayMode.LOOP);

                Array<TextureRegion> framesIzquierda = new Array<>();
                for (int i = 1; i <= cantSpriteMovimiento; i++) {
                    framesIzquierda.add(new TextureRegion(new Texture(Gdx.files.internal(rutaMovIzquierda + i + ".png"))));
                }
                super.animIzquierda = new Animation<>(0.1f, framesIzquierda, Animation.PlayMode.LOOP);

                Array<TextureRegion> framesAtaqueDer = new Array<>();
                for (int i = 1; i <= cantSpriteAtaque; i++) {
                    framesAtaqueDer.add(new TextureRegion(new Texture(Gdx.files.internal(rutaAtaqueDerecha + i + ".png"))));
                }
                super.animAtaqueDerecha = new Animation<>(0.1f, framesAtaqueDer, Animation.PlayMode.LOOP);

                Array<TextureRegion> framesAtaqueIzq = new Array<>();
                for (int i = 1; i <= cantSpriteAtaque; i++) {
                    framesAtaqueIzq.add(new TextureRegion(new Texture(Gdx.files.internal(rutaAtaqueIzquierda + i + ".png"))));
                }
                super.animAtaqueIzquierda = new Animation<>(0.1f, framesAtaqueIzq, Animation.PlayMode.LOOP);

                super.quietaDerecha = new TextureRegion(new Texture(Gdx.files.internal(rutaQuietoDerecha)));
                super.quietaIzquierda = new TextureRegion(new Texture(Gdx.files.internal(rutaQuietoIzquierda)));
            }
        };
        return pj;
    }
}
