package playerCardGame;

import java.util.Random;
import java.util.ArrayList;

public class RandyP {

    public static Player getPlayer() { return new RandomPlayer(); }

    public static class RandomPlayer implements Player {

	private final static Random random = new Random();

	public Card playFirstCard(ArrayList<Card> hand, ArrayList<Card> playedCards, Card trump, int tricks1, int tricks2) {
		Card rand = hand.get(random.nextInt(hand.size()));
		System.out.println("Randy Plays: "+rand);
	    return rand;
	}

	public Card playSecondCard(ArrayList<Card> hand, ArrayList<Card> playedCards, Card trump,int tricks1, int tricks2) {
		
	    Card card1 = playedCards.get(playedCards.size() - 1);
	    ArrayList<Card> ok = new ArrayList<Card>();
	    for(Card c : hand)
		if(c.suit == trump.suit || c.suit == card1.suit)
		    ok.add(c);
	    Card rand = ok.isEmpty() ? hand.get(random.nextInt(hand.size())) : ok.get(random.nextInt(ok.size()));
	   System.out.println("Randy Plays: "+rand);
	    return rand;
	}
    }
}
