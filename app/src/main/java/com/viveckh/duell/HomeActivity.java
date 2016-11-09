package com.viveckh.duell;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class HomeActivity extends AppCompatActivity {

    String nextPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ((Button)findViewById(R.id.btn_ProceedToGame)).setVisibility(View.INVISIBLE);
    }

    public void ProceedToGame(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("startMode", "new");    // Whether game is new or restored
        intent.putExtra("nextPlayer", nextPlayer);
        startActivity(intent);
    }

    public void RestoreGame(View view) {
        Board board = new Board();
        Serializer serializer = new Serializer();
        if (serializer.ReadAFile("GameSave.txt", board, 3, 4, "human")) {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("startMode", "restore");    // Whether game is new or restored
            intent.putExtra("nextPlayer", "human");
            intent.putExtra("gameBoard", board);
            startActivity(intent);
        }
        else {
            ((TextView)findViewById(R.id.txtView_TossResults)).setText("File couldn't be read. Try again!");
        }
    }

    public void StartNewGame(View view) {
        //Show only components necessary to view toss results and to proceed to a new game
        ((Button)findViewById(R.id.btn_RestoreFromFile)).setVisibility(View.INVISIBLE);
        ((Button)findViewById(R.id.btn_ProceedToGame)).setVisibility(View.VISIBLE);

        TossToBegin();
    }

    private void TossToBegin(){
        Random rand = new Random();
        int humanDieToss, botDieToss;
        TextView txtView_TossResults = (TextView)findViewById(R.id.txtView_TossResults);

        // Continue until both have different Toss results
        do {
            humanDieToss = rand.nextInt(6) + 1;
            botDieToss = rand.nextInt(6) + 1;
        } while (humanDieToss == botDieToss);

        // Whoever has the highest number on top - wins the toss
        if (humanDieToss > botDieToss) {
            nextPlayer = "human";
            txtView_TossResults.setText("Toss Results:\nComputer: " + botDieToss + "\nHuman: " + humanDieToss + "\nYou won the toss.");
        }
        else {
            nextPlayer = "computer";
            txtView_TossResults.setText("Toss Results:\nComputer: " + botDieToss + "\nHuman: " + humanDieToss + "\nComputer won the toss.");
        }
    }
}
