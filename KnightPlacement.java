import java.util.Random;

public class KnightPlacement {
    private static final int SIZE = 8;
    private static final char EMPTY = '.';
    private static final char KNIGHT = 'N';
    private static final int[][] MOVES = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            {1, -2}, {1, 2}, {2, -1}, {2, 1}
    };

    public static void main(String[] args) {
        char[][] board = new char[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                board[i][j] = EMPTY;

        int placed = 0;
        boolean improved = true;
        Random rand = new Random();

        while (improved) {
            improved = false;
            int bestI = -1, bestJ = -1;
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (board[i][j] == EMPTY && canPlace(board, i, j)) {
                        bestI = i;
                        bestJ = j;
                        improved = true;
                        break;
                    }
                }
                if (improved) break;
            }
            if (improved) {
                board[bestI][bestJ] = KNIGHT;
                placed++;
            }
        }

        printBoard(board);
        System.out.println("Total knights placed: " + placed);
    }

    private static boolean canPlace(char[][] board, int x, int y) {
        for (int[] move : MOVES) {
            int nx = x + move[0], ny = y + move[1];
            if (nx >= 0 && nx < SIZE && ny >= 0 && ny < SIZE && board[nx][ny] == KNIGHT)
                return false;
        }
        return true;
    }

    private static void printBoard(char[][] board) {
        for (char[] row : board) {
            for (char cell : row)
                System.out.print(cell + " ");
            System.out.println();
        }
    }
}