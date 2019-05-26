package developedSystem;

import java.util.ArrayList;
import java.util.HashMap;

public class WSN {
    public static void working(int timeWork, int sizeGroup)
    {
        BasicStation bs = new BasicStation();
        int N = bs.getKeyGroup();
        ArrayList<Cluster> wsn = new ArrayList<>();
        for(int i = 0; i < sizeGroup; i++)
        {
            wsn.add(new Cluster(i));
            if(i == 2)
            {
                wsn.get(i).createNodes(i * 30,(i+1) * 30,N,bs.getMacKey(i),10);
            }
            else {
                wsn.get(i).createNodes(i * 30, (i + 1) * 30, N, bs.getMacKey(i), 0);
            }
        }
        for(int i = 0; i < timeWork;i++)
        {
            int sq = bs.getNumberSeq();
            if(i % 3 == 0) {
                for (int y = 0; y < sizeGroup; y++) {
                    bs.setMessage(wsn.get(y).startWorking(sq), y);
                }
            }
            else
            {
                for (int z = 0; z < sizeGroup; z++) {
                    bs.setMessage(wsn.get(z).continueWork(sq), z);
                }
            }
            System.out.println( i + "Aggregation");
            ArrayList<Integer> result = bs.result();
            if(result != null)
            {
                if(result.size() != 0) {
                    System.out.println(result.toString());
                    for (int z = 0; z < sizeGroup; z++) {
                        wsn.get(z).setBadlist(result);
                    }
                }
            }
        }
    }
    public static void main(String []arg)
    {
        WSN.working(10,10);
    }


}
