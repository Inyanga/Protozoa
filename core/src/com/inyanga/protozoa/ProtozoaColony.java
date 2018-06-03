package com.inyanga.protozoa;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.inyanga.protozoa.prots.Amoeba;
import com.inyanga.protozoa.prots.Microfly;
import com.inyanga.protozoa.prots.Mold;
import com.inyanga.protozoa.prots.Plankton;
import com.inyanga.protozoa.prots.Proto;
import com.inyanga.protozoa.prots.Triform;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavel Shakhtarin on 03.06.2018.
 */
public class ProtozoaColony {


    private static final ProtozoaColony ourInstance = new ProtozoaColony();

    public static ProtozoaColony getInstance() {
        return ourInstance;
    }

    private static final int MAX_PROTS = 600;
    private static final int PLANKTON_FACTOR = 5;
    private static final int TRI_FACTOR = 6;

    private float generateDelay;
    private float nextGeneration;
    private List<Proto> prots;
    private Viewport viewport;
    private ShapeRenderer renderer;

    private ProtozoaColony() {
        prots = new ArrayList<Proto>();
        generateDelay = 0.0f;
        nextGeneration = 0.0f;
    }

    public void init(Viewport viewport, ShapeRenderer renderer) {
        this.viewport = viewport;
        this.renderer = renderer;
    }

    public void render(float delta) {
        nextGeneration += delta;
        if (nextGeneration >= generateDelay) {
            if (prots.size() < MAX_PROTS) {
                if(MathUtils.random(1) == 0) {
                    Proto mold = new Mold(viewport);
                    prots.add(mold);
                } else {
                    Amoeba amoeba = new Amoeba(viewport);
                    prots.add(amoeba);
                }

                if (prots.size() % 3 == 0) {

                    Microfly microfly = new Microfly(viewport);
                    prots.add(microfly);
                }
                if (prots.size() % PLANKTON_FACTOR == 0) {
                    Plankton plankton = new Plankton(viewport);
                    prots.add(plankton);
                }
                if (prots.size() % TRI_FACTOR == 0) {
                    Triform triform = new Triform(viewport);
                    prots.add(triform);
                }
            }

            nextGeneration = 0.0f;
        }
        for (Proto p : prots) {
            p.update(delta);
            p.render(renderer);
        }
    }

    public void setFullBounds() {
        for (Proto p : prots) {
            p.setBounds(viewport.getWorldWidth(), viewport.getWorldHeight(), 0, 0);
        }
    }

    public void setTarget(int x, int y) {
        for (Proto p : prots) {
            p.setTarget(viewport.unproject(new Vector2(x, y)));
        }
    }

    public void collapse(int x, int y) {
        for (Proto p : prots) {
            p.collapse(viewport.unproject(new Vector2(x, y)));
        }
    }

    public void release() {
        for (Proto p : prots) {
            p.release();
        }
    }
}
