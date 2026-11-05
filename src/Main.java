
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
    private double playerX = 450;
    private double playerY = 470;

    private final double PLAYER_SPEED = 3.0;

    private double facingX = 1;
    private double facingY = 0;

    private final Set<Integer> keys = new HashSet<>();

    // Flashlight
    private boolean flashlightOn = true;
    private boolean fPressed = false;
    // Developer cheat: bypass the darkness overlay for testing.
    private boolean developerFullBrightness = false;
    private JButton brightnessButton;

    // Golden key
    private boolean hasKey = false;
    private boolean keyCollected = false;

    private final double keyX = 450;
    private final double keyY = 405;

    // Monster
    private double monsterX = 750;
    private double monsterY = 350;

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
    private BufferedImage villaMapImage;

    GameScreen(JFrame frame) {
        this.frame = frame;
        try {
            villaMapImage = ImageIO.read(new File("assets/images/villa-map.jpg"));
        } catch (IOException e) {
            System.out.println("Could not load villa map: " + e.getMessage());
        }

        setBackground(Color.BLACK);
        setFocusable(true);
        setLayout(null);

        // Developer-only on-screen brightness toggle.
        brightnessButton = new JButton("FULL BRIGHT: OFF");
        brightnessButton.setFont(new Font("Arial", Font.BOLD, 12));
        brightnessButton.setForeground(new Color(255, 235, 190));
        brightnessButton.setBackground(new Color(55, 42, 25));
        brightnessButton.setFocusPainted(false);
        brightnessButton.setBorder(BorderFactory.createLineBorder(new Color(205, 160, 75), 2));
        brightnessButton.setOpaque(true);
        brightnessButton.setToolTipText("Developer toggle: reveal the entire villa map");
        brightnessButton.addActionListener(e -> {
            developerFullBrightness = !developerFullBrightness;
            brightnessButton.setText(developerFullBrightness ? "FULL BRIGHT: ON" : "FULL BRIGHT: OFF");
            brightnessButton.setBackground(developerFullBrightness
                ? new Color(105, 80, 25) : new Color(55, 42, 25));
            repaint();
            requestFocusInWindow();
        });
        add(brightnessButton);

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

    @Override
    public void doLayout() {
        super.doLayout();
        if (brightnessButton != null) {
            brightnessButton.setBounds(Math.max(12, getWidth() - 190), 52, 178, 34);
        }
    }

    // ================= HOUSE MAP =================

    private void createMap() {

        // Start with an open floor and build the villa's room layout.
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                map[r][c] = (r == 0 || r == ROWS - 1 || c == 0 || c == COLS - 1) ? '#' : '.';
            }
        }

        // Main room dividers: library / living room on the left,
        // grand foyer in the middle, dining room / kitchen on the right.
        for (int r = 1; r < 19; r++) {
            if (r != 5 && r != 14) map[r][10] = '#';
            if (r != 5 && r != 14) map[r][19] = '#';
        }

        // Left wing: library above, lounge below.
        for (int c = 1; c < 10; c++) {
            if (c != 5) map[9][c] = '#';
        }

        // Right wing: dining room above, kitchen below.
        for (int c = 20; c < 29; c++) {
            if (c != 24) map[9][c] = '#';
        }

        // Lower private rooms, connected to the central hall.
        for (int c = 1; c < 10; c++) {
            if (c != 5) map[15][c] = '#';
        }
        for (int c = 20; c < 29; c++) {
            if (c != 24) map[15][c] = '#';
        }

        // Bathroom / utility room divider in the lower-left wing.
        for (int r = 16; r < 19; r++) {
            if (r != 17) map[r][5] = '#';
        }

        // Keep the main exit at the end of the grand foyer.
        map[18][15] = 'D';

        // Decorative grand staircase boundary at the north end of the foyer.
        // The middle opening remains walkable.
        for (int c = 12; c <= 17; c++) {
            if (c != 14 && c != 15) map[2][c] = '#';
        }
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
                message = "The main exit is locked! Find the golden key.";
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

        // Keep the player inside the villa image.
        if (x - size < 6 || y - size < 6 ||
            x + size > 894 || y + size > 594) {
            return true;
        }

        // Collision boxes are authored in the 900 x 600 map coordinate space.
        // They cover the large solid walls and furniture, leaving doorways and
        // the main corridors open. Small decorative objects remain walkable.
        Rectangle2D.Double[] solidAreas = {
            // Library: bookcases, desk, chair and globe.
            new Rectangle2D.Double(42, 48, 235, 92),
            new Rectangle2D.Double(112, 190, 132, 48),
            new Rectangle2D.Double(145, 150, 58, 40),
            new Rectangle2D.Double(43, 235, 34, 48),

            // Grand staircase and foyer centre table.
            new Rectangle2D.Double(340, 0, 220, 82),
            new Rectangle2D.Double(405, 245, 92, 72),

            // Dining room table and chairs.
            new Rectangle2D.Double(625, 128, 205, 112),

            // Living room fireplace, coffee table and sofas.
            new Rectangle2D.Double(45, 258, 260, 92),
            new Rectangle2D.Double(142, 360, 112, 68),
            new Rectangle2D.Double(78, 405, 62, 48),
            new Rectangle2D.Double(230, 405, 62, 48),

            // Kitchen counters, cooker and island.
            new Rectangle2D.Double(655, 270, 215, 72),
            new Rectangle2D.Double(700, 372, 125, 62),

            // Bathroom: vanity, bath and toilet.
            new Rectangle2D.Double(68, 432, 175, 65),
            new Rectangle2D.Double(95, 505, 100, 52),
            new Rectangle2D.Double(205, 505, 45, 38),

            // Bedroom: wardrobe, bed and bedside cabinets.
            new Rectangle2D.Double(680, 445, 72, 105),
            new Rectangle2D.Double(755, 455, 115, 112),
            new Rectangle2D.Double(650, 475, 30, 55),
            new Rectangle2D.Double(865, 475, 25, 55),

            // Decorative planters beside the central hall.
            new Rectangle2D.Double(298, 270, 28, 35),
            new Rectangle2D.Double(574, 270, 28, 35),
            new Rectangle2D.Double(298, 455, 28, 35),
            new Rectangle2D.Double(574, 455, 28, 35)
        };

        Rectangle2D.Double playerBox =
            new Rectangle2D.Double(x - size, y - size, size * 2.0, size * 2.0);

        for (Rectangle2D.Double area : solidAreas) {
            if (playerBox.intersects(area)) return true;
        }

        // Main villa room walls. Each segment stops before its doorway opening.
        Rectangle2D.Double[] wallAreas = {
            // Left wing upper/lower boundaries.
            new Rectangle2D.Double(25, 215, 8, 25),
            new Rectangle2D.Double(25, 240, 8, 120),
            new Rectangle2D.Double(25, 360, 8, 28),
            new Rectangle2D.Double(25, 388, 8, 125),
            new Rectangle2D.Double(25, 513, 8, 70),

            // Right wing room partitions, preserving the central door gaps.
            new Rectangle2D.Double(590, 235, 9, 110),
            new Rectangle2D.Double(590, 345, 9, 38),
            new Rectangle2D.Double(590, 383, 9, 130),

            // Lower bedroom and bathroom partition walls.
            new Rectangle2D.Double(80, 420, 240, 8),
            new Rectangle2D.Double(80, 420, 8, 25),
            new Rectangle2D.Double(250, 420, 70, 8),
            new Rectangle2D.Double(610, 420, 270, 8)
        };

        for (Rectangle2D.Double wall : wallAreas) {
            if (playerBox.intersects(wall)) return true;
        }

        return false;
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

    // ================= LUXURY VILLA FURNITURE =================

    private void drawVillaFurniture(Graphics2D g2) {
        // Grand foyer runner and central floral table.
        g2.setColor(new Color(75, 19, 25));
        g2.fillRoundRect(350, 185, 200, 190, 12, 12);
        g2.setColor(new Color(190, 145, 82));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(357, 192, 186, 176, 9, 9);
        g2.setStroke(new BasicStroke(1));
        g2.setColor(new Color(90, 48, 29));
        g2.fillOval(405, 245, 90, 65);
        g2.setColor(new Color(170, 119, 65));
        g2.drawOval(405, 245, 90, 65);
        g2.setColor(new Color(66, 105, 54));
        g2.fillOval(438, 258, 25, 25);
        g2.setColor(new Color(205, 170, 190));
        g2.fillOval(444, 253, 10, 10);
        g2.fillOval(432, 264, 10, 10);
        g2.fillOval(451, 267, 10, 10);

        // Library shelves and writing desk (upper-left room).
        drawCabinet(g2, 45, 48, 225, 42);
        drawCabinet(g2, 45, 96, 225, 42);
        g2.setColor(new Color(64, 35, 24));
        g2.fillRoundRect(105, 190, 135, 48, 5, 5);
        g2.setColor(new Color(145, 101, 57));
        g2.drawRect(112, 196, 121, 34);
        g2.setColor(new Color(110, 25, 29));
        g2.fillRoundRect(150, 150, 48, 38, 8, 8);
        g2.setColor(new Color(190, 145, 85));
        g2.drawRoundRect(150, 150, 48, 38, 8, 8);

        // Lounge: fireplace, coffee table and two sofas (middle-left).
        g2.setColor(new Color(75, 42, 29));
        g2.fillRect(65, 330, 105, 20);
        g2.setColor(new Color(145, 104, 69));
        g2.drawRect(70, 334, 95, 13);
        g2.setColor(new Color(255, 125, 35));
        g2.fillOval(95, 333, 28, 13);
        g2.setColor(new Color(255, 205, 90));
        g2.fillOval(104, 333, 10, 10);
        drawSofa(g2, 60, 420, 72, 38);
        drawSofa(g2, 205, 420, 72, 38);
        g2.setColor(new Color(80, 43, 28));
        g2.fillRoundRect(135, 435, 65, 34, 7, 7);
        g2.setColor(new Color(173, 130, 79));
        g2.drawRoundRect(135, 435, 65, 34, 7, 7);

        // Dining room: long polished table and six chairs (upper-right).
        g2.setColor(new Color(91, 48, 29));
        g2.fillRoundRect(665, 135, 150, 72, 8, 8);
        g2.setColor(new Color(190, 143, 82));
        g2.drawRoundRect(670, 140, 140, 62, 6, 6);
        for (int i = 0; i < 3; i++) {
            drawChair(g2, 680 + i * 48, 112);
            drawChair(g2, 680 + i * 48, 210);
        }
        g2.setColor(new Color(58, 95, 48));
        g2.fillOval(730, 158, 18, 18);
        g2.setColor(new Color(190, 55, 65));
        g2.fillOval(736, 154, 9, 9);

        // Kitchen counters and island (middle-right).
        drawCabinet(g2, 635, 330, 235, 38);
        drawCabinet(g2, 635, 375, 235, 38);
        g2.setColor(new Color(35, 35, 37));
        g2.fillRoundRect(690, 425, 120, 58, 7, 7);
        g2.setColor(new Color(155, 155, 150));
        g2.drawRoundRect(695, 430, 110, 48, 5, 5);
        g2.setColor(new Color(20, 20, 22));
        for (int i = 0; i < 4; i++) g2.fillOval(708 + (i % 2) * 42, 439 + (i / 2) * 20, 13, 13);

        // Lower-left bathroom: tiled bath and vanity.
        g2.setColor(new Color(105, 111, 112));
        g2.fillRoundRect(48, 505, 105, 48, 20, 20);
        g2.setColor(new Color(205, 207, 198));
        g2.drawRoundRect(52, 509, 97, 39, 18, 18);
        g2.setColor(new Color(70, 110, 125));
        g2.fillRoundRect(61, 516, 80, 25, 14, 14);
        g2.setColor(new Color(150, 145, 132));
        g2.fillOval(205, 510, 38, 28);
        g2.setColor(new Color(220, 210, 190));
        g2.drawOval(209, 514, 30, 20);

        // Lower-right bedroom: large bed, headboard and bedside tables.
        g2.setColor(new Color(67, 37, 27));
        g2.fillRoundRect(690, 490, 145, 78, 8, 8);
        g2.setColor(new Color(130, 88, 55));
        g2.drawRoundRect(695, 495, 135, 68, 6, 6);
        g2.setColor(new Color(190, 164, 125));
        g2.fillRoundRect(705, 505, 115, 55, 8, 8);
        g2.setColor(new Color(235, 220, 190));
        g2.fillRoundRect(715, 510, 42, 20, 7, 7);
        g2.fillRoundRect(768, 510, 42, 20, 7, 7);
        g2.setColor(new Color(75, 42, 27));
        g2.fillRect(660, 510, 25, 35);
        g2.fillRect(840, 510, 25, 35);
        g2.setColor(new Color(220, 175, 90));
        g2.fillOval(669, 518, 7, 7);
        g2.fillOval(849, 518, 7, 7);

        // Indoor plants around the foyer.
        drawPlant(g2, 315, 255);
        drawPlant(g2, 570, 255);
        drawPlant(g2, 315, 465);
        drawPlant(g2, 570, 465);

        // Grand staircase at the north end of the foyer.
        g2.setColor(new Color(72, 35, 25));
        g2.fillRect(360, 0, 180, 75);
        for (int y = 8; y < 72; y += 13) {
            g2.setColor(new Color(155, 112, 68));
            g2.fillRect(365, y, 170, 4);
            g2.setColor(new Color(45, 25, 20));
            g2.drawLine(365, y + 5, 535, y + 5);
        }
        g2.setColor(new Color(190, 145, 80));
        g2.drawLine(355, 5, 355, 76);
        g2.drawLine(545, 5, 545, 76);
    }

    private void drawCabinet(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(new Color(65, 35, 24));
        g2.fillRoundRect(x, y, w, h, 5, 5);
        g2.setColor(new Color(151, 105, 59));
        g2.drawRoundRect(x + 3, y + 3, w - 6, h - 6, 4, 4);
        for (int i = 1; i < 7; i++) {
            int bx = x + i * w / 7;
            g2.setColor(new Color(105, 62, 35));
            g2.drawLine(bx, y + 5, bx, y + h - 5);
            g2.setColor(new Color(170, 125, 68));
            g2.fillRect(bx + 2, y + 8, 3, Math.max(8, h - 17));
        }
    }

    private void drawSofa(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(new Color(88, 30, 34));
        g2.fillRoundRect(x, y, w, h, 12, 12);
        g2.setColor(new Color(170, 112, 75));
        g2.drawRoundRect(x + 3, y + 3, w - 6, h - 6, 10, 10);
        g2.setColor(new Color(125, 60, 57));
        g2.fillRoundRect(x + 8, y + 7, w - 16, h - 14, 8, 8);
    }

    private void drawChair(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(70, 31, 29));
        g2.fillRoundRect(x, y, 28, 20, 6, 6);
        g2.setColor(new Color(174, 119, 68));
        g2.drawRoundRect(x + 2, y + 2, 24, 16, 5, 5);
    }

    private void drawPlant(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(105, 57, 34));
        g2.fillRoundRect(x - 10, y + 12, 22, 18, 5, 5);
        g2.setColor(new Color(43, 86, 43));
        g2.fillOval(x - 15, y - 8, 20, 24);
        g2.fillOval(x - 3, y - 15, 21, 25);
        g2.fillOval(x - 7, y - 2, 22, 20);
        g2.setColor(new Color(85, 125, 63));
        g2.drawLine(x, y + 15, x + 2, y - 8);
    }

    // ================= GOLDEN KEY =================

    private void drawKey(Graphics2D g2) {

        if (keyCollected) return;

        int x = (int) keyX;
        int y = (int) keyY;

        // Draw the key exactly where the interaction check expects it.
        g2.setColor(new Color(255, 210, 50));
        g2.fillOval(x - 7, y - 7, 14, 14);
        g2.setColor(new Color(255, 238, 150));
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(x + 4, y, x + 17, y);
        g2.drawLine(x + 12, y, x + 12, y + 6);
        g2.drawLine(x + 17, y, x + 17, y + 5);
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

        // Developer full-bright mode disables all darkness and flashlight masking.
        if (developerFullBrightness) return;

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

        // Detailed luxury villa map artwork.
        if (villaMapImage != null) {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(villaMapImage, 0, 0, 900, 600, this);
        } else {
            g2.setColor(new Color(35, 25, 20));
            g2.fillRect(0, 0, 900, 600);
            g2.setColor(Color.WHITE);
            g2.drawString("Villa map missing: assets/images/villa-map.jpg", 250, 300);
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
