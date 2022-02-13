package com.github.quintus_cult.nhs_coordinator.main;

import android.app.NotificationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new Interface(), config);

		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		Interface.WIDTH = displayMetrics.widthPixels;
		Interface.HEIGHT = displayMetrics.heightPixels;

		ServerClient client = new ServerClient(getString(R.string.channel_name), getString(R.string.channel_desc), this, getSystemService(NotificationManager.class));
		//Thread client_thread = new Thread(client);
		//client_thread.start();
	}

}
