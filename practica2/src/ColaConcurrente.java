package unam.fc.concurrent.practica2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ColaConcurrente {
    
    // Nodos para la cabeza y la cola de nuestra estructura
    private Nodo head;
    private Nodo tail;

    public ColaConcurrente() {
        // Inicializamos con un nodo centinela (dummy)
        Nodo centinela = new Nodo("centinela");
        head = centinela;
        tail = centinela;
    }

    // Método para encolar secuencial básico (sin sincronización)
    public void enq(String item) {
        Nodo nuevoNodo = new Nodo(item);
        tail.next = nuevoNodo;
        tail = nuevoNodo;
    }

    // Método para desencolar secuencial  (sin sincronización)
    public String deq() {
        if (head.next == null) {
            return null; // La cola está vacía
        }
        String item = head.next.item;
        head = head.next; // Movemos la cabeza al siguiente nodo
        return item;
    }

    public static void main(String[] args) {
        ColaConcurrente cola = new ColaConcurrente();
        
        // Creamos una pool de hilos 
        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<String>> futures = new ArrayList<>();

        // Mandamos varias tareas de encolar y desencolar a la pool
        for (int i = 0; i < 100; i++) {
            final int index = i;
            
            // Tarea de encolar
            executor.submit(() -> {
                cola.enq("Elemento " + index);
            });

            // Tarea de desencolar (usamos Callable para poder regresar el valor)
            futures.add(executor.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return cola.deq();
                }
            }));
        }

        // Apagamos el executor para que no reciba más tareas
        executor.shutdown();
    }
}
