package com.viveckh.duell;

/**
 * Created by ZCV0LHB on 11/14/2016.
 */
public final class Tournament {
    private static int humanScore;
    private static int computerScore;
    private static String nextPlayer;   //"human" or "computer" all lower case

    //Private constructor for a static class
    private Tournament() {
        humanScore = 0;
        computerScore = 0;
    }

    //Gets human score
    public static int GetHumanScore() {
        return humanScore;
    }

    //Gets computer score
    public static int GetComputerScore() {
        return computerScore;
    }

    //Gets next player in case if the tournament is resumed for a saved state
    public static String GetNextPlayer() {
        return nextPlayer;
    }

    //Sets game score
    public static void IncrementHumanScoreBy(int bumpScoreBy) {
        humanScore += bumpScoreBy;
    }

    //increments computer score in case of computer score
    public static void IncrementComputerScoreBy(int bumpScoreBy) {
        computerScore += bumpScoreBy;
    }

    // Resets scores for a fresh start
    public static void ResetScores() {
        humanScore = 0;
        computerScore = 0;
    }

    // Sets next player
    public static void SetNextPlayer(String player) {
        if (player == "computer" || player == "Computer") {
            nextPlayer = "computer";
        }
        else {
            nextPlayer = "human";
        }
    }
}