package com.viveckh.duell;

import java.util.Vector;

/**
 * Created by ZCV0LHB on 11/3/2016.
 */
public final class Notifications {
    //variables declaration
    private static Vector<String> notificationsList;
    private final static int SUM_OF_OPPOSITE_SIDES = 7;

    // Private constructor
    private Notifications() {
        notificationsList = new Vector<>(100);
    }

    // Gets notification list
    public static Vector GetNotificationsList() {
        return notificationsList;
    }

    // clears notification list
    public static void ClearNotificationsList() {
        notificationsList.clear();
    }

    // Improper input error msg
    public static void Msg_ImproperInput() {
        notificationsList.add("ERROR:\t\t Why you annoying me with improper inputs? Go, Try again!\n");
    }

    // Input out of bounds error msg
    public static void Msg_InputOutOfBounds() {
        notificationsList.add("ERROR:\t\t Input co-ordinates out of bound. *Rolls eyes* Try again!\n");
    }

    // Moving another player's dice error msg
     public static void Msg_WrongDice() {
        notificationsList.add("ERROR:\t\t Woah! Foul! That ain't your dice to move homie!\n");
    }

    // No dice to move error msg
    public static void Msg_NoDiceToMove() {
        notificationsList.add("ERROR:\t\t Don't you see there is no dice to move in that co-ordinate?\n");
    }

    // Invalid move error msg
    public static void Msg_InvalidMove() {
        notificationsList.add("ERROR:\t\t Why you always trying Invalid Moves?\n");
    }

    // Trying to run own dice error msg
    public static void Msg_RunningOverOwnDice() {
        notificationsList.add("MSG:\t\t Are you really trying to capture your own dice, bonehead?\n");
    }

    // Captured opponent msg
    public static void Msg_CapturedAnOpponent() {
        notificationsList.add("\nMSG:\t\t You just captured an opponent dice. Impressive for a Knucklehead? Eh!\n");
    }

    // No valid path msg
    public static void Msg_NoValidPath() {
        notificationsList.add("ERROR:\t\t Yo numskull! NO Valid Path was found to get to your selected destination. BOOO!\n");
    }

    // 90 degree turns re-routed msg
    public static void Msg_90DegreePathSelectionNotProcessed() {
        notificationsList.add("MSG:\t\t We're sorry, but your DUMB path selection for the 90 Degree turn was Invalid.\n");
        notificationsList.add("\t\t So, we - the smart bots species - automatically chose the alternate route for you.\n");
    }

    // Nature of path taken msg
    public static void Msg_NatureOfPathTaken(String path) {
        notificationsList.add("MSG:\t\t A " + path + " path was taken to get to the destination\n");
    }

    // Displaying start and end coordinates of the move
    public static void Msg_MoveDescription(int startRow, int startCol, int endRow, int endCol, int topValueAtStart, int rightValueAtStart, int topValueAtEnd, int rightValueAtEnd, boolean isBotOperated) {
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
        notificationsList.add("\t\t There were " + Math.abs(startRow - endRow) + " vertical rolls & " + Math.abs(startCol - endCol) + " horizontal rolls made.\n");
    }

    // Crash msg
    public static void Msg_CrashedWhileMakingTheMove() {
        notificationsList.add("ERROR:\t\t Whoopsie Daisy! The program crashed while making the move.\n");
    }

    // No msg
    public static void Msg_NoMsg() {}

    // Displays results of the tournament
    public static void Msg_DisplayResults(int botScore, int humanScore) {
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
    }

    // Draws a divider line
    public static void DrawDivider() {
        notificationsList.add("\n\n-*-*-*-*-********************************************-*-*-*-*-\n \n");
    }

    // Notifying the game is over
    public static void Msg_GameOver(String winner) {
        notificationsList.add("\n-*-*-*-*-**********" + winner + " WON**********************************-*-*-*-*-\n");
    }

    // Serialization failed and exit
    public static void Msg_SerializednExited(String status) {
        notificationsList.add("Serialization " +  status + " . The game will exit now.\n");
        DrawDivider();
    }

	/*
		THE FOLLOWING FUNCTIONS ARE ESPECIALLY MEANT TO GUIDE THE USER THROUGH COMPUTER'S THOUGHT PROCESS
	*/

    // Bot trying to capture opponent key pieces/squares msg
    public static void BotsThink_TryingToCaptureOpponentKeys() {
        notificationsList.add("Bots Mumbling:\t Trying to capture opponent's King or KeySquare...\n");
    }

    // Msg notifying that the safety of key pieces/squares are being taken care of
    public static void BotsThink_CheckingKingNKeySquareSafety() {
        notificationsList.add("Bots Mumbling:\t Monitoring territory to ensure the King & KeySquare are safe...\n");
    }

    // Key Threat detected msg
    public static void BotsThink_KeyThreatDetected(String whosUnderThreat) {
        notificationsList.add("Bots Mumbling:\t Imminent threat has been detected for the " + whosUnderThreat + "\n");
    }

    // hostile opponent captured msg
    public static void BotsThink_HostileOpponentCaptured(String whosUnderThreat) {
        notificationsList.add("Bots Mumbling:\t That hostile opponent aiming to attack our " + whosUnderThreat + " has been captured.\n");
    }

    // hostile opponent not capturable msg
    public static void BotsThink_HostileOpponentUncapturable(String whosUnderThreat) {
        notificationsList.add("Bots Mumbling:\t That hostile opponent aiming to attack " + whosUnderThreat + " couldn't be captured. Trying alternatives...\n");
    }

    // Blocking move successful msg
    public static void BotsThink_BlockingMoveMade() {
        notificationsList.add("Bots Mumbling:\t A Blocking move was successfully made to obstruct the hostile opponent.\n");
    }

    // Blocking move not successful msg
    public static void BotsThink_BlockingMoveNotPossible() {
        notificationsList.add("Bots Mumbling:\t A Blocking move wasn't possible at this time. Trying other options...\n");
    }

    // King relocation successful msg
    public static void BotsThink_KingMoved() {
        notificationsList.add("Bots Mumbling:\t The king has been moved and the threat has been averted for now.\n");
    }

    // King move unsafe msg
    public static void BotsThink_UnsafeToMoveKing() {
        notificationsList.add("Bots Mumbling:\t No safe surroundings to move the king. The humans have trapped our King.\n");
    }

    // Trying to capture opponent msg
    public static void BotsThink_TryingToCaptureOpponentDice() {
        notificationsList.add("Bots Mumbling:\t Looking for any vulnerable opponent dice to capture at this point...\n");
    }

    // Captured opponent msg
    public static void BotsThink_CapturedOpponentDice() {
        notificationsList.add("Bots Mumbling:\t We captured an opponent die.\n");
    }

    // Checking to see if any of own dice are threatened by opponent dices
    public static void BotsThink_ProtectDicesFromPotentialCaptures() {
        notificationsList.add("Bots Mumbling:\t Checking if any of the own dices are under threat of being captured by opponent...\n");
    }

    // searching an ordinary move msg
    public static void BotsThink_SearchingOrdinaryMove() {
        notificationsList.add("Bots Mumbling:\t Examining possible moves to get closer to the opponent king/keysquare...\n");
    }

    // Help mode on msg
    public static void Msg_HelpModeOn() {
        notificationsList.add("\nHELP MODE ACTIVATED!\n");
    }

    // Prints the move recommended by the computer
    public static void Msg_HelpModeRecommendedMove(int startRow, int startCol, int endRow, int endCol, int pathChoice) {
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
    }




}
