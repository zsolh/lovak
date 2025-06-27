import java.util.*;

public class KnightPlacementGA {
    private static final int SIZE = 8, POP_SIZE = 100, GENERATIONS = 200;
    private static final double MUTATION_RATE = 0.1;
    private static final int[][] MOVES = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            {1, -2}, {1, 2}, {2, -1}, {2, 1}
    };

    static class Board implements Comparable<Board> {
        int[][] grid = new int[SIZE][SIZE];
        int fitness = -1;

        Board() {}
        Board(Board other) {
            for (int i = 0; i < SIZE; i++)
                this.grid[i] = Arrays.copyOf(other.grid[i], SIZE);
        }

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
            attacks /= 2; // Each attack counted twice
            fitness = knights - attacks * 2; // Penalize attacks heavily
            return fitness;
        }

        @Override
        public int compareTo(Board o) {
            return Integer.compare(o.fitness(), this.fitness());
        }
    }

    public static void main(String[] args) {
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
            System.out.println("Generation " + gen + ":");
            printBoard(best.grid);
            System.out.println("Fitness: " + best.fitness());
            System.out.println("Knights placed: " + countKnights(best.grid));
            System.out.println();

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

        Collections.sort(population);
        Board best = population.get(0);
        printBoard(best.grid);
        System.out.println("Fitness: " + best.fitness());
        System.out.println("Knights placed: " + countKnights(best.grid));
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

    static void printBoard(int[][] board) {
        for (int[] row : board) {
            for (int cell : row)
                System.out.print(cell == 1 ? "N " : ". ");
            System.out.println();
        }
    }

    static int countKnights(int[][] board) {
        int c = 0;
        for (int[] row : board)
            for (int cell : row)
                if (cell == 1) c++;
        return c;
    }
}