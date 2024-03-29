package SDAP;

import general.function.*;
import org.apache.commons.math3.distribution.TDistribution;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class BS {
private ArrayList<Message> messageStore = new ArrayList<>();
private ArrayList<byte[]> attestationMac = new ArrayList<>();
private HashMap<Integer,SecretKey> keyStoreMAC = new HashMap<>();
private  HashMap<Integer,SecretKey> keyStoreEnc = new HashMap<>();
private int sq;
private int sa;
private  IvParameterSpec iv;
    private Random random = new Random();
    public void BS(){
    }
    public void clean()
    {
        messageStore =  new ArrayList<>();
    }
    public void setMessage(Message tmp)
    {
        if(tmp.recipient == 0)
            messageStore.add(tmp);
    }
    public SecretKey getKeymMAC(int id)
    {
        try {
            KeyGenerator gen = KeyGenerator.getInstance("HMACMD5");
            SecretKey key = gen.generateKey();
            keyStoreMAC.put(id, key);
            return key;
        }
        catch (NoSuchAlgorithmException e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public SecretKey getkeyEnc(int id)
    {
        try {
            KeyGenerator gen = KeyGenerator.getInstance("AES");
            SecretKey key = gen.generateKey();
            keyStoreEnc.put(id, key);
            return key;
        }
        catch (NoSuchAlgorithmException e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }
    private boolean checkMessage(Message tmp)
    {
            if(tmp.flag)
            {
                if(FuncConst.FunctionH(sq,tmp.getGroupLeaderId()) < FuncConst.FunctionG(tmp.getC()))
                {
                    return  true;
                }
                else
                {
                    return false;
                }
            }
            if(!tmp.flag)
            {
                if(FuncConst.FunctionH(sq,tmp.getId()) < FuncConst.FunctionG(tmp.getC()))
                {
                    return  false;
                }
                else
                {
                    return true;
                }
            }
            return false;
    }
    public byte[] decryptMessage(byte[] message, int idDescendant) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        SecretKey key = keyStoreEnc.get(idDescendant);
        cipher.init(Cipher.DECRYPT_MODE, key,iv);
        return cipher.doFinal(message);
    }
    public int[] decryptMessage(Message tmp) {
        byte[] decrypt = null;
        try {
            decrypt = decryptMessage(tmp.encrypt,tmp.getId());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        int[] dec = FuncConst.split(decrypt);
        return dec;
    }
    public ArrayList<int[]> getAllMessage()
    {
        ArrayList<int[]>result = new ArrayList<>();
        int[] resultAGGR = new int[4];
        resultAGGR[0] = 0;
        int count = 0;
        for(int i = 0; i < messageStore.size(); i++)
        {
            if (checkMessage(messageStore.get(i)))
            {
                if(messageStore.get(i).getGroupLeaderId() == 0)
                {
                    int[] dec = decryptMessage(messageStore.get(i));
                    resultAGGR[1] += dec[0];
                    resultAGGR[2] += dec[1];
                    resultAGGR[3] = dec[2];
                    count++;
                    continue;
                }
                int[] res = new int[4];
                res[0] = messageStore.get(i).getId();
                int[] dec = decryptMessage(messageStore.get(i));
                res[1] = dec[0];
                res[2] = dec[1];
                res[3] = dec[2];
                result.add(res);
            }
        }
        if(count != 0) {
            resultAGGR[2] = resultAGGR[2] / count;
            result.add(resultAGGR);
        }
        return result;
    }

    public  int generateSq() {
        sq = random.nextInt()%100;
        return sq;
    }
    public ArrayList<Integer> grubbsTest(ArrayList<int[]> tuples)
    {
        ArrayList<Integer> result = new ArrayList<>();
        while (true) {
            int n = tuples.size();
            double uc = 0;
            double sc = 0;
            double uu = 0;
            double su = 0;
            for (int i = 0; i < tuples.size(); i++) {
                uc += tuples.get(i)[1];
                uu += tuples.get(i)[2];
            }
            uc /= tuples.size();
            uu /= tuples.size();
            for (int i = 0; i < tuples.size(); i++) {
                sc += Math.pow(tuples.get(i)[1]- uc,2);
                su += Math.pow(tuples.get(i)[2]- uc,2);
            }
            sc /= tuples.size() - 1;
            su /= tuples.size() - 1;
            sc = Math.sqrt(sc);
            su = Math.sqrt(su);
            int max = 0;
            int agr = 0;
            int id = 0;
            int number = -1;
            for (int i = 0; i < tuples.size(); i++) {
                if(max <  tuples.get(i)[1])
                {
                    max =  tuples.get(i)[1];
                    agr = tuples.get(i)[2];
                    id = tuples.get(i)[0];
                    number = i;
                }
            }
            double Zc =(max - uc)/sc;
            double Zu = Math.abs(agr - uu)/su;
            if((getProbality(Zc,n) * getProbality(Zu,n)) < 0.05)
            {
                if (tuples.size() == 0)
                {
                    break;
                }
                tuples.remove(number);
                result.add(id);
            }
            else
            {
                break;
            }
        }
        return result;
    }
    public  int getSa()
    {
        sa = random.nextInt();
        return sa;
    }
    public int[] decryptA(Message message)
    {
        try {
            if (message.encrypt.length == 16) {
                byte[] mes = decryptMessage(message.encrypt, message.id);
                return FuncConst.split4(mes);
            }
            else
            {
                attestationMac.add(message.mac);
                byte[] mes = decryptMessage(message.encrypt, message.id);
                return FuncConst.split4(mes);
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public byte[] getMac(int index)
    {
        return messageStore.get(index).mac;
    }
    public void setIv(IvParameterSpec i)
    {
        iv = i;
    }
    private   double getProbality(double z,int n) {
        double t = Math.sqrt(((n - 2) * n * Math.pow(z, 2)) / (Math.pow(n - 1, 2) - n * Math.pow(z, 2)));
        TDistribution td = new TDistribution(n-2);
        double p = (1 - td.cumulativeProbability(t)) * n;
        return p;
    }





}
