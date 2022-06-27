public interface IVehicle extends Remote 
{
    void setId(int id) throws RemoteException;
    int getId() throws RemoteException;

    void setAddress(String adr) throws RemoteException;
    String getAddress() throws RemoteException;

    void setIsFree(bool free) throws RemoteException;
    bool getIsFree() throws RemoteException;

    void setRoundNum(int num) throws RemoteException;
    int getRoundNum() throws RemoteException;

    void setCallback(IVehicleCallback cb) throws RemoteException;
    IVehicleCallback getCallback() throws RemoteException;
}

public class Vehicle implements IVehicle
{
    public int id;
    public String address;
    public bool isFree;
    public int roundNum;
    public IVehicleCallback cb;

    public Vehicle(int id, String adr, bool free, int num, IVehicleCallback cb)
    {
        this.id = id;
        this.address = adr;
        this.isFree = free;
        this.roundNum = num;
        this.cb = cb;
    }
}

public interface IVehicleCallback extends Remote 
{
    void notifyVehicle(string adr) throws RemoteException;
}

public interface IVehicleManager extends Remote 
{
    int register(IVehicle vehicle) throws RemoteException;
    bool requestVehicle(String adr) throws RemoteException;
}

public class VehicleManager extends UnicastRemoteObject implements IVehicleCallback
{
    private List<IVehicle> vehicles;
    private Queue<String> addresses;
    private int queueSize;
    private int idGen;

    public VehicleManager(int capacity)
    {
        this.vehicles = new List<>();
        this.addresses = new Queue<>();
        this.queueSize = capacity;
        this.idGen = 1;
    }

    @Override
    public int register(IVehicle vehicle) throws RemoteException
    {
        if (vehicle == null)
            return -1;

        vehicle.id = idGen++;
        this.vehicles.add(vehicle);

        if (vehicle.isFree && this.addresses.size() > 0)
        {
            String address = this.addresses.pop();
            vehicle.isFree = false;
            vehicle.address = address;
            vehicle.roundNum += 1;
            vehicle.cb.notifyVehicle(address);
        }

        return vehicle.id;
    }

    @Override
    public bool requestVehicle(String adr) throws RemoteException
    {
        if (this.vehicles.isEmpty())
        {
            if (this.ad.size() < queueSize)
            {
                this.addresses.push(adr);
                return true;
            }
            else
                return false;
        }

        Vehicle temp = null;

        for (Vehivle v : this.vehicles)
            if (v.isFree)
                if (temp == null || v.roundNum <= temp.roundNum)
                    temp = v;

        if (temp == null)
            return false;
        
        temp.isFree = false;
        temp.address = adr;
        temp.roundNum += 1;
        temp.cb.notifyVehicle(adr);

        return true;
    }
}

public class VehicleServer 
{
    public VehicleServer() throws RemoteException, MalformedURLException, AlreadyBoundException
    {
        LocalRegistry.createRegistry(5050);
        Naming.bind("rmi://127.0.0.1:5050/VehicleService", new VehicleManager(10));
    }

    public Close() throws RemoteException, MalformedURLException, NotBoundException
    {
        Naming.unbind("rmi://127.0.0.1:5050/VehicleService");     
    }

    public static void main(String[] args)
    {
        VehicleServer server = new VehicleServer();

        Scanner s = new Scanner(System.in);
        s.nextLine();
        s.close();

        server.Close();
        System.exit(0);
    }
}

public class VehicleDriverClient implements IVehicleCallback 
{
    private IVehicleManager mng;

    public VehicleDriverClient() throws RemoteException, MalformedURLException, NotBoundException
    {
        super();
        mng = (IVehicleManager) Naming.lookup("rmi://127.0.0.1:5050/VehicleService");
    }

    @Override
    public void notifyCar(String adr) throws RemoteException 
    {
        System.out.println("Klijent vas ceka na lokaciji: '" + address + "'");
    }

    public void register() throws RemoteException
    {
        proxy.register(new Vehicle(5, "adr", true, 0, this));
    }

    public static void main(String[] args)
    {
        VehicleDriverClient client = new VehicleDriverClient();
        Scanner s = new Scanner(System.in);

        client.register();

        s.nextLine();
        s.close();

        System.exit(0);
    }
}

public class VehicleUserClient
{
    private IVehicleManager mng;

    public VehicleUserClient() throws RemoteException, MalformedURLException, NotBoundException
    {
        mng = (IVehicleManager) Naming.lookup("rmi://127.0.0.1:5050/VehicleService");    
    }

    public bool requestVehicle(String adr) throws RemoteException 
    {
        return proxy.requestVehicle(adr);
    }

    public static void main(String[] args)
    {
        VehicleUserClient client = new VehicleUserClient();
        
        Scanner s = new Scanner(System.in);

        client.requestVehicle("Bulevar Nikole Tesle 15");
        
        s.nextLine();
        s.close();

        System.exit(0);
    }
}