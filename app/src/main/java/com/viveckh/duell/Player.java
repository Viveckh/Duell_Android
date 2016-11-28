package com.viveckh.duell;

/**
 * Player Class
 * Implements the basic set of strategies for any player like traversing the board and determining path choices, validating and processing moves,
 * changing dice orientation based on the rolls, capturing and eliminating opponent dices.
 * Author: Vivek Pandey
 * Last Modified on: 11/27/2016
 */
public class Player {
    // VARIABLES & OBJECTS DECLARATIONS
    private int pathChoice;	// Choice of what type of path to take out of available four types
    private boolean multiplePathPossible;

    // Variables for Player Strategies
    private int tempStorage1;
    private int tempStorage2;

    // Counting variables for traversals to reset back the die position
    private int counterRowsTraversed;
    private int counterColumnsTraversed;
    
    // Temporary variable to receive result for tertiary operators
    protected boolean printStatus;
    protected static boolean printNotifications = true;

    /**
     * DEFAULT CONSTRUCTOR
     */
    public Player() {
        pathChoice = 0;
        multiplePathPossible = false;
        tempStorage1 = 0;
        tempStorage2 = 0;
        counterRowsTraversed = 0;
        counterColumnsTraversed = 0;
    }

    /**
     * Gets path choice
     * @return Gets the value of pathChoice variable
     */
    protected int GetPathChoice() {
        return pathChoice;
    }

    /*
	// NEW SECTION: THESE FUNCTIONS CAN BE USED TO CHANGE STATES EITHER IN A TEMPORARY OR A PERMANENT GAME BOARD
	*/

    /**
     * Does one up roll of the dice
     * @param dice the dice to be rolled
     * @param board the board in context
     */
    protected void RollUp(Dice dice, Board board) {

        //if (dice.GetRow() < 7) {}		Put this wrapper around the entire function to validate coordinates
        tempStorage1 = dice.GetFront();
        tempStorage2 = dice.GetRear();

        dice.SetFront(dice.GetTop());
        dice.SetRear(dice.GetBottom());
        dice.SetBottom(tempStorage1);
        dice.SetTop(tempStorage2);

        //Set the currently occupied square to empty (also capturing the current dice, if in destination), and place our dice in the next square
        board.SetSquareVacant(dice.GetRow(), dice.GetColumn());
        dice.SetRow(1, true);

        //This capture statement will only be executed at the destination square if path checking is done beforehand
        if (board.GetSquareResident(dice.GetRow(), dice.GetColumn()) != null) {
            board.SetSquareResidentCaptured(dice.GetRow(), dice.GetColumn(), true);
            printStatus = printNotifications ? Notifications.Msg_CapturedAnOpponent() : Notifications.Msg_NoMsg();
        }

        board.SetSquareOccupied(dice.GetRow(), dice.GetColumn(), dice);
    }

    /**
     * Does one down roll of the dice
     * @param dice the dice to be rolled
     * @param board the board in context
     */
    protected void RollDown(Dice dice, Board board) {
        //if (dice.GetRow() > 0) {}		Put this wrapper around the entire function to validate coordinates
        tempStorage1 = dice.GetFront();
        tempStorage2 = dice.GetRear();

        dice.SetRear(dice.GetTop());
        dice.SetFront(dice.GetBottom());
        dice.SetTop(tempStorage1);
        dice.SetBottom(tempStorage2);

        //Set the currently occupied square to empty (also capturing the current dice, if in destination), and place our dice in the next square
        board.SetSquareVacant(dice.GetRow(), dice.GetColumn());
        dice.SetRow(1, false);

        //This capture statement will only be executed at the destination square if path checking is done beforehand
        if (board.GetSquareResident(dice.GetRow(), dice.GetColumn()) != null) {
            board.SetSquareResidentCaptured(dice.GetRow(), dice.GetColumn(), true);
            printStatus = printNotifications ? Notifications.Msg_CapturedAnOpponent() : Notifications.Msg_NoMsg();
        }

        board.SetSquareOccupied(dice.GetRow(), dice.GetColumn(), dice);
    }

