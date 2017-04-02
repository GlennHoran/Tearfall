package com.frames;

import com.mvc.MvcContainer;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mvc.GameController;
import com.mvc.GameView;

public class GameFrame implements ApplicationListener {
    private GameView view;
    private GameController controller;
    private MvcContainer mvcContainer;
    private SpriteBatch batch;
    private Texture img;
	private ShapeRenderer shapeRenderer;
	private BitmapFont font;

    @Override
	public void create () {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        img = new Texture("core/assets/blocks.png");
		font = new BitmapFont();

        view.setShapeRenderer(new ShapeRenderer());
        view.setSpriteBatch(batch);
        view.setTileset(img);
		view.setFont(font);
    }

    @Override
    public void resize(int width, int height) {
	    view.setShapeRenderer(new ShapeRenderer());
	    view.setWindowSize(width, height);
        batch.dispose();
        batch = new SpriteBatch();
        view.setSpriteBatch(batch);
    }

    @Override
	public void render () {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        controller.showFrame();
	}

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
    	batch.dispose();
    	img.dispose();
    	shapeRenderer.dispose();
    	font.dispose();
    }

	public void setMvcContainer(MvcContainer mvcContainer) {
		this.mvcContainer = mvcContainer;
		this.view = mvcContainer.getView();
		this.controller = mvcContainer.getController();
	}
}