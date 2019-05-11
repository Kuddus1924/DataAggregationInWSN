public class Message {
    public int id;
    public int c;
    public byte[] encrypt;
    public byte[] mac;
    public boolean flag;
    public int recipient;

    public Message(int id,int c ,byte[] encrypt, byte[] mac,boolean flag, int idRecipient)
    {
        this.id = id;
        this.c = c;
        this.encrypt = encrypt;
        this.mac = mac;
        this.flag = flag;
        this.recipient = idRecipient;
    }

    public byte[] getEncrypt() {
        return encrypt;
    }

    public byte[] getMac() {
        return mac;
    }

    public int getC() {
        return c;
    }

    public int getId() {
        return id;
    }
}