package com.inyanga.protozoa;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by Pavel Shakhtarin on 24.05.2018.
 */
public final class Timer {

    public static float cyclePosition(long initialTime, float period) {
        return  elapsedPeriods(initialTime, period) % 1;

    }

    public static float elapsedSeconds(long initialTime) {
        float elapsedNanoSec = TimeUtils.nanoTime() - initialTime;
        return MathUtils.nanoToSec * elapsedNanoSec;
    }

    public static float elapsedPeriods(long initialTime, float period) {
        float elapsedNanoSec = TimeUtils.nanoTime() - initialTime;
        float elapsedSeconds = MathUtils.nanoToSec * elapsedNanoSec;
        return elapsedSeconds / period;

    }

}