    /**
     * Does one left roll of the dice
     * @param dice the dice to be rolled
     * @param board the board in context
     */
    protected void RollLeft(Dice dice, Board board) {
        // if (dice.GetColumn() > 0) {}		Put this wrapper around the entire function to validate coordinates
        tempStorage1 = dice.GetLeft();
        tempStorage2 = dice.GetRight();

        dice.SetLeft(dice.GetTop());
        dice.SetRight(dice.GetBottom());
        dice.SetBottom(tempStorage1);
        dice.SetTop(tempStorage2);

        //Set the currently occupied square to empty (also capturing the current dice, if in destination), and place our dice in the next square
        board.SetSquareVacant(dice.GetRow(), dice.GetColumn());
        dice.SetColumn(1, false);

        //This capture statement will only be executed at the destination square if path checking is done beforehand
        if (board.GetSquareResident(dice.GetRow(), dice.GetColumn()) != null) {
            board.SetSquareResidentCaptured(dice.GetRow(), dice.GetColumn(), true);
            printStatus = printNotifications ? Notifications.Msg_CapturedAnOpponent() : Notifications.Msg_NoMsg();
        }

        board.SetSquareOccupied(dice.GetRow(), dice.GetColumn(), dice);
    }

    /**
     * Does one right roll of the dice
     * @param dice the dice to be rolled
     * @param board the board in context
     */
    protected void RollRight(Dice dice, Board board) {
        // if (dice.GetColumn() < 8) {}		Put this wrapper around the entire function to validate coordinates
        tempStorage1 = dice.GetLeft();
        tempStorage2 = dice.GetRight();

        dice.SetRight(dice.GetTop());
        dice.SetLeft(dice.GetBottom());
        dice.SetTop(tempStorage1);
        dice.SetBottom(tempStorage2);

        //Set the currently occupied square to empty (also capturing the current dice, if in destination), and place our dice in the next square
        board.SetSquareVacant(dice.GetRow(), dice.GetColumn());
        dice.SetColumn(1, true);

        //This capture statement will only be executed at the destination square if path checking is done beforehand
        if (board.GetSquareResident(dice.GetRow(), dice.GetColumn()) != null) {
            board.SetSquareResidentCaptured(dice.GetRow(), dice.GetColumn(), true);
            printStatus = printNotifications ? Notifications.Msg_CapturedAnOpponent() : Notifications.Msg_NoMsg();
        }

        board.SetSquareOccupied(dice.GetRow(), dice.GetColumn(), dice);
    }

    /*
	// NEW SECTION: THE FOLLOWING FUNCTIONS WILL FORM TEMPORARY PASSED-BY-VALUE GAME OBJECTS AND CHECK THE VALIDITY OF ROUTE/DESTINATION
	*/

    /**
     * Checks if the destination is a valid one; create duplicate copies of function parameters
     * @param origin The dice to be moved
     * @param dest The destination square
     * @return true if the destination is valid; false otherwise
     */
    protected boolean IsValidDestination(Dice origin, Square dest) {
        // Making duplicate copies of function parameters to avoid modifying original reference
        Dice dice = new Dice(origin);
        Square destination = new Square(dest);

        // Destination square should either be null or contain a dice of different team other than the one moving.
        if ((destination.GetResident() == null) || (destination.GetResident().IsBotOperated() && !dice.IsBotOperated()) || (!destination.GetResident().IsBotOperated() && dice.IsBotOperated())) {

            // (Destination row - source row) + (Destination col - source col) gives the distance between the source and destination squares
            if (dice.GetTop() == Math.abs(destination.GetRow() - dice.GetRow()) + Math.abs(destination.GetColumn() - dice.GetColumn())) {
                return true;
            }
            else {
                printStatus = printNotifications ? Notifications.Msg_InvalidMove() : Notifications.Msg_NoMsg();
            }
        }
        else {
            printStatus = printNotifications ? Notifications.Msg_RunningOverOwnDice() : Notifications.Msg_NoMsg();
        }
        return false;
    }

