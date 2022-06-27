public class CalcRequest implements Serializable 
{
    public int cId;
    public List<double> a;
    public List<double> b;
    public double k;
    public ICalcCallback cb;

    public CalcRequest(int cId, List<double> a, List<double> b, double k, ICalcCallback cb)
    {
        super();
        this.cId = cId;
        this.a = a;
        this.b = b;
        this.k = k;
        this.cb = cb;
    }

    public double Operate()
    {
        double rez1 = 0, rez2 = 0 ret = 0;
        List<double> rezV1 = new List<>(), rezV2 = new List<>();

        for (int i = 0; i < a.size(); i++)
        {
            rezV1[i] = a[i] + b[i];
            rez1 += a[i] * a[i] + b[i] * b[i];

            rezV2[i] = a[i] - b[i];
            rez2 += a[i] * a[i] - b[i] * b[i];
        }

        rez1 = sqrt(rez1);
        rez2 = sqrt(rez2);

        for (int i = 0; i < a.size(); i++)
        {
            rezV1[i] *= rez1;
            rezV2[i] *= rez2;

            ret += rezV1[i] * rezV2[i];
        }

        return ret * k;
    }
}

public interface ICalcCallback extends Remote
{
    void onDone(int cId, double result) throws RemoteException;
}

public interface ICalcManager extends Remote
{
    int SendCalcRequest(CalcRequest req) throws RemoteException;
    bool RunNextCalc() throws RemoteException;
}

public class CalcManager extends UnicastRemoteObject implements ICalcManager
{
    private Queue<CalcRequest> queue;
    private int id;

    public CalcManager()
    {
        this.queue = new Queue<>();
        this.id = 1;
    }

    @Override
    public SendCalcRequest(CalcRequest req) throws RemoteException
    {
        if queue.contains(req)
            return -1;
        
        this.queue.push(new CalcRequest(req.cId, req.a, req.b, req.k, req.cb));
        return this.id++;
    }

    @Override
    public bool RunNextCalc() throws RemoteException
    {
        if (this.queue.isEmpty())
            return false;

        CalcRequest temp = this.queue.pop();
        double res = temp.Operate();
        if (res == null)
            return false;
        
        req.cb.onDone(req.cId, res);
        return true;
    }
}

public class CalcServer 
{
    private ICalcManager mng;

    public CalcServer() throws RemoteException, MalformedURLException, AlreadyBoundException 
    {
        this.mng = new CalcManager();
        LocateRegistry.createRegistry(5050);
        Naming.bind("rmi://127.0.0.1:5050/CalcService", mng);
    }

    public void Close() throws RemoteException, MalformedURLException, NotBoundException 
    {
        Naming.unbind("rmi://127.0.0.1:5050/CalcService");
    }

    public static void Main(String[] args)
    {
        CalcServer server = new CalcServer();

		Scanner s = new Scanner(System.in);
		s.nextLine();
		s.close();

		server.Close();
    }
}

public class CalcClient implements ICalcCallback
{
    private ICalcManager proxy;

    public CalcClient() throws RemoteException, MalformedURLException, NotBoundException
    {
        this.proxy = (ICalcManager) Naming.lookup("rmi://127.0.0.1:5050/CalcService");
    }

    public int SendCalcRequest(CalcRequest req) throws RemoteException
    {
        this.proxy.SendCalcRequest(req);      
    }

    public bool RunNextCalc() throws RemoteException 
    {
        this.proxy.RunNextCalc();
    }

    @Override
    public void onDone(int cId, double result) throws RemoteException
    {
        System.out.println("Rezultat kalkulacije " + id + " je: " + result);
    }

    public static void Main(String[] args)
    {
        CalcClient c = new CalcClient();
        
        Scanner s = new Scanner(System.in);

        c.SendCalcRequest(new CalcRequest());
        c.RunNextCalc();

        s.nextLine();
        s.close();

        System.exit(0);
    }
}