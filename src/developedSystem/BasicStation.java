package developedSystem;

import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class BasicStation {
    private HashMap<Integer, Cluster> wsn = new HashMap<>();
    private int N;
    private int d;
    private HashMap<Integer,SecretKey> poolKey = new HashMap<>();
    private int NumberSeq;
    private HashMap<Integer, ArrayList<Message>> messageStore = new HashMap<>();

    public BasicStation() {

    }

    public int getKeyGroup() {
        int p = 53;
        int q = 61;
        N = (2 * p + 1) * (2 * q + 1);
        d = 4 * p * q;
        int count = 1;
        while (d % N == 1) {
            d = d * count;
            count++;
        }
        return N;
    }
    public SecretKey getMacKey( int id ) {
            try {
                KeyGenerator gen = KeyGenerator.getInstance("HMACMD5");
                SecretKey key = gen.generateKey();
                poolKey.put(id, key);
                return key;
            }
            catch (NoSuchAlgorithmException e)
            {
                System.out.println(e.getMessage());
            }
            return null;
    }

    public int getNumberSeq() {
        Random random = new Random();
        return random.nextInt();
    }

    public void setMessage(Message message, int idGr) {
        messageStore.get(idGr).add(message);
    }

    private int encrypt(Message mes) {
        BigInteger enc = mes.message;
        BigInteger v = enc.pow(d).subtract(BigInteger.ONE).mod(new BigInteger(Integer.toString(N * N))).divide(new BigInteger(Integer.toString(N)));
        return v.intValue() / getParticipants(mes.getActiveNodes(),30);
    }

    public ArrayList<Integer> GrubbsTest(ArrayList<Integer> value,ArrayList<Integer> idGr) {
        int n = value.size();
        ArrayList<Integer> result = new ArrayList<>();
        double uc = 0;
        double sc = 0;
        for (int i = 0; i < value.size(); i++) {
            uc += value.get(i);
        }
        uc /= value.size();
        for (int i = 0; i < value.size(); i++) {
            sc += Math.pow(value.get(i) - uc, 2);
        }
        sc /= value.size() - 1;
        sc = Math.sqrt(sc);

       for(int i = 0; i < value.size() ; i++)
       {

       }
    }

    private int  getParticipants(byte[] mas, int size) {
        int result = 0;
        ByteBuffer wrapped = ByteBuffer.wrap(mas);
        int parti = wrapped.getInt();
        for (int i = 0; i < size; i++) {
            if ((parti & (int) Math.pow(2, i)) == Math.pow(2, i)) {
                result++;
            }
        }
        return result;

    }

    /*private int getKey(ArrayList<Integer> id, int grID) {
        int resultKey = 0;
        ArrayList<Integer> keyGr = key.get(grID);
        for (int i = 0; i < id.size(); i++) {
            resultKey += keyGr.get(id.get(i));
        }
        return resultKey;
    }*/
    public boolean checkMAC(byte[] macNode, byte[] message,int id) {
        try {
            Mac mac = Mac.getInstance("HMACMD5");
            mac.init(poolKey.get(id));
            if (Arrays.equals(macNode, mac.doFinal(message)))
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
    public  byte[] findmMax(ArrayList<Integer> list, ArrayList<Integer> ids)
    {
        int max = 0;
        int agr = 0;
        int id = 0;
        int number = -1;
        for (int i = 0; i < list.size(); i++) {
            if(max <  list.get(i))
            {
                max =  list.get(i);
                id = ids.get(i);
                number = i;
            }
        }
    }

}


