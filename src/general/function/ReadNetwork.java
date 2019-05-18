package general.function;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

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
    public static HashMap<Integer,Double> readTable(String filename)
    {
        HashMap<Integer,Double> result = new HashMap<>();
        BufferedReader stream;
        String str;
        String mas[];
        try {
            stream = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
            while ((str = stream.readLine()) != null)
            {
                mas = str.split(" ");
                result.put(Integer.getInteger(mas[0]),Double.valueOf(mas[1]));
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return result;
    }


}
