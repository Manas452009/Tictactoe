import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class testt {
    int boardWidth = 600;
    int boardHeight = 700; // Adjusted for extra space

    JFrame frame = new JFrame("TicTacToe");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JButton restart = new JButton("Restart");
    JPanel restartPanel = new JPanel();

    JButton[][] board = new JButton[3][3];
    String player = "X"; // AI's player
    String opponent = "O"; // Human player
    String currentPlayer = opponent;

    boolean gameOver = false;
    int turn = 0;

    testt() {
        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setBackground(Color.darkGray);
        textLabel.setForeground(Color.white);
        textLabel.setFont(new Font("Arial", Font.BOLD, 50));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("TIC-TAC-TOE");
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(3, 3));
        boardPanel.setBackground(Color.darkGray);
        frame.add(boardPanel);

        restart.setFont(new Font("Arial", Font.BOLD, 20));
        restart.setBackground(Color.black);
        restart.setForeground(Color.WHITE);
        restart.setFocusPainted(false);
        restartPanel.setLayout(new BorderLayout());
        restartPanel.add(restart, BorderLayout.CENTER);
        frame.add(restartPanel, BorderLayout.SOUTH);

        restart.addActionListener(e -> resetGame());

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                JButton tile = new JButton();
                board[r][c] = tile;
                boardPanel.add(tile);

                tile.setBackground(Color.darkGray);
                tile.setForeground(Color.white);
                tile.setFont(new Font("Arial", Font.BOLD, 120));
                tile.setFocusable(false);

                tile.addActionListener(e -> {
                    if (gameOver) return;
                    JButton clickedTile = (JButton) e.getSource();
                    if (clickedTile.getText().equals("") && currentPlayer.equals(opponent)) {
                        clickedTile.setText(opponent);
                        turn++;
                        checkWinner();
                        if (!gameOver) {
                            currentPlayer = player; // Switch to AI
                            textLabel.setText(currentPlayer + "'s turn.");
                            aiMove();
                        }
                    }
                });
            }
        }
        currentPlayer = player;
    aiMove();
    }

    void aiMove() {
        int bestMove = findBestMove(convertBoardToCharArray());
        board[bestMove / 3][bestMove % 3].setText(player);
        turn++;
        checkWinner();
        if (!gameOver) {
            currentPlayer = opponent;
            textLabel.setText(currentPlayer + "'s turn.");
        }
    }

    char[] convertBoardToCharArray() {
        char[] charBoard = new char[9];
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                String text = board[r][c].getText();
                charBoard[r * 3 + c] = text.equals(player) ? 'X' : text.equals(opponent) ? 'O' : ' ';
            }
        }
        return charBoard;
    }

    boolean isWinner(char[] charBoard, char player) {
        for (int i = 0; i < 3; i++) {
            if (charBoard[i * 3] == player && charBoard[i * 3 + 1] == player && charBoard[i * 3 + 2] == player){
                return true;}
            if (charBoard[i] == player && charBoard[i + 3] == player && charBoard[i + 6] == player){
                return true;}
        }
        return (charBoard[0] == player && charBoard[4] == player && charBoard[8] == player) ||
               (charBoard[2] == player && charBoard[4] == player && charBoard[6] == player);
    }

    boolean isMovesLeft(char[] charBoard) {
        for (char c : charBoard) {
            if (c == ' ') return true;
        }
        return false;
    }

    int minMax(char[] board, int depth, boolean isMaximizing) {
        if (isWinner(board, 'X')) return 10 - depth;
        if (isWinner(board, 'O')) return depth - 10;
        if (!isMovesLeft(board)) return 0;

        if (isMaximizing) {
            int best = Integer.MIN_VALUE;
            for (int i = 0; i < 9; i++) {
                if (board[i] == ' ') {
                    board[i] = 'X';
                    best = Math.max(best, minMax(board, depth + 1, false));
                    board[i] = ' ';
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int i = 0; i < 9; i++) {
                if (board[i] == ' ') {
                    board[i] = 'O';
                    best = Math.min(best, minMax(board, depth + 1, true));
                    board[i] = ' ';
                }
            }
            return best;
        }
    }

    int findBestMove(char[] board) {
        int bestVal = Integer.MIN_VALUE;
        int bestMove = -1;
        for (int i = 0; i < 9; i++) {
            if (board[i] == ' ') {
                board[i] = 'X';
                int moveVal = minMax(board, 0, false);
                board[i] = ' ';
                if (moveVal > bestVal) {
                    bestMove = i;
                    bestVal = moveVal;
                }
            }
        }
        return bestMove;
    }

    void checkWinner() {
        char[] charBoard = convertBoardToCharArray();
        
        // Check for horizontal wins
        for (int i = 0; i < 3; i++) {
            if (charBoard[i * 3] != ' ' && charBoard[i * 3] == charBoard[i * 3 + 1] && charBoard[i * 3] == charBoard[i * 3 + 2]) {
                highlightWinningTiles(i * 3, i * 3 + 1, i * 3 + 2);
                gameOver = true;
                textLabel.setText((charBoard[i * 3] == 'X' ? "AI" : "You") + " win!");
                return;
            }
        }
    
        // Check for vertical wins
        for (int i = 0; i < 3; i++) {
            if (charBoard[i] != ' ' && charBoard[i] == charBoard[i + 3] && charBoard[i] == charBoard[i + 6]) {
                highlightWinningTiles(i, i + 3, i + 6);
                gameOver = true;
                textLabel.setText((charBoard[i] == 'X' ? "AI" : "You") + " win!");
                return;
            }
        }
    
        // Check for diagonal wins
        if (charBoard[0] != ' ' && charBoard[0] == charBoard[4] && charBoard[0] == charBoard[8]) {
            highlightWinningTiles(0, 4, 8);
            gameOver = true;
            textLabel.setText((charBoard[0] == 'X' ? "AI" : "You") + " win!");
            return;
        }
        if (charBoard[2] != ' ' && charBoard[2] == charBoard[4] && charBoard[2] == charBoard[6]) {
            highlightWinningTiles(2, 4, 6);
            gameOver = true;
            textLabel.setText((charBoard[2] == 'X' ? "AI" : "You") + " win!");
            return;
        }
    
        // Check for a draw
        if (turn == 9) {
            gameOver = true;
            for(int i=0;i<3;i++){
                for(int j=0;j<3;j++){
                    board[i][j].setBackground(Color.gray);
                }
            }
            textLabel.setText("It's a tie!");
            
        }
    }
    
    void highlightWinningTiles(int pos1, int pos2, int pos3) {
        board[pos1 / 3][pos1 % 3].setBackground(Color.green);
        board[pos2 / 3][pos2 % 3].setBackground(Color.green);
        board[pos3 / 3][pos3 % 3].setBackground(Color.green);
    }
    
    void setWinner(JButton tile){
        tile.setForeground(Color.green);
        tile.setBackground(Color.gray);
        textLabel.setText(currentPlayer+"is the winner!");
    }

    void resetGame() {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                board[r][c].setText("");
                board[r][c].setForeground(Color.white);
                board[r][c].setBackground(Color.darkGray);
            }
        }
        currentPlayer = player;
        
        textLabel.setText("TIC-TAC-TOE");
        turn = 0;
        gameOver = false;
        aiMove();
    }

    public static void main(String[] args) {
        new testt();
    }
}
