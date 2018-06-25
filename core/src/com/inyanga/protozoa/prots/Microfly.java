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

    private static final float MAX_LIFE_TIME = 45.0f;
    private static final float MIN_LIFE_TIME = 25.0f;
    private static final float MAX_MUTATE = 1.0f;
    private static final float MIN_MUTATE = 0.75f;

    private Color randomColor;


    public Microfly(Viewport viewport,  LivingProcess colony) {
        super(viewport, colony);
        init();
    }

    @Override
    public void init() {
        super.init();

        switch (MathUtils.random(1)) {
            case 0:
                randomColor = Color.PURPLE;
                break;
            case 1:
                randomColor = Color.valueOf("D5000000");
                break;
            default:
                randomColor = Color.PURPLE;
        }
        isTargetSet = false;
        float randomFactor = (MathUtils.random() * (MAX_MUTATE - MIN_MUTATE)) + MIN_MUTATE;
        size = randomFactor * SIZE_FACTOR * Math.min(viewport.getWorldWidth(), viewport.getWorldHeight());
        lifeTime = (MathUtils.random() * (MAX_LIFE_TIME - MIN_LIFE_TIME)) + MIN_LIFE_TIME;
        position = setRandomPosition();
        initialTime = TimeUtils.nanoTime();
        randomMove(0, ACCELERATION);
    }

    @Override
    public void update(float delta) {

        follow(ACCELERATION);
        timeToNextMove += delta + delta * MathUtils.random() * 2f;
        moveDelay = MathUtils.random() * (MAX_MOVE_DELAY - MIN_MOVE_DELAY) + MIN_MOVE_DELAY;
        randomMove(moveDelay, ACCELERATION);

        velocity.x -= delta * DRAG * velocity.x;
        velocity.y -= delta * DRAG * velocity.y;

        float cyclePosition = Timer.cyclePosition(initialTime, PERIOD);
        float amplitude = size * 18;
        float cos = MathUtils.cos(MathUtils.PI2 * cyclePosition);
        float sin = MathUtils.sin(MathUtils.PI2 * cyclePosition);

        velocity.x += amplitude * cos;
        velocity.y += amplitude * sin;
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
        renderer.setColor(randomColor);
        renderer.rect(position.x, position.y, size, size);

    }


}