package SDAP;

import general.function.FuncConst;
import general.function.ReadNetwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class WSN {
    ArrayList<ArrayList<Node>> levels = new ArrayList<>();
    BS bs;

    public void workinWSN(int quantityRequests) {
        ArrayList<Double> m = new ArrayList<>();
        ArrayList<Double> ddd = new ArrayList<>();
        ArrayList<ArrayList<Integer>> connections = ReadNetwork.read("WSN.txt");
        bs = new BS();
        int count = 1;
        int cnt = 1;
        ArrayList<Node> tmp = new ArrayList<>();
        for (int i = 0; i < connections.size(); i++) {
            for (int j = 0; j < connections.get(i).size(); j++) {
                Node node = new Node(count, i);
                node.setKeyBS(bs.getkeyEnc(count), bs.getKeymMAC(count));
                tmp.add(node);
                count++;
            }
            if (i == cnt - 1) {
                cnt += tmp.size();
                levels.add(tmp);
                tmp = new ArrayList<>();
            }
        }
        levels.add(tmp);
        for (int i = 0; i < levels.size() - 1; i++) {
            for (int j = 0; j < levels.get(i).size(); j++) {
                for (int k = 0; k < levels.get(i + 1).size(); k++) {
                    if (levels.get(i + 1).get(k).getIdParants() == levels.get(i).get(j).getId()) {
                        levels.get(i).get(j).setAgregator(true);
                        levels.get(i + 1).get(k).setKeyPair(levels.get(i).get(j).getKeyPair(levels.get(i + 1).get(k).getId()));
                    }
                }
            }
        }
        ArrayList<ArrayList<Message>> listMessage = new ArrayList<>();
        for (int i = 0; i < quantityRequests; i++) {
            ArrayList<Message> end = new ArrayList<>();
            int sq = bs.generateSq();
            for (int j = levels.size() - 1; j >= 0; j--) {
                end = new ArrayList<>();
                for (int k = 0; k < levels.get(j).size(); k++) {
                    if (levels.get(j).get(k).isNotEndNode()) {
                        ArrayList<Message> messageArrayList = listMessage.get(listMessage.size() - 1);
                        for (int z = 0; z < messageArrayList.size(); z++) {
                            Message tmpMes = messageArrayList.get(z);
                            if (tmpMes.getRecipient() == levels.get(j).get(k).getId()) {
                                levels.get(j).get(k).setMessageStore(tmpMes);
                            }
                        }
                        Message mes = levels.get(j).get(k).getMessageParants(sq);
                        ArrayList<Message> forwarding = levels.get(j).get(k).getForwardingMessage();
                        if (forwarding.size() != 0) {
                            for (int y = 0; y < forwarding.size(); y++) {
                                end.add(forwarding.get(y));
                            }
                        }
                        end.add(mes);
                    } else {
                        end.add(levels.get(j).get(k).getMessageParants(sq));
                    }
                }
                listMessage.add(end);
            }
            for (int j = 0; j < listMessage.get(listMessage.size() - 1).size(); j++) {
                bs.setMessage(listMessage.get(listMessage.size() - 1).get(j));
            }
            bs.setIv(getNode(1).getIv());
            ArrayList<int[]> decryptMesBS = bs.getAllMessage();
            ArrayList<int[]> decryptMesBScopy = new ArrayList<>(decryptMesBS);
            ArrayList<Integer> attestatelist = bs.grubbsTest(decryptMesBScopy);
            int sa = bs.getSa();
            for (int j = 0; j < attestatelist.size(); j++) {
                ArrayList<Integer> result = new ArrayList<>();
                int x = attestatelist.get(j);
                Node node = getNode(x);
                while (true) {
                    int d = node.descendantsSize();
                    if (d == 0) {
                        break;
                    } else {
                        double h = FuncConst.FunctionH(sa, node.getId());
                        int sum = node.sumC();
                        double sigma = h * sum;
                        for (int z = 0; z < d; z++) {
                            if (node.sumC(z - 1) < sigma && sigma < node.sumC(z)) {
                                result.add(node.getDesId(z));
                                x = node.getDesId(z);
                                break;
                            }
                        }

                    }
                }
                ArrayList<Message> attestateMessage = new ArrayList<>();
                ArrayList<Integer> ag = new ArrayList<>();
                attestateMessage.add(getNode(attestatelist.get(j)).attestateMes(true, sa));
                ArrayList<int[]> ls = getNode(attestatelist.get(j)).getDescendants();
                for (int r = 0; r < result.size(); r++) {
                    boolean one = true;
                    ag.add(ls.size());
                    ArrayList<Integer> lev = new ArrayList<>();
                    for (int p = 0; p < ls.size(); p++) {
                        if (result.get(r) == ls.get(p)[0] && one) {
                            attestateMessage.add(getNode(result.get(r)).attestateMes(true, sa));
                        } else {
                            attestateMessage.add(getNode(ls.get(p)[0]).attestateMes(false, sa));
                        }
                        lev.add(ls.get(p)[0]);
                    }
                    ls = getNode(result.get(r)).getDescendants();
                }
                byte[] mac = null;
                int agr = 0;
                for(int g = 0;g < attestateMessage.size(); g++)
                {
                    agr += bs.decryptA(attestateMessage.get(g))[2];
                    mac = FuncConst.xorMac(mac,attestateMessage.get(g).mac);
                }
                agr = agr/attestateMessage.size();
                for(int f = 0;f < decryptMesBS.size();f++) {
                    if (decryptMesBS.get(f)[0] == attestatelist.get(i)) {
                        if (!(decryptMesBS.get(f)[2] == agr && Arrays.equals(bs.getMac(f), mac))) {
                            decryptMesBS.remove(f);
                        }
                        break;
                    }
                }
            }
            ArrayList<Integer> listgg = new ArrayList<>();
            for(int p = 0; p < decryptMesBS.size();p++)
            {
                listgg.add(decryptMesBS.get(p)[2]);
            }
            m.add(getM(listgg));
            ddd.add(getD(listgg,getM(listgg)));
            System.out.println("aggregation result: "  + sum(decryptMesBS));
            clean();
        }
        System.out.println("M=" + crednee(m));
        System.out.println("D=" + crednee(ddd));

    }
    public Node getNode(int id)
    {
        for(int i = 0;i < levels.size();i++)
        {
            for (int j = 0; j < levels.get(i).size();j++)
            {
                if(levels.get(i).get(j).getId() == id)
                    return levels.get(i).get(j);
            }
        }
        return null;
    }
    public double crednee(ArrayList<Double> list)
    {
        double gavno = 0;
        for(int i = 0;i < list.size();i++)
        {
            gavno += list.get(i);
        }
        return gavno/list.size();
    }
    public double sum(ArrayList<int[]> list)
    {
        double sum = 0;
        for(int i = 0; i < list.size();i++)
        {
            sum += list.get(i)[2];
        }
        sum /= list.size();
        return sum;
    }
    public void clean()
    {
        bs.clean();
        for(int j = 0; j < levels.size(); j++ )
        {
            for(int z = 0; z < levels.get(j).size(); z++ )
            {
                levels.get(j).get(z).clean();
            }
        }
    }
    public static void main(String[] arg)
    {
        WSN wsn = new WSN();
        wsn.workinWSN(100);
    }
    public double getM(ArrayList<Integer> ls)
    {
        ArrayList<Integer> count= new ArrayList<>();
        HashSet<Integer> res = new HashSet<>(ls);
        ArrayList<Integer> result = new ArrayList<>(res);
        int c = 0;
        for(int i = 0;i < result.size();i++)
        {
            c = 0;
            for(int j = 0; j< ls.size(); j++)
            {
                if(result.get(i) == ls.get(j))
                    c++;
            }
            count.add(c);
        }
        double M = 0;
        for(int i = 0; i < result.size();i++)
        {
            double y = count.get(i);
            double jz =ls.size();
            double neo =  result.get(i);
            M += (y/jz) * neo;
        }
        return M;
    }
    public double getD(ArrayList<Integer> ls, Double M)
    {
        ArrayList<Integer> count= new ArrayList<>();
        HashSet<Integer> res = new HashSet<>(ls);
        ArrayList<Integer> result = new ArrayList<>(res);
        int c = 0;
        for(int i = 0;i < result.size();i++)
        {
            c = 0;
            for(int j = 0; j< ls.size(); j++)
            {
                if(result.get(i) == ls.get(j))
                    c++;
            }
            count.add(c);
        }
        double D = 0;
        for(int i = 0; i < result.size();i++)
        {
            double y = count.get(i);
            double jz =ls.size();
            D += (y/jz) * Math.pow((result.get(i) - M),2);
        }
        return D;
    }


}
