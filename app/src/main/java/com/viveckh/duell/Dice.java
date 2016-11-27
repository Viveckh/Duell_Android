package com.viveckh.duell;

import java.io.Serializable;

/**
 * Dice Class
 * Implements the properties of a dice object used in the game.
 * The class consists of variables and functions to store and modify a dice’s face values, its coordinates within a game board, its controller (human or computer), its capture status, and whether it is a king.
 * Author: Vivek Pandey
 * Last Modified on: 11/27/2016
 */
public class Dice implements Serializable{
    //CONSTANTS & VARIABLES DECLARATIONS
    final int SUM_OF_OPPOSITE_SIDES = 7;

    // Variables to dice location & properties
    private int row;
    private int column;
    private boolean king;
    private boolean botOperated;
    private boolean captured;

    //Variables to store the values of different faces
    private int top;
    private int bottom;
    private int front;
    private int rear;
    private int left;
    private int right;

    // Arrays with the counter clockwise order of the face values when viewed from different angles
    private int[] counterClockwiseDiceOrder1 = { 1, 2, 6, 5, 1, 2, 6, 5 };
    private int[] counterClockwiseDiceOrder2 = { 3, 1, 4, 6, 3, 1, 4, 6 };
    private int[] counterClockwiseDiceOrder3 = { 2, 3, 5, 4, 2, 3, 5, 4 };

    /**
     * DEFAULT CONSTRUCTOR
     */
    public Dice() {
        row = 0;
        column = 0;
        top = 1;
        left = 2;
        rear = 3;
        bottom = SUM_OF_OPPOSITE_SIDES - top;
        right = SUM_OF_OPPOSITE_SIDES - left;
        front = SUM_OF_OPPOSITE_SIDES - rear;
        king = false;
        botOperated = false;
        captured = false;
    }

    /**
     * CONSTRUCTOR
     * @param row The row coordinate of the dice in a board context, 0 by default
     * @param column The column coordinate of the dice in a board context, 0 by default
     * @param king true if the dice is a king, false otherwise
     * @param botOperated true if the dice is bot operated, false otherwise
     */
    public Dice(int row, int column, boolean king, boolean botOperated) {
        this();     // Calling default constructor
        this.row = row;
        this.column = column;
        this.botOperated = botOperated;
        SetKing(king);
    }

    /**
     * COPY CONSTRUCTOR - Initializes a dice object as a copy of an existing dice
     * @param anotherOne The dice whose copy is to be made
     */
    public Dice(Dice anotherOne) {
        //System.out.println("anotherone.row : " + anotherOne.row + "" + anotherOne.column + " " + anotherOne.top + "" + anotherOne.left + anotherOne.botOperated + anotherOne.king);
        this.row = anotherOne.row;
        this.column = anotherOne.column;
        this.top = anotherOne.top;
        this.bottom = anotherOne.bottom;
        this.front = anotherOne.front;
        this.rear = anotherOne.rear;
        this.left = anotherOne.left;
        this.right = anotherOne.right;
        this.king = anotherOne.king;
        this.botOperated = anotherOne.botOperated;
        this.captured = anotherOne.captured;
    }

    // ACCESSORS

    // Getters for the coordinates
    public int GetRow() { return row; }
    public int GetColumn() { return column; }

    // Getters for the values of different faces of the dice
    public int GetTop() { return top; }
    public int GetBottom() { return bottom; }
    public int GetFront() { return front; }
    public int GetRear() { return rear; }
    public int GetLeft() { return left; }
    public int GetRight() { return right; }

    /**
     * Returns true if the dice is Bot Operated
     * @return true if the dice is bot operated, false otherwise
     */
    public boolean IsBotOperated() {
        return botOperated;
    }

    /**
     * Returns true if the dice is a king
     * @return true if the dice is a king; false otherwise
     */
    public boolean IsKing() {
        return king;
    }

    /**
     * Returns true if the dice is captured
     * @return Returns true if the dice is captured, false otherwise
     */
    public boolean IsCaptured() {
        return captured;
    }

    // MUTATORS

    /**
     * Calculates & Sets the beginning orientation with face values of a dice in the home row
     * @param top the top value of the dice
     * @param isBot true if the dice is bot Operated, false otherwise
     */
    public void SetBeginningOrientation(int top, boolean isBot) {
        //Values given in program specs
        this.top = top;
        bottom = SUM_OF_OPPOSITE_SIDES - top;

        //Since the dies are arranged facing each other in two opposite directions
        if (isBot) {
            front = 3;
            rear = SUM_OF_OPPOSITE_SIDES - front;
        }
        else {
            rear = 3;	// for human
            front = SUM_OF_OPPOSITE_SIDES - rear;
        }

        /*	Since the given dice is oriented in a counterclockwise direction and 3 is always facing the rear,
        the top-left-bottom-right values are always in some 1-2-6-5 order pattern.
        This means, for a human, the left value is always next to the top value in the given 1-2-6-5 order.
        For a bot, the reverse is true, i.e. right value is always next to the top value in the 1-2-6-5 order.
        */

        // Searching through the counter clockwise dice pattern to find the top value
        for (int index = 0; index < 4; index++) {
            if (counterClockwiseDiceOrder1[index] == top) {
                // If index has the top value, index+1 will have the left value for human, and right value for a bot
                if (index <= 2) {
                    if (isBot) {
                        right = counterClockwiseDiceOrder1[index + 1];
                        left = SUM_OF_OPPOSITE_SIDES - right;
                    }
                    else {
                        left = counterClockwiseDiceOrder1[index + 1];
                        right = SUM_OF_OPPOSITE_SIDES - left;
                    }
                }
                else {
                    // If the last index has the matching top value, then the first index would have the left value for human, and right value for a bot
                    if (isBot) {
                        right = counterClockwiseDiceOrder1[0];
                        left = SUM_OF_OPPOSITE_SIDES - right;
                    }
                    else {
                        left = counterClockwiseDiceOrder1[0];
                        right = SUM_OF_OPPOSITE_SIDES - left;
                    }
                }
                break;
            }
        }
    }

