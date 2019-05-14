import javax.crypto.SecretKey;
import java.util.ArrayList;

public class WSN {
    ArrayList<ArrayList<Node>> levels = new ArrayList<>();

    public void workinWSN(int quantityRequests)
    {
        ArrayList<ArrayList<Integer>> connections = ReadNetwork.read("WSN.txt");
        BS bs = new BS();
        int count = 1;
        int cnt = 1;
        ArrayList<Node>tmp = new ArrayList<>();
        for(int i = 0; i < connections.size();i ++)
        {
            for(int j = 0;j < connections.get(i).size();j++)
            {
                Node node = new  Node(count,i);
                node.setKeyBS(bs.getkeyEnc(count),bs.getKeymMAC(count));
                tmp.add(node);
                count++;
            }
            if(i == cnt - 1) {
                cnt += tmp.size();
                levels.add(tmp);
                tmp = new ArrayList<>();
            }
        }
        for(int i = 0;i < levels.size() - 1;i++) {
            for (int j = 0; j < levels.get(i).size(); j++)
            {
                for(int k = 0; k < levels.get(i + 1).size(); k++)
                {
                    if(levels.get(i + 1).get(k).getIdParants() == levels.get(i).get(j).getId())
                    {
                        levels.get(i).get(j).setAgregator(true);
                        levels.get(i + 1).get(k).setKeyPair(levels.get(i).get(j).getKeyPair(levels.get(i + 1).get(k).getId()));
                    }
                }
            }
        }
        ArrayList<ArrayList<Message>> listMessage = new ArrayList<>();
        for (int i = 0; i < quantityRequests;i++)
        {
            ArrayList<Message> end = new ArrayList<>();
            int sq = bs.generateSq();
            /*for(int j = 0;j < levels.get(levels.size()-1).size();j++)
            {
                end.add(levels.get(levels.size()-1).get(j).getMessageParants(sq));
            }
            listMessage.add(end);*/
            for(int j = levels.size() - 1; j >= 0;j--)
            {
                end = new ArrayList<>();
                for(int k = 0;k < levels.get(j).size();k++)
                {
                    if(levels.get(j).get(k).isNotEndNode())
                    {
                        ArrayList<Message> messageArrayList = listMessage.get(listMessage.size() - 1);
                        for(int z = 0; z < messageArrayList.size(); z++)
                        {
                            Message tmpMes = messageArrayList.get(z);
                            if(tmpMes.getRecipient() == levels.get(j).get(k).getId())
                            {
                                levels.get(j).get(k).setMessageStore(tmpMes);
                            }
                        }
                        Message mes = levels.get(j).get(k).getMessageParants(sq);
                        ArrayList<Message> forwarding = levels.get(j).get(k).getForwardingMessage();
                        if(forwarding.size() != 0)
                        {
                            for(int y = 0; y < forwarding.size(); y++)
                            {
                                end.add(forwarding.get(y));
                            }
                        }
                        end.add(mes);
                    }
                    else{
                        end.add(levels.get(j).get(k).getMessageParants(sq));
                    }
                }
                listMessage.add(end);
            }

        }

    }


}
