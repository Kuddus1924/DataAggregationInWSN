package developedSystem;

import java.math.BigInteger;

public class Message {
     public byte[] activeNodes;
     public BigInteger message;
     public int id;
     public int activeMembers;
     public int recipient;
     private int idgr;
     private byte[] Mac;

    public Message(int id,byte[]active,BigInteger mes,int recipient, byte[] mac)
    {
        this.id = id;
        activeNodes = active;
        message = mes;
        this.recipient = recipient;
        Mac = mac;
    }

    public byte[] getMac() {
        return Mac;
    }

    public byte[] getActiveNodes() {
        return activeNodes;
    }

    public BigInteger getMessage() {
        return message;
    }

    public void setIdgr(int idgr) {
        this.idgr = idgr;
    }
    public int getId()
    {
        return id;
    }

    public int getIdgr() {
        return idgr;
    }
}
