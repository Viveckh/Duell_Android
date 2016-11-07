package com.viveckh.duell;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

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

    // View components
    RadioGroup radioGrp_PathChoice;
    TextView txtView_nextPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        board = new Board();
        DrawBoard(board);
        human = new Human();
        computer = new Computer();

        radioGrp_PathChoice = (RadioGroup)findViewById(R.id.radioGrp_PathChoice);
        txtView_nextPlayer = (TextView)findViewById(R.id.txtView_nextPlayer);
        SetRadioGroupListener();

        //Temporary additions until bundle data is passed from previous action
        humanTurn = true;
        resetButtonAvailability();
    }

    // Processes the user input and passes over to finalize the move
    public void ProcessUserMove(View view) {
        // Human player can't make moves if it is not his turn
        if (!humanTurn) {
            DisplayControllerMessage("Not your turn!");
            return;
        }
        resetButtonAvailability();

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
            DisplayControllerMessage("Starting coordinate selected!");
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
            hasUserInitiatedMove = false;
            SetAButtonPress(destination, true);

            // If path choice necessary, then wait for user action by activating radiobuttons; else continue towards finalizing move
            if ((startRow != endRow) && (startCol != endCol)) {
                //Display the radiobuttons for path choice and wait user action
                radioGrp_PathChoice.setVisibility(View.VISIBLE);
                DisplayControllerMessage("Select path using the radio buttons to the right!");
                return;
            }
            else {
                FinalizeUserMove(0);    // Zero means no preference in path
                return;
            }
        }
    }

    //After ProcessUserMove processes the user input, this actually takes the path choice into consideration and finalizes move
    private void FinalizeUserMove(int pathChoice) {
        // PathChoice 0 indicates no preference, 1 indicates vertical-first, and 2 indicates lateral-first

        Notifications.ClearNotificationsList();
        // Make the move and, if successful, transfer the controls to the computer
        if (human.Play(startRow, startCol, endRow, endCol, board, pathChoice)) {
            //Go to next activity if game over
            if (board.GameOverConditionMet()) {
                // ATTENTION: Human won, go to next activity and display results, and ask if user wants to replay
            }

            DrawBoard(board);
            HighlightAMove(origin, destination);

            // Transfer control to computer and let it make a move
            humanTurn = false;
            computerTurn = true;
            txtView_nextPlayer.setText("Bot's Turn");
            // Reset Button Availability based on the Computer's turn next
        }
        //Do the following whether moves are successful or not
        DisplayBotMessages();
        resetButtonAvailability();
        SetAButtonPress(origin, false);
        SetAButtonPress(destination, false);
        // Hide the path choice radiobuttons no matter whether the move is successful or not
    }

    private void SetRadioGroupListener() {
        radioGrp_PathChoice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_vertical:
                        FinalizeUserMove(1);
                        break;
                    case R.id.radio_lateral:
                        FinalizeUserMove(2);
                        break;
                }
            }
        });
    }

    // Processes the move of a computer
    public void ProcessComputerMove(View view) {
        // return if not computer's turn
        if (!computerTurn) {
            return;
        }
        Notifications.ClearNotificationsList();

        // Process computer move and draw the board
        computer.Play(board, false);
        DrawBoard(board);

        //Go to next activity if game over
        if (board.GameOverConditionMet()) {
            // ATTENTION: Human won, go to next activity and display results, and ask if user wants to replay
        }

        // Transfer controls to human and disable the buttons for bot.play
        computerTurn = false;
        humanTurn = true;
        txtView_nextPlayer.setText("Your Turn");
        DisplayBotMessages();
        resetButtonAvailability();
    }

    //Generates an optimal move for the user using computer's algorithm
    public void TurnHelpModeOn(View view) {
        if (humanTurn) {
            Notifications.ClearNotificationsList();
            Notifications.Msg_HelpModeOn();
            computer.Play(board, true);
            DisplayBotMessages();
        }
    }

    // Simulates a button press given the view
    private void SetAButtonPress(View view, boolean state) {
        Button button = (Button) findViewById(view.getId());
        if (state) {
            button.setBackgroundColor(Color.BLUE);
        }
        else {
            button.setBackgroundResource(R.drawable.button_gameboard);
        }
    }

    //Resets the various button availability in the view depending on whose turn it is
    private void resetButtonAvailability() {
        Button btn_BotPlay = (Button)findViewById(R.id.btn_BotPlay);
        Button btn_Help = (Button)findViewById(R.id.btn_Help);

        if (humanTurn) {
            btn_Help.setVisibility(View.VISIBLE);
            btn_BotPlay.setVisibility(View.INVISIBLE);
        }
        if (computerTurn) {
            btn_Help.setVisibility(View.INVISIBLE);
            btn_BotPlay.setVisibility(View.VISIBLE);
        }
        //Hide the radiobuttons for choosing path no matter what. It will be turned on only in special circumstance
        radioGrp_PathChoice.clearCheck();
        radioGrp_PathChoice.setVisibility(View.INVISIBLE);
    }

    // Highlights a move given the origin and destination
    private void HighlightAMove(View origin, View destination) {
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

    public void DisplayBotMessages() {
        // If the msg vector is not empty, print out the msges that have been stored in it
        TextView txtView_BotMessages = (TextView) findViewById(R.id.txtView_BotMessages);
        String msgToDisplay;
        if (!Notifications.GetNotificationsList().isEmpty()) {
            msgToDisplay = Notifications.GetNotificationsList().toString()
                    .replace(",", "")
                    .replace("[", "")
                    .replace("]", "");
            txtView_BotMessages.setText(msgToDisplay);
        }
        else {
            msgToDisplay = "No messages to display.";
            txtView_BotMessages.setText(msgToDisplay);
        }
    }

    public void DisplayControllerMessage(String msg) {
        TextView txtView_BotMessages = (TextView) findViewById(R.id.txtView_BotMessages);
        txtView_BotMessages.setText(msg);
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
