package playerCardGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class DanielW {
	public static Player getPlayer() {
		return new WatsonPlayer();
	}

	public static class WatsonPlayer implements Player {
		static Deck theDeck = new Deck();
		private final static int numSamples = 100;

		public Card playFirstCard(ArrayList<Card> hand, ArrayList<Card> playedCards, Card trump, int tricks1, int tricks2) {
			int[] handints = new int[hand.size()];
			int[] playedints = new int[playedCards.size()];
			for (int i = 0; i < hand.size(); i++) {
				handints[i] = hand.get(i).getNumber();
			}
			for (int i = 0; i < playedCards.size(); i++) {
				playedints[i] = playedCards.get(i).getNumber();
			}

			gameNode root = new gameNode(handints, null, playedints, trump.suit.ordinal(), tricks1, tricks2, true);
			int card = root.bestCard(true, hand, playedCards);
			Card toPlayCard = hand.get(card);
			System.out.println("\nWatson Playing " + toPlayCard.toString() + "\n");
			return toPlayCard;
		}

		public Card playSecondCard(ArrayList<Card> hand, ArrayList<Card> playedCards, Card trump, int tricks1, int tricks2) {
			int[] handints = new int[hand.size()];
			int[] playedints = new int[playedCards.size()];
			for (int i = 0; i < hand.size(); i++) {
				handints[i] = hand.get(i).getNumber();
			}
			for (int i = 0; i < playedCards.size(); i++) {
				playedints[i] = playedCards.get(i).getNumber();
			}
			gameNode root = new gameNode(handints, null, playedints, trump.suit.ordinal(), tricks1, tricks2, true);
			int card = root.bestCard(false, hand, playedCards);
			Card toPlayCard = hand.get(card);
			System.out.println("\nWatson Playing " + toPlayCard.toString() + "\n");
			return toPlayCard;
		}

		private int[] randHand(int newSize, ArrayList<Card> hand, ArrayList<Card> playedCards) {
			theDeck.shuffle();
			int[] nhand = new int[newSize];
			int i = 0;
			int j=0;
			while(i<nhand.length){
				if(!hand.contains(theDeck.get(j))&&!playedCards.contains(theDeck.get(j))){
					nhand[i] = theDeck.get(j).getNumber();
					i++;
					j++;
				}
				else {
					j++;
				}
			}
			return nhand;
		}

		private int minimax(gameNode gn) {
			if (gn.playedCards.length == 14) {
				//if(gn.tricks1<gn.tricks2)
				//	System.out.println(gn);
				return gn.tricks1 - gn.tricks2;
			}
			if (gn.maximizing) {
				//System.out.println("Maxing: ");
				int bestValue = -8;
				if (gn.playedCards.length % 2 == 0) {
					for (gameNode nextChild : gn.getChildrenFirstMove()) {
						bestValue = Math.max(bestValue, minimax(nextChild));
					}
				} else {
					for (gameNode nextChild : gn.getChildrenSecondMove()) {
						bestValue = Math.max(bestValue, minimax(nextChild));
					}
				}
				//System.out.println("//"+bestValue);
				return bestValue;
			} else {
				//System.out.println("minning: ");
				int bestValue = 8;
				if (gn.playedCards.length % 2 == 0) {
					for (gameNode nextChild : gn.getChildrenFirstMove()) {
						bestValue = Math.min(bestValue, minimax(nextChild));
					}
				} else {
					for (gameNode nextChild : gn.getChildrenSecondMove()) {
						bestValue = Math.min(bestValue, minimax(nextChild));
					}
				}
				//System.out.println("//"+bestValue);
				return bestValue;
			}
		}

		private class gameNode {
			int[] hand, ohand, playedCards;
			int trump;
			int tricks1, tricks2;
			// ArrayList<gameNode> children;
			boolean maximizing;

			public gameNode(int[] hand1, int[] hand2, int[] pCards, int atrump, int atricks1, int atricks2, boolean max) {
				hand = hand1;
				ohand = hand2;
				playedCards = pCards;
				trump = atrump;
				tricks1 = atricks1;
				tricks2 = atricks2;
				maximizing = max;
			}

			public int bestCard(boolean first, ArrayList<Card> theHand, ArrayList<Card> used) {
				int[] wincount = new int[hand.length];
				long start = System.currentTimeMillis();
				for (int i = 0; (i < numSamples) && (System.currentTimeMillis()<start+5000); i++) {
					if (first)
						ohand = randHand(hand.length, theHand, used);
					else {
						ohand = randHand(hand.length - 1, theHand, used);
					}
					int bestCard = -1;
					ArrayList<gameNode> gns;
					if (first)
						gns = getChildrenFirstMove();
					else
						gns = getChildrenSecondMove();
					int bestVal = Integer.MIN_VALUE;
					for (gameNode g : gns) {
						long s = System.currentTimeMillis();
						int mnmx = minimax(g);
						//System.out.println(System.currentTimeMillis()-s);
					//	System.out.println(mnmx+"\n --- \n");
						// System.out.println(mnmx);
						if (mnmx > bestVal) {
							bestCard = g.playedCards[g.playedCards.length - 1];
							bestVal = mnmx;
						}
					}
				//	System.out.println("\n");
					int bestCardPos = -1;
					for (int j = 0; j < hand.length; j++) {
						if (hand[j] == bestCard)
							bestCardPos = j;
					}
					wincount[bestCardPos]++;
				}
				int maxWins = 0;
				int max = 0;
				//System.out.print("Hand ratings: ");
				for (int i = 0; i < wincount.length; i++) {
					//System.out.print(wincount[i] + " ");
					if (wincount[i] > maxWins) {
						max = i;
						maxWins = wincount[i];
					}
				}
				return max;
			}

			private ArrayList<gameNode> getChildrenFirstMove() { // moving first
				//if (hand.length > 5)
					//System.out.println(toString());
				ArrayList<gameNode> children = new ArrayList<gameNode>();
				if (maximizing) { // friendly turn
					for (int i = 0; i < hand.length; i++) {
						int[] nhand = new int[hand.length - 1];
						int[] nplayedCards = new int[playedCards.length + 1];
						for (int j = 0; j < playedCards.length; j++) {
							nplayedCards[j] = playedCards[j];
						}
						nplayedCards[nplayedCards.length - 1] = hand[i];
						for (int j = 0; j < hand.length; j++) {
							if (j < i)
								nhand[j] = hand[j];
							if (j > i)
								nhand[j - 1] = hand[j];
						}
						children.add(new gameNode(nhand, ohand, nplayedCards, trump, tricks1, tricks2, !maximizing));
					}
				} else { // enemy turn
					//System.out.print("enemy turn");
					for (int i = 0; i < ohand.length; i++) {
						int[] nhand = new int[ohand.length - 1];
						int[] nplayedCards = new int[playedCards.length + 1];
						for (int j = 0; j < playedCards.length; j++) {
							nplayedCards[j] = playedCards[j];
						}
						nplayedCards[nplayedCards.length - 1] = ohand[i];
						for (int j = 0; j < ohand.length; j++) {
							if (j < i)
								nhand[j] = ohand[j];
							if (j > i)
								nhand[j - 1] = ohand[j];
						}
						children.add(new gameNode(hand, nhand, nplayedCards, trump, tricks1, tricks2, !maximizing));
					}
				}
				//if (hand.length > 5)
				//	System.out.println("\nchildren:\n" + children);
				return children;
			}

			private ArrayList<gameNode> getChildrenSecondMove() {
				//if (hand.length > 5)
				//	System.out.print(toString());
				ArrayList<gameNode> children = new ArrayList<gameNode>();
				if (maximizing) {
					for (int i = 0; i < hand.length; i++) {
						if (playedCards[playedCards.length - 1] % 4 == hand[i] % 4 || hand[i] % 4 == trump) {
							int[] nhand = new int[hand.length - 1];
							int[] nplayedCards = new int[playedCards.length + 1];
							for (int j = 0; j < playedCards.length; j++) {
								nplayedCards[j] = playedCards[j];
							}
							nplayedCards[nplayedCards.length - 1] = hand[i];
							for (int j = 0; j < hand.length; j++) {
								if (j < i)
									nhand[j] = hand[j];
								if (j > i)
									nhand[j - 1] = hand[j];
							}
							if (aBeatsb(nplayedCards[nplayedCards.length - 2], nplayedCards[nplayedCards.length - 1])) {
								children.add(new gameNode(nhand, ohand, nplayedCards, trump, tricks1, tricks2 + 1, !maximizing));
							} else {
								children.add(new gameNode(nhand, ohand, nplayedCards, trump, tricks1 + 1, tricks2, !maximizing));
							}
						}
					}
					if (children.isEmpty()) {
						for (int i = 0; i < hand.length; i++) {
							int[] nhand = new int[hand.length - 1];
							int[] nplayedCards = new int[playedCards.length + 1];
							for (int j = 0; j < playedCards.length; j++) {
								nplayedCards[j] = playedCards[j];
							}
							nplayedCards[nplayedCards.length - 1] = hand[i];
							for (int j = 0; j < hand.length; j++) {
								if (j < i)
									nhand[j] = hand[j];
								if (j > i)
									nhand[j - 1] = hand[j];
							}
							if (aBeatsb(nplayedCards[nplayedCards.length - 2], nplayedCards[nplayedCards.length - 1])) {
								children.add(new gameNode(nhand, ohand, nplayedCards, trump, tricks1, tricks2 + 1, !maximizing));
							} else {
								children.add(new gameNode(nhand, ohand, nplayedCards, trump, tricks1 + 1, tricks2, !maximizing));
							}
						}

					}
				} else { // enemy turn
					//System.out.print("enemy turn");
					for (int i = 0; i < ohand.length; i++) {
						if (playedCards[playedCards.length - 1] % 4 == ohand[i] % 4 || ohand[i] % 4 == trump) {
							int[] nhand = new int[ohand.length - 1];
							int[] nplayedCards = new int[playedCards.length + 1];
							for (int j = 0; j < playedCards.length; j++) {
								nplayedCards[j] = playedCards[j];
							}
							nplayedCards[nplayedCards.length - 1] = ohand[i];
							for (int j = 0; j < ohand.length; j++) {
								if (j < i)
									nhand[j] = ohand[j];
								if (j > i)
									nhand[j - 1] = ohand[j];
							}
							if (aBeatsb(nplayedCards[nplayedCards.length - 2], nplayedCards[nplayedCards.length - 1])) {
								children.add(new gameNode(hand, nhand, nplayedCards, trump, tricks1 + 1, tricks2, !maximizing));
							} else {
								children.add(new gameNode(hand, nhand, nplayedCards, trump, tricks1, tricks2 + 1, !maximizing));
							}
						}
					}
					if (children.isEmpty()) {
						for (int i = 0; i < hand.length; i++) {
							int[] nhand = new int[ohand.length - 1];
							int[] nplayedCards = new int[playedCards.length + 1];
							for (int j = 0; j < playedCards.length; j++) {
								nplayedCards[j] = playedCards[j];
							}
							nplayedCards[nplayedCards.length - 1] = ohand[i];
							for (int j = 0; j < ohand.length; j++) {
								if (j < i)
									nhand[j] = ohand[j];
								if (j > i)
									nhand[j - 1] = ohand[j];
							}
							if (aBeatsb(nplayedCards[nplayedCards.length - 2], nplayedCards[nplayedCards.length - 1])) {
								children.add(new gameNode(hand, nhand, nplayedCards, trump, tricks1 + 1, tricks2, !maximizing));
							} else {
								children.add(new gameNode(hand, nhand, nplayedCards, trump, tricks1, tricks2 + 1, !maximizing));
							}
						}

					}
				}
				//if (hand.length > 5)
				//	System.out.println("\nchildren:\n" + children);
				return children;
			}

			private boolean aBeatsb(int a, int b) { // a is led card
				if (a % 4 == trump) {
					if (b % 4 == trump)
						return a >= b;
					else
						return true;
				} else {
					if (b % 4 == trump)
						return false;
					else
						return a >= b || b % 4 != a % 4;

				}
			}

			public String toString() {
				StringBuilder sBuilder = new StringBuilder();
				sBuilder.append("\nhand1: ");
				for (int i = 0; i < hand.length; i++) {
					sBuilder.append(hand[i] + " ");
				}
				sBuilder.append("\n");
				sBuilder.append("hand2: ");
				for (int i = 0; i < ohand.length; i++) {
					sBuilder.append(ohand[i] + " ");
				}
				sBuilder.append("\n");
				sBuilder.append("Played Cards: ");
				for (int i = 0; i < playedCards.length; i++) {
					sBuilder.append(playedCards[i] + " ");
				}
				sBuilder.append("\ntricks1: " + tricks1 + " tricks2: " + tricks2 + "\n");
				return sBuilder.toString();

			}

		}
	}

}
