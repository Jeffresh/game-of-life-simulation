import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
     * ClassNV.java
     * Purpose: generic Class that you can modify and adapt easily for any application
     * that need data visualization.
     * @author: Jeffrey Pallarés Núñez.
     * @version: 1.0 23/07/19
     */



public class CellularAutomata2D implements Runnable
{

    private static int[][] matrix;
    private static  int[][] actualGen, nextGen;
    public static AtomicIntegerArray population_counter;
    private int [] local_population_counter;
    private static LinkedList<Double>[] population;
    public static MainCanvas canvasTemplateRef;
    public static AnalyticsMultiChart population_chart_ref;

    public int[][] getData() { return matrix; }
    public void plug(MainCanvas ref) { canvasTemplateRef = ref; }
    public void plugPopulationChart(AnalyticsMultiChart ref) { population_chart_ref = ref;}

    private static int width, height;

    public static int states_number = 2;
    private static int cfrontier = 0;
    private static int seed;
    private static int cells_number;
    public static int generations;
    private static String initializerMode;
    private static Random randomGenerator;

    private int task_number;
    private static int total_tasks;
    private static CyclicBarrier barrier = null;
    private int in;
    private int fn;
    public static Boolean abort = false;
    private static int gens;
    private static int size_pool;
    private static ThreadPoolExecutor myPool;


    public void run() {

        for (int i = 0; i < generations-1 ; i++) {
            if(abort)
                break;
            nextGen(i);

            try
            {
                int l = barrier.await();
                for (int j = 0; j < states_number; j++) {
                    population_counter.getAndAdd(j,this.local_population_counter[j]);
                }

                if(barrier.getParties() == 0)
                    barrier.reset();

                l = barrier.await();


                if(this.task_number==1) {
                    canvasTemplateRef.revalidate();
                    canvasTemplateRef.repaint();
                    Thread.sleep(0,10);

                    int[] spatial_entropy_counter = new int [states_number];

                    for (int j = 0; j < states_number; j++) {
                        spatial_entropy_counter[j] = population_counter.get(j);
                        population[j].add((double)population_counter.get(j));
                    }
                    population_counter = new AtomicIntegerArray(states_number);

                    if(CellularAutomata2D.population_chart_ref != null)
                        CellularAutomata2D.population_chart_ref.plot();
                    changeRefs();
                }

                if(barrier.getParties() == 0)
                    barrier.reset();

                l = barrier.await();


                if(barrier.getParties() == 0)
                    barrier.reset();
            }catch(Exception e){}
        }

    }

    public CellularAutomata2D() {}

    public CellularAutomata2D(int i) {
        task_number = i;

        int paso = cells_number /total_tasks;


        fn = paso * task_number;
        in = fn - paso;

        if( total_tasks == task_number)
            fn =cells_number;
    }

    public static void next_gen_concurrent(int nt,int g) {
        gens =g;

        size_pool =nt;

        barrier = new CyclicBarrier (size_pool);
        total_tasks = size_pool;

        myPool = new ThreadPoolExecutor(
                size_pool, size_pool, 60000L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        CellularAutomata2D[] tareas = new CellularAutomata2D[nt];

        for(int t = 0; t < nt; t++)
        {
            tareas[t] = new CellularAutomata2D(t+1);
            myPool.execute(tareas[t]);

        }

        myPool.shutdown();
        try{
            myPool.awaitTermination(10, TimeUnit.HOURS);
        } catch(Exception e){
            System.out.println(e.toString());
        }

    }

    public LinkedList<Double>[] getPopulation(){
        return population;
    }

    private static void randomInitializer() {
        int nCells = height/2;
        for(int i=0; i < nCells; i++) {
            matrix[randomGenerator.nextInt(height)][randomGenerator.nextInt(height)]=1;
        }

    }

    //TODO: Implement random Island initializer
    private static void randomIslandInitializer() {

    }

    //TODO: Implement glider gun initializer
    private static void gliderGunInitializer() {

    }

    private static void initializeState(String initializerMode) {
        switch (initializerMode){
            case "Random":
                randomInitializer();
                break;
            case "Random Island":
                randomIslandInitializer();
                break;
            case "Gliders gun":
                gliderGunInitializer();
                break;
            default:

        }
    }

    public void initializer (int cells_number, int generations, int cfrontier, String initializerMode ) {
        randomGenerator = new Random();

        width = cells_number;
        height = cells_number;

        actualGen = new int[width][width]; nextGen = new int[width][width];
        matrix = new int[height][width];

        population_counter = new AtomicIntegerArray(states_number);

        CellularAutomata2D.cells_number = cells_number;
        CellularAutomata2D.generations = generations;
        CellularAutomata2D.cfrontier = cfrontier;
        CellularAutomata2D.initializerMode = initializerMode;

        population = new LinkedList[states_number];
        CellularAutomata2D.initializeState(initializerMode);


        for (int i = 0; i < states_number; i++) {
            population[i] = new LinkedList<Double>();
        }


    }

    public static void changeRefs() {
        int[][] aux = actualGen;
        actualGen = nextGen;
        nextGen = aux;
    }

    public static void stop() {
        abort = true;
    }

    public static LinkedList<Double>[]caComputation(int nGen) {
        abort = false;
        generations = nGen;
        next_gen_concurrent(4,nGen);

        return population;
    }

    private int computeVonNeumannNeighborhood(int i, int j) {
        int cellsAlive = 0 ;

        if(cfrontier==0) {
            for(int x = i-1; x<=i+1; x++) {
                for(int y = j-1; y<=j+1; y++) {
                    if((x >= 0 && x < width) && (y >= 0 && y < width) && (( x != i) || (y != j)) && (actualGen[x][y] == 1))
                        cellsAlive ++;
                }
            }
        }
        else{
            //TODO: implement cilindrical frontier
        }

        return cellsAlive;
    }

    private int transitionFunction(int cellsAlive, int i, int j) {
        int transitionFunctionValue;

        if(cellsAlive <2 || cellsAlive >3)
            transitionFunctionValue = 0;
        else if( cellsAlive == 2)
            transitionFunctionValue = actualGen[i][j];
        else
            transitionFunctionValue = 1;

        return transitionFunctionValue;
    }

    public int getCellValue(int i, int j){
        int cellsAlive = computeVonNeumannNeighborhood(i,j);
        return transitionFunction(cellsAlive, i, j);
    }

    public  LinkedList<Double>[] nextGen(int actual_gen) {

        local_population_counter = new int[states_number];

        for (int i = 0; i < states_number; i++) {
            this.local_population_counter[i]=0;
        }

        for (int i = in; i < fn; i++) {
            if(abort)
                break;
            int j = 0;

//
            local_population_counter[nextGen[i][j]]++;
        }

        return population;
    }

}