package com.viveckh.duell;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.ResultSet;
import java.util.Random;

/**
 * ResultsActivity Class
 * This activity displays the results of a game and gets user choice on whether to continue or end a tournament to act accordingly
 * Author: Vivek Pandey
 * Project: Duell in Java Android
 * Class: CMPS 366
 * Last Modified on: 11/29/2016
 */
public class ResultsActivity extends AppCompatActivity {

    //Declaring variables to point to the various view objects
    TextView txtView_GameWinner;
    TextView txtView_TournamentScores;
    TextView txtView_ExtraNotifications;
    TextView txtView_AnotherRoundPrompt;

    Button btn_PlayAnotherRound;
    Button btn_EndTournament;
    Button btn_Proceed;
    Button btn_Quit;

    @Override
    /**
     * Sets the view to the proper layout, assigns class variables to view objects, initializes onclick listeners on buttons for user input and hides unnecessary view objects
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        //Assigning variables to the various view objects
        txtView_GameWinner = (TextView)findViewById(R.id.txtView_GameWinner);
        txtView_TournamentScores = (TextView)findViewById(R.id.txtView_TournamentScores);
        txtView_ExtraNotifications = (TextView)findViewById(R.id.txtView_ExtraNotifications);
        txtView_AnotherRoundPrompt = (TextView)findViewById(R.id.txtView_AnotherRoundPrompt);

        btn_PlayAnotherRound = (Button)findViewById(R.id.btn_PlayAnotherRound);
        btn_EndTournament = (Button)findViewById(R.id.btn_EndTournament);
        btn_Proceed =(Button)findViewById(R.id.btn_Proceed);
        btn_Quit = (Button)findViewById(R.id.btn_Quit);

        //Hide certain elements when the activity is first started
        btn_Proceed.setVisibility(View.INVISIBLE);
        btn_Quit.setVisibility(View.INVISIBLE);

        //Display the winner
        Intent intent = getIntent();
        if (intent.getStringExtra("winner").equals("human")) {
            txtView_GameWinner.setText("You won the game");
        }
        else {
            txtView_GameWinner.setText("I (the computer) won the game");
        }

        //Setting the scores
        txtView_TournamentScores.setText("Scores" + "\nComp.:\t" + Tournament.GetComputerScore() +"\nHuman:\t" + Tournament.GetHumanScore());

        //Set on click listener if the user wants to play another round
        btn_PlayAnotherRound.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TossToBegin();
                btn_Proceed.setVisibility(View.VISIBLE);
                HideUserChoiceToContinue();
            }
        });

        //Set on click listener if the user doesn't want to continue anymore
        btn_EndTournament.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Tournament.GetHumanScore() > Tournament.GetComputerScore()) {
                    txtView_ExtraNotifications.setText("Congratulations! You won the Tournament. Our programmer must've done a terrible job on algorithms for someone like you to win.");
                }
                else if (Tournament.GetHumanScore() < Tournament.GetComputerScore()) {
                    txtView_ExtraNotifications.setText("The Computer Won the Tournament. *reinforcing the notion that we bots are better than you humans once again*");
                }
                else {
                    txtView_ExtraNotifications.setText("It was a draw. Guess we'll see who's better in the next tournament.");
                }

                btn_Quit.setVisibility(View.VISIBLE);
                HideUserChoiceToContinue();
            }
        });

        //Set on click listener to start new game after the toss
        btn_Proceed.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                intent.putExtra("startMode", "new");    // Whether game is new or restored
                startActivity(intent);
            }
        });

        //Set on click listener to quit the game when the user clicks the quit button
        btn_Quit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ResultsActivity.this.finishAffinity();
            }
        });
    }

    /**
     * Hides the user choice to continue to another game, normally used when user decides to end the tournament
     */
    private void HideUserChoiceToContinue() {
        txtView_AnotherRoundPrompt.setVisibility(View.INVISIBLE);
        btn_PlayAnotherRound.setVisibility(View.INVISIBLE);
        btn_EndTournament.setVisibility(View.INVISIBLE);
    }

    /**
     * Does a toss until one side wins, refreshes view with the toss result and sets the necessary values in static Tournament class regarding which side is the next player
     */
    private void TossToBegin(){
        Random rand = new Random();
        int humanDieToss, botDieToss;

        // Continue until both have different Toss results
        do {
            humanDieToss = rand.nextInt(6) + 1;
            botDieToss = rand.nextInt(6) + 1;
        } while (humanDieToss == botDieToss);

        // Whoever has the highest number on top - wins the toss
        if (humanDieToss > botDieToss) {
            Tournament.SetNextPlayer("human");
            txtView_ExtraNotifications.setText("Toss Results:\nComputer: " + botDieToss + "\nHuman: " + humanDieToss + "\nYou won the toss.");
        }
        else {
            Tournament.SetNextPlayer("computer");
            txtView_ExtraNotifications.setText("Toss Results:\nComputer: " + botDieToss + "\nHuman: " + humanDieToss + "\nComputer won the toss.");
        }
    }

}
