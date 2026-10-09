import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;

/*
Programa 2: Programa para medir el tiempo de TASLock. Usa matriz compartida de 10 x 10.
*/

public class RunSpinMatrixTASLock {
	// En vez de tener un contador compartido, todas las tareas escriben en una matriz N x N.
	private static final int N = 10;
	private static final int[][] matrix = new int[N][N];
	
	private static void assignMatrix(int[][] target, int taskNumber) {
		// Cada celda recibe su valor calculado a partir de la tarea y su posición.
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				target[i][j] = taskNumber * N * N + i * N + j;
			}
		}
	}
	
	private static void printMatrix(int[][] datos, int taskNumber) {
		// Imprimimos la matriz de cada tarea.
		synchronized (System.out) {
			System.out.println("Task " + taskNumber + " matrix:");
			for (int[] fila : datos) {
				for (int valor : fila) {
					System.out.print(valor + "\t");
				}
				System.out.println();
			}
		}
	}
	
	private static void task(Lock lock, int taskNumber) {
		// Imprimimos el resultado después de liberar el lock.
		int[][] snapshot = new int[N][N];
		lock.lock();
		try {
			assignMatrix(matrix, taskNumber);
			for (int i = 0; i < N; i++) {
				System.arraycopy(matrix[i], 0, snapshot[i], 0, N);
			}
		} finally {
			lock.unlock();
		}
		printMatrix(snapshot, taskNumber);
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		if (args.length > 2) {
			throw new IllegalArgumentException("Se permiten hasta dos argumentos.");
		}
		List<Future<?>> futures = new ArrayList<Future<?>>();
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
		Lock lock = new TASLock();
		
		long startTime = System.nanoTime();//Start time
		for(int i = 0; i < numberTasks; i++) {
			final int taskNumber = i;
			// Pasamos el número de tarea para generar valores distintos en la matriz.
			futures.add(executor.submit(() -> task(lock, taskNumber))); 
		}
		executor.shutdown();
		
		// Esperamos a que terminen las tareas.
		for (Future<?> future : futures) {
			// Esperamos la tarea
			future.get();
		}
		long endTime = System.nanoTime();//Finish time
		
		// Imprimimos el tiempo de ejecución
        System.out.println("Program took " +
                (endTime - startTime)*0.000001 + "ms"); //En milisegundos
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
