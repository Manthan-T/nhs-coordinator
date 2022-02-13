package com.github.quintus_cult.nhs_coordinator.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;

public class Interface extends ApplicationAdapter {

	/*
	 * All of the variables used in the code. Less obvious types are:
	 * 		SpriteBatches are used to draw stuff,
	 * 		and an ArrayList is an array but you can increase its size.
	 *
	 * In the code, drawing stuff must always happen between a SpriteBatch's
	 * begin and end method being called.
	 *
	 */

	static float WIDTH = 0;
	static float HEIGHT = 0;
	static float AZIMUTH = 0;
	static float OLD_AZIMUTH = 0;
	static long ROTATION_CLOCK = System.currentTimeMillis();
	static long NEXT_FLOOR_TIMEOUT = System.currentTimeMillis();

	SpriteBatch g_logo;
	float alpha = 0;

	Texture logo;
	boolean flipLogoAnimation = false;
	boolean logoDone = false;

	SpriteBatch g_menu;
	Texture menu_background;
	Texture next_floor_button;

	SpriteBatch g_floors;
	static ArrayList<Texture> floors = new ArrayList<>();
	static int current_floor_no = 0;
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

	// Initialises most variables that haven't been done so above

	@Override
	public void create() {
		g_logo = new SpriteBatch();
		logo = new Texture("logo.png");

		g_devices = new SpriteBatch();
		current_device = new Texture("set_start.png");

		g_menu = new SpriteBatch();
		menu_background = new Texture("menu_background.png");
		next_floor_button = new Texture("next_floor.png");

		while (!floor_errorThrown) {

			// Uses images in the assets folder to load all of the floor plans
			// Keeps trying to load a floor with a higher floor number until an error is thrown (when the file is not found)

			try {
				Texture floor = new Texture("floors/floor_" + floor_count + ".png");
				Interface.floors.add(floor);
				floor_count++;

			} catch (GdxRuntimeException e) {
				floor_errorThrown = true;
			}
		}

		// Sets the floor texture to the current floor

		if (floors.size() != 0) {
			current_floor = new Sprite(floors.get(current_floor_no));
		}

		g_floors = new SpriteBatch();
		no_floors = new Texture("no_floors.png");
		ssp = new Texture("set_start_pad.png");
		set_start_pad = new Sprite(ssp);
	}

	// Renders the logo

	@Override
	public void render() {
		ScreenUtils.clear(0, 0, 1, 1); // Sets the background colour
		g_logo.setColor(1, 1, 1, alpha);
		g_logo.begin();
			g_logo.draw(logo, WIDTH/2 - 343, HEIGHT/2 - 183);
		g_logo.end();
		update(); 							   // Calls the update method
	}

	private void update() {
		float delta = Gdx.graphics.getDeltaTime();  // Creates and assigns the delta variable (time between this frame and the last, used for timing motion)
		OLD_AZIMUTH = AZIMUTH;						// Assigns the old azimuth value to OLD_AZIMUTH
		AZIMUTH = Gdx.input.getAzimuth();			// Assigns the azimuth to AZIMUTH (the angle between the direction the phone is pointing and north)

		updateLogo(delta);							// Updates the logo

		if (logoDone) {								// Checks whether the logo is done
			if (floors.size() == 0) {				// And if so, check if floors are provided
				g_floors.begin();					// And if so, draw the no_floors image
					g_floors.draw(no_floors, 0, HEIGHT/2 + 61);
				g_floors.end();

			} else {								// If there are floor plans available:
				ScreenUtils.clear(0, 0.349f, 0.878f, 1); // Set the background colour

				g_floors.begin();
					current_floor.draw(g_floors);	// Draw the current floor
				g_floors.end();

				current_floor.setCenter(floorcX, floorcY);   // Sets the location of the floor (for motion)
				current_floor.setOrigin(WIDTH/2, HEIGHT/2 + 66); // Sets the centre of rotation for the floor

				if (System.currentTimeMillis() >= ROTATION_CLOCK + 30) {	// Sets how often to set the rotation of the floor
					ROTATION_CLOCK = System.currentTimeMillis();			// Updates the time of the last update
					current_floor.setRotation(Math.round(AZIMUTH));			// Does the rotation setting
				}

				g_devices.begin();
					g_devices.draw(current_device, cdX, cdY);				// Draw the image representing the current device

					if (!set_start) {										// If the start position has not been set
						set_start_pad.draw(g_devices);						// Draw the control pad
						set_start_pad.setX(WIDTH - 500);					// Set its X to be on the screen
						set_start_pad.setY(75);								// Set its Y (never changes elsewhere in the code though)
					}

				g_devices.end();
			}

			// Draws the menu

			g_menu.begin();
				g_menu.draw(menu_background, 0, HEIGHT - 200);
				g_menu.draw(next_floor_button, WIDTH/2 - 150, HEIGHT - 150);
			g_menu.end();
		}

		/*
		 *
		 * Code that allows the user to set the start point of their sprite on the map
		 * in comparison their real world location, and also sets their
		 * skin to the current_device.png (the first time the screen is touched)
		 *
		 */

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
				set_start = true;
				set_start_pad.setX(WIDTH + 500);
				defaultAccelX = -Gdx.input.getAccelerometerX();
				defaultAccelY = -Gdx.input.getAccelerometerY();
			}

		}

		// If the position of the holder on the map is set, update the floor's location

		if (set_start) {
			updateFloor();
		}

		// If the next_floor button is touched, update the floor and set_start is set to false
		// The control pad is brought back into view to reset the start point

		if (Gdx.input.isTouched() && Gdx.input.getX() <= WIDTH/2 + 150 && Gdx.input.getX() >= WIDTH/2 - 150 && Gdx.input.getY() >= 50 && Gdx.input.getY() <= 150 && System.currentTimeMillis() >= NEXT_FLOOR_TIMEOUT + 100) {
			current_floor_no++;
			NEXT_FLOOR_TIMEOUT = System.currentTimeMillis();

			if (current_floor_no > floors.size() - 1) {
				current_floor_no = 0;
			}
			set_start = false;
			set_start_pad.setX(WIDTH - 500);
		}
	}

	// Frees up memory by disposing all of the textures after the program is closed
	
	@Override
	public void dispose() {
		logo.dispose();
		menu_background.dispose();
		next_floor_button.dispose();

		ssp.dispose();
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

	// The splash screen start animation (fades the logo in and out)

	private void updateLogo(float delta) {
		if (alpha >= 1) {
			try {
				Thread.sleep(750);

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

	// Moves the floor when the holder moves, to help guide the holder, but only if the holder is not turning around

	private void updateFloor() {
		if (OLD_AZIMUTH == AZIMUTH) {
			if (defaultAccelX > 0) {
				floorcX += (Gdx.input.getAccelerometerX() + defaultAccelX);

			} else if (defaultAccelX < 0) {
				floorcX += (Gdx.input.getAccelerometerX() + defaultAccelX);

			} else {
				floorcX += Gdx.input.getAccelerometerX();
			}

			if (defaultAccelY > 0) {
				floorcY += (Gdx.input.getAccelerometerY() + defaultAccelY);

			} else if (defaultAccelY < 0) {
				floorcY += (Gdx.input.getAccelerometerY() + defaultAccelY);

			} else {
				floorcY += Gdx.input.getAccelerometerY();
			}

		}

	}

}
