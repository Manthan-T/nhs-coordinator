package com.github.quintus_cult.nhs_coordinator.main;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new Interface(), config);

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

}
