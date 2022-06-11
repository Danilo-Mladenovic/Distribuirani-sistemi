


public class Card extends Serializable
{
	public String color;
	public int value;

	public Card(String clr, int val)
	{
		super();
		this.color = clr;
		this.value = val;
	}
}



public interface IPlayer extends Remote
{
	int getID() throws RemoteException;
	void setID(int id) throws RemoteException;
	String getName() throws RemoteException;
	void setName(String name) throws RemoteException;
	int getPoints() throws RemoteException;
	void setPoints(int pts) throws RemoteException;

	void pickCard(Card card) throws RemoteException;
	int getSum() throws RemoteException;
	void dicardCards() throws RemoteException;
}



public class Player extends UnicastRemoteObject implements IPlayer
{
	private int ID;
	private String name;
	private int points;
	private ArrayList<Card> hand;
	private int sumOfHand;

	public Player(int id, String name, int pts) throws RemoteException
	{
		this.ID = id;
		this.name = name;
		this.points = pts;
		this.hand = new ArrayList<>();
		this.sumOfHand = 0;
	}

	public int getID() throws RemoteException
	{
		return this.ID;
	}

	public void setID(int id) throws RemoteException
	{
		this.ID = id;
	}

	public String getName() throws RemoteException
	{
		return this.name;
	}

	public void setName(String name) throws RemoteException
	{
		this.name = name;
	}

	public int getPoints() throws RemoteException
	{
		return this.points;
	}

	public void setPoints(int pts) throws RemoteException
	{
		this.points = pts;
	}

	public void pickCard(Card card) throws RemoteException
	{
		this.hand.push(card);
		for (Card c in this.hand)
			this.sumOfHand += c.value;
	}

	public int getSum() throws RemoteException
	{
		return this.sumOfHand;
	}

	public void dicardCards() throws RemoteException
	{
		this.hand = new ArrayList<>();
		this.sumOfHand = 0;
	}
}



public interface ICardGameManager extends Remote
{
	Card requestCard(IPlayer player) throws RemoteException;
	void pass(IPlayer player) throws RemoteException;
	void registerPlayer(IPlayer player) throws RemoteException;
}



public class CardGameManager extends UnicastRemoteObject implements ICardGameManager
{
	private List<Card, bool> cards;
	String[] color = {"Diamongs", "Hearts", "Clubs", "Spades"};

	public CardGameManager() throws RemoteException
	{
		super();
		this.cards = new ArrayList<>();
		for (int i = 1; i < 14; i++)
			for (int j = 0; j < 4; j++)
				this.cards.add(<new Card(this.color[j], i), false>);
	}

	public Card requestCard(IPlayer player) throws RemoteException
	{
		int cardNum = 0;
		Card card = null;
		bool pickedCard = true;

		while (pickedCard)
		{
			cardNum = (int) Math.random() * 52;
			if (this.cards[cardNum].bool == false)
			{
				card = this.cards[cardNum].card;
				pickedCard = false;
			}	`
		}

		player.pickCard(card);
	}
}