    /**
     * Checks the validity of a path to get from origin to destination square; create duplicate copies of function parameters
     * @param origin The dice to be moved
     * @param dest The destination square
     * @param gameBoard The game board in context
     * @return true if a valid path is found to the destination, false otherwise
     */
    protected boolean IsPathValid(Dice origin, Square dest, Board gameBoard) {
        // The temporary dice jumps from one square to the other and checks if it is already occupied

        // Making duplicate copies of function parameters to avoid modifying original reference
        Dice dice = new Dice(origin);
        Square destination = new Square(dest);
        Board board = new Board(gameBoard);

        counterRowsTraversed = 0;
        counterColumnsTraversed = 0;
        pathChoice = 0;
        multiplePathPossible = false;

        // CASE 1
        // If both the rows & columns differ in the destination, it means this is a frontal-lateral combined move attempt
        // TWO possible paths
        // ATTENTION: Need to work on this
        if ((dice.GetRow() != destination.GetRow()) && (dice.GetColumn() != destination.GetColumn())) {

            //Only one path needs to pass
            // Path 1 - First row traversal, then Column
            if (TraversedRowsWithoutBlockade(dice, destination, board) && TraversedColumnsWithoutBlockade(dice, destination, board)) {
                pathChoice = 1;
            }

            // In case we need to check the validity of second path, the dice needs to be reverted back to its original spot
            // based on the counters gathered while traversing above
            dice.SetRow(counterRowsTraversed, false);
            dice.SetColumn(counterColumnsTraversed, false);

            // Path 2 - First column traversal, then row
            if (TraversedColumnsWithoutBlockade(dice, destination, board) && TraversedRowsWithoutBlockade(dice, destination, board)) {
                // If the previous path was valid too, then there are surely two paths
                if (pathChoice == 1) {
                    multiplePathPossible = true;
                    return true;
                }

                pathChoice = 2;
                return true;
            }

            // If the first path was the only one valid
            if (pathChoice == 1) {
                return true;
            }

            //If both the path couldn't return true, then the path is invalid
            printStatus = printNotifications ? Notifications.Msg_NoValidPath() : Notifications.Msg_NoMsg();
            return false;
        }

        // CASE 2
        // If only the rows change in destination, it means this is a frontal/backward move attempt
        // ONE possible path
        if (dice.GetRow() != destination.GetRow()) {
            if (TraversedRowsWithoutBlockade(dice, destination, board)) {
                pathChoice = 3;
                return true;
            }
            else {
                printStatus = printNotifications ? Notifications.Msg_NoValidPath() : Notifications.Msg_NoMsg();
                return false;
            }
        }

        // CASE 3
        // If only the column change, it means this is a lateral move attempt
        // ONE possible path
        if (dice.GetColumn() != destination.GetColumn()) {
            if (TraversedColumnsWithoutBlockade(dice, destination, board)) {
                pathChoice = 4;
                return true;
            }
            else {
               printStatus = printNotifications ? Notifications.Msg_NoValidPath() : Notifications.Msg_NoMsg();
                return false;
            }
        }

        // If moving from and to the current location, still true lol
        return true;
    }

    /**
     * Returns true if traversal is successful without blockade until the destination row (The passed by reference dice is actually a temporary dice itself), create duplicate copies of dest and gameBoard params
     * @param dice The dice to be moved
     * @param dest The destination square
     * @param gameBoard The game board in context
     * @return true if the traversal is successful along the rows until destination row, false otherwise
     */
    protected boolean TraversedRowsWithoutBlockade(Dice dice, Square dest, Board gameBoard) {
        // Still passed by reference cause in case of a 90 degree turn, we want the dice state preserved to call TraversedColumnsWithoutBlockade

        // Making duplicate copies of function parameters to avoid modifying original reference
        Square destination = new Square(dest);
        Board board = new Board(gameBoard);

        counterRowsTraversed = 0;
        do {
            // Increment if destination is in a upper row, decrement if in a lower row
            if (dice.GetRow() < destination.GetRow()) {
                dice.SetRow(1, true);
                counterRowsTraversed++;
            }
            else {
                dice.SetRow(1, false);
                counterRowsTraversed--;
            }

            // No need to check on the destination. If it reaches there, the traversal is considered successful
            if ((dice.GetRow() == destination.GetRow()) && (dice.GetColumn() == destination.GetColumn())) {
                return true;
            }

            // Check if there is a blockade on the path as you go.
            // If yes, the path is invalid
            if (board.IsSquareOccupied(dice.GetRow(), dice.GetColumn())) {
                return false;
            }
        } while (dice.GetRow() != destination.GetRow());
        // If it gets to this point without any false returns, it is a valid path
        return true;
    }

