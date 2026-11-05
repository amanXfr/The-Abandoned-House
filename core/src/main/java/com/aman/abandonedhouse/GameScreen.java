package com.aman.abandonedhouse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class GameScreen extends ScreenAdapter {
    private static final float WIDTH = 1280, HEIGHT = 720;
    private final AbandonedHouseGame game;
    private final FitViewport viewport;
    private final BitmapFont font = new BitmapFont();
    private float playerX = 250, playerY = 250;
    private final float playerRadius = 13;
    private float moveSpeed = 230;
    private boolean flashlight = true;
    private float flicker;

    public GameScreen(AbandonedHouseGame game) {
        this.game = game;
        viewport = new FitViewport(WIDTH, HEIGHT, new OrthographicCamera());
        font.getData().setScale(1.15f);
    }

    @Override
    public void render(float delta) {
        float dt = Math.min(delta, 1f / 30f);
        flicker += dt;

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) flashlight = !flashlight;

        float dx = 0, dy = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) dx--;
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) dx++;
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) dy++;
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) dy--;
        if (dx != 0 || dy != 0) {
            float length = (float)Math.sqrt(dx * dx + dy * dy);
            playerX += dx / length * moveSpeed * dt;
            playerY += dy / length * moveSpeed * dt;
        }
        playerX = Math.max(42, Math.min(WIDTH - 42, playerX));
        playerY = Math.max(42, Math.min(HEIGHT - 42, playerY));

        ScreenUtils.clear(0.018f, 0.022f, 0.025f, 1f);
        viewport.apply();
        game.shapes.setProjectionMatrix(viewport.getCamera().combined);
        game.batch.setProjectionMatrix(viewport.getCamera().combined);

        drawMansion();

        game.shapes.begin(ShapeRenderer.ShapeType.Filled);
        // A simple warm flashlight pool. This will be replaced by a proper light system.
        if (flashlight) {
            float lightPulse = 0.92f + 0.08f * (float)Math.sin(flicker * 7f);
            game.shapes.setColor(0.62f, 0.48f, 0.27f, 0.08f * lightPulse);
            game.shapes.circle(playerX, playerY, 180, 48);
            game.shapes.setColor(0.72f, 0.61f, 0.39f, 0.08f * lightPulse);
            game.shapes.circle(playerX, playerY, 115, 48);
        }
        // Player silhouette and small torch glow.
        game.shapes.setColor(0.02f, 0.025f, 0.03f, 1f);
        game.shapes.circle(playerX, playerY - 3, playerRadius + 4, 20);
        game.shapes.setColor(0.17f, 0.19f, 0.21f, 1f);
        game.shapes.circle(playerX, playerY, playerRadius, 20);
        game.shapes.setColor(0.72f, 0.58f, 0.39f, 1f);
        game.shapes.circle(playerX + 2, playerY + 8, 6, 16);
        game.shapes.setColor(0.83f, 0.67f, 0.36f, 1f);
        game.shapes.circle(playerX + 17, playerY + 2, 3, 12);
        game.shapes.end();

        game.batch.begin();
        font.setColor(0.86f, 0.84f, 0.77f, 1f);
        font.draw(game.batch, "THE ABANDONED HOUSE", 34, HEIGHT - 30);
        font.setColor(0.57f, 0.59f, 0.57f, 1f);
        font.draw(game.batch, "WASD / ARROWS  MOVE     F  FLASHLIGHT     ESC  MENU", 34, HEIGHT - 58);
        font.setColor(flashlight ? new Color(0.82f, 0.69f, 0.43f, 1f) : Color.GRAY);
        font.draw(game.batch, flashlight ? "FLASHLIGHT: ON" : "FLASHLIGHT: OFF", WIDTH - 205, HEIGHT - 30);
        game.batch.end();
    }

    private void drawMansion() {
        game.shapes.begin(ShapeRenderer.ShapeType.Filled);
        // Main floor.
        game.shapes.setColor(0.105f, 0.105f, 0.095f, 1f);
        game.shapes.rect(70, 55, 1140, 610);

        // Worn floorboards.
        for (int y = 65; y < 655; y += 42) {
            game.shapes.setColor(0.13f, 0.125f, 0.11f, 1f);
            game.shapes.rect(82, y, 1116, 2);
            for (int x = 100 + ((y / 42) % 2) * 45; x < 1190; x += 180) {
                game.shapes.setColor(0.075f, 0.078f, 0.073f, 1f);
                game.shapes.rect(x, y, 2, 40);
            }
        }

        // Heavy outer walls and interior partitions.
        game.shapes.setColor(0.035f, 0.039f, 0.04f, 1f);
        game.shapes.rect(55, 40, 1170, 20);
        game.shapes.rect(55, 40, 20, 640);
        game.shapes.rect(1205, 40, 20, 640);
        game.shapes.rect(55, 660, 1170, 20);

        // Room dividers with open doorways.
        game.shapes.rect(430, 60, 14, 245);
        game.shapes.rect(430, 365, 14, 295);
        game.shapes.rect(845, 60, 14, 245);
        game.shapes.rect(845, 365, 14, 295);
        game.shapes.rect(445, 350, 400, 14);
        game.shapes.rect(75, 470, 355, 14);
        game.shapes.rect(860, 470, 340, 14);

        // Bedroom: bed, pillows and bedside tables.
        game.shapes.setColor(0.19f, 0.17f, 0.15f, 1f);
        game.shapes.rect(125, 520, 190, 105);
        game.shapes.setColor(0.34f, 0.31f, 0.27f, 1f);
        game.shapes.rect(137, 531, 166, 82);
        game.shapes.setColor(0.48f, 0.45f, 0.39f, 1f);
        game.shapes.rect(145, 585, 65, 22);
        game.shapes.rect(220, 585, 65, 22);
        game.shapes.setColor(0.16f, 0.13f, 0.11f, 1f);
        game.shapes.rect(335, 545, 58, 58);
        game.shapes.rect(105, 400, 72, 42);

        // Living room: sofa and coffee table.
        game.shapes.setColor(0.22f, 0.16f, 0.14f, 1f);
        game.shapes.rect(515, 520, 250, 70);
        game.shapes.setColor(0.31f, 0.23f, 0.19f, 1f);
        game.shapes.rect(530, 530, 220, 48);
        game.shapes.setColor(0.12f, 0.105f, 0.09f, 1f);
        game.shapes.rect(570, 405, 140, 65);
        game.shapes.setColor(0.27f, 0.22f, 0.17f, 1f);
        game.shapes.rect(580, 415, 120, 45);

        // Dining room: table and chairs.
        game.shapes.setColor(0.19f, 0.14f, 0.105f, 1f);
        game.shapes.rect(930, 520, 190, 95);
        game.shapes.setColor(0.29f, 0.22f, 0.16f, 1f);
        game.shapes.rect(945, 535, 160, 65);
        game.shapes.setColor(0.11f, 0.095f, 0.08f, 1f);
        game.shapes.rect(900, 545, 22, 42);
        game.shapes.rect(1128, 545, 22, 42);
        game.shapes.rect(1005, 625, 42, 20);

        // Kitchen counters and old sink.
        game.shapes.setColor(0.20f, 0.21f, 0.19f, 1f);
        game.shapes.rect(930, 115, 245, 65);
        game.shapes.setColor(0.31f, 0.32f, 0.29f, 1f);
        game.shapes.rect(942, 126, 220, 42);
        game.shapes.setColor(0.08f, 0.10f, 0.10f, 1f);
        game.shapes.rect(990, 135, 55, 25);

        // Old rug in the central hall.
        game.shapes.setColor(0.22f, 0.075f, 0.065f, 1f);
        game.shapes.rect(490, 190, 310, 115);
        game.shapes.setColor(0.38f, 0.20f, 0.13f, 1f);
        game.shapes.rect(502, 202, 286, 91);
        game.shapes.setColor(0.22f, 0.075f, 0.065f, 1f);
        game.shapes.rect(515, 215, 260, 65);

        // Windows with faint moonlight.
        game.shapes.setColor(0.16f, 0.22f, 0.25f, 1f);
        game.shapes.rect(185, 665, 90, 10);
        game.shapes.rect(1010, 665, 90, 10);
        game.shapes.setColor(0.29f, 0.34f, 0.34f, 1f);
        game.shapes.rect(228, 663, 3, 14);
        game.shapes.rect(1053, 663, 3, 14);

        // Door panels.
        game.shapes.setColor(0.24f, 0.16f, 0.11f, 1f);
        game.shapes.rect(438, 305, 7, 60);
        game.shapes.rect(853, 305, 7, 60);
        game.shapes.setColor(0.62f, 0.43f, 0.20f, 1f);
        game.shapes.circle(441, 320, 3, 12);
        game.shapes.circle(856, 320, 3, 12);
        game.shapes.end();
    }

    @Override
    public void resize(int width, int height) { viewport.update(width, height, true); }

    @Override
    public void dispose() { font.dispose(); }
}
