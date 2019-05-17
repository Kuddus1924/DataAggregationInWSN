import java.io.*;
import java.util.ArrayList;

public class ReadNetwork {
    public static ArrayList<ArrayList<Integer>> read(String nameFile)
    {
        String str;
        BufferedReader stream;
        ArrayList<ArrayList<Integer>> list = new ArrayList<>();
        ArrayList<Integer> tmp = new ArrayList<>();
        String mas[];
        try {
            stream = new BufferedReader(new InputStreamReader(new FileInputStream(nameFile)));
            while ((str = stream.readLine()) != null)
            {
                    tmp = new ArrayList<>();
                    mas = str.split(" ");
                    if(Integer.parseInt(mas[0]) == -1)
                    {
                        continue;
                    }
                    for(int i = 0; i < mas.length; i++)
                    {
                        tmp.add(Integer.parseInt(mas[i]));
                    }
                    list.add(tmp);
            }
        }
        catch (IOException e)
        {
            return  null;
        }
        return list;
    }

}
