public class TimeCount {
	private double time0;
	
	public TimeCount() {
		time0=System.currentTimeMillis();
	}
	
	public double askTime() {
		return (System.currentTimeMillis()- time0)/1000;
	}

}
