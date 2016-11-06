package com.viveckh.duell;

import java.util.Vector;

/**
 * Created by ZCV0LHB on 11/3/2016.
 */
public final class Notifications {
    //variables declaration
    private static Vector<String> notificationsList = new Vector<>(100);
    private final static int SUM_OF_OPPOSITE_SIDES = 7;

    // Private constructor
    private Notifications() {
    }

    // Gets notification list
    public static Vector GetNotificationsList() {
        return notificationsList;
    }

    // clears notification list
    public static boolean ClearNotificationsList() {
        if (notificationsList != null && !notificationsList.isEmpty()) {
            notificationsList.clear();
        }
        return true;
    }

    // Improper input error msg
    public static boolean Msg_ImproperInput() {
        notificationsList.add("ERROR:\t\t Why you annoying me with improper inputs? Go, Try again!\n");
        return true;
    }

    // Input out of bounds error msg
    public static boolean Msg_InputOutOfBounds() {
        notificationsList.add("ERROR:\t\t Input co-ordinates out of bound. *Rolls eyes* Try again!\n");
        return true;
    }

    // Moving another player's dice error msg
     public static boolean Msg_WrongDice() {
        notificationsList.add("ERROR:\t\t Woah! Foul! That ain't your dice to move homie!\n");
         return true;
     }

    // No dice to move error msg
    public static boolean Msg_NoDiceToMove() {
        notificationsList.add("ERROR:\t\t Don't you see there is no dice to move in that co-ordinate?\n");
        return true;
    }

    // Invalid move error msg
    public static boolean Msg_InvalidMove() {
        notificationsList.add("ERROR:\t\t Why you always trying Invalid Moves?\n");
        return true;
    }

    // Trying to run own dice error msg
    public static boolean Msg_RunningOverOwnDice() {
        notificationsList.add("MSG:\t\t Are you really trying to capture your own dice, bonehead?\n");
        return true;
    }

    // Captured opponent msg
    public static boolean Msg_CapturedAnOpponent() {
        notificationsList.add("MSG:\t\t You just captured an opponent dice. Impressive for a Knucklehead? Eh!\n");
        return true;
    }

    // No valid path msg
    public static boolean Msg_NoValidPath() {
        notificationsList.add("ERROR:\t\t Yo numskull! NO Valid Path was found to get to your selected destination. BOOO!\n");
        return true;
    }

    // 90 degree turns re-routed msg
    public static boolean Msg_90DegreePathSelectionNotProcessed() {
        notificationsList.add("MSG:\t\t We're sorry, but your DUMB path selection for the 90 Degree turn was Invalid.\n");
        notificationsList.add("\t\t So, we - the smart bots species - automatically chose the alternate route for you.\n");
        return true;
    }

    // Nature of path taken msg
    public static boolean Msg_NatureOfPathTaken(String path) {
        notificationsList.add("MSG:\t\t A " + path + " path was taken to get to the destination\n");
        return true;
    }

    // Displaying start and end coordinates of the move
    public static boolean Msg_MoveDescription(int startRow, int startCol, int endRow, int endCol, int topValueAtStart, int rightValueAtStart, int topValueAtEnd, int rightValueAtEnd, boolean isBotOperated) {
        char player;
        if (isBotOperated) {
            player = 'C';
            rightValueAtStart = SUM_OF_OPPOSITE_SIDES - rightValueAtStart;
            rightValueAtEnd = SUM_OF_OPPOSITE_SIDES - rightValueAtEnd;
        }
        else {
            player = 'H';
        }

        notificationsList.add("ACTION:\t\t The dice " + player + topValueAtStart + rightValueAtStart + " in (" + startRow + ", " + startCol + ") was moved to (" + endRow + ", " + endCol + "). It is now " + player + topValueAtEnd + rightValueAtEnd + "\n");
        notificationsList.add("\t\t\t There were " + Math.abs(startRow - endRow) + " vertical rolls & " + Math.abs(startCol - endCol) + " horizontal rolls made.\n");
        return true;
    }

    // Crash msg
    public static boolean Msg_CrashedWhileMakingTheMove() {
        notificationsList.add("ERROR:\t\t Whoopsie Daisy! The program crashed while making the move.\n");
        return true;
    }

    // No msg
    public static boolean Msg_NoMsg() { return true; }

    // Displays results of the tournament
    public static boolean Msg_DisplayResults(int botScore, int humanScore) {
        notificationsList.add("***************************************************************\n");
        notificationsList.add("\t\tTournament Results\n");
        notificationsList.add("***************************************************************\n");
        notificationsList.add("Computer Wins:	" + botScore + "\n");
        notificationsList.add("Human Wins: " + humanScore + "\n");
        if (botScore > humanScore) {
            notificationsList.add("The Computer Won the Tournament. *reinforcing the notion once again that we bots are better than you humans*\n");
        }
        else if (humanScore > botScore) {
            notificationsList.add("Congratulations! You won the Tournament. Our programmer must've done a terrible job on algorithms for someone like you to win.\n");
        }
        else {
            notificationsList.add("It was a draw. Guess we'll see who's better in the next tournament.\n");
        }
        return true;
    }

