package Sem2;

import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class Program {
    private static final char DOT_HUMAN = 'X';
    private static final char DOT_AI = '0';
    private static final char DOT_EMPTY = '*';
    private static final int MAX_FIELD_SIZE = 9; // Logic limit, can be changed
    private static final int MAX_FIGURE_TO_WIN_SIZE = 8; // Logic limit, can be changed
    private static final Scanner sc = new Scanner(System.in);
    private static final Random ran = new Random();
    private static char[][] field;
    private static int fieldSizeX;
    private static int fieldSizeY;
    private static int figuresToWin;

    /**
     * The entry point of the program, the main method.
     *
     * @param args This is an arguments
     */
    public static void main(String[] args) {
        System.out.print("Enter field size and figures to win (Space is separator)\n" +
                "Recommended 3 or more for both args : ");
        int size = valParams(MAX_FIELD_SIZE, "size");
        int figureNumber = valParams(MAX_FIGURE_TO_WIN_SIZE, "figures to win");
        while (true) {
            if (size < figureNumber || (size < 3 || figureNumber < 3)) {
                System.out.println("Incorrect parameters!");
                continue;
            }
            initialize(size, figureNumber);
            printField();
            while (true) {
                humanTurn();
                printField();
                if (checkGameState(DOT_HUMAN, "You Win")) {
                    break;
                }
                aiTurn();
                printField();
                if (checkGameState(DOT_AI, "AI Win")) {
                    break;
                }
            }
            System.out.println("Continue?: ");
            if (!sc.next().equalsIgnoreCase("y")) {
                break;
            }
        }
    }

    /**
     * Initialize game objects
     *
     * @param size    of field
     * @param figures to win
     */
    private static void initialize(int size, int figures) {
        fieldSizeX = size;
        fieldSizeY = size;
        figuresToWin = figures;
        field = new char[fieldSizeX][fieldSizeY];
        for (int x = 0; x < fieldSizeX; x++) {
            for (int y = 0; y < fieldSizeY; y++) {
                field[x][y] = DOT_EMPTY;
            }
        }
    }

    /**
     * Field format print
     */
    private static void printField() {
        System.out.print("+");
        for (int x = 0; x < fieldSizeX * 2 + 1; x++) {
            System.out.print((x % 2 == 0) ? "-" : x / 2 + 1);
        }
        System.out.println();
        for (int x = 0; x < fieldSizeX; x++) {
            System.out.print(x + 1 + "|");
            for (int y = 0; y < fieldSizeY; y++) {
                System.out.print(field[x][y] + "|");
            }
            System.out.println();
        }
        for (int x = 0; x < fieldSizeX * 2 + 2; x++) {
            System.out.print("-");
        }
        System.out.println();
    }

    /**
     * Human Turn
     * Player Enters coordinates in console
     * Template: "# #", x,y
     */
    private static void humanTurn() {
        int x, y;
        do {
            System.out.println("Enter coordinates X and Y: ");
            x = valParams(fieldSizeX, "X") - 1;
            y = valParams(fieldSizeY, "Y") - 1;
        } while (!isCellValid(x, y) || !isCellEmpty(x, y));
        field[x][y] = DOT_HUMAN;
    }

    /**
     * Check cell in field for emptiness
     *
     * @param x coordinate
     * @param y coordinate
     * @return bool
     */
    private static boolean isCellEmpty(int x, int y) {
        return field[x][y] == DOT_EMPTY;
    }

    /**
     * Check coordinates for validness
     *
     * @param x coordinate
     * @param y coordinate
     * @return bool
     */
    private static boolean isCellValid(int x, int y) {
        return x >= 0 && x < fieldSizeX && y >= 0 && y < fieldSizeY;
    }

    /**
     * Ai Turn
     * Operation principle: Initially, the AI checks the center; if it's available, it places its mark.
     * Then, it goes through the entire board, placing the opponent's mark and checking if it would win.
     * If placing the opponent's mark results in a victory,
     * it places its own mark there; otherwise, it reverts the mark before further evaluation. Other is a random put's
     */
    private static void aiTurn() {
        int x, y;
        x = fieldSizeX / 2;
        y = fieldSizeY / 2;
        System.out.println();
        if (isCellEmpty(x, y)) {
            field[x][y] = DOT_AI;
            return;
        }
        do {
            for (int i = 0; i < fieldSizeX; i++) {
                for (int j = 0; j < fieldSizeY; j++) {
                    char ch = field[i][j];
                    if (isCellEmpty(i, j)) {
                        field[i][j] = DOT_HUMAN;
                        if (checkWin(DOT_HUMAN)) {
                            field[i][j] = DOT_AI;
                            return;
                        } else field[i][j] = ch;
                    }
                }
            }
            x = ran.nextInt(fieldSizeX);
            y = ran.nextInt(fieldSizeY);
        } while (!isCellEmpty(x, y));
        field[x][y] = DOT_AI;
    }

    /**
     * Check game state
     *
     * @param c player figure
     * @param s string
     * @return bool
     */
    private static boolean checkGameState(char c, String s) {
        if (checkWin(c)) {
            System.out.println(s);
            return true;
        }
        if (checkDraw()) {
            System.out.println("Draw");
            return true;
        }
        return false;
    }

    /**
     * Check for win
     * Operation principle: It goes through the entire field in both vertical and horizontal directions,
     * counting consecutive marks of the 'c' parameter.
     * Then, it traverses the halves of the field in diagonal directions.
     * Work for any field size and figures to win.
     *
     * @param c char figure
     * @return bool
     */

    private static boolean checkWin(char c) {
//        int x, y;
        // Проверка на вертикальный и горизонтальный выигрыш
        int countVer = 0, countHor = 0;
        for (int i = 0; i < fieldSizeX; i++) {
            for (int j = 0; j < fieldSizeY; j++) {
                countVer = (field[i][j] == c) ? countVer + 1 : 0;
                countHor = (field[j][i] == c) ? countHor + 1 : 0;
                if (countVer >= figuresToWin || countHor >= figuresToWin) return true;
            }
            countVer = 0;
            countHor = 0;
        }
        int fieldSize = fieldSizeX; // Унифицирую размер поля

        // проход всех "/" подобных диагоналей
        int countDiag1 = 0, countDiag2 = 0;
        for (int i = 1; i < fieldSize; i++) {
            for (int j = 0, sh = i; j <= i; j++, sh--) {
                countDiag1 = (field[sh][j] == c) ? countDiag1 + 1 : 0;
                if (countDiag1 >= figuresToWin) return true;
            }
            countDiag1 = 0;
        }
        for (int i = 1; i < fieldSize - figuresToWin + 1; i++) {
            for (int j = i, sh = fieldSize - 1; j < fieldSize; j++, sh--) {
                countDiag1 = (field[sh][j] == c) ? countDiag1 + 1 : 0;
                if (countDiag1 >= figuresToWin) return true;
            }
            countDiag1 = 0;
        }

        // проход всех "\" подобных диагоналей
        for (int i = 0; i < fieldSize - figuresToWin + 1; i++) {
            for (int j = i, sh = 0; j < fieldSize; j++, sh++) {
                countDiag2 = (field[sh][j] == c) ? countDiag2 + 1 : 0;
                if (countDiag2 >= figuresToWin) return true;
            }
            countDiag2 = 0;
        }

        for (int i = 1; i < fieldSize - figuresToWin + 1; i++) {
            for (int j = 0, sh = i; j < fieldSize - i; j++, sh++) {
                countDiag2 = (field[sh][j] == c) ? countDiag2 + 1 : 0;
                if (countDiag2 >= figuresToWin) return true;
            }
            countDiag2 = 0;
        }
        return false;
    }

    /**
     * Check for draw
     *
     * @return bool
     */
    private static boolean checkDraw() {
        for (int x = 0; x < fieldSizeX; x++) {
            for (int y = 0; y < fieldSizeY; y++) {
                if (isCellEmpty(x, y)) return false;
            }
        }
        return true;
    }

    /**
     * Validate input from user
     *
     * @param till this parameter is the maximum limit for entering an integer number.
     * @param paramName Name of a parameter.
     * @return - correct Int value.
     */
    private static int valParams(int till, String paramName) {
        System.out.printf("Enter parameter %s: ", paramName);
        while (true) {
            try {
                int num = sc.nextInt();
                if (num > 0 && num <= till) {
                    return num;
                } else {
                    sc.nextLine();
                    sc.reset();
                    System.out.printf("\nEnter correct parameter %s (1 till %d): ", paramName, till);
                }
            } catch (InputMismatchException e) {
                sc.nextLine();
                sc.reset();
                System.out.println("Incorrect input");
                System.out.printf("\nEnter correct parameter for %s:  ", paramName);
            } finally {
                System.out.println();
            }
        }
    }
}
