package io.github.some;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
   private Music music;
    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("akame_de_frente_(detenida).png");
        music = Gdx.audio.newMusic(Gdx.files.internal("primeraisla.mp3"));
        music.setLooping(true);   // Repetir en bucle
        music.setVolume(0.5f);    // Volumen (0.0 a 1.0)
        music.play();        
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }
}
