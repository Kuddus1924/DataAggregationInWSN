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


}
