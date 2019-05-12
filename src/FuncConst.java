import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

public class FuncConst {
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
        byte[] c = Arrays.copyOfRange(mas, 0, 3);
        byte[] pp = Arrays.copyOfRange(mas, 4, 7);
        byte[] sq = Arrays.copyOfRange(mas, 8, 11);
        int[] result = new int[3];
        ByteBuffer wrapped = ByteBuffer.wrap(c);
        result[0] =wrapped.getInt();
        wrapped = ByteBuffer.wrap(pp);
        result[1] =wrapped.getInt();
        wrapped = ByteBuffer.wrap(sq);
        result[2] =wrapped.getInt();
        return result;
    }


}
