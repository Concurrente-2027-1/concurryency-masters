import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Implementación del algoritmo Bakery para 4 hilos con conteo de operaciones efectuadas
 * por cada hilo.
 */
public class Bakery4 {

    // Inicializamos los arreglos para 4 hilos.
    // Usamos volatile para asegurar el orden de actualizaciones de ambas variables en compilación.
    static volatile boolean[] flag = new boolean[4];
    static volatile int[] label = new int[4];

    static int contador = 0; // Contador global de la tarea
    static int[] aumentosPorHilo = new int[4]; // Contador de aumentos realizados por el hilo

    // Asignamos IDs locales a cada hilo
    static int siguienteId = 0;

    /**
     * Contador de IDs para asignarse a hilos
     * @return Siguiente ID
     */
    static synchronized int obtenerId() {
        return siguienteId++;
    }

    // Usamos ThreadLocal para poder asignar nuestros IDs propios a los hilos
    static ThreadLocal<Integer> threadIdLocal = ThreadLocal.withInitial(Bakery4::obtenerId);


    /**
     * Toma el candado y bloquea la CS
     */
    public static void lock() {
        int i = threadIdLocal.get(); // Obtenemos el ID 
        flag[i] = true; // Subimos bandera

        // Tomamos el siguiente ticket
        int maxTicket = 0;
        for (int j = 0; j < label.length; j++) {
            if (label[j] > maxTicket) {
                maxTicket = label[j];
            }
        }
        label[i] = maxTicket + 1;

        // Esperamos los turnos menores
        for (int k = 0; k < label.length; k++) {
            // Comparamos lexicográficamente las duplas (turno, id)
            while(k != i && flag[k] && label[k] < label[i] && compararDuplas(label[k], k, label[i], i)) {
                Thread.yield(); // Esperamos
            }
        }
    }

    /**
     * Deja el candado que bloquea la CS
     */
    public static void unlock() {
        int i = threadIdLocal.get(); // Obtenemos el ID 
        flag[i] = false; // Bajamos bandera
    }


    /**
     * Método para comparar de forma lexicográfica duplas de números.
     * @param a Primer número de la primera dupla
     * @param b Segundo número de la primera dupla
     * @param c Primer número de la segunda dupla
     * @param d Segundo número de la segunda dupla
     * @return
     */
    private static boolean compararDuplas(int a, int b, int c, int d) {
        if (a<c) return true;
        else if (a == c) {
            if (b < d) {
                return true;
            }
        }
        return false;
    }


    public static void main(String[] args){
        // Creamos un pool con 4 hilos
        ExecutorService executor = Executors.newFixedThreadPool(4);

        // Definimos la tarea del contador con Runnable
        Runnable tarea = new Runnable() {
            @Override
            public void run() {
                lock(); // Bloqueamos la CS con el candado
                try {
                    // SECCIÓN CRÍTICA: Aumentamos el contador
                    contador++;

                    // Tomamos el ID del hilo actual y luego sumamos la operación a su registro
                    int miId = threadIdLocal.get(); 
                    aumentosPorHilo[miId]++;
                } finally {
                    unlock(); // Salimos de la CS quitando el candado 
                }
            }
        };

        // Ejecutamos 400 tareas con el ExecutorService
        for (int i = 0; i < 400; i++) {
            executor.execute(tarea);
        }

        // Apagamos el executor y esperamos a que terminen todos los hilos
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Imprimimos los resultados y el número de veces que cada hilo trabajó aumentando el contador
        System.out.println("Resultado final del contador: " + contador);
        for (int i = 0; i < 4; i++) {
            System.out.println("El hilo " + i + " incrementó el contador " + aumentosPorHilo[i] + " veces.");
        }
    }

}

