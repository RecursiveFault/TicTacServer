# TicTacServer
Multithreaded TicTacToe, run by locally connected server and client

tictacServerMulti is the main for the server, which runs tictacServerThreads which implement the full logic for randomly deciding on a move from availible spaces, and determining if user sent moves or its own move causes a win condition.

tictacServer is a non-multithreaded variant of tictacThread, with the same logic. 

tictacClient is run by the client to connect to a tictacServerMulti which spawns a thread, or directly to a tictacServer. 
The user receives an output of the game board on prompt for their turn. Server determined moves are received and user input moves are sent.
