


public interface IUser extends Remote
{
	int getID() throws RemoteException;
	String getName() throws RemoteException;

	TagMessageCallback getCallback() throws RemoteException;
	void setCallback(TagMessageCallback cb) throws RemoteException;
}



public class User extends UnicastRemoteObject implements IUser
{
	private int ID;
	private String userName;
	private TagMessageCallback callback;

	public User(int id, String name, TagMessageCallback cb) throws RemoteException
	{
		super();
		this.ID = id;
		this.userName = name;
		this.callback = cb;
	}

	@Override
	public int getID() throws RemoteException
	{
		return this.ID;
	}

	@Override
	public String getName throws RemoteException
	{
		return this.userName;
	}

	@Override
	public TagMessageCallback getCallback() throws RemoteException
	{
		return this.callback;
	}

	@Override
	public void setCallback(TagMessageCallback cb) throws RemoteException
	{
		this.callback = cb;
	}
}



public class TagMessage implements Serializable 
{
	public IUser user;
	public String message;
	public List<String> tags;

	public TagMessage(IUser usr, String msg, List<String> tags)
	{
		this.user = usr;
		this.message = msg;
		this.tags = new ArrayList();
		for (String tag : tags)
			this.tags.add(tag);
	}
}



public interface TagMessageCallback extends Remote
{
	void onTagMessage(TagMessage msg, String tag) throws RemoteException;
}



public interface ITagMenager extends Remote 
{
	void SendMessage(TagMessage msg) throws RemoteException;
	void Follow(IUser usr, String tag, TagMessageCallback cb) throws RemoteException;
}



public class TagMenager extends UnicastRemoteObject implements ITagMenager
{
	private HashMap<String, HashMap<Integer, IUser>> followedTags;

	public TagMenager() throws RemoteException
	{
		super();
		this.followedTags = new HashMap<>();
	}

	@Override
	public void sendMessage(TagMessage msg) throws RemoteException
	{
		HashMap<Integer, IUser> notify = new HashMap<>();

		for (String tag : msg.tags)
			for (IUser usr : followedTags.get(tag).values())
				if (!notify.containsKey(usr.getID()) && usr.getID() != msg.user.getID())
				{
					notify.put(usr.getID(), usr);
					usr.getCallback().onTagMessage(msg, tag);
				}
	}

	@Override
	public void follow(IUser usr, String tag, TagMessageCallback cb) throws RemoteException
	{
		if (usr.getCallback() == null)
			usr.setCallback(cb);

		HashMap<Integer, IUser> followers = this.followedTags.get(tag);

		if (followers == null)
			this.followedTags.put(tag, followers = new HashMap<>());

		if (!followers.containsKey(usr.getID()))
			followers.put(usr.getID(), usr);
	}
}



public class TagServer
{
	private ITagMenager menager;

	public TagServer(String host, String port, String service)
		throws RemoteException, MalformedURLException, AlreadyBoundException
	{
		menager = new TagMenager();
		LocateRegistry.createRegistry(Integer.parseInt(port));
		Naming.bind("rmi://" + host + ":" + port + "/" + service, menager);
	}

	public void shutdown(String host, String port, String service)
		throws RemoteException, MalformedURLException, NotBoundException
	{
		Naming.unbind("rmi://" + host + ":" + port + "/" + service);
	}

	public static void main(Strings[] args)
	{
		TagServer server = new TagServer("127.0.0.1", "5050", "TagMessageService");

		Scanner s = new Scanner(System.in);
		s.nextLine();
		s.close();
		server.shutdown("127.0.0.1", "5050", "TagMessageService");

		System.exit(0);
	}
}



public class TagClient extends UnicastRemoteObject implements TagMessageCallback
{
	private TagMenager proxyMenager;

	public TagClient(String host, String port, String service)
		throws RemoteException, MalformedURLException, AlreadyBoundException
	{
		proxyMenager = (ITagMenager) Naming.lookup("rmi://" + host + "+" + port + "/" + service);
	}

	public void sendMessage(TagMessage msg) throws RemoteException
	{
		this.proxyMenager.sendMessage(msg);
	}

	public void follow(IUser usr, String msg) throws RemoteException
	{
		this.proxyMenager.follow(usr, msg, this);
	}

	public static void main(String[] args)
	{
		TagClient client = new TagClient("127.0.0.1", "5050", "TagMessageService");

		Scanner s = new Scanner(System.in);

	}
}