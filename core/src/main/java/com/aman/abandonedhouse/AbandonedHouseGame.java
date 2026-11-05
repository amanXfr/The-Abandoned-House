package com.aman.abandonedhouse;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class AbandonedHouseGame extends Game {
    public SpriteBatch batch;
    public ShapeRenderer shapes;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapes = new ShapeRenderer();
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        if (getScreen() != null) getScreen().dispose();
        batch.dispose();
        shapes.dispose();
    }
}
