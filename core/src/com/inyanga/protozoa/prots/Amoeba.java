package com.inyanga.protozoa.prots;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.inyanga.protozoa.Timer;

/**
 * Created by Pavel Shakhtarin on 24.05.2018.
 */
public class Amoeba extends Proto {

    private static final float MAX_SPEED = 350.0f;
    private static final float ACCELERATION = 150.0f;
    private static final float DRAG = 1.2f;
    private static final float MIN_MOVE_DELAY = 3.8f;
    private static final float MAX_MOVE_DELAY = 5.0f;

    private static final float PERIOD = 3.0f;

    private static final float SIZE_FACTOR = 1.0f / 28;

    private static final float ROTATION_FACTOR = 15.0f;
    private static final float ROTATION_OFFSET = 45.0f;

    private Color randomColor;

    public Amoeba(Viewport viewport) {
        super(viewport);
        init();
    }

    @Override
    public void init() {
        final float MAX_MUTATE = 1.0f;
        final float MIN_MUTATE = 0.55f;
        switch (MathUtils.random(2)) {
            case 0:
                randomColor = Color.valueOf("A5D6A700");
                break;
            case 1:
                randomColor = Color.BLACK;
                break;
            case 2:
                randomColor = Color.valueOf("B39DDB00");
                break;
            default:
                randomColor = Color.BLACK;
        }
        isTargetSet = false;
        float randomFactor = (MathUtils.random() * (MAX_MUTATE - MIN_MUTATE)) + MIN_MUTATE;
        size = randomFactor * SIZE_FACTOR * Math.min(viewport.getWorldWidth(), viewport.getWorldHeight());

        position = setRandomPosition();
        initialTime = TimeUtils.nanoTime();

        randomMove(ACCELERATION);
    }

    @Override
    public void update(float delta) {
        follow(ACCELERATION);

        timeToNextMove += delta + delta * MathUtils.random() * 2f;
        float moveDelay = MathUtils.random()* (MAX_MOVE_DELAY - MIN_MOVE_DELAY) + MIN_MOVE_DELAY;
        nextMove(moveDelay, ACCELERATION);


        velocity.x -= delta * DRAG * velocity.x;
        velocity.y -= delta * DRAG * velocity.y;
        float cyclePosition = Timer.cyclePosition(initialTime, PERIOD);
        rotation += MathUtils.random() * ROTATION_FACTOR * MathUtils.cos(MathUtils.PI2 * cyclePosition);
        velocity.clamp(0, MAX_SPEED);
        position.x += delta * velocity.x;
        position.y += delta * velocity.y;

        collideWithWalls(1.5f);
    }

    @Override
    public void render(ShapeRenderer renderer) {
        final int RENDER_COUNT = 3;
        renderer.set(ShapeRenderer.ShapeType.Line);
//        Color color = isFollow ? Color.LIGHT_GRAY : Color.BLACK;

        renderer.setColor(Color.BLACK);
        renderer.rect(position.x, position.y, size / 2, size / 2, size, size, 1.0f, 1.0f, rotation);
        renderer.rect(position.x, position.y, size / 2, size / 2, size, size, 1.0f, 1.0f, rotation + ROTATION_OFFSET / 2);
//        renderer.rect(position.x, position.y, size / 2, size / 2, size, size, 1.0f, 1.0f, rotation + ROTATION_OFFSET / 3);
        renderer.rect(position.x, position.y, size / 2, size / 2, size, size, 0.8f, 0.8f, rotation + ROTATION_OFFSET / 4);
        for (int i = 1; i <= RENDER_COUNT; i++) {

            renderer.rect(position.x, position.y, size / 2, size / 2, size, size, 1.0f, 1.0f, -rotation + ROTATION_OFFSET * i);
            renderer.rect(position.x, position.y, size / 2, size / 2, size, size, 1.0f / i, 1.0f / i, rotation + ROTATION_OFFSET * i * 2);


        }
        renderer.setColor(randomColor);
        renderer.rect(position.x, position.y, size / 2, size / 2, size, size, 0.3f , 0.3f , -rotation + ROTATION_OFFSET  * 3);
        renderer.rect(position.x, position.y, size / 2, size / 2, size, size, 0.2f , 0.2f , rotation + ROTATION_OFFSET  * 4);
    }

}