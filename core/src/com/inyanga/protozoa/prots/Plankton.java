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

    // Максимально возможная скорость
    private static final float MAX_SPEED = 310.0f;

    // Ускорение
    private static final float ACCELERATION = 150.0f;

    // Коэффициент замедления движения
    private static final float DRAG = 0.2f;

    //Границы в которых вычисляется время до следующего перемещения
    private static final float MIN_MOVE_DELAY = 3.8f;
    private static final float MAX_MOVE_DELAY = 5.0f;

    // Период вращения
    private static final float PERIOD = 4.8f;

    // Коэффициент влияющий на размер объекта
    private static final float SIZE_FACTOR = 1.0f / 250;

    // Коэффициент вращения
    private static final float ROTATION_FACTOR = 15.0f;

    // Границы в которых вычисляется время жизни объекта
    private static final float MAX_LIFE_TIME = 20.0f;
    private static final float MIN_LIFE_TIME = 12.0f;

    // Границы в которых вычисляется размер объекта
    private static final float MAX_MUTATE = 1.0f;
    private static final float MIN_MUTATE = 0.6f;

    // Цвет объекта
    private Color randomColor;

    public Plankton(Viewport viewport,  LivingProcess colony) {
        super(viewport,  colony);
        init();
    }


    @Override
    public void init() {
        super.init();
        isTargetSet = false;
        // Коэффициент создающий разброс в размерах объектов
        float randomFactor = (MathUtils.random() * (MAX_MUTATE - MIN_MUTATE)) + MIN_MUTATE;
        size = randomFactor * SIZE_FACTOR * Math.min(viewport.getWorldWidth(), viewport.getWorldHeight());
        lifeTime = (MathUtils.random() * (MAX_LIFE_TIME - MIN_LIFE_TIME)) + MIN_LIFE_TIME;
        position = setRandomPosition();
        initialTime = TimeUtils.nanoTime();
        randomMove(ACCELERATION, MIN_MOVE_DELAY, MAX_MOVE_DELAY);
        randomColor = setRandomColor();
    }

    @Override
    public void update(float delta) {

        follow(ACCELERATION);
        timeToNextMove += delta + delta * MathUtils.random() * 2f;
        if (timeToNextMove >= moveDelay) {
            randomMove(ACCELERATION, MIN_MOVE_DELAY, MAX_MOVE_DELAY);
        }


        // Постепенное торможение объекта
        velocity.x -= delta * DRAG * velocity.x;
        velocity.y -= delta * DRAG * velocity.y;

        // Определение положения в цикле и вычисление угла
        float cyclePosition = Timer.cyclePosition(initialTime, PERIOD);
        rotation = MathUtils.random() * ROTATION_FACTOR * MathUtils.cos(MathUtils.PI2 * cyclePosition);

        // Метод не позволяющий ускорению подняться больше максимума
        velocity.clamp(0, MAX_SPEED);

        // Обновление позиции объекта
        position.x += delta * velocity.x - rotation * delta * size * 3 * MathUtils.sin(MathUtils.random());
        position.y += delta * velocity.y + rotation * delta * size * 3 * MathUtils.cos(MathUtils.random());

        living(delta);
        collideWithWalls(1.5f);
    }

    @Override
    public void render(ShapeRenderer renderer) {
        // Рендер объекта
        renderer.set(ShapeRenderer.ShapeType.Line);
        renderer.setColor(randomColor);
        renderer.circle(position.x, position.y, size);
    }
}