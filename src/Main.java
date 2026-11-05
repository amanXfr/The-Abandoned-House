
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.HashSet;
import java.util.Set;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("THE ABANDONED HOUSE");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setMinimumSize(new Dimension(900, 600));
            frame.setLocationRelativeTo(null);

            frame.setContentPane(new MenuPanel(frame));
            frame.setVisible(true);
        });
    }
}

// ================= MAIN MENU =================

class MenuPanel extends JPanel {

    private final JFrame frame;
    private BufferedImage backgroundImage;

    MenuPanel(JFrame frame) {
        this.frame = frame;
        try {
            backgroundImage = ImageIO.read(new File("assets/images/menu-background.jpg"));
        } catch (IOException e) {
            System.out.println("Could not load menu background: " + e.getMessage());
        }
        setLayout(new GridBagLayout());
        setOpaque(true);

        JPanel menu = new JPanel();
        menu.setOpaque(false);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));

        JLabel eyebrow = new JLabel("A PLACE YOU SHOULD NEVER HAVE ENTERED");
        eyebrow.setFont(new Font("Arial", Font.BOLD, 12));
        eyebrow.setForeground(new Color(190, 160, 145));
        eyebrow.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("THE ABANDONED HOUSE");
        title.setFont(new Font("Serif", Font.BOLD, 48));
        title.setForeground(new Color(224, 218, 207));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("SOME DOORS ARE BETTER LEFT CLOSED");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(new Color(180, 165, 160));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton start = createButton("NEW GAME");
        JButton instructions = createButton("HOW TO PLAY");
        JButton exit = createButton("EXIT");

        start.addActionListener(e -> openGame());
        instructions.addActionListener(e ->
            JOptionPane.showMessageDialog(
                frame,
                "WASD / ARROW KEYS  -  MOVE\\n" +
                "F  -  TOGGLE FLASHLIGHT\\n" +
                "E  -  INTERACT\\n" +
                "ESC  -  RETURN TO MENU\\n\\n" +
                "Find the golden key, avoid the monster, and escape.",
                "HOW TO PLAY",
                JOptionPane.INFORMATION_MESSAGE
            )
        );
        exit.addActionListener(e -> System.exit(0));

        menu.add(eyebrow);
        menu.add(Box.createVerticalStrut(12));
        menu.add(title);
        menu.add(Box.createVerticalStrut(10));
        menu.add(subtitle);
        menu.add(Box.createVerticalStrut(42));
        menu.add(start);
        menu.add(Box.createVerticalStrut(12));
        menu.add(instructions);
        menu.add(Box.createVerticalStrut(12));
        menu.add(exit);

        add(menu);
    }

    private JButton createButton(String text) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                boolean hover = getModel().isRollover();
                g2.setColor(hover ? new Color(112, 31, 30, 245)
                                  : new Color(35, 20, 22, 225));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(hover ? new Color(220, 145, 115)
                                  : new Color(125, 76, 68));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Arial", Font.BOLD, 15));
        b.setForeground(new Color(238, 230, 222));
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(260, 48));
        b.setPreferredSize(new Dimension(260, 48));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return b;
    }

    private void openGame() {
        frame.setContentPane(new GameScreen(frame));
        frame.revalidate();
        frame.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                            RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();

        if (backgroundImage != null) {
            // Scale to fill the window without distorting the photograph.
            double scale = Math.max(
                (double) w / backgroundImage.getWidth(),
                (double) h / backgroundImage.getHeight()
            );
            int drawW = (int) (backgroundImage.getWidth() * scale);
            int drawH = (int) (backgroundImage.getHeight() * scale);
            int x = (w - drawW) / 2;
            int y = (h - drawH) / 2;
            g2.drawImage(backgroundImage, x, y, drawW, drawH, this);
        } else {
            g2.setPaint(new GradientPaint(0, 0, new Color(8, 9, 14),
                                          0, h, new Color(35, 18, 20)));
            g2.fillRect(0, 0, w, h);
        }

        // Dark cinematic overlay keeps the menu readable.
        g2.setColor(new Color(0, 0, 0, 125));
        g2.fillRect(0, 0, w, h);

        // Gentle vignette around the edges.
        g2.setPaint(new RadialGradientPaint(
            new Point(w / 2, h / 2), Math.max(w, h) * 0.72f,
            new float[]{0.35f, 1.0f},
            new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 115)}
        ));
        g2.fillRect(0, 0, w, h);
        g2.dispose();
    }
}

// ================= GAME SCREEN =================

class GameScreen extends JPanel {

