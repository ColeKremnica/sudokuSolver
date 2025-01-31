package code;

public class Cell {
    /* Represents a single square on the Sudoku board.
     * Holds the number (0 if unsolved) and potential values for the cell.
     * Also tracks which 3x3 box the cell belongs to.
     */

    private int number; // Solved value of the cell.
    private boolean[] potential = {false, true, true, true, true, true, true, true, true, true};
    /* Array representing possible values for the cell (1-9), index 0 is unused. */

    private int boxID; // ID of the 3x3 box the cell belongs to.

    // Checks if the given number is a potential value for the cell.
    public boolean canBe(int number){
        return potential[number];
    }

    // Marks the given number as not a potential value for the cell.
    public void cantBe(int number){
        potential[number] = false;
    }

    // Counts the number of possible values for the cell.
    public int numberOfPotentials(){
        int count = 0;
        for(int x = 1; x < 10; x++) {
            if(canBe(x)) {
                count++;
            }
        }
        return count;
    }

    // Returns the first possible number for the cell.
    public int getFirstPotential(){
        for(int x = 1; x < 10; x++) {
            if(canBe(x)) {
                return x;
            }
        }
        return -1;
    }

    // Returns the second possible number for the cell.
    public int getSecondPotential(){
        int count = 0;
        for(int x = 1; x < 10; x++) {
            if(canBe(x)) {
                if(count == 1) return x;
                count++;
            }
        }
        return -1;
    }

    // Getters and setters for the number, potential array, and boxID.
    public int getNumber() {
        return number;
    }

    // Sets the solved number for the cell and marks all other potentials as false.
    public void setNumber(int number) {
        this.number = number;
    }

    public boolean[] getPotential() {
        return potential;
    }

    public boolean getPotential(int numb) {
        return potential[numb];
    }

    public void setPotential(boolean[] potential) {
        this.potential = potential;
    }

    public void setPotential(int numb) {
        potential[numb] = true;
    }

    public int getBoxID() {
        return boxID;
    }

    public void setBoxID(int boxID) {
        this.boxID = boxID;
    }

}
