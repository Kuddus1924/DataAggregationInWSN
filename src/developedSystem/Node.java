package developedSystem;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.security.Security;
import java.util.ArrayList;
import java.util.Random;

public class Node {
    private int id;
    private boolean aggregator;
    private int key;
    private int physicalPhenomenon;
    private int numberReq;
    private int sizeGr;
    private int mod;
    private int numberGr;
    private ArrayList<Message> messages = new ArrayList();
    public Node(int id,int key, int gr, int numberGr)
    {
        this.id = id;
        this.key = key;
        this.sizeGr = gr;
        this.numberGr = numberGr;
    }

    public void setNumberReq(int numberReq) {
        this.numberReq = numberReq;
    }
    private byte[] encryptMessage()
    {
        int generateKey = new SecureRandom(ByteBuffer.allocate(4).putInt(numberReq+key).array()).nextInt()% mod;
        int encrypt = (generateKey + physicalPhenomenon)%mod;
        return ByteBuffer.allocate(4).putInt(encrypt).array();
    }
    private int getGroupNumber()
    {
        return (int)Math.pow(2,numberGr - 1);
    }
    private boolean amIanAggregator()
    {
        if(numberGr == new Random(numberReq).nextInt(sizeGr))
            return true;
        else
            return false;
    }
    private  void setMessages(Message message)
    {
        if(amIanAggregator())
            messages.add(message);
    }




}
