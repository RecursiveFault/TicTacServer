/*
 * TicTacToe Server - Multithreaded
 * Matthew Moellman
 * CSC460
 * 15 March 2016
*/
import java.net.*;
import java.io.*;
import java.util.Random;

public class tictacServer implements Runnable{
    
	Socket socket;
	tictacSemaphore semPtr;
	
	tictacServer(Socket inSocket, tictacSemaphore s) //constructor is used when .start() is called, to set the socket.
	{
		this.socket = inSocket;
		this.semPtr = s;
	}
	
	public static void main(String[] args) {
		ServerSocket server; //initialize
		Socket toClientSocket;
		
		tictacSemaphore sem = new tictacSemaphore();
		
		try{
			server = new ServerSocket(7788);
			while (true){
				toClientSocket = server.accept();
				System.out.println("New client connected...");
				new Thread(new tictacServer(toClientSocket, sem)).start(); //starts new thread by creating a new tictacServer object
			}
			
		}catch(IOException e){}
			
	}//end main
	
	@Override
	public void run()
	{
		final int EMPTY = 0; //sets static names for the 0-1-2 values as empty, X, and O
		final int X= 1;
		final int O= 2;
		try{
			DataInputStream inStream; //init PrintWriter and BufferedReader
			DataOutputStream outStream;
			inStream=new DataInputStream(socket.getInputStream()); //socket is taken from the 
			outStream=new DataOutputStream(socket.getOutputStream());
			PrintWriter out = new PrintWriter(outStream, true);
			BufferedReader in = new BufferedReader(new InputStreamReader(inStream));

			
			int gameboard[][] = new int[3][3];
			for (int i=0;i<3;i++) //set all values in gameboard array to EMPTY, or 0
			{
				for (int j=0;j<3;j++)
				{
					gameboard[i][j] = EMPTY;
				}
			}
			
			Random rand = new Random();
			boolean turn = rand.nextBoolean(); //random first move
			int servSeed, clientSeed; //holds X and O based on first move, X was originally whoever went first, but I fixed it after reading instructions again
			String delims="[ ]+"; //delimiter of space, for splitting input strings
			if (!turn) //Client goes first
			{
				out.println("NONE");
				String s = in.readLine();
				String str[]= s.split(delims);
				int row = Integer.parseInt(str[1]);
				int col = Integer.parseInt(str[2]);
				gameboard[row][col] = O;
				servSeed = X; clientSeed = O; //client is hardcoded to O, but could be altered
			}
			else{ //Server goes first
				out.println("MOVE 0 0");
				gameboard[0][0] = X;
				servSeed = X; clientSeed = O; //server is hardcoded to X, but could be altered
			}
			turn = !turn; //change turns
			int turnCount = 1; //start counter at 1, after previous initial move
			boolean winner = false, isFull = false;
			
			while (!isFull && !winner) //loop until winner or tie
			{
				if (turn) //Server turn
				{
					boolean select = false; int i= -1, j = -1;
					while (!select)
					{
						i = rand.nextInt(3); //random values between 0-2
						j = rand.nextInt(3);
						if (gameboard[i][j] == EMPTY){select =true;}
					}
					gameboard[i][j] = servSeed;
					if (hasWon(servSeed,gameboard,i,j))
					{
						out.println("MOVE 0 0 LOSS");
						winner = true;
						this.semPtr.updateKey(0, 0, 1);
					}
					else if(turnCount==8)
					{
						out.println("MOVE 0 0 TIE");
						isFull = true;
						this.semPtr.updateKey(0, 1, 0);
					}
					else{
						out.println("MOVE "+i +" "+j);
					}
				}
				else //client turn
				{
					String s = in.readLine();
					String move[] = s.split(delims);
					if (!move[0].equals("MOVE"))
					{
						System.out.println("Something went wrong, invalid command");
					}
					else{
						int row = Integer.parseInt(move[1]);
						int col = Integer.parseInt(move[2]);
						gameboard[row][col] = clientSeed;
						if (hasWon(clientSeed,gameboard,row,col))
						{
							winner = true;
							out.println("MOVE 0 0 WIN");
							this.semPtr.updateKey(1, 0, 0);
							
						}
						else if (turnCount == 8)
						{
							isFull = true;
							out.println("MOVE 0 0 TIE");
							this.semPtr.updateKey(0, 1, 0);
						}
					}
				}
				turn = !turn; //change turns, loop back up
				turnCount++;
			}//end while
			out(this.semPtr);
			socket.close();
		} catch(IOException e){} //end try
	}
	
	public static boolean hasWon(int seed, int board[][], int currentRow, int currentCol)
	{ //determines win condition and returns true if a win has occurred for given seed
		return (board[currentRow][0] == seed         // current row
                && board[currentRow][1] == seed
                && board[currentRow][2] == seed
           || board[0][currentCol] == seed      // current column
                && board[1][currentCol] == seed
                && board[2][currentCol] == seed
           || currentRow == currentCol            // diagonal
                && board[0][0] == seed
                && board[1][1] == seed
                && board[2][2] == seed
           || currentRow + currentCol == 2  // opposite diagonal
                && board[0][2] == seed
                && board[1][1] == seed
                && board[2][0] == seed);
	}
	
	public void out(tictacSemaphore s)
	{
		System.out.print("System Statistics:       ");
		System.out.println("Wins " + s.getWin()+ " Losses " + s.getLoss() + " Ties " + s.getTie());
	}
}
