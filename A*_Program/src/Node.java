//Daniel Angarita

import java.util.*;
import java.lang.*;

public class Node implements Denumerable, Comparable<Node> {
	int num;
	int g;
	int f;
	boolean expanded;
	State state;
	Node parent;
	
	
	
	public int getNumber() {
		return num;
	}
	public void setNumber(int x){
		this.num = x;
	}
	public int compareTo(Node node){
		return this.f - node.f;
	}
	
	public Node(State state, Node parentNode, int g) {
		this.expanded = false;
		this.g = g;
		this.f = g + state.h;
		this.state = state;
		this.parent = parentNode;
	}
	
	public String toString(){
		return state.toString();
	}
	
	public int hashCode(){
		return state.hashCode();
	}
	
	public boolean equals(Object o){
		return state.equals(((Node)o).state);
	}
}