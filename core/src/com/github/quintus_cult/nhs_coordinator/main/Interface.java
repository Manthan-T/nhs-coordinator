package com.github.quintus_cult.nhs_coordinator.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class Interface extends ApplicationAdapter {
	static final int WIDTH = 1080;
	static final int HEIGHT = 1920;

	OrthographicCamera camera;
	ExtendViewport viewport;

	SpriteBatch batch;
	Texture logo;
	
	@Override
	public void create () {
		camera = new OrthographicCamera();
		viewport = new ExtendViewport(WIDTH, HEIGHT, camera);

		batch = new SpriteBatch();
		logo = new Texture("logo.png");
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 1, 1);
		batch.begin();
		batch.draw(logo, WIDTH/2 - 343, HEIGHT/2 - 183);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		logo.dispose();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		batch.setProjectionMatrix(camera.combined);
	}

}
