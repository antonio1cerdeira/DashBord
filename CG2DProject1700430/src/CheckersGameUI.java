import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;	
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CheckersGameUI extends JPanel {
    private int tileSize = 50;  // Size of each square on the board
    private int boardSize = 8;  // Size of the board (8x8)
    private Piece[][] board;
    private Piece selectedPiece = null;
    private int selectedRow = -1, selectedCol = -1;

    // Two lists of colors: light colors for white pieces and dark colors for brown pieces
    private Color[] lightColors = {Color.WHITE, Color.PINK, Color.CYAN, Color.YELLOW};  // Light colors for white pieces
    private Color[] darkColors = {new Color(139, 69, 19), Color.BLACK, Color.DARK_GRAY, Color.MAGENTA};  // Dark colors for brown pieces
    private int currentLightColorIndex = 0;
    private int currentDarkColorIndex = 0;

    // Variable to control the transparency (alpha) of the board
    private float boardAlpha = 1.0f; // Board opacity (1.0 = opaque, 0.0 = transparent)

    public static void main(String[] args) {
        // Creating the JFrame
        JFrame frame = new JFrame("Checkers Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);

        // Adding the checkers game board
        CheckersGameUI checkersGameUI = new CheckersGameUI();
        frame.add(checkersGameUI, BorderLayout.CENTER);

        // Displaying the JFrame
        frame.setVisible(true);
    }

    public CheckersGameUI() {
        board = new Piece[boardSize][boardSize];
        initializeBoard();

        // Mouse event to select and move pieces
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = e.getY() / tileSize;
                int col = e.getX() / tileSize;

                // Check bounds before handling the click
                if (row >= 0 && row < boardSize && col >= 0 && col < boardSize) {
                    handleMousePressed(row, col);
                }

                repaint();  // Repaint the board after moving the piece
            }
        });

        // Keyboard event to change piece colors using arrow keys and adjust transparency
        this.setFocusable(true);  // Allows the panel to receive focus for keyboard events
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    // Cycle through the light colors (white pieces)
                    currentLightColorIndex = (currentLightColorIndex + 1) % lightColors.length;

                    // Cycle through the dark colors (brown pieces)
                    currentDarkColorIndex = (currentDarkColorIndex + 1) % darkColors.length;

                    // Update the colors of the pieces
                    updatePieceColors();

                    // Repaint the board with new colors
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    // Increase board transparency
                    if (boardAlpha < 1.0f) {
                        boardAlpha = Math.min(boardAlpha + 0.1f, 1.0f);  // Limit to 1.0
                        repaint();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    // Decrease board transparency
                    if (boardAlpha > 0.0f) {
                        boardAlpha = Math.max(boardAlpha - 0.1f, 0.0f);  // Limit to 0.0
                        repaint();
                    }
                }
            }
        });

        this.setPreferredSize(new Dimension(boardSize * tileSize, boardSize * tileSize));  // Set the board size
    }

    // Initializes the board with white and brown pieces
    private void initializeBoard() {
        // White pieces
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < boardSize; col++) {
                if ((row + col) % 2 != 0) {
                    board[row][col] = new Piece(lightColors[0]);  // Start with the first light color
                }
            }
        }
        // Brown pieces
        for (int row = 5; row < 8; row++) {
            for (int col = 0; col < boardSize; col++) {
                if ((row + col) % 2 != 0) {
                    board[row][col] = new Piece(darkColors[0]);  // Start with the first dark color
                }
            }
        }
    }

    // Updates the color of all pieces based on the current color index
    private void updatePieceColors() {
        // White pieces (top of the board)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (board[row][col] != null) {
                    board[row][col].setColor(lightColors[currentLightColorIndex]);
                }
            }
        }
        // Brown pieces (bottom of the board)
        for (int row = 5; row < 8; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (board[row][col] != null) {
                    board[row][col].setColor(darkColors[currentDarkColorIndex]);
                }
            }
        }
    }

    // Draws the board and the pieces
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawCheckersBoard(g);
    }

    // Draws the checkers board and pieces
    private void drawCheckersBoard(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        // Set the transparency of the board
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, boardAlpha));

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if ((row + col) % 2 == 0) {
                    g2.setColor(Color.LIGHT_GRAY);  // Light squares
                } else {
                    g2.setColor(Color.DARK_GRAY);  // Dark squares
                }
                g2.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);

                if (board[row][col] != null) {
                    g2.setColor(board[row][col].getColor());
                    g2.fillOval(col * tileSize + 5, row * tileSize + 5, tileSize - 10, tileSize - 10);  // Draw the pieces
                }
            }
        }

        // Draw a circle around the selected piece
        if (selectedPiece != null) {
            g2.setColor(Color.YELLOW);  // Highlight color
            g2.drawOval(selectedCol * tileSize, selectedRow * tileSize, tileSize, tileSize);  // Selection circle
        }
    }

    // Handles piece movement
    private void handleMousePressed(int row, int col) {
        if (selectedPiece == null) {
            // Select the piece
            if (board[row][col] != null) {
                selectedPiece = board[row][col];
                selectedRow = row;
                selectedCol = col;
            }
        } else {
            // Move the piece to the new position if it's a valid move
            if (isValidMove(row, col)) {
                // Move the piece
                board[row][col] = selectedPiece;
                board[selectedRow][selectedCol] = null;  // Clear the old position
                selectedPiece = null;  // Deselect the piece
                selectedRow = -1;  // Reset selected row
                selectedCol = -1;  // Reset selected column
            }
        }
    }

    // Checks if the move is valid
    private boolean isValidMove(int toRow, int toCol) {
        // Check bounds
        if (toRow < 0 || toRow >= boardSize || toCol < 0 || toCol >= boardSize) {
            return false; // Out of bounds
        }
        // Check if the target square is empty
        return board[toRow][toCol] == null;
    }

    // Inner class to represent the checkers pieces
    private class Piece {
        private Color color;

        public Piece(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }
    }
}
