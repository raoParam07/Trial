
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGameWithButtons extends JPanel implements ActionListener, KeyListener {
    private final int TILE_SIZE = 25;
    private final int WIDTH = 20; // Number of tiles horizontally
    private final int HEIGHT = 20; // Number of tiles vertically
    private final Timer timer;
    private final ArrayList<Point> snake = new ArrayList<>();
    private Point food;
    private String direction = "RIGHT";
    private boolean running = true;
    private boolean paused = false;

    public SnakeGameWithButtons() {
        setPreferredSize(new Dimension(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        timer = new Timer(100, this);

        // Initialize snake and food
        snake.add(new Point(5, 5));
        spawnFood();
        timer.start();
    }

    private void spawnFood() {
        Random random = new Random();
        int x, y;
        do {
            x = random.nextInt(WIDTH);
            y = random.nextInt(HEIGHT);
        } while (snake.contains(new Point(x, y)));
        food = new Point(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (running) {
            // Draw the food
            g.setColor(Color.RED);
            g.fillRect(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

            // Draw the snake
            g.setColor(Color.GREEN);
            for (Point p : snake) {
                g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        } else {
            // Game over screen
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Game Over", WIDTH * TILE_SIZE / 4, HEIGHT * TILE_SIZE / 2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            moveSnake();
            checkCollision();
        }
        repaint();
    }

    private void moveSnake() {
        Point head = snake.get(0);
        Point newHead = switch (direction) {
            case "UP" -> new Point(head.x, head.y - 1);
            case "DOWN" -> new Point(head.x, head.y + 1);
            case "LEFT" -> new Point(head.x - 1, head.y);
            case "RIGHT" -> new Point(head.x + 1, head.y);
            default -> head;
        };
        snake.add(0, newHead);
        if (newHead.equals(food)) {
            spawnFood();
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    private void checkCollision() {
        Point head = snake.get(0);

        // Check collision with walls
        if (head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT) {
            running = false;
        }

        // Check collision with itself
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                running = false;
                break;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_UP:
                if (!direction.equals("DOWN")) direction = "UP";
                break;
            case KeyEvent.VK_DOWN:
                if (!direction.equals("UP")) direction = "DOWN";
                break;
            case KeyEvent.VK_LEFT:
                if (!direction.equals("RIGHT")) direction = "LEFT";
                break;
            case KeyEvent.VK_RIGHT:
                if (!direction.equals("LEFT")) direction = "RIGHT";
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public void restartGame() {
        snake.clear();
        snake.add(new Point(5, 5));
        direction = "RIGHT";
        running = true;
        paused = false;
        spawnFood();
        timer.start();
    }

    public void togglePause() {
        paused = !paused;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGameWithButtons gamePanel = new SnakeGameWithButtons();

        JButton restartButton = new JButton("Restart");
        JButton pauseButton = new JButton("Pause/Resume");

        restartButton.addActionListener(e -> gamePanel.restartGame());
        pauseButton.addActionListener(e -> gamePanel.togglePause());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(restartButton);
        buttonPanel.add(pauseButton);

        frame.setLayout(new BorderLayout());
        frame.add(gamePanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}