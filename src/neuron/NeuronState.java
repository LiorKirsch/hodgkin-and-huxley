package neuron;

public class NeuronState {
	
	public double n; // activation of K
	public double m; // activation of Na
	public double h; // inactivation of Na
	public double V; // the neuron voltage
	
	/**
	 * 
	 * @param n activation of K
	 * @param m activation of Na
	 * @param h inactivation of Na
	 * @param V membrane voltage
	 */
	public NeuronState()
	{
		this(0,0,0,0);
	}
	
	public NeuronState(double n, double m, double h,double V)
	{
		this.n = n;
		this.m = m;
		this.h = h;
		this.V = V;
	}
	/**
	 * adds to this neuron an other state defined by otherNeuronState values
	 * @param otherNeuronState another neuron
	 */
	public void add(NeuronState otherNeuronState)
	{
		this.n +=  otherNeuronState.n;
		this.m +=  otherNeuronState.m;
		this.h +=  otherNeuronState.h;
		this.V +=  otherNeuronState.V;
	}
	
}
