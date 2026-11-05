
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

    // One source of truth for collision and the visible developer outlines.
    // Traced from the user's annotated floor-plan screenshot, normalized to 900x600.
    // Each string is a polyline: x,y pairs separated by spaces.
    private static final String[] ANNOTATED_WALL_PATHS = {
        "49,239 49,330 53,332 53,358 72,357 71,336 74,330 169,330 177,333 178,331 189,330 326,330 326,238",
        "594,55 594,128 845,129 848,127 847,55",
        "90,439 90,504 224,505 235,503 234,437 91,437",
        "416,283 416,355 483,355 483,284 445,282",
        "62,54 64,147 87,147 88,143 169,142 89,141 88,65 90,61 92,61 92,63 93,61 92,58 89,57 88,52 128,50 278,52 278,141 205,142 206,143 278,143 278,117 280,111 282,143 282,59 304,58 306,145 281,146 306,147 307,71 305,56 282,56 280,70 278,50 88,50 87,52 63,52",
        "584,269 583,344 623,345 624,269",
        "746,439 747,492 796,492 798,472 797,439",
        "648,442 647,510 676,510 678,512 678,537 741,537 741,497 740,536 680,535 678,517 678,445 680,443 740,443 741,495 743,443 741,441",
        "321,442 320,511 344,512 347,508 347,444 345,442",
        "500,44 498,73 499,87 514,86 530,88 531,161 533,159 531,158 530,82 533,76 538,76 539,74 538,72 531,71 531,48 571,47 573,49 574,158 558,159 558,161 574,160 574,46 545,45 540,47 530,44",
        "326,45 326,160 344,161 344,159 326,158 327,48 345,45 375,47 376,158 370,159 370,161 376,160 376,85 402,84 402,48 378,48 376,45 372,44",
        "72,162 73,208 97,208 97,162",
        "53,378 52,416 67,416 67,379",
        "839,267 776,265 684,266 683,346 813,345 684,345 683,269 685,267 836,267 838,270 838,295",
        "354,419 354,571 90,582 88,556 87,433 89,581 87,583 81,583 259,575 300,575 310,573 316,575 353,574 355,577 357,572 363,571 355,570",
        "854,66 860,423 856,98",
        "40,214 40,427 281,425 40,426 41,213",
        "804,567 686,571 622,571 575,574 534,574 510,569 495,569 495,571 551,575",
        "55,50 53,54 53,225",
        "234,227 71,230 160,230",
        "372,283 346,283 346,334 349,335 350,333 347,331 347,285 373,284",
        "552,438 551,517 566,517 553,517 551,515 551,442 553,439 617,440 617,515 614,517 617,517 617,438",
        "257,441 256,517 284,518 285,517 258,517 256,513 257,444 285,442",
        "809,476 811,546 811,496",
        "838,317 838,344 819,344 818,346 838,345",
        "173,142 200,143 190,141",
        "349,159 349,161 366,160",
        "750,359 756,361 761,359",
        "219,158 219,167 221,162",
        "182,119 192,120 188,118",
        "357,103 355,107 358,108",
        "357,117 355,120 358,120",
        "373,289 373,330 371,333 355,333 373,333",
        "729,160 729,163 731,163",
        "544,118 543,120 545,122",
        "721,359 719,359 719,407 719,360",
        "812,548 812,564",
        "358,538 404,538",
        "494,532 535,533",
        "571,517 609,517",
        "117,515 161,515",
        "809,435 809,473",
        "539,433 541,571",
        "322,435 345,433",
        "610,427 540,428",
        "842,427 654,428",
        "781,359 793,359",
        "763,359 773,359",
        "739,359 747,359",
        "116,339 131,339",
        "838,301 838,314",
        "340,298 341,353",
        "657,254 741,255",
        "846,252 787,253",
        "575,235 574,349",
        "340,225 340,296",
        "332,224 324,225",
        "244,224 304,225",
        "537,159 554,159",
        "144,150 143,203",
        "535,115 539,118",
        "585,50 586,165",
        "313,49 314,181"
    };

    private static final String[] ANNOTATED_FURNITURE_PATHS = {
        "651,144 651,221 802,221 802,144",
        "170,112 170,150 163,152 146,152 146,203 239,204 240,152 206,152 203,149 204,112",
        "411,535 412,599 485,599 484,534 413,533",
        "716,357 715,386 717,410 798,409 798,356",
        "569,462 569,528 610,529 612,527 611,462",
        "100,336 99,405 134,405 134,336",
        "112,511 113,552 170,552 170,511",
        "236,341 234,343 235,402 271,402 271,341",
        "155,362 156,400 213,399 212,361",
        "527,280 526,330 557,331 559,298 558,281",
        "369,465 368,520 397,521 397,465",
        "816,297 814,315 815,355 824,357 840,356 841,298",
        "502,470 502,519 529,520 530,471",
        "352,286 351,335 374,336 376,335 376,286",
        "346,129 346,174 367,174 368,129",
        "535,131 534,175 556,176 556,132",
        "267,303 267,313 275,314 275,303",
        "97,303 97,312 105,313 104,303",
        "83,303 83,313 91,313 90,303 88,303 86,307",
        "268,285 267,296 272,297 273,289 271,285",
        "258,284 255,286 256,296 260,297 260,285",
        "97,285 97,296 100,297 102,295 102,289 100,285",
        "260,303 254,303 254,306 252,307 254,313 260,308",
        "87,285 89,297 91,295 91,286",
        "267,265 267,276 269,277 269,267",
        "260,264 258,276 261,275",
        "778,468 777,474 780,474",
        "97,266 96,275 98,275",
        "91,267 89,276 91,275",
        "333,277 334,292"
    };

    private Shape[] buildAnnotatedShapes(String[] encodedPaths, float strokeWidth) {
        ArrayList<Shape> shapes = new ArrayList<>();
        BasicStroke collisionStroke = new BasicStroke(strokeWidth,
            BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

        for (String encoded : encodedPaths) {
            String[] points = encoded.trim().split("\\s+");
            Path2D.Double path = new Path2D.Double();
            boolean first = true;
            for (String point : points) {
                String[] xy = point.split(",");
                double px = Double.parseDouble(xy[0]);
                double py = Double.parseDouble(xy[1]);
                if (first) {
                    path.moveTo(px, py);
                    first = false;
                } else {
                    path.lineTo(px, py);
                }
            }
            path.closePath();
            shapes.add(collisionStroke.createStrokedShape(path));
        }
        return shapes.toArray(new Shape[0]);
    }

    private Shape[] getAnnotatedWallShapes() {
        return buildAnnotatedShapes(ANNOTATED_WALL_PATHS, 4.0f);
    }

    private Shape[] getAnnotatedFurnitureShapes() {
        return buildAnnotatedShapes(ANNOTATED_FURNITURE_PATHS, 4.0f);
    }

    private boolean collides(double x, double y, int size) {
        if (x - size < 6 || y - size < 6 ||
            x + size > 894 || y + size > 594) {
            return true;
        }

        Rectangle2D.Double playerBox =
            new Rectangle2D.Double(x - size, y - size, size * 2.0, size * 2.0);

        for (Shape wall : getAnnotatedWallShapes()) {
            if (wall.intersects(playerBox)) return true;
        }
        for (Shape furniture : getAnnotatedFurnitureShapes()) {
            if (furniture.intersects(playerBox)) return true;
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

    // ================= COLLISION DEBUG OVERLAY =================

    private void drawCollisionOverlay(Graphics2D g2) {
        Stroke oldStroke = g2.getStroke();

        // Draw the same traced polylines used by collision detection.
        g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(255, 75, 45, 225));
        for (String encoded : ANNOTATED_WALL_PATHS) {
            drawEncodedPath(g2, encoded);
        }

        g2.setColor(new Color(40, 190, 255, 230));
        for (String encoded : ANNOTATED_FURNITURE_PATHS) {
            drawEncodedPath(g2, encoded);
        }

        // Outer playable-map boundary.
        g2.setColor(new Color(255, 220, 70, 170));
        g2.setStroke(new BasicStroke(0.8f, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, 10.0f, new float[]{4.0f, 4.0f}, 0.0f));
        g2.drawRect(6, 6, 888, 588);
        g2.setStroke(oldStroke);
    }

    private void drawEncodedPath(Graphics2D g2, String encoded) {
        String[] points = encoded.trim().split("\\s+");
        Path2D.Double path = new Path2D.Double();
        boolean first = true;
        for (String point : points) {
            String[] xy = point.split(",");
            double px = Double.parseDouble(xy[0]);
            double py = Double.parseDouble(xy[1]);
            if (first) {
                path.moveTo(px, py);
                first = false;
            } else {
                path.lineTo(px, py);
            }
        }
        path.closePath();
        g2.draw(path);
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
        drawCollisionOverlay(g2);
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
