import java.math.BigInteger;
import java.util.ArrayList;

public class RandomGenerator{

    private BigInteger seed;
    private BigInteger random_ith_generated;
    private ArrayList<BigInteger> random_sequence;
    private ArrayList<BigInteger> random_combined_sequence;
    private boolean combined_sequence = false;

    public RandomGenerator(int seed){
        this.seed = BigInteger.valueOf(seed);
        this.random_ith_generated = BigInteger.valueOf(seed);
        this.random_sequence = new ArrayList<BigInteger>();
        this.random_combined_sequence = new ArrayList<BigInteger>();
    }
    public RandomGenerator(){ this(0);}

    public void reset(){
        this.random_ith_generated = this.seed;
        this.random_sequence = new ArrayList<BigInteger>();
        this.random_combined_sequence = new ArrayList<BigInteger>();
    }

    public void setSeed(int seed){ 
        this.seed = BigInteger.valueOf(seed);
        this.reset();
    }

    public BigInteger getRandomGenerated(){
        return this.random_ith_generated;
    }

    public ArrayList<BigInteger> getRandomSequenceGenerated(){
        return this.random_sequence;
    }

    public ArrayList<BigInteger> getRandomCombinedSequenceGenerated(){
        return this.random_combined_sequence;
    }

    private void processingRandoms(RandomEngine re, int initial_seed, int sequence_length, boolean get_sequence){
        this.setSeed(initial_seed);
        for(int i = 0; i < sequence_length ; i++){
            generateRandomNumber(re, this.random_ith_generated);
            if(get_sequence)
                random_sequence.add(this.random_ith_generated);
            else if(combined_sequence)
                random_combined_sequence.add(this.random_ith_generated);
        }
    }

    public ArrayList<BigInteger> getRandomSequence(RandomEngine re, int initial_seed,
                                                   int sequence_length){
        this.processingRandoms(re, initial_seed,sequence_length, true);
        return random_sequence;
    }

    public BigInteger getIthRandomNumber(RandomEngine re, int initial_seed, int ith_number){
        this.processingRandoms(re, initial_seed, ith_number, false);
        return this.random_ith_generated;
    }

    public BigInteger generateRandomNumber(RandomEngine re, BigInteger seed){
        this.random_ith_generated = re.generateRandom(seed);
        return this.random_ith_generated; 
    }

    public BigInteger getIthRandomNumberCombined(RandomCombinedEngine v, RandomEngine w, RandomEngine y,
                                                 RandomEngine x, int seed_w, int seed_y , int seed_x, int ith_number){
        return v.generateCombinedRandom(getIthRandomNumber(w, seed_w, ith_number),
        getIthRandomNumber(y, seed_y, ith_number),
        getIthRandomNumber(x, seed_x, ith_number));
    }

    public ArrayList<BigInteger> getRandomSequenceCombined(RandomCombinedEngine v, RandomEngine w, RandomEngine y,
                                                           RandomEngine x, int seed_w, int seed_y , int seed_x, int ith_number){

        combined_sequence = true;
        for (int i = 0; i < ith_number; i++) {
            random_combined_sequence.add(
            getIthRandomNumberCombined(v, w, y, x, seed_w, seed_y, seed_x, i));       
        }
        combined_sequence = false;

        return random_combined_sequence;
    }

    
}