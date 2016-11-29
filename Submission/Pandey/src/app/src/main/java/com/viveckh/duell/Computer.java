package com.viveckh.duell;

import java.util.Arrays;

/**
 * Computer Class
 * Implements Computer strategies to evaluate, prioritize, select and initiate the best move on behalf of the computer.
 * Author: Vivek Pandey
 * Project: Duell in Java Android
 * Class: CMPS 366
 * Last Modified on: 11/27/2016
 */
public class Computer extends Player {
    // CONSTANTS, VARIABLES AND OBJECT DECLARATIONS
    final private int TEAMSIZE = 9;
    private boolean helpModeOn;

    private Dice[] ownDiceList = new Dice[9];
    private Dice[] opponentDiceList = new Dice[9];
    private Square ownKingSquare, ownKeySquare;
    private Square opponentKingSquare, opponentKeySquare;

    /**
     * DEFAULT CONSTRUCTOR - Initializes the local copies of dice arrays needed for move calculations, sets help mode off.
     */
    public Computer() {
        helpModeOn = false;
        //Initializing dice array lists
        for (int count = 0; count < 9; count++) {
            ownDiceList[count] = new Dice();
            opponentDiceList[count] = new Dice();
        }
    }

    /**
     * Prioritizes, Calculates and makes the proper move for Computer on its turn
     * @param board The board in context where the move needs to be made
     * @param helpModeOn true if user is seeking for help using computer's algorithm; false otherwise
     * @return true if a move was made successfully; false otherwise
     */
    public boolean Play(Board board, boolean helpModeOn) {
        this.helpModeOn = helpModeOn;
        printNotifications = false;

        // Moving contents to a temporary board to prevent unintentional modification of actual gameboard during calculations
        Board calculationBoard = new Board(board);

        // If help mode on, the algorithm will work favorably for human and against the computer
        if (helpModeOn) {
            // Setting Human as the owner and bot as the opponent
            ownDiceList =  Arrays.copyOf(calculationBoard.humans, calculationBoard.humans.length);
            opponentDiceList = Arrays.copyOf(calculationBoard.bots, calculationBoard.bots.length);
            ownKingSquare = new Square(calculationBoard.GetSquareAtLocation(calculationBoard.GetHumanKing().GetRow(), calculationBoard.GetHumanKing().GetColumn()));
            ownKeySquare = new Square(calculationBoard.GetSquareAtLocation(0, 4));
            opponentKingSquare = new Square(calculationBoard.GetSquareAtLocation(calculationBoard.GetBotKing().GetRow(), calculationBoard.GetBotKing().GetColumn()));
            opponentKeySquare = new Square(calculationBoard.GetSquareAtLocation(7, 4));
        }
        else {
            // Setting Bot as the owner and human as the opponent
            ownDiceList =  Arrays.copyOf(calculationBoard.bots, calculationBoard.bots.length);
            opponentDiceList = Arrays.copyOf(calculationBoard.humans, calculationBoard.humans.length);
            ownKingSquare = new Square(calculationBoard.GetSquareAtLocation(calculationBoard.GetBotKing().GetRow(), calculationBoard.GetBotKing().GetColumn()));
            ownKeySquare = new Square(calculationBoard.GetSquareAtLocation(7, 4));
            opponentKingSquare = new Square(calculationBoard.GetSquareAtLocation(calculationBoard.GetHumanKing().GetRow(), calculationBoard.GetHumanKing().GetColumn()));
            opponentKeySquare = new Square(calculationBoard.GetSquareAtLocation(0, 4));
        }

        int index;

        Notifications.BotsThink_TryingToCaptureOpponentKeys();
        // STEP 1: Check if the opponent's king or key square can be captured. If yes, go for it!
        for (index = 0; index < TEAMSIZE; index++) {
            if (!ownDiceList[index].IsCaptured()) {
                // Try to capture the king die
                if (MakeAMove(ownDiceList[index].GetRow(), ownDiceList[index].GetColumn(), opponentKingSquare.GetRow(), opponentKingSquare.GetColumn(), board, helpModeOn, 0)) {
                    return true;
                }
                // Try to capture the key square by the king die
                if (ownDiceList[index].IsKing()) {
                    if (MakeAMove(ownDiceList[index].GetRow(), ownDiceList[index].GetColumn(), opponentKeySquare.GetRow(), opponentKeySquare.GetColumn(), board, helpModeOn, 0)) {
                        return true;
                    }
                }
            }
        }

        Notifications.BotsThink_CheckingKingNKeySquareSafety();
        // STEP 2: Check if own king or keysquare is under potential attack. If yes, Save Em'
        for (index = 0; index < TEAMSIZE; index++) {
            if (!opponentDiceList[index].IsCaptured()) {
                // ATTENTION: If both kingSquare and KeySquare are under threat, then blocking is the best way to go about it

                //Check if KingSquare is under imminent threat
                if (IsValidDestination(opponentDiceList[index], ownKingSquare)) {
                    if (IsPathValid(opponentDiceList[index], ownKingSquare, calculationBoard)) {
                        // King is under imminent threat
                        Notifications.BotsThink_KeyThreatDetected("King");

                        // First, Try capturing the hostile opponent
                        if (TryCapturingTheHostileOpponent(opponentDiceList[index], board)) {
                            printStatus = helpModeOn ? Notifications.Msg_NoMsg() : Notifications.BotsThink_HostileOpponentCaptured("King");
                            return true;
                        }
                        else {
                            Notifications.BotsThink_HostileOpponentUncapturable("King");
                        }

                        // Second, Try blocking the hostile opponent
                        if (TryBlockingAttack(opponentDiceList[index], ownKingSquare, board)) {
                            printStatus = helpModeOn ? Notifications.Msg_NoMsg() : Notifications.BotsThink_BlockingMoveMade();
                            return true;
                        }
                        else {
                            Notifications.BotsThink_BlockingMoveNotPossible();
                        }


                        // Third, Try moving the king as a last resort and make sure the new position is safe
                        if (TryMovingKing(ownKingSquare, board)) {
                            printStatus = helpModeOn ? Notifications.Msg_NoMsg() : Notifications.BotsThink_KingMoved();
                            return true;
                        }
                        else {
                            Notifications.BotsThink_UnsafeToMoveKing();
                        }
                    }
                }

                // SAFETY OF THE KEY SQUARE HAS BEEN TAKEN CARE IN ABOVE STEPS ALREADY
                // Opponent king is the only threat to the key square
                // And capture of opponent king has already been tried above and Blocking the hostile king can't really be done since it will be right next to the keysquare if a threat)

            }
        }

        // STEP 3: Try to capture any vulnerable opponent dice in the game board
        // We will not send king to capture opponents to make sure king is safe from opponent's trap, king will only capture opponent king which is facilitated above
        Notifications.BotsThink_TryingToCaptureOpponentDice();
        for (index = 0; index < TEAMSIZE; index++) {
            // Use the die to hunt only if it is not a king and hasn't been captured yet
            if (!ownDiceList[index].IsKing() && !ownDiceList[index].IsCaptured()) {
                for (int jindex = 0; jindex < TEAMSIZE; jindex++) {
                    if (!opponentDiceList[jindex].IsCaptured()) {
                        if (MakeAMove(ownDiceList[index].GetRow(), ownDiceList[index].GetColumn(), opponentDiceList[jindex].GetRow(), opponentDiceList[jindex].GetColumn(), board, helpModeOn, 0)) {
                            printStatus = helpModeOn ? Notifications.Msg_NoMsg() : Notifications.BotsThink_CapturedOpponentDice();
                            return true;
                        }
                    }
                }
            }
        }

        // STEP 4: Protect any own dice that might be potentially captured by the opponent in the next step
        Notifications.BotsThink_ProtectDicesFromPotentialCaptures();
        // for all uncaptured opponent dices
        for (index = 0; index < TEAMSIZE; index++) {
            if (!opponentDiceList[index].IsCaptured()) {
                // go through all of own uncaptured dices and check chances of hostile takeover
                for (int counter = 0; counter < TEAMSIZE; counter++) {
                    if (!ownDiceList[counter].IsCaptured()) {
                        if (IsValidDestination(opponentDiceList[index], calculationBoard.GetSquareAtLocation(ownDiceList[counter].GetRow(), ownDiceList[counter].GetColumn()))) {
                            if (IsPathValid(opponentDiceList[index], calculationBoard.GetSquareAtLocation(ownDiceList[counter].GetRow(), ownDiceList[counter].GetColumn()), calculationBoard)) {
                                if (ProtectTheDice(calculationBoard.GetSquareAtLocation(ownDiceList[counter].GetRow(), ownDiceList[counter].GetColumn()), board)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }


        // STEP 5: Go through the remaining possibilities and move a die with intentions of getting it closer to the king or key square.

        // Variables to store the best combination of origin and destination co-ordinates for a move, and also the minDistance that has been attained.
        int startRow = 0, startCol = 0, endRow = 0, endCol = 0;

        int minDistance = 99;
        int distanceFromFinalDestination = 99;

        Notifications.BotsThink_SearchingOrdinaryMove();
        //For each of the die, go through every square in the gameboard and find the most optimal square to move in current state
        for (index = 0; index < TEAMSIZE; index++) {
            if (!ownDiceList[index].IsKing() && !ownDiceList[index].IsCaptured()) {						// For every uncaptured soldier die
                for (int row = 0; row < 8; row++) {																			// Go through the entire board and
                    for (int col = 0; col < 9; col++) {
                        if (IsValidDestination(ownDiceList[index], calculationBoard.GetSquareAtLocation(row, col))) {			// Check if valid dest
                            if (IsPathValid(ownDiceList[index], calculationBoard.GetSquareAtLocation(row, col), board)) {		// Check if valid path
                                if (!IsInDanger(board.GetSquareAtLocation(row, col), board)) {											// Check if safe
                                    // Compare distance to get to the king square from new location
                                    distanceFromFinalDestination = Math.abs(opponentKingSquare.GetRow() - row) + Math.abs(opponentKingSquare.GetColumn() - col);
                                    if (distanceFromFinalDestination < minDistance) {													// Check if distance to key becomes minimum & then assign
                                        minDistance = distanceFromFinalDestination;
                                        startRow = ownDiceList[index].GetRow();
                                        startCol = ownDiceList[index].GetColumn();
                                        endRow = row;
                                        endCol = col;
                                    }

                                    //Compare distance to get to the key square from new location
                                    distanceFromFinalDestination = Math.abs(opponentKeySquare.GetRow() - row) + Math.abs(opponentKeySquare.GetColumn() - col);
                                    if (distanceFromFinalDestination < minDistance) {													// Check if distance to key becomes minimum & then assign
                                        minDistance = distanceFromFinalDestination;
                                        startRow = ownDiceList[index].GetRow();
                                        startCol = ownDiceList[index].GetColumn();
                                        endRow = row;
                                        endCol = col;
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }

        // If a better path was found from the above intensive checking
        if (minDistance < 99) {
            if (MakeAMove(startRow, startCol, endRow, endCol, board, helpModeOn, 0)) {
                return true;
            }
        }
        //It won't ever come to this, but return true anyway
        return true;
    }

    /**
     * Attempts to block the path of an opponent who is a potential threat; create duplicates of Dice and Square function params
     * @param hostileOne The hostile die
     * @param victim The square which is a potential victim of the hostile die
     * @param board The game board in context
     * @return true if a blocking move was successfully made, false otherwise
     */
    private boolean TryBlockingAttack(Dice hostileOne, Square victim, Board board) {
        // Duplicating some function params to prevent the modification of original reference
        Dice hostileDice = new Dice(hostileOne);
        Square squareToProtect = new Square(victim);

        //Get the path choice first
        IsPathValid(hostileDice, squareToProtect, board);
        int path = GetPathChoice();

        //Then based on that path, check which coordinate is best suited to jam the route
        switch (path)
        {
            // First vertically, a 90 degree turn, then laterally
            case 1:
                if (FindBlockPointVertically(hostileDice, squareToProtect, board)) return true;
                if (FindBlockPointLaterally(hostileDice, squareToProtect, board)) return true;
                break;
            // First laterally, a 90 degree turn, then vertically
            case 2:
                if (FindBlockPointLaterally(hostileDice, squareToProtect, board)) return true;
                if (FindBlockPointVertically(hostileDice, squareToProtect, board)) return true;
                break;
            // Vertically only
            case 3:
                if (FindBlockPointVertically(hostileDice, squareToProtect, board)) return true;
                break;
            // laterally only
            case 4:
                if (FindBlockPointLaterally(hostileDice, squareToProtect, board)) return true;
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * Finds a co-ordinate to place blocking dice on the vertical route, and makes the move if possible
     * @param hostileDice The hostile die that is a potential threat
     * @param squareToProtect The square that needs protection
     * @param board The game board in context
     * @return True if a blocking point was found along the vertical path and a move was successfully made; false otherwise
     */
    private boolean FindBlockPointVertically(Dice hostileDice, Square squareToProtect, Board board) {
        do {
            //Bump up/down the coordinate to check first
            if (hostileDice.GetRow() < squareToProtect.GetRow()) {
                hostileDice.SetRow(1, true);
            }
            else {
                hostileDice.SetRow(1, false);
            }

            // See if any of the own dies can take that spot and block
            for (int i = 0; i < TEAMSIZE; i++) {
                if (!ownDiceList[i].IsKing() && !ownDiceList[i].IsCaptured()) {
                    if (MakeAMove(ownDiceList[i].GetRow(), ownDiceList[i].GetColumn(), hostileDice.GetRow(), hostileDice.GetColumn(), board, helpModeOn, 0)) {
                        return true;
                    }
                }
            }
        } while (hostileDice.GetRow() != squareToProtect.GetRow());
        return false;
    }

    /**
     * Finds a co-ordinate to place blocking dice on the lateral route, and makes the move if possible
     * @param hostileDice The hostile die that is a potential threat
     * @param squareToProtect The square that needs protection
     * @param board The game board in context
     * @return True if a blocking point was found along the lateral path and a move was successfully made; false otherwise
     */
    private boolean FindBlockPointLaterally(Dice hostileDice, Square squareToProtect, Board board) {
        do {
            //Bump up/down the coordinate to check first
            if (hostileDice.GetColumn() < squareToProtect.GetColumn()) {
                hostileDice.SetColumn(1, true);
            }
            else {
                hostileDice.SetColumn(1, false);
            }

            // See if any of the own dies can take that spot and block
            for (int i = 0; i < TEAMSIZE; i++) {
                if (!ownDiceList[i].IsKing() && !ownDiceList[i].IsCaptured()) {
                    if (MakeAMove(ownDiceList[i].GetRow(), ownDiceList[i].GetColumn(), hostileDice.GetRow(), hostileDice.GetColumn(), board, helpModeOn, 0)) {
                        return true;
                    }
                }
            }
        } while (hostileDice.GetColumn() != squareToProtect.GetColumn());
        return false;
    }

    /**
     * Tries capturing a hostile opponent who is a potential threat; create duplicate copy of dice function param to prevent original modification
     * @param hostileOne The hostile die that is a potential threat
     * @param board The game board in context
     * @return true if the hostile opponent can be captured; false otherwise
     */
    private boolean TryCapturingTheHostileOpponent(Dice hostileOne, Board board) {
        //Duplicating function params to prevent modification of original params
        Dice hostileDice = new Dice(hostileOne);

        for (int i = 0; i < TEAMSIZE; i++) {
            if (!ownDiceList[i].IsCaptured()) {
                // Try to capture the king die
                if (MakeAMove(ownDiceList[i].GetRow(), ownDiceList[i].GetColumn(), hostileDice.GetRow(), hostileDice.GetColumn(), board, helpModeOn, 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Tries moving the king to a secure position; create duplicate copy of Square king function param for modification
     * @param king The king dice that needs security
     * @param board The game board in context
     * @return true if king can be moved to a secure location; false otherwise
     */
    private boolean TryMovingKing(Square king, Board board) {
        Square kingSquare = new Square(king);

        // Check if it is possible/safe to move the king upwards in the board
        if (kingSquare.GetRow() < 7) {
            if (IsValidDestination(kingSquare.GetResident(), board.GetSquareAtLocation(kingSquare.GetRow() + 1, kingSquare.GetColumn()))) {
                if (!IsInDanger(board.GetSquareAtLocation(kingSquare.GetRow() + 1, kingSquare.GetColumn()), board)) {
                    if (MakeAMove(kingSquare.GetRow(), kingSquare.GetColumn(), kingSquare.GetRow() + 1, kingSquare.GetColumn(), board, helpModeOn, 0)) {
                        return true;
                    }
                }
            }
        }

        // Check if it is possible/safe to move the king downwards in the board
        if (kingSquare.GetRow() > 0) {
            if (IsValidDestination(kingSquare.GetResident(), board.GetSquareAtLocation(kingSquare.GetRow() - 1, kingSquare.GetColumn()))) {
                if (!IsInDanger(board.GetSquareAtLocation(kingSquare.GetRow() - 1, kingSquare.GetColumn()), board)) {
                    if (MakeAMove(kingSquare.GetRow(), kingSquare.GetColumn(), kingSquare.GetRow() - 1, kingSquare.GetColumn(), board, helpModeOn, 0)) {
                        return true;
                    }
                }
            }
        }

        // Check if it is possible/safe to move the king rightwards in the board
        if (kingSquare.GetColumn() < 8) {
            if (IsValidDestination(kingSquare.GetResident(), board.GetSquareAtLocation(kingSquare.GetRow(), kingSquare.GetColumn() + 1))) {
                if (!IsInDanger(board.GetSquareAtLocation(kingSquare.GetRow(), kingSquare.GetColumn() + 1), board)) {
                    if (MakeAMove(kingSquare.GetRow(), kingSquare.GetColumn(), kingSquare.GetRow(), kingSquare.GetColumn() + 1, board, helpModeOn, 0)) {
                        return true;
                    }
                }
            }
        }

        // Check if it is possible/safe to move the king leftwards in the board
        if (kingSquare.GetColumn() > 0) {
            if (IsValidDestination(kingSquare.GetResident(), board.GetSquareAtLocation(kingSquare.GetRow(), kingSquare.GetColumn() - 1))) {
                if (!IsInDanger(board.GetSquareAtLocation(kingSquare.GetRow(), kingSquare.GetColumn() - 1), board)) {
                    if (MakeAMove(kingSquare.GetRow(), kingSquare.GetColumn(), kingSquare.GetRow(), kingSquare.GetColumn() - 1, board, helpModeOn, 0)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Tries protecting a dice under threat; create duplicate copy of Square function param for modification
     * @param potentialVictim The square whose resident needs to be checked for potential vulnerability
     * @param board The game board in context
     * @return true if the dice can be protected; false otherwise
     */
    private boolean ProtectTheDice(Square potentialVictim, Board board) {
        //Making duplicate copies of necessary function params for modification
        Square squareAtRisk = new Square(potentialVictim);

        // Go through the entire game board, find a location where the squareAtRisk will be safe and move it there
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 9; col++) {
                if (IsValidDestination(squareAtRisk.GetResident(), board.GetSquareAtLocation(row, col))) {
                    if (IsPathValid(squareAtRisk.GetResident(), board.GetSquareAtLocation(row, col), board)) {
                        if (!IsInDanger(board.GetSquareAtLocation(row, col), board)) {
                            if (MakeAMove(squareAtRisk.GetRow(), squareAtRisk.GetColumn(), row, col, board, helpModeOn, 0)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if a given square is at risk from opponent dices; create duplicate copies of function param
     * @param potentialVictim The square whose resident needs to be checked for potential vulnerability
     * @param gameBoard The game board in context
     * @return true if the resident in the square is in danger; false otherwise
     */
    private boolean IsInDanger(Square potentialVictim, Board gameBoard) {
        //Making duplicate copies of function params that were supposed to be passed by value
        Square squareAtRisk = new Square(potentialVictim);
        Board board = new Board(gameBoard);

        for (int index = 0; index < TEAMSIZE; index++) {
            // This even considers threat from the king and just not normal soldier attacks
            if (!opponentDiceList[index].IsCaptured()) {
                if (IsValidDestination(opponentDiceList[index], squareAtRisk)) {
                    if (IsPathValid(opponentDiceList[index], squareAtRisk, board)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}