/* TicTacToe Server (Multi-threaded)
 * Matthew Moellman
 * CSC460
 * 15 March 2016
*/
import java.net.*;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.io.*;

public class tictacServerMulti{

	int wins = 0;
	int ties = 0;
	int loss = 0;
	int updateKey = 1;
	public static void main(String[] args) {
		ServerSocket server = null;
		Socket toClientSocket = null;
		try{
			server = new ServerSocket(7788);
			System.out.println("Waiting for client..."); //Verbose output for connection
		}catch(IOException E){}	
			while (true)
				{
				try{
				toClientSocket = server.accept();
				System.out.println("Client connected! Starting thread...");
				}catch(IOException E){}	
				new tictacServerThread(toClientSocket).start();
				}
	}//end main
	
}//end class
