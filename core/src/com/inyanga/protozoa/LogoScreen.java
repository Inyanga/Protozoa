package com.inyanga.protozoa;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by Pavel Shakhtarin on 25.05.2018.
 */
public class LogoScreen implements Screen {

    private static final float GAME_SCREEN_TIME = 9.0f;

    private Viewport viewport;
    private long initialTime;
    private Game game;
    private SpriteBatch batch;
    private BitmapFont font;


    public LogoScreen(Game game) {
        this.game = game;
    }




    @Override
    public void show() {
        initialTime = TimeUtils.nanoTime();
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        // Create the default font
        font = new BitmapFont();
        // Scale it up
        font.getData().setScale(13);
        font.setColor(Color.BLACK);
        // Set the filter
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    @Override
    public void render(float delta) {
        viewport.apply();
        Gdx.gl.glClearColor(255, 255, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        font.draw(batch, "PROTOZOA", viewport.getWorldWidth()/2, viewport.getWorldHeight()/2);

        batch.end();
        if(Timer.elapsedSeconds(initialTime) >= GAME_SCREEN_TIME) {
            game.setScreen(new ProtozoaColony());
        }

    }

    @Override
    public void resize(int width, int height) {
        batch = new SpriteBatch();
        font = new BitmapFont();
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
