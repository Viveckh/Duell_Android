package com.viveckh.duell;

import java.io.Serializable;

/**
 * Board Class
 * Initializes and maintains a game board of squares and player dices.
 * It consists of functions to initialize a board, retrieve the contents at different squares within the board, check and set the occupancy of these squares that comprise the game board.
 * Author: Vivek Pandey
 * Last Modified on: 11/29/2016
 */
public class Board implements Serializable{

    //VARIABLES
    // The gameboard multi-dimensional Array
    private Square[][] gameBoard = new Square[8][9];

    // Top values of dice at home row
    private int[] startingTopValuesOfDices = { 5, 1, 2, 6, 1, 6, 2, 1, 5 };

    // Collection of Player dice. These arrays should be private/protected but accessible from elsewhere at the same time
    public Dice[] humans = new Dice[9];
    public Dice[] bots = new Dice[9];

    /**
     * DEFAULT CONSTRUCTOR - Sets up a board with the player dices in their respective spots
     */
    public Board() {

        // Arranging the initial orientation of the dices in the board
        for (int index = 0; index < 9; index++) {

            //initializing all the player dices
            humans[index] = new Dice();
            bots[index] = new Dice();

            //Supplying the top values since the rear value is 3 by default
            if (index == 4) {
                // Setting the kings
                humans[index].SetKing(true);
                bots[index].SetKing(true);
            }
            else {
                humans[index].SetBeginningOrientation(startingTopValuesOfDices[index], false);
                bots[index].SetBeginningOrientation(startingTopValuesOfDices[index], true);
            }
            System.out.println(humans[index].GetTop() + humans[index].GetLeft() + humans[index].GetBottom() + humans[index].GetRight());
        }

        // General board setup with Squares and Dices
        for (int currentRow = 0; currentRow < 8; currentRow++) {
            for (int currentCol = 0; currentCol < 9; currentCol++) {
                gameBoard[currentRow][currentCol] = new Square();
                gameBoard[currentRow][currentCol].SetCoordinates(currentRow, currentCol);

                System.out.println("(" + gameBoard[currentRow][currentCol].GetRow() + ", " + gameBoard[currentRow][currentCol].GetColumn() + ")\t");

                //Setting Humans Home Row
                if (currentRow == 0) {
                    gameBoard[currentRow][currentCol].SetOccupied(humans[currentCol]);
                    humans[currentCol].SetCoordinates(currentRow, currentCol);
                    humans[currentCol].SetBotControl(false);
                }

                //Settings Bots Home Row
                if (currentRow == 7) {
                    gameBoard[currentRow][currentCol].SetOccupied(bots[currentCol]);
                    bots[currentCol].SetCoordinates(currentRow, currentCol);
                    bots[currentCol].SetBotControl(true);
                }
            }
            System.out.println("\n");
        }
    }

    /**
     * COPY CONSTRUCTOR - Sets up a board as a copy of some existing board
     * @param anotherOne The board whose copy is to be made
     */
    public Board(Board anotherOne) {
        // Copying all the player dice
        for (int index = 0; index < 9; index++) {
            humans[index] = new Dice(anotherOne.humans[index]);
            bots[index] = new Dice(anotherOne.bots[index]);
        }

        // Copying the squares in the board
        for (int currentRow = 0; currentRow < 8; currentRow++) {
            for(int currentCol = 0; currentCol < 9; currentCol++) {
                gameBoard[currentRow][currentCol] = new Square(anotherOne.gameBoard[currentRow][currentCol]);

                //NEW ADDITION: TO FIX THE BUG THAT TOOK 7HRS TO DISCOVER
                // Making the resident variable of the given square point to its corresponding dice in the DiceArrays (to maintain the connection)
                if (gameBoard[currentRow][currentCol].GetResident() != null) {
                    for (int indyex = 0; indyex < 9; indyex++) {
                        //If any dice in DiceArrays has the same coordinate as the resident dice in a given square, then we make the square point to this array dice
                        if (!humans[indyex].IsCaptured()) {
                            if ((humans[indyex].GetRow() == gameBoard[currentRow][currentCol].GetResident().GetRow()) && (humans[indyex].GetColumn() == gameBoard[currentRow][currentCol].GetResident().GetColumn())) {
                                gameBoard[currentRow][currentCol].SetOccupied(humans[indyex]);
                                break;
                            }
                        }

                        if (!bots[indyex].IsCaptured()) {
                            if ((bots[indyex].GetRow() == gameBoard[currentRow][currentCol].GetResident().GetRow()) && (bots[indyex].GetColumn() == gameBoard[currentRow][currentCol].GetResident().GetColumn())) {
                                gameBoard[currentRow][currentCol].SetOccupied(bots[indyex]);
                                break;
                            }
                        }
                    }
                }

            }
        }
    }

