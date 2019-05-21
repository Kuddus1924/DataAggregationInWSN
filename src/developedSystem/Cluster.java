package developedSystem;
import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Random;

public class Cluster {
    private ArrayList<Node> nodes = new ArrayList<>();
    private int idLeader;
    private int seed;
    private Random random;
    private int id;
    private SecretKey key;
    public void setNode(Node node)
    {
        nodes.add(node);
    }
    public Cluster(int id)
    {
        this.id = id;
    }
    public void setKey(ArrayList<Integer> key)
    {
        for(int i = 0; i < key.size();i++)
        {
            nodes.get(i).setKey(key.get(i));
        }
    }
    public void setNumberSeq(int numberSeq)
    {
        seed = numberSeq;
        for(int i = 0;i < nodes.size(); i++)
        {
            nodes.get(i).setNumberReq(numberSeq);
        }
    }
    public Message getMessageGroup()
    {
        idLeader = new Random(seed).nextInt(nodes.size());
        for(int i = 0;i < nodes.size(); i++)
        {
            if(i != idLeader)
            {
                nodes.get(idLeader).setMessages(nodes.get(i).sendMessage());
            }
        }
        return nodes.get(idLeader).sendMessage();
    }
    public void createNodes(int start,int end,ArrayList<Integer> key)
    {
        for (int i = start; i <= end;i++)
        {
            nodes.add(new Node(i,end - start,id));
        }
        this.setKey(key);
    }








}
