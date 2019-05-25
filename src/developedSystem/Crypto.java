package developedSystem;

import java.util.ArrayList;
import java.util.Random;

public class Crypto {
    public static int getDeduction(int N)
    {
        ArrayList<Integer> z = new ArrayList<>();
        for (int i = 1;i < 100; i++)
        {
            if(gcd(N,i) == 1)
                z.add(i);
        }
        Random random = new Random();
        //return z.get(random.nextInt(z.size()));
        return 17;

    }
    private static int gcd(int a, int b)
    {
        if(b == 0)
            return Math.abs(a);
        return gcd(b,a % b);
    }

}
