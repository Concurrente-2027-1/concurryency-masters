import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Tarea implements Runnable {
    int tiempoTarea;
    int task;
    Semaphore semaphore; // Semáforo para limitar accesos simultáneos (en este caso tener máximo 3 hilos)
    Lock lock; // Candado para evitar la ejecución simultánea de hilos (en este caso el 0 y 2)

    public Tarea(int i, Semaphore semaphore, Lock lock) {
        this.task = i;
        this.semaphore = semaphore;
        this.lock = lock;
    }
    
    @Override
    public void run() {
        Thread currentThread = Thread.currentThread(); 
        long id = currentThread.getId(); 
        
        int value = (int) (id % 6); // ID virtual del hilo del 0 al 5
        
        // Definimos los tiempos para cada hilo
        switch(value) { 
          case 0, 2: 
              this.tiempoTarea = 500;
            break; 
          case 1: 
              this.tiempoTarea = 2000;
            break;
          default:
              this.tiempoTarea = 3000;
        }
        
        try {
            // Restringimos a 3 hilos máximo en el scheduler. Si ya hay, el hilo va a esperar aquí.
            semaphore.acquire();
            
            // Revisamos si es uno de los hilos que no queremos trabajando juntos (0 ó 2)
            if (value == 0 || value == 2) { 
                lock.lock(); // El hilo toma exclusividad para no toparse con el otro.
            }
            
            try {
                System.out.println("Running Thread " + value + " task: " + this.task);
                Thread.sleep(0, this.tiempoTarea); // Simulamos el trabajo esperando el tiempo especificado del hilo en ejecución
                System.out.println("Finished Thread " + value + " time: " + this.tiempoTarea);
            } finally { 
                // Revisamos nuevamente si es uno de los hilos que no queremos trabajando juntos (0 ó 2)
                if (value == 0 || value == 2) {
                    lock.unlock(); // Liberamos el candado para que el otro hilo pueda trabajar.
                }
            }
        } catch(InterruptedException e) { 
            System.out.println(e); 
        } finally {
            semaphore.release(); // Tras terminar la tarea liberamos un lugar en el servidor para que pueda entrar la siguiente
        }
    }
}