    /**
     * Returns true if traversal is successful without blockade until the destination column (The passed by reference dice is actually a temporary dice itself), create duplicate copies of dest and gameBoard params
     * @param dice The dice to be moved
     * @param dest The destination square
     * @param gameBoard The game board in context
     * @return true if the traversal is successful along the columns until destination column, false otherwise
     */
    protected boolean TraversedColumnsWithoutBlockade(Dice dice, Square dest, Board gameBoard) {
        // Making duplicate copies of function parameters to avoid modifying original reference
        Square destination = new Square(dest);
        Board board = new Board(gameBoard);

        counterColumnsTraversed = 0;
        do {

            // Increment if destination is in a right column, decrement if in a left column
            if (dice.GetColumn() < destination.GetColumn()) {
                dice.SetColumn(1, true);
                counterColumnsTraversed++;
            }
            else {
                dice.SetColumn(1, false);
                counterColumnsTraversed--;
            }

            // No need to check on the destination. If it reaches there, the traversal is considered successful
            if ((dice.GetRow() == destination.GetRow()) && (dice.GetColumn() == destination.GetColumn())) {
                return true;
            }

            // Check if there is a blockade on the path as you go.
            // If yes, the path is invalid
            if (board.IsSquareOccupied(dice.GetRow(), dice.GetColumn())) {
                return false;
            }
        } while (dice.GetColumn() != destination.GetColumn());
        return true;
    }

    /*
	// NEW SECTION: THE FOLLOWING FUNCTIONS WILL ACTUALLY MODIFY THE REAL GAMEBOARD.
	*/

    /**
     * Checks the validity of a given move, and performs it on the gameboard if valid [CALLED FOR THE DEFAULT PATH = 0 CASE]
     * @param startRow The row coordinate of the dice to be moved
     * @param startCol The column coordinate of the dice to be moved
     * @param endRow The destination row in board
     * @param endCol The destination column in board
     * @param board The game board in context
     * @param helpModeOn true if the function is being called under Help Mode, false otherwise
     * @return true if the move is successfully made, false otherwise
     */
    protected boolean MakeAMove(int startRow, int startCol, int endRow, int endCol, Board board, boolean helpModeOn) {
        return MakeAMove(startRow, startCol, endRow, endCol, board, helpModeOn, 0);
    }

