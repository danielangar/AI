package playerCardGame;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.Callable;

public class Tournament {

    private static final int maxTime = 10; // number of seconds allowed per card.

    private static class Contestant implements Comparable<Contestant> {
	Player player;
	String name;
	int wins;

	public Contestant(Player player, String name) {
	    this.player = player; this.name = name; wins = 0;
	}
	public int compareTo(Contestant other) { return wins - other.wins; }
	public void won() { wins++; }
	public String toString() { return name + "(" + wins + ")"; }
    }

    private static final Contestant[] contestants = {
	new Contestant(RandyP.getPlayer(), "RandyP"),
	//new Contestant(DanielW.getPlayer(), "DanielW"),

	new Contestant(YulderA.getPlayer(), "YulderA"),
    };
    
    

    public static class PlayerCallable implements Callable<Card> {
	private Player player;
	private ArrayList<Card> hand;
	private ArrayList<Card> playedCards;
	private Card trump;
	private int tricks1, tricks2;
	private boolean first;

	public PlayerCallable(Player player, ArrayList<Card> hand, ArrayList<Card> playedCards, Card trump, int tricks1, int tricks2, 
			      boolean first) {
	    this.player = player;
	    this.hand = hand;
	    this.playedCards = playedCards;
	    this.trump = trump;
	    this.tricks1 = tricks1;
	    this.tricks2 = tricks2;
	    this.first = first;
	}

	public Card call() {
	    return first ? player.playFirstCard(hand, playedCards, trump, tricks1, tricks2) 
		: player.playSecondCard(hand, playedCards, trump, tricks1, tricks2);
	}
    }

    public static Card timePlayer(Player player, ArrayList<Card> hand, ArrayList<Card> playedCards, Card trump, int tricks1, int tricks2,
				  boolean first) {
	final ExecutorService service = Executors.newSingleThreadExecutor();
	Card card = null;
	try {
	    final Future<Card> f = service.submit(new PlayerCallable(player, hand, playedCards, trump, tricks1, tricks2, first));
	    card = f.get(maxTime, TimeUnit.SECONDS);
	}
	catch (final TimeoutException e) {
	    card = null;
	}
	catch (final Exception e) {
	    throw new RuntimeException(e);
	}
	finally {
	    service.shutdown();
	    return card;
	}
    }

    private static void playHand(Contestant c1, Contestant c2) {
	Deck deck = new Deck();
	deck.shuffle();
	int numCards = 7;  // deal two hands of 7 cards and trump card
	ArrayList<Card> hand1 = new ArrayList<Card>();
	ArrayList<Card> hand2 = new ArrayList<Card>();
	ArrayList<Card> playedCards = new ArrayList<Card>();
	for(int i=0; i<numCards; i++) {
	    hand1.add(deck.get(2*i));
	    hand2.add(deck.get(2*i+1));
	}
	Card trump = deck.get(2*numCards);
	int tricks1 = 0, tricks2 = 0;
	System.out.println("Trump = " + trump + "\n" + c1.name + ": " + hand1 + "\n" + c2.name + ": " + hand2);
	while(!hand2.isEmpty()) {  // play hand
	    Card card1 = timePlayer(c1.player, hand1, playedCards, trump, tricks1, tricks2, true);
	    if(card1 == null) {
		tricks1 = 0; tricks2 = 7; 
		System.out.println(c1.name + " took too long.");
		break; 
	    }	
	    if(hand1.indexOf(card1) == -1) {
		tricks1 = 0; tricks2 = 7; 
		System.out.println(c1.name + " forfeits - no such card"); 
		break; 
	    }
	    hand1.remove(card1);
	    playedCards.add(card1);
	    Card card2 = timePlayer(c2.player, hand2, playedCards, trump, tricks1, tricks2, false);
	    if(card2 == null) {
		tricks1 = 7; tricks2 = 0; 
		System.out.println(c2.name + " took too long.");
		break; 
	    }	
	    if(hand2.indexOf(card2) == -1) { 
		tricks1 = 7; tricks2 = 0; 
		System.out.println(c2.name + " forfeits - no such card"); 
		break;
	    }
	    boolean canFollow = false;
	    for(Card c : hand2)
		if(c.suit == card1.suit)
		    canFollow = true;
	    if(canFollow && card2.suit != card1.suit && card2.suit != trump.suit) {
		tricks1 = 7; tricks2 = 0; 
		System.out.println(c2.name + " forfeits - illegal play");
		break;
	    }
	    hand2.remove(card2);
	    playedCards.add(card2);
	    if(card1.greater(card2, trump))
		tricks1++; 
	    else 
		tricks2++;
	}
	if(tricks1 > 3)
	    { c1.won(); System.out.println(c1.name + " wins the hand."); }
	else
	    { c2.won(); System.out.println(c2.name + " wins the hand."); }
    }
    
    public static void main(String[] args) {
	int numMatches = 10;
	int k = 0;
	for(int i=0; i<contestants.length; i++)
	    for(int j=i+1; j<contestants.length; j++) {
		System.out.println(contestants[i].name + " vs. " + contestants[j].name);
		for(int n=0; n<numMatches; n++) {
		    System.out.print((++k) + ": ");
		    playHand(contestants[i], contestants[j]);
		    playHand(contestants[j], contestants[i]);
		    System.out.println(contestants[i] + " " + contestants[j]);
		}
	    }
	Arrays.sort(contestants);
	for(int i=0; i<contestants.length; i++)
	    System.out.println(contestants[i]);
    }
}