    private static final int TILE = 30;
    private static final int ROWS = 20;
    private static final int COLS = 30;

    private final char[][] map = new char[ROWS][COLS];

    // Player
    private double playerX = 90;
    private double playerY = 90;

    private final double PLAYER_SPEED = 3.0;

    private double facingX = 1;
    private double facingY = 0;

    private final Set<Integer> keys = new HashSet<>();

    // Flashlight
    private boolean flashlightOn = true;
    private boolean fPressed = false;

    // Golden key
    private boolean hasKey = false;
    private boolean keyCollected = false;

    private final double keyX = 390;
    private final double keyY = 315;

    // Monster
    private double monsterX = 810;
    private double monsterY = 510;

    private double monsterSpeed = 1.45;
    private double patrolDirection = 1;

    private boolean monsterChasing = false;

    // Game status
    private boolean gameWon = false;
    private boolean gameOver = false;
    private boolean jumpscare = false;

    private String message = "Find the golden key!";

    private final JFrame frame;

    private Timer gameTimer;
    private Timer scareTimer;

    GameScreen(JFrame frame) {
        this.frame = frame;

        setBackground(Color.BLACK);
        setFocusable(true);

        createMap();
        setupControls();

        gameTimer = new Timer(16, e -> {
            updatePlayer();
            updateMonster();
            checkMonsterCollision();
            repaint();
        });

        gameTimer.start();
    }

    // ================= HOUSE MAP =================

    private void createMap() {

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {

                map[r][c] = '.';

                if (r == 0 || r == ROWS - 1 ||
                    c == 0 || c == COLS - 1) {
                    map[r][c] = '#';
                }
            }
        }

        for (int c = 2; c < 13; c++) {
            if (c != 7) map[6][c] = '#';
        }

        for (int r = 2; r < 16; r++) {
            if (r != 10) map[r][15] = '#';
        }

        for (int c = 16; c < 28; c++) {
            if (c != 22) map[13][c] = '#';
        }

        for (int r = 8; r < 18; r++) {
            if (r != 12) map[r][8] = '#';
        }

        map[3][4] = '#';
        map[3][5] = '#';
        map[4][4] = '#';

        map[9][19] = '#';
        map[9][20] = '#';

        map[16][23] = '#';
        map[16][24] = '#';

