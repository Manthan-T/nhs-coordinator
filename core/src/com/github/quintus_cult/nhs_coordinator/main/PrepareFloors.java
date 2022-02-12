package com.github.quintus_cult.nhs_coordinator.main;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class PrepareFloors implements Runnable {

    boolean errorThrown = false;
    int count = 0;

    @Override
    public void run() {
        while (!errorThrown) {
            try {
                Texture floor = new Texture("floors/floor_" + count + ".png");
                Interface.floors.add(floor);
                count++;

            } catch (GdxRuntimeException e) {
                errorThrown = true;
            }
        }

    }

}
