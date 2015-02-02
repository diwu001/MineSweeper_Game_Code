package mine_sweeper;

import java.awt.*; 
import java.awt.event.*; 

import javax.swing.*;

import java.io.*;

/* CleanMine Class creates game interface for Main Class. */

public class CleanMine extends JFrame implements ActionListener, MouseListener {	 
	 public JButton nowBomb;												// Button for showing number of bombs not cleared
	 public JButton start = new JButton("New Game"); 						// Button for starting game
	 public Panel MenuPanel = new Panel(); 
	 public Panel mainPanel = new Panel(); 
	 public Buttons[][] bombButton;    										// Each button represent a square in the game
	 public int BlockNum;    												// Total number of Buttons in the game
	 public int BombNum;    												// Number of bombs not cleared  
	 public int TotalBomb = 10;    											// Total number of bombs
	 public Icon icon_bomb = new ImageIcon("./images/bomb.png");            // Icon for bomb
	 public Icon icon_flag = new ImageIcon("./images/flag.png");            // Icon for flag
	 public Icon icon_question = new ImageIcon("./images/question.png");    // Icon for question
	 static String[] bombPosition = new String[100]; 						// String Array to store the answer sets		
	 
	 /* Interface Design */
	 public CleanMine() {
		 /* Menu Panel Design */
		 super("Mine Sweeper"); 
		 BlockNum = 81; 
		 BombNum = TotalBomb; 
		 Container c=getContentPane();
		 c.setBackground(Color.gray); 
		 c.setLayout(new BorderLayout()); 		 
		 nowBomb = new JButton("Bomb Number: "+"  "+BombNum+"");
		 MenuPanel.add(start); 
		 MenuPanel.add(nowBomb); 
		 c.add(MenuPanel,"North"); 
		 
		 /* BombButton Design */
		 mainPanel.setLayout(new GridLayout( (int)Math.sqrt(BlockNum) , (int)Math.sqrt(BlockNum)) );
		 bombButton=new Buttons[ (int)Math.sqrt(BlockNum) ][];
		 
		 for(int i = 0 ; i < (int)Math.sqrt(BlockNum) ; i++) {
			 bombButton[ i ]=new Buttons[ (int)Math.sqrt(BlockNum) ];
		 }
		 
		 for(int i = 0 ; i < (int)Math.sqrt(BlockNum) ; i++) 
			 for(int j = 0 ; j < (int)Math.sqrt(BlockNum) ; j++) { 
				 bombButton[ i ][ j ]=new Buttons(i,j);
			     bombButton[ i ][ j ].setForeground( Color.gray); 
			     bombButton[ i ][ j ].addActionListener(this); 
			     bombButton[ i ][ j ].addMouseListener(this); 
			 } 
		 
		 for(int i = 0 ; i < (int)Math.sqrt(BlockNum) ; i++) 
			 for(int j = 0 ; j < (int)Math.sqrt(BlockNum) ; j++)
				 mainPanel.add(bombButton[ i ][ j ]); 
		  
		 for(int i = 0 ; i < (int)Math.sqrt(BlockNum) ; i++) 
			 for(int j = 0 ; j < (int)Math.sqrt(BlockNum) ; j++) { 
				 bombButton[ i ][ j ].isBomb=false; 
				 bombButton[ i ][ j ].isClicked=false; 
				 bombButton[ i ][ j ].isCalculated=false;
				 bombButton[ i ][ j ].BombRoundCount=0;
				 bombButton[ i ][ j ].setEnabled(false); 
				 bombButton[ i ][ j ].setText(""); 
				 bombButton[ i ][ j ].setIcon(null); 
			 } 	
		 
		 /* Menu Bar Design */
		 JMenuBar bar=new JMenuBar();
		 JMenu game=new JMenu(" Game ");
		 JMenu help=new JMenu(" Help ");
		 JMenuItem item;
		 game.add(item=new JMenuItem(" New Game "));item.addActionListener(this);
		 game.addSeparator();
		 game.add(item=new JMenuItem(" Exit "));item.addActionListener(this);	
		 help.add(item=new JMenuItem(" View Help "));item.addActionListener(this);
		 help.addSeparator();
		 help.add(item=new JMenuItem(" About MineSweeper "));item.addActionListener(this);		
		 bar.add(game);
		 bar.add(help);		
		 this.setJMenuBar(bar);
		 
		 /* Start the game by clicking the start button	*/
		 start.addActionListener(new ActionListener() { 
			 public void actionPerformed(ActionEvent e) {   
				 BombNum = TotalBomb;
				 replay();				     	     
			 } 
		 } );
			 
		 c.add(mainPanel,"Center");
		 startBomb();
		 setSize(600,600);
		 setLocation(600,50);
		 setResizable(false);
	 }
	 
