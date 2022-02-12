package com.github.quintus_cult.nhs_coordinator.main;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import com.github.quintus_cult.nhs_coordinator.main.Interface;

public class AndroidLauncher extends AndroidApplication implements SensorEventListener {

	private SensorManager sensorManager;
	private static final float[] accelerometerReading = new float[3];
	private static final float[] magnetometerReading = new float[3];

	private static final float[] rotationMatrix = new float[9];
	private static final float[] orientationAngles = new float[3];

	protected static boolean updateRotation = false;
	private UpdateOrientation update;
	private Thread orientation;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new Interface(), config);

		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		update = new UpdateOrientation();
		orientation = new Thread(update);
		orientation.run();

		createNotificationChannel();

		NotificationCompat.Builder builder = new NotificationCompat.Builder(AndroidLauncher.this, "My Notification")
			.setSmallIcon(R.drawable.notification_icon)
			.setContentTitle("Patient in need")
			.setContentText("Room " + "1")
			//.addAction()
			.setPriority(NotificationCompat.PRIORITY_MAX);

		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
		notificationManager.notify(0, builder.build());

	}

	private void createNotificationChannel() {
		// Create the NotificationChannel, but only on API 26+ because
		// the NotificationChannel class is new and not in the support library
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			CharSequence name = getString(R.string.channel_name);
			String description = getString(R.string.channel_desc);
			int importance = NotificationManager.IMPORTANCE_DEFAULT;
			NotificationChannel channel = new NotificationChannel("My Notification", name, importance);
			channel.setDescription(description);
			// Register the channel with the system; you can't change the importance
			// or other notification behaviors after this
			NotificationManager notificationManager = getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(channel);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Do something here if sensor accuracy changes.
		// You must implement this callback in your code.
	}

	@Override
	protected void onResume() {
		super.onResume();

		updateRotation = true;
		if (!orientation.isAlive()) {
			orientation = new Thread(update);
			//orientation.run();
		}

		Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		if (accelerometer != null) {
			sensorManager.registerListener(this, accelerometer,
					SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
		}
		Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		if (magneticField != null) {
			sensorManager.registerListener(this, magneticField,
					SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		updateRotation = false;
		sensorManager.unregisterListener(this);
	}

	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			System.arraycopy(event.values, 0, accelerometerReading,
					0, accelerometerReading.length);

		} else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			System.arraycopy(event.values, 0, magnetometerReading,
					0, magnetometerReading.length);
		}
	}

	public static void updateOrientationAngles() {
		SensorManager.getRotationMatrix(rotationMatrix, null,
				accelerometerReading, magnetometerReading);

		SensorManager.getOrientation(rotationMatrix, orientationAngles);

		Interface.AZIMUTH = -rotationMatrix[0];
	}

}
