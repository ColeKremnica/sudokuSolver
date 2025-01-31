package code;

public class SudokuSolver {
    public static void main(String[] args) throws Exception {

        // Create a new Sudoku board puzzle
        Board puzzle = new Board();

        // Load the puzzle using the "test" difficulty level
        puzzle.loadPuzzle("test");

        // Display the initial puzzle state
        puzzle.display();

        // Perform logic cycles to solve the puzzle
        puzzle.logicCycles();

        // Display the solved puzzle
        puzzle.display();

        // Check if there are any errors in the puzzle
        System.out.println("is error found?");
        System.out.println(puzzle.errorFound());

        // Check if the puzzle is completely solved
        System.out.println("is puzzle solved?");
        System.out.println(puzzle.isSolved());
    }
}
