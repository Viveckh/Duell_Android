package com.viveckh.duell;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class GameActivity extends AppCompatActivity {

    Board board;
    final public int TROWS = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        board = new Board();
        DrawBoard(board);
        Human human = new Human();
        Computer computer = new Computer();

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
    }

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
