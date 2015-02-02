package mine_sweeper;

import javax.swing.JButton;  

/* Buttons Class creates buttons needed in the game.
 * Each button represent a bomb or the number of bombs around the button. */

public class Buttons extends JButton { 
	public int num_x,num_y;       // Coordinates of current button 
	public int BombRoundCount;    // Number of bombs around current button 
	public boolean isBomb;        // Current button is a bomb or not
	public boolean isClicked;     // Current button has been clicked or not
	public int BombFlag;          // A Flag used to identify bomb 
	public boolean isCalculated;  // The number of bombs around current button has been calculated or not
	public boolean isRight;       // Current button has been right-clicked or not
	 
	public Buttons(int x,int y) {    
	    num_x = x;
	    num_y = y; 
	    BombRoundCount = 0; 
	    isBomb = false; 
	    isClicked = false;
	    BombFlag = 0; 
	    isCalculated = false;
	    isRight = false;
	}
} 