	 /* Initialize number of bombs in every button to be -2147483648 */
	 public void startBomb() {
		 for(int i = 0 ; i < (int)Math.sqrt(BlockNum) ; i++ ) 
			  for(int j = 0 ; j < (int)Math.sqrt(BlockNum) ; j++ ) { 
				  bombButton[ i ][ j ].BombRoundCount = Integer.MIN_VALUE;
			  }
	 }
	 
	 /* Initialize the game */
	 public void replay() { 
		 /* Set Current Number of bombs not cleared to be total number of bombs */
		 BombNum = TotalBomb;
		 nowBomb.setText("Bomb Number: "+"  "+BombNum+"");
		 
		 /* If there is an existing input file (Input.lp), delete that file */ 
		 try {
			 File f = new File("./solver/Input.lp");
			 if(f.isFile() && f.exists()) { 
				 f.delete();
			 }
		 } catch (Exception e) {
	      		e.printStackTrace();
	       }
		 
		 /* Initialize all fields of bombButton */
		 for(int i = 0 ; i < (int)Math.sqrt(BlockNum) ; i++) 
			 for(int j = 0 ; j < (int)Math.sqrt(BlockNum) ; j++) { 
				 bombButton[ i ][ j ].isBomb=false; 
				 bombButton[ i ][ j ].isClicked=false; 
				 bombButton[ i ][ j ].isRight=false;
				 bombButton[ i ][ j ].BombFlag=0;
				 bombButton[ i ][ j ].isCalculated=false;
				 bombButton[ i ][ j ].BombRoundCount=0;
				 bombButton[ i ][ j ].setEnabled(true); 
				 bombButton[ i ][ j ].setText(""); 
				 bombButton[ i ][ j ].setIcon(null); 
			 } 
		 startBomb();
	 } 

	 /* Create a new input file (Input.lp) for answer set programming solver, 
	  * Write content to this new file */
	 public void creatInputFile(String content) {
		 String s = new String();  
	     String s1 = new String();
	     String path = "./solver/Input.lp";
	     
	     try {
	    	 File f = new File(path);
	    	 if (f.exists()) { 					// File already exists
	    	 } else {         					// File doesn't exist, start creating now
	    		 if (f.createNewFile()) { 		// File creation success
	    		 } else {						// File creation fails
	    		 }
	    	 }
	    	 
	    	 BufferedReader input = new BufferedReader(new FileReader(f));
	    	 
	    	 while ((s = input.readLine()) != null) {
	    		 s1 += s + "\n";
	    	 }	
	    	 
	    	 input.close();
	    	 s1 += content;                     // Add content to this new file
	    	 BufferedWriter output = new BufferedWriter(new FileWriter(f));
	    	 output.write(s1);
	    	 output.close();
	      	 } catch (Exception e) {
	      		e.printStackTrace();
	      	}
	 }
	 
	 /* Create a new thread to call answer set programming solver, 
	  * Solver may take a long time to compute all answer set, 
	  * and using Multi-thread,user need not to wait a long time to see the board layout updated*/
	 public void callSolver_Multithread() {
		 Thread th2 = new Thread(new Test2());  	     
	     th2.start();  			// Start thread th2
	     Thread.yield();  	  
 	     th2.interrupt();       // Interrupt th2 and kill th2 after 1 second
	     Thread.yield();  
	 }
	 
