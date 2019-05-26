package general.function;

import java.util.ArrayList;
import java.util.Random;

public class ValueModeling {
    private static Random random = new Random();
    public static int getValue(boolean flag)
    {
        if(flag)
        {
            double val = random.nextGaussian () * 2 + 40;
            return (int) Math.round (val);
        }
       else {
            double val = random.nextGaussian() * 2 + 14;
            return (int) Math.round(val);
        }

    }
}
