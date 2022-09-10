

public class VCalcRequest implements Serializable
{
    public int cId;
    public Vector<Float> a;
    public Vector<Float> b;
    public IVCalcCallback callback;

    public VCalcRequest(int cId, Vector<Float> a, Vector<Float> b; IVCalcCallback cb) throws RemoteException
    {
        this.cId = cId;
        this.a = a;
        this.b = b;
        this.callback = cb;
    }
}


