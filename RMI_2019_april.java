


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
	private HashMap<Integer, Ticket> tickets;
	private Vector<Integer> drawn;

	public LotoManager() throws RemoteException
	{
		super();
		this.tickets = new HashMap<>();
	}

	@Override
	public Ticket playTicket(Vector<Integer> numbers) throws RemoteException
	{
		if (this.drawn.size() > 0)
			return null;

		int id;
        do {
            id = (int) Math.random() * 1000;
        } while (this.tickets.containsKey(id));

        Ticket ticket = new Ticket(id, numbers);
        this.tickets.put(ticket);
        return ticket;

	}

	@Override
	public Vector<Integer> getWinners() throws RemoteException
	{

	}

	@Override
	public void drawNumbers() throws RemoteException
	{

	}
}