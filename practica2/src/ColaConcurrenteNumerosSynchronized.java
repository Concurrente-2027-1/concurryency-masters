import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ColaConcurrenteNumerosSynchronized {

    public class Nodo {
	    public String item;
	    public Nodo next;
	    public Nodo(String item) {
	    	this.item = item;
	    }
    }
    
    private Nodo head;
	private Nodo tail;

	public ColaConcurrenteNumerosSynchronized() {
		this.head  = new Nodo("hnull");
	    this.tail  = new Nodo("tnull");
	    this.head.next = this.tail;
	}

    public synchronized String enq(String x) {
		Nodo newnode = new Nodo(x);
		if(this.head.next == this.tail) {
			newnode.next = this.tail;
			this.head.next = newnode;	
		}else {
			Nodo last =  this.tail.next;
			newnode.next = tail;
			last.next = newnode;
		}
		tail.next = newnode;
		return "Encolamos: " + x;
	}

    public synchronized String deq() {
		if(this.head.next == this.tail) {
			return "empty";
		}
		Nodo first = this.head.next;
		this.head.next = first.next;		
		return "Desencolamos: " + first.item;
	}

	public void print() {
		System.out.println("Print ");
        Nodo pred = this.head;
        Nodo curr = pred.next;
        System.out.println(pred.item);
        while (curr.item != "tnull") {
          pred = curr;
          curr = curr.next;
          System.out.println(pred.item);
        }
	}
    
    public static void main(String[] args)  throws InterruptedException, ExecutionException{

        //// Lista de Futures de Encolar
        //List<Future<Boolean>> futuresEnq = new ArrayList<Future<Boolean>>();
        //
        //// Lista de Futures de Desencolar
        //List<Future<String>> futuresDeq = new ArrayList<Future<String>>();
        
        // Lista de Futures 
        List<Future<String>> futures = new ArrayList<Future<String>>();

        // Creamos una pool de 4 hilos
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // La cola para hacer pruebas 
        ColaConcurrenteNumeros cola = new ColaConcurrenteNumeros();

                
        for(int i=0; i<100; i++){
            final int valorActual = i;
            futures.add(executor.submit(()->cola.enq(valorActual+"")));
        }
        
        // Asignamos un Future a la tarea del hilo que desencolará de la cola
        //Future<String> fut2 = executor.submit(()->cola.deq());  
        // Guardamos el Future en la lista de Futures de Encolar
        //futures.add(fut7);


        // Hacemos que los hilos ya no se apunten a nuevas tareas, por si las moscas
        executor.shutdown();


        /** 
            Impresión de los resultados de los Future de la lista de Future en el 
            orden en el que se asignaron las tareas
        **/ 
        System.out.println("Imprimiremos a los resultados de los hilos según el " + 
                            "orden en el que se asignaron a sus tareas, pero el " +
                            "contenido de la impresión de la cola mostrará el verdadero orden " + 
                            "en el que se ejecutaron los hilos."
        );

        for(int i=0; i<futures.size(); i++){
            while(!futures.get(i).isDone()); //Consime CPU, creo que sería equivalente a sólo hacer futures.get(i).get(), porque eso sólo detiene al hilo main y no a los otros hilos, lo cuál es equivalente a detener el hilo main con un while(true)
            
            String resultado = futures.get(i).get();
            System.out.println(resultado);            
        }

        System.out.println();
        System.out.println("La cola final se ve así:");
        cola.print();
    }
}
