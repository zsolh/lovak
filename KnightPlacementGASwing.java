import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class KnightPlacementGASwing {
    private static final int SIZE = 8, POP_SIZE = 100, GENERATIONS = 200;
    private static final double MUTATION_RATE = 0.1;
    private static final int CELL_SIZE = 50;
    private static final int[][] MOVES = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            {1, -2}, {1, 2}, {2, -1}, {2, 1}
    };

    private final List<int[][]> bestBoards = new ArrayList<>();
    private int currentGen = 0;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(KnightPlacementGASwing::new);
    }

    public KnightPlacementGASwing() {
        JFrame frame = new JFrame("Knight Placement Genetic Algorithm (Swing)");
        BoardPanel boardPanel = new BoardPanel();
        JButton nextBtn = new JButton("Next Generation");

        nextBtn.addActionListener(e -> {
            if (currentGen < bestBoards.size()) {
                boardPanel.setBoard(bestBoards.get(currentGen));
                nextBtn.setText("Next Generation (" + (currentGen + 1) + "/" + bestBoards.size() + ")");
                currentGen++;
            }
        });

        frame.setLayout(new BorderLayout());
        frame.add(boardPanel, BorderLayout.CENTER);
        frame.add(nextBtn, BorderLayout.SOUTH);
        frame.setSize(CELL_SIZE * SIZE + 20, CELL_SIZE * SIZE + 80);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        new Thread(() -> runGA(boardPanel, nextBtn)).start();
    }

    private void runGA(BoardPanel boardPanel, JButton nextBtn) {
        Random rand = new Random();
        List<Board> population = new ArrayList<>();
        for (int i = 0; i < POP_SIZE; i++) {
            Board b = new Board();
            b.randomize(rand);
            population.add(b);
        }

        for (int gen = 0; gen < GENERATIONS; gen++) {
            Collections.sort(population);
            Board best = population.get(0);
            int[][] snapshot = new int[SIZE][SIZE];
            for (int i = 0; i < SIZE; i++)
                snapshot[i] = Arrays.copyOf(best.grid[i], SIZE);
            bestBoards.add(snapshot);

            // Keep top 20%
            population = new ArrayList<>(population.subList(0, POP_SIZE / 5));
            // Refill population
            while (population.size() < POP_SIZE) {
                Board parent1 = population.get(rand.nextInt(POP_SIZE / 5));
                Board parent2 = population.get(rand.nextInt(POP_SIZE / 5));
                Board child = crossover(parent1, parent2, rand);
                mutate(child, rand);
                population.add(child);
            }
        }
        SwingUtilities.invokeLater(() -> {
            boardPanel.setBoard(bestBoards.get(0));
            nextBtn.setText("Next Generation (1/" + bestBoards.size() + ")");
            currentGen = 1;
        });
    }

    static class Board implements Comparable<Board> {
        int[][] grid = new int[SIZE][SIZE];
        int fitness = -1;

        void randomize(Random rand) {
            for (int i = 0; i < SIZE; i++)
                for (int j = 0; j < SIZE; j++)
                    grid[i][j] = rand.nextBoolean() ? 1 : 0;
        }

        int fitness() {
            if (fitness != -1) return fitness;
            int knights = 0, attacks = 0;
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (grid[i][j] == 1) {
                        knights++;
                        for (int[] m : MOVES) {
                            int ni = i + m[0], nj = j + m[1];
                            if (ni >= 0 && ni < SIZE && nj >= 0 && nj < SIZE && grid[ni][nj] == 1)
                                attacks++;
                        }
                    }
                }
            }
            attacks /= 2;
            fitness = knights - attacks * 2;
            return fitness;
        }

        @Override
        public int compareTo(Board o) {
            return Integer.compare(o.fitness(), this.fitness());
        }
    }

    static Board crossover(Board a, Board b, Random rand) {
        Board child = new Board();
        for (int i = 0; i < SIZE; i++)
            child.grid[i] = rand.nextBoolean() ? Arrays.copyOf(a.grid[i], SIZE) : Arrays.copyOf(b.grid[i], SIZE);
        return child;
    }

    static void mutate(Board b, Random rand) {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                if (rand.nextDouble() < MUTATION_RATE)
                    b.grid[i][j] = 1 - b.grid[i][j];
        b.fitness = -1;
    }

    static class BoardPanel extends JPanel {
        private int[][] board = new int[SIZE][SIZE];

        void setBoard(int[][] board) {
            for (int i = 0; i < SIZE; i++)
                this.board[i] = Arrays.copyOf(board[i], SIZE);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if ((i + j) % 2 == 0)
                        g.setColor(new Color(245, 222, 179));
                    else
                        g.setColor(new Color(139, 69, 19));
                    g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                    if (board[i][j] == 1) {
                        g.setColor(Color.RED);
                        g.fillOval(j * CELL_SIZE + 10, i * CELL_SIZE + 10, CELL_SIZE - 20, CELL_SIZE - 20);
                    }
                }
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(CELL_SIZE * SIZE, CELL_SIZE * SIZE);
        }
    }
}