	 public static class Test2 implements Runnable {  
		 public void run() {  
	         long startTime = System.currentTimeMillis();   // Record start time
	         callSolver();									// Call answer set programming solver
	         while (true) {  
	             if (Thread.interrupted()) {  
	                 if (Thread.interrupted()) {  	                   
	                     break;  
	                 }  
	             }  
	  
	             /* th2 ends automatically after 1 second */
	             if (System.currentTimeMillis() - startTime > 1000) {  
	                 break;  
	             }  
	         }  
	     }  
	 }  
	 
	/* Call Answer set programming solver to compute answer sets */
	 public static void callSolver() {
		 String dir = "./solver";              // Set relative path of the solver 
		 Runtime run = Runtime.getRuntime();
		 int i=0;
		 
		 /* Start a process to run cmd.exe, and cmd.exe calls iclingo as solver to compute answer sets*/
		 try {
			 /* Solver take Input.lp and Mine_Sweeper_Solver.lp files as input
			  * --imax=n means perform at most n incremental solving steps before termination. */
			 Process p = run.exec("cmd.exe /k cd \""+dir+"\" & cmd.exe /c \"iclingo Input.lp Mine_Sweeper_Solver.lp --imax=1000\"");
			 
			 BufferedInputStream in = new BufferedInputStream(p.getInputStream());
			 BufferedReader inBr = new BufferedReader(new InputStreamReader(in));
			 String lineStr;
			
			 /* Store answer sets in a string array*/
			 while(!(lineStr = inBr.readLine()).isEmpty() && i<bombPosition.length) {  			 
				 bombPosition[i] = lineStr;	
				 //System.out.println(bombPosition[i]);
				 i++;
			 }

			 inBr.close();
			 in.close();
			 p.destroy();
		 } catch ( Exception e_process) {
			 e_process.printStackTrace();				
		 }
	 }
	 
	 /* Based on answer set, update mine and number distribution on board */
	 public void updateBomb() {		 
		 String position1 = bombPosition[0];
		 
		 /* If the string starts with 'U', it means "Unsatisfiable" */
		 if (position1.charAt(0)=='U') {
			 JOptionPane msg = new JOptionPane(); 
			 JOptionPane.showMessageDialog(this,"No Solution! Replay","Warning",2);  // Show an error message			  
			 replay();																 // Need to replay
		 }
		 
		 /* The game layout is satisfiable, update mine distribution and BombRoundCount */
		 else {			   
			 int i = (int)(Math.random()*(bombPosition.length));                     // Randomly choose a number between 0 and 99
			 
			 /* Search from i backward and find a string beginning with letter 'n' or 'm'.
			  * Letter 'n' represents "number(X,Y,N)", letter 'm' represents "mine(X,Y)".
			  * A string starting with 'n' or 'm' is an answer set.
			  * Once found such a string, break from while loop */
			 while(i>=0) {
				 if(bombPosition[i]!=null && (bombPosition[i].charAt(0)=='n' || bombPosition[i].charAt(0)=='m')) {
					 break;
				 }
				 i--;
			 }
			 
			 String position = bombPosition[i];   									 // Store the answer set in a string position
			 
			 /* Based on the answer set, update mine positions in the board */
			 int j=0, x=0, y=0;
			 while(j<position.length()-7) {
				 if(position.charAt(j)=='m' && position.charAt(j+1)=='i') {
					 x = position.charAt(j+5)-'0';
					 y = position.charAt(j+7)-'0';
					 bombButton[x-1][y-1].isBomb = true;
				 }
				 j++;
			 }
			 
			 /* Based on the answer set, update number positions in the board */
			 int k = 0, p=0, q=0;
			 while(k<position.length()-11) {
				 if(position.charAt(k)=='n' && position.charAt(k+1)=='u') {
					 p = position.charAt(k+7)-'0';
					 q = position.charAt(k+9)-'0';
					 if(bombButton[p-1][q-1].isCalculated==false && bombButton[p-1][q-1].isBomb==false) {
						 bombButton[p-1][q-1].BombRoundCount = position.charAt(k+11)-'0';
						 bombButton[p-1][q-1].isCalculated = true;						 
					 }					 	
				 }
				 k++;
			 }
		 } 
	 }				 			 
	 
