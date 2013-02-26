package neuron;
import static java.lang.Math.*;

import java.util.ArrayList;
import java.util.ListIterator;

public class TempNeuron {
	public int debugIndex;
	private double n; // activation of K
	private double m; // activation of Na
	private double h; // inactivation of Na
	private double V; // the neuron voltage
	private ArrayList<Double> spikeTimes;
	private ArrayList<NeuronalConnection> inputSynapses;

	//private double Iext; // external current
	
	public double nDot; // activation of K
	public double mDot; // activation of Na
	public double hDot; // inactivation of Na
	public double VDot; // the neuron voltage
	
	static final double E_Na = 50; // resting potential of Na
	static final double E_K = -77; // resting potential of K
	static final double E_L = -54.5; // resting potential of the leak
	
	static final double gNa  = 120; // maximum conductance for Na
	static final double gK = 36; // maximum conductance for Na
	static final double gL = 0.3; // maximum conductance for Na
	static final double C = 1; // maximum conductance for Na
	static final double threshold = 23 ;// threshold to be consider a spike, used for evaluating spike time vector

	public TempNeuron ()
	{
		this (0,0,0,0);
	}
		
	/**
	 * 
	 * @param n activation of K
	 * @param m activation of Na
	 * @param h inactivation of Na
	 * @param V membrane voltage
	 * @param Iext the external current applied
	 */
	public TempNeuron (double n, double m, double h,double V)
	{
		this.n = n;
		this.m = m;
		this.h = h;
		this.V = V;
		this.spikeTimes = new ArrayList<Double>();
		this.inputSynapses = new ArrayList<NeuronalConnection>();
	}
	// for debug
	public TempNeuron (double n, double m, double h,double V, int indexNumberDebug)
	{
		this.debugIndex = indexNumberDebug;
		this.n = n;
		this.m = m;
		this.h = h;
		this.V = V;
		this.spikeTimes = new ArrayList<Double>();
		this.inputSynapses = new ArrayList<NeuronalConnection>();
	}
	public String toString()
	{
		String output = "neuron " + this.debugIndex;
		return output;
	}
	public void addInputNeuron (Neuron neuron)
	{
		NeuronalConnection newConnection = new NeuronalConnection(neuron);
		inputSynapses.add(newConnection);
	}
	public void addInputNeuron (Neuron neuron,double synapseStrength,double synapseLatency, double vSyn)
	{
		NeuronalConnection newConnection = new NeuronalConnection(neuron,synapseStrength,synapseLatency,vSyn);
		inputSynapses.add(newConnection);
	}
	/**
	 * 
	 * @param neuron
	 * @param synapseStrength
	 * @param synapseLatency
	 * @param excitatory true means the synapse is excitatory and vSyn is set to 0 mV
	 * false means the synapse is inhibitory and vSyn is set to -80 mV
	 */
	public void addInputNeuron (Neuron neuron,double synapseStrength,double synapseLatency, boolean excitatory ,double dt)
	{
		synapseLatency = synapseLatency /dt;
		NeuronalConnection newConnection;
		if (excitatory)
		{
			newConnection = new NeuronalConnection(neuron,synapseStrength,synapseLatency,0);
		}
		else
		{
			newConnection = new NeuronalConnection(neuron,synapseStrength,synapseLatency,-80);	
		}
			
		inputSynapses.add(newConnection);
	}

	
	public ArrayList<Double> getSpikeTimes()
	{
		return this.spikeTimes;
	}
	/**
	 * get the spike time only within the time window defined by latency
	 * @param windowSize the window size 
	 * @param currentTime time to look (prior to it) 
	 * @return neuron spike times
	 */
	public ArrayList<Double> getSpikeTimes(double currentTime , double windowSize)
	{
		double timeOfSpike;
		ArrayList<Double> spikesInWindow  = new ArrayList<Double>();
		if (spikeTimes.size() > 0)
		{
			System.out.println(spikeTimes.size() - 1);

			ListIterator<Double> listIterator = this.spikeTimes.listIterator(spikeTimes.size() - 1);
			
			while (listIterator.hasNext())
			{
				timeOfSpike = listIterator.previous();
				if (timeOfSpike <currentTime 	& 	timeOfSpike > currentTime - windowSize)
				{
					spikesInWindow.add(timeOfSpike);
				}
			}
			
			
		}
		
		return spikesInWindow;
	}
	
