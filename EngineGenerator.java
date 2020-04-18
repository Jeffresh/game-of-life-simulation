import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@FunctionalInterface
interface RandomEngine {
    BigInteger generateRandom(BigInteger seed);
}

@FunctionalInterface
interface RandomCombinedEngine{
    BigInteger generateCombinedRandom(BigInteger w, BigInteger y, BigInteger x);
}

public class EngineGenerator {

    public Map<String, RandomEngine> engines;
    public Map<String, RandomCombinedEngine> combined_engines;

    public static BigInteger two_pow_31_minus_one = BigInteger.valueOf(2147483647);

    public EngineGenerator() {
        this.engines = new HashMap<String, RandomEngine>();
        this.combined_engines = new HashMap<String, RandomCombinedEngine>();
    }

    public void createEngines() {

        RandomEngine generator261a = (a) -> a.multiply(BigInteger.valueOf(5))
                .mod(BigInteger.valueOf(2).pow(5));
        this.engines.put("generator261a", generator261a);

        RandomEngine generator261b = (a) -> a.multiply(BigInteger.valueOf(7))
                .mod(BigInteger.valueOf(2).pow(5));
        this.engines.put("generator261b", generator261b);

        RandomEngine generator262 = (a) -> a.multiply(BigInteger.valueOf(3))
                .mod(BigInteger.valueOf(31));
        this.engines.put("generator262", generator262);

        RandomEngine generator263 = (a) -> a.multiply(BigInteger.valueOf(7).pow(5))
                .mod(BigInteger.valueOf(2147483647));
        this.engines.put("generator263", generator263);

        RandomEngine generatorFishmanAndMoore1 = (a) -> a.multiply(BigInteger.valueOf(48271)).mod(two_pow_31_minus_one);
        this.engines.put("generatorFishmanAndMore1", generatorFishmanAndMoore1);

        RandomEngine generatorFishmanAndMoore2 = (a) -> a.multiply(BigInteger.valueOf(69621)).mod(two_pow_31_minus_one);
        this.engines.put("generatorFishmanAndMore2", generatorFishmanAndMoore2);

        RandomEngine generatorRandu = (a) -> a.multiply(BigInteger.valueOf(65539)).mod(BigInteger.valueOf(2147483647).
                add(BigInteger.valueOf(1)));
        this.engines.put("generatorRandu", generatorRandu);
        RandomEngine generatorCombinedw = (a) -> a.multiply(BigInteger.valueOf(157)).mod(BigInteger.valueOf(32363));
        this.engines.put("generatorCombinedW", generatorCombinedw);

        RandomEngine generatorCombinedy = (a) -> a.multiply(BigInteger.valueOf(142)).mod(BigInteger.valueOf(31657));
        this.engines.put("generatorCombinedY", generatorCombinedy);

        RandomEngine generatorCombinedx = (a) -> a.multiply(BigInteger.valueOf(146)).mod(BigInteger.valueOf(31727));
        this.engines.put("generatorCombinedX", generatorCombinedx);

        RandomCombinedEngine generatorCombinedWXY = (w, x, y) -> (w.subtract(x).add(y)).mod(BigInteger.valueOf(32362));
        this.combined_engines.put("generatorCombinedWXY", generatorCombinedWXY);
    }
    
}
