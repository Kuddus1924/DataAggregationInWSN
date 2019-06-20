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
        ArrayList<Integer> result = new ArrayList<>();
        ArrayList<Integer> v = new ArrayList<>(value);
        ArrayList<Integer> ig = new ArrayList<>(idGr);
        for (int j = 0; j < value.size(); j++) {
            double uc = 0;
            double sc = 0;
            for (int i = 0; i < v.size(); i++) {
                uc += v.get(i);
            }
            uc /= v.size();
            for (int i = 0; i < v.size(); i++) {
                sc += Math.pow(v.get(i) - uc, 2);
            }
            sc /= v.size() - 1;
            sc = Math.sqrt(sc);
            int[] res = findmMax(v, ig);
            double uPr = Math.abs(res[0] - uc) / sc;
            if (uPr > FuncConst.getGrabbsCriterion(v.size() - 1)) {
                result.add(res[1]);
                v.remove(res[2]);
                ig.remove(res[2]);
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
        ArrayList<Integer> result ;
        ArrayList<Integer> listId ;
        ArrayList<ArrayList<Message>>list = new ArrayList<>();
        ArrayList<ArrayList<Integer>>  listid = new ArrayList<>();
        for(int i = 0; i < 4;i++)
        {
            list.add(new ArrayList<Message>());
            listid.add(new ArrayList<Integer>());
        }
        for(int i = 0; i < messageStore.size(); i++)
        {
            if (checkMAC(messageStore.get(i).getMac(), createMac(messageStore.get(i).getId(), messageStore.get(i).message, NumberSeq),messageStore.get(i).getIdgr()))
            {
                list.get(i%4).add(messageStore.get(i));
                listid.get(i%4).add(messageStore.get(i).getId());
            }
        }
        ArrayList<Worker> workers = new ArrayList<>();
        for(int i = 0; i < 4;i++)
        {
            workers.add(new Worker(N,z,list.get(i),listid.get(i)));
            workers.get(i).run();
        }
        for(int i = 0; i < 4;i++)
        {
            try {
                workers.get(i).join();
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }
        }
        result = new ArrayList<>(workers.get(0).getResult());
        listId = new ArrayList<>(workers.get(0).getIds());
        for(int i = 1;i < 4;i++)
        {
            result.addAll(workers.get(i).getResult());
            listId.addAll(workers.get(i).getIds());
        }

        ArrayList<Integer> badlist = GrubbsTest(result,listId);
        badPool.add(badlist);
        int aggregetion = 0;
        int count = 0;
        int count1 = 0;
        ArrayList<Integer> stat = new ArrayList<>();
        Boolean flag  = false;
        int[] pos = getNode(listId,badlist);
        for(int i = 0; i < result.size();i++)
        {
            flag = false;
            if(pos.length != 0 && count != pos.length) {
                for(int z = 0 ; z < pos.length;z++) {
                    if (i == pos[z]) {
                        System.out.println("Did not accept result from " + listId.get(i) + " result " + result.get(i));
                        flag = true;
                        break;

                    }
                }
            }
            if(!flag) {
                stat.add(result.get(i));
                aggregetion += result.get(i);
                count1++;
            }
        }
        System.out.println("result aggr = " + (double)aggregetion/count1);
        System.out.println("M = " + getM(stat));
        System.out.println("D = " + getD(stat,getM(stat)));
        if(badPool.size()%3 == 0)
        {
            return getBad();
        }
        messageStore = new ArrayList<>();
        return null;
    }
    private int[] getNode(ArrayList<Integer> list,ArrayList<Integer> list1)
    {
        int[] result = new int[list1.size()];
        for(int z = 0;z < list1.size(); z++) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == list1.get(z)) {
                    result[z] = i;
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
                    result.add(list2.get(i));
            }
        }
        return new ArrayList<>(result);
    }
    public double getBadlistSize()
    {
        double res = 0.0;
        for(int i = 0; i < badPool.size(); i++)
        {
            res+=badPool.get(i).size();
        }

        return res;
    }
    public ArrayList<Integer> GrubbsTest1(ArrayList<Integer> value, ArrayList<Integer> idGr) {
        ArrayList<Integer> result = new ArrayList<>();
        ArrayList<Integer> v = new ArrayList<>(value);
        ArrayList<Integer> ig = new ArrayList<>(idGr);
        for (int j = 0; j < value.size(); j++) {
            ArrayList<Integer> sort = new ArrayList<>(v);
            ArrayList<Integer> raz = new ArrayList<>();
            Collections.sort(sort);
            int max = 0;
            int indexMax = 0;
            for(int i = 0; i < sort.size() - 1;i++)
            {
                raz.add(sort.get(i + 1) - sort.get(i));
                if(raz.get(i) > max)
                {
                    indexMax = i;
                    max = raz.get(i);
                }
            }
            if((indexMax + 1 >= sort.size() * 0.3) &&(indexMax + 1 <= sort.size() - sort.size() * 0.3 ))
            {
                return result;
            }
            ArrayList<Integer> sortNew = new ArrayList<>();
            if(indexMax + 1< sort.size() * 0.3)
            {
                for(int i = indexMax; i < sort.size();i++)
                {
                    sortNew.add(sort.get(i));
                }
            }
            if(indexMax + 1 >  sort.size() - sort.size() * 0.3)
            {
                for(int i = 0; i <= indexMax;i++)
                {
                    sortNew.add(sort.get(i));
                }
            }
            double uc = 0;
            double sc = 0;
            for (int i = 0; i < sortNew.size(); i++) {
                uc += sortNew.get(i) * sortNew.size();
            }
            uc /= sortNew.size();
            for (int i = 0; i < sortNew.size(); i++) {
                sc += Math.pow(sortNew.get(i) - uc, 2) * sortNew.size();
            }
            sc /= sortNew.size() - 1;
            sc = Math.sqrt(sc);
            int[] res = findmMax(v, ig);
            double uPr = Math.abs(uc - max) / sc;
            if (uPr > FuncConst.getGrabbsCriterion(sort.size())) {
                result.add(res[1]);
                v.remove(res[2]);
                ig.remove(res[2]);
            } else {
                break;
            }
        }
        return result;
    }
    public double getM(ArrayList<Integer> ls)
    {
        ArrayList<Integer> count= new ArrayList<>();
        HashSet<Integer> res = new HashSet<>(ls);
        ArrayList<Integer> result = new ArrayList<>(res);
        int c = 0;
        for(int i = 0;i < result.size();i++)
        {
            c = 0;
            for(int j = 0; j< ls.size(); j++)
            {
                if(result.get(i) == ls.get(j))
                    c++;
            }
            count.add(c);
        }
        double M = 0;
        for(int i = 0; i < result.size();i++)
        {
            double y = count.get(i);
            double jz =ls.size();
            double neo =  result.get(i);
            M += (y/jz) * neo;
        }
        return M;
    }
    public double getD(ArrayList<Integer> ls, Double M)
    {
        ArrayList<Integer> count= new ArrayList<>();
        HashSet<Integer> res = new HashSet<>(ls);
        ArrayList<Integer> result = new ArrayList<>(res);
        int c = 0;
        for(int i = 0;i < result.size();i++)
        {
            c = 0;
            for(int j = 0; j< ls.size(); j++)
            {
                if(result.get(i) == ls.get(j))
                    c++;
            }
            count.add(c);
        }
        double D = 0;
        for(int i = 0; i < result.size();i++)
        {
            double y = count.get(i);
            double jz =ls.size();
            D += (y/jz) * Math.pow((result.get(i) - M),2);
        }
        return D;
    }
    private class Worker extends Thread
    {
        private int N;
        private int z;
        private ArrayList<Message> data;
        private ArrayList<Integer> id;
        private ArrayList<Integer> result = new ArrayList<>();
        public Worker(int N, int z, ArrayList<Message> data,ArrayList<Integer> id)
        {
            this.N = N;
            this.z = z;
            this.data = data;
            this.id = id;
        }
        @Override
        public void run()
        {
            for(int i = 0;i < data.size();i++)
            {
                result.add(encrypt(data.get(i)));
            }
        }
        private int encrypt(Message mes) {
            BigInteger enc = mes.message;
            BigInteger v = enc.pow(z).subtract(BigInteger.ONE).mod(new BigInteger(Integer.toString(N * N))).divide(new BigInteger(Integer.toString(N)));
            return v.intValue() / getParticipants(mes.getActiveNodes(), 30);
        }
        public ArrayList<Integer> getResult()
        {
            return result;
        }
        public ArrayList<Integer> getIds() {
            return id;
        }
    }



}


