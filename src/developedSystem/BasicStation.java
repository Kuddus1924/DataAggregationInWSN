package developedSystem;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class BasicStation {
    private HashMap<Integer, Cluster> wsn = new HashMap<>();
    private int N;
    private int d;
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
        return v.intValue();
    }

    public ArrayList<Integer> GrubbsTest() {

    }

    private ArrayList<Integer> getParticipants(byte[] mas, int size) {
        ArrayList<Integer> result = new ArrayList<>();
        ByteBuffer wrapped = ByteBuffer.wrap(mas);
        int parti = wrapped.getInt();
        for (int i = 0; i < size; i++) {
            if ((parti & (int) Math.pow(2, i)) == Math.pow(2, i)) {
                result.add(i);
            }
        }
        return result;

    }

    private int getKey(ArrayList<Integer> id, int grID) {
        int resultKey = 0;
        ArrayList<Integer> keyGr = key.get(grID);
        for (int i = 0; i < id.size(); i++) {
            resultKey += keyGr.get(id.get(i));
        }
        return resultKey;
    }
}





}
