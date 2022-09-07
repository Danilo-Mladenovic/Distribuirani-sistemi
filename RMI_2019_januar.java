

public class Reservation implements Serializable
{
    public int id;
    public int day;
    public int month;
    public int hour;
    public int numHours;

    public Reservation(int id, int day, int month, int hour, int numHours)
    {
        this.super();

        this.id = id;
        this.day = day;
        this.month = month;
        this.hour = hour;
        this.numHours = numHours;
    }
}


public interface IGymReservationManager extends Remote
{
    Reservation makeReservation(int day, int month, int hour, int numHours) throws RemoteException;
    Reservation extendReservation(Reservation res, int numExtraHours) throws RemoteException;
    void cancelReservation(Reservation res) throws RemoteException;
}


public class GymReservationManager extends UnicastRemoteObject implements IGymReservationManager
{
    private HashMap<Integer, Reservation> reservations;
    private int idCounter;

    public GymReservationManager()
    {
        this.super();

        this.reservations = new HashMap<>();
        this.idCounter = 0;
    }

    @Override
    public Reservation makeReservation(int day, int month, int hour, int numHours) throws RemoteException
    {
        if (day > 31 || day < 1 || month > 12 || month < 1 || hour + numHours > 24 || hour < 0 || numHours < 0)
            return null;
        
        for (Reservation r : this.reservations.values())
            if (r.month == month && r.day == day && r.hour < hour && r.hour + r.numHours > hour)
                return null;

        Reservation r = new Reservation(idCounter++, day, month, hour, numHours);
        this.reservation.put(r.id, r);
        
        return r;
    }

    @Override
    public Reservation extendReservation(Reservation res, int numExtraHours) throws RemoteException
    {
        if (res == null)
            return null;
        
        Reservation res = this.reservations.get(res.id);

        if (res.hours + res.numHours + numExtraHours > 24)
            return null;
        else
            res.numHours += numExtraHours;
        
        return res;

    }

    @Override
    public void cancelReservation(Reservation res) throws RemoteException
    {
        if (res == null)
            return null;
        
        this.reservations.remove(res.id);
    }
}


public class GymReservationServer
{
    public GymReservationManager() throws RemoteException, MalformedURLException, AlreadyBoundException
    {
        LocateRegistry.createRegistry(1050);
        Naming.rebind("rmi://127.0.0.1:1050/GymReservationService", new GymReservationManager());
    }

    public close() throws RemoteException, MalformedURLException, NotBoundException
    {
        Naming.unbind("rmi://127.0.0.1:1050/GymReservationService");
    }

    public static void main(String[] args)
    {
        GymReservationServer server = new GymReservationServer();

        Scanner s = new Scanner(System.in);
        s.nextLine();
        server.close();
        System.exit(0);
    }
}


public class GymReservationClient
{
    private IGymReservationManager proxy;
    private HashMap<Integer, Reservation> myReservations;

    public GymReservationClient() throws RemoteException, MalformedURLException, NotBoundException
    {
        proxy = (IGymReservationManager) Naming.lookup("rmi://127.0.0.1:1050/GymReservationService");
        this.myReservations = new HashMap<>();
    }

    public static void main(String[] args)
    {
        new GymReservationClient();

        Reservation r = this.proxy.makeReservation(new Reservation(123, 7, 10, 13, 1));
        this.myReservations.put(r.id, r);

        r = this.proxy.extendReservation(r, 1);

        this.proxy.cancelReservation(r);
    }

}