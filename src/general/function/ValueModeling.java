package general.function;

import java.util.ArrayList;
import java.util.Random;

public class ValueModeling {
    private static Random random = new Random();
    private static int count = 3;
    public static int getValue(boolean flag)
    {
        if(flag)
        {
            double val = Math.random() * 22;
            while (val < 9)
            {
                val = Math.random() * 12;
            }
            return (int) val;
        }
       else {
            double val = random.nextGaussian() * 2 + 7;
            return (int) Math.round(val);
        }

    }
}
