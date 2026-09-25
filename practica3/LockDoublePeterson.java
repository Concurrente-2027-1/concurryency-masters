import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LockDoublePeterson{
    static int[] victorias = new int[4];
    private static void task(DoublePeterson lock, CounterNaive counter, int id) {
        try {
            lock.lock(id);
            counter.increment();
            victorias[id]++;
        }finally {
            lock.unlock(id);		
        }
    }
    
    
    public static void main(String[] args) {
        DoublePeterson lock = new DoublePeterson();
        CounterNaive counter = new CounterNaive();
        ExecutorService executor = Executors.newFixedThreadPool(4);//El candado solo funciona para cuatro hilos
        
        for(int i = 0; i < 400; i++) {
            int id = i % 4;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    task(lock, counter, id);
                    //System.out.println("Hilo " + id + " ha ganado " + victorias[id] + " veces.");
                }
            });
        }
        executor.shutdown();
        
        
        try{
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
            System.out.println(counter.getValue());
            for(int i = 0; i < 4; i++) {
                System.out.println("Hilo " + i + " ha ganado " + victorias[i] + " veces.");
            }
        }catch(InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            System.out.println(e);
        }
    
    }

}