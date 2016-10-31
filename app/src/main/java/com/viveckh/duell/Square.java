package com.viveckh.duell;

import com.viveckh.duell.Dice;

/**
 * Created by ZCV0LHB on 10/30/2016.
 */

public class Square {
    // VARIABLE DECLARATIONS

    // Coordinates of the square within the game board
    private int row;
    private int column;

    // Occupancy status, not occupied if the pointer is null
    private Dice resident = new Dice();

    // Default Constructor
    public Square() {
        row = 0;
        column = 0;
        resident = null;
    }

    // Constructor
    public Square(int row, int column) {
        this(); // Calling default constructor
        this.row = row;
        this.column = column;
    }

    //ACCESSORS

    // Gets the row of the square in a board
    public int GetRow() { return row; }

    // Gets the column of the square in a board
    public int GetColumn() { return column; }

    // Gets the pointer to resident die
    public Dice GetResident() {
        return resident;
    }

    // Checks whether the square is occupied by a gameobject
    public boolean IsOccupied() {
        if (resident != null) {
            return true;
        }
        else {
            return false;
        }
    }

    // MUTATORS
    // Sets coordinates of the square in a board
    public void SetCoordinates(int row, int column) {
        this.row = row;
        this.column = column;
    }

    // Sets the given dice as a resident of the square
    public void SetOccupied(Dice dice) {
        resident = dice;
    }

    // Clears the occupancy in the square
    public void SetVacant() {
        resident = null;
    }
}