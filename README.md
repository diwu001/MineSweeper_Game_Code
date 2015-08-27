# MineSweeper_Game

* Answer Set Programming MineSweeper Solver with Java Interface
* Tina D. Wu (Email: diwubupt@gmail.com)
* Department of Computer Science, Texas Tech University
* Last updated 6/27/2014

This program is a Java GUI interface for MineSweeper solver written in Answer Set Programming. When running this program a GUI interface is opened which displays a blank gray board with 81 squares. When user clicks "New Game", the board becomes blue and all squares are activated. When user clicks any one of the squares, a random number (between 0 and 3) will be assigned to that square. (Therefore, user won't step on a mine in the first step.) And this calls the MineSweeper solver to take this number as input and compute all possible solutions for board layout. Generally, the game layout has multiple solutions, and only one will be randomly chosen as the solution.

In the solution, each square is either a mine or a number representing how many mines are hidden in the eight surrounding regions. User keeps clicking all squares which he assumes are not mines based on current board layout. If a square is suspected to conceal a mine, user can right-click it and mark it with a red flag. If user is not sure about the square, he can right-click the square again to mark it with a question mark. If user infers that this square cannot be a mine, he can right-click for the third time to make the square to be empty.

After user marks all mines with red flags, a message box will say that user wins the game. However, if user clicks a square which conceals a mine, a message box will say that user fails and needs to replay.

The "New Game" button starts a new game.
The 'Exit' button closes the game interface.
The "View Help" button opens a Help document.
The "About MineSweeper" button shows information about this game.

To run this program requires that iclingo be installed and in the users path. When created, this program was using iclingo version 3.0.5 as Answer Set Solver. The program Mine_Sweeper_Solver.lp must be in the path or in the same directory as this program. This program creates an input file (Input.lp) when running. This input file is deleted when a new game is started.

This program has been tested on Window 7 OS and using Eclipse 4.3.2, JRE7 and iclingo 3.0.5.
