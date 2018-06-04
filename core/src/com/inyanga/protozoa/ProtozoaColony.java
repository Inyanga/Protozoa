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
public class ProtozoaColony implements Proto.LivingProcess {


    private static final ProtozoaColony ourInstance = new ProtozoaColony();

    public static ProtozoaColony getInstance() {
        return ourInstance;
    }

    private static final int MAX_PROTS = 600;
    private static final int PLANKTON_FACTOR = 5;
    private static final int TRI_FACTOR = 6;

    private float generateDelay;
    private float nextGeneration;
    private List<Proto> livingProto;
    private List<Proto> deadProto;
    private Viewport viewport;
    private ShapeRenderer renderer;

    private ProtozoaColony() {

    }

    public void init(Viewport viewport, ShapeRenderer renderer) {
        livingProto = new ArrayList<Proto>();
        deadProto = new ArrayList<Proto>();
        this.viewport = viewport;
        this.renderer = renderer;
        generateDelay = 0.0f;
        nextGeneration = 0.0f;

    }

    public void render(float delta) {
        nextGeneration += delta;
        if (nextGeneration >= generateDelay) {
            if (livingProto.size() < MAX_PROTS) {
                if (MathUtils.random(1) == 0) {
                    Proto mold = new Mold(viewport, this);
                    livingProto.add(mold);
                } else {
                    Amoeba amoeba = new Amoeba(viewport, this);
                    livingProto.add(amoeba);
                }

                if (livingProto.size() % 3 == 0) {
                    switch (MathUtils.random(5)) {
                        case 0:
                            Microfly microfly = new Microfly(viewport, this);
                            livingProto.add(microfly);
                            break;
                        case 1:
                            Plankton plankton = new Plankton(viewport, this);
                            livingProto.add(plankton);
                            break;
                        case 2:
                            Triform triform = new Triform(viewport, this);
                            livingProto.add(triform);
                            break;
                        default:
                            microfly = new Microfly(viewport, this);
                            livingProto.add(microfly);
                            break;
                    }

                }
//                if (livingProto.size() % PLANKTON_FACTOR == 0) {
//
//                }
//                if (livingProto.size() % TRI_FACTOR == 0) {
//
//                }
            }

            nextGeneration = 0.0f;
        }
        for (Proto p : livingProto) {
            p.update(delta);
            p.render(renderer);
        }
        if (deadProto.size() > 0) {
            livingProto.removeAll(deadProto);
            deadProto.clear();
        }
    }

    public void setFullBounds() {
        for (Proto p : livingProto) {
            p.setBounds(viewport.getWorldWidth(), viewport.getWorldHeight(), 0, 0);
        }
    }

    public void setTarget(int x, int y) {
        for (Proto p : livingProto) {
            p.setTarget(viewport.unproject(new Vector2(x, y)));
        }
    }

    public void collapse(int x, int y) {
        for (Proto p : livingProto) {
            p.collapse(viewport.unproject(new Vector2(x, y)));
        }
    }

    public void release() {

        for (Proto p : livingProto) {
            p.release();
        }
    }

    @Override
    public void dying(Proto deadProto) {

        this.deadProto.add(deadProto);

    }
}
