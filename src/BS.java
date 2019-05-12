import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class BS {
private ArrayList<Message> messageStore = new ArrayList<>();
private HashMap<Integer,SecretKey> keyStoreMAC = new HashMap<>();
private  HashMap<Integer,SecretKey> keyStoreEnc = new HashMap<>();
private int sq;
private Random random = new Random();
    public void BS(){
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
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(message);
    }
    public int[] decryptMessage(Message tmp) {
        byte[] decrypt = null;
        int[] result
        try {
            decrypt = decryptMessage(tmp.encrypt,tmp.getGroupLeaderId());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        int[] dec = FuncConst.split(decrypt);
        return dec;
    }

    public  int generateSq() {
        sq = random.nextInt();
        return sq;
    }



}
