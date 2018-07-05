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

    // Максимально возможная скорость
    private static final float MAX_SPEED = 350.0f;

    // Ускорение
    private static final float ACCELERATION = 70.0f;

    // Коэффициент замедления движения
    private static final float DRAG = 0.8f;

    //Границы в которых вычисляется время до следующего перемещения
    private static final float MIN_MOVE_DELAY = 4.8f;
    private static final float MAX_MOVE_DELAY = 7.5f;

    // Период вращения
    private static final float PERIOD = 3.0f;

    // Коэффициент влияющий на размер объекта
    private static final float SIZE_FACTOR = 1.0f / 70;

    // Коэффициент смещения
    private static final float ROTATION_OFFSET = 15.0f;

    // Границы в которых вычисляется время жизни объекта
    private static final float MAX_LIFE_TIME = 25.0f;
    private static final float MIN_LIFE_TIME = 15.0f;

    // Границы в которых вычисляется размер объекта
    private static final float MAX_MUTATE = 1.0f;
    private static final float MIN_MUTATE = 0.55f;

    // Коэффициент с которым объект растет до максимального размера
    private static final float BIRTH_SIZE_FACTOR = 2.5f;

    private float cyclePosition;


    private Color randomColor;
    private Vector2 satellitePos;
    private int direction;

    public Mold(Viewport viewport, LivingProcess colony) {
        super(viewport, colony);
        init();
    }

    @Override
    public void init() {
        super.init();
        randomColor = setRandomColor();
        isTargetSet = false;

        //  Направление вращения
        direction = MathUtils.random(1);

        // Коэффициент создающий разброс в размерах объектов
        float mutationFactor = (MathUtils.random() * (MAX_MUTATE - MIN_MUTATE)) + MIN_MUTATE;
        lifeTime = (MathUtils.random() * (MAX_LIFE_TIME - MIN_LIFE_TIME)) + MIN_LIFE_TIME;
        maxSize = mutationFactor * SIZE_FACTOR * Math.min(viewport.getWorldWidth(), viewport.getWorldHeight());
        position = setRandomPosition();
        satellitePos = new Vector2();
        initialTime = TimeUtils.nanoTime();
        randomMove(ACCELERATION, MIN_MOVE_DELAY, MAX_MOVE_DELAY);
    }

    @Override
    public void update(float delta) {
        if (size < maxSize && !isDying) {
            // Если размер объекта меньше максимального и время жизни не истекло, увеличивает размер
            size += delta * BIRTH_SIZE_FACTOR;
        }

        // Определение положения в цикле
        cyclePosition = Timer.cyclePosition(initialTime, PERIOD);
        follow(ACCELERATION);
        timeToNextMove += delta + delta * MathUtils.random() * 2f;
        if (timeToNextMove >= moveDelay) {
            randomMove(ACCELERATION, MIN_MOVE_DELAY, MAX_MOVE_DELAY);
        }

        // Постепенное торможение объекта
        velocity.x -= delta * DRAG * velocity.x;
        velocity.y -= delta * DRAG * velocity.y;

        // Метод не позволяющий ускорению подняться больше максимума
        velocity.clamp(0, MAX_SPEED);

        // Обновление позиции объекта
        position.x += delta * velocity.x;
        position.y += delta * velocity.y;

        living(delta);
        collideWithWalls(1.5f);
    }

    @Override
    public void render(ShapeRenderer renderer) {

        // Рендер объекта
        final int RENDER_COUNT = 6;
        renderer.set(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.BLACK);
        renderer.circle(position.x, position.y, size);
        renderer.circle(position.x, position.y, size / 1.8f);
        renderer.setColor(randomColor);
        renderer.circle(position.x, position.y, size / 3f);
        renderer.circle(position.x, position.y, size / 3.5f);
        renderer.circle(position.x, position.y, size / 7f);
        for (int i = 1; i < RENDER_COUNT; i++) {
            if (direction == 0) {
                satellitePos.x = position.x + size * 1.2f * MathUtils.sin(MathUtils.PI2 * cyclePosition + ROTATION_OFFSET * i);
                satellitePos.y = position.y + size * 1.2f * MathUtils.cos(MathUtils.PI2 * cyclePosition + ROTATION_OFFSET * i);
            } else {
                satellitePos.x = position.x + size * 1.2f * MathUtils.cos(MathUtils.PI2 * cyclePosition + ROTATION_OFFSET * i);
                satellitePos.y = position.y + size * 1.2f * MathUtils.sin(MathUtils.PI2 * cyclePosition + ROTATION_OFFSET * i);
            }
            renderer.circle(satellitePos.x, satellitePos.y, size / 15.0f);
        }
    }


}