	 /* Uncover squares and show number in these squares */
	 public void isNull(Buttons[][] bombButton,Buttons ClickecButton) {
		 int i,j;
		 i=ClickecButton.num_x;
		 j=ClickecButton.num_y;
	  
		 if (ClickecButton.isBomb==true) { }
		 else {	
			 /* Check if the up left square is not a bomb, hasn't be clicked and hasn't be marked.
			  * Then uncover this square and show the number in it */
			 if ((i - 1 >= 0) && (j - 1 >= 0)) { 
				 if (bombButton[i - 1][j - 1].isBomb == false && bombButton[i - 1][j - 1].isClicked == false && bombButton[i - 1][j - 1].isRight == false) {			 
					 bombButton[i - 1][j - 1].setText(bombButton[i - 1][j - 1].BombRoundCount + "");
					 bombButton[i - 1][j - 1].setEnabled(false);
					 bombButton[i - 1][j - 1].isClicked=true;
				 }
			 }
			 
			/* Check if the up square is not a bomb, hasn't be clicked and hasn't be marked.
		     * Then uncover this square and show the number in it */		 
			if ((i - 1 >= 0)) { 
				 if (bombButton[i - 1][ j ].isBomb == false && bombButton[i - 1][ j ].isClicked == false && bombButton[i - 1][ j ].isRight == false) {
					 bombButton[i - 1][ j ].setText((bombButton[i - 1][ j ].BombRoundCount)+"");
					 bombButton[i - 1][ j ].setEnabled(false);
					 bombButton[i - 1][ j ].isClicked=true;
				 }
			 }
			 
			 /* Check if the up right square is not a bomb, hasn't be clicked and hasn't be marked.
			  * Then uncover this square and show the number in it */	
			 if ((i - 1 >= 0) && (j + 1 <= ((int)Math.sqrt(BlockNum)-1)) ) { 
				 if (bombButton[i - 1][j + 1] .isBomb == false && bombButton[i - 1][j + 1].isClicked == false && bombButton[i - 1][j + 1].isRight == false) {
					 bombButton[i - 1][j + 1].setText((bombButton[i - 1][j + 1].BombRoundCount)+"");
					 bombButton[i - 1][j + 1].setEnabled(false);
					 bombButton[i - 1][j + 1].isClicked=true;
				 }
	    
			 }
			 
			 /* Check if the left square is not a bomb, hasn't be clicked and hasn't be marked.
			  * Then uncover this square and show the number in it */	
			 if ((j - 1 >= 0)) { 
				 if (bombButton[ i ][j - 1].isBomb == false && bombButton[ i ][j - 1].isClicked == false && bombButton[ i ][j - 1].isRight == false) {
					 bombButton[ i ][j - 1].setText((bombButton[ i ][j - 1].BombRoundCount)+"");
					 bombButton[ i ][j - 1].setEnabled(false);
					 bombButton[ i ][j - 1].isClicked=true; 
				 }	    
			 }
			 
			 /* Check if the right square is not a bomb, hasn't be clicked and hasn't be marked.
			  * Then uncover this square and show the number in it */
			 if ((i >= 0) && (j + 1 <= ((int)Math.sqrt(BlockNum)-1)) ) { 
				 if (bombButton[ i ][j + 1].isBomb == false && bombButton[ i ][j + 1].isClicked == false && bombButton[ i ][j + 1].isRight == false) {
					 bombButton[ i ][j + 1].setText((bombButton[ i ][j + 1].BombRoundCount)+"");
					 bombButton[ i ][j + 1].setEnabled(false);
					 bombButton[ i ][j + 1].isClicked=true;
				 }
			 }
			 
			 /* Check if the down left square is not a bomb, hasn't be clicked and hasn't be marked.
			  * Then uncover this square and show the number in it */
			 if ((j - 1 >= 0) && (i + 1 <= ((int)Math.sqrt(BlockNum)-1)) ) { 
				 if (bombButton[i + 1][j - 1].isBomb == false && bombButton[i + 1][j - 1].isClicked == false && bombButton[i + 1][j - 1].isRight == false) {
					 bombButton[i + 1][j - 1].setText((bombButton[i + 1][j - 1].BombRoundCount)+"");
					 bombButton[i + 1][j - 1].setEnabled(false);
					 bombButton[i + 1][j - 1].isClicked=true;
				 }
			 }
			 
			 /* Check if the down square is not a bomb, hasn't be clicked and hasn't be marked.
			  * Then uncover this square and show the number in it */
			 if ((i + 1 <= ((int)Math.sqrt(BlockNum)-1)) ) { 
				 if (bombButton[i + 1][ j ].isBomb == false && bombButton[i + 1][ j ].isClicked == false && bombButton[i + 1][ j ].isRight == false) {
					 bombButton[i + 1][ j ].setText((bombButton[i + 1][ j ].BombRoundCount)+"");
					 bombButton[i + 1][ j ].setEnabled(false);
					 bombButton[i + 1][ j ].isClicked=true;
				 }
			 }
			 
			 /* Check if the down right square is not a bomb, hasn't be clicked and hasn't be marked.
			  * Then uncover this square and show the number in it */
			 if ((j + 1 <= ((int)Math.sqrt(BlockNum)-1) ) && (i + 1 <= ((int)Math.sqrt(BlockNum)-1)) ) { 
				 if (bombButton[i + 1][j + 1].isBomb == false && bombButton[i + 1][j + 1].isClicked == false && bombButton[i + 1][j + 1].isRight == false) {
					 bombButton[i + 1][j + 1].setText((bombButton[i + 1][j + 1].BombRoundCount)+"");
					 bombButton[i + 1][j + 1].setEnabled(false);
					 bombButton[i + 1][j + 1].isClicked=true;
				 }
			 }
			 
			 /* Recursively check all eight surrounding squares */
			 if ((i - 1 >= 0) && (j - 1 >= 0))
				 isNull(bombButton,bombButton[i - 1][j - 1]);
			 if ((i - 1 >= 0))
				 isNull( bombButton,bombButton[i - 1][ j ]);
			 if ((i - 1 >= 0) && (j + 1 <= (int)Math.sqrt(BlockNum)-1))
				 isNull( bombButton,bombButton[i - 1][j + 1]);
			 if ((j - 1 >= 0))
				 isNull(bombButton,bombButton[i][j - 1]);
			 if ((i >= 0) && (j + 1 <= ((int)Math.sqrt(BlockNum)-1)) )
				 isNull(bombButton,bombButton[i][j + 1]);
			 if ((j - 1 >= 0) && (i + 1 <= ((int)Math.sqrt(BlockNum)-1)) )
				 isNull(bombButton,bombButton[i + 1][j - 1]); 
			 if ((i + 1 <= ((int)Math.sqrt(BlockNum)-1)) ) 
				 isNull(bombButton,bombButton[i + 1][ j ]);
			 if ((j + 1 <= ((int)Math.sqrt(BlockNum)-1)) && (i + 1 <= ((int)Math.sqrt(BlockNum)-1)) ) 
				 isNull(bombButton,bombButton[i + 1][j + 1]);	

		 } 
	 }
	 
