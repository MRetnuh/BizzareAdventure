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
    this.stage = new Stage();
    Gdx.input.setInputProcessor(this.stage);

    this.musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("musica/"+ this.nombreMusica + ".mp3"));
    this.musicaFondo.setLooping(true);
    this.musicaFondo.setVolume(this.volumen);
    this.musicaFondo.play();
}

public void subirVolumen() {
    this.volumen = Math.min(1f, this.volumen + 0.1f);
    this.musicaFondo.setVolume(this.volumen);
}

public void bajarVolumen() {
    this.volumen = Math.max(0f, volumen - 0.1f);
    this.musicaFondo.setVolume(volumen);
}

public void setVolumen(float nuevoVolumen) {
	this.volumen = Math.max(0f, Math.min(1f, nuevoVolumen));
	if (this.musicaFondo != null) {
		this.musicaFondo.setVolume(this.volumen);
	}
}


public void cambiarMusica(String nombreArchivo) {
	this.musicaFondo.stop();
	this.nombreMusica = nombreArchivo;
	show();
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