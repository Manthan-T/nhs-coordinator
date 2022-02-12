package com.github.quintus_cult.nhs_coordinator.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.util.ArrayList;

public class Interface extends ApplicationAdapter {
	static final float WIDTH = 1080;
	static final float HEIGHT = 1920;
	static float AZIMUTH = 0;

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
	static ArrayList<Texture> floors = new ArrayList<Texture>();
	int current_floor_no = 0;
	Sprite current_floor;
	Texture no_floors;
	boolean floor_errorThrown = false;
	int floor_count = 0;

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

		g_menu = new SpriteBatch();
		menu_background = new Texture("menu_background.png");
		add_floor_button = new Texture("add_floor_button.png");
		view_floor_button = new Texture("view_floor_button.png");

		while (!floor_errorThrown) {
			try {
				Texture floor = new Texture("floors/floor_" + floor_count + ".png");
				Interface.floors.add(floor);
				floor_count++;

			} catch (GdxRuntimeException e) {
				floor_errorThrown = true;
			}
		}

		if (floors.size() != 0) {
			current_floor = new Sprite(floors.get(current_floor_no));
		}

		g_floors = new SpriteBatch();
		no_floors = new Texture("no_floors.png");
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

	private void update() {
		float delta = Gdx.graphics.getDeltaTime();

		updateLogo(delta);

		if (logoDone) {
			if (floors.size() == 0) {
				g_floors.begin();
					g_floors.draw(no_floors, 0, HEIGHT/2 + 61);
				g_floors.end();

			} else {
				ScreenUtils.clear(0, 0.349f, 0.878f, 1);
				g_floors.begin();
					current_floor.draw(g_floors);
				g_floors.end();

				current_floor.setCenter(WIDTH/2, HEIGHT/2 + 125);
				current_floor.setRotation(AZIMUTH);

				g_devices.begin();
				g_devices.draw(current_device, WIDTH/2 - 66, HEIGHT/2 + 66);
				g_devices.end();
			}

			g_menu.begin();
			g_menu.draw(menu_background, 0, HEIGHT + 50);
			g_menu.draw(add_floor_button, 50, HEIGHT + 50);
			g_menu.draw(view_floor_button, 700, HEIGHT + 50);
			g_menu.end();

		}

		if (Gdx.input.getX() <= 350 && Gdx.input.getX() >= 50 && Gdx.input.getY() >= HEIGHT - 50 && Gdx.input.getY() <= HEIGHT + 50) {
			System.out.println("HIIIIII");

		} else if (Gdx.input.getX() <= 1050 && Gdx.input.getX() >= 700 && Gdx.input.getY() >= HEIGHT - 50 && Gdx.input.getY() <= HEIGHT + 50) {
			System.out.println("HIIIIII");
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

	private void updateLogo(float delta) {
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
		}

	}

	private void updateFloor() {

	}

}