    // Draws a divider line
    public static boolean DrawDivider() {
        notificationsList.add("\n\n-*-*-*-*-********************************************-*-*-*-*-\n \n");
        return true;
    }

    // Notifying the game is over
    public static boolean Msg_GameOver(String winner) {
        notificationsList.add("\n-*-*-*-*-**********" + winner + " WON**********************************-*-*-*-*-\n");
        return true;
    }

    // Serialization failed and exit
    public static boolean Msg_SerializednExited(String status) {
        notificationsList.add("Serialization " +  status + " . The game will exit now.\n");
        DrawDivider();
        return true;
    }

	/*
		THE FOLLOWING FUNCTIONS ARE ESPECIALLY MEANT TO GUIDE THE USER THROUGH COMPUTER'S THOUGHT PROCESS
	*/

    // Bot trying to capture opponent key pieces/squares msg
    public static boolean BotsThink_TryingToCaptureOpponentKeys() {
        notificationsList.add("Bots Mumbling:\t Trying to capture opponent's King or KeySquare...\n");
        return true;
    }

    // Msg notifying that the safety of key pieces/squares are being taken care of
    public static boolean BotsThink_CheckingKingNKeySquareSafety() {
        notificationsList.add("Bots Mumbling:\t Monitoring territory to ensure the King & KeySquare are safe...\n");
        return true;
    }

    // Key Threat detected msg
    public static boolean BotsThink_KeyThreatDetected(String whosUnderThreat) {
        notificationsList.add("Bots Mumbling:\t Imminent threat has been detected for the " + whosUnderThreat + "\n");
        return true;
    }

    // hostile opponent captured msg
    public static boolean BotsThink_HostileOpponentCaptured(String whosUnderThreat) {
        notificationsList.add("Bots Mumbling:\t That hostile opponent aiming to attack our " + whosUnderThreat + " has been captured.\n");
        return true;
    }

    // hostile opponent not capturable msg
    public static boolean BotsThink_HostileOpponentUncapturable(String whosUnderThreat) {
        notificationsList.add("Bots Mumbling:\t That hostile opponent aiming to attack " + whosUnderThreat + " couldn't be captured. Trying alternatives...\n");
        return true;
    }

    // Blocking move successful msg
    public static boolean BotsThink_BlockingMoveMade() {
        notificationsList.add("Bots Mumbling:\t A Blocking move was successfully made to obstruct the hostile opponent.\n");
        return true;
    }

    // Blocking move not successful msg
    public static boolean BotsThink_BlockingMoveNotPossible() {
        notificationsList.add("Bots Mumbling:\t A Blocking move wasn't possible at this time. Trying other options...\n");
        return true;
    }

    // King relocation successful msg
    public static boolean BotsThink_KingMoved() {
        notificationsList.add("Bots Mumbling:\t The king has been moved and the threat has been averted for now.\n");
        return true;
    }

    // King move unsafe msg
    public static boolean BotsThink_UnsafeToMoveKing() {
        notificationsList.add("Bots Mumbling:\t No safe surroundings to move the king. The humans have trapped our King.\n");
        return true;
    }

    // Trying to capture opponent msg
    public static boolean BotsThink_TryingToCaptureOpponentDice() {
        notificationsList.add("Bots Mumbling:\t Looking for any vulnerable opponent dice to capture at this point...\n");
        return true;
    }

    // Captured opponent msg
    public static boolean BotsThink_CapturedOpponentDice() {
        notificationsList.add("Bots Mumbling:\t We captured an opponent die.\n");
        return true;
    }

    // Checking to see if any of own dice are threatened by opponent dices
    public static boolean BotsThink_ProtectDicesFromPotentialCaptures() {
        notificationsList.add("Bots Mumbling:\t Checking if any of the own dices are under threat of being captured by opponent...\n");
        return true;
    }

    // searching an ordinary move msg
    public static boolean BotsThink_SearchingOrdinaryMove() {
        notificationsList.add("Bots Mumbling:\t Examining possible moves to get closer to the opponent king/keysquare...\n");
        return true;
    }

    // Help mode on msg
    public static boolean Msg_HelpModeOn() {
        notificationsList.add("HELP MODE ACTIVATED!\n");
        return true;
    }

    // Prints the move recommended by the computer
    public static boolean Msg_HelpModeRecommendedMove(int startRow, int startCol, int endRow, int endCol, int pathChoice) {
        notificationsList.add("\nRECOMMENDED:\t Move the dice in square (" + startRow + ", " + startCol + ") to (" + endRow + ", " + endCol + ") using a ");
        switch (pathChoice) {
            case 1:
                notificationsList.add("Vertical then Lateral Path\n");
                break;
            case 2:
                notificationsList.add("Lateral then Vertical Path\n");
                break;
            case 3:
                notificationsList.add("Vertical Path\n");
                break;
            case 4:
                notificationsList.add("Lateral Path\n");
                break;
            default:
                break;
        }
        notificationsList.add("\nHELP MODE DEACTIVATED!\n");
        return true;
    }




}
