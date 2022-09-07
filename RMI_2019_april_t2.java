

public class Ticket implements Serializable
{
    public int id;
    public Vector<Integer> numbers;

    public Ticket(int id, Vector<Integer> numbers)
    {
        this.id = id;
        this.numbers = numbers;
    }
}


public interface ILotoManager extends Remote
{
    Ticket playTicket(Vector<Integer> numbers) throws RemoteException;
    Vector<Integer> getWinners() throws RemoteException;
    void drawNumbers() throws RemoteException;
}


public class LotoManager extends UnicastRemoteObject implements ILotoManager
{
    private HashMap<Integer, Ticket> playedTickets;
    private Vector<Integer> drawnNumbers;
    private int idCounter;

    public LotoManager() throws RemoteException
    {
        this.super();

        this.playedTickets = new HashMap<>();
        this.drawnNumbers = new Vector<>();
        this.idCounter = 0;
    }

    @Override
    public Ticket playTicket(Vector<Integer> numbers) throws RemoteException
    {
        if (numbers == null || numbers.size() != 7 || drawnNumbers.size() > 0)
            return null;
        
        for (int i = 0; i < 7; i++)
            if (numbers[i] < 1 || numbers[i] > 39)
                return null;
        
        Ticket ticket = new Ticket(idCounter++, numbers);
        this.playedTickets.put(ticket.id, ticket);

        return ticket;
    }

    @Override
    public Vector<Integer> getWinners() throws RemoteException
    {
        if (this.drawnNumbers.size() != 7)
            return null;

        Vector<Integer> winners = new Vector<>();
        int max = 0;

        for (Ticket t : this.playedTickets.values())
        {
            int counter = 0;

            for (int i : t.numbers)
                if (this.drawnNumbers.contains(i))
                    counter++;

            if (counter > max)
            {
                max = counter;
                winners.clear();
                winners.add(t.id);
            }
            else if (counter == max)
                winners.add(t.id);   
        }

        return winners;
    }

    @Override
    public void drawNumbers() throws RemoteException
    {
        if (this.drawnNumbers.size() != 0)
            return;

        for (int i = 0; i < 7; i++)
        {
            int num;

            do
            {
                num = ((int) Math.random() * 39) % 1;
            } while (this.drawnNumbers.contains(num));

            this.drawnNumbers.add(num);
        }
    }
}


public class LotoServer
{
    public LotoServer() throws RemoteException, MalformedURLException, AlreadyBoundException
    {
        LocateRegistry.createRegistry(1050);
        Naming.rebind("rmi://127.0.0.1:1050/LotoService", new LotoManager);
    }

    public close() throws RemoteException, MalformedURLException, NotBoundException
    {
        Naming.unbind("rmi://127.0.0.1:1050/LotoService");
    }

    public static void main(String[] args)
    {
        LotoServer server = new LotoServer();

        Scanner s = new Scanner(System.in);

        s.nextLine();
        s.close();

        server.close();

        System.exit(0);
    }
}


public class LotoClient
{
    private ILotoManager proxy;

    public LotoClient() throws RemoteException, MalformedURLException, NotBoundException
    {
        proxy = (ILotoManager) Naming.lookup("rmi://127.0.0.1:1050/LotoService");
    }

    public static void main(String[] args)
    {
        LotoClient client = new LotoClient();

        proxy.playTicket(new Vector<Integer>{1, 2, 3, 4, 5, 6, 7});
        proxy.drawNumbers();
        Vector<Integer> winners = proxy.getWinners();
    }
}