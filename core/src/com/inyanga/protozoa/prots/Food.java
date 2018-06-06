package com.inyanga.protozoa.prots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.inyanga.protozoa.Timer;

/**
 * Created by Pavel Shakhtarin on 03.06.2018.
 */
public class Food extends Proto {
    private static final float MAX_SPEED = 250.0f;
    private static final float ACCELERATION = 50.0f;
    private static final float DRAG = 1.2f;
    private static final float MIN_MOVE_DELAY = 4.0f;
    private static final float MAX_MOVE_DELAY = 5.0f;

    private static final float PERIOD = 15.0f;

    private static final float SIZE_FACTOR = 1.0f / 15;

    private static final float ROTATION_FACTOR = 5.0f;
    private static final float ROTATION_OFFSET = 33.0f;
    private static final float BIRTH_SIZE_FACTOR = 65.0f;
    private static final float MAX_MUTATE = 1.0f;
    private static final float MIN_MUTATE = 0.75f;

    private ProtoFood protoFood;
    private SpriteBatch batch;
    private Sprite sprite;

    public Food(Viewport viewport, LivingProcess colony, ProtoFood protoFood) {
        super(viewport, colony);
        this.protoFood = protoFood;
        init();
    }

    public interface ProtoFood {
        //TODO Переименовать
        void foodEmpty();
    }


    @Override
    public void init() {
        Texture texture;
        float randomFactor = (MathUtils.random() * (MAX_MUTATE - MIN_MUTATE)) + MIN_MUTATE;
        maxSize = randomFactor * SIZE_FACTOR * Math.min(viewport.getWorldWidth(), viewport.getWorldHeight());
        position = setRandomPosition();
        initialTime = TimeUtils.nanoTime();
        batch = new SpriteBatch();
        switch (MathUtils.random(1)) {
            case 0:
                texture = new Texture(Gdx.files.internal("food_1.png"));
                break;
            case 1:
                texture = new Texture(Gdx.files.internal("food_2.png"));
                break;
            default:
                texture = new Texture(Gdx.files.internal("food_1.png"));
                break;
        }

        sprite = new Sprite(texture);
        randomMove(0, ACCELERATION);
    }

    @Override
    public void update(float delta) {
        if (size < maxSize && !isDying) {
            size += delta * BIRTH_SIZE_FACTOR;
        } else {
            isDying = true;
        }
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

        collideWithWalls(1.5f);
    }

    @Override
    public void render(ShapeRenderer renderer) {
//        final int RENDER_COUNT = 3;
//        renderer.set(ShapeRenderer.ShapeType.Filled);
//        renderer.setColor(Color.BLACK);

//        for (int i =2; i < RENDER_COUNT; i++) {
//            renderer.rect(position.x, position.y, size / 2, size / 2, size / i + 1, size / i + 1, 1.0f, 1.0f, rotation * i);
//            renderer.rect(position.x, position.y, size / 2, size / 2, size / i + 1, size / i + 1, 1.0f, 1.0f, rotation  +  ROTATION_OFFSET * 2);
//        }

        batch.begin();
//        sprite.draw(batch);
        batch.draw(sprite, position.x, position.y, size / 2, size / 2, size, size, 1.0f, 1.0f, rotation);
        batch.end();
    }


    public void takeBite(float damage) {

        size -= damage;
        if (size <= 0) {
            protoFood.foodEmpty();
        }
    }
}
