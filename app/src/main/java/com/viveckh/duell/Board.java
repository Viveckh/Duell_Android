package com.viveckh.duell;

import java.io.Serializable;

/**
 * Created by ZCV0LHB on 10/30/2016.
 */
public class Board implements Serializable{
    // The gameboard Array
    private Square[][] gameBoard = new Square[8][9];

    // Top values of dice at home row
    private int[] startingTopValuesOfDices = { 5, 1, 2, 6, 1, 6, 2, 1, 5 };

    // VARIABLES
    // ATTENTION: These arrays should be private/protected but accessible from elsewhere at the same time
    public Dice[] humans = new Dice[9];
    public Dice[] bots = new Dice[9];

    // DEFAULT CONSTRUCTOR
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

                //Humans Home Row
                if (currentRow == 0) {
                    gameBoard[currentRow][currentCol].SetOccupied(humans[currentCol]);
                    humans[currentCol].SetCoordinates(currentRow, currentCol);
                    humans[currentCol].SetBotControl(false);
                }

                //Bots Home Row
                if (currentRow == 7) {
                    gameBoard[currentRow][currentCol].SetOccupied(bots[currentCol]);
                    bots[currentCol].SetCoordinates(currentRow, currentCol);
                    bots[currentCol].SetBotControl(true);
                }
            }
            System.out.println("\n");
        }
    }

    // Copy Constructor
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
            }
        }
    }

    // Checks if the condition to end the game has been met
    public boolean GameOverConditionMet() {
        //If one of the kings captured
        if (humans[4].IsCaptured() || bots[4].IsCaptured()) {
            return true;
        }

        //If the human key square is occupied by the bots King die
        if (IsSquareOccupied(0, 4)) {
            if (GetSquareResident(0, 4).IsBotOperated()) {
                if (GetSquareResident(0, 4).IsKing()) {
                    return true;
                }
            }
        }

        //If the computer key square is occupied by the human King die
        if (IsSquareOccupied(7, 4)) {
            if (!GetSquareResident(7, 4).IsBotOperated()) {
                if (GetSquareResident(7, 4).IsKing()) {
                    return true;
                }
            }
        }

        //If none of the game over conditions are met
        return false;
    }

    // SELECTORS
    // Checks if a square in the gameboard is occupied with dice
    public boolean IsSquareOccupied(int row, int col) {
        return gameBoard[row][col].IsOccupied();
    }

    // Gets the pointer to the resident die in the square
    public Dice GetSquareResident(int row, int col) {
        return gameBoard[row][col].GetResident();
    }

    // Gets the square in the given co-ordinates of the gameboard
    public Square GetSquareAtLocation(int row, int col) {
        return gameBoard[row][col];
    }

    // Gets the human King Die in the Gameboard
    public Dice GetHumanKing() {
        return humans[4];
    }

    // Gets the bot king Die in the Gameboard
    public Dice GetBotKing() {
        return bots[4];
    }

    // MUTATORS
    // Sets the given square in the gameboard as occupied with the given dice
    public void SetSquareOccupied(int row, int col, Dice dice) {
        gameBoard[row][col].SetOccupied(dice);
    }

    // Sets a square vacant from any dice occupancies
    public void SetSquareVacant(int row, int col) {
        gameBoard[row][col].SetVacant();
    }
}