	 /* After marking all bombs with red flag, user wins the game */
	 public void isWin() {
		 int findBomb=0;          // Initialize the number of found bomb to be 0
		 
		 /* If a square is a bomb and has been right-clicked, it's considered to be a found bomb */
		 for(int i = 0;i < (int)Math.sqrt(BlockNum) ; i++) 
			 for(int j = 0;j < (int)Math.sqrt(BlockNum ); j++) { 
				 if(bombButton[ i ][ j ].isBomb == true && bombButton[ i ][ j ].isRight == true)
					 findBomb++; 
			 }
		 
		 /* When all bombs are found, show a message box */
		 if( findBomb == TotalBomb ) { 
			 JOptionPane msg = new JOptionPane(); 
			 JOptionPane.showMessageDialog(this,"You found all the mines! You win!","You win!",2); 			 
			 replay();            //After clicking "ok" in the message box, user can play a new game	 
		 }
	 }
	 
	 /* Create a new thread to call cmd.exe to open Help.pdf file */
	 public void callPdf_Multithread() {
		 Thread th3 = new Thread(new Test3());  	     
	     th3.start();  			// Start thread th3
	     Thread.yield();  	  
 	     th3.interrupt();       // Interrupt th3 and kill th3 after 1 second
	     Thread.yield();  
	 }
	 
