package developedSystem;

public class Message {
     public byte[] activeNodes;
     public byte[] message;
     public int id;
     public int activeMembers;
     public int recipient;


    public Message(int id,byte[]active,byte[]mes,int recipient)
    {
        this.id = id;
        activeNodes = active;
        message = mes;
        this.recipient = recipient;
    }

    public byte[] getActiveNodes() {
        return activeNodes;
    }

    public byte[] getMessage() {
        return message;
    }
}
