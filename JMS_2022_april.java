public class Client
{
	private Topic topic;
	private TopicConnection tConnection;
	private TopicSession tSession;
	private TopicPublisher publisher;
	private LinkedList<TopicSubscriber> subscriber;


	public Client()
	{
		InitialContext ic = new InitialContext();

		TopicConnectionFactory tcf = (TopicDestinationFactory) ic.lookup("april2022");
		topic = (Topic) ic.lookup("tapril2022");

		ic.close();

		tConnection = (TopicConnection) tcf.createTopicConnection();
		tSession = (TopicSession) tConnection.createTopicSession(false, Sessio.AUTO_ACKNOWLEDGE);

		publisher = tSession.createPublisher(topic);
		subscriber = new LinkedList<>();
	}

	public void Start(List<Proizvod> proizvodi, double minCena, double maxCena)
	{
		for (Proizvod p : proizvodi)
		{
			subscriber.add(tSession.createSubscriber(topic, "Artikl = '" + p.naziv + "' AND Cena < '" + maxCena + "' AND Cena > '" + minCena +"'", true));
			this.subssriber.getLast().setMessageListener(new MessageListener {

				@Override
				public void onMessage(Message msg)
				{
					String proizvodjac = msg.getStringProperty("Proizvodjac");
					String naziv = msg.getStringProperty("Artikl");
					double cena = msg.getDoubleProperty("Cena");

					System.out.println("Primio");
				}
			this.tConnection.start();
		}
	}

	public void stop() throws JMSException
    {
        for(TopicSubscriber sub : this.subscriber)
            sub.close();
        
        this.ts.close();
        this.tc.close();
    }

    public void promenaStanja(Proizvod p)
    {
    	Message m = this.tSession.createMessage();
    	m.setStringProperty("Proizvodjac", p.proizvodjacNaziv);
    	m.setStringProparty("Artikl", p.naziv);
    	m.setDoubleProperty("Cena", p.cena);

    	this.publisher.send(m);
    }

    public static void main(String[] args)
    {
    	Client c = new Client();
    	Scanner s = new Scanner(System.in);

    	String artikl = "Kosulja";
    	Double minCena = 1600;
    	Double maxCena = 3200;
    	Double cena;
    	String artikl, proizvodjac;
    	List<Proizvod> lista = new LinkedList<>();

    	while (true)
    	{
    		proizvodjac = s.nextLine().trim();
    		if (proizvodjac.equals("kraj"))
    			break;
    		artikl = s.nextLine().trim();
    		cena = Double.parseDouble(s.nextLine().trim());

    		lista.add(new Proizvod(proizvodjac, artikl, cena));
    	}

    	c.Start(lista, minCena, maxCena);

    	
    	proizvodjac = s.nextLine().trim();
    	artikl = s.nextLine().trim();
    	cena = Double.parseDouble(s.nextLine().trim());
    	c.promenaStanja(new Proizvod(proizvodjac, artikl, cena));

    	c.stop();
    	System.exit(0);
    }
}

public class Proizvod implements Serializable {
    public String ID;
    public String Naziv;
    public String Proizvodjac;
    public double cena;
     
    public Proizvod(String i,String n,String p,double k)
    {
        ID=i;
        Naziv=n;
        Proizvodjac=p;
        cena=k;
    }   
}