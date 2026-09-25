public class DoublePeterson{
    private Peterson lockAB;
    private Peterson lockCD;
    private Peterson lockFinal;

    public DoublePeterson(){
        lockAB = new Peterson();
        lockCD = new Peterson();
        lockFinal = new Peterson();
        
    }

    public void lock(int id) {
		int group = id / 2;
		int position = id % 2;
		if (group == 0) {
			lockAB.lock(position);
		} else {
			lockCD.lock(position);
		}
		lockFinal.lock(group);
	}
	public void unlock(int id) {
		int i = id;
        lockFinal.unlock(i / 2);
		if (i / 2 == 0) {
			lockAB.unlock(i % 2);
		} else {
			lockCD.unlock(i % 2);
		}
	}


}