package com.inyanga.protozoa.prots;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.inyanga.protozoa.Timer;

/**
 * Created by Pavel Shakhtarin on 25.05.2018.
 */
public class Plankton extends Proto {

    private static final float MAX_SPEED = 310.0f;
    private static final float ACCELERATION = 150.0f;
    private static final float DRAG = 0.2f;
    private static final float MIN_MOVE_DELAY = 3.8f;
    private static final float MAX_MOVE_DELAY = 5.0f;

    private static final float PERIOD = 4.8f;

    private static final float SIZE_FACTOR = 1.0f / 250;

    private static final float ROTATION_FACTOR = 15.0f;



    private float angle;


    public Plankton(Viewport viewport) {
        super(viewport);
        init();
    }


    @Override
    public void init() {
        final float MAX_MUTATE = 1.0f;
        final float MIN_MUTATE = 0.6f;

        isTargetSet = false;
        float randomFactor = (MathUtils.random() * (MAX_MUTATE - MIN_MUTATE)) + MIN_MUTATE;
        size = randomFactor * SIZE_FACTOR * Math.min(viewport.getWorldWidth(), viewport.getWorldHeight());

        position = setRandomPosition();
        initialTime = TimeUtils.nanoTime();
        angle = randomMove(ACCELERATION);
    }

    @Override
    public void update(float delta) {

        follow(-ACCELERATION);

        timeToNextMove += delta + delta * MathUtils.random() * 2f;
        float moveDelay = MathUtils.random() * (MAX_MOVE_DELAY - MIN_MOVE_DELAY) + MIN_MOVE_DELAY;
        nextMove(moveDelay, ACCELERATION);

        float cyclePosition = Timer.cyclePosition(initialTime, PERIOD);
        velocity.x -= delta * DRAG * velocity.x;
        velocity.y -= delta * DRAG * velocity.y;
        rotation = (MathUtils.random(2) - 1) * ROTATION_FACTOR * MathUtils.cos(MathUtils.PI2 * cyclePosition);
        velocity.clamp(0, MAX_SPEED);

        position.x += delta * velocity.x - rotation * delta* size* 3 * MathUtils.sin(angle);;
        position.y += delta * velocity.y + rotation * delta * size* 3 * MathUtils.cos(angle);

        collideWithWalls(1.5f);
    }

    @Override
    public void render(ShapeRenderer renderer) {
        final int RENDER_COUNT = 3;
        renderer.set(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.PINK);
        renderer.circle(position.x, position.y, size);

    }





}