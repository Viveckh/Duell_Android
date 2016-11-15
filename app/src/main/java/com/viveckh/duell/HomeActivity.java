package com.viveckh.duell;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class HomeActivity extends AppCompatActivity {

    String nextPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ((Button)findViewById(R.id.btn_ProceedToGame)).setVisibility(View.INVISIBLE);

        // Get the files in the given folder and display in the ListView
        ArrayList<String> FilesInFolder = GetFiles(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Duell Data");
        final ListView listView_SerializationFiles = (ListView)findViewById(R.id.listView_SerializationFiles);
        listView_SerializationFiles.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, FilesInFolder));

        listView_SerializationFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // Clicking on items
                String selectedItem = (String)(listView_SerializationFiles.getItemAtPosition(position));
                RestoreGame(selectedItem);
            }
        });

        //Button btn_RestoreFromFile = (Button) findViewById(R.id.btn_RestoreFromFile);
    }

    // Gets the files in a given directory and puts them into an array list
    public ArrayList<String> GetFiles(String DirectoryPath) {
        ArrayList<String> MyFiles = new ArrayList<String>();
        File f = new File(DirectoryPath);

        f.mkdirs();
        File[] files = f.listFiles();
        if (files.length == 0)
            return null;
        else {
            for (int i=0; i<files.length; i++)
                MyFiles.add(files[i].getName());
        }

        return MyFiles;
    }

    public void ProceedToGame(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("startMode", "new");    // Whether game is new or restored
        intent.putExtra("nextPlayer", nextPlayer);
        startActivity(intent);
    }

    public void RestoreGame(String fileName) {
        Board board = new Board();
        Serializer serializer = new Serializer();
        if (serializer.ReadAFile(fileName, board, 3, 4, "human")) {
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
