import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/*
Programa 2: Programa para medir el tiempo de CounterAtomic (sin lock).
*/

public class RunSpinCounterAtomic {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length > 2) {
			throw new IllegalArgumentException("Se permiten hasta dos argumentos.");
		}
		List<Future<Integer>> futures = new ArrayList<Future<Integer>>();
		int numberTasks = 400;
		int numberThreads = 4;
		if (args.length > 0) {
			numberTasks = Integer.parseInt(args[0]);
		}
		if (args.length > 1) {
			numberThreads = parseThreadCount(args[1]);
		}
		if (numberTasks < 1) {
			throw new IllegalArgumentException("El numero de tareas debe ser positivo.");
		}
		ExecutorService executor = Executors.newFixedThreadPool(numberThreads);
		CounterAtomic counter = new CounterAtomic();
		
		long startTime = System.nanoTime();//Start time
		for(int i = 0; i < numberTasks; i++) {
			futures.add(executor.submit(() -> counter.increment()));
		}
		executor.shutdown();
		
		for (int i = 0; i < futures.size(); i++) {
            while(!futures.get(i).isDone()){}; // Comprobar que todas las tareas terminen
		}
		long endTime = System.nanoTime();//Finish time
		
        System.out.println("Program took " +
                (endTime - startTime)*0.000001 + "ms, Count result: " + counter.getValue()) ; //En milisegundos
	}

	private static int parseThreadCount(String argument) {
		int threads;
		if (argument.equalsIgnoreCase("max")) {
			threads = Runtime.getRuntime().availableProcessors() - 1;
		} else {
			threads = Integer.parseInt(argument);
		}
		if (threads < 1) {
			throw new IllegalArgumentException("El numero de hilos debe ser positivo.");
		}
		return threads;
	}
}