	private double calcIsynaptic(double currentTime)
	{
		double Isyn = 0;
		double gSyn = 3; // not sure about this value
		// instead of gSyn I use the currentSynapse.synapseStrength parameter
		if (this.inputSynapses != null)
		{
			for (NeuronalConnection currentSynapse : inputSynapses) {
				Neuron currentNeuron = currentSynapse.neuron ;
				ArrayList<Double> currentNeuronSpikeTimes = currentNeuron.getSpikeTimes();
				for (int j = 0; j < currentNeuronSpikeTimes.size(); j++) {
					Isyn += -gSyn  * currentSynapse.synapseStrength * alphaFunction(currentTime -currentNeuronSpikeTimes.get(j) - currentSynapse.synapseLatency ) * (this.V - currentSynapse.vSyn );
				}
			}
		}
		return Isyn;
	}
	/**
	 * 
	 * @param t the time
	 * @param tau characteristic time for the synaptic current
	 */
	private double alphaFunction (double t)
	{
		double tauD = 10;
		double tauR = 1;
		double result = 0;
		if (t>0)
			result = 1 /(tauD - tauR) * ( exp(-t / tauD) - exp(-t/tauR));

		return result;
	}
	/**
	 * 
	 * @param n activation of K
	 * @param m activation of Na
	 * @param h inactivation of Na
	 * @param V membrane voltage
	 * @param Iext the external current applied
	 * @return an  of { nDot,mDot,hDot,VDot}
	 */
	private NeuronState calcDotProduct (double n, double m, double h,double V, double Iext,double t)
	{
		double alpha_m,beta_m,minf,tau_m,alpha_n,beta_n ,ninf,tau_n,alpha_h,beta_h,hinf,tau_h,I_L,I_Na,I_K,Isyn;
		
 		alpha_m = 0.1*(V+40) / (1 - exp(-0.1*(V+40)));
		beta_m = 4*exp(-(V+65)/18);
		minf = alpha_m / (alpha_m + beta_m);
		tau_m = 1 / (alpha_m + beta_m);
		mDot = (minf - m) / tau_m;

		alpha_n = 0.01*(V+55)/(1 - exp(-0.1*(V+55)));
		beta_n = 0.125*exp(-(V+65)/80);
		ninf = alpha_n / (alpha_n + beta_n);
		tau_n = 1 / (alpha_n + beta_n);
		nDot = (ninf - n) / tau_n;

		alpha_h = 0.07*exp(-(V+65)/20);
		beta_h = 1/(1+exp(-0.1*(V+35)));
		hinf = alpha_h / (alpha_h + beta_h);
		tau_h = 1 / (alpha_h + beta_h);
		hDot = (hinf - h) / tau_h;

		I_L = gL * (V - E_L);
		I_Na = gNa * pow(m,3) * h * (V - E_Na);
		I_K = gK * pow(n,4) * (V - E_K);
		
		Isyn = calcIsynaptic(t);
		VDot = (Iext-I_Na-I_K-I_L + Isyn) / C;
		NeuronState neuronState  = new NeuronState( nDot,mDot,hDot,VDot);
		 return neuronState;
	}
	/**
	 *  
	    4'th order Runge-Kutta algorithm with a constant step size.
	 * @param t the time
	 * @param h the step size
	 * @param Iext the external current
	 */
	private void rk4(double t,double h,double Iext)
	{
		   double hh = h*0.5;
		   //double th = t + hh;
		    /**
		      y' = f
		
			    K1 = f(y_n);
			    K2 = f(y_n + 0.5*h*K1)
			    K3 = f(y_n + 0.5*h*K2)
			    yTemp = y_n + 0.5*K3
			    K3 = K3 + K2
			    K2 = f(yTemp)
				y_(n+1) = y_n + (h/6) * (K1 + K2 + 2*K3);
  
		     */
		    // First step
		   double tempN,tempM,tempH,tempV;
		   NeuronState dydt,dyt,dym;
		   
		   dydt =  this.calcDotProduct(this.n,this.m,this.h,this.V,Iext, t);
		    tempN = this.n + hh*dydt.n;
		    tempM = this.m + hh*dydt.m;
		    tempH = this.h + hh*dydt.h;
		    tempV = this.V + hh*dydt.V;
		    
		    //yt = y + hh * dydt;
	
		    // Second step
		    //dyt = feval(derivs,th,yt,Iext);
		    dyt = this.calcDotProduct(tempN,tempM,tempH,tempV,Iext, t);
		    //yt = y + hh * dyt;
		    tempN = this.n + hh*dyt.n;
		    tempM = this.m + hh*dyt.m;
		    tempH = this.h + hh*dyt.h;
		    tempV = this.V + hh*dyt.V;
	
		    // Third step
		    //dym = feval(derivs,th,yt,Iext);
		    dym = this.calcDotProduct(tempN,tempM,tempH,tempV,Iext, t);
		    //yt = y + hh * dym;
		    tempN = this.n + hh*dym.n;
		    tempM = this.m + hh*dym.m;
		    tempH = this.h + hh*dym.h;
		    tempV = this.V + hh*dym.V;
		    dym.add(dyt);
		    
		    //		dym = dym + dyt;
	
		    // Fourth step
		    //dyt = feval(derivs,t+h,yt,Iext);
		    dyt = this.calcDotProduct(tempN,tempM,tempH,tempV,Iext, t);

		    // Accumulate increments with proper weights
		   // y_out = y + (h/6) * (dydt + dyt + 2*dym);
		    this.n = this.n + (h/6) *(dydt.n + dyt.n + 2*dym.n);
		    this.m = this.m + (h/6) *(dydt.m + dyt.m + 2*dym.m);
		    this.h = this.h + (h/6) *(dydt.h + dyt.h + 2*dym.h);
		    this.V = this.V + (h/6) *(dydt.V + dyt.V + 2*dym.V);		
	}
	
	/**
	 * update all the internal parameters of the neuron according to the inputs recieved
	 * @param inputsToNeuron  of all inputs to this neuron
	 * @return V the voltage of the neuron
	 
	public double calcStates (double[] inputsToNeuron, double t,double dt)
	{
		
		//this.rk4mat(t, dt,extI);
		return this.V;
	}
	*/
	/**
	 * update all the internal parameters of the neuron when it receives only external current
	 * @param extI the neuron 
	 * @return V the voltage of the neuron
	 */
	public double calcStates (double Iext, double t,double dt)
	{
		double oldVdot = this.VDot;
		this.rk4(t, dt,Iext);
		checkForSpike(oldVdot,t);
		return this.V;
	}
	/**
	 *  check if a spike accord in time t , using the values of the current neuron
	 *  Vdot and the oldVdot parameter supplied
	 * @param oldVdot the value of Vdot before it was changed to current value
	 * @param t the time
	 */
	private void checkForSpike(double oldVdot, double t)
	{
	    if (  (oldVdot > 0)  && (this.VDot < 0) && ( this.V >threshold) )
	    	spikeTimes.add(t);
	}
}
