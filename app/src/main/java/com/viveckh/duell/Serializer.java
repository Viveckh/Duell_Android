package com.viveckh.duell;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

/**
 * Created by ZCV0LHB on 11/7/2016.
 */
public class Serializer {
    //Declaring variables
    private String[][] serializedGameBoard = new String[8][9];
    private String fileName;
    private File sdCard;

    //Constructor
    public Serializer() {
        fileName = "Duell_LastGameSerialization.txt";
        sdCard = Environment.getExternalStorageDirectory();
        //initialize all the indexes of the serializedGameBoard multi-dimensional string array
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 9; j++) {
                serializedGameBoard[i][j] = new String();
            }
        }
    }

    // Writing serialized game state along with tournament history results to file
    public boolean WriteToFile(String fileName, Board board) {
        // Setup the proper location to write the game state to
        this.fileName = fileName;
        File writeToDir =  new File (sdCard.getAbsolutePath() + "/Duell Data");
        writeToDir.mkdirs();
        File file = new File(writeToDir, fileName);

        // Update the multidimensional string array for serialization first
        UpdateSerializedBoard(board);

        try {
            FileOutputStream outputStream = new FileOutputStream(file);

            // Writing the board first
            outputStream.write("Board\n".getBytes());
            for (int row = 7; row >= 0; row--) {
                for (int col = 0; col < 9; col++) {
                    outputStream.write((serializedGameBoard[row][col] + "\t").getBytes());
                }
                outputStream.write("\n".getBytes());
            }

            // Writing the number of wins and next Player
            outputStream.write("\n".getBytes());
            outputStream.write(("Computer Wins: " + Tournament.GetComputerScore() + "\n").getBytes());
            outputStream.write(("Human Wins: " + Tournament.GetHumanScore() + "\n").getBytes());
            outputStream.write(("Next Player: " + Tournament.GetNextPlayer() + "\n").getBytes());
            outputStream.close();
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    // Reads a serialization file and stores in a multidimensional string array for restoring purposes
    boolean ReadAFile(String fileName, Board board) {
        System.out.println(fileName);
        File file = new File(sdCard + "/Duell Data", fileName);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            //Step 1: Reading the line with the "Board" text
            reader.readLine();

            //Step 2: Reading the gameboard and populating the multidimensional string array
            // The topmost row in the file is actually the 8th row in the model, so read inverted
            int row = 7;
            while (((line = reader.readLine()) != null) && (row >= 0)) {
                System.out.println(line);
                line = line.trim();       // To get rid of all the leading and trailing spaces in the read string
                String temp[] = line.split("\\s+");
                for (int col = 0; col < 9; col++) {
                    if (col < temp.length) {
                        serializedGameBoard[row][col] = temp[col];
                        System.out.println("|" + serializedGameBoard[row][col] + "|");
                    }
                }
                row--;
            }
            SetBoard(board);

            //Step 3: Reading the human and computer scores and the next player
            while (((line = reader.readLine()) != null)) {
                // Continue if the line is not empty
                if (!line.trim().isEmpty()) {
                    // Parse number of computer wins
                    if (line.matches("(\\s*)[Cc]omputer(\\s+)[Ww]ins(.*)")) {
                        System.out.println(line);
                        int botWins = Integer.parseInt(line.replaceAll("[\\D]", ""));
                        Tournament.IncrementComputerScoreBy(botWins);   //assuming if we are reading a file, the score is initially set to zero
                        System.out.println(botWins);
                    }

                    // Parse number of human wins
                    if (line.matches("(\\s*)[Hh]uman(\\s+)[Ww]ins(.*)")) {
                        System.out.println(line);
                        int humanWins = Integer.parseInt(line.replaceAll("[\\D]", ""));
                        Tournament.IncrementHumanScoreBy(humanWins); //assuming if we are reading a file, the score is initially set to zero
                        System.out.println(humanWins);
                    }

                    // Parse the next player
                    if (line.matches("(\\s*)[Nn]ext(\\s+)[Pp]layer(.*)")) {
                        System.out.println(line);
                        if (line.matches("(.*):(.*)[Cc]omputer(.*)")) {
                            Tournament.SetNextPlayer("computer");
                        }
                        else {
                            Tournament.SetNextPlayer("human");
                        }
                        System.out.println(Tournament.GetNextPlayer());
                    }
                }
            }
            reader.close();
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    // Sets the given board based on the contents of the string array restored by reading file
    private void SetBoard(Board board) {
        // This one is for going through the Human's player dices
        int humanCount = 0;
        int botCount = 0;

        // This one is for temporary purposes
        int tempHumanIndex = 0;
        int tempBotIndex = 0;

        // Go through every index of the serializedGameBoard stored in the string array and update the actual game board
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 9; col++) {
                board.SetSquareVacant(row, col);
                // If the square is empty
                if (serializedGameBoard[row][col].charAt(0) == '0') {
                    continue;
                }

                // If the square is occupied by computer dice
                if (serializedGameBoard[row][col].charAt(0) == 'C') {
                    // Check if the dice at hand is a king, and determine the index accordingly
                    if (serializedGameBoard[row][col].charAt(1) == '1' && serializedGameBoard[row][col].charAt(2) == '1') {
                        // it is a king, so assign the king index
                        tempBotIndex = 4;
                    }
                    else {
                        tempBotIndex = botCount;
                    }

                    // Setting bot properties
                    board.bots[tempBotIndex].SetBotControl(true);
                    board.bots[tempBotIndex].SetCaptured(false);
                    board.bots[tempBotIndex].SetCoordinates(row, col);
                    board.bots[tempBotIndex].SetTop(Character.getNumericValue(serializedGameBoard[row][col].charAt(1)));
                    board.bots[tempBotIndex].SetLeft(Character.getNumericValue(serializedGameBoard[row][col].charAt(2)));		// Setting left, because computer's left is actually board's right.

                    if (board.bots[tempBotIndex].GetTop() == 1 && board.bots[tempBotIndex].GetLeft() == 1) {
                        board.bots[tempBotIndex].SetKing(true);
                    }
                    else {
                        board.bots[tempBotIndex].SetRemainingSides(board.bots[tempBotIndex].GetTop(), board.bots[tempBotIndex].GetLeft());
                    }

                    // setting square properties
                    board.SetSquareOccupied(row, col, board.bots[tempBotIndex]);
                    //cout << tempBotIndex << "C" << board.bots[tempBotIndex].GetTop() << board.bots[tempBotIndex].GetRight() << endl;

                    // Incrementing counts
                    if (tempBotIndex != 4) { botCount++; }
                    if (botCount == 4) { botCount++; }	// This index is reserved for the king
                    continue;
                }

                // If the square is occupied by human dice
                if (serializedGameBoard[row][col].charAt(0) == 'H') {
                    // Check if the dice at hand is a king, and determine the index accordingly
                    if (serializedGameBoard[row][col].charAt(1) == '1' && serializedGameBoard[row][col].charAt(2) == '1') {
                        // it is a king, so assign the king index
                        tempHumanIndex = 4;
                    }
                    else {
                        tempHumanIndex = humanCount;
                    }

                    // Setting human properties
                    board.humans[tempHumanIndex].SetBotControl(false);
                    board.humans[tempHumanIndex].SetCaptured(false);
                    board.humans[tempHumanIndex].SetCoordinates(row, col);
                    board.humans[tempHumanIndex].SetTop(Character.getNumericValue(serializedGameBoard[row][col].charAt(1)));
                    board.humans[tempHumanIndex].SetRight(Character.getNumericValue(serializedGameBoard[row][col].charAt(2)));

                    if (board.humans[tempHumanIndex].GetTop() == 1 && board.humans[tempHumanIndex].GetRight() == 1) {
                        board.humans[tempHumanIndex].SetKing(true);
                    }
                    else {
                        board.humans[tempHumanIndex].SetRemainingSides(board.humans[tempHumanIndex].GetTop(), board.humans[tempHumanIndex].GetRight());
                    }

                    // setting square properties
                    board.SetSquareOccupied(row, col, board.humans[tempHumanIndex]);
                    //cout << tempHumanIndex << "H" << board.humans[tempHumanIndex].GetTop() << board.humans[tempHumanIndex].GetRight() << endl;

                    // incrementing counts
                    if (tempHumanIndex != 4) { humanCount++; }
                    if (humanCount == 4) { humanCount++; }	// This index is reserved for the king
                    continue;
                }
            }
        }

        // The dices not found are definitely captured, so set the flags
        while (botCount != 9) {
            if (botCount != 4) board.bots[botCount].SetCaptured(true);
            botCount++;
        }
        while (humanCount != 9) {
            if (humanCount != 4) board.humans[humanCount].SetCaptured(true);
            humanCount++;
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
