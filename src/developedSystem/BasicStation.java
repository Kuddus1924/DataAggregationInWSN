package developedSystem;

import general.function.FuncConst;
import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class BasicStation {
    private HashMap<Integer, Cluster> wsn = new HashMap<>();
    private int N;
    private int z;
    private ArrayList<ArrayList<Message>> messagePool = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> badPool = new ArrayList<>();
    private HashMap<Integer, SecretKey> poolKey = new HashMap<>();
    private int NumberSeq;
    private ArrayList<Message> messageStore = new ArrayList<>();

    public BasicStation() {

    }

    public int getKeyGroup() {
        int p = 13;
        int q = 29;
        N = (2 * p + 1) * (2 * q + 1);
       int  d = 4 * p * q;
        int count = 1;
        while (z % N != 1) {
            z = d * count;
            count++;
        }
        /*d =  new BigInteger(Integer.toString(4 * p * q));
        int count = 1;
        while (d.mod(new BigInteger(Integer.toString(N))) != BigInteger.ONE) {
            d = d.multiply(new BigInteger(Integer.toString(count)));
            count++;
        }*/
        return N;
    }

    public SecretKey getMacKey(int id) {
        try {
            KeyGenerator gen = KeyGenerator.getInstance("HMACMD5");
            SecretKey key = gen.generateKey();
            poolKey.put(id, key);
            return key;
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public int getNumberSeq() {
        Random random = new Random();
        NumberSeq = random.nextInt();
        return NumberSeq;
    }

    public void setMessage(Message message, int idGr) {
        messageStore.add(message);
    }

    private int encrypt(Message mes) {
        BigInteger enc = mes.message;
        BigInteger v = enc.pow(z).subtract(BigInteger.ONE).mod(new BigInteger(Integer.toString(N * N))).divide(new BigInteger(Integer.toString(N)));
        return v.intValue() / getParticipants(mes.getActiveNodes(), 30);
    }

    public ArrayList<Integer> GrubbsTest(ArrayList<Integer> value, ArrayList<Integer> idGr) {
        int n = value.size();
        ArrayList<Integer> result = new ArrayList<>();
        double uc = 0;
        double sc = 0;
        ArrayList<Integer> v = new ArrayList<>(value);
        ArrayList<Integer> ig = new ArrayList<>(idGr);
        for (int i = 0; i < value.size(); i++) {
            uc += value.get(i);
        }
        uc /= value.size();
        for (int i = 0; i < value.size(); i++) {
            sc += Math.pow(value.get(i) - uc, 2);
        }
        sc /= value.size() - 1;
        sc = Math.sqrt(sc);

        for (int i = 0; i < value.size(); i++) {
            int[] res = findmMax(v, ig);
            double uPr = Math.abs(res[0] - uc) / sc;
            if (uPr > FuncConst.getGrabbsCriterion(value.size())) {
                result.add(res[2]);
                v.remove(res[3]);
                ig.remove(res[3]);
            } else {
                break;
            }
        }
        return result;
    }

    private int getParticipants(byte[] mas, int size) {
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

    public boolean checkMAC(byte[] macNode, byte[] message, int id) {
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

    public byte[] createMac(int id, BigInteger x, int numberReq) {
        byte[] ids = ByteBuffer.allocate(4).putInt(id).array();
        byte[] xmac = x.toByteArray();
        byte[] sq = ByteBuffer.allocate(4).putInt(numberReq).array();
        byte[] toMac = ArrayUtils.addAll(ids, xmac);
        toMac = ArrayUtils.addAll(toMac, sq);
        return toMac;
    }

    public int[] findmMax(ArrayList<Integer> list, ArrayList<Integer> ids) {
        int max = 0;
        int id = 0;
        int number = -1;
        for (int i = 0; i < list.size(); i++) {
            if (max < list.get(i)) {
                max = list.get(i);
                id = ids.get(i);
                number = i;
            }
        }
        return new int[]{max, id, number};
    }

    private void isEnd()
    {
        messagePool.add(messageStore);
        messageStore = new ArrayList<>();
    }
    public ArrayList<Integer> result()
    {
        ArrayList<Integer> result = new ArrayList<>();
        ArrayList<Integer> listId = new ArrayList<>();
        for(int i = 0; i < messageStore.size(); i++)
        {
            if (checkMAC(messageStore.get(i).getMac(), createMac(messageStore.get(i).getId(), messageStore.get(i).message, NumberSeq),messageStore.get(i).getIdgr()))
            {
                result.add(encrypt(messageStore.get(i)));
                listId.add(messageStore.get(i).getId());
            }
        }
        ArrayList<Integer> badlist = GrubbsTest(result,listId);
        badPool.add(badlist);
        int aggregetion = 0;
        int count = 0;
        int[] pos = getNode(listId,badlist);
        for(int i = 0; i < result.size();i++)
        {
            if(i == pos[count])
            {
                count++;
                continue;
            }
            else{
                aggregetion += result.get(i);
            }
        }
        System.out.println("result aggr = " + aggregetion);
        if(badPool.size()%3 == 0)
        {
            return getBad();
        }
        return null;
    }
    private int[] getNode(ArrayList<Integer> list,ArrayList<Integer> list1)
    {
        int[] result = new int[list1.size()];
        for(int z = 0;z < list1.size(); z++) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == list1.get(z)) {
                    result[i] = z;
                }
            }
        }
        return result;
    }
    private ArrayList<Integer> getBad()
    {
        HashSet<Integer> result = new HashSet<>();
        ArrayList<Integer> list1 = badPool.get(badPool.size() - 1);
        ArrayList<Integer> list2 = badPool.get(badPool.size() - 2);
        ArrayList<Integer> list3 = badPool.get(badPool.size() - 3);
        for(int i = 0; i < list1.size(); i++)
        {
            for(int z = 0; z < list2.size(); z++)
            {
                if(list1.get(i) == list2.get(z))
                    result.add(list1.get(i));
            }
            for(int f = 0; f < list3.size(); f++)
            {
                if(list1.get(i) == list3.get(f))
                    result.add(list1.get(i));
            }
        }
        for(int i = 0; i < list2.size(); i++) {
            for (int z = 0; z < list3.size(); z++) {
                if (list2.get(i) == list3.get(z))
                    result.add(list1.get(i));
            }
        }
        return new ArrayList<>(result);
    }



}


