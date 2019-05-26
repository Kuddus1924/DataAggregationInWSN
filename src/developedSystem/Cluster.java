package developedSystem;
import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Cluster {
    private ArrayList<Node> nodes = new ArrayList<>();
    private int idLeader = 0;
    private int seed;
    private Random random = new Random();
    private int id;
    private SecretKey key;
    private HashSet<Integer> badlist = new HashSet<>();
    private ArrayList<Integer> listAggregation = new ArrayList<>();
    public void setNode(Node node)
    {
        nodes.add(node);
    }
    public Cluster(int id)
    {
        this.id = id;
        for(int i = 0; i < 21; i++)
        {
            listAggregation.add(0);
        }
    }
    public void setBadlist(ArrayList<Integer> badList)
    {
        if(badList.size() == 0)
            return;
        for(int i = 0;i < badList.size();i++) {
            this.badlist.add(badList.get(i));
        }
    }
    public void setKey(int n, SecretKey k)
    {
        key = k;
        for(int i = 0; i < nodes.size();i++)
        {
            nodes.get(i).setKey(n,k);
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
        for(int i = 0;i < nodes.size(); i++)
        {
            if(i != idLeader)
            {
                nodes.get(idLeader).setMessages(nodes.get(i).sendMessage());
            }
        }
        return nodes.get(idLeader).sendMessage();
    }
    public void createNodes(int start,int end,int key,SecretKey k,int badnode)
    {
        int count = 0;
        for (int i = start + 1; i <= end;i++)
        {
            if(count < badnode) {
                nodes.add(new Node(i, count, id, true));
            }
            else {
                nodes.add(new Node(i, count, id, false));
            }
            count++;
        }
        this.setKey(key,k);
    }
    public  int  selectionAggregation()
    {
            while (true) {
                for (int i = 0; i < nodes.size(); i++) {
                    if (checklist(i)) {
                        if (nodes.get(i).generateNumber() < (0.05) / (1 - 0.05 * (listAggregation.size() % 20))) //поменять формулу
                        {
                            idLeader = i;
                            System.out.println("id group - " + id +" " + "idLeader -" + nodes.get(i).getId());
                            listAggregation.add(i);
                            return i;
                        }
                    }
                }
            }
    }
    public boolean checklist(int i)
    {
        for (int z = listAggregation.size() - 1;z >=  listAggregation.size() - 20; z--)
        {
            if(i == listAggregation.get(z))
            {
                return false;
            }
        }
        for(int d = 0; d < badlist.size(); d++)
        {
            if(badlist.contains(nodes.get(i).getId()))
            {
                return false;
            }
        }
        return true;
    }
    public Message startWorking(Integer sq)
    {
        end();
        setNumberSeq(sq);
        int select = selectionAggregation();
        for(int i = 0; i < nodes.size(); i++)
        {
            nodes.get(i).setAggregator(select);
        }
        return getMessageGroup();
    }
    public Message continueWork(Integer sq)
    {
        end();
        setNumberSeq(sq);
        return getMessageGroup();
    }
    private void end()
    {
        nodes.get(idLeader).clean();
    }











}
