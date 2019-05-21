package developedSystem;

import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Node {
    private int id;
    private int aggregator;
    private int key;
    private String algo = "HMACMD5";
    private SecretKey keyMac;
    private int physicalPhenomenon;
    private int numberReq;
    private int sizeGr;
    private int mod;
    private int numberGr;
    private ArrayList<Message> messages = new ArrayList();
    public Node(int id, int gr, int numberGr)
    {
        this.id = id;
        this.sizeGr = gr;
        this.numberGr = numberGr;
    }

    public void setKey(int key,SecretKey k) {
        this.key = key;
        keyMac = k;
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
    public void setMessages(Message message)
    {
        if(amIanAggregator())
            messages.add(message);
    }
    private byte[] getActive()
    {
        return ByteBuffer.allocate(4).putInt((int)Math.pow(2,numberGr)).array();
    }
    public Message sendMessage()
    {
        if(amIanAggregator())
        {
            BigInteger enctypt = encryptMessage();
            byte[] active = new byte[4];
            for(int i = 0;i < messages.size();i++)
            {
                if()
                enctypt = enctypt.multiply(messages.get(i).getMessage().mod(new BigInteger(Integer.toString(key*key))));
                active = xor(active,messages.get(i).getActiveNodes());
            }
            return new Message(this.id,active,enctypt,-1,getMAC(createMac(this.id,encryptMessage())));
        }
        else
        {
            return new Message(this.id,getActive(),encryptMessage(),aggregator,getMAC(createMac(this.id,encryptMessage())));
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
            if (Arrays.equals(macNode, mac.doFinal(message)))
                return true;
            else
                return false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
    public byte[] createMac(int id, BigInteger x)
    {
        byte[] ids = ByteBuffer.allocate(4).putInt(id).array();
        byte[] xmac =  x.toByteArray();
        byte[] toMac = ArrayUtils.addAll(ids, xmac);
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
            if(checkMAC(mes.getMac(),createMac(mes.id,mes.message)))
            {
                return true;
            }
        }
        else {
            return false;
        }
        return false;
    }


}
