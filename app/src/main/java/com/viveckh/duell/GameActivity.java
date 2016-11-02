package com.viveckh.duell;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GameActivity extends AppCompatActivity {

    // Declaring instances of objects and class variables
    Board board;
    Human human;
    Computer computer;

    final public int TROWS = 8;
    private boolean hasUserInitiatedMove = false;

    int startRow, startCol, endRow, endCol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        board = new Board();
        DrawBoard(board);
        human = new Human();
        computer = new Computer();
        /*
        computer.Play(board, false);
        human.Play(0, 0, 1, 4, board);
        computer.Play(board, false);
        human.Play(1, 4, 4, 4, board);
        computer.Play(board, false);
        human.Play(0, 5, 3, 8, board);
        computer.Play(board, false);
        human.Play(4, 4, 7, 2, board);
        computer.Play(board, false);
        human.Play(0, 8, 2, 5, board);
        computer.Play(board, false);
        DrawBoard(board);
        */
    }

    // Processes the user input
    public void ProcessUserMove(View view) {
        // Get the details of the pressed button
        String buttonDetails = view.getTag().toString();
        int btnRow = Integer.parseInt(buttonDetails.split(",")[0]);
        int btnColumn = Integer.parseInt(buttonDetails.split(",")[1]);
        System.out.println(btnRow);
        System.out.println(btnColumn);

        // If move hasn't been initiated, consider the just clicked button as starting point
        if (!hasUserInitiatedMove) {
            startRow = TROWS - btnRow - 1;  //Since the view is inverted, topmost row in model is bottommost in view and vice versa.
            startCol = btnColumn;
            hasUserInitiatedMove = true;
            System.out.println(startRow + "" + startCol);
            //Call function to highlight that button
            return;
        }

        // If the move has already been initiated, then consider the just clicked button as end point
        // and make the move
        if (hasUserInitiatedMove) {
            endRow = TROWS - btnRow - 1;
            endCol = btnColumn;
            System.out.println(endRow + "" + endCol);
            if (human.Play(startRow, startCol, endRow, endCol, board)) {
                System.out.println("Made the move successfully");
                computer.Play(board, false);
            }
            DrawBoard(board);
            hasUserInitiatedMove = false;
            return;
        }
    }


    // Updates the board view based on the current state of the game
    public void DrawBoard(Board board) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 9; col++) {
                // Subtracting from a constant to print an inverted model in the view
                int model_row = TROWS - row - 1;

                // Determining the text of the button
                String button_Text = "";
                if (board.IsSquareOccupied(model_row, col)) {
                    if (board.GetSquareResident(model_row, col).IsBotOperated()) {
                        button_Text = "C" + board.GetSquareResident(model_row, col).GetTop() + board.GetSquareResident(model_row, col).GetLeft();
                    }
                    else {
                        button_Text = "H" + board.GetSquareResident(model_row, col).GetTop() + board.GetSquareResident(model_row, col).GetRight();
                    }
                }

                String button_ID = "button" + row + col;
                int button_ResID = getResources().getIdentifier(button_ID, "id", getPackageName());
                Button button = (Button)findViewById(button_ResID);
                button.setText(button_Text);
            }
        }
    }




}
