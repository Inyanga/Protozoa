package com.inyanga.protozoa.prots;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.inyanga.protozoa.Timer;

/**
 * Created by Pavel Shakhtarin on 30.05.2018.
 */
public class Microfly extends Proto {

    private static final float MAX_SPEED = 400.0f;
    private static final float ACCELERATION = 400.0f;
    private static final float DRAG = 0.5f;
    private static final float MIN_MOVE_DELAY = 3.8f;
    private static final float MAX_MOVE_DELAY = 5.0f;

    private static final float PERIOD = 0.3f;

    private static final float SIZE_FACTOR = 1.0f / 480;



    public Microfly(Viewport viewport) {
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

        follow(ACCELERATION);
        timeToNextMove += delta + delta * MathUtils.random() * 2f;
        float moveDelay = MathUtils.random() * (MAX_MOVE_DELAY - MIN_MOVE_DELAY) + MIN_MOVE_DELAY;
        nextMove(moveDelay, ACCELERATION);

        velocity.x -= delta * DRAG * velocity.x;
        velocity.y -= delta * DRAG * velocity.y;

        float cyclePosition = Timer.cyclePosition(initialTime, PERIOD);
        float amplitude = size*18;
        float cos = MathUtils.cos(MathUtils.PI2  * cyclePosition);
        float sin = MathUtils.sin(MathUtils.PI2 * cyclePosition);

        velocity.x += amplitude * cos;
        velocity.y += amplitude * sin;
        velocity.clamp(0, MAX_SPEED);
        position.x += delta * velocity.x;
        position.y += delta * velocity.y;

        collideWithWalls(1.5f);
    }

    @Override
    public void render(ShapeRenderer renderer) {
        final int RENDER_COUNT = 3;
        renderer.set(ShapeRenderer.ShapeType.Line);
//        Color color = isFollow ? Color.BLACK : Color.PURPLE;
        Color color = Color.PURPLE;
        renderer.setColor(color);
            renderer.rect(position.x, position.y, size, size);

    }


}