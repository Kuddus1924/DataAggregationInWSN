package general.function;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import static general.function.ReadNetwork.readTable;

public  class FuncConst {
    public  HashMap<Integer,Double> tableGrubs = readTable("C:\\Users\\kuddu\\Desktop\\Диплом\\grubbs.txt");
    public static double FunctionG(int c)
    {
        return Math.pow((1.0 - Math.exp(-1*0.3*c)),5);
    }
    public static double FunctionH(int sq,int id)
    {
        Random random = new Random(sq+id);
        return random.nextDouble();
    }
    public static int[] split(byte[] mas)
    {
        byte[] c = Arrays.copyOfRange(mas, 0, 4);
        byte[] pp = Arrays.copyOfRange(mas, 4, 8);
        byte[] sq = Arrays.copyOfRange(mas, 8, 12);
        int[] result = new int[3];
        ByteBuffer wrapped = ByteBuffer.wrap(c);
        result[0] =wrapped.getInt();
        wrapped = ByteBuffer.wrap(pp);
        result[1] =wrapped.getInt();
        wrapped = ByteBuffer.wrap(sq);
        result[2] =wrapped.getInt();
        return result;
    }
    public static int[] split4(byte[] mas)
    {
        byte[] c = Arrays.copyOfRange(mas, 0, 3);
        byte[] pp = Arrays.copyOfRange(mas, 4, 7);
        byte[] sq = Arrays.copyOfRange(mas, 8, 11);
        byte[] sa = Arrays.copyOfRange(mas, 11, 15);
        int[] result = new int[3];
        ByteBuffer wrapped = ByteBuffer.wrap(c);
        result[0] =wrapped.getInt();
        wrapped = ByteBuffer.wrap(pp);
        result[1] =wrapped.getInt();
        wrapped = ByteBuffer.wrap(sq);
        result[2] =wrapped.getInt();
        wrapped = ByteBuffer.wrap(sa);
        result[3] =wrapped.getInt();
        return result;
    }
    public static byte[] xorMac(byte[] tmp,byte[] tmp2)
    {
        if(tmp == null)
        {
            return tmp2;
        }
        byte[] xor = new byte[tmp.length];
        for(int j = 0;j < tmp.length; j++)
        {
            xor[j] = (byte)(tmp[j]^tmp2[j]);
        }
        return xor;
    }
    public static double getGrabbsCriterion(int n)
    {
        HashMap<Integer,Double> tableGrubs = readTable("C:\\Users\\kuddu\\Desktop\\Диплом\\grubbs.txt");
        return tableGrubs.get(n);
    }



}
