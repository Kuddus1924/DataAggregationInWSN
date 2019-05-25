package developedSystem;

import java.util.ArrayList;
import java.util.Random;

public class ValueModeling {
    private static Random random = new Random();
    public static int getValue()
    {
       double val = random.nextGaussian () * 2 + 14;
        return (int) Math.round (val);

    }
}
