package com.viveckh.duell;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.GridLayout;
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
    private RadioGroup radioGrp_PathChoice;
    private TextView txtView_nextPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Get intent and set board based on whether the game is fresh or restored
        Intent intent = getIntent();
        if (intent.getStringExtra("startMode").equals("new")) {
            board = new Board();
        }
        if (intent.getStringExtra("startMode").equals("restore")) {
            board = new Board((Board)intent.getSerializableExtra("gameBoard"));
        }

        // Display the tournament scores and the next player based on the contents from Tournament static class
        txtView_nextPlayer = (TextView)findViewById(R.id.txtView_nextPlayer);
        // Find out and set whose turn it is first
        if (Tournament.GetNextPlayer().equals("human")) {
            humanTurn = true;
            txtView_nextPlayer.setText("Your Turn");
        }
        else {
            computerTurn = true;
            txtView_nextPlayer.setText("Bot's Turn");
        }

        //Setting the scores
        ((TextView)findViewById(R.id.txtView_Scores)).setText("Scores" + "\nComp.:\t\t" + Tournament.GetComputerScore() +"\nHuman:\t" + Tournament.GetHumanScore());

        DrawBoard(board);
        human = new Human();
        computer = new Computer();



        // Initialize the radio buttons for user path choices
        radioGrp_PathChoice = (RadioGroup)findViewById(R.id.radioGrp_PathChoice);
        SetRadioGroupListener();

        //reset the button availability based on the game initialization
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

        // Ensure the previously selected coordinates are cleared in 90 degree turn cases when user switches origin-destination without selecting a path
        if (!hasUserInitiatedMove & (origin != null && destination != null)) {
            SetAButtonPress(origin, false);
            SetAButtonPress(destination, false);
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
                Tournament.IncrementHumanScoreBy(1);
                AutoRedirectToResults("human");
            }

            DrawBoard(board);
            HighlightAMove(origin, destination);

            // Transfer control to computer and let it make a move
            humanTurn = false;
            computerTurn = true;
            Tournament.SetNextPlayer("computer");
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

        //Save the board before move for using it later to find out which coordinates to highlight based on changes
        Board boardBeforeMove = new Board(board);

        // Process computer move and draw the board
        computer.Play(board, false);
        DrawBoard(board);
        ShowComputerMove(boardBeforeMove, board);

        //Go to next activity if game over
        if (board.GameOverConditionMet()) {
            // ATTENTION: Computer won, go to next activity and display results, and ask if user wants to replay
            Tournament.IncrementComputerScoreBy(1);
            AutoRedirectToResults("computer");
        }

        // Transfer controls to human and disable the buttons for bot.play
        computerTurn = false;
        humanTurn = true;
        Tournament.SetNextPlayer("human");
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

    // Finds out how the computer changed the board after its move and highligts the move it made
    private void ShowComputerMove(Board initialBoard, Board finalBoard) {
        int changesFound = 0;

        //Find out the changes that have occured to the board after the move
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 9; col++) {
                // Subtracting from a constant to print an inverted model in the view
                int model_row = TROWS - row - 1;

                //Get the corresponding residents in the two boards first
                String resident_Initial = "";
                String resident_Final = "";

                // Finding the resident in the given coordinate for the initial board state
                if (initialBoard.IsSquareOccupied(model_row, col)) {
                    if (initialBoard.GetSquareResident(model_row, col).IsBotOperated()) {
                        resident_Initial = "C" + initialBoard.GetSquareResident(model_row, col).GetTop() + initialBoard.GetSquareResident(model_row, col).GetLeft();
                    }
                    else {
                        resident_Initial = "H" + initialBoard.GetSquareResident(model_row, col).GetTop() + initialBoard.GetSquareResident(model_row, col).GetRight();
                    }
                }

                // Finding the resident in the given coordinate for the final board state
                if (finalBoard.IsSquareOccupied(model_row, col)) {
                    if (finalBoard.GetSquareResident(model_row, col).IsBotOperated()) {
                        resident_Final = "C" + finalBoard.GetSquareResident(model_row, col).GetTop() + finalBoard.GetSquareResident(model_row, col).GetLeft();
                    }
                    else {
                        resident_Final = "H" + finalBoard.GetSquareResident(model_row, col).GetTop() + finalBoard.GetSquareResident(model_row, col).GetRight();
                    }
                }

                //Find where changes have taken place and set them to either origin or destination
                //System.out.println("INITIAL RESIDENT: " + resident_Initial);
                //System.out.println("FINAL RESIDENT: " + resident_Final);

                //Set the class variables of origin and destination
                if (!resident_Initial.equals(resident_Final)) {
                    String button_ID = "button" + row + col;
                    int button_ResID = getResources().getIdentifier(button_ID, "id", getPackageName());
                    if (changesFound == 0) {
                        origin = findViewById(button_ResID);
                    }
                    if (changesFound == 1) {
                        destination = findViewById(button_ResID);
                    }
                    changesFound++;
                }
            }
        }

        System.out.println("CHANGES FOUND: " + changesFound);
        //If at least two changes are found, then continue with the highlighting
        if (changesFound >= 2) {
            HighlightAMove(origin, destination);
        }
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
        animation.setRepeatCount(12); // Repeat animation infinitely
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

    //Calls a timer to wait before automatically redirecting to the results activity
    private void AutoRedirectToResults(String winner) {
        //Disable the entire view first to prevent user manipulation
        GridLayout layout = (GridLayout) findViewById(R.id.boardGrid);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            //Change everything to background color red if not the msgbox
            if (child.getId() != R.id.txtView_BotMessages) {
                child.setEnabled(false);
            }
            else {
                child.setBackgroundColor(Color.RED);
            }
        }

        final String winningTeam = winner;
        //Now start the timer to wait
        final TextView txtView_BotMessages = (TextView) findViewById(R.id.txtView_BotMessages);
        CountDownTimer timer = new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                txtView_BotMessages.setText("GAME OVER.\n" + winningTeam + " won.\nRedirecting to results page in " + millisUntilFinished / 1000 + " seconds");
            }

            public void onFinish() {
                txtView_BotMessages.setText("Redirecting Now!");
                Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
                intent.putExtra("winner", winningTeam);    // Whether game is new or restored
                startActivity(intent);
            }
        };
        timer.start();
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
                        System.out.print(model_row + "" + col + "\t");
                    }
                }
                System.out.println(row + "" + col + board.IsSquareOccupied(model_row, col));

                String button_ID = "button" + row + col;
                int button_ResID = getResources().getIdentifier(button_ID, "id", getPackageName());
                Button button = (Button)findViewById(button_ResID);
                button.setText(button_Text);
            }
        }
        board.ViewNonCapturedDice();
    }

    // Serialize game
    public void SerializeGame(View view) {
        // Display an alert dialog box to confirm the user wants to save and exit the game
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(this);
        alert_confirm.setMessage("Are you sure you want to save and quit?");
        alert_confirm.setCancelable(true);

        alert_confirm.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Serializer serializer = new Serializer();
                        serializer.WriteToFile("GameSave.txt", board);
                        dialog.cancel();
                        GameActivity.this.finishAffinity();
                    }
                });

        alert_confirm.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        return;
                    }
                });

        AlertDialog alertDialog = alert_confirm.create();
        alertDialog.show();
    }
}
