package neuron;

import java.util.concurrent.ConcurrentLinkedQueue;

public class NoiseSynapse extends NeuronalConnection {
	
	public NoiseSynapse(Neuron neuron, ConcurrentLinkedQueue<Double> lastSpikes)
	{
		super(neuron,3,15 , -65);
		this.lastSpikeTime = lastSpikes;
	}
	
	public NoiseSynapse(Neuron neuron,double synapseStrength,double synapseLatency, double vSyn, ConcurrentLinkedQueue<Double> lastSpikes)
	{
		super(neuron, synapseStrength, synapseLatency, vSyn);
		this.lastSpikeTime = lastSpikes;
	}
	

}
