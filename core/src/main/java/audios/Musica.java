package audios;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Musica implements Screen{
private String nombreMusica;
private Music musicaFondo;
private Stage stage;
private float volumen = 0.5f;

public Musica(String nombreMusica) {
    this.nombreMusica = nombreMusica;
}

@Override
public void show() {
    stage = new Stage();
    Gdx.input.setInputProcessor(stage);

    musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("musica/"+ nombreMusica + ".mp3"));
    musicaFondo.setLooping(true);
    musicaFondo.setVolume(volumen);
    musicaFondo.play();
}

public void detenerMusica() {
    musicaFondo.stop();
}

public void subirVolumen() {
    volumen = Math.min(1f, volumen + 0.1f);
    musicaFondo.setVolume(volumen);
}

public void bajarVolumen() {
    volumen = Math.max(0f, volumen - 0.1f);
    musicaFondo.setVolume(volumen);
}

public void silenciar() {
      musicaFondo.setVolume(0f);
}

@Override
public void render(float delta) {

}

@Override
public void resize(int width, int height) {

}

@Override
public void pause() {
    // TODO Auto-generated method stub

}

@Override
public void resume() {

}

@Override
public void hide() {

}

@Override
public void dispose() {
}

public float getVolumen() {
    return this.volumen;
}
}