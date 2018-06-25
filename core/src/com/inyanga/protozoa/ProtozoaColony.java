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

    private static final int MAX_PROTS = 520;
    private static final float GENERATE_DELAY = 0.2f;

    private float nextProto = 0.0f;

    private List<Proto> livingProto = new ArrayList<Proto>();
    private List<Proto> deadProto = new ArrayList<Proto>();
    private Viewport viewport;
    private ShapeRenderer renderer;


    private ProtozoaColony() {

    }

    public void init(Viewport viewport, ShapeRenderer renderer) {
        this.viewport = viewport;
        this.renderer = renderer;
    }

    public void render(float delta) {
        nextProto += delta;
        if (nextProto >= GENERATE_DELAY) {
            if (livingProto.size() < MAX_PROTS) {
                if (MathUtils.random(1) == 0) {
                    Mold mold = new Mold(viewport, this);
                    livingProto.add(mold);
                } else {
                    Amoeba amoeba = new Amoeba(viewport, this);
                    livingProto.add(amoeba);
                }

                if (livingProto.size() % 3 == 0) {

                    if (MathUtils.random(1) == 0) {
                        Microfly microfly = new Microfly(viewport, this);
                        livingProto.add(microfly);
                    } else {
                        Plankton plankton = new Plankton(viewport, this);
                        livingProto.add(plankton);
                    }
                    if (livingProto.size() % 5 == 0) {
                        Triform triform = new Triform(viewport, this);
                        livingProto.add(triform);
                    }
                }
            }
            nextProto = 0.0f;
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