    /**
     * Checks if the condition to end the game has been met
     * @return true if the condition for the game to be over is met; false otherwise.
     */
    public boolean GameOverConditionMet() {

        //If one of the kings captured, GAME IS OVER
        if (humans[4].IsCaptured() || bots[4].IsCaptured()) {
            System.out.println("in here");
            return true;
        }

        //If the human key square is occupied by the bots King die, GAME IS OVER
        if (IsSquareOccupied(0, 4)) {
            if (GetSquareResident(0, 4).IsBotOperated()) {
                if (GetSquareResident(0, 4).IsKing()) {
                    return true;
                }
            }
        }

        //If the computer key square is occupied by the human King die, GAME IS OVER
        if (IsSquareOccupied(7, 4)) {
            if (!GetSquareResident(7, 4).IsBotOperated()) {
                if (GetSquareResident(7, 4).IsKing()) {
                    return true;
                }
            }
        }

        //If none of the game over conditions are met, game isn't over yet.
        return false;
    }

    /**
     * Prints out the indexes of player dice that are still active on the gameboard; used for testing purposes
     */
    public void ViewNonCapturedDice() {
        System.out.print("Non Captured Human indexes: ");
        for (int i = 0; i < 9; i++) {
            if (!humans[i].IsCaptured()) {
                System.out.println(i + ": " + humans[i].GetRow() + " " + humans[i].GetColumn());
                if (gameBoard[humans[i].GetRow()][humans[i].GetColumn()].MatchingResident(humans[i])) { System.out.println("Resident Match"); }
            }
        }

        System.out.print("Non Captured Bot indexes: ");
        for (int j = 0; j < 9; j++) {
            if (!bots[j].IsCaptured()) {
                System.out.println(j + ": " + bots[j].GetRow() + " " + bots[j].GetColumn());
                if (gameBoard[bots[j].GetRow()][bots[j].GetColumn()].MatchingResident(bots[j])) { System.out.println("Resident Match"); }
            }
        }
        System.out.println();
    }

    // SELECTORS

    /**
     * Checks if a square in the gameboard is occupied with dice
     * @param row The row coordinate of the square to be checked
     * @param col The column coordinate of the square to be checked
     * @return true if the square is occupied; false otherwise
     */
    public boolean IsSquareOccupied(int row, int col) {
        return gameBoard[row][col].IsOccupied();
    }

    /**
     * Gets a copy of the resident die in the square
     * @param row The row coordinate of the square whose resident is to be extracted
     * @param col The column coordinate of the square whose resident is to be extracted
     * @return A copy of the dice in the given square
     */
    public Dice GetSquareResident(int row, int col) {
        return gameBoard[row][col].GetResident();
    }

    /**
     * Gets a copy of the square in the given co-ordinate of the Board
     * @param row The row coordinate of the square in the game board
     * @param col The column coordinate of the square in the game board
     * @return A copy of the square at the given location in game board
     */
    public Square GetSquareAtLocation(int row, int col) {
        return gameBoard[row][col];
    }

    /**
     * Gets the human King Die in the Board
     * @return A copy of the Human King Dice
     */
    public Dice GetHumanKing() {
        return humans[4];
    }

    /**
     * Gets the bot king Die in the Board
     * @return A copy of the Bot King Dice
     */
    public Dice GetBotKing() {
        return bots[4];
    }

    // MUTATORS

    /**
     * Sets the given square in the Board as occupied with the given dice
     * @param row The row coordinate of the square in the Board
     * @param col The column coordinate of the square in the Board
     * @param dice The dice that needs to occupy the given square
     */
    public void SetSquareOccupied(int row, int col, Dice dice) {
        gameBoard[row][col].SetOccupied(dice);
    }

    /**
     * Sets a square vacant from any dice occupancies
     * @param row The row coordinate of the square in the Board
     * @param col The column coordinate of the square in the Board
     */
    public void SetSquareVacant(int row, int col) {
        gameBoard[row][col].SetVacant();
    }

    /**
     * Sets the resident of a square at the given coordinate to be captured/uncaptured
     * @param row The row coordinate of the square in the Board
     * @param col The column coordinate of the square in the Board
     * @param value True or False value stating whether the resident dice is to be captured or not
     */
    public void SetSquareResidentCaptured(int row, int col, boolean value) {
        gameBoard[row][col].SetResidentCaptured(value);
    }
}
