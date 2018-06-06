package com.inyanga.protozoa.prots;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.inyanga.protozoa.GameScreen;

/**
 * Created by Pavel Shakhtarin on 24.05.2018.
 */
public abstract class Proto {

    private static final float TO_POINT_ACC = 70.0f;
    private static final float BOUND_X_DIVIDER = 4.0f;
    private static final float BOUND_Y_DIVIDER = 2.5f;
    private static final float BITE_DELAY = 0.5f;
    private static final float TO_FOOD_ACC = 2.0f;

    private LivingProcess colony;
    private Vector2 target = new Vector2();
    private Vector2 movement = new Vector2();
    private Vector2 foodPosition = new Vector2();
    private float reactionDistance;
    private float maxX, maxY, minX, minY;
    private boolean isFollow = false;

    Viewport viewport = null;
    Vector2 position = new Vector2();
    Vector2 velocity = new Vector2();
    long initialTime = 0L;
    float size = 0.0f;
    float maxSize = 0.0f;
    float rotation = 0.0f;
    float timeToNextMove = 0.0f;
    float timeToNextBite = 0.0f;
    float moveDelay = 5.0f;
    float lifeTime = 10.0f;
    float biteDamage = 0.1f;
    boolean isDying = false;
    boolean isTargetSet = false;
    boolean isRunningToPoint = false;

    public interface LivingProcess {
        void dying(Proto deadProto);

        void bite(float biteDamage);

        boolean isFoodReady();

        Vector2 getFoodPosition();

        float getFoodSize();
    }

    Proto(Viewport viewport, LivingProcess colony) {
        this.viewport = viewport;
        this.colony = colony;

        if (colony.isFoodReady()) {
            foodPosition = colony.getFoodPosition();
        }
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

        reactionDistance = Math.min(viewport.getWorldWidth(), viewport.getWorldHeight()) / 4.0f;
    }


    public abstract void init();

    public abstract void update(float delta);

    public abstract void render(ShapeRenderer renderer);


    void collideWithWalls(float velocityK) {

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
        float x = (MathUtils.random() * (maxX - minX)) + minX;
        float y = (MathUtils.random() * (maxY - minY)) + minY;
        return new Vector2(x, y);
    }

    public void collapse(Vector2 target) {
        isRunningToPoint = true;
        movement = new Vector2(target.x - position.x, target.y - position.y);
        velocity.mulAdd(movement, TO_POINT_ACC);
    }

    void follow(float acceleration) {
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

    float randomMove(float moveDelay, float acceleration) {
        float angle = MathUtils.PI2 * MathUtils.random();
        if (timeToNextMove >= moveDelay) {
            if (!isFollow) {
                timeToNextMove = MathUtils.random();
                velocity.x = acceleration * MathUtils.cos(angle);
                velocity.y = acceleration * MathUtils.sin(angle);
            }
        }
        return angle;
    }

    void living(float delta) {
        if (!isFollow && !isRunningToPoint) {
            lifeTime -= delta;
            if (lifeTime <= 0) {
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

    public void setFoodPosition(Vector2 foodPosition) {
        this.foodPosition = foodPosition;
    }

    public void feed(float delta, float acceleration) {

        if (foodPosition != null && colony.isFoodReady() && !isFollow && !isRunningToPoint) {
            if (foodPosition.dst(position) <= colony.getFoodSize() + size) {
                velocity = new Vector2(0, 0);

                timeToNextBite += delta;
                if (timeToNextBite >= BITE_DELAY) {
                    colony.bite(biteDamage);
                    lifeTime += biteDamage;
                    if (size < maxSize) {
                        size += delta;
                    }
                    timeToNextBite = 0.0f;
                }
                return;
            }
            if (foodPosition.dst(position) <= reactionDistance) {
                movement = new Vector2(foodPosition.x + colony.getFoodSize() / 2 - position.x, foodPosition.y + colony.getFoodSize() / 2 - position.y);
                velocity = new Vector2(0, 0);
                velocity.mulAdd(movement, TO_FOOD_ACC);
            }


        }
    }

    public void setBounds(float maxX, float maxY, float minX, float minY) {
        this.maxX = maxX;
        this.maxY = maxY;
        this.minX = minX;
        this.minY = minY;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getSize() {
        return size;
    }

    public void setDying(boolean dying) {
        isDying = dying;
    }
}
