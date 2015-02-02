% Answer Set Programming MineSweeper Solver
% by Di Wu
% Department of Computer Science
% Texas Tech University
% E-mail: di.wu@ttu.edu
% Last updated 6/27/2014

% This program solves MineSweeper Game using Answer Set Programming.  

% It expects an input file(Input.lp) containing the initial layout of the MineSweeper Board.
% Initial layout is composed by atoms of the form "number(X,Y,N)", 
% which indicate that value "N" is in row "X", column "Y".
% Here, "N" means how many mines are hidden in the eight squares around the square
% denoted by row "X" and column "Y".
 
% When the input file (Input.lp) and this file (Mine_Sweeper_Solver.lp) are passed to an 
% Answer Set Solver, if layout of the game is unsolvable, there will be no answer set. 
% If layout of the game can be satisfied, the resulting answer sets will each contain 
% 81 atoms. These atoms is either of the form "mine(X,Y)" or of the form "number(X,Y,N)",
% which indicates that either a mine or a number is placed in each of the 81 squares.

% This code was last tested using iclingo 3.0.5.

% Integers 0 through 3 are the valid numbers.
num(0..3).

% MineSweeper boards has 9 rows and 9 cols. 
row(1..9).
col(1..9).

% Each square can be a number or a mine.
1 {number(X,Y,N) : num(N), mine(X,Y)} 1 :- row(X), col(Y).

% defining adjacent(X1,Y1,X2,Y2) - True if 
% (X1,Y1) are in the eight adjacent squares around the square (X2,Y2).
adjacent(X1,Y1,X2,Y2) :- row(X1;X2), col(Y1;Y2), |X1-X2|==1, |Y1-Y2|==1.
adjacent(X1,Y1,X2,Y2) :- row(X1;X2), col(Y1;Y2), |X1-X2|+|Y1-Y2|==1.

% There are N mines around a number N.
N {mine(X2, Y2) : adjacent(X2,Y2,X1,Y1)} N :- number(X1,Y1,N).

% Total number of mines in a game is 10.
10 #count {mine(X1,Y1) :row(X1) : col(Y1)} 10.

% Total number of squares surrounding by 0 mines is between 10 and 20.
% This is to avoid the answer set with too many number(X,Y,0), 
% so that mines won't be too densely distributed. 
10 #count {number(X,Y,0) :row(X) : col(Y)} 20.

% Optimize mines distribution.
% It's impossible to place more than 3 mines in the same row.
:- mine(X,Y1),mine(X,Y2),mine(X,Y3),Y1!=Y2,Y1!=Y3,Y2!=Y3,row(X).

% It's impossible to place more than 3 mines in the same column.
:- mine(X1,Y),mine(X2,Y),mine(X3,Y),X1!=X2,X1!=X3,X2!=X3,col(Y).

% It's impossible to place mines in the following four patterns:
% Because if in one of the following patterns, the square in any of the four
% corners (1,1),(1,9),(9,1) or (9,9) can have 3 mines around or can be a mine.
% Players can only guess the square in corners to be a number or a mine.
% So players have half a chance to step on a mine and end the game with failure.
:- mine(1,2),mine(2,1),mine(2,2).
:- mine(1,8),mine(2,8),mine(2,9).
:- mine(8,8),mine(8,9),mine(9,8).
:- mine(8,1),mine(8,2),mine(9,2).

% It's impossible to place mines densely in any of the four corners. 
:- mine(1,1),mine(1,2),mine(2,1),mine(2,2).
:- mine(1,8),mine(1,9),mine(2,8),mine(2,9).
:- mine(8,1),mine(8,2),mine(9,1),mine(9,2).
:- mine(8,8),mine(8,9),mine(9,8),mine(9,9).

% When giving the Answer Sets, only show the "mine" and "number" atoms.
#hide.
#show mine/2.
#show number/3.
