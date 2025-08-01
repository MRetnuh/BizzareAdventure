package audios;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class EfectoSonido {
    private Sound efecto;

    public EfectoSonido(String nombreArchivo) {
        efecto = Gdx.audio.newSound(Gdx.files.internal("sonidos/" + nombreArchivo + ".mp3"));
    }

    public void reproducir() {
        efecto.play();
    }

    public void reproducir(float volumen) {
        efecto.play(volumen);
    }

    public void liberar() {
        efecto.dispose();
    }
}
