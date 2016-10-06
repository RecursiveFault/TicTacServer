/* TicTacToe Client
 * Matthew Moellman
 * CSC460
 * 15 February 2016
*/
import java.net.*;
import java.util.Scanner;
import java.io.*;

public class tictacClient {

	public static void main(String[] args) {
		Socket toServerSocket;
		
		final int EMPTY = 0;
		final int X= 1;
		final int O= 2;
		
		System.out.println("CLIENT attemping to connect...");
		try{
			toServerSocket = new Socket("localhost",7788);
			System.out.println("CONNECTED!");
			
			DataInputStream inStream;
			DataOutputStream outStream;
			inStream=new DataInputStream(toServerSocket.getInputStream());
			outStream=new DataOutputStream(toServerSocket.getOutputStream());
			PrintWriter out = new PrintWriter(outStream, true);
			BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
			
			
			int gameboard[][] = new int[3][3];
			for (int i=0;i<3;i++) //set all values in array to EMPTY
			{
				for (int j=0;j<3;j++)
				{
					gameboard[i][j] = EMPTY;
				}
			}
			boolean turn = false;
			int servSeed, clientSeed;
			String delims="[ ]+";
			String s = in.readLine();
			String sSplit[] = s.split(delims);
			
			if (sSplit[0].equals("NONE")) //received string indicates we go first
			{
				System.out.println("You go first as O!");
				turn = true; clientSeed = O; servSeed = X;
				String str = promptUser(gameboard); 
				String strSplit[] = str.split(delims);
				int i = Integer.parseInt(strSplit[1]);
				int j = Integer.parseInt(strSplit[2]);
				gameboard[i][j] = clientSeed;
				out.println(str);
			}
			else{
				System.out.println("You go second as O!");
				turn = false; clientSeed = O; servSeed = X;
				int row = Integer.parseInt(sSplit[1]);
				int col = Integer.parseInt(sSplit[2]);
				gameboard[row][col] = servSeed;
			}
			turn = !turn;
			boolean ongoing =true;
			while (ongoing)
			{
				if (!turn) //server turn, (goes first to match server code)
				{
					String str = in.readLine();
					String splitStr[] = str.split(" ");
					if (splitStr.length != 3) //more than 3 arguments, means WIN/LOSS/TIE
					{
						if (splitStr[3].equals("TIE"))
						{
							ongoing = false;
							for (int i=0;i<3;i++) //because a tie game message on the server side does not include the final move, this allows for the final space to be filled when the board id displayed
							{
								for (int j=0;j<3;j++)
								{
									if (gameboard[i][j] == EMPTY)
										gameboard[i][j] = servSeed;
								}
							}
							showFinal(gameboard);
							System.out.println("Tie game!");
						}
						else if (splitStr[3].equals("WIN"))
						{
							ongoing = false;
							showFinal(gameboard);
							System.out.println("You win!");
						}
						else if (splitStr[3].equals("LOSS"))
						{
							ongoing = false;
							showFinal(gameboard);
							System.out.println("You lose!");
						}
					}
					else { //otherwise treat as insert
						int row = Integer.parseInt(splitStr[1]);
						int col = Integer.parseInt(splitStr[2]);
						gameboard[row][col] = servSeed;
					}
				}
				else{ //client turn
					String str = promptUser(gameboard);
					String strSplit[] = str.split(delims);
					int i = Integer.parseInt(strSplit[1]);
					int j = Integer.parseInt(strSplit[2]);
					gameboard[i][j] = clientSeed;
					out.println(str);
				}
				turn = !turn;
			}
			toServerSocket.close();
		} catch(IOException e){}
	}
	
	public static String promptUser(int board[][])//displays board, gets input, and checks if space chosen is empty or legal
	{
		Scanner sc = new Scanner(System.in);
		for (int i=0; i<3; i++)
		{
			for (int j=0; j<3; j++)
			{
				switch(board[i][j]){ //print board using switch
				case 0: System.out.print("   "); break;
				case 1: System.out.print(" X "); break;
				case 2: System.out.print(" O "); break;
				}
				if (j != 2)
					System.out.print("|"); //vertical divider
			}
			System.out.println();
			if (i != 2)
				System.out.println("-----------"); //horizontal divider
		}
		System.out.println();
		boolean legalMove = false; String r= "0";
		while (!legalMove)
		{
			System.out.println("Enter your move in the form \"MOVE # #\": ");
			r = sc.nextLine();
			String strSplit[] = r.split(" ");
			int i = Integer.parseInt(strSplit[1]);
			int j = Integer.parseInt(strSplit[2]);
			if (i >= 3 || j >= 3) //checks user input for validity
				System.out.println("You must choose a value between 0-2 for both row and col");
			else if (board[i][j] != 0)
				System.out.println("That space is already taken by a symbol, try again:");
			else
				legalMove = true; //empty space, in valid position
		}
		return r;
	}
	public static void showFinal(int board[][]) //only shows board
	{
		Scanner sc = new Scanner(System.in);
		for (int i=0; i<3; i++)
		{
			for (int j=0; j<3; j++)
			{
				switch(board[i][j]){
				case 0: System.out.print("   "); break;
				case 1: System.out.print(" X "); break;
				case 2: System.out.print(" O "); break;
				}
				if (j != 2)
					System.out.print("|");
			}
			System.out.println();
			if (i != 2)
				System.out.println("-----------");
		}
		System.out.println();
	}

}
