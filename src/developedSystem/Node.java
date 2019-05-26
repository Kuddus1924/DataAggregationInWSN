package developedSystem;

import general.function.ValueModeling;
import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class Node {
    private int id;
    private int aggregator;
    private int key;
    private String algo = "HMACMD5";
    private SecretKey keyMac;
    private int physicalPhenomenon;
    private int numberReq;
    private int myNumber;
    private BigInteger test;
    private int mod;
    private int numberGr;
    private boolean bad ;
    private ArrayList<Message> messages = new ArrayList();
    public Node(int id, int gr, int numberGr, boolean flag)
    {
        this.id = id;
        this.myNumber = gr;
        this.numberGr = numberGr;
        bad = flag;
    }
    public void setKey(int key,SecretKey k) {
        this.key = key;
        keyMac = k;
    }

    public int getId() {
        return id;
    }

    public void setNumberReq(int numberReq) {
        this.numberReq = numberReq;
    }
    private BigInteger encryptMessage()
    {
        int r = Crypto.getDeduction(key);
        BigInteger nMes = new BigInteger(Integer.toString(key + 1)).pow(physicalPhenomenon);
        BigInteger rMes = new BigInteger(Integer.toString(r)).pow(key);
        BigInteger mes = rMes.multiply(nMes).mod(new BigInteger(Integer.toString((int)Math.pow(key,2))));
        return mes;


    }
    private boolean amIanAggregator()
    {
        if(myNumber == aggregator)
            return true;
        else {
            return false;
        }
    }
    public void setMessages(Message message)
    {
        if(amIanAggregator())
            messages.add(message);
    }
    private byte[] getActive()
    {
        return ByteBuffer.allocate(4).putInt((int)Math.pow(2,myNumber)).array();
    }
    public Message sendMessage()
    {
        physicalPhenomenon = ValueModeling.getValue(bad);
        if(amIanAggregator())
        {
            BigInteger enctypt = encryptMessage();
            byte[] active = new byte[4];
            for(int i = 0;i < messages.size();i++)
            {
                if(checkMessage(messages.get(i))) {
                    enctypt = enctypt.multiply(messages.get(i).getMessage()).mod(new BigInteger(Integer.toString(key * key)));
                    active = xor(active, messages.get(i).getActiveNodes());
                }
            }
            Message result =  new Message(this.id,active,enctypt,-1,getMAC(createMac(this.id,enctypt,numberReq)));
            result.setIdgr(numberGr);
            return result;
        }
        else
        {
            test = encryptMessage();
            return new Message(this.id,getActive(),test,aggregator,getMAC(createMac(this.id,test,numberReq)));
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
    public byte[] getMAC(byte[] message) {
        try {
            Mac mac = Mac.getInstance(algo);
            mac.init(keyMac);
            return mac.doFinal(message);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public boolean checkMAC(byte[] macNode, byte[] message) {
        try {
            Mac mac = Mac.getInstance(algo);
            mac.init(keyMac);
            byte [] finals = mac.doFinal(message);
            if (Arrays.equals(macNode,finals))
                return true;
            else
                return false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
    public byte[] createMac(int id, BigInteger x,int numberReq)
    {
        byte[] ids = ByteBuffer.allocate(4).putInt(id).array();
        byte[] xmac =  x.toByteArray();
        byte[] sq = ByteBuffer.allocate(4).putInt(numberReq).array();
        byte[] toMac = ArrayUtils.addAll(ids, xmac);
        toMac = ArrayUtils.addAll(toMac, sq);
        return toMac;
    }
    private boolean checkMessage(Message mes) {
        int count = 0;
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).id == mes.id) {
                count++;
            }
        }
        if (count == 1)
        {
            if(checkMAC(mes.getMac(),createMac(mes.id,mes.message,numberReq)))
            {
                return true;
            }
        }
        else {
            return false;
        }
        return false;
    }
    public  void  setAggregator(int aggregator)
    {
        this.aggregator = aggregator;
    }
    public  double generateNumber()
    {
        return Math.random();
    }
    public void clean()
    {
        messages = new ArrayList<>();
    }


}