	 public static class Test3 implements Runnable {  
		 public void run() {  
	         long startTime = System.currentTimeMillis();   // Record start time
	         callPdf();									    // Call cmd.exe to open Help.pdf file
	         while (true) {  
	             if (Thread.interrupted()) {  
	                 if (Thread.interrupted()) {  	                   
	                     break;  
	                 }  
	             }  
	  
	             /* th3 ends automatically after 1 second */
	             if (System.currentTimeMillis() - startTime > 1000) {  
	                 break;  
	             }  
	         }  
	     }  
	 }  
	 
	 public static void callPdf() {
		 String dir = "./help";            // Set relative path of Help.pdf
		 Runtime run = Runtime.getRuntime();
		 int i=0;
		 /* Start a process to run cmd.exe, and cmd.exe opens help.pdf*/
		 try {
			 Process p = run.exec("cmd.exe /k cd \""+dir+"\" & cmd.exe /c \"Help.pdf\"");
			 BufferedInputStream in = new BufferedInputStream(p.getInputStream());
			 BufferedReader inBr = new BufferedReader(new InputStreamReader(in));
			 String lineStr;				
			 			 
			 while(!(lineStr = inBr.readLine()).isEmpty()&&i<=1) { 
				 i++;
			 }
			 
			 inBr.close();
			 in.close();
			 p.destroy();
		 } catch ( Exception e_pdf) {
			 e_pdf.printStackTrace();				
		 }			
	 }
	 
	 /* Handle action performed by user */
	 public void actionPerformed(ActionEvent e) {		 
		 /* If user clicks "New Game", starts a new game */
		 if(e.getActionCommand().equals(" New Game ")){
			 replay();
			 return;
		 } 
		
		 /* If user clicks "Exit", closes the game interface.*/
		 if(e.getActionCommand().equals(" Exit ")){
			 System.exit(0);
			 return;
		 }
		 
		 /* If user clicks "View Help", opens Help.pdf file */
		 if(e.getActionCommand().equals(" View Help ")){ 
			 callPdf_Multithread();
			 return;			 
		 }
		 
		 /* If user clicks "About MineSweeper", show information about the game */
		 if(e.getActionCommand().equals(" About MineSweeper ")){
			 JOptionPane msg = new JOptionPane(); 
			 JOptionPane.showMessageDialog(this,"MineSweeper Version 1.0 \n\nCopyright Di Wu. All right reserved. "
			 		+ "\n\nDesigned for Course CS5331 Practical Logic Programming at Texas Tech University. "
			 		+ "\nInstructed by Dr. Richard Watson.","About MineSweeper",2); 
			 return;
		 } 
		 
		 /* If the current square has the initial value (-2147483648) and is not a bomb, assign it with a random number,
		  * create the input file (Input.lp), call Answer set solver and update mine distribution */
		 if(((Buttons)e.getSource()).BombRoundCount==Integer.MIN_VALUE && ((Buttons)e.getSource()).isBomb==false) {
			 ((Buttons)e.getSource()).BombRoundCount =(int)(Math.random()*4);  //Assign BombRoundCount with a random number (between 0 and 3)
			
			/* Get the coordinate (X,Y) of current square, and create an input file with content of the form "number(X,Y,N)" */
			int row = ((Buttons)e.getSource()).num_x + 1;
			int col = ((Buttons)e.getSource()).num_y +1;			
			String content = "number (" + row + "," + col  + "," + ((Buttons)e.getSource()).BombRoundCount + ").";
			creatInputFile(content);
			callSolver_Multithread();           
		    try {
		    	Thread.sleep(300);               // Main thread sleeps for 0.3s to wait the result of CallSolver Thread
		    } catch ( Exception e1) {
			 e1.printStackTrace();				
		 	}
				    
			updateBomb();
			
			((Buttons)e.getSource()).setText((((Buttons)e.getSource()).BombRoundCount )+"");       // Show that random number in the square
			((Buttons)e.getSource()).setEnabled(false);                                            // Deactivate this square
			isNull(bombButton,(Buttons)e.getSource());                                             // Uncover some squares
		 }		
		
		 /* If the current square is not a bomb and hasn't been clicked yet, show the number in this square.
		  * If the number is  0, uncover some squares. Then check whether or not win the game */
		 if(((Buttons)e.getSource()).isBomb==false && ((Buttons)e.getSource()).isClicked == false) {
			((Buttons)e.getSource()).setText(( ((Buttons)e.getSource()).BombRoundCount )+""); 
			((Buttons)e.getSource()).isClicked=true;
			((Buttons)e.getSource()).setIcon(null); 
			((Buttons)e.getSource()).setEnabled(false); 
			if((((Buttons)e.getSource()).BombRoundCount) == 0)
				 isNull(bombButton,(Buttons)e.getSource());
			isWin();			 
		 } 
		 
		 /* If the current square is a bomb, show all bombs in the board */
		 else if(((Buttons)e.getSource()).isBomb == true) {	   
			 for(int i=0;i<(int)Math.sqrt(BlockNum);i++) 
				 for(int j=0;j<(int)Math.sqrt(BlockNum);j++) { 
				 	 if(bombButton[ i ][ j ].isBomb == true)
						 bombButton[ i ][ j ].setIcon(icon_bomb);
					 } 
			 
			 /* Show a waring message that user steps on a bomb */
			 JOptionPane msg = new JOptionPane(); 
			 JOptionPane.showMessageDialog(this,"A mine here! You need to Replay!","Warning",2); 
			 
			 /* Replay the game */
			 replay(); 
		 } 
	 }
	 
