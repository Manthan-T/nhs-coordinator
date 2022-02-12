package com.github.quintus_cult.nhs_coordinator.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.util.ArrayList;

import javax.management.Notification;

public class Interface extends ApplicationAdapter {
	static final float WIDTH = 1080;
	static final float HEIGHT = 1920;

	OrthographicCamera camera;
	ExtendViewport viewport;

	SpriteBatch g_logo;
	float alpha = 0;

	Texture logo;
	boolean flipLogoAnimation = false;
	boolean logoDone = false;

	SpriteBatch g_menu;
	Texture menu_background;
	Texture add_floor_button;
	Texture view_floor_button;

	SpriteBatch g_floors;
	ArrayList<Texture> floors = new ArrayList<Texture>();
	Texture no_floors;

	SpriteBatch g_devices;
	Texture current_device;
	ArrayList<Devices> devices = new ArrayList<Devices>();
	
	@Override
	public void create() {
		camera = new OrthographicCamera();
		viewport = new ExtendViewport(WIDTH, HEIGHT, camera);

		g_logo = new SpriteBatch();
		logo = new Texture("logo.png");

		g_devices = new SpriteBatch();
		current_device = new Texture("current_device.png");
	}

	@Override
	public void render() {
		ScreenUtils.clear(0, 0, 1, 1);
		g_logo.setColor(1, 1, 1, alpha);
		g_logo.begin();
		g_logo.draw(logo, WIDTH/2 - 343, HEIGHT/2);
		g_logo.end();
		update();
	}

	public void update() {
		float delta = Gdx.graphics.getDeltaTime();

		updateLogo(delta);

		if (logoDone) {
			g_devices.begin();
			g_devices.draw(current_device, WIDTH/2 - 66, HEIGHT/2 + 66);
			g_devices.end();
		}
	}
	
	@Override
	public void dispose() {
		logo.dispose();
		menu_background.dispose();
		add_floor_button.dispose();
		view_floor_button.dispose();

		no_floors.dispose();
		current_device.dispose();

		for (Texture floor : floors) {
			floor.dispose();
		}

		g_logo.dispose();
		g_menu.dispose();
		g_floors.dispose();
		g_devices.dispose();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		g_logo.setProjectionMatrix(camera.combined);
	}

	public void updateLogo(float delta) {
		if (alpha >= 1) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			flipLogoAnimation = true;
		}

		if (!flipLogoAnimation) {
			alpha += delta * 0.4;

		} else {
			alpha -= delta * 0.4;
		}

		g_logo.setColor(1, 1, 1, alpha);

		if (alpha < 0) {
			ScreenUtils.clear(0, 0, 0, 1);
			logoDone = true;

			g_menu = new SpriteBatch();
			menu_background = new Texture("menu_background.png");
			add_floor_button = new Texture("add_floor_button.png");
			view_floor_button = new Texture("view_floor_button.png");

			g_menu.begin();
			g_menu.draw(menu_background, 0, HEIGHT + 50);
			g_menu.draw(add_floor_button, 50, HEIGHT + 50);
			g_menu.draw(view_floor_button, 700, HEIGHT + 50);
			g_menu.end();
		}

	}

}
