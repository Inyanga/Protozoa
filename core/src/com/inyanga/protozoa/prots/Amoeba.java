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

    private static final float SIZE_FACTOR = 1.0f / 38;

    private static final float ROTATION_FACTOR = 15.0f;
    private static final float ROTATION_OFFSET = 45.0f;

    private static final float MAX_LIFE_TIME = 25.0f;
    private static final float MIN_LIFE_TIME = 15.0f;
    private static final float MAX_MUTATE = 1.0f;
    private static final float MIN_MUTATE = 0.55f;
    private static final float BIRTH_SIZE_FACTOR = 2.0f;
    private Color randomColor;

    public Amoeba(Viewport viewport, LivingProcess colony) {
        super(viewport,  colony);
        init();
    }

    @Override
    public void init() {
        super.init();

        randomColor = setRandomColor();

        float randomFactor = (MathUtils.random() * (MAX_MUTATE - MIN_MUTATE)) + MIN_MUTATE;
        maxSize = randomFactor * SIZE_FACTOR * Math.min(viewport.getWorldWidth(), viewport.getWorldHeight());
        lifeTime = (MathUtils.random() * (MAX_LIFE_TIME - MIN_LIFE_TIME)) + MIN_LIFE_TIME;
        position = setRandomPosition();
        initialTime = TimeUtils.nanoTime();
        randomMove(0, ACCELERATION);
    }

    @Override
    public void update(float delta) {
        if (size < maxSize && !isDying) {
            size += delta * BIRTH_SIZE_FACTOR;
        }
        follow(ACCELERATION);

        timeToNextMove += delta + delta * MathUtils.random() * 2f;
        moveDelay = MathUtils.random() * (MAX_MOVE_DELAY - MIN_MOVE_DELAY) + MIN_MOVE_DELAY;
        randomMove(moveDelay, ACCELERATION);
        velocity.x -= delta * DRAG * velocity.x;
        velocity.y -= delta * DRAG * velocity.y;
        float cyclePosition = Timer.cyclePosition(initialTime, PERIOD);
        rotation += MathUtils.random() * ROTATION_FACTOR * MathUtils.cos(MathUtils.PI2 * cyclePosition);
        velocity.clamp(0, MAX_SPEED);
        position.x += delta * velocity.x;
        position.y += delta * velocity.y;
        living(delta);

        collideWithWalls(1.5f);
    }

    @Override
    public void render(ShapeRenderer renderer) {
        final int RENDER_COUNT = 3;
        renderer.set(ShapeRenderer.ShapeType.Line);

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