    /**
     * Checks the validity of a given move, and performs it on the gameboard if valid
     * @param startRow The row coordinate of the dice to be moved
     * @param startCol The column coordinate of the dice to be moved
     * @param endRow The destination row in board
     * @param endCol The destination column in board
     * @param board The game board in context
     * @param helpModeOn true if the function is being called under Help Mode, false otherwise
     * @param path 1 if Vertical then Lateral, 2 if Lateral then Vertical
     * @return true if the move is successfully made, false otherwise
     */
    protected boolean MakeAMove(int startRow, int startCol, int endRow, int endCol, Board board, boolean helpModeOn, int path) {
        //Check if destination is valid, then if path is valid
        //Then, either make the move or log an error
        // This can be used for both human or computer after verifying that they are moving their own players.
        // Path 1 and 2 need to offset the changes done by the first function, and hence the startRow/startCol has a counter added in the second function
        if (IsValidDestination(board.GetSquareResident(startRow, startCol), board.GetSquareAtLocation(endRow, endCol))) {
            if (IsPathValid(board.GetSquareResident(startRow, startCol), board.GetSquareAtLocation(endRow, endCol), board)) {
                // If help mode is on, no need to make the actual move, return true here and print suggestion
                if (helpModeOn) {
                    Notifications.Msg_HelpModeRecommendedMove(startRow + 1, startCol + 1, endRow + 1, endCol + 1, pathChoice);
                    return true;
                }

                int topValueAtStart = board.GetSquareResident(startRow, startCol).GetTop();
                int rightValueAtStart = board.GetSquareResident(startRow, startCol).GetRight();

                // If user has input a preferred path in case of a 90 degree turn, we need to honor that
                if (path != 0) {
                    if (path == 1 && pathChoice == 1) {
                        pathChoice = 1;
                    }

                    if ((path == 2) && (pathChoice == 2 || multiplePathPossible)) {
                        pathChoice = 2;
                    }

                    // Display a notification if the user's choice of path wasn't valid and had to be superceded by the next best route
                    if (path != pathChoice) {
                        printStatus = printNotifications ? Notifications.Msg_90DegreePathSelectionNotProcessed() : Notifications.Msg_NoMsg();
                    }
                }

                switch (pathChoice)
                {
                    // First vertically, a 90 degree turn, then laterally
                    case 1:
                        KeepRollingVertically(board.GetSquareResident(startRow, startCol), board.GetSquareAtLocation(endRow, endCol), board);
                        KeepRollingLaterally(board.GetSquareResident(startRow + counterRowsTraversed, startCol), board.GetSquareAtLocation(endRow, endCol), board);
                        Notifications.Msg_NatureOfPathTaken("VERTICAL & LATERAL");
                        break;
                    // First laterally, a 90 degree turn, then vertically
                    case 2:
                        KeepRollingLaterally(board.GetSquareResident(startRow, startCol), board.GetSquareAtLocation(endRow, endCol), board);
                        KeepRollingVertically(board.GetSquareResident(startRow, startCol + counterColumnsTraversed), board.GetSquareAtLocation(endRow, endCol), board);
                        Notifications.Msg_NatureOfPathTaken("LATERAL & VERTICAL");
                        break;
                    // Vertically only
                    case 3:
                        KeepRollingVertically(board.GetSquareResident(startRow, startCol), board.GetSquareAtLocation(endRow, endCol), board);
                        Notifications.Msg_NatureOfPathTaken("VERTICAL");
                        break;
                    // Laterally only
                    case 4:
                        KeepRollingLaterally(board.GetSquareResident(startRow, startCol), board.GetSquareAtLocation(endRow, endCol), board);
                        Notifications.Msg_NatureOfPathTaken("LATERAL");
                        break;
                    default:
                        //ATTENTION: LOG ERROR SAYING THE PATH DESTINATION COULDN'T BE SET FOR SOME REASON
                        Notifications.Msg_CrashedWhileMakingTheMove();
                        return false;
                }
                Notifications.Msg_MoveDescription(startRow + 1, startCol + 1, endRow + 1, endCol + 1, topValueAtStart, rightValueAtStart, board.GetSquareResident(endRow, endCol).GetTop(), board.GetSquareResident(endRow, endCol).GetRight(), board.GetSquareResident(endRow, endCol).IsBotOperated());	// +1 To compensate for 1 offset in the array indexes
                return true;
            }
        }
        return false;
    }

    /**
     * Rolls the dice vertically until it is in the destination row
     * @param dice The dice to be moved
     * @param destination The destination square in the board
     * @param board The game board in context
     */
    protected void KeepRollingVertically(Dice dice, Square destination, Board board) {
        counterRowsTraversed = 0;
        do {
            if (dice.GetRow() < destination.GetRow()) {
                RollUp(dice, board);
                counterRowsTraversed++;
            }
            else {
                RollDown(dice, board);
                counterRowsTraversed--;
            }
        } while (dice.GetRow() != destination.GetRow());
    }

    /**
     * Rolls the dice vertically until it is in the destination column
     * @param dice The dice to be moved
     * @param destination The destination square in the board
     * @param board The game board in context
     */
    protected void KeepRollingLaterally(Dice dice, Square destination, Board board) {
        counterColumnsTraversed = 0;
        do {
            if (dice.GetColumn() < destination.GetColumn()) {
                RollRight(dice, board);
                counterColumnsTraversed++;
            }
            else {
                RollLeft(dice, board);
                counterColumnsTraversed--;
            }
        } while (dice.GetColumn() != destination.GetColumn());
    }
}
