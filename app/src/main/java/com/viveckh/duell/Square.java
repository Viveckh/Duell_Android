package com.viveckh.duell;

import java.io.Serializable;

/**
 * Square Class
 * Stores and maintains the location and occupancy status of a square in the game board. A square can be occupied by a dice object only.
 * Author: Vivek Pandey
 * Last Modified on: 11/27/2016
 */

public class Square implements Serializable{
    // VARIABLE DECLARATIONS

    // Coordinates of the square within the game board
    private int row;
    private int column;

    // Occupancy status, not occupied if the pointer is null
    private Dice resident;

    /**
     * DEFAULT CONSTRUCTOR
     */
    public Square() {
        row = 0;
        column = 0;
        resident = new Dice();
        resident = null;
    }

    /**
     * CONSTRUCTOR
     * @param row The row of the square in a Board context
     * @param column The columno f the square in a Board context
     */
    public Square(int row, int column) {
        this(); // Calling default constructor
        this.row = row;
        this.column = column;
    }

    /**
     * COPY CONSTRUCTOR
     * @param anotherOne The square whose contents are to be copied over
     */
    public Square(Square anotherOne) {
        this.row = anotherOne.row;
        this.column = anotherOne.column;
        if (anotherOne.resident != null) {
            resident = new Dice(anotherOne.resident);
        }
        else {
            resident = null;
        }
    }

    //ACCESSORS

    /**
     * Gets the row of the square in a board
     * @return the row of the square
     */
    public int GetRow() {
        return row;
    }

    /**
     * Gets the column of the square in a board
     * @return the column of the square
     */
    public int GetColumn() {
        return column;
    }

    /**
     * Gets a copy of the resident die
     * @return A copy of the resident die
     */
    public Dice GetResident() {
        return resident;
    }

    /**
     * Checks whether the square is occupied by a gameobject
     * @return true if occupied, false otherwise
     */
    public boolean IsOccupied() {
        if (resident != null) {
            return true;
        }
        else {
            return false;
        }
    }

    // MUTATORS

    /**
     * Sets coordinates of the square in a board
     * @param row The row of the square in a Board context
     * @param column The column of the square in a Board context
     */
    public void SetCoordinates(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Sets the given dice as a resident of the square
     * @param dice the dice to be set as the resident of the square
     */
    public void SetOccupied(Dice dice) {
        resident = dice;
    }

    /**
     * Clears the occupancy in the square
     */
    public void SetVacant() {
        resident = null;
    }

    /**
     * Sets the resident as captured/uncaptured
     * @param value true if resident is to be captured, false if it is to be uncaptured
     */
    public void SetResidentCaptured(boolean value) {
        resident.SetCaptured(value);
    }

    /**
     * If the given dice has the same reference in the memory as the resident die
     * @param dice The dice to be tested
     * @return true if the given dice references the same memory location as the resident die
     */
    public boolean MatchingResident(Dice dice) {
        if (resident == dice) {
            return true;
        }
        return false;
    }
}