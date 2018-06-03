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


    private Color randomColor;
    private Vector2 satellitePos;
    private int direction;

    public Mold(Viewport viewport) {
        super(viewport);
        init();
    }


    @Override
    public void init() {
        final float MAX_MUTATE = 1.0f;
        final float MIN_MUTATE = 0.5f;

        switch (MathUtils.random(3)) {
            case 0:
                randomColor = Color.valueOf("26C6DA00");
                break;
            case 1:
                randomColor = Color.valueOf("EF9A9A00");
                break;
            case 2:
                randomColor = Color.valueOf("01579B00");
                break;
            case 3:
                randomColor = Color.BLACK;
                break;
            default:
                randomColor = Color.BLACK;
    }
        isTargetSet = false;
        direction = MathUtils.random(1);
        float mutationFactor = (MathUtils.random() * (MAX_MUTATE - MIN_MUTATE)) + MIN_MUTATE;
        size = mutationFactor * SIZE_FACTOR * Math.min(viewport.getWorldWidth(), viewport.getWorldHeight());
        position = setRandomPosition();
        satellitePos = new Vector2();
        initialTime = TimeUtils.nanoTime();
        randomMove(0, ACCELERATION);
    }

    @Override
    public void update(float delta) {

        cyclePosition = Timer.cyclePosition(initialTime, PERIOD);
        follow(ACCELERATION);

        timeToNextMove += delta + delta * MathUtils.random() * 2f;
        moveDelay = MathUtils.random() * (MAX_MOVE_DELAY - MIN_MOVE_DELAY) + MIN_MOVE_DELAY;
        randomMove(moveDelay, ACCELERATION);


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


        renderer.setColor(Color.BLACK);
        renderer.circle(position.x, position.y, size);
        renderer.circle(position.x, position.y, size / 2f);


        renderer.setColor(randomColor);
        renderer.circle(position.x, position.y, size / 3f);
        renderer.circle(position.x, position.y, size / 5f);
        renderer.circle(position.x, position.y, size / 10f);


        for (int i = 1; i < RENDER_COUNT; i++) {
            if( direction == 0) {
                satellitePos.x = position.x + size * 1.2f * MathUtils.sin(MathUtils.PI2 * cyclePosition + ROTATION_OFFSET * i) ;
                satellitePos.y = position.y + size * 1.2f * MathUtils.cos(MathUtils.PI2 * cyclePosition + ROTATION_OFFSET * i) ;
            } else {
                satellitePos.x = position.x + size * 1.2f * MathUtils.cos(MathUtils.PI2 * cyclePosition + ROTATION_OFFSET * i) ;
                satellitePos.y = position.y + size * 1.2f * MathUtils.sin(MathUtils.PI2 * cyclePosition + ROTATION_OFFSET * i) ;
            }


            renderer.circle(satellitePos.x, satellitePos.y, size / 15.0f);
        }
    }


}