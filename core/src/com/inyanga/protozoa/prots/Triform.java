package com.inyanga.protozoa.prots;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.inyanga.protozoa.Timer;

/**
 * Created by Pavel Shakhtarin on 31.05.2018.
 */
public class Triform extends Proto {

    private static final float MAX_SPEED = 350.0f;
    private static final float ACCELERATION = 270.0f;
    private static final float DRAG = 5.7f;
    private static final float MIN_MOVE_DELAY = 4.0f;
    private static final float MAX_MOVE_DELAY = 5.0f;

    private static final float PERIOD = 15.0f;

    private static final float SIZE_FACTOR = 1.0f / 44;
    private static final float ROTATION_FACTOR = 3.0f;
    private static final float BIRTH_SIZE_FACTOR = 1.0f;

    private static final float MAX_LIFE_TIME = 25.0f;
    private static final float MIN_LIFE_TIME = 15.0f;
    private static final float MAX_MUTATE = 1.0f;
    private static final float MIN_MUTATE = 0.75f;


    public Triform(Viewport viewport, LivingProcess colony) {
        super(viewport,  colony);
        init();
    }

    @Override
    public void init() {
        super.init();

        isTargetSet = false;
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
        rotation += ROTATION_FACTOR * MathUtils.cos((MathUtils.PI2) * cyclePosition);
        velocity.clamp(0, MAX_SPEED);
        position.x += delta * velocity.x;
        position.y += delta * velocity.y;
        living(delta);

        collideWithWalls(4.5f);
    }

    @Override
    public void render(ShapeRenderer renderer) {
        final int RENDER_COUNT = 10;
        renderer.set(ShapeRenderer.ShapeType.Line);

        renderer.setColor(Color.BLACK);



//            renderer.circle(position.x+ size/2, position.y+ size/2, size, 8);

        for (int i =2; i < RENDER_COUNT; i++) {
            renderer.rect(position.x, position.y, size / 2, size / 2, size / i + 1, size / i + 1, 1.0f, 1.0f, rotation * i);
//            renderer.rect(position.x, position.y, size / 2, size / 2, size / i + 1, size / i + 1, 1.0f, 1.0f, rotation  +  ROTATION_OFFSET * 2);
        }
    }



}