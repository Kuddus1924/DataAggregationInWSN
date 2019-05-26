package general.function;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

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
        String mas[] = new String[2];
        try {
            stream = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
            while ((str = stream.readLine()) != null)
            {
                mas = str.split(" ");
                int z = Integer.valueOf(mas[0]);
                double z1 = Double.valueOf(mas[1]);
                result.put(z,z1);
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return result;
    }
}
