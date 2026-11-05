package com.aman.abandonedhouse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class MainMenuScreen extends ScreenAdapter {
    private static final float WIDTH = 1280, HEIGHT = 720;
    private final AbandonedHouseGame game;
    private final FitViewport viewport;
    private final BitmapFont titleFont = new BitmapFont();
    private final BitmapFont uiFont = new BitmapFont();
    private final GlyphLayout layout = new GlyphLayout();
    private float pulse;

    public MainMenuScreen(AbandonedHouseGame game) {
        this.game = game;
        viewport = new FitViewport(WIDTH, HEIGHT, new OrthographicCamera());
        titleFont.getData().setScale(4.2f);
        uiFont.getData().setScale(1.35f);
    }

    @Override
    public void render(float delta) {
        pulse += delta;
        ScreenUtils.clear(0.025f, 0.027f, 0.035f, 1f);
        viewport.apply();
        game.shapes.setProjectionMatrix(viewport.getCamera().combined);
        game.batch.setProjectionMatrix(viewport.getCamera().combined);

        game.shapes.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);
        // Deep, atmospheric background layers.
        game.shapes.setColor(0.035f, 0.045f, 0.055f, 1f);
        game.shapes.rect(0, 0, WIDTH, HEIGHT);
        game.shapes.setColor(0.055f, 0.065f, 0.073f, 1f);
        game.shapes.rect(0, 0, 470, HEIGHT);
        // Distant mansion silhouette.
        game.shapes.setColor(0.012f, 0.016f, 0.021f, 1f);
        game.shapes.rect(690, 125, 470, 310);
        game.shapes.rect(755, 435, 95, 105);
        game.shapes.rect(910, 435, 105, 130);
        game.shapes.rect(1070, 435, 70, 95);
        // Windows.
        game.shapes.setColor(0.34f, 0.22f, 0.12f, 0.55f);
        game.shapes.rect(755, 330, 45, 65);
        game.shapes.rect(940, 330, 45, 65);
        game.shapes.rect(1080, 330, 35, 65);
        // Foreground fog bands.
        game.shapes.setColor(0.16f, 0.18f, 0.19f, 0.12f);
        game.shapes.rect(0, 90, WIDTH, 2);
        game.shapes.rect(0, 112, WIDTH, 1);
        game.shapes.end();

        game.batch.begin();
        titleFont.setColor(new Color(0.83f, 0.82f, 0.77f, 1f));
        titleFont.draw(game.batch, "THE", 100, 555);
        titleFont.draw(game.batch, "ABANDONED", 100, 445);
        titleFont.draw(game.batch, "HOUSE", 100, 335);

        uiFont.setColor(0.62f, 0.62f, 0.58f, 1f);
        uiFont.draw(game.batch, "SOME PLACES SHOULD STAY FORGOTTEN.", 105, 260);

        float glow = 0.72f + 0.22f * (float)Math.sin(pulse * 2.2f);
        uiFont.setColor(0.78f, 0.68f, 0.49f, glow);
        uiFont.draw(game.batch, "PRESS ENTER TO BEGIN", 105, 165);
        uiFont.setColor(0.42f, 0.44f, 0.45f, 1f);
        uiFont.draw(game.batch, "WASD / ARROWS  MOVE     F  FLASHLIGHT     ESC  QUIT", 105, 105);
        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) Gdx.app.exit();
    }

    @Override
    public void resize(int width, int height) { viewport.update(width, height, true); }

    @Override
    public void dispose() {
        titleFont.dispose();
        uiFont.dispose();
    }
}
