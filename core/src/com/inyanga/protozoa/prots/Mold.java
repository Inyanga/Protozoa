package com.inyanga.protozoa.prots;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.inyanga.protozoa.Timer;

/**
 * Created by Pavel Shakhtarin on 25.05.2018.
 */
public class Mold extends Proto {

    private static final float MAX_SPEED = 350.0f;
    private static final float ACCELERATION = 70.0f;
    private static final float DRAG = 0.8f;
    private static final float MIN_MOVE_DELAY = 4.8f;
    private static final float MAX_MOVE_DELAY = 7.5f;

    private static final float PERIOD = 3.0f;

    private static final float SIZE_FACTOR = 1.0f / 55;


    private static final float ROTATION_OFFSET = 15.0f;


    private float cyclePosition;


    private Vector2 satellitePos;
    private float direction;

    public Mold(Viewport viewport) {
        super(viewport);
        init();
    }


    @Override
    public void init() {
        final float MAX_MUTATE = 1.0f;
        final float MIN_MUTATE = 0.5f;

        isTargetSet = false;
        float randomFactor = (MathUtils.random() * (MAX_MUTATE - MIN_MUTATE)) + MIN_MUTATE;
        size = randomFactor * SIZE_FACTOR * Math.min(viewport.getWorldWidth(), viewport.getWorldHeight());
        direction = (((int) MathUtils.random()) == 0) ? 1.0f : -1.0f;
        position = setRandomPosition();
        satellitePos = new Vector2();
        initialTime = TimeUtils.nanoTime();
        randomMove(ACCELERATION);
    }

    @Override
    public void update(float delta) {

        cyclePosition = Timer.cyclePosition(initialTime, PERIOD);
        follow(ACCELERATION);

        timeToNextMove += delta + delta * MathUtils.random() * 2f;
        float moveDelay = MathUtils.random() * (MAX_MOVE_DELAY - MIN_MOVE_DELAY) + MIN_MOVE_DELAY;
        nextMove(moveDelay, ACCELERATION);


        velocity.x -= delta * DRAG * velocity.x;
        velocity.y -= delta * DRAG * velocity.y;
        velocity.clamp(0, MAX_SPEED);
        position.x += delta * velocity.x;
        position.y += delta * velocity.y;


        collideWithWalls(1.5f);
    }

    @Override
    public void render(ShapeRenderer renderer) {
        final int RENDER_COUNT = 9;
        renderer.set(ShapeRenderer.ShapeType.Line);

        Color color = Color.BLACK;
        renderer.setColor(color);
        renderer.circle(position.x, position.y, size);
        renderer.circle(position.x, position.y, size / 2f);
        renderer.circle(position.x, position.y, size / 3f);
        renderer.circle(position.x, position.y, size / 5f);
        renderer.circle(position.x, position.y, size / 10f);


        for (int i = 1; i < RENDER_COUNT; i++) {
            satellitePos.x = position.x + size * 1.2f * -MathUtils.cos(MathUtils.PI2 * cyclePosition + ROTATION_OFFSET * i) * direction;
            satellitePos.y = position.y + size * 1.2f * -MathUtils.sin(MathUtils.PI2 * cyclePosition + ROTATION_OFFSET * i) * direction;

            renderer.circle(satellitePos.x, satellitePos.y, size / 15.0f);
        }
    }


}