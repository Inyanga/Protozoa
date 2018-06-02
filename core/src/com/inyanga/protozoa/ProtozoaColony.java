package com.inyanga.protozoa;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.inyanga.protozoa.prots.Amoeba;
import com.inyanga.protozoa.prots.Triform;
import com.inyanga.protozoa.prots.Mold;
import com.inyanga.protozoa.prots.Plankton;
import com.inyanga.protozoa.prots.Proto;
import com.inyanga.protozoa.prots.Microfly;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavel Shakhtarin on 24.05.2018.
 */
public class ProtozoaColony extends GameAdapter {

    private static final int MAX_PROTS = 500;
    private static final int PLANKTON_FACTOR = 5;
    private static final int TRI_FACTOR = 6;
    private static final int TOUCH_THRESHOLD = 20;
    private static final float LOGO_HIDE_FACTOR = 0.3f;
    private Viewport viewport;
    private ShapeRenderer renderer;
    private float generateDelay;
    private float nextGeneration;
    private int dragTimer;
    private int prevX;
    private int prevY;

    private SpriteBatch batch;
    private Sprite logoSprite;
    private boolean isLogoVisible;
    private float logoAlpha;
    private BitmapFont font;

    private List<Proto> prots;


    @Override
    public void show() {
        viewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        renderer = new ShapeRenderer();
        prots = new ArrayList<Proto>();
        generateDelay = 0.0f;
        nextGeneration = 0.0f;
        dragTimer = 0;
        prevX = -1;
        prevY = -1;
        isLogoVisible = true;
        logoAlpha = 1.0f;
        batch = new SpriteBatch();
        Texture texture = new Texture(Gdx.files.internal("protozoa_logo_pic.png"));
        logoSprite = new Sprite(texture);
        logoSprite.setPosition(Gdx.graphics.getWidth() / 2 - logoSprite.getWidth() / 2,
                Gdx.graphics.getHeight() / 2 - logoSprite.getHeight() / 2);

        font = new BitmapFont();
        font.getData().setScale(1);
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
        nextGeneration += delta;
        if (nextGeneration >= generateDelay) {
            if (prots.size() < MAX_PROTS) {
                Amoeba amoeba = new Amoeba(viewport);
                prots.add(amoeba);
                if (prots.size() % 3 == 0) {
                    Proto mold = new Mold(viewport);
                    prots.add(mold);
                    Microfly microfly = new Microfly(viewport);
                    prots.add(microfly);
                }
                if (prots.size() % PLANKTON_FACTOR == 0) {
                    Plankton plankton = new Plankton(viewport);
                    prots.add(plankton);
                }
                if (prots.size() % TRI_FACTOR == 0) {
                    Triform triform = new Triform(viewport);
                    prots.add(triform);
                }
            }
            nextGeneration = 0.0f;
        }
        for (Proto p : prots) {
            p.update(delta);
            p.render(renderer);
        }
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

        float fps = 1 / delta;
        font.getData().setScale(1.2f);
        font.draw(batch, "" + (int) fps, 30, 30);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        renderer = new ShapeRenderer();
        viewport.update(width, height, true);
    }

    @Override
    public void hide() {
        renderer.dispose();
        batch.dispose();
        font.dispose();
    }



    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if (isLogoVisible) {
            for (Proto p : prots) {
                p.setBounds(viewport.getWorldWidth(), viewport.getWorldHeight(), 0, 0);
            }
            isLogoVisible = false;
        }

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        for (Proto p : prots) {
            p.setTarget(viewport.unproject(new Vector2(screenX, screenY)));
        }

        if (screenX == prevX && screenY == prevY) {
            dragTimer++;
        } else {
            dragTimer = 0;
            prevX = screenX;
            prevY = screenY;
        }

        if (dragTimer >= TOUCH_THRESHOLD) {
            for (Proto p : prots) {
                p.runToPoint(viewport.unproject(new Vector2(screenX, screenY)));
            }
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        for (Proto p : prots) {
            p.stopFollow();
        }
        return true;
    }

}