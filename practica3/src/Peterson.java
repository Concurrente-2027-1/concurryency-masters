public class Peterson {
	private volatile boolean[] flag = new boolean[2];
	private volatile int victim;
	
	public Peterson() { 
		flag[0] = false; 
		flag[1] = false;
		victim = 1; // Inicializamos con 0 o 1
	}
	
	public void lock(int id) {
		int i = id; // Como ya le pasamos 0 o 1
		int j = 1 - i;
		flag[i] = true;
		victim = i;
		while (flag[j] && victim == i) {
		}
	}
	
	public void unlock(int id) {
		int i = id;
		flag[i] = false;
	}
}