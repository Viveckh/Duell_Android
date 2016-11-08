package com.viveckh.duell;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Created by ZCV0LHB on 11/7/2016.
 */
public class Serializer {
    //Declaring variables
    private String[][] serializedGameBoard = new String[8][9];
    private String fileName;

    //Constructor
    public Serializer() {
        fileName = "Duell_LastGameSerialization.txt";
        //initialize all the indexes of the serializedGameBoard multi-dimensional string array
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 9; j++) {
                serializedGameBoard[i][j] = new String();
            }
        }
    }

    // Writing serialized game state along with tournament history results to file
    public boolean WriteToFile(String fileName, Board board, int botWins, int humanWins, String nextPlayer) {
        this.fileName = fileName;
        // Update the multidimensional string array for serialization first
        UpdateSerializedBoard(board);

        try {
            PrintWriter writer = new PrintWriter(fileName);

            // Writing the board first
            writer.println("Board");
            for (int row = 7; row >= 0; row--) {
                for (int col = 0; col < 9; col++) {
                    writer.print(serializedGameBoard[row][col] + '\t');
                }
                writer.print("\n");
            }

            // Writing the number of wins and next Player
            writer.println("Computer Wins: " + botWins + "\n");
            writer.println("Human Wins: " + humanWins + "\n");
            writer.println("Next Player: " + nextPlayer + "\n");
            writer.close();
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    // Stores the game state in a multidimensional string array.
    private void UpdateSerializedBoard(Board board) {
        for (int row = 7; row >= 0; row--) {
            for (int col = 0; col < 9; col++) {
                serializedGameBoard[row][col] = "0";
                if (board.IsSquareOccupied(row, col)) {
                    if (board.GetSquareResident(row, col).IsBotOperated()) {
                        serializedGameBoard[row][col] = "C";
                        // Append the top and right value of the occupying dice
                        serializedGameBoard[row][col] += Integer.toString(board.GetSquareResident(row, col).GetTop());
                        serializedGameBoard[row][col] += Integer.toString(board.GetSquareResident(row, col).GetLeft());
                    }
                    else {
                        serializedGameBoard[row][col] = "H";
                        // Append the top and right value of the occupying dice
                        serializedGameBoard[row][col] += Integer.toString(board.GetSquareResident(row, col).GetTop());
                        serializedGameBoard[row][col] += Integer.toString(board.GetSquareResident(row, col).GetRight());
                    }
                }
            }
        }
    }

}
