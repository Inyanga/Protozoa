package com.inyanga.protozoa;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by Pavel Shakhtarin on 24.05.2018.
 */

public class GameScreen extends GameAdapter {

    private static final int TOUCH_THRESHOLD = 13;
    private static final float LOGO_HIDE_FACTOR = 0.6f;
    private static boolean isLogoVisible;
    private static boolean isTouched = false;
    private Viewport viewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    private ShapeRenderer renderer = new ShapeRenderer();
    private int dragTimer = 0;
    private SpriteBatch batch = new SpriteBatch();
    private Texture texture = new Texture(Gdx.files.internal("protozoa_logo_pic.png"));
    private Sprite logoSprite = new Sprite(texture);
    private float logoAlpha = 1.0f;
    private BitmapFont font = new BitmapFont();
    private ProtozoaColony colony = ProtozoaColony.getInstance();
    private Rectangle touchArea;
    private float touchAreaSize;
    private Music intro = Gdx.audio.newMusic(Gdx.files.internal("intro.mp3"));


    @Override
    public void show() {

        intro.play();

        isLogoVisible = true;
        logoSprite.setPosition(Gdx.graphics.getWidth() / 2 - logoSprite.getWidth() / 2,
                Gdx.graphics.getHeight() / 2 - logoSprite.getHeight() / 2);
        font.getData().setScale(1.5f);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        viewport.apply();
        Gdx.gl.glClearColor(255, 255, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.setProjectionMatrix(viewport.getCamera().combined);
        renderer.setAutoShapeType(true);
        renderer.begin();
        colony.render(delta);
        renderer.end();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        if (!isLogoVisible && logoAlpha > 0.0f) {
            logoAlpha -= delta * LOGO_HIDE_FACTOR;
            logoSprite.setAlpha(logoAlpha);
        }
        if (logoAlpha > 0.0f) {
            logoSprite.draw(batch);
        }

//        float fps = 1 / delta;
//        font.draw(batch, "" + (int) fps, 30, 30);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        renderer = new ShapeRenderer();
        viewport.update(width, height, true);
        colony.init(viewport, renderer);
        touchAreaSize = Math.min(viewport.getWorldWidth(), viewport.getWorldHeight()) / 15.0f;
    }

    @Override
    public void hide() {
        renderer.dispose();
        batch.dispose();
        font.dispose();
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector2 unproject = viewport.unproject(new Vector2(screenX, screenY));
        touchArea = new Rectangle(unproject.x - touchAreaSize / 2, unproject.y - touchAreaSize / 2, touchAreaSize, touchAreaSize);
        if (isLogoVisible) {
            colony.setFullBounds();
        }
        isTouched = true;
        isLogoVisible = false;

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        colony.setTarget(screenX, screenY);
        if (touchArea.contains(viewport.unproject(new Vector2(screenX, screenY)))) {
            dragTimer++;
        } else {
            dragTimer = 0;
        }
        if (dragTimer >= TOUCH_THRESHOLD) {
            colony.collapse(screenX, screenY);
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        colony.release();
        dragTimer = 0;
        isTouched = false;

        return true;
    }

    public static boolean isLogoVisible() {
        return isLogoVisible;
    }

    public static boolean isTouched() {
        return isTouched;
    }
}