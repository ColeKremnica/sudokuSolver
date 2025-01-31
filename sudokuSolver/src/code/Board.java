package code;

import java.io.File;
import java.util.Scanner;

public class Board {

    /*The Sudoku Board is made of 9x9 cells for a total of 81 cells.
     * In this program we will be representing the Board using a 2D Array of cells.
     */

    private Cell[][] Board = new Cell[9][9];  // 2D array to represent the 9x9 Sudoku board
    private String level = ""; // Tracks the difficulty level of the puzzle (easy, medium, hard)
    private Cell[][][] stack = new Cell[81][9][9];  // Used for tracking different board states

    // Initializes every cell on the board and assigns the boxID for each cell
    public Board() {
        for(int x = 0; x < 9; x++) {
            for(int y = 0 ; y < 9; y++) {
                Board[x][y] = new Cell();  // Initialize a new Cell for each position
                // Assign a unique boxID to each cell based on its position
                Board[x][y].setBoxID(3 * (x / 3) + (y) / 3 + 1);
            }
        }
    }

    /* This method takes a string parameter representing the puzzle difficulty (easy, medium, hard).
     * Based on the level, it selects the appropriate file to load the puzzle's numbers and sets them.
     */
    public void loadPuzzle(String level) throws Exception {
        this.level = level;  // Set the puzzle's level
        String fileName = "easyPuzzle.txt";  // Default file for easy puzzle
        // Change the file name based on the puzzle difficulty
        if(level.contentEquals("medium")) fileName = "mediumPuzzle.txt";
        else if(level.contentEquals("hard")) fileName = "hardPuzzle.txt";
        else if(level.contentEquals("test")) fileName = "testPuzzle.txt";

        // Read the puzzle from the file
        Scanner input = new Scanner(new File(fileName));

        for(int x = 0; x < 9; x++) {  // Loop through each row
            for(int y = 0; y < 9; y++) {  // Loop through each column
                int number = input.nextInt();  // Get the number from the file
                if(number != 0) {
                    solve(x, y, number);  // If the number is not zero, solve the cell
                }
            }
        }

        input.close();  // Close the file scanner
    }

    /* Returns TRUE if every cell has been solved, else FALSE */
    public boolean isSolved() {
        // Check if any cell is unsolved (has number 0)
        for(int x = 0; x < 9; x++) {
            for(int y = 0; y < 9; y++) {
                if(Board[x][y].getNumber() == 0)
                    return false;
            }
        }
        return true;  // Puzzle is solved if no 0s are found
    }

    /* Displays the board with borders around boxes */
    public void display() {
        System.out.println("+--------------------+");
        // Loop through and display the board
        for(int x = 0; x < 9; x++) {
            for(int y = 0; y < 9; y++) {
                if(y % 3 == 0)  // Vertical divider
                    System.out.print("|");
                System.out.print(Board[x][y].getNumber() + " ");  // Display cell number
            }
            System.out.print("|");
            System.out.println();
            if(x % 3 == 2) {  // Horizontal divider after every 3 rows
                System.out.println("+--------------------+");
            }
        }
    }

    // Overloaded display method for custom 2D Cell array
    public void display(Cell temp[][]) {
        System.out.println("+--------------------+");
        for(int x = 0; x < 9; x++) {
            for(int y = 0; y < 9; y++) {
                if(y % 3 == 0)
                    System.out.print("|");
                System.out.print(temp[x][y].getNumber() + " ");
            }
            System.out.print("|");
            System.out.println();
            if(x % 3 == 2) {
                System.out.println("+--------------------+");
            }
        }
    }


    /* Solves a specific cell at (x, y) with the given number and adjusts constraints for others */
    public void solve(int x, int y, int number) {
        // Mark number as impossible in the same column and row
        for(int z = 0; z < 9; z++) {
            if(Board[z][y] != Board[x][y]) Board[z][y].cantBe(number);
            if(Board[x][z] != Board[x][y]) Board[x][z].cantBe(number);
        }

        // Find the 3x3 box and mark the number as impossible in other cells of that box
        int position = Board[x][y].getBoxID();
        int row = 0, colum = 0;
        boolean found = false;
        int boxPlace = 0, boxDepth = 0;

        while(row < 9 && !found) {
            colum = 0;
            while(colum < 9 && !found) {
                if(position == Board[row][colum].getBoxID()) {
                    boxPlace = row;
                    boxDepth = colum;
                    found = true;
                }
                colum += 3;
            }
            row += 3;
        }

        // Mark number as impossible in the 3x3 box cells
        for(int z = boxPlace; z < boxPlace + 3; z++) {
            for(int a = boxDepth; a < boxDepth + 3; a++) {
                if(Board[z][a] != Board[x][y]) Board[z][a].cantBe(number);
            }
        }

        // Eliminate all numbers except the one being solved for in the current cell
        for(int z = 1; z < 10; z++) {
            if(z != number) Board[x][y].cantBe(z);
        }

        // Set the number in the current cell
        Board[x][y].setNumber(number);
    }



    // logicCycles continuously applies logic algorithms until no more changes are made
    public void logicCycles() throws Exception {
        int count = 0;
        while (!isSolved()) {
            int changesMade = 0;
            do {
                changesMade = 0;
                changesMade += logic1();
                changesMade += logic2();
                changesMade += logic3();
                changesMade += logic4();

                if (errorFound()) {
                    count--;
                    reSet(stack[count]);
                    break;
                }
            } while (changesMade != 0);

            stack[count] = Board;  // Save current state for guessing
            guess(stack[count]);   // Make a guess if needed
            count++;
        }
    }

