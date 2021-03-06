package crazyeights.syd.jjj;


import defaults.syd.jjj.CardCollection;
import defaults.syd.jjj.Card;
import defaults.syd.jjj.Deck;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Simulates a game of Crazy Eights. See https://en.wikipedia.org/wiki/Crazy_Eights for basic play
 * and scoring rules.
 */
public class Eights {

  private CardCollection drawPile;
  private CardCollection discardPile;
  private Scanner in;
  private ArrayList<EightsPlayer> players;

  /**
   * Initializes the state of the game.
   */
  public Eights() {
    Deck deck = new Deck("Deck");
    deck.shuffle();

    // create an ArrayList of players
    this.players = new ArrayList<EightsPlayer>();

    // turn one card face up
    discardPile = new CardCollection("Discards");
    deck.deal(discardPile, 1);

    // put the rest of the deck face down
    drawPile = new CardCollection("Draw pile");
    deck.dealAll(drawPile);

    // create the scanner we'll use to wait for the user
    in = new Scanner(System.in);
  }


  /**
   * Adds the given player to the collection.
   */
  public void createPlayer(EightsPlayer player) {
    players.add(player);
  }

  /**
   * Prompts user to create a player name and uses input to create it.
   * @return The player's name
   */
  public String playerName() {
    System.out.println("Enter player name: ");
    String name = in.next();
    while (name == "") {
      name = in.nextLine();
    }
    return name;
  }

  /**
   * Gives player within collection a name and a hand with 5 cards.
   */
  public void equipPlayer() {
    EightsPlayer player = new EightsPlayer(playerName());
    createPlayer(player);
    drawPile.deal(player.getHand(), 5);
  }

  /**
   * Returns a player from the array at a given index.
   */
  public EightsPlayer getPlayer(int i) {
    return players.get(i);
  }

  /**
   * Returns index of a given player.
   */
  public int getPlayerIndex(EightsPlayer player) {
    return players.indexOf(player);
  }


  /**
   * Returns true if either hand is empty.
   */
  public boolean isDone() {
    for (int x = 0; x < players.size(); x++) {
      if (getPlayer(x).getHand().empty()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Moves cards from the discard pile to the draw pile and shuffles.
   */
  public void reshuffle() {
    // save the top card
    Card convert = discardPile.popCard();
    EightsCard prev = new EightsCard(0, 0);
    prev = prev.convert(convert);
    

    // move the rest of the cards
    discardPile.dealAll(drawPile);

    // put the top card back
    discardPile.addCard(prev);

    // shuffle the draw pile
    drawPile.shuffle();
  }

  /**
   * Returns a card from the draw pile.
   */
  public EightsCard draw() {
    if (drawPile.empty()) {
      reshuffle();
    }
    Card convert = drawPile.popCard();
    EightsCard prev = new EightsCard(0, 0);
    return prev.convert(convert);
  }

  /**
   * Switches players.
   */
  public EightsPlayer nextPlayer(EightsPlayer current) {

    if (getPlayerIndex(current) < players.size() - 1) {
      return getPlayer(getPlayerIndex(current) + 1);
    }
    if (getPlayerIndex(current) == players.size() - 1) {
      return getPlayer(getPlayerIndex(current) + 1 - players.size());
    }
    return null;

  }

  /**
   * Displays the state of the game.
   */
  public void displayState() {
    for (int x = 0; x < players.size(); x++) {
      getPlayer(x).display();
    }
    discardPile.display();
    System.out.print("Draw pile: ");
    System.out.println(drawPile.size() + " cards");
  }

  /**
   * Waits for the user to press enter.
   */
  public void waitForUser() {
    in.nextLine();
  }

  /**
   * One player takes a turn.
   */
  public void takeTurn(EightsPlayer player) {
    Card convert = discardPile.last();
    EightsCard prev = new EightsCard(0, 0);
    prev = prev.convert(convert);    
    EightsCard next = player.play(this, prev);
    discardPile.addCard(next);

    System.out.println(player.getName() + " plays " + next);
    System.out.println();
  }

  /**
   * Plays the game.
   */
  public void playGame() {

    System.out.println("It's time to play Crazy Eights!");
    System.out.println();
    System.out.println("Enter number of players: ");

    int nop = in.nextInt();
    while (nop > 4 || nop < 2) {
      System.err.println("Number of players must be between 2 and 4!");
      System.out.println();
      System.out.println("Re-enter number of players: ");
      nop = in.nextInt();
    }

    for (int i = 0; i < nop; i++) {
      equipPlayer();
    }
    EightsPlayer player = getPlayer(0);

    // keep playing until there's a winner
    while (!isDone()) {
      displayState();
      waitForUser();
      takeTurn(player);
      player = nextPlayer(player);
    }
    // display the final score
    for (int x = 0; x < players.size(); x++) {
      getPlayer(x).displayScore();
    }
  }

  /**
   * Creates the game and runs it.
   */
  public static void main(String[] args) {

    Eights game = new Eights();
    game.playGame();

  }
}
