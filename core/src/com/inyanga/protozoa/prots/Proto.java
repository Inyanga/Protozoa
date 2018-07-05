package com.inyanga.protozoa.prots;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.inyanga.protozoa.GameScreen;

/**
 * Created by Pavel Shakhtarin on 24.05.2018.
 */

//  Абстрактный класс от которого наследуются все прото,
//  определяет абстрактные методы update и render
public abstract class Proto {

    //  Ускорение для перемещения при касании экрана
    private static final float TO_POINT_ACC = 70.0f;

    //  Коэффициенты  задающие область появления объектов когда на экране отображено лого
    //  для создания эффекта подвижной надписи
    private static final float BOUND_X_DIVIDER = 4.0f;
    private static final float BOUND_Y_DIVIDER = 2.5f;

    // Цвета объектов
    private static final int COLOR_RANGE = 4;
    private static final String GREEN = "8BC34A00";
    private static final String PURPLE = "B39DDB00";
    private static final String AQUA = "26C6DA00";
    private static final String BLUE = "0088DC00";
    private static final String PINK = "FF007F00";

    // Переменная хранящая объект, реализующий интерфейс LivingProcess
    private LivingProcess colony;

    // Вектор, хранящий координаты касания экрана
    private Vector2 target = new Vector2();

    // Вектор направления движения при касании
    private Vector2 movement = new Vector2();

    // Расстояние реакции на касание
    private float reactionDistance;

    // Координаты границ за которые не могут выходить объекты
    private float maxX, maxY, minX, minY;

    // Следует ли за объект за касанием
    private boolean isFollow = false;

    // Движется ли объект к точке при длительном касании экрана
    private boolean isRunningToPoint = false;

    // Вьюпорт устройства
    Viewport viewport = null;

    // Вектор текущего положения объекта
    Vector2 position = new Vector2();

    // Вектор ускорения
    Vector2 velocity = new Vector2();

    // Время появления объекта
    long initialTime = 0L;

    // Текущий размер объекта
    float size = 0.0f;

    // Максимальный размер объекта
    float maxSize = 0.0f;

    // Угол поворота
    float rotation = 0.0f;

    // Перменная увеличивающаяся с каждым фреймом,
    // когда становится >= moveDelay происходит следующее перемещение
    float timeToNextMove = 0.0f;

    // Время до следующего перемещения
    float moveDelay = 0.0f;

    // Время жизни объекта. Уменьшается с каждым фреймом, если объект не следует за касанием
    float lifeTime = 10.0f;

    // true, когда lifeTime <= 0
    boolean isDying = false;

    // true, когда произошло касание дисплея
    boolean isTargetSet = false;


    public interface LivingProcess {

        // callback который будет вызван при lifeTime <= 0
        void dying(Proto deadProto);
    }

    Proto(Viewport viewport, LivingProcess colony) {
        this.viewport = viewport;
        this.colony = colony;
    }


    public void init() {

        // Задание границ за которые не может выйти объект в зависимости от видимости лого

        if (GameScreen.isLogoVisible()) {
            maxX = viewport.getWorldWidth() - viewport.getWorldWidth() / BOUND_X_DIVIDER;
            maxY = viewport.getWorldHeight() - viewport.getWorldHeight() / BOUND_Y_DIVIDER;
            minX = viewport.getWorldWidth() / BOUND_X_DIVIDER;
            minY = viewport.getWorldHeight() / BOUND_Y_DIVIDER;
        } else {
            maxX = viewport.getWorldWidth();
            maxY = viewport.getWorldHeight();
            minX = 0;
            minY = 0;
        }

        // Расчет дистанции реакции на касание
        reactionDistance = Math.min(viewport.getWorldWidth(), viewport.getWorldHeight()) / 4.0f;
    }

    public abstract void update(float delta);

    public abstract void render(ShapeRenderer renderer);


    void collideWithWalls(float velocityK) {

        // Проверка столкновения с заданными границами, изменение вектора ускорения

        if (position.x - size < minX) {
            position.x = minX + size;
            velocity.x = -velocity.x * velocityK;
        }
        if (position.x + size > maxX) {
            position.x = maxX - size;
            velocity.x = -velocity.x * velocityK;
        }
        if (position.y - size < minY) {
            position.y = minY + size;
            velocity.y = -velocity.y * velocityK;
        }
        if (position.y + size > maxY) {
            position.y = maxY - size;
            velocity.y = -velocity.y * velocityK;
        }
    }


    Vector2 setRandomPosition() {

        // Возвращает рандомный вектор в рамках указанных значений
        float x = (MathUtils.random() * (maxX - minX)) + minX;
        float y = (MathUtils.random() * (maxY - minY)) + minY;
        return new Vector2(x, y);
    }

    public void collapse(Vector2 target) {

        // Метод собирает все объекты в точку при длительном касании
        isRunningToPoint = true;
        movement = new Vector2(target.x - position.x, target.y - position.y);
        velocity.mulAdd(movement, TO_POINT_ACC);
    }

    void follow(float acceleration) {

        // Если расстояние до касания <= reactionDistance объект начинает движение к точке касания
        if (isTargetSet && !isRunningToPoint) {
            if (target.dst(position) <= reactionDistance) {
                isFollow = true;
                movement = new Vector2(target.x - position.x, target.y - position.y);
                velocity.mulAdd(movement, acceleration);
            } else {
                isFollow = false;
            }
        }
    }

    void randomMove(float acceleration, float minDelay, float maxDelay) {

        // Метод задает движение объекта в случаайно выбранном направлении

        if (!isFollow) {
            float angle = MathUtils.PI2 * MathUtils.random();
            timeToNextMove = MathUtils.random();
            moveDelay = MathUtils.random() * (maxDelay - minDelay) + minDelay;
            velocity.x = acceleration * MathUtils.cos(angle);
            velocity.y = acceleration * MathUtils.sin(angle);
        }
    }

    void living(float delta) {
        if (!isFollow && !isRunningToPoint) {
            // Уменьшение вреиени жизни каждый фрейм
            lifeTime -= delta;
            if (lifeTime <= 0) {
                // Если время жизни истекло объект начинает уменьшаться
                isDying = true;
                size -= delta * 2;
                if (size <= 0) {
                    colony.dying(this);
                }
            }
        }
    }

    public void release() {
        isTargetSet = false;
        isFollow = false;
        isRunningToPoint = false;
        velocity.mulAdd(position, 0);
    }

    public void setTarget(Vector2 target) {
        this.target = target;
        isTargetSet = true;
    }


    public void setBounds(float maxX, float maxY, float minX, float minY) {
        this.maxX = maxX;
        this.maxY = maxY;
        this.minX = minX;
        this.minY = minY;
    }

    Color setRandomColor() {
        switch (MathUtils.random(COLOR_RANGE)) {
            case 0:
                return Color.valueOf(GREEN);
            case 1:
                return Color.valueOf(AQUA);
            case 2:
                return Color.valueOf(PURPLE);
            case 3:
                return Color.valueOf(BLUE);
            case 4:
                return Color.valueOf(PINK);
            default:
                return Color.BLACK;
        }
    }

}
