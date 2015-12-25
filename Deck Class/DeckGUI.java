/* Nicholas Vadivelu
 * 22 November 2015
 * ICS 4U1
 */

//Show, shuffle, add (select), deal (select), search (select), quicksort, selection, reset

import java.awt.*;
import javax.imageio.*; // allows image loading
import java.io.*; // allows file access
import javax.swing.*;
import java.awt.event.*;  // Needed for ActionListener
import java.lang.Math.*; //need for Math.random();

class DeckGUI extends JFrame
{
  static Deck deck = new Deck ();
  private JComboBox<String> jcb_cardSuit; //used to record suit
  private JSpinner sp_cardNums, sp_dealpos; //used to get the cardnumber and position of the card you want to deal
  private JButton b_search; //button used to search for something
  //======================================================== constructor
  public DeckGUI ()
  {
    // 1... Create/initialize components
    
    //Set up toolbar
    BtnListener btnListener = new BtnListener (); // listener for all buttons
    JToolBar tb = new JToolBar();
    tb.setRollover(true);
    tb.setBorderPainted(true);
    tb.setFloatable(true);
    
    //Shuffle Button
    JButton b_shuffle = new JButton ("Shuffle");
    b_shuffle.addActionListener (btnListener);
    b_shuffle.setToolTipText("Shuffles the cards");
    
    //Set up sorting buttons
    JButton b_quicksort = new JButton ("QuickSort");
    b_quicksort.addActionListener (btnListener);
    b_quicksort.setToolTipText("Sorts the cards based on rank then suit");
    JButton b_selectsort = new JButton ("SelectionSort");
    b_selectsort.addActionListener (btnListener);
    b_selectsort.setToolTipText("Sorts the cards based on suit then rank");
    
    //Set up Adding new cards
    String[] cardnums = {"           2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};
    sp_cardNums = new JSpinner(new SpinnerListModel(cardnums)); 
    sp_cardNums.setToolTipText("Choose the rank of the card you want to add.");
    jcb_cardSuit = new JComboBox<String>();
    jcb_cardSuit.setToolTipText("Choose the suit of the card you want to add.");
    jcb_cardSuit.addItem("Diamonds");
    jcb_cardSuit.addItem("Clubs");
    jcb_cardSuit.addItem("Hearts");
    jcb_cardSuit.addItem("Spades");
    JButton b_add = new JButton ("Add");
    b_add.addActionListener (btnListener);
    b_add.setToolTipText("Add the card that you selected to the deck.");
    
    //Set up search
    b_search = new JButton("Search");
    b_search.addActionListener (btnListener);
    b_search.setToolTipText("Search for card that you selected and flip the other cards over.");
    
    //Set up deal
    sp_dealpos = new JSpinner(new SpinnerNumberModel(1, 1, deck.getDeck().length, 1)); 
    sp_dealpos.setToolTipText("Position of card you want to deal.");
    JButton b_deal = new JButton("Deal");
    b_deal.setToolTipText("Deal the card you selected");
    b_deal.addActionListener (btnListener);
    
    //Set up reset
    JButton b_reset = new JButton("Reset");
    b_reset.setToolTipText("Reset the entire deck");
    b_reset.addActionListener (btnListener);
    
    // 2... Create content pane, set layout
    JPanel content = new JPanel ();        // Create a content pane
    content.setLayout (new BorderLayout ()); // Use BorderLayout for panel
    JPanel north = new JPanel (); // where the buttons, etc. will be
    north.setLayout (new FlowLayout ()); // Use FlowLayout for input area
    DrawArea board = new DrawArea (700, 400); // Area for cards to be displayed
    
    //Adding all components to toolbar
    tb.add (b_shuffle);
    tb.addSeparator();
    tb.add (b_quicksort);
    tb.add (b_selectsort);
    tb.addSeparator();
    tb.add (sp_cardNums);
    tb.add (new JLabel(" of "));
    tb.add (jcb_cardSuit);
    tb.add (b_add);
    tb.add (b_search);
    tb.addSeparator();
    tb.add (sp_dealpos);
    tb.add (b_deal);
    tb.addSeparator();
    tb.add (b_reset);
    
    //add toolbar and panels onto frame
    north.add(tb);
    content.add (north, "North"); // Input area
    content.add (board, "South"); // Deck display area
    JScrollPane scrollPane = new JScrollPane(content, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    // 4... Set this window's attributes.
    setContentPane (scrollPane);
    pack ();
    setTitle ("Deck GUI");
    setSize (700, 500);
    setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo (null);           // Center window.
  }
  
  // put ActionListener class for your buttons here
  class BtnListener implements ActionListener // Button menu
  {
    private int getVal () //returns the value inside the jcombobox and jspinner
    {
      int cardv = (jcb_cardSuit.getSelectedIndex())*13; //gets the suot of the card
      String val =  sp_cardNums.getValue().toString(); //gets the string of the combobox
      val = val.trim(); //gets rid of additional spaces (from the 2)
      if (val.equals("Jack"))
        val = "11";
      else if (val.equals("Queen"))
        val = "12";
      else if (val.equals("King"))
        val = "13";
      else if (val.equals("Ace")) //checks for the face cards
        val = "14";
      cardv += (Integer.parseInt(val)-2);//finds the number of the card
      return cardv;
    }
    public void actionPerformed (ActionEvent e)
    {
      try //just in case the deck is empty
      {
        if (e.getActionCommand ().equals ("Shuffle"))
          deck.shuffle (); // shuffle deck
        else if (e.getActionCommand ().equals ("QuickSort"))
          deck.quickSort (); // sort based on rank first deck
        else if (e.getActionCommand ().equals ("SelectionSort"))
          deck.selectionSort (); // sort based on suitsfirst deck
        else if (e.getActionCommand ().equals ("Add"))
          deck.add(new Card(getVal())); //adds the card
        else if (e.getActionCommand ().equals ("Search"))
        {
          int cardv = getVal();
          try { //flips all the cards except for the ones that were searched for
            int[] pos = deck.search(new Card(cardv));
            int n = 0;
            for (int i = 0; i < deck.getDeck().length; i++)
            {
              if (i != pos[n])
                deck.getDeck()[i].flip();
              else if (n < pos.length-1)
                n++;
            }
            b_search.setText("Flip Back"); //changes the button
          }
          catch (ArrayIndexOutOfBoundsException n)  //just in case there was no matches
          { 
            JOptionPane.showMessageDialog (null, new Card(cardv).toString() + " was not found in this deck!");
          };
        }
        else if (e.getActionCommand ().equals ("Deal")) //removes a card from the deck
        {
          //if (deck.getDeck().length > 1)
          //{
          int cur = Integer.parseInt(sp_dealpos.getValue().toString());
          deck.deal(cur-1);
          sp_dealpos.setModel(new SpinnerNumberModel(1, 1, deck.getDeck().length, 1));
          while (cur > deck.getDeck().length)
            cur--;
          sp_dealpos.setValue(cur);
          //}
          //else
          //  JOptionPane.showMessageDialog (null, "Please leave the last card!");
        }
        else if (e.getActionCommand().equals("Reset")) //resets the deck
          deck = new Deck();
        else if (e.getActionCommand ().equals ("Flip Back")) //this allows you to reset after searching
        {
          for (int i = 0; i < deck.getDeck().length; i++)
            deck.getDeck()[i].flip(true);
          b_search.setText("Search");
          
        }
      }
      catch (ArrayIndexOutOfBoundsException n)  //just in case there was no matches
      { 
        JOptionPane.showMessageDialog (null, new Card(cardv).toString() + " was not found in this deck!");
      };
      repaint (); // do after each action taken to update deck
    }
  }
  
  class DrawArea extends JPanel 
  {
    public DrawArea (int width, int height)
    {
      this.setPreferredSize (new Dimension (width, height)); // size 
    }
    
    public void paintComponent (Graphics g)
    {
      deck.show (g); //shows the deck
    }
  }
  
//======================================================== method main
  public static void main (String[] args)
  {
    DeckGUI window = new DeckGUI ();
    window.setVisible (true);
  }
}

// -------------------------------------- Card Class ------------------------------------------------------
class Card
{
  private int rank, suit;
  private Image image;
  private boolean faceup;
  private static Image cardback; // shared image for back of card
  
  public Card (int cardNum)  // Creates card from 0-51
  {
    rank = cardNum % 13;
    suit = cardNum / 13;  //determining the card information
    faceup = true;
    image = null;
    try
    {
      image = ImageIO.read (new File ("cards\\n (" + (cardNum + 1) + ").gif")); // load file into Image object
      cardback = ImageIO.read (new File ("cards\\b.gif")); // load file into Image object
    }
    catch (IOException e) { System.out.println ("File not found"); } 
  }
  
  public Card (Card card) //copy constructor
  {
    rank = card.rank;
    suit = card.suit;
    faceup = card.faceup;
    image = card.image;
  }
  
  public void show (Graphics g, int x, int y)  // draws card face up or face down
  {
    if (faceup) 
      g.drawImage (image, x, y, null);
    else
      g.drawImage (cardback, x, y, null);
  }
  
  public int compareTo(Card card) //compares the suits of the cards
  {
    if (card.suit*13 + card.rank > suit*13 + rank)
      return -1;
    else if (card.suit*13 + card.rank < suit*13 + rank)
      return 1;
    else
      return 0;
  }
  
  public int compareTo2(Card card) //compares the ranks of the cards
  {
    if (card.rank*13 + card.suit > rank*13 + suit)
      return -1;
    else if (card.rank*13 + card.suit < rank*13 + suit)
      return 1;
    else
      return 0;
  }
  
  public boolean equals (Card card) //checks if the cards are equal
  {
    if (rank == card.rank && suit == card.suit) //checks if the value is the same.
      return true;
    else
      return false;
  }
  
  public void flip () { faceup = false; }
  public void flip (boolean b) { faceup = b; } //methods that flip the card
  
  public int getRank(){ return rank; }
  public int getSuit() { return suit; }
  public boolean getFaceup() { return faceup; } //getter/ accessor methods for these three data fields
  
  public String toString() //to String method to display card as text
  {
    String suiter = "";
    if (suit == 0)
      suiter = "Diamonds";
    else if (suit == 1)
      suiter = "Clubs";
    else if (suit == 2)
      suiter = "Hearts";
    else if (suit == 3)
      suiter = "Spades";
    
    return "" + (2+rank) + " of " + suiter;
  }
}

// -------------------------------------- Deck Class ------------------------------------------------------
class Deck
{
  private Card deck[];
  
  public Deck () //constructor
  {
    deck = new Card [52];
    for (int x = 0 ; x < 52 ; x++)  // for each card in standard deck
    {
      deck [x] = new Card (x); // create card
    }
  }
  
  public void show (Graphics g)  // draws card face up or face down
  {
    try 
    {
      for (int c = 0 ; c < deck.length ; c++)
      {
        deck [c].show (g, c % 13 * 20 + 150, c / 13 * 50 + 20); //arranges the cards appropriately
      }
    } catch (NullPointerException n) {};
  }
  
  
  public void shuffle () //shuffles the deck
  {
    for (int i = deck.length; i > 1; i--)
    {
      swap(i-1, (int) (Math.random()*i)); //swaps the cards
    }
  }
  
  private void swap (int x, int y) //needed for the shuffle method (switches two arrays)
  {
    Card t = deck[x]; 
    deck[x] = deck[y];
    deck[y] = t;
  }
  
  public Card deal() //takes the top card off the deck
  {
    Card returner = deck[0]; //stores the card to be dealt
    if (deck.length > 1)
    {
      Card deck2[] = new Card[deck.length-1]; //creates a smaller deck
      for (int i = 0; i < deck2.length; i++) //transfers all the cards
        deck2[i] = deck[i+1];
      deck = deck2; //reassigns array
    } else if (deck.length == 1)
    {
      deck = null;
    }
    return returner;
  }
  
  public Card deal(int pos)
  {
    Card returner = new Card(deck[pos]); //stores the card to be dealt
    try
    { //just in case the array is empty
      if (deck.length>1)
      {
        Card deck2[] = new Card[deck.length-1]; //creates a smaller deck
        for (int i = 0; i < pos; i++) //transfers all the cards
          deck2[i] = deck[i];
        for (int i = pos; i < deck2.length; i++)
          deck2[i] = deck[i+1];
        deck = deck2; //reassigns array
      }
      else if (deck.length == 1)
      {
        deck = null;
      }
    }
    catch (NullPointerException n) {  }; //JOptionPane.showMessageDialog (null, "There are no cards in this deck! Please add some cards or reset.");
    return returner;
  }
  
  public void add (Card card)
  {
    int len;
    try{
      len = deck.length+1;
    } catch (NullPointerException n)
    {
      len = 1;
    }
    if (len == 0)
      len++;
    Card[] deck2 = new Card[len]; //new array that's larger to accomodate the new card
    deck2[len-1] = card; //final position is the new card
    for (int i = 0; i < len-1; i++) //moves all the new items
      deck2[i] = deck[i];
    deck = deck2; //reassigns old deck to new one
  }
  
  public int[] search (Card card)
  {
    int numOccur = 0; //counts number of occurences
    for (int i = 0; i < deck.length; i++) //goes through deck searching for number of occurence
      if (deck[i].equals(card))
      numOccur++;
    int[] positions = new int[numOccur];// array of the positions
    int pos = 0; //keeps track of which occurence number you are currently at
    for (int i = 0; i < deck.length; i++) //goes through deck searching for number of occurence
    {
      if (deck[i].equals(card))
      {
        positions[pos] = i; //records position
        pos++; 
      }
    }
    return positions;
  }
  
  public void quickSort() //calls the quickSort method with appropriate arguments
  {
    quickSort(0, deck.length-1);
  }
  
  private void quickSort (int lowIndex, int highIndex)
  {
    if (deck == null || deck.length == 0)
      return; //checks to make sure array isn't empty
    int low = lowIndex, high = highIndex;
    Card pivot = deck[low + (high-low)/2]; //index is middle number
    while (low <= high)
    {
      while (deck[low].compareTo2(pivot) == -1) //finds a card greater than the pivot
        low++;
      while (deck[high].compareTo2(pivot) == 1) //finds a card that is less than the pivot
        high--;
      if (low <= high) //switches them if they should be switched
      {
        swap(low, high);
        low++;
        high--;
      }
    }
    //calls quicsort recursively
    if (lowIndex < high)
      quickSort(lowIndex, high);
    if (low < highIndex)
      quickSort(low, highIndex);
  }
  
  public void selectionSort ()
  {
    int index, smallerNumber;
    for (int i = 0; i < deck.length - 1; i++)
    {
      index = i;
      for (int j = i + 1; j < deck.length; j++) //cycles through the deck searching for numbers out of order
        if (deck[j].compareTo( deck[index]) == -1)
        index = j;
      swap(index, i); //swaps the two numbesr
    }
  }
  
  public Card[] getDeck() { return deck; } //getter method for deck
}

