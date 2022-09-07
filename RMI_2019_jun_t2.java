

public class Card implements Serializable
{
    public String color;
    public int value;

    public Card(String clr, int val)
    {
        this.super();

        this.color = clr;
        this.value = val;
    }
}


public interface IPlayer extends Remote
{
    void drawCard(Card card) throws RemoteException;
    int handSum() throws remoteException;
}


public class Player extends UnicastRemoteObject implements IPlayer
{
    public int id;
    public String name;
    public int points;
    public ArrayList<Card> handCards;
    public ICardCallback callback;
    private int handSum;

    public Player(int id, String name, int pts, ICardCallback cb) throws RemoteException
    {
        this.super();

        this.id = id;
        this.name = name;
        this.points = pts;
        this.handCards = new ArrayList<>();
        this.callback = cb;
        this.handSum = 0;
    }

    @Override
    public boolean drawCard(Card card) throws RemoteException
    {
        if (this.handCards.contains(card))
            return false;
        
        this.handCards.add(card);
        this.handSum += card.value;

        return true;
    }

    @Override
    public int handSum() throws RemoteException
    {
        return this.handSum;
    }
}


public interface ICardGameManager extends Remote
{
    Card requestCard(Player player) throws RemoteException;
    void pass(Player player) throws RemoteException;
    boolean registerPlayer(Player player) throws remoteException;
}


public class CardGameManager extends UnicastRemoteObject implements ICardGameManager
{
    private ArrayList<Card> playingCards;
    private int drawnCards;
    private HashMap<Integer, Player> players;
    private HashMap<Integer, Player> skipped;

    public CardGameManager() throws RemoteException
    {
        this.super();

        for (String clr : new ArrayList<String>{"Diamonds", "Hearts", "Clubs", "Spades"})
            for (int i = 1; i < 15; i++)
                if (i != 11)
                    this.playingCards.add(new Card(clr, i));
        this.drawnCards = 0;
        this.players = new HashMap<>();
        this.skipped = new HashMap<>();
    }

    @Override
    public Card requestCard(Player player) throws RemoteException
    {
        if (this.drawnCards >= 52)
            return null;

        Card card;

        do
        {
            card = this.playingCards[((int) Math.random() * 52) % 1];
        }  player.drawCard(card);

        this.drawnCards += 1;


    }

    @Override
    public void pass(Player player) throws RemoteException
    {
        if (player == null)
            return;

        if (!this.skipped.containsKey(player.id))
            this.skipped.put(player.id, player);
        
        if (this.skipped.size() == this.players.size())
            calculatePoints();
    }

    @Override
    public boolean registerPlayer(Player player) throws remoteException
    {   
        if (player == null || this.players.contains(player.id))
            return false;

        this.playes.put(player.id, player);
        return true;
    }
}


public interface ICardCallback extends Remote
{
    void isWinner() throws RemoteException;
}


public class CardGameServer
{
    public CardGameServer() throws RemoteException, MalformedURLException, AlreadyBoundException
    {
        LocateRegistry.createRegistry(1050);
        Naming.rebind("rmi://127.0.0.1:1050/CardGameService", new CardGameManager);
    }

    public shutdown() throws RemoteException, MalformedURLException, NotBoundException
    {
        Naming.unbind("rmi://127.0.0.1:1050/CardGameService");
    }

    public static void main(String[] args)
    {
        CardGameServer server = new CardGameServer();
        Scanner s = new Scanner(System.in);

        s.nextLine();
        s.close();

        server.shutdown();
        System.exit(0);
    }
}


public class CardGameClient implements ICardCallback
{
    private ICardGameManager proxy;
    private IPlayer player;

    public CardGaemClient() throws RemoteException, MalformedURLException, NotBoundException
    {
        this.proxy = (ICardGaemManager) Naming.lookup("rmi://127.0.0.1:1050/CardGameService");
        this.player = new Player(123, "Danilo", 0, this);
    }

    @Override
    public void isWinner() throws RemoteException
    {
        System.out("Pobedili ste!")
    }

    public static void main(String[] args)
    {
        CardGameClient client = new CardGameClient();

        proxy.registerPlayer(player);
        proxy.requestCard(player);
        proxy.pass(player);
    }
}