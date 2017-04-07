//Daniel Angarita

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

public class State {

	int zeroLoc;
	int h; // Manhattan Distance
	int[] newBoard = new int[36];
	protected int[] board;

	public static final int[] gtiles = { -1, -1, 0, 1, -1, -1, -1, -1, 2, 3,
			-1, -1, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, -1, -1, 16, 17,
			-1, -1, -1, -1, 18, 19, -1, -1 };

	public enum Direction {
		left, right, up, down;
	}

	public State(int[] board) {
		this.board = board;
		h = calManhattanDistanceCost();
	}

	// Calculate the Manhattan Distance
	public int calManhattanDistanceCost() {

		int h = 0;
		int targetX = 0;
		int targetY = 0;
		int currentX = 0;
		int currentY = 0;
		

		for (int i = 0; i < board.length; i++) {

			// Misplaced tiles

			if (board[i] != -1) {
				if (board[i] == 0)
					zeroLoc = i;
				currentX = i % 6;
				currentY = i / 6;

				for (int j = 0; j < gtiles.length; j++) {
					if (board[i] == gtiles[j]) {
						targetX = j % 6;
						targetY = j / 6;
						h += (Math.abs(targetY - currentY) + Math.abs(targetX - currentX));
					}
				}

			}
		}

		return h;

	}

	public Vector<State> getSuccessors() {
		
		
		// Let's store the new states in a vector
		Vector<State> states = new Vector<State>();

		// If the move is legal, let's create another board and add it to the
		// vector
		State t;
		for (Direction direction : Direction.values())
			if((t = move(direction))!=null)
				states.add(t);
		
		return states;
	}

	public int[] swap(int[] nBoard, int i, int j) {
		int[] neoBoard = Arrays.copyOf(nBoard, board.length);
		int t = neoBoard[i];
		neoBoard[i] = neoBoard[j];
		neoBoard[j] = t;
		return neoBoard;

	}

	public State move(Direction dir) {
		int currentX = zeroLoc % 6;
		int currentY = zeroLoc / 6;
		// Direction direction=null;

		switch (dir) {
		// If tile is at the top, can't move up
		case up:
			if (currentY != 0 && board[zeroLoc - 6] != -1) {
				return new State(swap(board, zeroLoc, zeroLoc - 6));
			}
			break;
			// If tile is at the lowest level, cannot move down
		case down:
			if (currentY < (6 - 1) && board[zeroLoc + 6] != -1) {
				return new State(swap(board, zeroLoc, zeroLoc + 6));

			}
			break;
			// If tile is at the left most, can't move left
		case left:
			if (currentX != 0 && board[zeroLoc - 1] != -1) {
				return new State(swap(board, zeroLoc, zeroLoc - 1));
			}
			break;
			// If tile is at the right most, can't move right
		case right:
			if (currentX < (6 - 1) && board[zeroLoc + 1] != -1) {
				return new State(swap(board, zeroLoc, zeroLoc + 1));

			}
			break;
		}
		return null;
	}

	public PrintStream showBoard() {
		String x = "";
		String str2= " ";
		String ne = "-1";
		for (int i = 0; i < 36; i++) {
			if(board[i] != -1) {
			 x += board[i] + "\t" ;
			}
			else
				x+="\t";

			
			if ((i + 1) % 6 == 0)
				x += "\n";
		}
		System.out.println("______________________________________________");
		return System.out.printf("%1s", x);

	}

}


