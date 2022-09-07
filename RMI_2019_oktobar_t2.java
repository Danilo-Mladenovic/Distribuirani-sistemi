

public interface IUser extends Remote
{

}

public class User implements IUser
{
    public int id;
    public String userName;
    public IChatMessageCallback callback;

    public User(int id, String userName, IChatMessageCallback cb) throws RemoteException
    {
        this.id = id;
        this.userName = userName;
        this.callback = cb;
    }
}


public class ChatMessage implements Serializable
{
    public User fromUser;
    public User toUser;
    public String message;
    public int hour;
    public int minute;

    public ChatMessage(User fromUser, User toUser, String msg, int hour, int minute)
    {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.message = msg;
        this.hour = hour;
        this.minute = minute;
    }
}


public interface IChatMessageCallback extends Remote
{
    void onChatMessage() throws RemoteException;
}


public interface IChatAppManager extends Remote
{
    void sendChatMessage(User fromUser, User toUser, String msg) throws RemoteException;
    Vector<ChatMessage> getChatMessages(User user, int hour, int minute) throws RemoteException;
    boolean register(User user) throws RemoteException;
    boolean unregister(User user) throws RemoteException;
}


public class ChatAppManager extends UnicastRemoteObject implements IChatAppManager
{
    private Vector<ChatMessage> sentMessages;
    private Vector<User> registeredUsers;

    public ChatAppManager() throws RemoteException
    {
        this.super();

        this.sentMessages = new Vector<>();
        this.registeredUsers = new Vector<>();
    }

    @Override
    public void sendChatMessage(User fromUser, User toUser, String msg) throws RemoteException
    {
        if (fromUser == null || toUser == null || cmsg == null)
            return;

        if (!this.registeredUsers.contains(fromUser) || !this.registeredUsers.contains(toUser))
            return;

        DateTime dt = new DateTime();
        ChatMessage cmsg = new ChatMessage(fromUser, toUser, msg, dt.getHour(), dt.getMinute());
        this.sentMessages.add(cmsg);

        toUser.callback.onChatMessage();
    }

    @Override
    public Vector<ChatMessage> getChatMessages(User user, int hour, int minute) throws RemoteException
    {
        if (user == null || !this.registeredUsers.contains(useR))
            return null;

        if (hour > 24 || hour < 0 || minute > 59 || minute < 0)
            return null;
        
        Vector<ChatMessage> returningMessages = new Vector<>();
        for (ChatMessage cmsg : this.sentMessages)
            if (cmsg.toUser == user)
                if ((cmsg.hour == hour && cmsg.minute > minute) || cmsg.hour > hour)
                    returningMessges.add(cmsg);

        return returningMessages;
    }

    @Override
    public boolean register(User user) throws RemoteException
    {
        if (user == null || this.registeredUsers.contains(user.id))
            return false;

        this.registeredUsers.add(user);
    }

    @Override
    public boolean unregister(User user) throws RemoteException
    {
        if (user == null || !this.registeredUsers.contains(user.id))
            return false;

        this.registeredUsers.remove(user);
    }
}


public class ChatAppServer
{
    public ChatAppServer() throws RemoteException, MalformedURLException, AlreadyBoundException
    {
        LocateRegistry.createRegistry(1050);
        Naming.rebind("rmi://127.0.0.1:1050/ChatAppService", new ChatAppManager);
    }

    public void shutdown() throws RemoteException, MalformedURLException, NotBoundException
    {
        Naming.unbind("rmi://127.0.0.1:1050/ChatAppService");
    }

    public static void main(String[] args)
    {
        ChatAppServer server = new ChatAppServer();
        Scanner s = new Scanner(System.in);

        s.nextLine();
        s.close();

        server.shutdown();
        System.exit(0);
    }
}


public class ChatAppClient implements IChatMessageCallback
{
    private IChatAppManager proxy;

    public ChatAppClient() throws RemoteException, MalformedURLException, NotBoundException
    {
        this.proxy = (IChatAppManager) Naming.lookup("rmi://127.0.0.1:1050/ChatAppService");
    }

    @Override
    public void onChatMessage() throws RemoteException
    {
        System.out("You've got mail!");
    }

    public static void main(String[] args)
    {
        ChatAppClient client = new ChatAppClient();
        Scanner s = new Scanner(System.in);
        s.nextLine();

        IUser user1 = new User(123, "Daki", this);
        IUser user2 = new User(234, "Pera", this);

        proxy.register(user1);
        proxy.register(user2);

        proxy.sendChatMessage(user1, user2, "Cao brt");
        proxy.getChatMessages(user2, 15, 0);

        proxy.unregister(user1);
        proxy.unregister(user2);

        s.close();
        System.exit(0);
    }
}