    /**
     * Calculates & Sets the remaining sides of a dice given the Top-left or Top-Right sides
     * @param arg1 the top value of the dice
     * @param arg2 Can be left (for computer) or right (for human)
     */
    public void SetRemainingSides(int arg1, int arg2) {
        // The first parameter is the top, the second one can either be left (for computer) or right (for human)
        bottom = SUM_OF_OPPOSITE_SIDES - arg1;

        // for computer the given arg2 is left whereas for human the given arg2 is right value
        if (IsBotOperated()) {
            right = SUM_OF_OPPOSITE_SIDES - arg2;
        }
        else {
            left = SUM_OF_OPPOSITE_SIDES - arg2;
        }

        front = rear = 0;	//resetting before calculating new one

        // From this point, it doesn't matter whether it is human or computer die because both are saved in the model in the same way
        // above we had to be careful to meet our model's specs because the computer's right given in the serialization file is actually left in our overall board model
        for (int index = 0; index < 7; index++) {
            if (counterClockwiseDiceOrder1[index] == top && counterClockwiseDiceOrder1[index + 1] == right) {
                // take higher out of the remaining two not in the array as the rear value
                rear = 4;
                front = SUM_OF_OPPOSITE_SIDES - rear;
                break;
            }

            if (counterClockwiseDiceOrder1[index] == right && counterClockwiseDiceOrder1[index + 1] == top) {
                // take lower out of the remaining two not in the array as the rear value
                rear = 3;
                front = SUM_OF_OPPOSITE_SIDES - rear;
                break;
            }

            if (counterClockwiseDiceOrder2[index] == top && counterClockwiseDiceOrder2[index + 1] == right) {
                // take higher out of the remaining two not in the array as the rear value
                rear = 5;
                front = SUM_OF_OPPOSITE_SIDES - rear;
                break;
            }

            if (counterClockwiseDiceOrder2[index] == right && counterClockwiseDiceOrder2[index + 1] == top) {
                // take lower out of the remaining two not in the array as the rear value
                rear = 2;
                front = SUM_OF_OPPOSITE_SIDES - rear;
                break;
            }

            if (counterClockwiseDiceOrder3[index] == top && counterClockwiseDiceOrder3[index + 1] == right) {
                // take higher out of the remaining two not in the array as the rear value
                rear = 6;
                front = SUM_OF_OPPOSITE_SIDES - rear;
                break;
            }

            if (counterClockwiseDiceOrder3[index] == right && counterClockwiseDiceOrder3[index + 1] == top) {
                // take lower out of the remaining two not in the array as the rear value
                rear = 1;
                front = SUM_OF_OPPOSITE_SIDES - rear;
                break;
            }
        }
    }

    /**
     * Sets the values of faces if the given dice is a king
     * @param value true if the dice is to be set as a king
     */
    public void SetKing(boolean value) {
        king = value;
        if (value) {
            top = 1;
            bottom = 1;
            front = 1;
            rear = 1;
            left = 1;
            right = 1;
        }
    }

    /**
     * Sets the controller of a dice
     * @param value true if botOperated, false if humanOperated
     */
    public void SetBotControl(boolean value) {
        botOperated = value;
    }

    /**
     * Sets the dice as captured or uncaptured
     * @param value true if dice is to be set as captured, false otherwise
     */
    public void SetCaptured(boolean value) {
        captured = value;
    }

    /**
     * Sets the dice coordinates
     * @param row row coordinate of the dice in a game board
     * @param column column coordinate of the dice in a game board
     */
    public void SetCoordinates(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Increments or decrements a dice row based on given value and the boolean flag
     * @param value value to increment or decrement the row of the dice by
     * @param increment true if row is to be incremented, false if it is to be decremented
     */
    public void SetRow(int value, boolean increment) {
        if (increment && (row + value) < 8) {
            row += value;
        }
        if (!increment && (row - value) >= 0) {
            row -= value;
        }
    }

    /**
     * Increments or decrements a dice column based on given value and the boolean flag
     * @param value value to increment or decrement the column of the dice by
     * @param increment true if column is to be incremented, false if it is to be decremented
     */
    public void SetColumn(int value, boolean increment) {
        if (increment && (column + value) < 9) {
            column += value;
        }
        if (!increment && (column - value) >= 0) {
            column -= value;
        }
    }

    /**
     * Sets the top Value of a dice
     * @param value top value of the dice
     */
    public void SetTop(int value) {
        top = value;
    }

    /**
     * Sets the bottom Value of a dice
     * @param value bottom value of the dice
     */
    public void SetBottom(int value) {
        bottom = value;
    }

    /**
     * Sets the front Value of a dice
     * @param value front value of the dice
     */
    public void SetFront(int value) {
        front = value;
    }

    /**
     * Sets the rear Value of a dice
     * @param value rear value of the dice
     */
    public void SetRear(int value) {
        rear = value;
    }

    /**
     * Sets the left Value of a dice
     * @param value left value of the dice
     */
    public void SetLeft(int value) {
        left = value;
    }

    /**
     * Sets the right Value of a dice
     * @param value Right value of the dice
     */
    public void SetRight(int value) {
        right = value;
    }
}
