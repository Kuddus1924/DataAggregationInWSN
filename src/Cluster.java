import java.util.ArrayList;
import java.util.Random;

public class Cluster {
    private ArrayList<Node> nodes = new ArrayList<>();
    private int idLeader;
    private int seed;
    private Random random;
    public void setNode(Node node)
    {
        nodes.add(node);
    }
    public Cluster(int seed)
    {
        this.seed = seed;
        random = new Random(seed);
    }
    public void getNewID()
    {
        idLeader = random.nextInt(nodes.size());
    }




}
