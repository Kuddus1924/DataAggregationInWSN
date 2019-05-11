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
    private ArrayList<Integer> descendants = new ArrayList<>();
    private SecretKey keyPair;
    private SecretKey keyBS;
    private boolean isNotEndNode;
    private String algo = "HMACMD5";
    private String secAlgo = "AES/CBC/NoPadding (128)";
    private int idParants;
    private KeyGenerator gen;
    private Message message;


    public Node(int id, int idP) {
        this.id = id;
        isNotEndNode = false;
        physicalPhenomenon = 0;
        seqNumber = 0;
        idParants = idP;
    }

    public void setMessage(Message mes) {
        messageStore.add(mes);
    }

    public void setKeyPair(SecretKey keyPair) {
        this.keyPair = keyPair;
    }

    public void setKeyBS(SecretKey keyBS) {
        this.keyBS = keyBS;
    }

    public void setSeqNumber(int seqNumber) {
        this.seqNumber = seqNumber;
    }

    public void setAgregator(boolean agregator) {
        this.isNotEndNode = agregator;
        try {
            gen = KeyGenerator.getInstance(algo);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public byte[] getMAC(byte[] message) {
        try {
            Mac mac = Mac.getInstance(algo);
            mac.init(keyBS);
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
            descendants.add(idNode);
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

    public byte[] generateEncryptMessage(byte[] message) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, keyPair);
        return cipher.doFinal(message);
    }

    public byte[] decryptMessage(byte[] message, int idDescendant) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        SecretKey key = keyStore.get(idDescendant);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(message);
    }

    public Message getMessage(int count) {
        if (!isNotEndNode) {
            byte[] flag = ByteBuffer.allocate(4).putInt(0).array();
            byte[] id = ByteBuffer.allocate(4).putInt(this.id).array();
            byte[] c = ByteBuffer.allocate(4).putInt(1).array();
            byte[] pp = ByteBuffer.allocate(4).putInt(physicalPhenomenon).array();
            byte[] sq = ByteBuffer.allocate(4).putInt(count).array();
            byte[] mes = ArrayUtils.addAll(c, pp);
            byte[] encrypt = null;
            mes = ArrayUtils.addAll(mes, sq);
            try {
                encrypt = generateEncryptMessage(mes);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }
            byte[] toMac = ArrayUtils.addAll(flag, c);
            toMac = ArrayUtils.addAll(toMac, id);
            toMac = ArrayUtils.addAll(toMac, pp);
            toMac = ArrayUtils.addAll(toMac, sq);
            byte[] mac = getMAC(toMac);
            Message message = new Message(this.id, 1, encrypt, mac, false, idParants);
            this.message = message;
            return message;
        } else
            return null;
    }

    public Message getMessageParants(int count,boolean flag) {
        if (isNotEndNode) {
            ArrayList<Integer> agregation = new ArrayList<>();
            ArrayList<byte[]> mac = new ArrayList<>();
            ArrayList<Integer> counts = new ArrayList<>();
            if (flag) {
                for (int i = 0; i < messageStore.size(); i++) {
                    Message tmp = messageStore.get(i);
                    if (tmp.flag == false) {
                        byte[] decrypt = null;
                        try {
                            decrypt = decryptMessage(tmp.encrypt, tmp.id);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        int[] dec = split(decrypt);
                        if (dec[2] == this.seqNumber) {
                            agregation.add(dec[1]);
                            mac.add(tmp.mac);
                            counts.add(dec[0]);
                        } else {
                            continue;
                        }
                    }
                }
                int c = sum(counts);
                if(FuncConst.FunctionH(this.seqNumber,this.id) < FuncConst.FunctionG(c)) {
                    Message message = new Message(id, )//если лидер
                }
                else
                {
                    Message message = new Message(id,c, )//если не лидер
                }
            }
        }


    }
    private int[] split(byte[] mas)
    {
        byte[] c = Arrays.copyOfRange(mas, 0, 3);
        byte[] pp = Arrays.copyOfRange(mas, 4, 7);
        byte[] sq = Arrays.copyOfRange(mas, 8, 11);
        int[] result = new int[3];
        ByteBuffer wrapped = ByteBuffer.wrap(c);
        result[0] =wrapped.getInt();
        wrapped = ByteBuffer.wrap(pp);
        result[1] =wrapped.getInt();
        wrapped = ByteBuffer.wrap(sq);
        result[2] =wrapped.getInt();
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
}
