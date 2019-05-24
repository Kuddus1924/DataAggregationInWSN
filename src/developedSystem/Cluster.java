package developedSystem;
import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Random;

public class Cluster {
    private ArrayList<Node> nodes = new ArrayList<>();
    private int idLeader;
    private int seed;
    private Random random = new Random();
    private int id;
    private SecretKey key;
    private ArrayList<Integer> listAggregation = new ArrayList<>();
    public void setNode(Node node)
    {
        nodes.add(node);
    }
    public Cluster(int id)
    {
        this.id = id;
        for(int i = 0; i < 5; i++)
        {
            listAggregation.add(0);
        }
    }
    public void setKey(int n, SecretKey k)
    {
        for(int i = 0; i < nodes.size();i++)
        {
            nodes.get(i).setKey(n,key);
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
    public void createNodes(int start,int end,int key,SecretKey k)
    {
        int count = 1;
        for (int i = start; i <= end;i++)
        {
            nodes.add(new Node(i,count,id));
            count++;
        }
        this.setKey(key,k);
    }
    public  int  selectionAggregation()
    {
          for(int i = 0;i < nodes.size();i++)
          {
                if(checklist(i))
                {
                    if(nodes.get(i).generateNumber() < (1 - 0.5*(listAggregation.size()% 20)))
                    {
                        return  i;
                    }
                }
          }
          return random.nextInt(nodes.size());
    }
    public boolean checklist(int i)
    {
        for (int z = listAggregation.size() - 1;z >=  listAggregation.size() - 6; z--)
        {
            if(i == listAggregation.get(z))
            {
                return false;
            }
        }
        return true;
    }









}
