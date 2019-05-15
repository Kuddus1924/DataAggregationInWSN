import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Node {
    private int id;
    private int physicalPhenomenon;
    private int seqNumber;
    private HashMap<Integer, SecretKey> keyStore = new HashMap<>();
    private ArrayList<Message> messageStore = new ArrayList();
    private ArrayList<int[]> descendants = new ArrayList<>();
    private ArrayList<int[]> shippingTable = new ArrayList<>();
    private SecretKey keyPair;
    private SecretKey[] keyBS;
    private boolean isNotEndNode;
    private String algo = "HMACMD5";
    private int idParants;
    private byte[] attestateMac;
    private KeyGenerator gen;
    private Message message;
    private int agr = 0;
    private int C = 0;


    public Node(int id, int idP) {
        this.id = id;
        isNotEndNode = false;
        physicalPhenomenon = 0;
        seqNumber = 0;
        idParants = idP;
    }

    /*public void setMessage(Message mes) {
        messageStore.add(mes);
    }*/

    public int getIdParants() {
        return idParants;
    }

    public int getId() {
        return id;
    }

    public void setKeyPair(SecretKey keyPair) {
        this.keyPair = keyPair;
    }

    public void setKeyBS(SecretKey keyBS,SecretKey macKeyBS) {
        this.keyBS[0] = keyBS;
        this.keyBS[1] = macKeyBS;
    }

    public void setSeqNumber(int seqNumber) {
        this.seqNumber = seqNumber;
    }

    public void setAgregator(boolean agregator) {
        this.isNotEndNode = agregator;
        if(agregator) {
            try {
                gen = KeyGenerator.getInstance(algo);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public byte[] getMAC(byte[] message) {
        try {
            Mac mac = Mac.getInstance(algo);
            mac.init(keyBS[1]);
            return mac.doFinal(message);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public SecretKey getKeyPair(int idNode) {
        if (isNotEndNode) {
            SecretKey key = gen.generateKey();
            keyStore.put(idNode, key);
            return key;
        } else {
            System.out.println("Error! This node is not an aggregator");
            return null;
        }
    }

    public boolean checkMAC(byte[] macNode, int idNode, byte[] message) {
        if (isNotEndNode) {
            SecretKey key = keyStore.get(idNode);
            if (key == null) {
                return false;
            }
            try {
                Mac mac = Mac.getInstance(algo);
                mac.init(key);
                if (Arrays.equals(macNode, mac.doFinal(message)))
                    return true;
                else
                    return false;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        } else {
            System.out.println("Error! This node is not an aggregator");
            return false;
        }
        return false;
    }

    public byte[] generateEncryptMessage(byte[] message, boolean bs) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        if(bs)
        cipher.init(Cipher.ENCRYPT_MODE, keyBS[0]);
        else
        cipher.init(Cipher.ENCRYPT_MODE, keyPair);
        return cipher.doFinal(message);
    }

    public byte[] decryptMessage(byte[] message, int idDescendant) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        SecretKey key = keyStore.get(idDescendant);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(message);
    }

    public Message getMessageParants(int count) {
        this.seqNumber = count;
        if (isNotEndNode) {
            ArrayList<Integer> agregation = new ArrayList<>();
            ArrayList<byte[]> mac = new ArrayList<>();
            ArrayList<Integer> counts = new ArrayList<>();
            for (int i = 0; i < messageStore.size(); i++) {
                Message tmp = messageStore.get(i);
                if (tmp.flag == false) {
                    byte[] decrypt = null;
                    try {
                        decrypt = decryptMessage(tmp.encrypt, tmp.id);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    int[] dec = FuncConst.split(decrypt);
                    if (dec[2] == this.seqNumber) {
                        agregation.add(dec[1]);
                        mac.add(tmp.mac);
                        counts.add(dec[0]);
                    } else {
                        continue;
                    }
                    descendants.add(new int[]{tmp.getId(), tmp.getC()});
                }
            }
            int c = sum(counts);
            C = c;
            Message mes;
            agregation.add(this.physicalPhenomenon);
            agr = sum(agregation) / agregation.size();
            attestateMac = xorMac(mac);
            if (FuncConst.FunctionH(this.seqNumber, this.id) < FuncConst.FunctionG(c)) {
                mes = new Message(id, c, getEncrypt(c, agr, this.seqNumber, true), getByteMac(1, c, agr, xorMac(mac), this.seqNumber, true), true, this.idParants);//если лидер
            } else {
                if (idParants == 0) {
                    mes = new Message(id, c, getEncrypt(c, agr, this.seqNumber, true), getByteMac(0, c, agr, xorMac(mac), seqNumber, true), false, idParants);//если не лидер
                } else {
                    mes = new Message(id, c, getEncrypt(c, agr, this.seqNumber, false), getByteMac(0, c, agr, xorMac(mac), seqNumber, true), false, idParants);//если не лидер
                }
            }
            this.message = mes;
            return mes;
        }
        else {
            Message mes = new Message(this.id, 1, getEncrypt(1, this.physicalPhenomenon, seqNumber, false), getByteMac(0, 1, this.physicalPhenomenon, null, this.seqNumber, false), false, idParants);
            this.message = mes;
            this.attestateMac = getByteMac(0, 1, this.physicalPhenomenon, null, this.seqNumber, false);
            return mes;
        }
    }

    public boolean isNotEndNode() {
        return isNotEndNode;
    }

    public ArrayList<Message> getForwardingMessage()
    {
        ArrayList <Message>result = new ArrayList();
        for(int i = 0;i < messageStore.size();i++)
        {
            int[] store = new int[3];
            Message tmp = messageStore.get(i);
            if(tmp.flag) {
                store[0] = seqNumber;
                store[1] = tmp.getId();
                store[2] = tmp.getGroupLeaderId();
                shippingTable.add(store);
                tmp.setRecipient(this.id);
                result.add(tmp);
            }
        }
        return result;
    }
    private int sum(ArrayList<Integer> tmp)
    {
        int result = 1;
        for(int i = 0;i < tmp.size();i++)
        {
            result += tmp.get(i);
        }
        return result;
    }
    private byte[] getEncrypt(int c1, int pp1, int sq1,boolean bs)
    {
        byte[] c = ByteBuffer.allocate(4).putInt(c1).array();
        byte[] pp = ByteBuffer.allocate(4).putInt(pp1).array();
        byte[] sq = ByteBuffer.allocate(4).putInt(sq1).array();
        byte[] mes = ArrayUtils.addAll(c, pp);
        byte[] encrypt = null;
        mes = ArrayUtils.addAll(mes, sq);
        try {
            encrypt = generateEncryptMessage(mes,bs);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        return encrypt;
    }
    private byte[] getByteMac(int flag1,int count,int pp1,byte[] macXor,int sq1,boolean parents)
    {

        byte[] flag = ByteBuffer.allocate(4).putInt(flag1).array();
        byte[] id = ByteBuffer.allocate(4).putInt(this.id).array();
        byte[] c = ByteBuffer.allocate(4).putInt(count).array();
        byte[] pp = ByteBuffer.allocate(4).putInt(pp1).array();
        byte[] sq = ByteBuffer.allocate(4).putInt(sq1).array();
        byte[] toMac = ArrayUtils.addAll(flag, c);
        toMac = ArrayUtils.addAll(toMac, id);
        toMac = ArrayUtils.addAll(toMac, pp);
        if(parents) {
            toMac = ArrayUtils.addAll(toMac, macXor);
        }
        toMac = ArrayUtils.addAll(toMac, sq);
        byte[] mac = getMAC(toMac);
        return mac;
    }
    private byte[] xorMac(ArrayList<byte[]> tmp)
    {
        byte[] xor = new byte[tmp.get(0).length];
        byte[] mac;
        for (int i = 0; i < tmp.size(); i++)
        {
            mac = tmp.get(i);
            for(int j = 0;j < xor.length; j++)
            {
                xor[j] = (byte)(xor[j]^mac[j]);
            }
        }
        return xor;
    }
    public void setMessageStore(Message mes)
    {
        if(mes.recipient == this.id)
        {
            messageStore.add(mes);
        }
    }
    public int sumC()
    {
        int result = 1;
        for(int i = 0; i < descendants.size();i++)
        {
            result += descendants.get(i)[1];
        }
        return result;
    }
    public int sumC(int count)
    {
        int result = 0;
        for(int i = 0; i < count;i++)
        {
            result += descendants.get(i)[1];
        }
        return result;
    }
    public int descendantsSize()
    {
        return descendants.size();
    }
    public int getDesId(int pos)
    {
        return descendants.get(pos)[0];
    }

    public ArrayList<int[]> getDescendants() {
        return descendants;
    }
    public Message attestateMes(boolean flag, int sa)
    {
        if(flag)
            return new Message(id,0,getEncryptA(id,C,physicalPhenomenon,sa,true),null,false,this.idParants);
        else
            return new Message(id,0,getEncryptA(id,C,agr,sa,true),attestateMac,false,idParants);
    }
    private byte[] getEncryptA(int id, int ca, int r,int ss,boolean bs)
    {
        byte[] c = ByteBuffer.allocate(4).putInt(id).array();
        byte[] pp = ByteBuffer.allocate(4).putInt(ca).array();
        byte[] sq = ByteBuffer.allocate(4).putInt(r).array();
        byte[] sa = ByteBuffer.allocate(4).putInt(ss).array();
        byte[] mes = ArrayUtils.addAll(c, pp);
        byte[] encrypt = null;
        mes = ArrayUtils.addAll(mes, sq);
        mes = ArrayUtils.addAll(mes, sa);
        try {
            encrypt = generateEncryptMessage(mes,bs);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        return encrypt;
    }
    private byte[] getEncryptAmac(int id, int ca, int r,int ss,byte[] mac,boolean bs)
    {
        byte[] c = ByteBuffer.allocate(4).putInt(id).array();
        byte[] pp = ByteBuffer.allocate(4).putInt(ca).array();
        byte[] sq = ByteBuffer.allocate(4).putInt(r).array();
        byte[] sa = ByteBuffer.allocate(4).putInt(ss).array();
        byte[] mes = ArrayUtils.addAll(c, pp);
        byte[] encrypt = null;
        mes = ArrayUtils.addAll(mes, sq);
        mes = ArrayUtils.addAll(mes, sa);
        mes = ArrayUtils.addAll(mes, mac);
        try {
            encrypt = generateEncryptMessage(mes,bs);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        return encrypt;
    }
}
