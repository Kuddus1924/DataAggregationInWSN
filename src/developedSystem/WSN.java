package developedSystem;

import general.function.Schedule;

import java.util.ArrayList;
import java.util.HashMap;

public class WSN {
    public static  double working(int timeWork, int sizeGroup,double probality,int size)
    {

        BasicStation bs = new BasicStation();
        double statistic = 0;
        int N = bs.getKeyGroup();
        int bdnode = (int)(30 * probality);
        ArrayList<Cluster> wsn = new ArrayList<>();
        for(int i = 0; i < sizeGroup; i++)
        {
            wsn.add(new Cluster(i));
            if (i <  size)
                wsn.get(i).createNodes(i * 30, (i + 1) * 30, N, bs.getMacKey(i), bdnode);
            else
                wsn.get(i).createNodes(i * 30, (i + 1) * 30, N, bs.getMacKey(i), 0);
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
        return (bs.getBadlistSize()/ (double)(timeWork*size));
    }
    public static void main(String []arg)
    {

            ArrayList<Double> result = new ArrayList<>();
            /*result.add(6.86);
            result.add(6.88);
            result.add(6.8);
            result.add(6.768);
            result.add(6.8);
            result.add(6.75);
            result.add(6.8);
            result.add(6.82);*/
            for (int i = 0; i < 8; i++) {
                System.out.println(0.1 + (0.05 * i));
                result.add(WSN.working(8, 10, (double) 0.1 + (0.05 * i), 3));
            }


           //System.out.println(result);
           //1Schedule.print(result,Integer.toString(2 * 10) + "%");
    }


}
