//Daniel Angarita

import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;

public class Puzzle {
	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);

		// This array is for the indexes of the tiles
		int[] z = { 2, 3, 8, 9, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
				26, 27, 32, 33 };
		
		int[] board = new int[36];
		System.out.println("Please enter 19 digits in the order which you prefer:\n");

		// Fill the white spaces with -1
		Arrays.fill(board, -1);

		// Loading the board
		for (int i = 0; i < 20; i++) {
			board[z[i]] = sc.nextInt();
		}
		
		// Initial state board
		State initialState = new State(board);
		
		// Manhattan distance for the initial state
		initialState.calManhattanDistanceCost();

		// Initial Node
		Node initialNode = new Node(initialState, null, 0);

		// Queue for the nodes
		MinPQ<Node> queue = new MinPQ<Node>();

		// Hash Map to keep track of old and new nodes
		HashMap<State, Node> map = new HashMap<State, Node>();

		// always update after add
		queue.add(initialNode);
		queue.update(initialNode);
		// Key for the HashMap. Key is the hashCode of the state. Values is the
		// node
		State key = initialNode.state;
		map.put(key, initialNode);

		// Calling the A* algorithm
		AStar(initialNode, map, queue);
	}

	public static void AStar(Node initialNode, HashMap<State, Node> map,MinPQ<Node> queue) {

		Node currentNode = null;
		int moves=0;

		// Looping to apply the whole algorithm
		while (!queue.isEmpty()) {
			
			// Remove the frontier
			currentNode = queue.remove();
			
			//Print the node that is remove from the frontier
			currentNode.state.showBoard();
			moves++;
			
			//Print the size of the queue
			System.out.println("queue size:" + queue.size());
			
			
			// Generate currentNode successors
			Vector<State> newStates = currentNode.state.getSuccessors();
			Vector<Node> newNodes = new Vector<Node>();
			
			for (State s : newStates){
				newNodes.add(new Node(s, currentNode, currentNode.g + 1));
			}
			
			//Print # successors and h
			System.out.println("Number of successors:" + " " + newNodes.size());
			System.out.println("h:" + currentNode.state.h);
			System.out.println("map:" + map.size());
			
			
			currentNode.expanded = true;
				

			// check if the currentNode gets the goal
			if (currentNode.state.h == 0) {
				System.out.println("We got the goal!!!!!");
				currentNode.state.showBoard();
				System.out.println("It took " + ++moves + " moves");
				
				break;
			} else {
				
				// Check for each successor if successor is the goal and stop it
				for (Node n : newNodes) {
					// only make new node after you have checked to see if this
					// state has been seen before or not
					// Check if a node with the same position in the hasMap as
					// successor, has a lower f than successor
					
					if (map.containsKey(n.state)) {
						
						// Getting the node on the map that has the same
						// position in the hashMap as successor
						
						Node hashMapNode = map.get(n.state);
						
						if (!hashMapNode.expanded && (n.g+1) < hashMapNode.g) {
							hashMapNode.state = n.state;
							hashMapNode.g = n.g+1;
							hashMapNode.f = n.f;
							hashMapNode.parent = n.parent;
							queue.update(hashMapNode);
						} else if (hashMapNode.expanded && (n.g+1) < hashMapNode.g) {
							
						}

					} else {
						queue.add(n);
						queue.update(n);
						map.put(n.state, n);
					}
				}
			}
			
		}
	}
}