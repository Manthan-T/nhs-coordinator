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
	static float WIDTH = 0;
	static float HEIGHT = 0;
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
	Texture on_next_floor_button;

	SpriteBatch g_floors;
	static ArrayList<Texture> floors = new ArrayList<Texture>();
	int current_floor_no = 0;
	Sprite current_floor;
	Texture no_floors;
	boolean floor_errorThrown = false;
	int floor_count = 0;
	float floorcX = 540;
	float floorcY = 1085;

	SpriteBatch g_devices;
	Texture current_device;
	boolean set_start = false;
	Texture ssp;
	Sprite set_start_pad;
	float cdX = WIDTH/2;
	float cdY = HEIGHT/2;

	float defaultAccelX = 0;
	float defaultAccelY = 0;
	
	@Override
	public void create() {
		camera = new OrthographicCamera();
		viewport = new ExtendViewport(WIDTH, HEIGHT, camera);

		g_logo = new SpriteBatch();
		logo = new Texture("logo.png");

		g_devices = new SpriteBatch();
		current_device = new Texture("set_start.png");

		g_menu = new SpriteBatch();
		menu_background = new Texture("menu_background.png");
		on_next_floor_button = new Texture("on_next_floor.png");

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
		ssp = new Texture("set_start_pad.png");
		set_start_pad = new Sprite(ssp);
	}

	@Override
	public void render() {
		ScreenUtils.clear(0, 0, 1, 1);
		g_logo.setColor(1, 1, 1, alpha);
		g_logo.begin();
			g_logo.draw(logo, WIDTH/2 - 343, HEIGHT/2 - 183);
		g_logo.end();
		update();
	}

	private void update() {
		float delta = Gdx.graphics.getDeltaTime();
		AZIMUTH = Gdx.input.getAzimuth();

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

				current_floor.setCenter(floorcX, floorcY);
				current_floor.setOrigin(cdX + 66, cdY + 66);
				current_floor.setRotation(AZIMUTH);

				g_devices.begin();
					g_devices.draw(current_device, cdX, cdY);

					if (!set_start) {
						set_start_pad.draw(g_devices);
						set_start_pad.setX(WIDTH - 500);
						set_start_pad.setY(75);
					}

				g_devices.end();
			}

			g_menu.begin();
				g_menu.draw(menu_background, 0, HEIGHT - 200);
				g_menu.draw(on_next_floor_button, WIDTH/2 - 150, HEIGHT - 150);
			g_menu.end();
		}

		if (Gdx.input.isTouched() && Gdx.input.getY() >= 100 && !set_start) {
			current_device.dispose();
			current_device = new Texture("current_device.png");
			cdX = WIDTH/2 - 66;
			cdY = HEIGHT/2 + 66;

			if (Gdx.input.getX() <= WIDTH - 193 && Gdx.input.getX() >= WIDTH - 321 && Gdx.input.getY() >= HEIGHT - 520 && Gdx.input.getY() <= HEIGHT - 392) {
				floorcY -= delta * 200;

			} else if (Gdx.input.getX() <= WIDTH - 193 && Gdx.input.getX() >= WIDTH - 321 && Gdx.input.getY() >= HEIGHT - 200 && Gdx.input.getY() <= HEIGHT - 72) {
				floorcY += delta * 200;

			} else if (Gdx.input.getX() <= WIDTH - 350 && Gdx.input.getX() >= WIDTH - 478 && Gdx.input.getY() >= HEIGHT - 361 && Gdx.input.getY() <= HEIGHT - 233) {
				floorcX += delta * 200;

			} else if (Gdx.input.getX() <= WIDTH - 27 && Gdx.input.getX() >= WIDTH - 155 && Gdx.input.getY() >= HEIGHT - 361 && Gdx.input.getY() <= HEIGHT - 233) {
				floorcX -= delta * 200;

			} else if (Gdx.input.getX() <= WIDTH - 193 && Gdx.input.getX() >= WIDTH - 321 && Gdx.input.getY() >= HEIGHT - 361 && Gdx.input.getY() <= HEIGHT - 233) {
				defaultAccelX -= Gdx.input.getAccelerometerX();
				defaultAccelY -= Gdx.input.getAccelerometerY();

				set_start = true;
				set_start_pad.setX(WIDTH + 500);
				ssp.dispose();
			}

		}

		if (set_start) {
			updateFloor();
		}

		if (Gdx.input.isTouched() && Gdx.input.getX() <= WIDTH/2 + 150 && Gdx.input.getX() >= WIDTH/2 - 150 && Gdx.input.getY() >= 50 && Gdx.input.getY() <= 150) {
			current_floor_no++;

			if (current_floor_no > floors.size() - 1) {
				current_floor_no = 0;
			}
		}
	}
	
	@Override
	public void dispose() {
		logo.dispose();
		menu_background.dispose();
		on_next_floor_button.dispose();

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
		if (defaultAccelX > 0) {
			floorcX += (Gdx.input.getAccelerometerX() + defaultAccelX) * 0.01;

		} else if (defaultAccelX < 0) {
			floorcX += (Gdx.input.getAccelerometerX() + defaultAccelX) * 0.01;

		} else {
			floorcX += Gdx.input.getAccelerometerX() * 0.01;
		}

		if (defaultAccelY > 0) {
			floorcY += (Gdx.input.getAccelerometerY() + defaultAccelY) * 0.01;

		} else if (defaultAccelY < 0) {
			floorcY += (Gdx.input.getAccelerometerY() + defaultAccelY) * 0.01;

		} else {
			floorcY += Gdx.input.getAccelerometerY() * 0.01;
		}

	}

}
