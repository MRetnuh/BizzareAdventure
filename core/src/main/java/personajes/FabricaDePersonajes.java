package personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public enum FabricaDePersonajes {

    AKAME("Akame", 250, "EspadaCorte", 1,
            "imagenes/personajes/akame/akame_derecha_moviendose_",
            "imagenes/personajes/akame/akame_izquierda_moviendose_",
            "imagenes/personajes/akame/ataque/akame_derecha_atacando_",
            "imagenes/personajes/akame/ataque/akame_izquierda_atacando_",
            "imagenes/personajes/akame/akame_derecha_(detenida).png",
            "imagenes/personajes/akame/akame_izquierda_(detenida).png"
    ),
    LEONE("Leone", 230, "EspadaCorte", 1,
            "imagenes/personajes/leone/leone_derecha_moviendose_",
            "imagenes/personajes/leone/leone_izquierda_moviendose_",
            "imagenes/personajes/akame/ataque/akame_derecha_atacando_",
            "imagenes/personajes/akame/ataque/akame_izquierda_atacando_",
            "imagenes/personajes/leone/leone_derecha_(detenida).png",
            "imagenes/personajes/leone/leone_izquierda_(detenida).png"
    );

    private final String nombre;
    private final int velocidad;
    private final String nombreAtaque;
    private final int vida;
    private final String rutaMovDerecha, rutaMovIzquierda;
    private final String rutaAtaqueDerecha, rutaAtaqueIzquierda;
    private final String rutaQuietoDerecha, rutaQuietoIzquierda;
    
    FabricaDePersonajes(String nombre, int velocidad, String nombreAtaque, int vida,
                        String rutaMovDerecha, String rutaMovIzquierda,
                        String rutaAtaqueDerecha, String rutaAtaqueIzquierda,
                        String rutaQuietoDerecha, String rutaQuietoIzquierda) {
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
    }

    public Personaje crear() {
        Personaje pj = new Personaje(nombre, velocidad, nombreAtaque, vida) {
            @Override
            protected void cargarTexturas() {
                Array<TextureRegion> framesDerecha = new Array<>();
                for (int i = 1; i <= 4; i++) {
                    framesDerecha.add(new TextureRegion(new Texture(Gdx.files.internal(rutaMovDerecha + i + ".png"))));
                }
                super.animDerecha = new Animation<>(0.1f, framesDerecha, Animation.PlayMode.LOOP);

                Array<TextureRegion> framesIzquierda = new Array<>();
                for (int i = 1; i <= 4; i++) {
                    framesIzquierda.add(new TextureRegion(new Texture(Gdx.files.internal(rutaMovIzquierda + i + ".png"))));
                }
                super.animIzquierda = new Animation<>(0.1f, framesIzquierda, Animation.PlayMode.LOOP);

                Array<TextureRegion> framesAtaqueDer = new Array<>();
                for (int i = 1; i <= 6; i++) {
                    framesAtaqueDer.add(new TextureRegion(new Texture(Gdx.files.internal(rutaAtaqueDerecha + i + ".png"))));
                }
                super.animAtaqueDerecha = new Animation<>(0.1f, framesAtaqueDer, Animation.PlayMode.LOOP);

                Array<TextureRegion> framesAtaqueIzq = new Array<>();
                for (int i = 1; i <= 6; i++) {
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
