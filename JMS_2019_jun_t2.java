

public class Stanica
{
    private String lokacija;

    private Topic tStigao;
    private Topis tKvar;

    private TopicConnection tc;
    private TopicSession ts;

    private TopicPublisher pubStigao;
    private TopicPublisher pubKvar;

    private TopisSubscriber subStigao;
    private TopisSubscriber subKvar;

    public Stanica(String lokacija)
    {
        this.lokacija = lokacija;

        InitialContext ictx = new InitialContext();
        tStigao = (Topic) ictx.lookup("tStigao");
        tKvar = (Topic) ictx.lookup("tKvar");
        TopicConnectionFactory tcf = (TopicConnectionFactory) ictx.lookup("tcfStanica");
        ictx.close();

        tc = (TopicConnection) tcf.createConnection();
        ts = (TopicSession) tc.createSession(false, Sessio.AUTO_ACKNOWLEDGE);
    
        pubStigao = (TopicPublisher) ts.createPublisher(tStigao);
        pubKvar = (TopicPublisher) ts.createPublisher(tKvar);

        subStigao = (TopicSubscriber) ts.createSubscriber(tStigao, "Stanica LIKE '%" + this.lokacija + "%'", false);
        subStigao.setMessageListener(new MessegeListener() {
            @Override
            public void onMessage(Message msg)
            {
                TextMessage txt = (TextMessage)msg;
                
            }
        });







        subKvar = 
    }
}