


public class VCalcRequest extends Serializable
{
	public int cID;
	public Vector<Double> a;
	public Vector<Double> b;
	public VCalcCallback cb;

	public VCalcRequest(int id, Vector<Double> a, Vector<Double> b, VCalcCallback cb)
	{
		super();
		this.cID = id;
		this.a = a;
		this.b = b;
		this.cb = cb;
	}

	public double Operate()
	{
		if (this.a == null || this.b == null || this.a.size() != this.b.size())
            return null;

        Double res = 0.0;
        for (int i = 0; i < this.a.size(); i++)
        	res += this.a.get(i) * this.b.get(i);

        return res;
	}
}



public interface VCalcCallback extends Remote 
{
	void onDone(int id, double result) throws RemoteException;
}



public interface ICalcManager extends Remote
{
	int SendVCalcRequest(VCalcRequest req) throws RemoteException;
	bool RunNextVCalc() throws RemoteException;
}



public class VCalcManager extends UnicastRemoteObject implements ICalcManager
{
	private Queue<VCalcRequest> queue;
	private int id;

	public VCalcManager() throws RemoteException
	{
		super();
		queue = new Queue<>();
		this.id = 1;
	}

	@Override
	public int SendVCalcRequest(VCalcRequest req) throws RemoteException
	{
		this.queue.push(new VCalcRequest(req.cID, req.a, req.b, req.cb));
		return this.id++;
	}

	@override
	public bool RunNextVCalc() throws RemoteException
	{
		if (this.queue.size() == 0)
			return false;

		VCalcRequest req = this.queue.pop();
		if (req == null)
			return false;

		double res = req.Operate();
		if (res == null)
			return false;

		req.cb.onDone(req.cID, res);
		return true;
	}
}



public class VCalcServer
{
	private IVCalcManager manager;

	public VCalcServer() 
		throws RemoteException, MalformedURLException, AlreadyBoundException
	{
		this.manager = new VCalcManager();
		LocateRegistry.createRegistry(5050);
		Naming.bind("rmi://127.0.0.1:5050/VCalcService");
	}

	public void shutdown()
		throw RemoteException, MalformedURLException, NotBoundException 
	{
		Naming.unbind("rmi://127.0.0.1:5050/VCalcService", this.manager);
	}

	public static void main(String[] args)
	{
		VCalcServer server = new VCalcServer();

		Scanner s = new Scanner(System.in);
		s.nextLine();
		s.close();

		server.shutdown();
	}
}



public class VCalcClient extends UnicastRemoteObject implements VCalcCallback
{
	private IVCalcManager proxyManager;  

	public VCalcClient()
		throws RemoteException, MalformedURLException, AlreadyBoundException
	{
		this.proxyManager = (IVCalcManager) Naming.lookup("rmi://127.0.0.1:5050/VCalcService");
	}

	public int SendVCalcRequest(VCalcRequest req) throws RemoteException
	{
		this.proxyManager.SendVCalcRequest(req);
	}

	public bool RunNextVCalc() throws RemoteException
	{
		this.proxyManager.RunNextVCalc();
	}

	@override
	public void onDone(int id, double result) throws RemoteException
	{
		System.out.println("Rezultat kalkulacije " + id + " je: " + result);
	}

	public static void main(String[] args)
	{
		VCalcClient client = new VCalcClient();
	
	}
}