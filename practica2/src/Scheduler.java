import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore; 
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Scheduler { 
    
    // Inicializamos el semáforo en 3 para que máximo entren tres hilos al mismo tiempo.
    // Usamos el parámetro 'true' para asegurar justicia (FIFO).
    static Semaphore smphre = new Semaphore(3, true); 
    
    // Candado para evitar la ejecución simultánea de hilos (en este caso el 0 y 2)
    // Usamos el parámetro 'true' para asegurar justicia (FIFO).
    static Lock lock = new ReentrantLock(true);

    public static void main(String[] args){
        // Creamos un pool con 6 hilos (serán las personas que envían las tareas).
        ExecutorService executorTarea = Executors.newFixedThreadPool(6);
        
        for(int i = 0; i < 26; i++) { // Creamos y encolamos 26 tareas
            executorTarea.execute(new Tarea(i, smphre, lock)); // Enviamos la tarea, con el semáforo y candado, al ejecutor
        }
        
        // Bloqueamos la recepción de nuevas tareas y detenemos todo tras completar la cola.
        executorTarea.shutdown();
    }
}