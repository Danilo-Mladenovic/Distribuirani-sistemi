

public interface ITaxi extends Remote
{
    void setTaxiStatus(boolean isFree) throws RemoteException;
}

public class Taxi extends UnicastRemoteObject implements ITaxi
{
    public int id;
    public String address;
    public boolean isFree;
    public ITaxiCallback callback;

    public Taxi(int id, String adr, boolean isFree, ITaxiCallback cb) throws RemoteException
    {
        this.super();

        this.id = id;
        this.address = adr;
        this.isFree = isFree;
        this.callback = cb;
    }

    public Taxi(int id, ITaxiCallback cb) throws RemoteException
    {
        this.super();

        this.id = id;
        this.address = "";
        this.isFree = true;
        this.callback = cb;
    }

    @Override
    public void setTaxiStatus(boolean isFree) throws RemoteException
    {
        this.isFree = isFree;
    }
}


public interface ITaxiCallback extends Remote
{
    void notifyTaxi(String address) throws RemoteException;
}


public interface ITaxiManager extends Remote
{
    boolean requestTaxi(String address) throws remoteException;
    void setTaxiStatus(int id, boolean isFree) throws RemoteException;
    void register(ITaxi taxi) throws RemoteException;
    void unregister(ITaxi taxi) throws RemoteException;
}


public class TaxiManager extends UnicastRemoteObject implements ITaxiManager
{
    private HashMap<Integer, ITaxi> taxis;
    private Queue<String> addresses;

    public TaxiManager() throws RemoteException
    {
        this.super();

        this.taxis = new HashMap<>();
        this.addresses = new ArrayList<>();
    }

    @Override
    public boolean requestTaxi(String address) throws remoteException
    {
        if (this.taxis.size() == 0)
        {
            this.addresses.push(address);
            return false;
        }
        
        for (ITaxi taxi : this.taxis.values())
            if (taxi.isFree)
            {
                taxi.address = address;
                taxi.setTaxiStatus(false);
                taxi.callback.notifyTaxi(address);
                return true;
            }
        
        this.addresses.push(addresses);
        return false;
    }

    @Override
    public void setTaxiStatus(int id, boolean isFree) throws RemoteException
    {
        if (!this.taxis.contains(id))
            return;

        ITaxi taxi = this.taxis.getKey(id);
        taxi.setTaxiStatus(isFree);

        if (isFree)
            if (!this.addresses.isEmpty())
            {
                String adr = this.addresses.pop();
                taxi.address = adr;
                taxi.callback.notifyTaxi(adr);
            }
    }

    @Override
    public void register(ITaxi taxi) throws RemoteException
    {
        if (taxi == null || this.taxis.containsKey(taxi.id))
            return;
        
        this.taxis.put(taxi.id, taxi);
        setTaxiStatus(taxi.id, taxi.isFree);
    }
    
    @Override
    public void unregister(ITaxi taxi) throws RemoteException
    {
        if (!this.taxis.containsKey(taxi.id))
            return;
        
        this.taxis.remove(taxi.id);
    }
}


public class TaxiServer
{
    public TaxiServer() throws RemoteException, MalformedURLException, AlreadyBoundException
    {
        LocateRegistry.createRegistry(1050);
        Naming.rebind("rmi://127.0.0.1:1050/TaxiService", new TaxiManager());
    }
    
    public void shutdown() throws RemoteException, MalformedURLException, NotBoundeExcepiton
    {
        Naming.unbind("rmi://127.0.0.1:1050/TaxiService");
    }

    public static void main(String[] args)
    {
        TaxiServer server = new TaxiServer();
        Scanner s = new Scanner(System.in);

        s.nextLine();
        s.close();

        server.shutdown();
        System.exit(0);
    }
}


public class TaxiDriverClient implements ITaxiCallback
{
    private ITaxiManager proxy;

    public TaxiDriverClient() throws RemoteException, MalformedURLException, NotBoundException
    {
        this.proxy = (ITaxiManager) Naming.lookup("rmi://127.0.0.1:1050/TaxiService");        
    }

    @Override
    public void notifyTaxi(String address) throws RemoteException
    {
        System.out("Taxi has been called to: " + address);
    }

    public static void main(String[] args)
    {
        TaxiDriverClient driverClient = new TaxiDriverClient();

        ITaxi taxi1 = new Taxi(123, this);

        proxy.register(taxi1);

        ITaxi taxi2 = new Taxi(234, this);

        proxy.register(taxi2);
        proxy.setTaxiStatus(taxi2.id, false);

        proxy.unregister(taxi2);

        System.exit(0);
    }
}


public class TaxiUserClient
{
    private ITaxiManager proxy;

    public TaxiUserClient() throws RemoteException, MalformedURLException, NotBoundException
    {
        this.proxy = (ITaxiManager) Naming.lookup("rmi://127.0.0.1:1050/TaxiService");
    }

    public static void main(String[] args)
    {
        TaxiUserClient userClient = new TaxiUserClient();

        proxy.requestTaxi("Bulevar Nikole Tesle 15");

        System.exit(0);
    }   
}