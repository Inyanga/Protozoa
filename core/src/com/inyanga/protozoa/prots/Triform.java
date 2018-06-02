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

    private static final float PERIOD = 3.0f;

    private static final float SIZE_FACTOR = 1.0f / 84;
    private static final float ROTATION_FACTOR = 50.0f;
    private static final float ROTATION_OFFSET = 45.0f;


    public Triform(Viewport viewport) {
        super(viewport);
        init();
    }

    @Override
    public void init() {
        final float MAX_MUTATE = 1.0f;
        final float MIN_MUTATE = 0.75f;

        isTargetSet = false;
        float randomFactor = (MathUtils.random() * (MAX_MUTATE - MIN_MUTATE)) + MIN_MUTATE;
        size = randomFactor * SIZE_FACTOR * Math.min(viewport.getWorldWidth(), viewport.getWorldHeight());

        position = setRandomPosition();
        initialTime = TimeUtils.nanoTime();

        randomMove(ACCELERATION);
    }

    @Override
    public void update(float delta) {
        float acceleration = (isRunningToPoint) ? ACCELERATION : -ACCELERATION;
        follow(-ACCELERATION);
        timeToNextMove += delta + delta * MathUtils.random() * 2f;
        float moveDelay = MathUtils.random() * (MAX_MOVE_DELAY - MIN_MOVE_DELAY) + MIN_MOVE_DELAY;
        nextMove(moveDelay, ACCELERATION);


        velocity.x -= delta * DRAG * velocity.x;
        velocity.y -= delta * DRAG * velocity.y;
        float cyclePosition = Timer.cyclePosition(initialTime, PERIOD);
        rotation += ROTATION_FACTOR * MathUtils.cos(MathUtils.PI2 * cyclePosition);
        velocity.clamp(0, MAX_SPEED);
        position.x += delta * velocity.x;
        position.y += delta * velocity.y;

        collideWithWalls(4.5f);
    }

    @Override
    public void render(ShapeRenderer renderer) {
        final int RENDER_COUNT = 2;
        renderer.set(ShapeRenderer.ShapeType.Line);
//        Color color = isFollow ? Color.LIGHT_GRAY : Color.BLACK;
        Color color = Color.BLACK;
        renderer.setColor(color);

        for (int i = 1; i <= RENDER_COUNT; i++) {

            renderer.circle(position.x, position.y, size / i, 3);
            renderer.rect(position.x - size/2, position.y - size/2, size / 2, size / 2, size , size , 1.2f, 1.2f, rotation + ROTATION_OFFSET * i);
        }

    }



}