	 /* Handle mouse event */
	 public void mouseClicked(MouseEvent e) { 
		 Buttons bombSource = (Buttons)e.getSource(); 
		 boolean right = SwingUtilities.isRightMouseButton(e); 
	     
		 /* If user right-clicks a square and that square hasn't been clicked, 
		  * mark the square with flag or question, or undo the mark */
		 if((right == true) && (bombSource.isClicked == false)) { 
			 bombSource.BombFlag = (bombSource.BombFlag + 1)%3;   //BombFlag can only have 3 values: 1,2,0
			 
			 // If a square is suspected to conceal a mine, user can right-click it and mark it with a red flag
			 if(bombSource.BombFlag == 1) { 	    
				 if(BombNum > 0 && bombSource.isRight == false ){
					 bombSource.setIcon(icon_flag);
					 bombSource.isRight = true;
					 BombNum--;                                     // Update the current uncovered bomb number
				 }				 
				 nowBomb.setText("Bomb Number: "+"  "+BombNum+""); 
				 isWin();                                           // Check whether or not user win the game
			 }
			 
			 /* If user is not sure about the square, he can right-click the square again to mark it with a question mark */
			 else if(bombSource.BombFlag == 2) 
			 { 
				 if( (BombNum !=0 && (bombSource.getIcon()==icon_flag) ) ||(BombNum ==0 && (bombSource.getIcon()==icon_flag)) )
					 BombNum++; 
				 bombSource.setIcon(icon_question);
				 nowBomb.setText("Bomb Number: "+"  "+BombNum+"");
			 } 
			 
			 /* If user infers that this square can not be a mine, he can right-click for the third time to make the square to be empty. */
			 else if(bombSource.BombFlag == 0) 
			 {  
				 bombSource.setIcon(null);
				 bombSource.isRight = false;
			 } 
		 }
	 } 
	 
	 public void mouseEntered(MouseEvent e) 
	 {} 
	 public void mouseReleased(MouseEvent e) 
	 {} 
	 public void mouseExited(MouseEvent e) 
	 {} 
	 public void mousePressed(MouseEvent e) 
	 {} 
}