    /// logic1 solves cells with only one potential number
    public int logic1() {
        int changesMade = 0;
        for (int row = 0; row < Board.length; row++) {
            for (int col = 0; col < Board.length; col++) {
                if (Board[row][col].numberOfPotentials() == 1 && Board[row][col].getNumber() == 0) {
                    solve(row, col, Board[row][col].getFirstPotential());
                    changesMade++;
                }
            }
        }
        return changesMade;
    }

    /// logic2 solves cells that are the only possible place for a number in their row or column
    public int logic2() {
        int changesMade = 0;
        int numberOfOnePot = 0;
        int place = 0;
        for (int z = 1; z < 10; z++) {
            // Row checks
            for (int x = 0; x < 9; x++) {
                numberOfOnePot = 0;
                place = 0;
                for (int y = 0; y < 9; y++) {
                    if (Board[x][y].canBe(z)) {
                        numberOfOnePot++;
                        place = y;
                    }
                }
                if (numberOfOnePot == 1 && Board[x][place].getNumber() == 0) {
                    solve(x, place, z);
                    changesMade++;
                }
            }
            // Column checks
            for (int y = 0; y < 9; y++) {
                numberOfOnePot = 0;
                place = 0;
                for (int x = 0; x < 9; x++) {
                    if (Board[x][y].canBe(z)) {
                        numberOfOnePot++;
                        place = x;
                    }
                }
                if (numberOfOnePot == 1 && Board[place][y].getNumber() == 0) {
                    solve(place, y, z);
                    changesMade++;
                }
            }
        }
        return changesMade;
    }

    /// logic3 solves cells that are the only possible place for a number in their 3x3 box
    public int logic3() {
        int changesMade = 0;
        int xSave = 0, ySave = 0;
        int numberOfOnePot = 0;
        for (int boxWidth = 0; boxWidth < 9; boxWidth += 3) {
            for (int boxDepth = 0; boxDepth < 9; boxDepth += 3) {
                for (int possible = 1; possible < 10; possible++) {
                    numberOfOnePot = 0;
                    xSave = 0;
                    ySave = 0;
                    for (int row = boxWidth; row < boxWidth + 3; row++) {
                        for (int col = boxDepth; col < boxDepth + 3; col++) {
                            if (Board[row][col].canBe(possible)) {
                                numberOfOnePot++;
                                xSave = row;
                                ySave = col;
                            }
                        }
                    }
                    if (numberOfOnePot == 1 && Board[xSave][ySave].getNumber() == 0) {
                        solve(xSave, ySave, possible);
                        changesMade++;
                    }
                }
            }
        }
        return changesMade;
    }

    /// logic4 solves a pair of cells that have the same two possible numbers in a row or column
    public int logic4() {
        int changesMade = 0;
        int firstPot = 0, secondPot = 0;

        // Row checks
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                if (Board[x][y].numberOfPotentials() == 2) {
                    firstPot = Board[x][y].getFirstPotential();
                    secondPot = Board[x][y].getSecondPotential();
                    for (int colum = y + 1; colum < 9; colum++) {
                        if (Board[x][colum].numberOfPotentials() == 2) {
                            if (Board[x][colum].getFirstPotential() == firstPot && Board[x][colum].getSecondPotential() == secondPot) {
                                for (int i = 0; i < 9; i++) {
                                    if (i == y || i == colum) continue;
                                    if (Board[x][colum].getPotential(firstPot) || Board[x][colum].getPotential(secondPot)) {
                                        Board[x][i].cantBe(firstPot);
                                        Board[x][i].cantBe(secondPot);
                                        changesMade++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Column checks
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (Board[x][y].numberOfPotentials() == 2) {
                    firstPot = Board[x][y].getFirstPotential();
                    secondPot = Board[x][y].getSecondPotential();
                    for (int row = x + 1; row < 9; row++) {
                        if (Board[row][y].numberOfPotentials() == 2) {
                            if (Board[row][y].getFirstPotential() == firstPot && Board[row][y].getSecondPotential() == secondPot) {
                                for (int i = 0; i < 9; i++) {
                                    if (i == x || i == row) continue;
                                    if (Board[i][y].getPotential(firstPot) || Board[i][y].getPotential(secondPot)) {
                                        Board[i][y].cantBe(firstPot);
                                        Board[i][y].cantBe(secondPot);
                                        changesMade++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return changesMade;
    }

    /// errorFound detects logical errors by checking if any cell has no potentials
    public boolean errorFound() {
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                if (Board[x][y].numberOfPotentials() == 0) return true;
            }
        }
        return false;
    }

    // Make a guess on the cell with the lowest number of potentials
    public void guess(Cell[][] copy) {
        int lowestPot = 100;
        int xSave = 0, ySave = 0;
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                if (copy[x][y].numberOfPotentials() < lowestPot && copy[x][y].getNumber() == 0) {
                    lowestPot = copy[x][y].numberOfPotentials();
                    xSave = x;
                    ySave = y;
                }
            }
        }
        int guess = copy[xSave][ySave].getFirstPotential();
        solve(xSave, ySave, guess);
    }

    // Reset a guessed cell if it leads to an error
    public void reSet(Cell[][] copy) {
        int lowestPot = 100;
        int xSave = 0, ySave = 0;
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                if (copy[x][y].numberOfPotentials() < lowestPot && copy[x][y].getNumber() == 0) {
                    lowestPot = copy[x][y].numberOfPotentials();
                    xSave = x;
                    ySave = y;
                }
            }
        }
        int guess = copy[xSave][ySave].getFirstPotential();
        copy[xSave][ySave].cantBe(guess);
    }

}