        // Exit door
        map[18][15] = 'D';
    }

    // ================= KEYBOARD CONTROLS =================

    private void setupControls() {

        int[] movementKeys = {
            KeyEvent.VK_W, KeyEvent.VK_A,
            KeyEvent.VK_S, KeyEvent.VK_D,
            KeyEvent.VK_UP, KeyEvent.VK_DOWN,
            KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT
        };

        for (int key : movementKeys) {

            getInputMap(WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(key, 0, false),
                     "press" + key);

            getInputMap(WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(key, 0, true),
                     "release" + key);

            getActionMap().put("press" + key, new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    keys.add(key);
                }
            });

            getActionMap().put("release" + key, new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    keys.remove(key);
                }
            });
        }

        // Flashlight
        getInputMap(WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_F, 0, false),
                 "flashOn");

        getInputMap(WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_F, 0, true),
                 "flashOff");

        getActionMap().put("flashOn", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!fPressed && !gameOver && !gameWon) {
                    flashlightOn = !flashlightOn;
                    fPressed = true;
                }
            }
        });

        getActionMap().put("flashOff", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                fPressed = false;
            }
        });

        // Interact
        getInputMap(WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_E, 0, false),
                 "interact");

        getActionMap().put("interact", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                interact();
            }
        });

        // Escape to return to menu
        getInputMap(WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false),
                 "pause");

        getActionMap().put("pause", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!gameWon && !gameOver) {
                    int choice = JOptionPane.showConfirmDialog(
                        frame,
                        "Return to the main menu?",
                        "PAUSE",
                        JOptionPane.YES_NO_OPTION
                    );

                    if (choice == JOptionPane.YES_OPTION) {
                        gameTimer.stop();
                        returnToMenu();
                    }
                }
            }
        });
    }

    // ================= INTERACTION =================

    private void interact() {

        if (gameWon || gameOver) return;

        double keyDistance = Math.hypot(
            playerX - keyX,
            playerY - keyY
        );

        if (!keyCollected && keyDistance < 45) {

            keyCollected = true;
            hasKey = true;

            message = "You found the golden key!";
            repaint();
            return;
        }

        double doorDistance = Math.hypot(
            playerX - 465,
            playerY - 555
        );

        if (doorDistance < 65) {

            if (hasKey) {

                gameWon = true;
                gameTimer.stop();

                int choice = JOptionPane.showOptionDialog(
                    frame,
                    "YOU ESCAPED!\n\n" +
                    "Congratulations! You survived the house.",
                    "GAME COMPLETED",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new String[]{"PLAY AGAIN", "MAIN MENU"},
                    "PLAY AGAIN"
                );

                if (choice == 0) restartGame();
                else returnToMenu();

            } else {
                message = "The exit is locked! Find the golden key.";
            }

            return;
        }

        message = "Nothing nearby to interact with.";
    }

    // ================= PLAYER MOVEMENT =================

    private void updatePlayer() {

        if (gameWon || gameOver) return;

        double dx = 0;
        double dy = 0;

        if (keys.contains(KeyEvent.VK_W) ||
            keys.contains(KeyEvent.VK_UP)) dy--;

        if (keys.contains(KeyEvent.VK_S) ||
            keys.contains(KeyEvent.VK_DOWN)) dy++;

        if (keys.contains(KeyEvent.VK_A) ||
            keys.contains(KeyEvent.VK_LEFT)) dx--;

        if (keys.contains(KeyEvent.VK_D) ||
            keys.contains(KeyEvent.VK_RIGHT)) dx++;

        if (dx != 0 || dy != 0) {

            double length = Math.sqrt(dx * dx + dy * dy);

            dx = dx / length * PLAYER_SPEED;
            dy = dy / length * PLAYER_SPEED;

            facingX = dx / PLAYER_SPEED;
            facingY = dy / PLAYER_SPEED;
        }

        if (!collides(playerX + dx, playerY, 11)) {
            playerX += dx;
        }

        if (!collides(playerX, playerY + dy, 11)) {
            playerY += dy;
        }
    }

    // ================= COLLISION =================

    private boolean collides(double x, double y, int size) {

        int left = (int) (x - size);
        int right = (int) (x + size);
        int top = (int) (y - size);
        int bottom = (int) (y + size);

        return isWall(left, top) ||
               isWall(right, top) ||
               isWall(left, bottom) ||
               isWall(right, bottom);
    }

    private boolean isWall(int x, int y) {

        int col = x / TILE;
        int row = y / TILE;

        if (row < 0 || row >= ROWS ||
            col < 0 || col >= COLS) return true;

        return map[row][col] == '#' ||
               map[row][col] == 'D';
    }

    // ================= MONSTER AI =================

    private void updateMonster() {

        if (gameOver || gameWon) return;

        double distance = Math.hypot(
            playerX - monsterX,
            playerY - monsterY
        );

        if (distance < 280) {
            monsterChasing = true;
        }

        if (distance > 390) {
            monsterChasing = false;
        }

        if (monsterChasing) {

            double dx = playerX - monsterX;
            double dy = playerY - monsterY;

            double length = Math.hypot(dx, dy);

            if (length > 0) {

                dx = dx / length * monsterSpeed;
                dy = dy / length * monsterSpeed;

                moveMonster(dx, dy);
            }

        } else {

            double nextX = monsterX + patrolDirection;

            if (collides(nextX, monsterY, 12)) {
                patrolDirection *= -1;
            } else {
                monsterX = nextX;
            }

            if (monsterX > 840) patrolDirection = -1;
            if (monsterX < 760) patrolDirection = 1;
        }
    }

    private void moveMonster(double dx, double dy) {

        if (!collides(monsterX + dx, monsterY, 12)) {
            monsterX += dx;
        }

        if (!collides(monsterX, monsterY + dy, 12)) {
            monsterY += dy;
        }
    }

    // ================= JUMPSCARE =================

    private void checkMonsterCollision() {

        if (gameOver || gameWon) return;

        double distance = Math.hypot(
            playerX - monsterX,
            playerY - monsterY
        );

        if (distance < 25) {
            triggerJumpscare();
        }
    }

    private void triggerJumpscare() {

        gameOver = true;
        jumpscare = true;
        keys.clear();

        gameTimer.stop();

        scareTimer = new Timer(1100, e -> {

            scareTimer.stop();

            int choice = JOptionPane.showOptionDialog(
                frame,
                "THE MONSTER CAUGHT YOU!\n\n" +
                "You did not escape the abandoned house.",
                "GAME OVER",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                new String[]{"TRY AGAIN", "MAIN MENU"},
                "TRY AGAIN"
            );

            if (choice == 0) restartGame();
            else returnToMenu();
        });

        scareTimer.setRepeats(false);
        scareTimer.start();

        repaint();
    }

    // ================= GOLDEN KEY =================

    private void drawKey(Graphics2D g2) {

        if (keyCollected) return;

        g2.setColor(new Color(255, 210, 50));
        g2.fillOval(383, 308, 14, 14);

        g2.setStroke(new BasicStroke(3));

        g2.drawLine(390, 315, 404, 315);
        g2.drawLine(400, 315, 400, 321);
        g2.drawLine(404, 315, 404, 320);

        g2.setStroke(new BasicStroke(1));
    }

    // ================= EXIT DOOR =================

    private void drawDoor(Graphics2D g2) {

        g2.setColor(new Color(35, 15, 10));
        g2.fillRect(445, 535, 40, 35);

        g2.setColor(new Color(100, 45, 25));
        g2.fillRect(450, 540, 30, 30);

        g2.setColor(new Color(200, 140, 70));
        g2.drawRect(450, 540, 30, 30);

        g2.setColor(Color.YELLOW);
        g2.fillOval(472, 553, 5, 5);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("EXIT", 450, 530);
    }

    // ================= PLAYER DRAWING =================

    private void drawPlayer(Graphics2D g2) {

        Graphics2D p = (Graphics2D) g2.create();

        p.translate(playerX, playerY);
        p.rotate(Math.atan2(facingY, facingX));

        // Backpack
        p.setColor(new Color(85, 48, 27));
        p.fillRoundRect(-17, -8, 13, 17, 5, 5);

        p.setColor(new Color(130, 83, 45));
        p.drawRoundRect(-17, -8, 13, 17, 5, 5);

        // Legs
        p.setColor(new Color(35, 38, 45));
        p.fillRoundRect(-8, 5, 7, 10, 4, 4);
        p.fillRoundRect(2, 5, 7, 10, 4, 4);

        // Shoes
        p.setColor(Color.DARK_GRAY);
        p.fillRoundRect(-9, 12, 9, 5, 3, 3);
        p.fillRoundRect(2, 12, 9, 5, 3, 3);

        // Hoodie
        p.setColor(new Color(30, 35, 43));
        p.fillRoundRect(-9, -6, 19, 16, 8, 8);

        p.setColor(Color.GRAY);
        p.drawRoundRect(-9, -6, 19, 16, 8, 8);

        // Arm
        p.setColor(new Color(30, 35, 43));
        p.fillRoundRect(4, -5, 13, 6, 5, 5);

        // Hand
        p.setColor(new Color(225, 180, 145));
        p.fillOval(13, -5, 6, 6);

        // Face
        p.setColor(new Color(238, 193, 156));
        p.fillOval(-6, -19, 16, 16);

        // Blond hair
        p.setColor(new Color(230, 175, 60));
        p.fillArc(-7, -21, 17, 13, 0, 180);

        // Cap
        p.setColor(new Color(24, 27, 34));
        p.fillArc(-8, -23, 19, 15, 0, 180);
        p.fillOval(5, -14, 9, 5);

        // Eye
        p.setColor(Color.BLACK);
        p.fillOval(5, -14, 2, 2);

        // Torch
        p.setColor(Color.GRAY);
        p.fillRoundRect(17, -5, 9, 6, 3, 3);

        p.setColor(Color.YELLOW);
        p.fillOval(23, -4, 4, 4);

        p.dispose();
    }

    // ================= MONSTER DRAWING =================

    private void drawMonster(Graphics2D g2) {

        Graphics2D m = (Graphics2D) g2.create();

        m.translate(monsterX, monsterY);

        m.setColor(new Color(0, 0, 0, 150));
        m.fillOval(-20, -12, 42, 38);

        m.setColor(new Color(15, 12, 18));
        m.fillOval(-16, -20, 32, 42);

        m.setColor(new Color(35, 15, 20));
        m.fillOval(-17, -23, 34, 29);

        m.setColor(Color.RED);
        m.fillOval(-11, -12, 7, 5);
        m.fillOval(5, -12, 7, 5);

        m.setColor(Color.BLACK);
        m.fillArc(-9, -3, 18, 13, 180, 180);

        m.setColor(Color.RED);
        m.drawArc(-9, -3, 18, 13, 180, 180);

        m.setColor(new Color(15, 12, 18));
        m.drawLine(-12, 0, -23, 17);
        m.drawLine(12, 0, 23, 17);

        m.dispose();
    }

    // ================= FLASHLIGHT =================

    private void drawDarkness(Graphics2D g2) {

        if (!flashlightOn) {

            g2.setColor(new Color(0, 0, 0, 225));
            g2.fillRect(0, 0, 900, 600);
            return;
        }

        double angle = Math.atan2(facingY, facingX);
        double distance = 230;

        Path2D beam = new Path2D.Double();

        beam.moveTo(playerX, playerY);

        beam.lineTo(
            playerX + Math.cos(angle - 0.65) * distance,
            playerY + Math.sin(angle - 0.65) * distance
        );

        beam.lineTo(
            playerX + Math.cos(angle + 0.65) * distance,
            playerY + Math.sin(angle + 0.65) * distance
        );

        beam.closePath();

        Area darkness = new Area(
            new Rectangle(0, 0, 900, 600)
        );

        Area light = new Area(beam);

        light.add(new Area(
            new Ellipse2D.Double(
                playerX - 55, playerY - 55, 110, 110
            )
        ));

        darkness.subtract(light);

        g2.setColor(new Color(0, 0, 0, 205));
        g2.fill(darkness);

        g2.setColor(new Color(255, 215, 120, 30));
        g2.fill(beam);
    }

    // ================= RESTART AND MENU =================

    private void restartGame() {

        if (scareTimer != null) scareTimer.stop();

        frame.setContentPane(new GameScreen(frame));
        frame.revalidate();
        frame.repaint();
    }

    private void returnToMenu() {

        if (scareTimer != null) scareTimer.stop();

        frame.setContentPane(new MenuPanel(frame));
        frame.revalidate();
        frame.repaint();
    }

    // ================= DRAW EVERYTHING =================

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        // Scale map to fullscreen
        g2.scale(
            getWidth() / 900.0,
            getHeight() / 600.0
        );

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        // House floor and walls
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {

                int x = c * TILE;
                int y = r * TILE;

                if (map[r][c] == '#') {

                    g2.setColor(new Color(35, 32, 33));
                    g2.fillRect(x, y, TILE, TILE);

                    g2.setColor(new Color(65, 55, 55));
                    g2.drawRect(x, y, TILE, TILE);

                } else {

                    g2.setColor(new Color(65, 54, 48));
                    g2.fillRect(x, y, TILE, TILE);

                    g2.setColor(new Color(75, 63, 55));
                    g2.drawRect(x, y, TILE, TILE);
                }
            }
        }

        drawDoor(g2);
        drawKey(g2);
        drawMonster(g2);
        drawDarkness(g2);
        drawPlayer(g2);

        // HUD
        g2.setColor(new Color(0, 0, 0, 235));
        g2.fillRect(0, 0, 900, 45);

        g2.setFont(new Font("Serif", Font.BOLD, 19));
        g2.setColor(Color.WHITE);
        g2.drawString("THE ABANDONED HOUSE", 15, 29);

        g2.setFont(new Font("Arial", Font.BOLD, 13));
        g2.setColor(new Color(220, 65, 65));
        g2.drawString("OBJECTIVE: Find key and escape!", 510, 28);

        // Monster status
        g2.setColor(monsterChasing ? Color.RED : Color.LIGHT_GRAY);
        g2.drawString(
            monsterChasing ? "MONSTER: CHASING YOU!" : "MONSTER: PATROLLING",
            15, 65
        );

        // Inventory
        g2.setColor(new Color(0, 0, 0, 220));
        g2.fillRoundRect(690, 480, 195, 65, 10, 10);

        g2.setColor(Color.WHITE);
        g2.drawString("INVENTORY", 705, 500);

        g2.setColor(hasKey ? Color.YELLOW : Color.GRAY);
        g2.drawString(
            hasKey ? "Golden Key: FOUND" : "Golden Key: EMPTY",
            705, 525
        );

        // Message
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString(message, 300, 565);

        // Controls
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.setColor(Color.LIGHT_GRAY);

        g2.drawString(
            "WASD / ARROWS: MOVE   F: FLASHLIGHT   E: INTERACT",
            15, 585
        );

        // Jumpscare
        if (jumpscare) {

            g2.setColor(new Color(100, 0, 0, 220));
            g2.fillRect(0, 0, 900, 600);

            g2.setColor(Color.BLACK);
            g2.fillOval(300, 110, 300, 330);

            g2.setColor(Color.RED);
            g2.fillOval(355, 200, 65, 40);
            g2.fillOval(480, 200, 65, 40);

            g2.setColor(Color.BLACK);
            g2.fillOval(370, 210, 25, 25);
            g2.fillOval(495, 210, 25, 25);

            g2.setColor(Color.RED);
            g2.setFont(new Font("Serif", Font.BOLD, 45));
            g2.drawString("RUN!", 390, 500);
        }

        g2.dispose();
    }
}
