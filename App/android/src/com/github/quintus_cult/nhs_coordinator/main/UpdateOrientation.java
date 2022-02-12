package com.github.quintus_cult.nhs_coordinator.main;

public class UpdateOrientation implements Runnable {

    @Override
    public void run() {
        while (AndroidLauncher.updateRotation) {
            AndroidLauncher.updateOrientationAngles();
        }
    }
}
