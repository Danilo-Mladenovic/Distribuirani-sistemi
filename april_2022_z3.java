public class Klijent {

    Topic obavestenje;
    TopicConnection tc;
    TopicSession ts;
    TopicConnectionFactory tcf;
    TopicSubscriber subscriber;

    ArrayList<Proizvod> rasprodaje;

    public Klijent() throws NamingException, JMSException {

        InitialContext ictx = new InitialContext();

        obavestenje = (Topic) ictx.lookup("jms/Klijent");

        tcf = (TopicConnectionFactory) ictx.lookup("jms/tcf");

        tc = (TopicConnection) tcf.createTopicConnection();
        ts = (TopicSession) tc.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

    }

    public void start() throws JMSException {

        tc.start();
    }

    public void Dodaj(String p, double min, double max) throws JMSException {

        subscriber = (TopicSubscriber) ts.createSubscriber(obavestenje, "Naziv = " + p + "' AND cena >= " + min + " AND cena <= " + max, true);
        subscriber.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                   ObjectMessage m=(ObjectMessage) message;
                try {
                    Proizvod q=(Proizvod) m.getObject();
                    System.out.println("primio sma isk");
                } catch (JMSException ex) {
                    Logger.getLogger(Klijent.class.getName()).log(Level.SEVERE, null, ex);
                }
                   
            }
        });
        
    
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

public class Sistem {
      Topic obavestenje;
    TopicConnection tc;
    TopicSession ts;
    TopicConnectionFactory tcf;
    TopicPublisher publisher;
    
    
 public Sistem() throws NamingException, JMSException {

        InitialContext ictx = new InitialContext();

        obavestenje = (Topic) ictx.lookup("jms/Klijent");

        tcf = (TopicConnectionFactory) ictx.lookup("jms/tcf");

        tc = (TopicConnection) tcf.createTopicConnection();
        ts = (TopicSession) tc.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

        publisher=(TopicPublisher) ts.createPublisher(obavestenje);
        
        tc.start();
    }

    public void salji(Proizvod p) throws JMSException
    {
        ObjectMessage m=(ObjectMessage)ts.createObjectMessage();
        m.setObject(p);
        publisher.publish(m);
    }
}