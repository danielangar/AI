package playerCardGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class YulderA {
	public static Player getPlayer() {
		return new GamePlayer();
	}

	public static class GamePlayer implements Player {
		private static Deck theDeck=new Deck();
		
		public Card playFirstCard(ArrayList<Card> hand,ArrayList<Card> playedCards, Card trump, int tricks1,int tricks2) {
			Card c = bestCard(hand, playedCards, trump, tricks1, tricks2);
			System.out.println("Yulder Plays: "+c);
			return c;
		}

		public Card playSecondCard(ArrayList<Card> hand, ArrayList<Card> playedCards, Card trump, int tricks1,int tricks2) {
			Card c =bestCard(hand, playedCards, trump, tricks1, tricks2);
			System.out.println("Yulder Plays: "+c);
			return c;
			
		}
		

		private Card bestCard(ArrayList<Card> hand, ArrayList<Card> playedCards, Card trump, int tricks1, int tricks2) {
			
			int[] maxes = new int[hand.size()];
			long start = System.currentTimeMillis();
			for(int i=0; i < 100 && System.currentTimeMillis()<start+5000; i++){
				Card[] r = randomHand((14-playedCards.size())-hand.size(), hand, playedCards);
				Card[] hd = new Card[hand.size()];
				Card[] plc = new Card[playedCards.size()];
				int bestVal =Integer.MIN_VALUE;
				Card bestCard=null;
				Node root = new Node(hand.toArray(hd), r, playedCards.toArray(plc),tricks1, tricks2, trump, true );
				for(Node gs : root.getSuccessors()){
					int minMax= miniAlphaBeta(gs,Integer.MIN_VALUE,Integer.MAX_VALUE, true);
					
					if(minMax>bestVal){
						bestVal = minMax;
						bestCard = gs.playedCard[gs.playedCard.length-1];
					}
				}
				maxes[hand.indexOf(bestCard)]++;
			}
			int max = Integer.MIN_VALUE;
			int maxind = 0;
			for(int i=0; i < hand.size(); i++){
				if(maxes[i]>max){
					max = maxes[i];
					maxind =i;
				}
			}
			return hand.get(maxind);
		}
		
		
		private Card[] randomHand(int z, ArrayList<Card> hand, ArrayList<Card> playedCards){
			theDeck.shuffle();
			Card[] nhand = new Card[z];
			int i = 0;
			int j = 0;
			while(i<z)
			{
//				System.out.println("i" + " " + i + " " + "j" +  " " + j);
				if(hand.contains(theDeck.get(j)) && playedCards.contains(theDeck.get(i))){
					j++;
				}
				else
				{
					nhand[i]=theDeck.get(j);
					i++;
					j++;
				}
			}
			return nhand;
		}


		public class Node {
			Card[] mHand;//my hand
			Card[] rHand;//rival hand
			Card[] playedCard;
			int trick1;
			int trick2;
			Card trump;
			boolean turn;

			Node(Card[] mH, Card[] rH, Card[] pyd, int trick1, int trick2, Card trump, boolean turn) {
				this.mHand = mH;
				this.rHand = rH;
				this.playedCard = pyd;
				this.trick1 = trick1;
				this.trick2 = trick2;
				this.trump= trump;
				this.turn = turn;
			}

			public ArrayList<Node> getSuccessors() {

				ArrayList<Node> successors = new ArrayList<Node>();

				if (turn == true) {
					if (playedCard.length % 2 == 0) {
						for (int i = 0; i < mHand.length; i++) {
							Card[] newmHand;
							Card[] newPlayedCard = Arrays.copyOf(playedCard, playedCard.length+1);
							newPlayedCard[newPlayedCard.length-1] = mHand[i];

							newmHand=remove1(mHand, i);

							successors.add(new Node(newmHand, rHand, newPlayedCard, trick1, trick2, trump, !turn));
						}
					} else {
						for (int i = 0; i < mHand.length; i++) {
							if(mHand[i].suit==playedCard[playedCard.length-1].suit || mHand[i].suit==trump.suit){


								Card[] newmHand;
								Card[] newPlayedCard = Arrays.copyOf(playedCard, playedCard.length+1);
								newPlayedCard[newPlayedCard.length-1] = mHand[i];

								newmHand=remove1(mHand, i);
								if(mHand[i].greater(playedCard[playedCard.length-1], trump)){
									successors.add(new Node(newmHand, rHand, newPlayedCard, trick1+1, trick2, trump, !turn));
								}
								else{
									successors.add(new Node(newmHand, rHand, newPlayedCard, trick1, trick2+1, trump, !turn));
								}

							}

						}
						if(successors.isEmpty()){
							for (int i = 0; i < mHand.length; i++) {
								Card[] newmHand;
								Card[] newPlayedCard = Arrays.copyOf(playedCard, playedCard.length+1);
								newPlayedCard[newPlayedCard.length-1] = mHand[i];

								newmHand=remove1(mHand, i);
								if(mHand[i].greater(playedCard[playedCard.length-1], trump)){
									successors.add(new Node(newmHand, rHand, newPlayedCard, trick1+1, trick2, trump, !turn));
								}
								else{
									successors.add(new Node(newmHand, rHand, newPlayedCard, trick1, trick2+1, trump, !turn));
								}
							}
						}
					}
				} else {
					
					if (playedCard.length % 2 == 0) {
						for (int i = 0; i < rHand.length; i++) {
							Card[] newrHand;
							Card[] newPlayedCard = Arrays.copyOf(playedCard, playedCard.length+1);
							newPlayedCard[newPlayedCard.length-1] = rHand[i];

							newrHand=remove1(rHand, i);

							successors.add(new Node(mHand, newrHand, newPlayedCard, trick1, trick2, trump, !turn));
						}
					} else {
						for (int i = 0; i < rHand.length; i++) {
							if(rHand[i].suit==playedCard[playedCard.length-1].suit || rHand[i].suit==trump.suit){


								Card[] newrHand;
								Card[] newPlayedCard = Arrays.copyOf(playedCard, playedCard.length+1);
								newPlayedCard[newPlayedCard.length-1] = rHand[i];

								newrHand=remove1(rHand, i);
								if(rHand[i].greater(playedCard[playedCard.length-1], trump)){
									successors.add(new Node(mHand, newrHand, newPlayedCard, trick1, trick2+1, trump, !turn));
								}
								else{
									successors.add(new Node(mHand,newrHand, newPlayedCard, trick1+1, trick2, trump, !turn));
								}

							}

						}
						if(successors.isEmpty()){
							for (int i = 0; i < rHand.length; i++) {
								Card[] newrHand;
								Card[] newPlayedCard = Arrays.copyOf(playedCard, playedCard.length+1);
								newPlayedCard[newPlayedCard.length-1] = rHand[i];

								newrHand=remove1(rHand, i);
								if(rHand[i].greater(playedCard[playedCard.length-1], trump)){
									successors.add(new Node(mHand,newrHand,newPlayedCard, trick1, trick2+1, trump, !turn));
								}
								else{
									successors.add(new Node(mHand,newrHand,newPlayedCard, trick1+1, trick2, trump, !turn));
								}
							}
						}
					}

				}

				return successors;
			}
		}

		public static Card[] remove1(Card[] arr, int k) {
			Card[] nArray = new Card[arr.length - 1];
			for (int i = 0; i < arr.length; i++) {
				if (i < k) {
					nArray[i] = arr[i];
				}
				if (i > k) {
					nArray[i - 1] = arr[i];
				}
			}

			return nArray;

		}

		public static int miniAlphaBeta(Node node, int alpha, int beta, boolean turn) {
			int bestValue;

			if (node.playedCard.length == 14) {
				return node.trick1 -node.trick2;
			} 
			
			else if (turn) {
				bestValue = alpha;

				for (Node gameNode : node.getSuccessors()) {
					int childValue = miniAlphaBeta(gameNode, bestValue, beta, false);
					bestValue = Math.max(bestValue, childValue);
					if (beta <= bestValue) {
						break;
					}
				}
			} else {
				bestValue = beta;
				for (Node gameNode : node.getSuccessors()) {
					int childValue = miniAlphaBeta(gameNode, alpha, bestValue, true);
					bestValue = Math.min(bestValue, childValue);
					if (bestValue <= alpha) {
						break;
					}
				}
			}
			return bestValue;
		}
//		public static void main(String[] args) {
//		//[5 of hearts (14), 5 of diamonds (13), 10 of hearts (34), 8 of hearts (26), 9 of hearts (30), 9 of clubs (28), 4 of clubs (8)] [3 of diamonds (5)] 9 of diamonds (29) 0 0
//			int[] h = {14, 13, 34, 26, 30, 28, 8};
//			ArrayList<Card> hand = new ArrayList<Card>();
//			ArrayList<Card> played = new ArrayList<Card>();
//			for(int x : h)
//			    hand.add(new Card(x));
//			played.add(new Card(5));
//			getPlayer().playSecondCard(hand, played, new Card(29), 0, 0);
//		    }

	}
}
