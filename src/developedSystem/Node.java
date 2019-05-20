package developedSystem;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.security.Security;
import java.util.ArrayList;
import java.util.Random;

public class Node {
    private int id;
    private int aggregator;
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
        else {
            aggregator = new Random(numberReq).nextInt(sizeGr);
            return false;
        }
    }
    private  void setMessages(Message message)
    {
        if(amIanAggregator())
            messages.add(message);
    }
    private byte[] getActive()
    {
        return ByteBuffer.allocate(4).putInt((int)Math.pow(2,numberGr)).array();
    }
    private Message sendMessage()
    {
        if(amIanAggregator())
        {
            byte[] enctypt = encryptMessage();
            byte[] active = new byte[4];
            for(int i = 0;i < messages.size();i++)
            {
                enctypt = xor(enctypt,messages.get(i).getMessage());
                active = xor(active,messages.get(i).getActiveNodes());
            }
            return new Message(this.id,active,enctypt,-1);
        }
        else
        {
            return new Message(this.id,getActive(),encryptMessage(),aggregator);
        }
    }
    private byte[] xor(byte[] mas1,byte[] mas2)
    {
            byte[] xor = new byte[4];
            for(int j = 0;j < mas1.length; j++)
            {
                xor[j] = (byte)(mas1[j]^mas2[j]);
            }

        return xor;
    }





}
