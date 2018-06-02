package com.inyanga.protozoa.prots;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

/**
 * Created by Pavel Shakhtarin on 24.05.2018.
 */
public abstract class Proto {

    private static final float TO_POINT_ACC = 200.0f;
    private static final float BOUND_X_DIVIDER = 4.0f;
    private static final float BOUND_Y_DIVIDER = 2.5f;

    private Vector2 target;
    private Vector2 movement;
    private float reactionDistance;
    private float maxX, maxY, minX, minY;
    private boolean isFollow;

    Viewport viewport;
    Vector2 position;
    Vector2 velocity;
    long initialTime;
    float size;
    float rotation;
    float timeToNextMove;
    boolean isTargetSet;
    boolean isRunningToPoint;


    Proto(Viewport viewport) {
        this.viewport = viewport;
        position = new Vector2();
        velocity = new Vector2();
        target = new Vector2();
        size = 0.0f;
        rotation = 0.0f;
        initialTime = 0L;
        isRunningToPoint = false;
        isFollow = false;
        maxX = viewport.getWorldWidth() - viewport.getWorldWidth()/BOUND_X_DIVIDER;
        maxY = viewport.getWorldHeight() - viewport.getWorldHeight()/BOUND_Y_DIVIDER;
        minX = viewport.getWorldWidth()/BOUND_X_DIVIDER;
        minY = viewport.getWorldHeight()/BOUND_Y_DIVIDER;
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

    float randomMove(float acceleration) {

        float angle = MathUtils.PI2 * MathUtils.random();
        velocity.x = acceleration * MathUtils.cos(angle);
        velocity.y = acceleration * MathUtils.sin(angle);
        return angle;
    }

    Vector2 setRandomPosition() {

        float x = (MathUtils.random() * (maxX - minX)) + minX ;
        float y = (MathUtils.random() * (maxY  - minY)) + minY;
        return new Vector2(x, y);
    }

    public void runToPoint(Vector2 target) {
        isRunningToPoint = true;

        movement = new Vector2(target.x - position.x, target.y - position.y);
        velocity.mulAdd(movement, TO_POINT_ACC);
    }

    void follow(float acceleration) {
        if (isTargetSet ) {
            if (target.dst(position) <= reactionDistance) {
                isFollow = true;
                movement = new Vector2(target.x - position.x, target.y - position.y);
                velocity.mulAdd(movement, (acceleration > 0) ? acceleration : -acceleration);
            } else {
                isFollow = false;
            }
        }
    }
    void nextMove(float moveDelay, float acceleration) {
        if (timeToNextMove >= moveDelay) {
            if (!isFollow) {
                timeToNextMove = MathUtils.random();
                randomMove(acceleration);
            }
        }
    }

    public void stopFollow() {
        isTargetSet = false;
        isFollow = false;
        isRunningToPoint = false;
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
}
