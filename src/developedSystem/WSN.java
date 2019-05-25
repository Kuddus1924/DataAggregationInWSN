package developedSystem;

import java.util.ArrayList;
import java.util.HashMap;

public class WSN {
    public static void working()
    {
        BasicStation bs = new BasicStation();
        int N = bs.getKeyGroup();
        ArrayList<Cluster> wsn = new ArrayList<>();
        for(int i = 0; i < 4; i++)
        {
            wsn.add(new Cluster(i));
            wsn.get(i).createNodes(i * 30,(i+1) * 30,N,bs.getMacKey(i));
        }
        for(int i = 0; i < 30;i++)
        {
            int sq = bs.getNumberSeq();
            if(i%3 == 0) {
                for (int z = 0; z < 4; z++) {
                    bs.setMessage(wsn.get(z).startWorking(sq), z);
                }
            }
            else
            {
                for (int z = 0; z < 4; z++) {
                    bs.setMessage(wsn.get(z).continueWork(sq), z);
                }
            }
            System.out.println( i + "Aggregation");
            ArrayList<Integer> result = bs.result();
            if(result != null)
            {
                System.out.println( result.toString());
                for (int z = 0; z < 1; z++) {
                    wsn.get(z).setBadlist(result);
                }
            }
        }
    }
    public static void main(String []arg)
    {
        WSN.working();
    }


}
