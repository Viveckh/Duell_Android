package com.viveckh.duell;

/**
 * Tournament Class
 * Static class that keeps a track of the scores and next player throughout the tournament.
 * Author: Vivek Pandey
 * Last Modified on: 11/27/2016
 */
public final class Tournament {
    // VARIABLES
    private static int humanScore;
    private static int computerScore;
    private static String nextPlayer;   //"human" or "computer" all lower case

    /**
     * PRIVATE CONSTRUCTOR
     */
    private Tournament() {
        humanScore = 0;
        computerScore = 0;
    }

    /**
     * Gets Human Score in the tournament
     * @return human score
     */
    public static int GetHumanScore() {
        return humanScore;
    }

    /**
     * Gets computer score in the tournament
     * @return computer score
     */
    public static int GetComputerScore() {
        return computerScore;
    }

    /**
     * Gets next player in case if the tournament is resumed from a saved state
     * @return Next Player
     */
    public static String GetNextPlayer() {
        return nextPlayer;
    }

    /**
     * Increments Human Score by certain points
     * @param bumpScoreBy Value to bump Human score by
     */
    public static void IncrementHumanScoreBy(int bumpScoreBy) {
        humanScore += bumpScoreBy;
    }

    /**
     * Increments Computer Score by certain points
     * @param bumpScoreBy Value to bump Computer score by
     */
    public static void IncrementComputerScoreBy(int bumpScoreBy) {
        computerScore += bumpScoreBy;
    }

    /**
     * Resets scores for a fresh start
     */
    public static void ResetScores() {
        humanScore = 0;
        computerScore = 0;
    }

    /**
     * Sets next player
     * @param player the next player ("computer" or "human")
     */
    public static void SetNextPlayer(String player) {
        if (player == "computer" || player == "Computer") {
            nextPlayer = "computer";
        }
        else {
            nextPlayer = "human";
        }
    }
}