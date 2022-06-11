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
		this.topic = (Topic) ic.lookup("tapril2022");

		ic.close();

		this.tConnection = (TopicConnection) tcf.createTopicConnection();
		this.tSession = (TopicSession) tConnection.createTopicSession(false, Sessio.AUTO_ACKNOWLEDGE);

		this.publisher = this.tSession.createPublisher(this.topic);
		this.subscriber = new LinkedList<>();
	}

	public void Start(List<Proizvod> proizvodi, string artikl, double minCena, double maxCena)
	{
		for (Proizvod p : proizvodi)
		{
			this.subscriber.add(this.tSession.createSubscriber(this.topic, "Artikl = '" + artikl + "' AND Cena < '" + maxCena + "' AND Cena > '" + minCena +"'"));
			this.subssriber.getLast().setMessageListener(new MessageListener {

				@Override
				public void onMessage(Message msg)
				{
					String proizvodjac = msg.getStringProperty("Proizvodjac");
					String naziv = msg.getStringProperty("Artikl");
					double cena = msg.getDoubleProperty("Cena");

					Symste.out.println("dsadas");
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

    public void promenaStanja(Proizvod p, int cena)
    {
    	Message m = this.tSession.createMessage();
    	m.setStringProperty("Proizvodjac", p.proizvodjacNaziv);
    	m.setStringProparty("Artikl", p.naziv);
    	m.setDoubleProperty("Cena", cena);

    	this.publisher.send(m);
    }

    public static void main(String[] args)
    {
    	Client c = new Client();
    	Scanner s = new Scanner(System.in);

    	String artikl = "dada";
    	Double minCena = 1600;
    	Double maxCena = 3200;
    	Double cena;
    	String artikl, proizvodjac;
    	List<Proizvod> lista = new LinkedList<>();

    	while (true)
    	{
    		proizvodjac = s.nextLine().trim();
    		if (input.equals("kraj"))
    			break;
    		artikl = s.nextLine().trim();
    		cena = Double.parseDouble(s.nextLine().trim());

    		lista.add(new Proizvod(proizvodjac, artikl, cena));
    	}

    	c.start(lista, artikl, minCena, maxCena);

    	
    	proizvodjac = s.nextLine().trim();
    	artikl = s.nextLine().trim();
    	cena = Double.parseDouble(s.nextLine().trim());
    	c.promenaStanja(new Proizvod(proizvodjac, artikl, cena), cena);
    }
}