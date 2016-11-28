package com.viveckh.duell;

/**
 * Human Class
 * Handles the move requests from a human player by performing input validations and processing them to reflect changes in the game board.
 * Author: Vivek Pandey
 * Last Modified on: 11/27/2016
 */
public class Human extends Player {

    /**
     * DEFAULT CONSTRUCTOR
     */
    public Human() {}

    /**
     * // Validates user's Input, Validates user's move and performs the move as instructed by human player; Overridden for the 'default path = 0' case
     * @param startRow The row coordinate of the dice to move
     * @param startCol The column coordinate of the dice to move
     * @param endRow The destination row in the board
     * @param endCol The destination column in the board
     * @param board The game board in context
     * @return true if the move successful, false otherwise
     */
    public boolean Play(int startRow, int startCol, int endRow, int endCol, Board board) {
        return Play(startRow, startCol, endRow, endCol, board, 0);
    }

    /**
     * Validates user's Input, Validates user's move and performs the move as instructed by human player
     * @param startRow The row coordinate of the dice to move
     * @param startCol The column coordinate of the dice to move
     * @param endRow The destination row in the board
     * @param endCol The destination column in the board
     * @param board The game board in context
     * @param path In case of 90 degree turns, 1 if vertical first, 2 if lateral first
     * @return true if the move is successful, false otherwise
     */
    public boolean Play(int startRow, int startCol, int endRow, int endCol, Board board, int path) {
        printNotifications = true;

        if (IndexOutOfBounds(startRow, startCol, endRow, endCol)) {
            // Log error here
            Notifications.Msg_InputOutOfBounds();
            return false;
        }

        /*
        Commented since the buttons in the GUI are indexed starting from zero
        // Decrementing the input values to match the gameboard internal representation in array
        startRow--;
        startCol--;
        endRow--;
        endCol--;
        */

        // Verify the dice is not bot operated so that the dice belongs to human player
        if (board.GetSquareResident(startRow, startCol) != null) {
            if (!board.GetSquareResident(startRow, startCol).IsBotOperated()) {
                // Checking to see if there is a 90 degree turn
                if (MakeAMove(startRow, startCol, endRow, endCol, board, false, path)) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                Notifications.Msg_WrongDice();
                return false;
            }
        }
        else {
            Notifications.Msg_NoDiceToMove();
            return false;
        }
    }

    /**
     * Checks if any input values are out of bounds, Call before decrementing the input values to match the gameboard array indexes
     * @param startRow The row coordinate of the dice to move
     * @param startCol The column coordinate of the dice to move
     * @param endRow The destination row in the board
     * @param endCol The destination column in the board
     * @return true if any of the coordinates are out of bounds, false otherwise
     */
    public boolean IndexOutOfBounds(int startRow, int startCol, int endRow, int endCol) {
        if (startRow < 0 || startRow >= 8) {
            return true;
        }

        if (startCol < 0 || startCol >= 9) {
            return true;
        }

        if (endRow < 0 || endRow >= 8) {
            return true;
        }

        if (endCol < 0 || endCol >= 9) {
            return true;
        }
        return false;
    }
}
