package com.viveckh.duell;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

public class GameActivity extends AppCompatActivity {

    // Declaring instances of objects and class variables
    private Board board;
    private Human human;
    private Computer computer;

    final public int TROWS = 8;
    private boolean hasUserInitiatedMove = false;

    private int startRow, startCol, endRow, endCol;
    private boolean humanTurn = false;
    private boolean computerTurn = false;

    // View elements
    private View origin, destination;   // Start and end squares in the display board, from view perspective

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        board = new Board();
        DrawBoard(board);
        human = new Human();
        computer = new Computer();

        //Temporary additions until bundle data is passed from previous action
        humanTurn = true;
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
        // Human player can't make moves if it is not his turn
        if (!humanTurn) {
            return;
        }

        // Animate the pressed button

        // Get the details of the pressed button
        String buttonDetails = view.getTag().toString();
        int btnRow = Integer.parseInt(buttonDetails.split(",")[0]);
        int btnColumn = Integer.parseInt(buttonDetails.split(",")[1]);

        // If move hasn't been initiated, consider the just clicked button as starting point
        if (!hasUserInitiatedMove) {
            origin = view;
            startRow = TROWS - btnRow - 1;  //Since the view is inverted, topmost row in model is bottommost in view and vice versa.
            startCol = btnColumn;
            hasUserInitiatedMove = true;
            //Call function to highlight that button

            // Set the button viewable in pressed state, so it is easy to identify. Undo when the destination is picked
            SetAButtonPress(origin, true);
            return;
        }

        // If the move has already been initiated, then consider the just clicked button as end point
        // and make the move
        if (hasUserInitiatedMove) {
            destination = view;
            endRow = TROWS - btnRow - 1;    //Since the view is inverted, topmost row in model is bottommost in view and vice versa.
            endCol = btnColumn;

            // Make the move and, if successful, transfer the controls to the computer
            if (human.Play(startRow, startCol, endRow, endCol, board)) {
                DrawBoard(board);
                HighlightAMove(origin, destination);

                // Transfer control to computer and let it make a move
                humanTurn = false;
                computerTurn = true;
                ProcessComputerMove();

            }
            hasUserInitiatedMove = false;
            return;
        }
    }

    public void ProcessComputerMove() {
        // return if not computer's turn
        if (!computerTurn) {
            return;
        }
        // Process computer move and transfer controls to human
        computer.Play(board, false);
        DrawBoard(board);

        computerTurn = false;
        humanTurn = true;
    }

    public void SetAButtonPress(View view, boolean state) {
        /*
        Button button = (Button)findViewById(view.getId());
        view.setPressed(true);
        */
    }

    public void HighlightAMove(View origin, View destination) {
        // Unpress buttons if they were pressed earlier for easy identification purposes
        SetAButtonPress(origin, false);
        SetAButtonPress(destination, false);

        // Prepare the animation settings
        Animation animation = new AlphaAnimation(1, 0);     // Change alpha from fully visible to invisible
        animation.setDuration(200); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(6); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in

        // Get the views to highlight and start animation
        Button startPoint = (Button)findViewById(origin.getId());
        Button endPoint = (Button)findViewById(destination.getId());

        /*
        // Setting listener to make main thread wait until animation is over
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                startPoint.setVisibility(View.VISIBLE);
            }
        });
        */

       // Start animation
        startPoint.startAnimation(animation);
        endPoint.startAnimation(animation);
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
