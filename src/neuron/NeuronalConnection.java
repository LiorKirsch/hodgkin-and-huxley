package neuron;

import java.util.concurrent.ConcurrentLinkedQueue;

public class NeuronalConnection {
	public Neuron neuron;
	public double synapseStrength;
	public double synapseLatency;
	public double vSyn;
	public double neuronYrecursive;
	public double neuronZrecursive;
	public ConcurrentLinkedQueue<Double> lastSpikeTime ;

	/**
	 * creating the synapse using default values of strength = 1, latency = 500, vSyn = -65
	 * @param neuron the input neuron of the synapse
	 */
	public NeuronalConnection(Neuron neuron)
	{
		this(neuron,1,15 , 0);
		// the default values are : strength=1 , delay = 15 , E_syn = 0 , so this is exictatory 
	}
	
	public NeuronalConnection(Neuron neuron,double synapseStrength,double synapseLatency, double vSyn)
	{
		this.neuron = neuron;
		this.synapseStrength = synapseStrength;
		this.synapseLatency = synapseLatency;
		this.vSyn = vSyn;
		this.neuronYrecursive = 0;
		this.neuronZrecursive = 0;
		this.lastSpikeTime = new ConcurrentLinkedQueue<Double>();

	}
	public String toString()
	{
		return "connection with neuron " + this.neuron.toString();
	}
}
