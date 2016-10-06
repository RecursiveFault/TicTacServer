/*
 * tictacSemaphore
 * Matthew Moellman
 * CSC460
 * 15 March 2016
*/
public class tictacSemaphore { //this class is used to hold the win/loss/tie counters, and to update them across syncronized methods
	private int win;
	private int loss;
	private int tie;
	
	tictacSemaphore()
	{
		win = 0;
		tie = 0;
		loss= 0;
	}
	
	public synchronized void updateKey(int won, int tied, int lost)
	{
		if (won==1)
			this.win += 1;
		if (tied==1)
			this.tie += 1;
		if (lost==1)
			this.loss +=1;
	}
	
	public int getWin(){return this.win;}
	public int getTie(){return this.tie;}
	public int getLoss(){return this.loss;}
	
	
	
}
