package developedSystem;

import java.util.ArrayList;
import java.util.Random;

public class ValueModeling {
    private static Random random = new Random();
    public static int getValue()
    {
        int meet = 67;
        return meet + (int)(random.nextGaussian() * 4);
    }
}
